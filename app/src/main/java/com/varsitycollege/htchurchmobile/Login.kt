package com.varsitycollege.htchurchmobile

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
import de.keyboardsurfer.android.widget.crouton.Crouton
import de.keyboardsurfer.android.widget.crouton.Style

class Login : AppCompatActivity() {
    private val e = Errors()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.login)
        supportActionBar?.hide()
        check()
        autologin()
        permissions()
        var forgot = findViewById<TextView>(R.id.forgotpassword)
        forgot.setOnClickListener()
        {
            forgotpassword()
        }

    }
    private fun autologin() {

        val auth = FirebaseAuth.getInstance()
        val currentUser = auth.currentUser
        if (currentUser != null) {
            val intent = Intent(this, Home::class.java)

            startActivity(intent)
            overridePendingTransition(0, 0)
            finish()
        }

    }
    fun check() {
        var login = findViewById<Button>(R.id.login_button)
        login.setOnClickListener() {

            try {

                val emails: EditText = findViewById(R.id.usernametxt)
                val pass: EditText = findViewById(R.id.password)
                val parts = emails.text.split('@', '.')
                val result = parts[0] + parts[1]

            Log.d("resultemail", result)
            val db = FirebaseFirestore.getInstance()
            val docRef = db.collection("pastors").document(result.lowercase())
            docRef.get()
                .addOnSuccessListener { document ->
                    if (document.exists()) {
                        val userDetails = document.get("userDetails") as Map<String, Any>
                        val admincheck = userDetails["admin"].toString().lowercase()
                  if(admincheck == "true")
                  {
                        val auth = FirebaseAuth.getInstance()
                        auth.createUserWithEmailAndPassword(
                            emails.text.toString().lowercase(),
                            pass.text.toString()
                        )
                            .addOnCompleteListener(this) { task ->
                                if (task.isSuccessful) {

                                    val user = auth.currentUser
                                    val text =
                                        "you have claimed your credentials and have been logged in "
                                    val duration = Toast.LENGTH_SHORT
                                    val toast = Toast.makeText(this, text, duration)
                                    toast.show()
                                    autologin()

                                } else {

                                    val security = Firebase.auth


                                    security.signInWithEmailAndPassword(
                                        emails?.text.toString().trim(),
                                        pass?.text.toString().trim()

                                    )
                                        .addOnCompleteListener(this) { task ->
                                            when {
                                                task.isSuccessful -> {

                                                    val text = "you  have been logged in "
                                                    val duration = Toast.LENGTH_SHORT
                                                    val toast = Toast.makeText(this, text, duration)
                                                    toast.show()
                                                    Handler(Looper.getMainLooper()).postDelayed({
                                                        val intent = Intent(this, Home::class.java)
                                                        startActivity(intent)
                                                        overridePendingTransition(0, 0)
                                                        finish()
                                                    }, (duration * 1000).toLong())

                                                }

                                                else -> {
                                                    val crouton = Crouton.makeText(
                                                        this,
                                                        e.loginError,
                                                        Style.ALERT
                                                    )
                                                    crouton.show()
                                                }
                                            }
                                        }


                                }
                            }
                  }
                        else{
                      val crouton = Crouton.makeText(this,"User ${emails.text.toString()} is not an admin please speak to Your supervisor", Style.ALERT)
                      crouton.show()
                  }

                    } else {
                        val crouton = Crouton.makeText(this,"Profile is not confirmed,please save your details under the profile page of the website", Style.ALERT)
                        crouton.show()
                    }
                }

        }    catch (E:Exception)
            {  val crouton = Crouton.makeText(this, "Fields cant be empty ", Style.ALERT)
                crouton.show()

            }

        }

    }
    fun forgotpassword() {
        var email: EditText = findViewById(R.id.usernametxt)
        if (email.text.toString().isNullOrEmpty())
        {
            val crouton = Crouton.makeText(this, "Email is Empty", Style.ALERT)
            crouton.show()
        }
        else {
            val auth = FirebaseAuth.getInstance()
            auth.sendPasswordResetEmail(email.text.toString())
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        val crouton = Crouton.makeText(this, "forgot email sent", Style.ALERT)
                        crouton.show()
                    } else {
                        // There was an error. Handle it here.
                    }
                }
        }
    }
    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    private fun permissions() {
        val code = 0

        when {
            ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.CAMERA
            ) != PackageManager.PERMISSION_GRANTED || ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.READ_EXTERNAL_STORAGE
            ) != PackageManager.PERMISSION_GRANTED || ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE
            ) != PackageManager.PERMISSION_GRANTED || ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_MEDIA_LOCATION
            ) != PackageManager.PERMISSION_GRANTED || ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.READ_MEDIA_IMAGES
            ) != PackageManager.PERMISSION_GRANTED || ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.READ_MEDIA_AUDIO
            ) != PackageManager.PERMISSION_GRANTED || ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.READ_MEDIA_VIDEO
            ) != PackageManager.PERMISSION_GRANTED || ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.POST_NOTIFICATIONS
            ) != PackageManager.PERMISSION_GRANTED || ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED || ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED || ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.INTERNET
            ) != PackageManager.PERMISSION_GRANTED -> {
                ActivityCompat.requestPermissions(
                    this, arrayOf(
                        Manifest.permission.CAMERA,
                        Manifest.permission.READ_EXTERNAL_STORAGE,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE,
                        Manifest.permission.ACCESS_MEDIA_LOCATION,
                        Manifest.permission.READ_MEDIA_IMAGES,
                        Manifest.permission.READ_MEDIA_AUDIO,
                        Manifest.permission.READ_MEDIA_VIDEO,
                        Manifest.permission.POST_NOTIFICATIONS,
                        Manifest.permission.ACCESS_FINE_LOCATION,
                        Manifest.permission.ACCESS_COARSE_LOCATION,
                        Manifest.permission.INTERNET
                    ), code
                )
            }
        }
    }

}