package com.example.practiceproject.ui.viewpager

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.viewpager2.widget.ViewPager2
import com.example.practiceproject.R
import com.example.practiceproject.data.places.Place
import com.example.practiceproject.data.places.PlacesViewModel
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator

class ViewPagerFragment : Fragment() {
    private lateinit var adapter: ListAdapter
    private lateinit var viewPager: ViewPager2
    private lateinit var tabLayout: TabLayout
    private val placesViewModel: PlacesViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_view_pager, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val placesObserver = Observer<List<List<Place>>> {
            viewPager.adapter = adapter
            TabLayoutMediator(tabLayout, viewPager) { tab, position ->
                if (it[position].isNotEmpty()) {
                    tab.text = it[position][0]::class.java.simpleName
                } else {
                    tab.text = "Loading"
                }
            }.attach()
        }
        placesViewModel.getPlaces().observe(viewLifecycleOwner, placesObserver)

        viewPager = view.findViewById(R.id.view_pager)
        tabLayout = view.findViewById(R.id.tab_layout)

        adapter = ListAdapter(this, placesViewModel.getPlaces())
        viewPager.adapter = adapter
        TabLayoutMediator(tabLayout, viewPager) { tab, _ ->
            tab.text = "Loading"
        }.attach()
    }
}