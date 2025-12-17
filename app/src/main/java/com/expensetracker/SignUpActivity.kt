package com.expensetracker

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth

class SignUpActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_up)

        auth = FirebaseAuth.getInstance()

        val etEmail = findViewById<EditText>(R.id.etEmail)
        val etPassword = findViewById<EditText>(R.id.etPassword)
        val btnSignUp = findViewById<Button>(R.id.btnSignUp)
        val tvLoginRedirect = findViewById<TextView>(R.id.tvLoginRedirect)
        val progress = findViewById<ProgressBar>(R.id.signupProgress)

        btnSignUp.setOnClickListener {

            val email = etEmail.text.toString().trim()
            val pass = etPassword.text.toString().trim()

            // 🔍 Validation
            if (email.isEmpty() || pass.isEmpty()) {
                Toast.makeText(this, "Fill all fields", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (pass.length < 6) {
                Toast.makeText(this, "Password must be at least 6 characters", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // 🔥 Show loading
            progress.visibility = View.VISIBLE
            btnSignUp.isEnabled = false

            // 🔐 Firebase Signup
            auth.createUserWithEmailAndPassword(email, pass)
                .addOnCompleteListener { task ->

                    // 🔥 Hide loading
                    progress.visibility = View.GONE
                    btnSignUp.isEnabled = true

                    if (task.isSuccessful) {
                        Toast.makeText(this, "Sign Up Successful!", Toast.LENGTH_SHORT).show()

                        // Optional: directly go to MainActivity
                        startActivity(Intent(this, LoginActivity::class.java))
                        finish()
                    } else {
                        Toast.makeText(
                            this,
                            task.exception?.message ?: "Sign Up Failed",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
        }

        tvLoginRedirect.setOnClickListener {
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
        }
    }
}
