package com.softwareforgood.pridefestival.ui.parade

import android.content.Context
import android.util.AttributeSet
import androidx.constraintlayout.widget.ConstraintLayout
import com.softwareforgood.pridefestival.R
import com.softwareforgood.pridefestival.data.FavoritesStorage
import com.softwareforgood.pridefestival.data.model.ParadeEvent
import com.softwareforgood.pridefestival.databinding.ViewParadeListItemBinding
import com.softwareforgood.pridefestival.util.*
import io.reactivex.disposables.CompositeDisposable

class ParadeItemView(context: Context, attrs: AttributeSet) : ConstraintLayout(context, attrs) {

    private var disposables = CompositeDisposable()

    private lateinit var binding: ViewParadeListItemBinding

    override fun onFinishInflate() {
        super.onFinishInflate()
        binding = ViewParadeListItemBinding.bind(this)
    }

    fun bind(parade: ParadeEvent, favoritesStorage: FavoritesStorage) {
        with(parade) {
            binding.title.text = name
            binding.lineup.text = getString(R.string.lineup_number, lineupNumber)
            binding.details.text = details ?: ""
        }

        disposables += favoritesStorage.hasParade(parade)
                .observeOnAndroidScheduler()
                .subscribe(handleFavoriteParade)

        binding.favoriteButton.setOnClickListener {
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
        with(binding.favoriteButton) {
                if (hasParade) setAsFavorited() else setAsNotFavorited()
        }
    }
}
