package com.softwareforgood.pridefestival.ui.misc

import android.content.Context
import android.util.AttributeSet
import android.widget.LinearLayout
import kotlinx.android.synthetic.main.view_simple_list_header.view.*

class SimpleListHeaderView(context: Context, attrs: AttributeSet) : LinearLayout(context, attrs) {
    fun bind(displayText: String) {
        list_header_text.text = displayText
    }
}
