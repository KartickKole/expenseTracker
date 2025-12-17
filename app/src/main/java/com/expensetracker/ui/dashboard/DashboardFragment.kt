package com.expensetracker.ui.dashboard

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.expensetracker.ui.addexpense.AddExpenseFragment
import com.expensetracker.ui.addincome.AddIncomeFragment
import com.expensetracker.ui.history.HistoryFragment
import com.expensetracker.R
import com.expensetracker.ui.savings.SetSavingGoalFragment
import com.github.mikephil.charting.charts.PieChart
import com.github.mikephil.charting.data.PieData
import com.github.mikephil.charting.data.PieDataSet
import com.github.mikephil.charting.data.PieEntry
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class DashboardFragment : Fragment() {

    private lateinit var tvBalance: TextView
    private lateinit var tvTodayExpense: TextView
    private lateinit var tvMonthExpense: TextView
    private lateinit var pieChart: PieChart
    private lateinit var tvBudgetPercent: TextView

    private val MONTHLY_BUDGET = 5000.0   // change later from Firebase

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        val view = inflater.inflate(R.layout.fragment_dashboard, container, false)

        tvBalance = view.findViewById(R.id.tvBalance)
        tvTodayExpense = view.findViewById(R.id.tvTodayExpense)
        tvMonthExpense = view.findViewById(R.id.tvMonthExpense)
        pieChart = view.findViewById(R.id.pieChart)
        tvBudgetPercent = view.findViewById(R.id.tvBudgetPercent)

        view.findViewById<Button>(R.id.btnAddExpense).setOnClickListener {
            parentFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, AddExpenseFragment())
                .addToBackStack(null)
                .commit()
        }

        view.findViewById<Button>(R.id.btnAddIncome).setOnClickListener {
            parentFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, AddIncomeFragment())
                .addToBackStack(null)
                .commit()
        }

        view.findViewById<Button>(R.id.btnSetSavingGoal).setOnClickListener {
            parentFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, SetSavingGoalFragment())
                .addToBackStack(null)
                .commit()
        }

        view.findViewById<Button>(R.id.btnHistory).setOnClickListener {
            parentFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, HistoryFragment())
                .addToBackStack(null)
                .commit()
        }
        loadDashboardData()
        return view
    }

    private fun loadDashboardData() {

        val user = FirebaseAuth.getInstance().currentUser ?: return
        val uid = user.uid

        val db = FirebaseDatabase.getInstance()
            .getReference("users")
            .child(uid)

        val today = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(Date())
        val month = SimpleDateFormat("MM/yyyy", Locale.getDefault()).format(Date())

        var todayExpense = 0.0
        var monthExpense = 0.0
        var totalExpense = 0.0
        var totalIncome = 0.0

        db.child("expenses").get().addOnSuccessListener { snap ->
            snap.children.forEach {
                val amt = it.child("amount").value?.toString()?.toDoubleOrNull() ?: 0.0
                val date = it.child("date").value?.toString() ?: ""

                totalExpense += amt
                if (date == today) todayExpense += amt
                if (date.contains(month)) monthExpense += amt
            }

            tvTodayExpense.text = "₹$todayExpense"
            tvMonthExpense.text = "₹$monthExpense"

            db.child("income").get().addOnSuccessListener { inc ->
                inc.children.forEach {
                    totalIncome += it.child("amount").value?.toString()?.toDoubleOrNull() ?: 0.0
                }

                tvBalance.text = "₹${totalIncome - totalExpense}"
                setupPieChart(monthExpense)
            }
        }
    }

    private fun setupPieChart(monthExpense: Double) {

        val percent = ((monthExpense / MONTHLY_BUDGET) * 100).coerceAtMost(100.0)

        val entries = arrayListOf(
            PieEntry(percent.toFloat(), "Used"),
            PieEntry((100 - percent).toFloat(), "")
        )

        val dataSet = PieDataSet(entries, "")
        dataSet.setDrawValues(false)
        dataSet.colors = listOf(
            Color.parseColor("#FFA726"),
            Color.parseColor("#EEEEEE")
        )

        val data = PieData(dataSet)
        pieChart.data = data

        pieChart.isDrawHoleEnabled = true
        pieChart.holeRadius = 75f
        pieChart.setHoleColor(Color.TRANSPARENT)
        pieChart.legend.isEnabled = false
        pieChart.description.isEnabled = false
        pieChart.setTouchEnabled(false)

        pieChart.centerText = "₹${monthExpense.toInt()}\nThis Month"
        pieChart.setCenterTextSize(16f)

        pieChart.invalidate()

        tvBudgetPercent.text = "${percent.toInt()}% of monthly budget"
    }
}