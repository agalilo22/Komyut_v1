package com.example.komyut_v1

import android.Manifest
import android.annotation.SuppressLint
import android.app.AlertDialog
import android.app.TimePickerDialog
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.location.Location
import android.os.Bundle
import android.view.LayoutInflater
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.app.ActivityCompat
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.*
import com.google.android.gms.maps.model.*
import org.json.JSONArray
import org.json.JSONObject
import java.util.*

class RoutePlannerActivity : AppCompatActivity(), OnMapReadyCallback {

    // Use camel case for private property names
    private val modes = arrayOf("Jeepney", "Bus", "MRT/LRT", "Tricycle", "UV Express")
    private lateinit var map: GoogleMap
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var backButton: ImageButton
    private lateinit var homeButton: ImageButton

    // Views initialized lazily
    private val startLocationEditText by lazy { findViewById<EditText>(R.id.start_point) }
    private val destinationEditText by lazy { findViewById<EditText>(R.id.end_point) }
    private val modeSpinner by lazy { findViewById<Spinner>(R.id.spinner_mode) }
    private val travelTimeTextView by lazy { findViewById<TextView>(R.id.tv_set_travel_time) }
    private val recentLocationsListView by lazy { findViewById<ListView>(R.id.lv_recent_locations) }
    private val savedPlacesListView by lazy { findViewById<ListView>(R.id.lv_saved_places) }
    private val routeDetailsTextView by lazy { findViewById<TextView>(R.id.tvRouteDetails) }
    private val btnEnter by lazy { findViewById<Button>(R.id.btn_enter) }
    private val btnRouteOverview by lazy { findViewById<Button>(R.id.btn_route_overview) }
    private val radioGroupSetPoint by lazy { findViewById<RadioGroup>(R.id.rg_set_point) }
    private val btnChooseFromMap by lazy { findViewById<Button>(R.id.btn_choose_from_map) }
    private val btnCurrentLocation by lazy { findViewById<Button>(R.id.btn_current_location) }

    private val recentLocations = listOf("Location A", "Location B", "Location C", "Location D")
    private val savedPlaces = listOf("Home", "Work", "Gym")

