package com.varsitycollege.htchurchmobile

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.ContextMenu
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.widget.AdapterView
import android.widget.Button
import android.widget.EditText
import android.widget.FrameLayout
import android.widget.ImageButton
import android.widget.ListView
import android.widget.RelativeLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import com.github.clans.fab.FloatingActionButton
import com.github.clans.fab.FloatingActionMenu
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale


class Finances : AppCompatActivity() {
    private val TAG = "FinancesActivity"
    private val financeHistoryList = ArrayList<FinanceData>()
    private lateinit var financeHistoryAdapter: FinanceDataAdapter
    private var totalAmount: Double = 0.0
    private var totalTithes: Double = 0.0
    private var totalDonations: Double = 0.0
    private var totalFundRaised: Double = 0.0

    data class DataClass(var data: String)

    private var iddata: DataClass? = null

    private fun IDload(callback: () -> Unit) {
        val LoggedinProfile = FirebaseAuth.getInstance().currentUser
        val regEmail = LoggedinProfile?.email
        val parts = regEmail!!.split('@', '.')
        val userID = parts[0] + parts[1]
        Log.d("userid", userID)

        val db = FirebaseFirestore.getInstance()
        val docRef = db.collection("pastors").document(userID)
        docRef.get()
            .addOnSuccessListener { document ->
                if (document != null) {
                    Log.d(TAG, "DocumentSnapshot data: ${document.data}")
                    val profiles = document.get("userDetails") as Map<String, Any>
                    val id = profiles["churchid"].toString()
                    Log.d("churchid", id)
                    iddata = DataClass(id)
                    callback()
                } else {
                    Log.d(TAG, "No such document")
                }
            }
            .addOnFailureListener { exception ->
                Log.d(TAG, "get failed with ", exception)
            }
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.finances)
        supportActionBar?.hide()
        navs()
        securGuard()
        val currentUser = FirebaseAuth.getInstance().currentUser
        val userEmail = currentUser?.email
        IDload {
            Log.d(TAG, "Navigating to the finance page - User: $userEmail")

            financeHistoryAdapter =
                FinanceDataAdapter(this, R.layout.history_finance, financeHistoryList)
            val listView = findViewById<ListView>(R.id.finance_history)
            listView.adapter = financeHistoryAdapter
            registerForContextMenu(listView)

            val totalCardView = findViewById<CardView>(R.id.totalCardView)

            val totalAmountTextView = findViewById<TextView>(R.id.totalAmountTextView)

            fetchAndDisplayFinanceHistory()

        }
        val showTotalButton = findViewById<Button>(R.id.separateTotalButton)
        val totalCardLayout = findViewById<FrameLayout>(R.id.totalCardLayout)

        showTotalButton.setOnClickListener {
            if (totalCardLayout.visibility == View.VISIBLE) {
                totalCardLayout.visibility = View.GONE
            } else {
                totalCardLayout.visibility = View.VISIBLE
            }
        }
        val toggleAddFinanceButton = findViewById<ImageButton>(R.id.showAddFinanceButton)
        val addFinanceOverlay = findViewById<FrameLayout>(R.id.addFinanceOverlay)

        toggleAddFinanceButton.setOnClickListener {
            if (addFinanceOverlay.visibility == View.VISIBLE) {
                addFinanceOverlay.visibility = View.GONE
            } else {
                addFinanceOverlay.visibility = View.VISIBLE
            }
        }
        val backButton = findViewById<ImageButton>(R.id.back_btn)

        backButton.setOnClickListener {
            val intent = Intent(this, Home::class.java)

            startActivity(intent)

            finish()
        }


