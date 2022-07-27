package com.example.practiceproject.ui.viewpager

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import com.example.practiceproject.R
import com.example.practiceproject.data.places.Place
import com.example.practiceproject.data.places.PlaceType
import com.example.practiceproject.data.places.Sight
import com.example.practiceproject.data.places.Station
import com.example.practiceproject.utils.SightsUtils
import com.example.practiceproject.utils.StationsUtils
import com.google.android.material.bottomsheet.BottomSheetBehavior

private const val LIST = "List"

class PageFragment : Fragment() {

    private var list: List<Place>? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            list = it.getParcelableArrayList(LIST)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_page, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val behavior = BottomSheetBehavior
            .from(view.findViewById<LinearLayout>(R.id.bottom_sheet))
        behavior.state = BottomSheetBehavior.STATE_HIDDEN
        val layout = view.findViewById<LinearLayout>(R.id.placesList)
        if (list != null) {
            when (list!![0].getType()) {
                PlaceType.Station -> StationsUtils.addStationsList(
                    layout,
                    view.findViewById(R.id.bottom_sheet),
                    list!! as List<Station>
                )
                PlaceType.Sight -> SightsUtils.addSightsList(
                    layout,
                    view.findViewById(R.id.bottom_sheet),
                    list!! as List<Sight>
                )
            }
        } else {
            view.findViewById<TextView>(R.id.place).text = "Loading"
        }
    }

    companion object {
        @JvmStatic
        fun newInstance(list: List<Place>) =
            PageFragment().apply {
                arguments = Bundle().apply {
                    putParcelableArrayList(LIST, list as ArrayList)
                }
            }

        @JvmStatic
        fun newInstance() = PageFragment()
    }
}