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

class ViewPastors:AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.pastors)
        supportActionBar?.hide()
        print()
        navs()
        val loginBack = object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                val intent = Intent(this@ViewPastors, Home::class.java)

                startActivity(intent)
                overridePendingTransition(0, 0)
                finish()


            }
        }
        var back = findViewById<ImageButton>(R.id.back_btn)
        back.setOnClickListener()
        {
            val intent = Intent(this@ViewPastors, Home::class.java)

            startActivity(intent)
            overridePendingTransition(0, 0)
            finish()
        }
        onBackPressedDispatcher.addCallback(this, loginBack)
    }
    fun print()
    {
        val pastordisplay = findViewById<RecyclerView>(R.id.pastor_display)
        pastordisplay.layoutManager = LinearLayoutManager(this)
        val db = FirebaseFirestore.getInstance()

        db.collection("pastors")
            .get()
            .addOnSuccessListener { result ->
                val pastors = result.mapNotNull { document ->
                    val PastorProfiles = document.get("userDetails") as? HashMap<*, *>
                    if (PastorProfiles != null) {
                        Pastordata(
                            firstname = PastorProfiles["firstname"] as? String ?: "",
                            surname = PastorProfiles["surname"] as? String ?: "",
                            email = PastorProfiles["email"] as? String ?: "",
                            churchid = PastorProfiles["churchid"] as? String ?: "",
                        ).also {
                            Log.d("Firestore", "Fetched pastor: $it")
                        }
                    } else {
                        null
                    }
                }
                pastordisplay.adapter = pastoradaptor(pastors)
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

            val intent = Intent(this, Events::class.java)

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

data class Pastordata(
    val firstname: String,
    val surname: String,
    val email: String,
    val churchid: String,

    )
class pastoradaptor(private val pdata: List<Pastordata>) : RecyclerView.Adapter<pastoradaptor.PastorHolder>() {

    inner class PastorHolder(view: View) : RecyclerView.ViewHolder(view) {
        val firstName: TextView = view.findViewById(R.id.pastor_name)
        val SurName: TextView = view.findViewById(R.id.pastor_surname)
        val email: TextView = view.findViewById(R.id.pastor_email)
        val churchid: TextView = view.findViewById(R.id.pastor_churchname)

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PastorHolder {
        val PastorLayout = LayoutInflater.from(parent.context).inflate(R.layout.pastor_cv, parent, false)
        return PastorHolder(PastorLayout)
    }

    override fun onBindViewHolder(pdisplay: PastorHolder, position: Int) {
        val psdata = pdata[position]

        pdisplay.firstName.text = psdata.firstname
        pdisplay.SurName.text = psdata.surname
        pdisplay.email.text = psdata.email
        pdisplay.churchid.text = psdata.churchid






    }

    override fun getItemCount() = pdata.size
}
