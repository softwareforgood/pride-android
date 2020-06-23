package com.softwareforgood.pridefestival.ui.misc

import android.content.Context
import android.util.AttributeSet
import android.widget.LinearLayout
import com.softwareforgood.pridefestival.databinding.ViewSimpleListHeaderBinding

class SimpleListHeaderView(context: Context, attrs: AttributeSet) : LinearLayout(context, attrs) {

    private lateinit var binding: ViewSimpleListHeaderBinding

    override fun onFinishInflate() {
        super.onFinishInflate()
        binding = ViewSimpleListHeaderBinding.bind(this)
    }

    fun bind(displayText: String) {
        binding.listHeaderText.text = displayText
    }
}
