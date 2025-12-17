package com.expensetracker.ui.profile

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.expensetracker.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.UserProfileChangeRequest

class EditProfileActivity : AppCompatActivity() {

    private lateinit var etName: EditText
    private lateinit var etPhone: EditText
    private lateinit var btnSave: Button
    private lateinit var btnClose: Button
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_profile)

        auth = FirebaseAuth.getInstance()
        val user = auth.currentUser

        etName = findViewById(R.id.etEditName)
        etPhone = findViewById(R.id.etEditPhone)
        btnSave = findViewById(R.id.btnSaveProfile)
        btnClose = findViewById(R.id.btnClose)

        // আগে থেকে সেট করা নাম থাকলে তা দেখানো
        if (user != null) {
            etName.setText(user.displayName)
        }

        // Save বাটনে ক্লিক করলে
        btnSave.setOnClickListener {
            val newName = etName.text.toString().trim()

            if (newName.isEmpty()) {
                etName.error = "Name required"
                return@setOnClickListener
            }

            // Firebase এ নাম আপডেট করা
            val profileUpdates = UserProfileChangeRequest.Builder()
                .setDisplayName(newName)
                .build()

            user?.updateProfile(profileUpdates)
                ?.addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        Toast.makeText(this, "Profile Updated!", Toast.LENGTH_SHORT).show()
                        finish() // কাজ শেষে পেজ বন্ধ হয়ে যাবে
                    } else {
                        Toast.makeText(this, "Error: ${task.exception?.message}", Toast.LENGTH_SHORT).show()
                    }
                }
        }

        btnClose.setOnClickListener {
            finish()
        }
    }
}