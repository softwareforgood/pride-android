package com.softwareforgood.pridefestival.ui.misc

import android.content.Context
import android.util.AttributeSet
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class LinearRecyclerView(context: Context, attrs: AttributeSet) : RecyclerView(context, attrs) {
    init {
        layoutManager = LinearLayoutManager(context).apply {
            orientation = RecyclerView.VERTICAL
        }
    }
}