    // Updated property names
    private val mapPickerRequest = 100
    private val locationPermissionRequestCode = 1 // Updated property name

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_route_planner)

        showWarningDialog()

        // Inflate the navigation bar layout and add it to the main layout
        val constraintLayout = findViewById<ConstraintLayout>(R.id.constraintLayout) // Replace with your main ConstraintLayout ID
        val navBar = LayoutInflater.from(this).inflate(R.layout.nav_bar, constraintLayout, false)
        constraintLayout.addView(navBar)

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




        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        setupModeSpinner()
        setupListViews()
        setupListeners()
    }

    private fun showWarningDialog() {
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Important Reminder")
        builder.setMessage("Routes are still not finalized. Coordinates do not have stored values yet. " +
                "Kindly choose Route A to B (This is in Recent Locations.) for the meantime so you can proceed.")
        builder.setPositiveButton("OK") { dialog, _ ->
            dialog.dismiss()
        }
        builder.create().show()
    }

    private fun setupModeSpinner() {
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, modes)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        modeSpinner.adapter = adapter
    }

    private fun setupListViews() {
        recentLocationsListView.adapter =
            ArrayAdapter(this, android.R.layout.simple_list_item_1, recentLocations)
        savedPlacesListView.adapter =
            ArrayAdapter(this, android.R.layout.simple_list_item_1, savedPlaces)

        recentLocationsListView.setOnItemClickListener { _, _, position, _ ->
            val location = recentLocations[position]
            showLocationSelectionDialog(location)
        }

        savedPlacesListView.setOnItemClickListener { _, _, position, _ ->
            val place = savedPlaces[position]
            showLocationSelectionDialog(place)
        }
    }

    private fun setupListeners() {
        travelTimeTextView.setOnClickListener { showTimePicker() }

        btnEnter.setOnClickListener {
            handleLocationInput(
                startLocationEditText.text.toString(),
                destinationEditText.text.toString()
            )
        }

        btnRouteOverview.setOnClickListener { launchRouteOverview() }

        btnChooseFromMap.setOnClickListener {
            val intent = Intent(this, MapPickerActivity::class.java)
            startActivityForResult(intent, mapPickerRequest)
        }

        btnCurrentLocation.setOnClickListener {
            // Check for location permission
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                // Request permission if not granted
                ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), locationPermissionRequestCode)
            } else {
                // Permission already granted, get current location
                getCurrentLocation()
            }
        }
    }

    // Handle the result of the permission request
    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults) // Call super method first

        when (requestCode) {
            locationPermissionRequestCode -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // Permission granted, access location
                    getCurrentLocation()
                } else {
                    // Permission denied
                    showToast("Location permission is required to access current location")
                }
            }
        }
    }

    private fun getCurrentLocation() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return // Return if permission is not granted
        }

        fusedLocationClient.lastLocation.addOnSuccessListener { location: Location? ->
            if (location != null) {
                val currentLatLng = LatLng(location.latitude, location.longitude)
                showSetLocationDialog(currentLatLng)
                // Only move the camera if needed; comment out if not required
                // map.moveCamera(CameraUpdateFactory.newLatLngZoom(currentLatLng, 15f))
                // map.addMarker(MarkerOptions().position(currentLatLng).title("You are here"))
            } else {
                showToast("Unable to get current location")
            }
        }.addOnFailureListener { exception ->
            showToast("Failed to retrieve location: ${exception.message}")
        }
    }



    private fun showLocationNotAvailableNotification() {
        showToast("Unable to retrieve current location. Please check your GPS or network settings.")
    }


    private fun showSetLocationDialog(currentLatLng: LatLng) {
        val options = arrayOf("Set as Start Point", "Set as End Point")
        AlertDialog.Builder(this)
            .setTitle("Set Current Location")
            .setItems(options) { _, which ->
                when (which) {
                    0 -> startLocationEditText.setText("Lat: ${currentLatLng.latitude}, Lng: ${currentLatLng.longitude}") // Set Start Point
                    1 -> destinationEditText.setText("Lat: ${currentLatLng.latitude}, Lng: ${currentLatLng.longitude}") // Set End Point
                }
            }
            .show()
    }



    @Deprecated("Deprecated in Java")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == mapPickerRequest && resultCode == RESULT_OK) {
            val selectedLocation = data?.getStringExtra("location") ?: ""

            // Use RadioGroup to determine where to set the location
            if (findViewById<RadioButton>(R.id.rb_set_start).isChecked) {
                startLocationEditText.setText(selectedLocation)
            } else if (findViewById<RadioButton>(R.id.rb_set_end).isChecked) {
                destinationEditText.setText(selectedLocation)
            }
        }
    }

    private fun showLocationSelectionDialog(location: String) {
        val options = arrayOf("Set as Start Point", "Set as End Point")
        AlertDialog.Builder(this)
            .setTitle("Use $location")
            .setItems(options) { _, which ->
                when (which) {
                    0 -> startLocationEditText.setText(location)
                    1 -> destinationEditText.setText(location)
                }
            }
            .show()
    }

    private fun handleLocationInput(start: String, end: String) {
        if (start.isNotEmpty() && end.isNotEmpty()) {
            val routeJson = loadJsonFromAssets("routes.json")
            val matchingRoute = findMatchingRoute(routeJson, start, end)

            if (matchingRoute != null) {
                showToast("Route found: $start to $end")
                routeDetailsTextView.text = matchingRoute
            } else {
                showToast("No route found for $start to $end")
                routeDetailsTextView.text = ""
            }
        } else {
            showToast("Please enter both start and end points")
        }
    }

    private fun findMatchingRoute(jsonString: String, start: String, end: String): String? {
        try {
            val routesArray = JSONObject(jsonString).getJSONArray("routes")
            for (i in 0 until routesArray.length()) {
                val route = routesArray.getJSONObject(i)
                val userInput = route.getString("userInput")

                if (userInput.contains(start, ignoreCase = true) && userInput.contains(end, ignoreCase = true)) {
                    return userInput // or format this string as you need
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
            showToast("Error parsing routes: ${e.message}")
        }
        return null
    }

    private fun launchRouteOverview() {
        val routeDetails = routeDetailsTextView.text.toString()
        val routeJson = loadJsonFromAssets("routes.json")

        val intent = Intent(this, RouteOverviewActivity::class.java).apply {
            putExtra("route_details", routeDetails)
            putExtra("route_json", routeJson)
        }
        startActivity(intent)
    }

    override fun onMapReady(googleMap: GoogleMap) {
        map = googleMap
        parseRoutes(loadJsonFromAssets("routes.json"))
    }

    private fun loadJsonFromAssets(fileName: String): String {
        return try {
            assets.open(fileName).bufferedReader().use { it.readText() }
        } catch (e: Exception) {
            e.printStackTrace()
            showToast("Failed to load routes: ${e.message}")
            ""
        }
    }

    private fun parseRoutes(jsonString: String) {
        if (jsonString.isEmpty()) return

        try {
            val routesArray = JSONObject(jsonString).getJSONArray("routes")
            for (i in 0 until routesArray.length()) {
                val route = routesArray.getJSONObject(i)
                val userInput = route.getString("userInput")
                val transportMode = route.getString("transportMode")
                val path = parsePath(route.getJSONArray("path"))

                routeDetailsTextView.append("$userInput via $transportMode: $path\n")
                drawRouteOnMap(path)
            }
        } catch (e: Exception) {
            e.printStackTrace()
            showToast("Error parsing routes: ${e.message}")
        }
    }

    private fun parsePath(pathArray: JSONArray): List<LatLng> {
        return List(pathArray.length()) { i ->
            val point = pathArray.getJSONObject(i)
            LatLng(point.getDouble("lat"), point.getDouble("lng"))
        }
    }

    private fun drawRouteOnMap(path: List<LatLng>) {
        if (!::map.isInitialized) {
            showToast("Map not ready yet.")
            return
        }

        map.addPolyline(PolylineOptions().addAll(path).color(Color.BLUE).width(10f))
        if (path.isNotEmpty()) {
            map.addMarker(MarkerOptions().position(path.first()).title("Start"))
            map.addMarker(MarkerOptions().position(path.last()).title("End"))
            map.moveCamera(CameraUpdateFactory.newLatLngZoom(path.first(), 15f))
        }
    }

    private fun showTimePicker() {
        val c = Calendar.getInstance()
        val hour = c.get(Calendar.HOUR)
        val minute = c.get(Calendar.MINUTE)

        TimePickerDialog(this, { _, selectedHour, selectedMinute ->
            val formattedTime = formatTime(selectedHour, selectedMinute)
            travelTimeTextView.text = formattedTime
        }, hour, minute, false).show()
    }

    private fun formatTime(hour: Int, minute: Int): String {
        val amPm = if (hour < 12) "AM" else "PM"
        val formattedHour = if (hour > 12) hour - 12 else hour
        return String.format("%02d:%02d %s", formattedHour, minute, amPm)
    }

    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }
}
