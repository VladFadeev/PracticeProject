package com.example.practiceproject.ui

import android.Manifest
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.NavigationUI
import com.example.practiceproject.R
import com.example.practiceproject.ui.map.MapFragment
import com.example.practiceproject.utils.MyUserInteractionListener
import com.example.practiceproject.utils.PermissionsUtils
import com.example.practiceproject.utils.SightsUtils
import com.example.practiceproject.utils.StationsUtils
import com.google.android.material.bottomnavigation.BottomNavigationView
import java.io.BufferedWriter
import java.io.File
import java.io.FileWriter

class MainActivity : AppCompatActivity() {
    private var interactionListener: MyUserInteractionListener? = null
    private lateinit var bottomNavigationView: BottomNavigationView
    private lateinit var navController: NavController

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        PermissionsUtils.getLocationPermission(this)
        PermissionsUtils.getInternetPermission(this)
        setContentView(R.layout.activity_main)
        val prefs = getSharedPreferences("com.example.practiceproject", MODE_PRIVATE)
        if (prefs.getBoolean("firstrun", true)) {
            if (PermissionsUtils.isInternetPermissionGranted) {
                StationsUtils.ReceiveStations()
            }
            SightsUtils.receiveSights(applicationContext)
            prefs.edit().putBoolean("firstrun", false).commit()
        }
        bottomNavigationView = findViewById(R.id.menu)
        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        navController = navHostFragment.navController
        NavigationUI.setupWithNavController(bottomNavigationView, navController)
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        PermissionsUtils.onRequestPermissionsResult(requestCode, grantResults)
        if (grantResults.isNotEmpty()) {
            when (permissions[0]) {
                Manifest.permission.ACCESS_FINE_LOCATION -> {
                    if (!PermissionsUtils.isLocationPermissionGranted) {
                        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
                    } else {
                        val mapFragment = supportFragmentManager.fragments[0].childFragmentManager.fragments[0] as MapFragment
                        mapFragment.updateDeviceLocation()
                    }
                }
                Manifest.permission.INTERNET -> if (!PermissionsUtils.isInternetPermissionGranted) {
                    super.onRequestPermissionsResult(requestCode, permissions, grantResults)
                }
            }
        }
    }

    fun setInteractionListener(interactionListener: MyUserInteractionListener?) {
        this.interactionListener = interactionListener
    }

    override fun onUserInteraction() {
        super.onUserInteraction()
        interactionListener?.onUserInteraction()
    }
}