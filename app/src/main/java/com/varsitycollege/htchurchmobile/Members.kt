package com.varsitycollege.htchurchmobile

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
import com.google.firebase.firestore.FirebaseFirestore

class Members: AppCompatActivity(){
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.members)
        supportActionBar?.hide()
        print()
        navs()
        val loginBack = object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                val intent = Intent(this@Members, Home::class.java)

                startActivity(intent)
                overridePendingTransition(0, 0)
                finish()


            }
        }
        onBackPressedDispatcher.addCallback(this, loginBack)
        var back = findViewById<ImageButton>(R.id.back_btn)
        back.setOnClickListener()
        {
            val intent = Intent(this@Members, Home::class.java)

            startActivity(intent)
            overridePendingTransition(0, 0)
            finish()
        }
    }
    fun print()
    {
        val recyclerView = findViewById<RecyclerView>(R.id.display_member)
        recyclerView.layoutManager = LinearLayoutManager(this)
        val db = FirebaseFirestore.getInstance()

        db.collection("members")
            .get()
            .addOnSuccessListener { result ->
                val pastors = result.mapNotNull { document ->
                    val memberDetails = document.get("memberDetails") as? HashMap<*, *>
                    if (memberDetails != null) {
                        Pastor(
                            firstname = memberDetails["firstname"] as? String ?: "",
                            surname = memberDetails["surname"] as? String ?: "",
                            email = memberDetails["email"] as? String ?: "",
                            churchname = memberDetails["worshipname"] as? String ?: "",
                        ).also {
                            Log.d("Firestore", "Fetched pastor: $it")
                        }
                    } else {
                        null
                    }
                }
                recyclerView.adapter = BirdAdapter(pastors)
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

            val intent = Intent(this, Events::class.java)

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

data class Pastor(
    val firstname: String,
    val surname: String,
    val email: String,
    val churchname: String,

)
class BirdAdapter(private val birds: List<Pastor>) : RecyclerView.Adapter<BirdAdapter.BirdViewHolder>() {

    inner class BirdViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val firstName: TextView = view.findViewById(R.id.member_name)
        val SurName: TextView = view.findViewById(R.id.member_surname)
        val email: TextView = view.findViewById(R.id.member_email)
        val churchname: TextView = view.findViewById(R.id.member_worship_name)

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BirdViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.members_cv, parent, false)
        return BirdViewHolder(view)
    }

    override fun onBindViewHolder(holder: BirdViewHolder, position: Int) {
        val bird = birds[position]

        holder.firstName.text = bird.firstname
        holder.SurName.text = bird.surname
        holder.email.text = bird.email
        holder.churchname.text = bird.churchname






    }

    override fun getItemCount() = birds.size
}
