package com.example.komyut_v1

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class LoginActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login) // Assuming activity_login.xml is your layout

        val etEmail: EditText = findViewById(R.id.et_email)
        val etPassword: EditText = findViewById(R.id.et_password)
        val btnLogin: Button = findViewById(R.id.btn_login)
        val tvRegister: TextView = findViewById(R.id.tv_register)

        // Login Button Logic
        btnLogin.setOnClickListener {
            val email = etEmail.text.toString()
            val password = etPassword.text.toString()

            // Simple login validation logic
            if (email.isNotEmpty() && password.isNotEmpty()) {
                // Mock login success
                if (isLoginSuccessful(email, password)) {
                    Toast.makeText(this, "Login Successful", Toast.LENGTH_SHORT).show()

                    // Start the next activity (MainActivity)
                    val intent = Intent(this, HomepageActivity::class.java)
                    startActivity(intent)
                    // Remove the finish() call to keep LoginActivity alive
                } else {
                    Toast.makeText(this, "Invalid email or password", Toast.LENGTH_SHORT).show()
                }
            } else {
                Toast.makeText(this, "Please enter email and password", Toast.LENGTH_SHORT).show()
            }
        }


        tvRegister.setOnClickListener {
            val intent = Intent(this, RegisterActivity::class.java)
            startActivity(intent)
        }
    }

    // Mock function to simulate login success
    private fun isLoginSuccessful(email: String, password: String): Boolean {
        return email == "test@example.com" && password == "password123"
    }
}
