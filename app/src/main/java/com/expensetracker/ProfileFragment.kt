package com.expensetracker

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.auth.FirebaseAuth

class ProfileFragment : Fragment() {

    private lateinit var tvEmail: TextView
    private lateinit var btnLogout: Button
    private lateinit var auth: FirebaseAuth

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_profile, container, false)

        auth = FirebaseAuth.getInstance()

        // ভিউ ফাইন্ডিং
        tvEmail = view.findViewById(R.id.tvUserEmail)
        val tvName = view.findViewById<TextView>(R.id.tvUserName) // নামের টেক্সটভিউ
        btnLogout = view.findViewById(R.id.btnLogout)
        val btnEditProfile = view.findViewById<TextView>(R.id.btnEditProfile) // এডিট বাটন

        // ডাটা দেখানোর ফাংশন কল করা
        updateUI(tvName)

        // এডিট প্রোফাইল পেজে যাওয়ার লিসেনার
        btnEditProfile.setOnClickListener {
            val intent = Intent(activity, EditProfileActivity::class.java)
            startActivity(intent)
        }

        // লগআউট বাটন (Firebase + Google Logout)
        btnLogout.setOnClickListener {

            // ১. Firebase থেকে সাইন আউট
            auth.signOut()

            // ২. Google Client থেকেও সাইন আউট (যাতে পরের বার আবার মেইল সিলেক্ট করতে দেয়)
            val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).build()
            val googleSignInClient = GoogleSignIn.getClient(requireActivity(), gso)

            googleSignInClient.signOut().addOnCompleteListener {
                // ৩. পুরোপুরি লগআউট হওয়ার পর লগইন পেজে পাঠানো
                val intent = Intent(activity, LoginActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                startActivity(intent)
            }
        }

        return view
    }

    // নাম এবং ইমেইল আপডেট করার ফাংশন
    private fun updateUI(tvName: TextView) {
        val user = auth.currentUser
        if (user != null) {
            tvEmail.text = user.email
            // নাম না থাকলে ডিফল্ট দেখাবে, থাকলে নাম দেখাবে
            tvName.text = if (user.displayName.isNullOrEmpty()) "No Name Set" else user.displayName
        }
    }

    // এডিট পেজ থেকে ফিরে আসলে নাম রিফ্রেশ করার জন্য
    override fun onResume() {
        super.onResume()
        val tvName = view?.findViewById<TextView>(R.id.tvUserName)
        if (tvName != null) {
            auth.currentUser?.reload()?.addOnCompleteListener {
                updateUI(tvName) // ডাটা রিফ্রেশ
            }
        }
    }
}