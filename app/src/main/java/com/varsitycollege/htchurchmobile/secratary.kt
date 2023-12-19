package com.varsitycollege.htchurchmobile

import android.content.ContentValues
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.github.clans.fab.FloatingActionButton
import com.github.clans.fab.FloatingActionMenu
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class secratary : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.secretary)
        supportActionBar?.hide()
IDload()
        navs()
        val loginBack = object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                val intent = Intent(this@secratary, Home::class.java)

                startActivity(intent)
                overridePendingTransition(0, 0)
                finish()


            }
        }
        onBackPressedDispatcher.addCallback(this, loginBack)
        var back = findViewById<ImageButton>(R.id.sec_back_btn)
        back.setOnClickListener()
        {
            val intent = Intent(this@secratary, Home::class.java)

            startActivity(intent)
            overridePendingTransition(0, 0)
            finish()
        }
        var addnew = findViewById<ImageButton>(R.id.sec_add_btn)
        addnew.setOnClickListener()
        {
            val intent = Intent(this, AddSec::class.java)

            startActivity(intent)
            overridePendingTransition(0, 0)
            finish()
            Log.d("clicked", "i am clicked")
        }
    }

    class DataClass {
        var data: String = ""
    }

    fun IDload() {


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
                    Log.d(ContentValues.TAG, "DocumentSnapshot data: ${document.data}")
                    val profiles = document.get("userDetails") as Map<String, Any>

                    val id = profiles["churchid"].toString()
                    val iddata = DataClass()
                    iddata.data = id
                    printDATA(iddata)


                } else {
                    Log.d(ContentValues.TAG, "No such document")
                }
            }
            .addOnFailureListener { exception ->
                Log.d(ContentValues.TAG, "get failed with ", exception)
            }


    }

    fun printDATA(data: DataClass) {
        val recyclerview = findViewById<RecyclerView>(R.id.sec_display)
        recyclerview.layoutManager = LinearLayoutManager(this)
        val db = FirebaseFirestore.getInstance()
        val user = FirebaseAuth.getInstance().currentUser

        val userEmail = user?.email

        val parts = userEmail!!.split('@', '.')
        val userID = parts[0] + parts[1]
        db.collection("churchs").document(data.data)
            .get()
            .addOnSuccessListener { document ->
                if (document != null) {
                    val secs = document.data?.get("secretary") as? Map<String, Map<String, String>>
                    if (secs != null) {
                        val seclist = secs.values.map {
                            Sec(
                                it["firstname"]!!,
                                it["surname"]!!,
                                it["email"]!!,
                                it["churchname"]!!,
                                it["churchid"]!!,
                                it["datestart"]!!
                            )
                        }
                        recyclerview.adapter = SecAdapter(seclist)
                        Log.d("Firestore", seclist.toString())
                    } else {
                        Log.d("Firestore", "No birdObservations in document")
                    }
                } else {
                    Log.d("Firestore", "No such document")
                }
            }
            .addOnFailureListener { exception ->
                Log.d("Firestore", "get failed with ", exception)
            }

    }
    fun navs()
    {
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

data class Sec(
    val firstname: String,
    val surname: String,
    val email: String,
    val worshipname: String,
    val id: String,
    val date: String
)

class SecAdapter(private val churchsecratary: List<Sec>) : RecyclerView.Adapter<SecAdapter.SecHolder>() {

    inner class SecHolder(view: View) : RecyclerView.ViewHolder(view) {
        val firstNames: TextView = view.findViewById(R.id.sec_name)
        val surnames: TextView = view.findViewById(R.id.sec_surname)
        val emails: TextView = view.findViewById(R.id.sec_email)
        val worhshipname: TextView = view.findViewById(R.id.sec_churchname)
        val ids: TextView = view.findViewById(R.id.secid)
        val dates: TextView = view.findViewById(R.id.sec_date)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SecHolder {
        val secLayout = LayoutInflater.from(parent.context).inflate(R.layout.sec_cv, parent, false)
        return SecHolder(secLayout)
    }

    override fun onBindViewHolder(secdatas: SecHolder, position: Int) {
        val secdata = churchsecratary[position]
        secdatas.firstNames.text = secdata.firstname
        secdatas.surnames.text = secdata.surname
        secdatas.emails.text = secdata.email
        secdatas.worhshipname.text = secdata.worshipname
        secdatas.ids.text = secdata.id
        secdatas.dates.text = secdata.date




        secdatas.itemView.setOnClickListener {
            val clickedData = churchsecratary[position]
            val context = secdatas.itemView.context
            val intent = Intent(context, EditSec::class.java)
            intent.putExtra("birdid", clickedData.email)



            context.startActivity(intent)

        }
    }

    override fun getItemCount() = churchsecratary.size
}
