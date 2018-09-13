package com.softwareforgood.pridefestival.util

import android.graphics.Color
import android.widget.ImageView
import com.softwareforgood.pridefestival.R

fun ImageView.setAsFavorited() {
    setImageResource(R.drawable.ic_favorite)
    setColorFilter(getColor(R.color.favorited_color))
}

fun ImageView.setAsNotFavorited() {
    setImageResource(R.drawable.ic_favorite_border)
    setColorFilter(Color.BLACK)
}
