package com.varsitycollege.htchurchmobile

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.ImageButton
import android.widget.TextView
import java.text.SimpleDateFormat
import java.util.Locale

class FinanceDataAdapter(context: Context, resource: Int, objects: List<Finances.FinanceData>) :
    ArrayAdapter<Finances.FinanceData>(context, resource, objects) {

    // Inside FinanceDataAdapter class
    @SuppressLint("MissingInflatedId")
    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val financeData = getItem(position)

        val itemView = LayoutInflater.from(context).inflate(R.layout.history_finance, parent, false)

        val expenseTextView = itemView.findViewById<TextView>(R.id.typeOfExpenseTextView)
        val tithesTextView = itemView.findViewById<TextView>(R.id.tithesTextView)
        val donationsTextView = itemView.findViewById<TextView>(R.id.donationsTextView)
        val donationSourceTextView = itemView.findViewById<TextView>(R.id.donationSourceTextView)
        val fundRaiserTextView = itemView.findViewById<TextView>(R.id.fundRaiserTextView)
        val totalAmountTextView = itemView.findViewById<TextView>(R.id.totalAmountTextView)
        val confirmationTimeTextView = itemView.findViewById<TextView>(R.id.confirmationTimeTextView)

        val currencyFormat = context.getString(R.string.currency_format)

        expenseTextView.text = financeData?.typeOfExpense
        tithesTextView.text = String.format(currencyFormat, financeData?.tithesValue)
        donationsTextView.text = String.format(currencyFormat, financeData?.donationsValue)
        donationSourceTextView.text = financeData?.donationSource
        fundRaiserTextView.text = String.format(currencyFormat, financeData?.fundRaiserValue)

        val tithes = financeData?.tithesValue ?: 0.0
        val donations = financeData?.donationsValue ?: 0.0
        val fundRaiser = financeData?.fundRaiserValue ?: 0.0
        val totalAmount = tithes + donations + fundRaiser
        totalAmountTextView.text = String.format(currencyFormat, totalAmount)

        val confirmationTime = financeData?.confirmationTime
        val dateFormat = SimpleDateFormat("MMMM dd yyyy", Locale.getDefault())
        val formattedDate = dateFormat.format(confirmationTime?.toDate())
        confirmationTimeTextView.text = formattedDate

        return itemView
    }

}