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

        // Set Report Issue button listener
        reportIssueButton.setOnClickListener {
            showReportIssueDialog()
        }
    }

    private fun displaySteps(route: JSONObject) {
        val stepsArray = route.getJSONArray("steps")
        segregateStepsByType(stepsArray)
    }

    private fun segregateStepsByType(stepsArray: JSONArray) {
        val walkingSteps = JSONArray()
        val transportSteps = JSONArray()

        for (i in 0 until stepsArray.length()) {
            val step = stepsArray.getJSONObject(i)
            if (step.getString("type") == "walking") {
                walkingSteps.put(step)
            } else {
                transportSteps.put(step)
            }
        }

        addExpandableSection("Walking Steps", walkingSteps)
        addExpandableSection("Transport Steps", transportSteps)
    }

    private fun addExpandableSection(title: String, steps: JSONArray) {
        val sectionLayout = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(16)
        }

        val titleTextView = TextView(this).apply {
            text = title
            textSize = 18f
            setPadding(8, 8, 8, 8)
            setTypeface(null, android.graphics.Typeface.BOLD) // Set bold style here
        }

        val toggleButton = Button(this).apply {
            text = "Show"
            setOnClickListener {
                sectionLayout.visibility = if (sectionLayout.visibility == LinearLayout.GONE) {
                    text = "Hide"
                    LinearLayout.VISIBLE
                } else {
                    text = "Show"
                    LinearLayout.GONE
                }
            }
        }

        tripStepsLayout.addView(titleTextView)
        tripStepsLayout.addView(toggleButton)
        tripStepsLayout.addView(sectionLayout)

        sectionLayout.visibility = LinearLayout.GONE

        for (i in 0 until steps.length()) {
            val stepView = CardView(this).apply {
                layoutParams = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
                )
                setCardBackgroundColor(resources.getColor(android.R.color.white))
                radius = 8f
                cardElevation = 4f

                val instructionTextView = TextView(this@TripActivity).apply {
                    text = steps.getJSONObject(i).getString("instruction")
                    setPadding(16, 16, 16, 16)
                    textSize = 16f
                    setTextColor(resources.getColor(android.R.color.black))
                }

                addView(instructionTextView)
            }
            sectionLayout.addView(stepView)
        }
    }

    private fun showReportIssueDialog() {
        val input = EditText(this).apply {
            hint = "Describe your issue"
            inputType = InputType.TYPE_CLASS_TEXT
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
