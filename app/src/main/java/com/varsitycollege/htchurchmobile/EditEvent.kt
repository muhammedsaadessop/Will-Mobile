package com.varsitycollege.htchurchmobile

import android.content.ContentValues.TAG
import android.content.Intent

import android.os.Bundle
import android.util.Log
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.Spinner
import android.widget.TimePicker
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.firestore.FirebaseFirestore
import com.prolificinteractive.materialcalendarview.CalendarDay
import com.prolificinteractive.materialcalendarview.MaterialCalendarView


class EditEvent: AppCompatActivity() {
    private lateinit var churchSpinner: Spinner
    override fun onCreate(savedInstanceState: Bundle?) {
        supportActionBar?.hide()
        super.onCreate(savedInstanceState)
        setContentView(R.layout.edit_event)
        val calendarView = findViewById<MaterialCalendarView>(R.id.calendarView)
        val eventName = intent.getStringExtra("eventName")
        val eventDate: EditText = findViewById(R.id.event_date)
        val eventNameEditText = findViewById<EditText>(R.id.event_name)
        val eventDescriptionEditText = findViewById<EditText>(R.id.event_description)
        val eventAddressEditText = findViewById<EditText>(R.id.event_address)
        val startTimePicker = findViewById<TimePicker>(R.id.start_time_picker)
        val endTimePicker = findViewById<TimePicker>(R.id.end_time_picker)
        churchSpinner = findViewById(R.id.church_spinner)
        Log.d("EditEvent", "eventName: $eventName")
        val loginBack = object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                val intent = Intent(this@EditEvent, Home::class.java)
                startActivity(intent)
                overridePendingTransition(0, 0)
                finish()
            }
        }
        calendarView.setOnDateChangedListener { widget, date, selected ->
            val selectedDate = String.format("%04d-%02d-%02d", date.year, date.month, date.day)
            eventDate.setText(selectedDate)
            Log.d("Selected Date", selectedDate)
        }
        var back = findViewById<ImageButton>(R.id.back_btn)
        back.setOnClickListener()
        {
            val intent = Intent(this@EditEvent, Home::class.java)
            startActivity(intent)
            overridePendingTransition(0, 0)
            finish()
        }
        onBackPressedDispatcher.addCallback(this, loginBack)
        val finishButton: Button = findViewById(R.id.finish_btn)
        finishButton.setOnClickListener {
            // Retraive the values from the view
            val updatedName = eventNameEditText.text.toString()
            val updatedDate = eventDate.text.toString()
            val updatedDescription = eventDescriptionEditText.text.toString()
            val updatedAddress = eventAddressEditText.text.toString()
            val updatedStartTime = String.format("%02d:%02d", startTimePicker.hour, startTimePicker.minute)
            val updatedEndTime = String.format("%02d:%02d", endTimePicker.hour, endTimePicker.minute)
            val selectedChurch = churchSpinner.selectedItem.toString()
            //  missing fields
            var missingFields = ""
            if (updatedName.isEmpty()) missingFields += "Event Name, "
            if (updatedStartTime.isEmpty()) missingFields += "Start Time, "
            if (updatedEndTime.isEmpty()) missingFields += "End Time, "
            if (updatedDate.isEmpty()) missingFields += "Event Date, "
            if (updatedDescription.isEmpty()) missingFields += "Event Description, "
            if (updatedAddress.isEmpty()) missingFields += "Event Address, "
            if (selectedChurch.isEmpty()) missingFields += "Church, "
            if (missingFields.isNotEmpty()) {
                missingFields = missingFields.dropLast(2)
                Toast.makeText(this, "Please fill in the following fields: $missingFields", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            val updatedEvent = hashMapOf(
                "name" to updatedName,
                "date" to updatedDate,
                "description" to updatedDescription,
                "start_time" to updatedStartTime,
                "end_time" to updatedEndTime,
                "address" to updatedAddress,
                "church" to selectedChurch
            )
            val db = FirebaseFirestore.getInstance()
            // Update document in Firestore
            if (eventName != null) {
                db.collection("events")
                    .document(eventName)  // Use eventName as your document name
                    .update(updatedEvent as Map<String, Any>)
                    .addOnSuccessListener {
                        Log.d(TAG, "DocumentSnapshot successfully updated!")
                        Toast.makeText(this, "Event $updatedName details have been updated successfully.", Toast.LENGTH_SHORT).show()
                    }

                    .addOnFailureListener { e ->
                        Log.w(TAG, "Error updating document", e)
                    }
            }
        }
        val db = FirebaseFirestore.getInstance()
        db.collection("events")
            .whereEqualTo("name", eventName)
            .get()
            .addOnSuccessListener { documents ->
                for (document in documents) {
                    Log.d(TAG, "${document.id} => ${document.data}")
                    // Retrieve all the fields
                    val name = document.getString("name")
                    val date = document.getString("date")
                    val description = document.getString("description")
                    val startTime = document.getString("start_time")
                    val church = document.getString("church")
                    val endTime = document.getString("end_time")
                    val address = document.getString("address")
                    Log.d(TAG, "name: $name")
                    Log.d(TAG, "date: $date")
                    Log.d(TAG, "description: $description")
                    Log.d(TAG, "start_time: $startTime")
                    Log.d(TAG, "chruch: $church")
                    Log.d(TAG, "end_time: $endTime")
                    Log.d(TAG, "location: $address")
                    val dateParts = date?.split("-")?.map { it.toInt() }
                    val year = dateParts?.get(0) ?: 0
                    val month = dateParts?.get(1) ?: 1
                    val day = dateParts?.get(2) ?: 0
                    val calendarDay = CalendarDay.from(year, month, day)
                    Log.d(TAG, "DATE: $calendarDay")
                    // Set the selected date on the MaterialCalendarView
                    calendarView.setDateSelected(calendarDay, true)
                    // Populate the views
                    eventDate.setText(date)
                    eventNameEditText.setText(name)
                    eventDescriptionEditText.setText(description)
                    eventAddressEditText.setText(address)
                    // Parse the start and end times
                    val startTimeParts = startTime?.split(":")?.map { it.toInt() }
                    val endTimeParts = endTime?.split(":")?.map { it.toInt() }
                    if (startTimeParts != null && endTimeParts != null) {
                        val startHour = startTimeParts[0]
                        val startMinute = startTimeParts[1]
                        val endHour = endTimeParts[0]
                        val endMinute = endTimeParts[1]
                        startTimePicker.hour = startHour
                        startTimePicker.minute = startMinute
                        endTimePicker.hour = endHour
                        endTimePicker.minute = endMinute
                    }
                    if (church != null) {
                        populateSpinner(church)
                    }
                }
            }
            .addOnFailureListener { exception ->
                Log.d(TAG, "Error getting documents: ", exception) }
    }

    fun populateSpinner(church: String) {
        val db = FirebaseFirestore.getInstance()
        db.collection("spinnerchurchIds")
            .get()
            .addOnSuccessListener { result ->
                val churches = ArrayList<String>()
                for (document in result) {
                    val churchCenter = document.getString("Spinnerchurchcenter")
                    if (churchCenter != null) {
                        churches.add(churchCenter)
                    }
                }
                Log.d(TAG, "Churches: $churches")  // Log the churches list
                // Create an ArrayAdapter using the string array
                val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, churches)

                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                // Apply the adapter
                churchSpinner.adapter = adapter
                // Set the selected item in the spinner to be 'church'
                val selectedIndex = churches.indexOf(church)
                if (selectedIndex != -1) {
                    churchSpinner.setSelection(selectedIndex)
                }
            }
            .addOnFailureListener { exception ->
                Log.w(TAG, "Error getting documents.", exception)
            }
    }
}