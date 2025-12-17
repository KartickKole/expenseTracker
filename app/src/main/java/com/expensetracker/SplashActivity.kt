package com.expensetracker

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth

class SplashActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        supportActionBar?.hide()

        Handler(Looper.getMainLooper()).postDelayed({

            val auth = FirebaseAuth.getInstance()
            val user = auth.currentUser

            val intent = if (user != null) {
                // Logged in → Main
                Intent(this, MainActivity::class.java)
            } else {
                // Not logged in → Login
                Intent(this, LoginActivity::class.java)
            }

            // 🔥 IMPORTANT FLAGS (THIS FIXES THE BACK ISSUE)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK

            startActivity(intent)
            finish()

        }, 1200) // 1.2 sec smooth splash
    }
}
