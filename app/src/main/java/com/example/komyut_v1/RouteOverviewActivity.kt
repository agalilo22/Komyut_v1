package com.example.komyut_v1

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.coordinatorlayout.widget.CoordinatorLayout
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.gms.maps.model.PolylineOptions
import org.json.JSONObject

class RouteOverviewActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var routeDetailsTextView: TextView
    private lateinit var map: GoogleMap
    private lateinit var startPointTextView: TextView
    private lateinit var endPointTextView: TextView
    private lateinit var directionsTextView: TextView
    private lateinit var transportModeTextView: TextView
    private lateinit var travelTimeTextView: TextView
    private lateinit var backButton: ImageButton
    private lateinit var homeButton: ImageButton

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.route_overview)

        // Inflate the navigation bar layout and add it to the main layout
        val coordinatorLayout = findViewById<CoordinatorLayout>(R.id.coordinatorLayout)
        val navBar = LayoutInflater.from(this).inflate(R.layout.nav_bar, coordinatorLayout, false)
        coordinatorLayout.addView(navBar)

        // Initialize buttons
        backButton = navBar.findViewById(R.id.button_back)
        homeButton = navBar.findViewById(R.id.button_home)

        // Set up the Back button functionality
        backButton.setOnClickListener {
            finish() // Navigates back to the previous screen
        }

        // Set up the Home button functionality
        homeButton.setOnClickListener {
            startActivity(Intent(this, MainActivity::class.java)) // Navigates to the home screen
            Toast.makeText(this, "Navigating to Home", Toast.LENGTH_SHORT).show()
        }

        // Initialize views
        routeDetailsTextView = findViewById(R.id.tv_route_overview)
        startPointTextView = findViewById(R.id.startPoint)
        endPointTextView = findViewById(R.id.endPoint)
        directionsTextView = findViewById(R.id.directionsView)
        transportModeTextView = findViewById(R.id.transportMode)
        travelTimeTextView = findViewById(R.id.travelTime)

        // Hide route details, transport mode, and travel time views
        routeDetailsTextView.visibility = View.GONE
        transportModeTextView.visibility = View.GONE
        travelTimeTextView.visibility = View.GONE

        // Get the route details from the intent
        val routeDetails = intent.getStringExtra("route_details") ?: "No route details available"
        routeDetailsTextView.text = routeDetails

        // Set up the map fragment
        val mapFragment = supportFragmentManager.findFragmentById(R.id.map_fragment) as? SupportMapFragment
        mapFragment?.getMapAsync(this) ?: showToast("Map Fragment not found.")

        // Set up click listeners for start and end points
        startPointTextView.setOnClickListener { launchDirectionsActivity() }
        endPointTextView.setOnClickListener { launchDirectionsActivity() }
    }



    override fun onMapReady(googleMap: GoogleMap) {
        map = googleMap  // Initialize the map

        // Get the route JSON string from the intent
        val jsonString = intent.getStringExtra("route_json") ?: return
        parseRoute(jsonString)  // Call the modified parse function
    }

    private fun parseRoute(jsonString: String) {
        try {
            val jsonObject = JSONObject(jsonString)
            val routesArray = jsonObject.getJSONArray("routes")

            // Assuming you have logic to identify the selected route.
            // For demonstration, we will just pick the first route.
            if (routesArray.length() > 0) {
                val route = routesArray.getJSONObject(0) // Change index based on your selection logic

                // Set start and end points
                val start = route.getString("start")
                val end = route.getString("end")
                startPointTextView.text = "Start: $start"
                endPointTextView.text = "End: $end"

                // Get directions
                val directionsArray = route.getJSONArray("directions")
                val directionsList = StringBuilder()
                for (i in 0 until directionsArray.length()) {
                    directionsList.append(directionsArray.getString(i)).append("\n")
                }
                directionsTextView.text = directionsList.toString()

                // Parse the path without displaying transport mode and travel time
                val pathArray = route.getJSONArray("path")
                val path = mutableListOf<LatLng>()
                for (j in 0 until pathArray.length()) {
                    val point = pathArray.getJSONObject(j)
                    val lat = point.getDouble("lat")
                    val lng = point.getDouble("lng")
                    path.add(LatLng(lat, lng))
                }

                // Draw the route on the map
                drawRouteOnMap(path)
            }
        } catch (e: Exception) {
            e.printStackTrace()
            showToast("Error parsing route: ${e.message}")
        }
    }

    private fun drawRouteOnMap(path: List<LatLng>) {
        if (!::map.isInitialized) {
            showToast("Map not ready yet.")
            return
        }

        val polylineOptions = PolylineOptions()
            .addAll(path)
            .color(android.graphics.Color.BLUE)
            .width(10f)

        map.addPolyline(polylineOptions)

        // Add markers for start and end points
        if (path.isNotEmpty()) {
            map.addMarker(MarkerOptions().position(path.first()).title("Start"))
            map.addMarker(MarkerOptions().position(path.last()).title("End"))
            map.moveCamera(CameraUpdateFactory.newLatLngZoom(path.first(), 15f))
        }
    }

    private fun launchDirectionsActivity() {
        val routeJsonString = intent.getStringExtra("route_json") ?: return
        val start = startPointTextView.text.toString().replace("Start: ", "")
        val end = endPointTextView.text.toString().replace("End: ", "")

        // Start DirectionsActivity with the route data passed as extras
        val intent = Intent(this, DirectionsActivity::class.java).apply {
            putExtra("route_json", routeJsonString)  // Pass JSON data
            putExtra("start_location", start)         // Pass start location
            putExtra("end_location", end)             // Pass end location
        }

        startActivity(intent)
    }

    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }
}
