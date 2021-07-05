package com.ranjan.malav.morselight_flashlightwithmorsecode.utils

import android.content.Context
import android.util.AttributeSet
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.res.getDrawableOrThrow
import androidx.core.content.res.getStringOrThrow
import com.ranjan.malav.morselight_flashlightwithmorsecode.R

class LabelledContainer(context: Context, attrs: AttributeSet?) : ConstraintLayout(context, attrs) {
    private val view = LayoutInflater.from(context)
        .inflate(R.layout.labelled_container_layout, this, true)

    init {
        context.theme.obtainStyledAttributes(
            attrs,
            R.styleable.LabelledContainer, 0, 0
        ).apply {
            try {
                val label: TextView = view.findViewById(R.id.label)
                val border: View = view.findViewById(R.id.border)

                label.text = getStringOrThrow(R.styleable.LabelledContainer_label)
                label.setTextColor(
                    getColor(
                        R.styleable.LabelledContainer_labelColor,
                        R.attr.containerLabelColor
                    )
                )
                border.background = getDrawableOrThrow(R.styleable.LabelledContainer_borderColor)

                val a = TypedValue()
                context.theme.resolveAttribute(R.attr.backgroundColor, a, true)
                label.setBackgroundColor(
                    getColor(
                        R.styleable.LabelledContainer_labelBgColor,
                        a.data
                    )
                )
            } finally {
                recycle()
            }
        }
    }
}