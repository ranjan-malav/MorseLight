package com.ranjan.malav.morselight_flashlightwithmorsecode.utils

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.widget.FrameLayout
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.widget.AppCompatImageView
import androidx.core.content.ContextCompat
import androidx.core.content.res.getStringOrThrow
import androidx.core.widget.ImageViewCompat
import com.google.android.material.card.MaterialCardView
import com.ranjan.malav.morselight_flashlightwithmorsecode.R


class AccountOptionView(context: Context, attrs: AttributeSet) : LinearLayout(context, attrs) {
    private val view = LayoutInflater.from(context)
        .inflate(R.layout.account_option_view, this, true)

    private val icon: AppCompatImageView = view.findViewById(R.id.account_option_icon)

    init {
        context.theme.obtainStyledAttributes(
            attrs,
            R.styleable.AccountOption, 0, 0
        ).apply {
            try {
                val container: MaterialCardView =
                    view.findViewById(R.id.account_option_container)
                container.setOnClickListener { (it.parent as View).performClick() }

                val iconContainer: FrameLayout =
                    view.findViewById(R.id.account_option_icon_container)
                val title: TextView = view.findViewById(R.id.account_option_title)
                val nextIcon: AppCompatImageView = view.findViewById(R.id.account_option_next)

                title.text = getStringOrThrow(R.styleable.AccountOption_title)
                icon.setImageResource(getResourceId(R.styleable.AccountOption_icon, -1))

                val titleColor = getColorStateList(R.styleable.AccountOption_titleColor)
                if (titleColor != null) {
                    title.setTextColor(titleColor)
                }

                val iconColor = getColorStateList(R.styleable.AccountOption_iconTint)
                if (iconColor != null) {
                    ImageViewCompat.setImageTintList(nextIcon, iconColor)
                    ImageViewCompat.setImageTintList(icon, iconColor)
                }

                when (getInt(R.styleable.AccountOption_iconBackgroundTint, 0)) {
                    0 -> iconContainer.background =
                        ContextCompat.getDrawable(view.context, R.drawable.ripple_background_36dp)
                }
            } finally {
                recycle()
            }
        }
    }
}