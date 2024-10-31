package com.example.komyut_v1

import android.Manifest
import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.view.View
import android.view.WindowInsets
import android.view.WindowManager
import android.widget.Button
import android.widget.FrameLayout
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.app.ActivityCompat
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions

class MainActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var btnCenterLocation: Button
    private lateinit var btnPlanTrip: Button
    private lateinit var mapPlaceholder: FrameLayout
    private lateinit var toolbar: Toolbar
    private lateinit var map: GoogleMap

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_main) // Ensure this matches your layout file name

        // Initialize the map
        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map_placeholder) as SupportMapFragment
        mapFragment.getMapAsync(this) // Load the map asynchronously

        // Center Location Button click listener
        btnCenterLocation.setOnClickListener {
            centerMapOnLocation()
        }

        // Plan a Trip Button click listener
        btnPlanTrip.setOnClickListener {
            val intent = Intent(this, RoutePlannerActivity::class.java)
            startActivity(intent)
        }


    }

    // Callback when the map is ready
    override fun onMapReady(googleMap: GoogleMap) {
        map = googleMap
        checkLocationPermission()
        // Set a marker at a specific location (e.g., Manila)
        val location = LatLng(14.5995, 120.9842) // Example: Manila
        map.addMarker(MarkerOptions().position(location).title("Marker in Manila"))
        map.moveCamera(CameraUpdateFactory.newLatLngZoom(location, 15f))
    }

    // Check for location permission
    private fun checkLocationPermission() {
        if (ActivityCompat.checkSelfPermission(
                this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // Request the permission
            ActivityCompat.requestPermissions(
                this, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), 1
            )
        } else {
            // Enable the user's location on the map
            map.isMyLocationEnabled = true
        }
    }

    // Center the map on the user's location
    private fun centerMapOnLocation() {
        if (::map.isInitialized) {
            // Assuming you want to center on Manila as a default
            val location = LatLng(14.5995, 120.9842) // Example: Manila
            map.moveCamera(CameraUpdateFactory.newLatLngZoom(location, 15f))
            Toast.makeText(this, "Centered location...", Toast.LENGTH_SHORT).show()
        } else {
            Toast.makeText(this, "Map is not ready yet!", Toast.LENGTH_SHORT).show()
        }
    }

    // Handle permission request result
    @SuppressLint("MissingPermission")
    override fun onRequestPermissionsResult(
        requestCode: Int, permissions: Array<out String>, grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == 1 && grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            map.isMyLocationEnabled = true
        } else {
            Toast.makeText(this, "Permission denied", Toast.LENGTH_SHORT).show()
        }
    }

}
