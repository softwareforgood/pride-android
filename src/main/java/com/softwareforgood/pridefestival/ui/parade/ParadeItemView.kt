package com.softwareforgood.pridefestival.ui.parade

import android.content.Context
import android.util.AttributeSet
import androidx.constraintlayout.widget.ConstraintLayout
import com.softwareforgood.pridefestival.R
import com.softwareforgood.pridefestival.data.FavoritesStorage
import com.softwareforgood.pridefestival.data.model.ParadeEvent
import com.softwareforgood.pridefestival.util.*
import io.reactivex.disposables.CompositeDisposable
import kotlinx.android.synthetic.main.view_parade_list_item.view.*

class ParadeItemView(context: Context, attrs: AttributeSet) : ConstraintLayout(context, attrs) {

    private var disposables = CompositeDisposable()

    fun bind(parade: ParadeEvent, favoritesStorage: FavoritesStorage) {
        with(parade) {
            parade_list_item_title.text = name
            parade_list_item_lineup.text = getString(R.string.lineup_number, lineupNumber)
            parade_list_item_details.text = details ?: ""
        }

        disposables += favoritesStorage.hasParade(parade)
                .observeOnAndroidScheduler()
                .subscribe(handleFavoriteParade)

        parade_list_item_favorite_button.setOnClickListener {
            disposables += favoritesStorage.hasParade(parade)
                    .flatMap { hasParade: Boolean ->
                        if (hasParade) favoritesStorage.deleteParade(parade).toSingleDefault(!hasParade)
                        else favoritesStorage.saveParade(parade).toSingleDefault(!hasParade)
                    }
                    .observeOnAndroidScheduler()
                    .subscribe(handleFavoriteParade)
        }
    }

    override fun onDetachedFromWindow() {
        disposables.clear()
        super.onDetachedFromWindow()
    }

    private val handleFavoriteParade = { hasParade: Boolean ->
        with(parade_list_item_favorite_button) {
                if (hasParade) setAsFavorited() else setAsNotFavorited()
        }
    }
}
