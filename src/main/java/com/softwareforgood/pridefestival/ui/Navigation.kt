package com.softwareforgood.pridefestival.ui

import androidx.annotation.IdRes
import androidx.annotation.LayoutRes
import com.softwareforgood.pridefestival.R

enum class Navigation(@LayoutRes val layout: Int, @IdRes val actionId: Int) {
    EVENTS(R.layout.view_events, R.id.menu_schedule),
    FAVORITES(R.layout.view_favorites, R.id.menu_favorites),
    MAP(R.layout.view_map, R.id.menu_map),
    PARADE(R.layout.view_parade, R.id.menu_parade),
    VENDOR(R.layout.view_vendor, R.id.menu_vendors);

    companion object {
        fun fromActionId(@IdRes actionId: Int) = values().first { it.actionId == actionId }
    }
}
