package com.example.practiceproject.utils

import android.content.Context
import android.util.TypedValue
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import com.example.practiceproject.R

object ListItemUtils {
    private const val TEXT_VIEW_MARGIN_BOTTOM = 20f
    private const val TEXT_VIEW_PADDING_LEFT = 10f
    private const val TEXT_VIEW_PADDING_VERTICAL = 8f
    private const val TEXT_SIZE = 6f

    fun makeTextViewItem(context: Context) : TextView {
        val textView = TextView(context)
        textView.setBackgroundColor(R.color.teal_200)
        val params =
            ConstraintLayout.LayoutParams(
                ConstraintLayout.LayoutParams.MATCH_PARENT,
                ConstraintLayout.LayoutParams.WRAP_CONTENT
            )
        params.bottomMargin =
            TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP,
                TEXT_VIEW_MARGIN_BOTTOM,
                context.resources.displayMetrics
            ).toInt()
        textView.setPadding(
            TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP, TEXT_VIEW_PADDING_LEFT,
                context.resources.displayMetrics
            ).toInt(),
            TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP, TEXT_VIEW_PADDING_VERTICAL,
                context.resources.displayMetrics
            ).toInt(),
            0,
            TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP, TEXT_VIEW_PADDING_VERTICAL,
                context.resources.displayMetrics
            ).toInt()
        )
        textView.layoutParams = params
        textView.textSize = TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_SP,
            TEXT_SIZE,
            context.resources.displayMetrics
        )
        return textView
    }
}