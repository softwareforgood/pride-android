package com.softwareforgood.pridefestival.ui.events

import android.content.Context
import androidx.constraintlayout.widget.ConstraintLayout
import android.util.AttributeSet
import android.view.View
import androidx.core.net.toUri
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.softwareforgood.pridefestival.R
import com.softwareforgood.pridefestival.data.FavoritesStorage
import com.softwareforgood.pridefestival.data.model.Event
import com.softwareforgood.pridefestival.data.model.EventType.SPORTS
import com.softwareforgood.pridefestival.data.model.EventType.PERFORMANCE
import com.softwareforgood.pridefestival.data.model.EventType.MUSIC
import com.softwareforgood.pridefestival.data.model.EventType.FOOD
import com.softwareforgood.pridefestival.data.model.EventType.MISCELLANEOUS
import com.softwareforgood.pridefestival.util.getColor
import com.softwareforgood.pridefestival.util.getDrawable
import com.softwareforgood.pridefestival.util.observeOnAndroidScheduler
import com.softwareforgood.pridefestival.util.plusAssign
import com.softwareforgood.pridefestival.util.toTimeString
import io.reactivex.disposables.CompositeDisposable
import kotlinx.android.synthetic.main.view_event_list_item.view.*
import timber.log.Timber
import com.softwareforgood.pridefestival.util.launchCustomTab
import com.softwareforgood.pridefestival.util.setAsFavorited
import com.softwareforgood.pridefestival.util.setAsNotFavorited
import com.softwareforgood.pridefestival.util.subscribeOnIoScheduler
import com.softwareforgood.pridefestival.util.toSingle

class EventItemView(context: Context, attrs: AttributeSet) : ConstraintLayout(context, attrs) {

    private var disposables = CompositeDisposable()

    fun bind(event: Event, favoritesStorage: FavoritesStorage) {
        Timber.d("bind() called with event = [%s]", event)

        with(event) {
            loadImage(event)
            event_list_item_title.text = name
            event_list_item_start_time.text = startTime?.toTimeString() ?: ""
            event_list_item_location.text = locationName ?: ""
            event_list_website.visibility = if (website.isNullOrBlank()) View.GONE else View.VISIBLE
            event_list_website.setOnClickListener {
                !website.isNullOrBlank() || return@setOnClickListener
                website?.toUri()?.launchCustomTab(context)
            }
        }

        disposables += favoritesStorage.hasEvent(event)
                .observeOnAndroidScheduler()
                .subscribe(handleFavoriteEvent)

        event_list_item_favorite_button.setOnClickListener {
            disposables += favoritesStorage.hasEvent(event)
                    .flatMap { hasEvent: Boolean ->
                        if (hasEvent) favoritesStorage.deleteEvent(event).toSingleDefault(!hasEvent)
                        else favoritesStorage.saveEvent(event).toSingleDefault(!hasEvent)
                    }
                    .observeOnAndroidScheduler()
                    .subscribe(handleFavoriteEvent)
        }
    }

    override fun onDetachedFromWindow() {
        disposables.clear()
        super.onDetachedFromWindow()
    }

    private fun loadImage(event: Event) {
        val backgroundColor = when (event.type) {
            PERFORMANCE, MUSIC -> getColor(R.color.event_blue)
            SPORTS, FOOD -> getColor(R.color.event_green)
            MISCELLANEOUS -> getColor(R.color.event_yellow)
        }
        event_list_item_image_container.setBackgroundColor(backgroundColor)

        // image in case network image doesn't exist or does not load
        val defaultImage = when (event.type) {
            PERFORMANCE -> getDrawable(R.drawable.event_cell_type_performance_icon)
            MUSIC -> getDrawable(R.drawable.event_cell_type_music_icon)
            SPORTS -> getDrawable(R.drawable.cell_type_sport_icon)
            FOOD -> getDrawable(R.drawable.vendor_cell_type_food_icon)
            MISCELLANEOUS -> getDrawable(R.drawable.cell_type_miscellaneous_icon)
        }
        glide.load(defaultImage)
                .thumbnail(0.5f)
                .apply(RequestOptions().fitCenter())
                .into(event_list_item_image)

        event.image?.let {
            disposables += it.toSingle()
                    .subscribeOnIoScheduler()
                    .observeOnAndroidScheduler()
                    .subscribe(
                            { bytes -> glide.load(bytes)
                                    .apply(RequestOptions().circleCrop())
                                    .into(event_list_item_image) },
                            { error -> Timber.e(error, "Error loading image") }
                    )
        }
    }

    private val handleFavoriteEvent = { hasEvent: Boolean ->
        with(event_list_item_favorite_button) {
            if (hasEvent) setAsFavorited() else setAsNotFavorited()
        }
    }

    private val glide get() = Glide.with(this)
}
