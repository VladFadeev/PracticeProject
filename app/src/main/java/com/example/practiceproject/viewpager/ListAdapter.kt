package com.example.practiceproject.viewpager

import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter

class ListAdapter(fragment: Fragment, private val lists: List<List<Place>>) : FragmentStateAdapter(fragment) {
    override fun getItemCount(): Int = lists.size

    override fun createFragment(position: Int): Fragment {
        val fragment = when (lists[position][0]) {
            is Station -> StationsFragment(lists[position] as List<Station>)
            is Sight -> SightFragment()
            else -> BlankFragment()
        }
        return fragment
    }
}