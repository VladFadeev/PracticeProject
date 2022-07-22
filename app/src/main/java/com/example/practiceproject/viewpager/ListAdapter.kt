package com.example.practiceproject.viewpager

import androidx.fragment.app.Fragment
import androidx.lifecycle.LiveData
import androidx.viewpager2.adapter.FragmentStateAdapter

class ListAdapter(fragment: Fragment, private val data: LiveData<List<List<Place>>>) : FragmentStateAdapter(fragment) {
    override fun getItemCount(): Int = data.value?.size ?: 1

    override fun createFragment(position: Int): Fragment {
        val fragment = if (data.value != null) when (data.value!![position][0]) {
            is Station -> StationsFragment.newInstance(data.value!![position] as List<Station>)
            is Sight -> SightFragment()
            else -> BlankFragment()
        } else BlankFragment()
        return fragment
    }
}