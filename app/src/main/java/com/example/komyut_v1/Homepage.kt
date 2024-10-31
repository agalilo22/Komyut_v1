package com.example.komyut_v1

import android.Manifest
import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.widget.Button
import android.widget.ImageButton
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng

class HomepageActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var map: GoogleMap
    private lateinit var backButton: ImageButton // Declare back button
    private lateinit var homeButton: ImageButton // Declare home button


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.homepage)

        // Initialize navigation buttons
        backButton = findViewById(R.id.button_back)
        homeButton = findViewById(R.id.button_home)

        backButton.setOnClickListener { onBackPressed() } // Go back to the previous activity
        homeButton.setOnClickListener {
            // Start the main activity (or the appropriate home activity)
            val intent = Intent(this, MainActivity::class.java) // Update with your actual home activity
            startActivity(intent)
            finish() // Finish this activity if you want to remove it from the back stack
        }


        val btnCenterLocation: ImageButton = findViewById(R.id.btn_center_location)
        val planTripButton: Button = findViewById(R.id.plan_trip_button)


        // Initialize the map
        val mapFragment = supportFragmentManager.findFragmentById(R.id.map_placeholder) as SupportMapFragment
        mapFragment.getMapAsync(this)

        btnCenterLocation.setOnClickListener {
            centerMapOnCurrentLocation()
        }

        planTripButton.setOnClickListener {
            startActivity(Intent(this, RoutePlannerActivity::class.java))
        }
    }

    override fun onMapReady(googleMap: GoogleMap) {
        map = googleMap
        checkLocationPermission()
    }

    private fun checkLocationPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
            != PackageManager.PERMISSION_GRANTED) {
            // Request the permission
            ActivityCompat.requestPermissions(
                this, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), LOCATION_PERMISSION_REQUEST_CODE
            )
        } else {
            // Enable location if permission is granted
            enableMyLocation()
        }
    }

    @SuppressLint("MissingPermission")
    private fun enableMyLocation() {
        if (::map.isInitialized) {
            map.isMyLocationEnabled = true
        } else {
            Toast.makeText(this, "Map is not ready yet!", Toast.LENGTH_SHORT).show()
        }
    }

    private fun centerMapOnCurrentLocation() {
        if (::map.isInitialized) {
            val defaultLocation = LatLng(14.5995, 120.9842) // Example coordinates (Manila)
            map.moveCamera(CameraUpdateFactory.newLatLngZoom(defaultLocation, 12f))
        } else {
            Toast.makeText(this, "Map is not ready yet!", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int, permissions: Array<out String>, grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                enableMyLocation()
            } else {
                Toast.makeText(this, "Permission denied", Toast.LENGTH_SHORT).show()
            }
        }
    }

    companion object {
        private const val LOCATION_PERMISSION_REQUEST_CODE = 1
    }
}
