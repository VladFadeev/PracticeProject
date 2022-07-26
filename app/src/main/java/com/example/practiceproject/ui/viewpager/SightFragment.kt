package com.example.practiceproject.ui.viewpager

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import com.example.practiceproject.R
import com.example.practiceproject.data.places.Sight
import com.example.practiceproject.data.places.Station
import com.example.practiceproject.utils.SightsUtils
import com.example.practiceproject.utils.StationsUtils
import com.google.android.material.bottomsheet.BottomSheetBehavior

private const val LIST = "List"

class SightFragment : Fragment() {

    private var list: List<Sight>? = null

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
        return inflater.inflate(R.layout.fragment_sight, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val behavior = BottomSheetBehavior
            .from(view.findViewById<LinearLayout>(R.id.bottom_sheet))
        behavior.state = BottomSheetBehavior.STATE_HIDDEN
        val layout = view.findViewById<LinearLayout>(R.id.sightsList)
        SightsUtils.addSightsList(layout, view.findViewById(R.id.bottom_sheet), list!!)
    }

    companion object {
        @JvmStatic
        fun newInstance(list: List<Sight>) =
            SightFragment().apply {
                arguments = Bundle().apply {
                    putParcelableArrayList(LIST, list as ArrayList)
                }
            }
    }
}