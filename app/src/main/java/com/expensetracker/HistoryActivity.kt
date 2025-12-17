package com.expensetracker

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.database.*

class HistoryActivity : AppCompatActivity() {

    private lateinit var rvHistory: RecyclerView
    private lateinit var expenseList: ArrayList<ExpenseModel>
    private lateinit var expenseAdapter: ExpenseAdapter
    private lateinit var database: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_history)

        rvHistory = findViewById(R.id.rvHistory)
        rvHistory.layoutManager = LinearLayoutManager(this)
        rvHistory.setHasFixedSize(true)

        expenseList = ArrayList()
        expenseAdapter = ExpenseAdapter(expenseList)
        rvHistory.adapter = expenseAdapter

        database = FirebaseDatabase.getInstance().getReference("Expenses")

        getExpenseData()
    }

    private fun getExpenseData() {
        database.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                expenseList.clear()
                if (snapshot.exists()) {
                    for (expenseSnapshot in snapshot.children) {
                        val expense = expenseSnapshot.getValue(ExpenseModel::class.java)
                        expenseList.add(expense!!)
                    }
                    expenseList.reverse() // নতুন ডাটা সবার উপরে দেখাবে
                    expenseAdapter.notifyDataSetChanged()
                } else {
                    Toast.makeText(this@HistoryActivity, "No data found", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(this@HistoryActivity, "Error: ${error.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }
}