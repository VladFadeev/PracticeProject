package com.example.practiceproject

import android.app.Activity
import android.os.Handler
import android.widget.Button
import android.widget.SeekBar

class SeekBarInactivityHandler(private val button: Button, private val seekBar: SeekBar, private val activity: Activity) {
    private val mHandler = Handler(activity.mainLooper)
    private val customAnimations = CustomAnimations()
    private val inactivityCallback : Runnable = Runnable{
        (activity as MainActivity).setInteractionListener(null)
        customAnimations.collapse(seekBar)
        customAnimations.expand2d(button)
    }

    fun startHandler(delay: Long = 3000) {
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