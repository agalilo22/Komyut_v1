package com.example.komyut_v1

import android.content.Intent
import android.os.Bundle
import android.text.InputType
import android.view.LayoutInflater
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.core.view.setPadding
import org.json.JSONArray
import org.json.JSONObject

class TripActivity : AppCompatActivity() {

    private lateinit var tripStepsLayout: LinearLayout
    private lateinit var reportIssueButton: Button
    private lateinit var backButton: ImageButton
    private lateinit var homeButton: ImageButton

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_trip)

        // Initialize views
        tripStepsLayout = findViewById(R.id.trip_steps_layout)
        reportIssueButton = findViewById(R.id.btn_report_issue)

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

        // Get route data from Intent
        val routeData = intent.getStringExtra("route_data")
        if (routeData != null) {
            displaySteps(JSONObject(routeData))
        } else {
            showToast("No trip details available.")
        }

        // Set Report Issue button listener and add icon
        reportIssueButton.apply {
            text = "Report an Issue"
            setOnClickListener { showReportIssueDialog() }
        }
    }

    private fun displaySteps(route: JSONObject) {
        val stepsArray = route.getJSONArray("steps")
        addStepsToLayout(stepsArray)
    }

    private fun addStepsToLayout(stepsArray: JSONArray) {
        for (i in 0 until stepsArray.length()) {
            val step = stepsArray.getJSONObject(i)
            val stepView = CardView(this).apply {
                layoutParams = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
                )
                setCardBackgroundColor(resources.getColor(android.R.color.white))
                radius = 12f
                cardElevation = 8f
                setPadding(16)

                // Horizontal LinearLayout to hold icon and instruction text
                val stepLayout = LinearLayout(this@TripActivity).apply {
                    orientation = LinearLayout.HORIZONTAL
                    setPadding(16)
                }

                // Add Icon beside the instruction text
                val icon = ImageView(this@TripActivity).apply {
                    val iconResId = getIconResource(step.getString("type"))
                    setImageResource(iconResId)
                    layoutParams = LinearLayout.LayoutParams(100, 100) // Larger icon size
                    setPadding(0, 0, 16, 0) // Padding to the right of the icon
                }

                // Instruction TextView
                val instructionTextView = TextView(this@TripActivity).apply {
                    text = step.getString("instruction")
                    textSize = 18f
                    setTextColor(resources.getColor(android.R.color.black))
                    setTypeface(null, android.graphics.Typeface.BOLD)
                }

                // Vertical layout to stack instruction and details
                val instructionLayout = LinearLayout(this@TripActivity).apply {
                    orientation = LinearLayout.VERTICAL
                }

                // Duration and Distance TextView below instruction
                val detailsTextView = TextView(this@TripActivity).apply {
                    text = "Duration: ${step.getString("duration")}, Distance: ${step.getString("distance")}"
                    textSize = 14f
                    setTextColor(resources.getColor(android.R.color.darker_gray))
                }

                // Add instruction and details to the vertical layout
                instructionLayout.addView(instructionTextView)
                instructionLayout.addView(detailsTextView)


                stepLayout.addView(icon)
                stepLayout.addView(instructionLayout)


                addView(stepLayout)
            }
            tripStepsLayout.addView(stepView)
        }
    }

    private fun getIconResource(type: String): Int {
        return when (type) {
            "walking" -> R.drawable.walking_icon
            "payment" -> R.drawable.payment_icon
            "arrival" -> R.drawable.move_icon
            "transport" -> R.drawable.bus_icon
            else -> R.drawable.warning
        }
    }

    private fun showReportIssueDialog() {
        val input = EditText(this).apply {
            hint = "Describe your issue"
            inputType = InputType.TYPE_CLASS_TEXT
            setPadding(16)
        }

        AlertDialog.Builder(this)
            .setTitle("Report an Issue")
            .setView(input)
            .setPositiveButton("Submit") { _, _ ->
                val issue = input.text.toString()
                if (issue.isNotBlank()) {
                    showToast("Issue reported: $issue")
                } else {
                    showToast("Issue description cannot be empty.")
                }
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }
}
