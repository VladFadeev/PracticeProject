package com.example.practiceproject.utils

import android.app.Activity
import android.os.Handler
import android.widget.Button
import android.widget.SeekBar
import com.example.practiceproject.ui.MainActivity
import com.example.practiceproject.R
import com.example.practiceproject.utils.CustomAnimations

class SeekBarInactivityHandler(private val button: Button,
                               private val seekBar: SeekBar,
                               private val activity: Activity) {
    private val mHandler = Handler(activity.mainLooper)
    private val normalDelay = activity.resources.getInteger(R.integer.normal_delay).toLong()
    private val inactivityCallback : Runnable = Runnable{
        (activity as MainActivity).setInteractionListener(null)
        CustomAnimations.collapse(seekBar)
        CustomAnimations.expand2d(button)
    }

    fun startHandler(delay: Long = normalDelay ) {
        mHandler.postDelayed(inactivityCallback, delay)
    }

    fun resetHandler() {
        mHandler.removeCallbacks (inactivityCallback)
        startHandler()
    }

    fun stopHandler() {
        mHandler.removeCallbacks(inactivityCallback)
    }
}