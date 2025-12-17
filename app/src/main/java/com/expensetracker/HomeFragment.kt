package com.expensetracker

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.fragment.app.Fragment
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import java.text.SimpleDateFormat
import java.util.*

class HomeFragment : Fragment() {

    private lateinit var etTitle: EditText
    private lateinit var etAmount: EditText
    private lateinit var btnAdd: Button
    private lateinit var btnHistory: Button
    private lateinit var tvTotalExpense: TextView

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        val view = inflater.inflate(R.layout.fragment_home, container, false)

        etTitle = view.findViewById(R.id.etTitle)
        etAmount = view.findViewById(R.id.etAmount)
        btnAdd = view.findViewById(R.id.btnAddExpense)
        btnHistory = view.findViewById(R.id.btnViewHistory)
        tvTotalExpense = view.findViewById(R.id.tvTotalExpense)

        btnAdd.setOnClickListener { saveExpenseData() }

        btnHistory.setOnClickListener {
            startActivity(Intent(requireContext(), HistoryActivity::class.java))
        }

        loadTotalExpense()

        return view
    }

    private fun saveExpenseData() {

        val title = etTitle.text.toString().trim()
        val amount = etAmount.text.toString().trim()

        if (title.isEmpty() || amount.isEmpty()) {
            Toast.makeText(requireContext(), "Please enter details", Toast.LENGTH_SHORT).show()
            return
        }

        val uid = FirebaseAuth.getInstance().currentUser?.uid ?: return

        val database = FirebaseDatabase.getInstance()
            .getReference("users")
            .child(uid)
            .child("expenses")

        val expenseId = database.push().key ?: return
        val date = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(Date())

        val expense = ExpenseModel(expenseId, title, amount, date)

        database.child(expenseId).setValue(expense)
            .addOnSuccessListener {
                Toast.makeText(requireContext(), "Expense Added", Toast.LENGTH_SHORT).show()
                etTitle.text.clear()
                etAmount.text.clear()
                loadTotalExpense()
            }
            .addOnFailureListener {
                Toast.makeText(requireContext(), "Failed to add", Toast.LENGTH_SHORT).show()
            }
    }

    private fun loadTotalExpense() {

        val uid = FirebaseAuth.getInstance().currentUser?.uid ?: return

        val database = FirebaseDatabase.getInstance()
            .getReference("users")
            .child(uid)
            .child("expenses")

        database.get().addOnSuccessListener { snapshot ->
            var total = 0.0
            for (child in snapshot.children) {
                val amount = child.child("amount").value.toString().toDoubleOrNull() ?: 0.0
                total += amount
            }
            tvTotalExpense.text = "Total Expense: ₹$total"
        }
    }
}