        val confirmButton = findViewById<Button>(R.id.confirmButtom)
        confirmButton.setOnClickListener {
            try {
                val typeOfExpense = findViewById<EditText>(R.id.typeOfExpense).text.toString()
                val tithesEditText = findViewById<EditText>(R.id.tighesInput)
                val donationsEditText = findViewById<EditText>(R.id.donationsInput)
                val donationSource = findViewById<EditText>(R.id.donationSourceInput)

                val fundRaiserEditText = findViewById<EditText>(R.id.fundInput)

                val tithesValue = tithesEditText.text.toString().toDoubleOrNull() ?: 0.0
                val donationsValue = donationsEditText.text.toString().toDoubleOrNull() ?: 0.0
                val fundRaiserValue = fundRaiserEditText.text.toString().toDoubleOrNull() ?: 0.0

                val currentTime = Timestamp.now()
                val sdf = SimpleDateFormat("yyyy-MM-dd")
                val currentDate = sdf.format(currentTime.toDate())

                totalAmount = tithesValue + donationsValue + fundRaiserValue

                if (iddata != null) {
                    val documentPath = "churchs/${iddata?.data}"

                    val newEntry = mapOf(
                        "expenseInput" to typeOfExpense,
                        "tithes" to tithesValue,
                        "donations" to donationsValue,
                        "donationSource" to donationSource.text.toString(),
                        "fundRaiser" to fundRaiserValue,
                        "confirmationTime" to currentTime,
                        "total" to totalAmount
                    )

                    FirebaseFirestore.getInstance().document(documentPath).get()
                        .addOnSuccessListener { document ->
                            if (document != null) {
                                val existingFinanceData = document.get("finance") as Map<*, *>?

                                val existingFinanceList: MutableList<Map<String, Any>> =
                                    if (existingFinanceData != null && existingFinanceData["entries"] is List<*>) {
                                        (existingFinanceData["entries"] as List<*>).filterIsInstance<Map<String, Any>>()
                                            .toMutableList()
                                    } else {
                                        mutableListOf()
                                    }

                                existingFinanceList.add(newEntry)

                                val updatedFinanceData = mapOf("entries" to existingFinanceList)

                                FirebaseFirestore.getInstance()
                                    .document(documentPath)
                                    .set(
                                        mapOf("finance" to updatedFinanceData),
                                        SetOptions.merge()
                                    )
                                    .addOnSuccessListener {
                                        Log.d(
                                            TAG,
                                            "Data saved successfully for church ID: ${iddata?.data}"
                                        )
                                        Toast.makeText(
                                            this@Finances,
                                            "Data saved successfully",
                                            Toast.LENGTH_SHORT
                                        )
                                            .show()
                                        val addFinanceOverlay =
                                            findViewById<FrameLayout>(R.id.addFinanceOverlay)
                                        addFinanceOverlay.visibility = View.GONE
                                        fetchAndDisplayFinanceHistory()
                                        findViewById<EditText>(R.id.typeOfExpense).text.clear()
                                        tithesEditText.text.clear()
                                        donationsEditText.text.clear()
                                        donationSource.text.clear()
                                        fundRaiserEditText.text.clear()

                                        val tithesTextView =
                                            findViewById<TextView>(R.id.tithesTextView)
                                        val donationsTextView =
                                            findViewById<TextView>(R.id.donationsTextView)

                                        val fundRaiserTextView =
                                            findViewById<TextView>(R.id.fundRaiserTextView)
                                        val totalAmountTextView =
                                            findViewById<TextView>(R.id.totalAmountTextView)




                                        calculateTotalAmount()
                                        updateOverallTotal()
                                        try {


                                            tithesTextView.text =
                                                String.format("R %.2f", tithesValue)
                                            donationsTextView.text =
                                                String.format("R %.2f", donationsValue)

                                            fundRaiserTextView.text =
                                                String.format("R %.2f", fundRaiserValue)
                                            totalAmountTextView.text =
                                                String.format("Total: R %.2f", totalAmount)
                                        }catch (E:Exception){}
                                    }
                                    .addOnFailureListener { e ->
                                        Log.e(TAG, "Error saving data: $e")
                                        Toast.makeText(
                                            this@Finances,
                                            "Error saving data: $e",
                                            Toast.LENGTH_SHORT
                                        )
                                            .show()
                                    }
                            } else {
                                val initialFinanceData = mapOf("entries" to listOf(newEntry))
                                FirebaseFirestore.getInstance()
                                    .document(documentPath)
                                    .update(mapOf("finance" to initialFinanceData))
                                    .addOnSuccessListener {
                                        Log.d(
                                            TAG,
                                            "Finance document created for church ID: ${iddata?.data}"
                                        )
                                        Toast.makeText(
                                            this@Finances,
                                            "Finance document created",
                                            Toast.LENGTH_SHORT
                                        ).show()
                                    }
                                    .addOnFailureListener { e ->
                                        Log.e(TAG, "Error creating finance document: $e")
                                        Toast.makeText(
                                            this@Finances,
                                            "Error creating finance document: $e",
                                            Toast.LENGTH_SHORT
                                        ).show()
                                    }
                            }
                        }
                        .addOnFailureListener { e ->
                            Log.e(TAG, "Error retrieving existing data: $e")
                            Toast.makeText(
                                this@Finances,
                                "Error retrieving existing data: $e",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                } else {
                    Toast.makeText(this@Finances, "Error getting church ID", Toast.LENGTH_SHORT)
                        .show()
                }
            }catch (e:Exception){}
        }
    }

    private fun calculateTotalAmount() {
        var totalAmount = 0.0
        for (entry in financeHistoryList) {
            totalAmount += entry.tithesValue + entry.donationsValue + entry.fundRaiserValue
        }
        this.totalAmount = totalAmount
    }

    fun showSeparateTotals(view: View) {
        Log.d(TAG, "showSeparateTotals clicked")
        val totalCardLayout = findViewById<FrameLayout>(R.id.totalCardLayout)

        if (totalCardLayout.visibility == View.VISIBLE) {
            Log.d(TAG, "totalCardLayout is visible, hiding it.")
            totalCardLayout.visibility = View.GONE
        } else {
            Log.d(TAG, "totalCardLayout is not visible, showing it.")
            totalCardLayout.visibility = View.VISIBLE

            val totalTithesTextView = findViewById<TextView>(R.id.totalTithesTextView)
            val totalDonationsTextView = findViewById<TextView>(R.id.totalDonationsTextView)
            val totalFundRaisedTextView = findViewById<TextView>(R.id.totalFundRaisedTextView)

            val totalTithes = financeHistoryList.sumOf { it.tithesValue }
            val totalDonations = financeHistoryList.sumOf { it.donationsValue }
            val totalFundRaised = financeHistoryList.sumOf { it.fundRaiserValue }
            val tithesTextView = findViewById<TextView>(R.id.tithesTextView)
            val donationsTextView = findViewById<TextView>(R.id.donationsTextView)
            val fundRaisedTextView = findViewById<TextView>(R.id.fundRaiserTextView)

            tithesTextView.text = String.format("Tithes: R %.2f", totalTithes)
            donationsTextView.text = String.format("Donations: R %.2f", totalDonations)
            fundRaisedTextView.text = String.format("Funds Raised: R %.2f", totalFundRaised)

            totalTithesTextView.text = String.format("Total Tithes: R %.2f", totalTithes)
            totalDonationsTextView.text = String.format("Total Donations: R %.2f", totalDonations)
            totalFundRaisedTextView.text = String.format("Total Fund Raised: R %.2f", totalFundRaised)

            Log.d(TAG, "Total Tithes: $totalTithes")
            Log.d(TAG, "Total Donations: $totalDonations")
            Log.d(TAG, "Total Fund Raised: $totalFundRaised")

        }
    }

    private fun showOverallTotal() {
        for (entry in financeHistoryList) {
            entry.calculateTotalAmount()
        }

        val overallTotalAmount = financeHistoryList.sumOf { it.totalAmount }

        val overallTotalTextView = findViewById<TextView>(R.id.overallTotalTextView)
        overallTotalTextView.text = String.format("Overall Total: R %.2f", overallTotalAmount)
    }

    private fun updateOverallTotal() {
        calculateTotalAmount()

        val overallTotalAmount = financeHistoryList.sumOf { it.totalAmount }

        val overallTotalTextView = findViewById<TextView>(R.id.overallTotalTextView)
        overallTotalTextView.text = String.format("Overall Total: R %.2f", overallTotalAmount)
    }

    private fun fetchAndDisplayFinanceHistory() {
        financeHistoryList.clear()


        if (iddata != null) {
            val documentPath = "churchs/${iddata?.data}"
            val docRef = FirebaseFirestore.getInstance().document(documentPath)

            docRef.get()
                .addOnSuccessListener { document ->
                    if (document != null) {
                        Log.d(TAG, "DocumentSnapshot data: ${document.data}")

                        val financeData = document.get("finance") as Map<String, Any>?
                        val entries = financeData?.get("entries") as List<Map<String, Any>>?
                        var totalTithes = 0.0
                        var totalDonations = 0.0
                        var totalFundRaised = 0.0
                        if (entries != null) {
                            for (entryMap in entries) {
                                val typeOfExpense = entryMap["expenseInput"] as? String
                                val tithesValue = entryMap["tithes"] as? Double
                                val donationsValue = entryMap["donations"] as? Double
                                val donationSource = entryMap["donationSource"] as? String ?: "No Donation Source"
                                val fundRaiserValue =
                                    entryMap["fundRaiser"] as? Double
                                val confirmationTime = entryMap["confirmationTime"] as Timestamp

                                if (typeOfExpense != null && confirmationTime != null) {
                                    val tithes = tithesValue ?: 0.0
                                    val donations =
                                        donationsValue ?: 0.0
                                    val fundRaiser =
                                        fundRaiserValue ?: 0.0

                                    val (month, date, time) = calculateMonthDateAndTime(
                                        confirmationTime
                                    )
                                    if (tithesValue != null) {
                                        totalTithes += tithesValue
                                    }
                                    if (donationsValue != null) {
                                        totalDonations += donationsValue
                                    }
                                    if (fundRaiserValue != null) {
                                        totalFundRaised += fundRaiserValue
                                    }
                                    val financeEntry = FinanceData(
                                        typeOfExpense,
                                        tithes,
                                        donations,
                                        donationSource,
                                        fundRaiser,
                                        confirmationTime,
                                        month.toDouble()
                                    )

                                    financeEntry.calculateTotalAmount()


                                    financeHistoryList.add(financeEntry)
                                }
                                else {
                                    Log.e(TAG, "Invalid data format: $entryMap")
                                }

                            }
                            financeHistoryAdapter.notifyDataSetChanged()
                            Log.d(TAG, "Finance data retrieved and displayed: $financeHistoryList")
                            calculateTotalAmount()
                            showOverallTotal()

                            val totalTithesTextView = findViewById<TextView>(R.id.totalTithesTextView)
                            val totalDonationsTextView = findViewById<TextView>(R.id.totalDonationsTextView)
                            val totalFundRaisedTextView = findViewById<TextView>(R.id.totalFundRaisedTextView)

                            totalTithesTextView.text = String.format("Total Tithes: R %.2f", totalTithes)
                            totalDonationsTextView.text = String.format("Total Donations: R %.2f", totalDonations)
                            totalFundRaisedTextView.text = String.format("Total Fund Raised: R %.2f", totalFundRaised)

                            fetchOverallTotal()

                        } else {
                            Log.d(TAG, "No finance entries found")
                        }

                    } else {
                        Log.d(TAG, "No such document")
                    }
                }
                .addOnFailureListener { exception ->
                    Log.e(TAG, "get failed with ", exception)
                }
        } else {
            Toast.makeText(this@Finances, "Error getting church ID", Toast.LENGTH_SHORT).show()
        }
    }
    private fun fetchOverallTotal() {
        val churchsCollection = FirebaseFirestore.getInstance().collection("churchs")

        churchsCollection.get().addOnSuccessListener { querySnapshot ->
            var overallTotalAmount = 0.0

            for (document in querySnapshot.documents) {
                val financeData = document.get("finance") as Map<*, *>?
                val entries = financeData?.get("entries") as List<Map<String, Any>>?

                entries?.let {
                    val churchTotal = it.sumOf { entry ->
                        val tithes = entry["tithes"] as? Double ?: 0.0
                        val donations = entry["donations"] as? Double ?: 0.0
                        val fundRaiser = entry["fundRaiser"] as? Double ?: 0.0
                        tithes + donations + fundRaiser
                    }

                    overallTotalAmount += churchTotal
                }
            }

            val overallChurchsTextView = findViewById<TextView>(R.id.overallChurchsTotalTextView)
            overallChurchsTextView.text = String.format("Overall Church Total: R %.2f", overallTotalAmount)
        }
            .addOnFailureListener { exception ->
                Log.e(TAG, "Error fetching church data: $exception")
                Toast.makeText(this@Finances, "Error fetching church data", Toast.LENGTH_SHORT).show()
            }
    }

    private fun calculateMonthDateAndTime(confirmationTime: Timestamp): Triple<Int, Int, String> {
        val calendar = Calendar.getInstance()
        calendar.time = confirmationTime.toDate()

        val month = calendar[Calendar.MONTH] + 1 // Adding 1 because months are 0-based
        val date = calendar[Calendar.DATE]
        val timeFormat = SimpleDateFormat("HH:mm", Locale.getDefault())
        val time = timeFormat.format(calendar.time)

        return Triple(month, date, time)
    }

    override fun onCreateContextMenu(
        menu: ContextMenu?,
        v: View?,
        menuInfo: ContextMenu.ContextMenuInfo?
    ) {
        super.onCreateContextMenu(menu, v, menuInfo)

        val listView = findViewById<ListView>(R.id.finance_history)

        if (v == listView) {
            menuInflater.inflate(R.menu.delete_item, menu)
        }
    }

    override fun onContextItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.delete_item) {
            val info = item.menuInfo as AdapterView.AdapterContextMenuInfo
            val position = info.position

            if (position >= 0 && position < financeHistoryList.size) {
                val selectedEntry = financeHistoryList[position]

                if (iddata != null) {
                    val documentPath = "churchs/${iddata?.data}"
                    val db = FirebaseFirestore.getInstance()

                    db.document(documentPath)
                        .get()
                        .addOnSuccessListener { documentSnapshot ->
                            if (documentSnapshot.exists()) {
                                val financeData =
                                    documentSnapshot.get("finance") as Map<String, Any>?
                                val entries = financeData?.get("entries") as List<Map<String, Any>>?

                                if (entries != null) {

                                    val uniqueIdentifier = selectedEntry.confirmationTime

                                    val updatedEntries = entries.filterNot { entry ->
                                        entry["confirmationTime"] == uniqueIdentifier
                                    }

                                    val updatedData =
                                        mapOf("finance" to mapOf("entries" to updatedEntries))
                                    db.document(documentPath)
                                        .update(updatedData)
                                        .addOnSuccessListener {
                                            Log.d(TAG, "Entry deleted from Firestore")

                                            fetchAndDisplayFinanceHistory()
                                        }
                                        .addOnFailureListener { e ->
                                            Log.e(TAG, "Error deleting entry from Firestore: $e")
                                        }
                                }
                            }
                        }
                }

                financeHistoryList.removeAt(position)
                financeHistoryAdapter.notifyDataSetChanged()

                updateOverallTotal()

                return true
            }
        }
        return super.onContextItemSelected(item)
    }
    fun closeAddFinanceOverlay(view: View) {
        val addFinanceOverlay = findViewById<FrameLayout>(R.id.addFinanceOverlay)
        addFinanceOverlay.visibility = View.GONE
    }


    data class FinanceData(
        val typeOfExpense: String,
        val tithesValue: Double,
        val donationsValue: Double,
        val donationSource: String,
        val fundRaiserValue: Double,
        val confirmationTime: Timestamp,
        var totalAmount: Double = 0.0
    ) {
        val month: Int
        val date: Int
        val time: String

        init {
            val calendar = Calendar.getInstance()
            calendar.time = confirmationTime.toDate()

            this.month = calendar[Calendar.MONTH] + 1
            this.date = calendar[Calendar.DATE]

            val timeFormat = SimpleDateFormat("HH:mm", Locale.getDefault())
            this.time = timeFormat.format(calendar.time)
        }

        companion object {
            fun fromMap(map: Map<String, Any>): FinanceData {
                return FinanceData(
                    map["expenseInput"] as String,
                    (map["tithes"] as Long).toDouble(),
                    (map["donations"] as Long).toDouble(),
                    map["donationSource"] as String,
                    (map["fundRaiser"] as Long).toDouble(),
                    map["confirmationTime"] as Timestamp
                )
            }
        }

        fun toMap(): Map<String, Any> {
            return mapOf(
                "expenseInput" to typeOfExpense,
                "tithes" to tithesValue,
                "donations" to donationsValue,
                "donationsSource" to donationSource,
                "fundRaiser" to fundRaiserValue,
                "confirmationTime" to confirmationTime
            )
        }

        fun calculateTotalAmount() {
            totalAmount = tithesValue + donationsValue + fundRaiserValue
        }


        override fun toString(): String {
            return "Type of Expense: $typeOfExpense\n" +
                    "Tithes: $tithesValue\n" +
                    "Donations: $donationsValue\n" +
                    "DonationsSource: $donationSource\n" +
                    "Fund Raiser: $fundRaiserValue\n" +
                    "Confirmation Time: $month/$date $time\n" +
                    "Total Amount: $totalAmount"
        }
    }
    fun navs() {
        val menu: FloatingActionMenu = findViewById(R.id.menu)
        val profiles: FloatingActionButton = findViewById(R.id.profile_button)
        profiles.setOnClickListener {
            val intent = Intent(this, Profile::class.java)

            startActivity(intent)
            overridePendingTransition(0, 0)
            finish()
            menu.close(true)
        }

        val members: FloatingActionButton = findViewById(R.id.member)
        members.setOnClickListener {

            val intent = Intent(this, Members::class.java)

            startActivity(intent)
            overridePendingTransition(0, 0)
            finish()

            menu.close(true)
        }

        val exit: FloatingActionButton = findViewById(R.id.sec)
        exit.setOnClickListener {

            val intent = Intent(this, secratary::class.java)

            startActivity(intent)
            overridePendingTransition(0, 0)
            finish()
            menu.close(true)
        }
        val events: FloatingActionButton = findViewById(R.id.back_btns)
        events.setOnClickListener {

            val intent = Intent(this, Home::class.java)

            startActivity(intent)
            overridePendingTransition(0, 0)
            finish()
            menu.close(true)
        }
        val pastors: FloatingActionButton = findViewById(R.id.pastor)
        pastors.setOnClickListener {

            val intent = Intent(this, ViewPastors::class.java)

            startActivity(intent)
            overridePendingTransition(0, 0)
            finish()
            menu.close(true)
        }
        val financials: FloatingActionButton = findViewById(R.id.finance)
        financials.setOnClickListener {

            val intent = Intent(this, Events::class.java)
            startActivity(intent)
            overridePendingTransition(0, 0)
            finish()
            menu.close(true)
        }
    }
    private fun securGuard() {
        // this checks user tokens
        // if invalid forces the user to login again
        // if deleted forces the user out of the app
        val tempusSecurity = FirebaseAuth.getInstance()
        tempusSecurity.addAuthStateListener { firebaseAuth ->
            when (firebaseAuth.currentUser) {
                null -> {
                    val intent = Intent(this, Login::class.java)

                    startActivity(intent)
                    overridePendingTransition(0, 0)
                    finish()

                }
            }
        }
    }

}