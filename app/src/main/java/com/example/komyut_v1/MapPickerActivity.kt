package com.example.komyut_v1

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions

class MapPickerActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var map: GoogleMap

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_map_picker)

        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)
    }

    override fun onMapReady(googleMap: GoogleMap) {
        map = googleMap

        // Set an onClickListener to capture the selected location
        map.setOnMapClickListener { latLng ->
            // Add a marker and move the camera
            map.clear()
            map.addMarker(MarkerOptions().position(latLng).title("Selected Location"))
            map.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15f))

            // Prepare the result to send back
            val resultIntent = Intent().apply {
                putExtra("location", "${latLng.latitude}, ${latLng.longitude}")
            }
            setResult(Activity.RESULT_OK, resultIntent)
            finish() // Close the activity
        }
    }
}
