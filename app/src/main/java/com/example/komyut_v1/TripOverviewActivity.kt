package com.example.komyut_v1

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.widget.Button
import android.widget.ImageButton
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.core.content.ContextCompat
import org.json.JSONObject

class TripOverviewActivity : AppCompatActivity() {

    private lateinit var transportModeTextView: TextView
    private lateinit var transportModeDescTextView: TextView
    private lateinit var etaTextView: TextView
    private lateinit var etaDescTextView: TextView
    private lateinit var fareTextView: TextView
    private lateinit var fareDescTextView: TextView
    private lateinit var directionsDescTextView: TextView
    private lateinit var directionsLayout: LinearLayout
    private lateinit var startTripButton: Button
    private lateinit var backButton: ImageButton
    private lateinit var homeButton: ImageButton

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_trip_overview)


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
            startActivity(Intent(this, MainActivity::class.java))
            Toast.makeText(this, "Navigating to Home", Toast.LENGTH_SHORT).show()
        }

        // Initialize views
        transportModeTextView = findViewById(R.id.tv_transport_mode)
        transportModeDescTextView = findViewById(R.id.tv_transport_mode_desc)
        etaTextView = findViewById(R.id.tv_eta)
        etaDescTextView = findViewById(R.id.tv_eta_desc)
        fareTextView = findViewById(R.id.tv_fare)
        fareDescTextView = findViewById(R.id.tv_fare_desc)
        directionsDescTextView = findViewById(R.id.tv_directions_desc)
        directionsLayout = findViewById(R.id.directions_layout)
        startTripButton = findViewById(R.id.btn_start_trip)

        // Get the route data from the intent
        val routeData = intent.getStringExtra("route_data")
        if (routeData != null) {
            displayRouteDetails(JSONObject(routeData))
        } else {
            showToast("No trip details available.")
        }

        startTripButton.setOnClickListener {
            val intent = Intent(this, TripActivity::class.java)
            intent.putExtra("route_data", routeData)
            startActivity(intent)
        }

    }

    private fun displayRouteDetails(route: JSONObject) {
        // Set transport mode and description
        transportModeTextView.text = route.getString("transport_mode")
        transportModeDescTextView.text = "The mode of transportation chosen for this trip."

        // Set ETA and description
        etaTextView.text = route.getString("travel_time")
        etaDescTextView.text = "Estimated time of arrival at your destination."

        // Set fare and description
        fareTextView.text = route.getString("predicted_fare")
        fareDescTextView.text = "Total fare for the selected trip."


        // Display route name, get on and get off information
        val routeName = route.getString("route_name")
        val getOn = route.getString("get_on")
        val getOff = route.getString("get_off")

        // Create a header for the route details
        val routeHeader = TextView(this).apply {
            text = "Route: $routeName"
            textSize = 20f
            setPadding(16, 16, 16, 8) // Add padding
            setTextColor(ContextCompat.getColor(context, android.R.color.black))
        }

        val getOnOffInfo = TextView(this).apply {
            text = "Get On: $getOn\nGet Off: $getOff"
            textSize = 16f
            setPadding(16, 0, 16, 8) // Add padding
            setTextColor(ContextCompat.getColor(context, android.R.color.darker_gray))
        }

        // Add route header and get on/off information to the directions layout
        directionsLayout.addView(routeHeader)
        directionsLayout.addView(getOnOffInfo)

        // Populate directions layout
        val directionsArray = route.getJSONArray("directions")
        for (i in 0 until directionsArray.length()) {
            val directionView = TextView(this).apply {
                text = directionsArray.getString(i)
                textSize = 14f // Set a consistent text size for directions
                setPadding(16, 8, 16, 8) // Add padding around each direction
                setBackgroundResource(R.drawable.direction_background)
            }
            directionsLayout.addView(directionView)
        }
    }


    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }
}
