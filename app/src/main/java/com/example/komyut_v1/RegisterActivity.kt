package com.example.komyut_v1

import android.content.Intent
import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class RegisterActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register) // Ensure this matches your layout name

        // Find the "Already have an account?" TextView
        val tvLogin: TextView = findViewById(R.id.tv_login)

        // Set click listener to navigate to LoginActivity
        tvLogin.setOnClickListener {
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
        }
    }
}
