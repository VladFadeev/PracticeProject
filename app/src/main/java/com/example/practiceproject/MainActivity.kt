package com.example.practiceproject

import android.Manifest
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.NavigationUI
import com.google.android.material.bottomnavigation.BottomNavigationView

class MainActivity : AppCompatActivity() {
    private var interactionListener: MyUserInteractionListener? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val bottomNavigationView = findViewById<BottomNavigationView>(R.id.menu)
        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        val navController = navHostFragment.navController
        NavigationUI.setupWithNavController(bottomNavigationView, navController)
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        PermissionsUtils.onRequestPermissionsResult(requestCode, grantResults)
        when (permissions[0]) {
            Manifest.permission.ACCESS_FINE_LOCATION -> {
                if (!PermissionsUtils.isLocationPermissionGranted) {
                    super.onRequestPermissionsResult(requestCode, permissions, grantResults)
                }
                val mapFragment = MapFragment()
                mapFragment.updateLocationUI()
            }
            Manifest.permission.INTERNET -> if (!PermissionsUtils.isInternetPermissionGranted) {
                super.onRequestPermissionsResult(requestCode, permissions, grantResults)
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