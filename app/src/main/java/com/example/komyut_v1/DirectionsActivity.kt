package com.example.komyut_v1

import android.content.Intent
import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.google.android.material.card.MaterialCardView
import org.json.JSONArray
import org.json.JSONObject
import android.view.View
import android.widget.LinearLayout

class DirectionsActivity : AppCompatActivity() {

    private lateinit var startPointTextView: TextView
    private lateinit var endPointTextView: TextView
    private lateinit var transportModeTextView: TextView
    private lateinit var travelTimeTextView: TextView
    private lateinit var feedbackButton: Button
    private lateinit var newRouteButton: Button
    private lateinit var suggestedRoutesLayout: LinearLayout
    private lateinit var suggestedRoutesTitleTextView: TextView
    private lateinit var backButton: ImageButton
    private lateinit var homeButton: ImageButton


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_directions)

        // Initialize views
        startPointTextView = findViewById(R.id.tv_start_point)
        endPointTextView = findViewById(R.id.tv_end_point)
        transportModeTextView = findViewById(R.id.tv_transport_mode)
        travelTimeTextView = findViewById(R.id.tv_travel_time)
        feedbackButton = findViewById(R.id.btn_feedback)
        newRouteButton = findViewById(R.id.btn_new_route)
        suggestedRoutesLayout = findViewById(R.id.suggested_routes_layout)
        suggestedRoutesTitleTextView = findViewById(R.id.tv_suggested_routes_title)

        // Initialize navigation buttons
        backButton = findViewById(R.id.button_back)
        homeButton = findViewById(R.id.button_home)

        backButton.setOnClickListener { onBackPressed() } // Go back to the previous activity
        homeButton.setOnClickListener {

            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            finish() // Finish this activity if you want to remove it from the back stack
        }

        // Get the JSON string and user input from the Intent
        val routeJsonString = intent.getStringExtra("route_json")
        val startLocation = intent.getStringExtra("start_location") ?: "Unknown"
        val endLocation = intent.getStringExtra("end_location") ?: "Unknown"



        // Display start and end locations
        startPointTextView.text = getString(R.string.start_point, startLocation)
        endPointTextView.text = getString(R.string.end_point, endLocation)

        // Hide transport mode and travel time views
        transportModeTextView.visibility = View.GONE
        travelTimeTextView.visibility = View.GONE
        suggestedRoutesTitleTextView.visibility = View.VISIBLE

        if (routeJsonString != null) {
            parseRouteData(routeJsonString)
        } else {
            showToast("No route data available.")
        }

        feedbackButton.setOnClickListener { showFeedbackDialog() }
        newRouteButton.setOnClickListener { showNewRouteDialog() }
    }

    private fun parseRouteData(jsonString: String) {
        try {
            val jsonObject = JSONObject(jsonString)
            val routesArray = jsonObject.getJSONArray("routes")

            if (routesArray.length() > 0) {
                val route = routesArray.getJSONObject(0)  // Default to the first route

                // Set transport mode and travel time but keep them hidden
                transportModeTextView.text = getString(R.string.transport_mode, route.getString("transport_mode"))
                travelTimeTextView.text = getString(R.string.travel_time, route.getString("travel_time"))

                addSuggestedRoutes(routesArray)
            } else {
                showToast(getString(R.string.no_routes_found))
            }
        } catch (e: Exception) {
            e.printStackTrace()
            showToast(getString(R.string.error_parsing_route, e.message ?: "Unknown error"))
        }
    }

    // Create ImageView for the icon

    private fun addSuggestedRoutes(routesArray: JSONArray) {
        suggestedRoutesLayout.removeAllViews() // Clear previous views

        for (i in 0 until routesArray.length()) {
            val route = routesArray.getJSONObject(i) // Get the current route JSON object

            // Ensure required properties exist before accessing
            val routeName = route.optString("transport_mode", "Unknown mode")
            val travelTime = route.optString("travel_time", "Unknown time")
            val predictedFare = route.optString("predicted_fare", "Unknown fare")
            val iconName = route.optString("vehicle_icon", "default_icon") // Get the icon name from JSON

            // Prepare details string
            val routeDetails = "ETA: $travelTime, Fare: $predictedFare"

            // Create MaterialCardView
            val cardView = MaterialCardView(this).apply {
                layoutParams = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
                ).apply {
                    setMargins(0, 0, 0, 8) // Margin bottom
                }
                isClickable = true
                isFocusable = true
                foreground = ContextCompat.getDrawable(context, R.drawable.selectable_item_background)
                setCardBackgroundColor(ContextCompat.getColor(context, android.R.color.white))
                cardElevation = 4f
                radius = 8f
            }

            // Create inner LinearLayout for the entire content
            val innerLayout = LinearLayout(this).apply {
                orientation = LinearLayout.VERTICAL // Vertical layout for outer container
                setPadding(16, 16, 16, 16)
            }

            // Create horizontal layout for the icon and transport mode
            val horizontalLayout = LinearLayout(this).apply {
                orientation = LinearLayout.HORIZONTAL // Horizontal layout for icon and transport mode
                setPadding(0, 0, 0, 8) // Padding to separate from details
            }

            // Create ImageView for the icon
            val ivRouteIcon = ImageView(this).apply {
                // Use the correct variable to get the icon name from the JSON object
                val iconResId = resources.getIdentifier(iconName, "drawable", packageName)
                setImageResource(iconResId) // Set the icon image
                layoutParams = LinearLayout.LayoutParams(150, 150) //
                setPadding(0, 0, 16, 0)
            }

            // Create TextView for route name (transport mode)
            val tvRouteName = TextView(this).apply {
                text = routeName
                textSize = 18f
                setTextColor(ContextCompat.getColor(context, android.R.color.black))
                layoutParams = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    ivRouteIcon.layoutParams.height // Match the icon height
                )
                gravity = Gravity.CENTER_VERTICAL // Center vertically within the layout
            }

            // Add ImageView and TextView to horizontal layout
            horizontalLayout.addView(ivRouteIcon)
            horizontalLayout.addView(tvRouteName)

            // Create TextView for route details (ETA and Fare)
            val tvRouteDetails = TextView(this).apply {
                text = routeDetails
                textSize = 14f
                setTextColor(ContextCompat.getColor(context, android.R.color.darker_gray))
                setPadding(0, 4, 0, 0) // Padding top
            }

            // Add horizontal layout and details TextView to the inner layout
            innerLayout.addView(horizontalLayout) // Add horizontal layout first
            innerLayout.addView(tvRouteDetails) // Then add the details

            // Add inner layout to card view
            cardView.addView(innerLayout)

            // Add card view to suggested routes layout
            suggestedRoutesLayout.addView(cardView)

            // Set click listener for the card
            cardView.setOnClickListener {
                // Open TripOverviewActivity with detailed route info
                val intent = Intent(this@DirectionsActivity, TripOverviewActivity::class.java)
                intent.putExtra("route_data", route.toString())
                startActivity(intent)
            }
        }

        // If no routes were added, show a message
        if (routesArray.length() == 0) {
            showToast("No routes available.")
        }
    }


    private fun showFeedbackDialog() {
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Submit Feedback")

        val input = EditText(this)
        input.hint = "Enter your feedback"
        builder.setView(input)

        builder.setPositiveButton("Submit") { dialog, _ ->
            val feedback = input.text.toString()
            if (feedback.isNotEmpty()) {
                showToast("Feedback submitted: $feedback")
                dialog.dismiss()
            } else {
                showToast("Feedback cannot be empty.")
            }
        }

        builder.setNegativeButton("Cancel") { dialog, _ -> dialog.dismiss() }
        builder.show()
    }

    private fun showNewRouteDialog() {
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Report New Route")

        val layout = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(32, 16, 32, 16)
        }

        val routeNameInput = EditText(this).apply { hint = "Enter route name" }
        val startPointInput = EditText(this).apply { hint = "Enter start point" }
        val endPointInput = EditText(this).apply { hint = "Enter end point" }

        layout.addView(routeNameInput)
        layout.addView(startPointInput)
        layout.addView(endPointInput)
        builder.setView(layout)

        builder.setPositiveButton("Submit") { dialog, _ ->
            val routeName = routeNameInput.text.toString()
            val startPoint = startPointInput.text.toString()
            val endPoint = endPointInput.text.toString()

            if (routeName.isNotEmpty() && startPoint.isNotEmpty() && endPoint.isNotEmpty()) {
                showToast("New route reported: $routeName from $startPoint to $endPoint")
                dialog.dismiss()
            } else {
                showToast("All fields must be filled.")
            }
        }

        builder.setNegativeButton("Cancel") { dialog, _ -> dialog.dismiss() }
        builder.show()
    }

    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }
}
