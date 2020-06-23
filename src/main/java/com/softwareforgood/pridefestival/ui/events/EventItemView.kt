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
import com.softwareforgood.pridefestival.databinding.ViewEventListItemBinding
import com.softwareforgood.pridefestival.util.getColor
import com.softwareforgood.pridefestival.util.getDrawable
import com.softwareforgood.pridefestival.util.observeOnAndroidScheduler
import com.softwareforgood.pridefestival.util.plusAssign
import com.softwareforgood.pridefestival.util.toTimeString
import io.reactivex.disposables.CompositeDisposable
import timber.log.Timber
import com.softwareforgood.pridefestival.util.launchCustomTab
import com.softwareforgood.pridefestival.util.setAsFavorited
import com.softwareforgood.pridefestival.util.setAsNotFavorited
import com.softwareforgood.pridefestival.util.subscribeOnIoScheduler
import com.softwareforgood.pridefestival.util.toSingle

class EventItemView(context: Context, attrs: AttributeSet) : ConstraintLayout(context, attrs) {

    private lateinit var binding: ViewEventListItemBinding
    private var disposables = CompositeDisposable()

    override fun onFinishInflate() {
        super.onFinishInflate()
        binding = ViewEventListItemBinding.bind(this)
    }

    fun bind(event: Event, favoritesStorage: FavoritesStorage) {
        Timber.d("bind() called with event = [%s]", event)

        with(event) {
            loadImage(event)
            binding.title.text = name
            binding.startTime.text = startTime?.toTimeString() ?: ""
            binding.location.text = locationName ?: ""
            binding.website.visibility = if (website.isNullOrBlank()) View.GONE else View.VISIBLE
            binding.website.setOnClickListener {
                !website.isNullOrBlank() || return@setOnClickListener
                website?.toUri()?.launchCustomTab(context)
            }
        }

        disposables += favoritesStorage.hasEvent(event)
                .observeOnAndroidScheduler()
                .subscribe(handleFavoriteEvent)

        binding.favoriteButton.setOnClickListener {
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
        binding.imageContainer.setBackgroundColor(backgroundColor)

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
                .into(binding.image)

        event.image?.let {
            disposables += it.toSingle()
                    .subscribeOnIoScheduler()
                    .observeOnAndroidScheduler()
                    .subscribe(
                            { bytes -> glide.load(bytes)
                                    .apply(RequestOptions().circleCrop())
                                    .into(binding.image) },
                            { error -> Timber.e(error, "Error loading image") }
                    )
        }
    }

    private val handleFavoriteEvent = { hasEvent: Boolean ->
        with(binding.favoriteButton) {
            if (hasEvent) setAsFavorited() else setAsNotFavorited()
        }
    }

    private val glide get() = Glide.with(this)
}
