package com.calai.bitecal.widget

import android.graphics.Typeface
import android.text.SpannableString
import android.text.Spanned
import android.text.style.ForegroundColorSpan
import android.text.style.StyleSpan

object BiteCalWidgetText {
    fun remainingLabel(
        text: String,
        labelColor: Int,
        emphasisColor: Int
    ): CharSequence {
        val splitIndex = text.lastIndexOf(' ')
        val spannable = SpannableString(text)
        if (splitIndex <= 0 || splitIndex >= text.lastIndex) {
            spannable.setSpan(
                ForegroundColorSpan(emphasisColor),
                0,
                text.length,
                Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
            )
            spannable.setSpan(
                StyleSpan(Typeface.BOLD),
                0,
                text.length,
                Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
            )
            return spannable
        }

        spannable.setSpan(
            ForegroundColorSpan(labelColor),
            0,
            splitIndex + 1,
            Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
        )
        spannable.setSpan(
            ForegroundColorSpan(emphasisColor),
            splitIndex + 1,
            text.length,
            Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
        )
        spannable.setSpan(
            StyleSpan(Typeface.BOLD),
            splitIndex + 1,
            text.length,
            Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
        )
        return spannable
    }
}
