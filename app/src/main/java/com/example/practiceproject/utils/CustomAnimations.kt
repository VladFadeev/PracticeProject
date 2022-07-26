package com.example.practiceproject.utils

import android.view.View
import android.view.animation.Animation
import android.view.animation.Transformation


object CustomAnimations {
    fun expand(v: View) {
        val targetHeight = v.layoutParams.height
        // Older versions of android (pre API 21) cancel animations for views with a height of 0.
        v.layoutParams.height = 1
        v.visibility = View.VISIBLE
        val a: Animation = object : Animation() {
            override fun applyTransformation(interpolatedTime: Float, t: Transformation?) {
                v.layoutParams.height = (targetHeight * interpolatedTime).toInt()
                v.requestLayout()
            }

            override fun willChangeBounds(): Boolean {
                return true
            }
        }
        // Expansion speed of 1dp/ms
        a.duration = (targetHeight / (v.context.resources.displayMetrics.density)).toLong()
        v.startAnimation(a)
    }

    fun collapse(v: View) {
        val initialHeight = v.layoutParams.height
        val a: Animation = object : Animation() {
            override fun applyTransformation(interpolatedTime: Float, t: Transformation?) {
                if (interpolatedTime == 1f) {
                    v.visibility = View.GONE
                    v.layoutParams.height = initialHeight
                } else {
                    v.layoutParams.height =
                        initialHeight - (initialHeight * interpolatedTime).toInt()
                    v.requestLayout()
                }
            }
            override fun willChangeBounds(): Boolean {
                return true
            }
        }
        // Collapse speed of 1dp/ms
        a.duration = (initialHeight / v.context.resources.displayMetrics.density).toLong()
        v.startAnimation(a)
    }

    fun expand2d(v: View) {
        val targetHeight = v.layoutParams.height
        val targetWidth = v.layoutParams.width
        // Older versions of android (pre API 21) cancel animations for views with a height of 0.
        v.layoutParams.height = 1
        v.layoutParams.width = 1
        v.visibility = View.VISIBLE
        val a: Animation = object : Animation() {
            override fun applyTransformation(interpolatedTime: Float, t: Transformation?) {
                v.layoutParams.height = (targetHeight * interpolatedTime).toInt()
                v.layoutParams.width = (targetWidth * interpolatedTime).toInt()
                v.requestLayout()
            }

            override fun willChangeBounds(): Boolean {
                return true
            }
        }
        // Expansion speed of 1dp/ms
        a.duration = (1.5 * targetHeight / (v.context.resources.displayMetrics.density)).toLong()
        v.startAnimation(a)
    }

    fun collapse2d(v: View) {
        val initialHeight = v.layoutParams.height
        val initialWidth = v.layoutParams.width
        val a: Animation = object : Animation() {
            override fun applyTransformation(interpolatedTime: Float, t: Transformation?) {
                if (interpolatedTime == 1f) {
                    v.visibility = View.GONE
                    v.layoutParams.height = initialHeight
                    v.layoutParams.width = initialWidth
                } else {
                    v.layoutParams.height =
                        initialHeight - (initialHeight * interpolatedTime).toInt()
                    v.layoutParams.width =
                        initialWidth - (initialWidth * interpolatedTime).toInt()
                    v.requestLayout()
                }
            }
            override fun willChangeBounds(): Boolean {
                return true
            }
        }
        // Collapse speed of 1dp/ms
        a.duration = (1.5 * initialHeight / v.context.resources.displayMetrics.density).toLong()
        v.startAnimation(a)
    }
}