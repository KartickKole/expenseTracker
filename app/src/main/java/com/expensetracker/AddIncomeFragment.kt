package com.expensetracker

import android.os.Bundle
import android.view.*
import android.widget.*
import androidx.fragment.app.Fragment
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase

class AddIncomeFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {

        val view = inflater.inflate(R.layout.fragment_add_income, container, false)

        val etAmount = view.findViewById<EditText>(R.id.etAmount)
        val btnSave = view.findViewById<Button>(R.id.btnSaveIncome)

        btnSave.setOnClickListener {

            val amount = etAmount.text.toString()
            if (amount.isEmpty()) return@setOnClickListener

            val uid = FirebaseAuth.getInstance().currentUser!!.uid
            val ref = FirebaseDatabase.getInstance()
                .getReference("users")
                .child(uid)
                .child("income")

            val id = ref.push().key!!
            ref.child(id).child("amount").setValue(amount)

            Toast.makeText(context, "Income Added", Toast.LENGTH_SHORT).show()
            parentFragmentManager.popBackStack()
        }
        return view
    }
}
