package com.softwareforgood.pridefestival.ui

import android.content.Context
import android.util.AttributeSet
import androidx.constraintlayout.widget.ConstraintLayout
import com.softwareforgood.pridefestival.R
import com.softwareforgood.pridefestival.util.inflate
import kotlinx.android.synthetic.main.view_generic_error.view.*

class GenericErrorView(
    context: Context,
    attrs: AttributeSet
) : ConstraintLayout(context, attrs) {
    init {
        inflate(resource = R.layout.view_generic_error, attachToRoot = true)
    }

    override fun setOnClickListener(listener: OnClickListener?) {
        error_try_again.setOnClickListener(listener)
    }
}
