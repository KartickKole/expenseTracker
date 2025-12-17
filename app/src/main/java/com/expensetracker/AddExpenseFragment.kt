package com.expensetracker

import android.os.Bundle
import android.view.*
import android.widget.*
import androidx.fragment.app.Fragment
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import java.text.SimpleDateFormat
import java.util.*

class AddExpenseFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {

        val view = inflater.inflate(R.layout.fragment_add_expense, container, false)

        val etTitle = view.findViewById<EditText>(R.id.etTitle)
        val etAmount = view.findViewById<EditText>(R.id.etAmount)
        val btnSave = view.findViewById<Button>(R.id.btnSave)

        btnSave.setOnClickListener {

            val title = etTitle.text.toString()
            val amount = etAmount.text.toString()

            if (title.isEmpty() || amount.isEmpty()) return@setOnClickListener

            val uid = FirebaseAuth.getInstance().currentUser!!.uid
            val ref = FirebaseDatabase.getInstance()
                .getReference("users")
                .child(uid)
                .child("expenses")

            val id = ref.push().key!!
            val date = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(Date())

            ref.child(id).setValue(
                ExpenseModel(id, title, amount, date)
            ).addOnSuccessListener {
                Toast.makeText(context, "Expense Added", Toast.LENGTH_SHORT).show()
                parentFragmentManager.popBackStack()
            }
        }
        return view
    }
}
