package com.expensetracker.ui.savings

import android.app.DatePickerDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.expensetracker.R
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class SetSavingGoalFragment : Fragment() {

    private lateinit var etTargetAmount: TextInputEditText
    private lateinit var tvTargetDate: TextView
    private lateinit var btnSaveGoal: Button

    private var selectedDate = ""

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        val view = inflater.inflate(R.layout.fragment_set_saving_goal, container, false)

        etTargetAmount = view.findViewById(R.id.etTargetAmount)
        tvTargetDate = view.findViewById(R.id.tvTargetDate)
        btnSaveGoal = view.findViewById(R.id.btnSaveGoal)

        tvTargetDate.setOnClickListener { openDatePicker() }
        btnSaveGoal.setOnClickListener { saveGoal() }

        return view
    }

    private fun openDatePicker() {
        val calendar = Calendar.getInstance()

        DatePickerDialog(
            requireContext(),
            { _, year, month, day ->
                calendar.set(year, month, day)
                val sdf = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
                selectedDate = sdf.format(calendar.time)
                tvTargetDate.text = selectedDate
            },
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        ).show()
    }

    private fun saveGoal() {

        val amountText = etTargetAmount.text.toString()

        if (amountText.isEmpty()) {
            toast("Enter target amount")
            return
        }

        if (selectedDate.isEmpty()) {
            toast("Select target date")
            return
        }

        val user = FirebaseAuth.getInstance().currentUser ?: return
        val uid = user.uid

        val goalData = mapOf(
            "targetAmount" to amountText.toDouble(),
            "targetDate" to selectedDate,
            "createdAt" to System.currentTimeMillis(),
            "achieved" to false
        )

        FirebaseDatabase.getInstance()
            .getReference("users")
            .child(uid)
            .child("savingGoal")
            .setValue(goalData)
            .addOnSuccessListener {
                toast("Saving goal saved")
                parentFragmentManager.popBackStack()
            }
            .addOnFailureListener {
                toast("Failed to save goal")
            }
    }

    private fun toast(msg: String) {
        Toast.makeText(requireContext(), msg, Toast.LENGTH_SHORT).show()
    }
}