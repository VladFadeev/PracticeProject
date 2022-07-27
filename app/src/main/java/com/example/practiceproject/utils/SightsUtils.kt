package com.example.practiceproject.utils

import android.content.Context
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
            val textView = TextView(layout.context)
            textView.text = sight.getName()
            textView.setOnClickListener {
                if (behavior.state == BottomSheetBehavior.STATE_HIDDEN) {
                    behavior.state = BottomSheetBehavior.STATE_COLLAPSED
                }
                peek.text = (it as TextView).text
            }
            layout.addView(textView)
        }
    }
}