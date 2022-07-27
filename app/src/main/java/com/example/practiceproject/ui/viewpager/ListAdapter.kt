package com.example.practiceproject.ui.viewpager

import androidx.fragment.app.Fragment
import androidx.lifecycle.LiveData
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.example.practiceproject.data.places.Place
import com.example.practiceproject.data.places.Sight
import com.example.practiceproject.data.places.Station

class ListAdapter(fragment: Fragment, private val data: LiveData<List<List<Place>>>) : FragmentStateAdapter(fragment) {
    override fun getItemCount(): Int = data.value?.size ?: 1

    override fun createFragment(position: Int): Fragment {
        val fragment = if (data.value != null &&
            position < data.value!!.size && data.value!![position].isNotEmpty()) {
            when (data.value!![position][0]) {
                is Station -> PageFragment.newInstance(data.value!![position])
                is Sight -> PageFragment.newInstance(data.value!![position])
                else -> PageFragment.newInstance()
            }
        } else {
            PageFragment.newInstance()
        }
        return fragment
    }
}