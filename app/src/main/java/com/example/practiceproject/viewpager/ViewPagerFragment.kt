package com.example.practiceproject.viewpager

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.viewpager2.widget.ViewPager2
import com.example.practiceproject.R
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator

class ViewPagerFragment : Fragment() {
    private lateinit var adapter: ListAdapter
    private lateinit var viewPager: ViewPager2
    private lateinit var tabLayout: TabLayout
    private var placesViewModel: PlacesViewModel? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_view_pager, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        if (placesViewModel == null) {
            placesViewModel = ViewModelProvider(this)[PlacesViewModel::class.java]
        }
        adapter = ListAdapter(this, placesViewModel!!.placesList.value!!)
        viewPager = view.findViewById(R.id.view_pager)

        tabLayout = view.findViewById(R.id.tab_layout)
        val placesObserver = Observer<List<List<Place>>> { placesList ->
            viewPager.adapter = ListAdapter(this, placesList)
        }
        placesViewModel!!.placesList.observe(viewLifecycleOwner, placesObserver)
        viewPager.adapter = adapter
        TabLayoutMediator(tabLayout, viewPager) { tab, position ->
            tab.text = placesViewModel!!.placesList.value!![position][0]::class.java.simpleName
        }.attach()
        //placesViewModel!!.updateAllPlaces()
    }
}