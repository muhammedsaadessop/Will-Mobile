package com.varsitycollege.htchurchmobile

import android.content.ContentValues
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.TextView
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AppCompatActivity
import com.github.clans.fab.FloatingActionButton
import com.github.clans.fab.FloatingActionMenu
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class AddSec : AppCompatActivity() {
    private val e = Errors()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.add_secratary)
        supportActionBar?.hide()
        IDload()
        securGuard()
        navs()
        var save = findViewById<Button>(R.id.save_sec)
        save.setOnClickListener()
        {
            IDsave()
        }
        val loginBack = object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                val intent = Intent(this@AddSec, secratary::class.java)

                startActivity(intent)
                overridePendingTransition(0, 0)
                finish()


            }
        }
        onBackPressedDispatcher.addCallback(this, loginBack)
        var back = findViewById<ImageButton>(R.id.back_btn)
        back.setOnClickListener()
        {
            val intent = Intent(this, secratary::class.java)

            startActivity(intent)
            overridePendingTransition(0, 0)
            finish()
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

    class DataClass {
        var data: String = ""
    }

    fun IDload() {

        val churchid = findViewById<TextView>(R.id.sec_church_id)

        val user = FirebaseAuth.getInstance().currentUser

        val userEmail = user?.email


        val parts = userEmail!!.split('@', '.')
        val userID = parts[0] + parts[1]
        Log.d("userid", userID)
        val db = FirebaseFirestore.getInstance()
        val docRef = db.collection("pastors").document(userID)
        docRef.get()
            .addOnSuccessListener { document ->
                if (document != null) {
                    Log.d(ContentValues.TAG, "DocumentSnapshot data: ${document.data}")
                    val userDetails = document.get("userDetails") as Map<String, Any>

                    val id = userDetails["churchid"].toString()
                    val iddata = DataClass()
                    iddata.data = id
                    churchid.setText(id)


                } else {
                    Log.d(ContentValues.TAG, "No such document")
                }
            }
            .addOnFailureListener { exception ->
                Log.d(ContentValues.TAG, "get failed with ", exception)
            }


    }

    fun IDsave() {


        val user = FirebaseAuth.getInstance().currentUser

        val userEmail = user?.email


        val parts = userEmail!!.split('@', '.')
        val userID = parts[0] + parts[1]
        Log.d("userid", userID)
        val db = FirebaseFirestore.getInstance()
        val docRef = db.collection("pastors").document(userID)
        docRef.get()
            .addOnSuccessListener { document ->
                if (document != null) {
                    Log.d(ContentValues.TAG, "DocumentSnapshot data: ${document.data}")
                    val userDetails = document.get("userDetails") as Map<String, Any>

                    val id = userDetails["churchid"].toString()
                    val iddata = DataClass()
                    iddata.data = id

                    addtodb(iddata)


                } else {
                    Log.d(ContentValues.TAG, "No such document")
                }
            }
            .addOnFailureListener { exception ->
                Log.d(ContentValues.TAG, "get failed with ", exception)
            }


    }

    private fun addtodb(data: DataClass) {


        val firstname = findViewById<EditText>(R.id.sec_name)
        val surname = findViewById<EditText>(R.id.sec_surname)
        val email = findViewById<EditText>(R.id.sec_email)
        val church = findViewById<EditText>(R.id.sec_church_name)
        val churchid = findViewById<TextView>(R.id.sec_church_id)
        val dates = findViewById<EditText>(R.id.sec_date)
        val phone = findViewById<EditText>(R.id.sec_number)


        when {
            firstname.text.toString().isEmpty() -> {
                Snackbar.make(firstname, e.noFName, Snackbar.LENGTH_SHORT).show()

            }

            surname.text.toString().isEmpty() -> {
                Snackbar.make(surname, e.noSName, Snackbar.LENGTH_SHORT).show()

            }

            church.text.toString().isEmpty() -> {

                Snackbar.make(church, e.emptychurch, Snackbar.LENGTH_SHORT).show()

            }


            email.text.toString().isEmpty() -> {
                Snackbar.make(email, e.emailValidationEmptyError, Snackbar.LENGTH_SHORT).show()


            }

            dates.text.toString().isEmpty() -> {
                Snackbar.make(email, e.noStartDate, Snackbar.LENGTH_SHORT).show()


            }

            phone.text.toString().isEmpty() -> {
                Snackbar.make(email, e.nophonenumber, Snackbar.LENGTH_SHORT).show()


            }


            else -> {


                val parts = email.text.split('@', '.')
                val result = parts[0] + parts[1]

                val db = FirebaseFirestore.getInstance()
                Firebase.firestore


                val secdetails = hashMapOf(

                    "firstname" to firstname.text.toString(),
                    "surname" to surname.text.toString(),
                    "churchname" to church.text.toString(),
                    "churchid" to churchid.text.toString().lowercase(),
                    "email" to email.text.toString().lowercase(),
                    "datestart" to dates.text.toString(),
                    "phone" to phone.text.toString(),

                    )
                val secs = hashMapOf(
                    result.lowercase() to secdetails


                )
                db.collection("churchs").document(data.data.lowercase())
                    .set(hashMapOf("secretary" to secs), SetOptions.merge())
                    .addOnSuccessListener {

                        val intent = Intent(this@AddSec, secratary::class.java)

                        startActivity(intent)
                        overridePendingTransition(0, 0)
                        finish()
                        Snackbar.make(
                            surname,
                            "Added ${firstname.text.toString()}",
                            Snackbar.LENGTH_SHORT

                        ).show()
                    }
                    .addOnFailureListener { exception ->
                        Log.d(
                            "failedwith",
                            exception.toString()
                        )
                    }


            }

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

            val intent = Intent(this, Events::class.java)

            startActivity(intent)
            overridePendingTransition(0, 0)
            finish()
            menu.close(true)
        }
        val events: FloatingActionButton = findViewById(R.id.back_btns)
        events.setOnClickListener {

            val intent = Intent(this, secratary::class.java)

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

            val intent = Intent(this, Finances::class.java)
            startActivity(intent)
            overridePendingTransition(0, 0)
            finish()
            menu.close(true)
        }
    }


}