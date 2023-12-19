package com.varsitycollege.htchurchmobile

import android.content.ContentValues
import android.content.ContentValues.TAG
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
import de.keyboardsurfer.android.widget.crouton.Crouton
import de.keyboardsurfer.android.widget.crouton.Style

class ChurchDetails : AppCompatActivity() {
    private val e = Errors()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.church_details)
        supportActionBar?.hide()
        securGuard()
        IDload()
        navs()

        val loginBack = object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                val intent = Intent(this@ChurchDetails, Home::class.java)

                startActivity(intent)
                overridePendingTransition(0, 0)
                finish()


            }

        }
        onBackPressedDispatcher.addCallback(this, loginBack)
        var back = findViewById<ImageButton>(R.id.back_btn)
        back.setOnClickListener()
        {
            val intent = Intent(this@ChurchDetails, Home::class.java)

            startActivity(intent)
            overridePendingTransition(0, 0)
            finish()
        }
        var savedata = findViewById<Button>(R.id.sec_confirm_btn)
        savedata.setOnClickListener()
        {
            editdata()
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

        val churchid: TextView = findViewById(R.id.church_id)
        val churchname: TextView = findViewById(R.id.church_Name)

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
                    val name = userDetails["church"].toString()
                    churchname.setText(name)
                    val iddata = DataClass()
                    iddata.data = id

                    churchid.setText(id)
                    Dataload(iddata)

                } else {
                    Log.d(ContentValues.TAG, "No such document")
                }
            }
            .addOnFailureListener { exception ->
                Log.d(ContentValues.TAG, "get failed with ", exception)
            }


    }

    fun Dataload(data: DataClass) {


        val churchname: TextView = findViewById(R.id.church_Name)

        val place: EditText = findViewById(R.id.location)

        val memberno: EditText = findViewById(R.id.member_number)

        val pastorno: EditText = findViewById(R.id.pastor_number)

        val user = FirebaseAuth.getInstance().currentUser

        val userEmail = user?.email


        val parts = userEmail!!.split('@', '.')
        val userID = parts[0] + parts[1]
        Log.d("userid", userID)
        val db = FirebaseFirestore.getInstance()
        val docRef = db.collection("churchs").document(data.data.toString())
        docRef.get()
            .addOnSuccessListener { document ->
                if (document != null) {
                    Log.d(ContentValues.TAG, "DocumentSnapshot data: ${document.data}")
                    try {


                        val userDetails = document.get("churchDetails") as Map<String, Any>

                        val mno = userDetails["members"].toString()
                        val pno = userDetails["pastors"].toString()

                        val location = userDetails["location"].toString()
                        memberno.setText(mno)
                        pastorno.setText(pno)

                        place.setText(location)
                    } catch(e:Exception)
                    {

                    }
                } else {
                    Log.d(ContentValues.TAG, "No such document")
                }
            }
            .addOnFailureListener { exception ->
                Log.d(ContentValues.TAG, "get failed with ", exception)
            }


    }

    fun editdata() {
        val namefield: TextView= findViewById(R.id.church_Name)
        val place: EditText = findViewById(R.id.location)

        val memberno: EditText = findViewById(R.id.member_number)
        val churchid: TextView = findViewById(R.id.church_id)
        val pastorno: EditText = findViewById(R.id.pastor_number)


        val user = FirebaseAuth.getInstance().currentUser

        val userEmail = user?.email


        val parts = userEmail!!.split('@', '.')
        val userID = parts[0] + parts[1]
        Log.d("userid", userID)
        val db = FirebaseFirestore.getInstance()

        val name = namefield.text.toString()
        val location = place.text.toString().replace("\\s".toRegex(), "")
        val members = memberno.text.toString().replace("\\s".toRegex(), "")
        val pastors = pastorno.text.toString().replace("\\s".toRegex(), "")


        val id = churchid.text.toString().replace("\\s".toRegex(), "")


        val users = churchdetail(
            id, name, members, pastors, location
        )
        val docRef = db.collection("churchs").document(id)
        docRef.set(
            mapOf("churchDetails" to users), SetOptions.merge()
        ).addOnSuccessListener {

            Snackbar.make(
                namefield,
                "Updated Church Details for $name",
                Snackbar.LENGTH_SHORT

            ).show()
        }.addOnFailureListener { e ->
            Log.w(TAG, "Error writing document", e)
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

            val intent = Intent(this, Finances::class.java)
            startActivity(intent)
            overridePendingTransition(0, 0)
            finish()
            menu.close(true)
        }
    }
}