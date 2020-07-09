package com.softwareforgood.pridefestival.ui

import android.content.Context
import android.util.AttributeSet
import androidx.constraintlayout.widget.ConstraintLayout
import com.softwareforgood.pridefestival.databinding.ViewGenericErrorBinding
import com.softwareforgood.pridefestival.util.layoutInflater

class GenericErrorView(
    context: Context,
    attrs: AttributeSet
) : ConstraintLayout(context, attrs) {

    private val binding: ViewGenericErrorBinding = ViewGenericErrorBinding.inflate(layoutInflater, this)

    override fun setOnClickListener(listener: OnClickListener?) {
        binding.errorTryAgain.setOnClickListener(listener)
    }
}
