package com.example.practiceproject.utils

import android.content.Context
import android.util.TypedValue
import android.widget.LinearLayout
import android.widget.TextView
import androidx.lifecycle.LiveData
import com.example.practiceproject.R
import com.example.practiceproject.dao
import com.example.practiceproject.data.places.Place
import com.example.practiceproject.data.places.Sight
import com.example.practiceproject.data.places.Station
import com.google.android.material.bottomsheet.BottomSheetBehavior
import java.util.concurrent.Executors

object SightsUtils: PlacesUtils {
    override fun getAll(): List<Place> {
        return dao.getAllSights()
    }

    override fun addAll(data: LiveData<List<List<Place>>>) {
        //return getAll()
    }

    fun receiveSights(context: Context) {
        val service = Executors.newSingleThreadExecutor()
        service.submit {
            dao.insertAll(
                JsonUtils.createSightsList(JsonUtils.readJsonFromAssets(context))
            )
        }
        service.shutdown()
    }

    fun addSightsList(layout: LinearLayout, bottomSheet: LinearLayout, sights: List<Sight>) {
        layout.removeAllViews()
        val behavior = BottomSheetBehavior.from(bottomSheet)
        val peek = bottomSheet.findViewById<TextView>(R.id.bottom_sheet_peek)
        for (sight in sights) {
            val textView = TextView(layout.context, null, 0, R.style.ListItem)
            textView.text = sight.getName()
            val ll = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            )
            ll.setMargins(
                TypedValue.applyDimension(
                    TypedValue.COMPLEX_UNIT_DIP,
                    5f,
                    layout.resources.displayMetrics
                ).toInt(),
                TypedValue.applyDimension(
                    TypedValue.COMPLEX_UNIT_DIP,
                    15f,
                    layout.resources.displayMetrics
                ).toInt(),
                TypedValue.applyDimension(
                    TypedValue.COMPLEX_UNIT_DIP,
                    5f,
                    layout.resources.displayMetrics
                ).toInt(),
                TypedValue.applyDimension(
                    TypedValue.COMPLEX_UNIT_DIP,
                    5f,
                    layout.resources.displayMetrics
                ).toInt()
            )
            textView.layoutParams = ll
            textView.setOnClickListener {
                if (behavior.state == BottomSheetBehavior.STATE_HIDDEN) {
                    behavior.state = BottomSheetBehavior.STATE_COLLAPSED
                } else if (peek.text == (it as TextView).text) {
                    behavior.state = BottomSheetBehavior.STATE_HIDDEN
                }
                if ((it as TextView).text != peek.text) {
                    peek.text = it.text
                }
            }
            layout.addView(textView)
        }
    }
}