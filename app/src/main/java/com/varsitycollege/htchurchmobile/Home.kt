package com.varsitycollege.htchurchmobile

import android.annotation.SuppressLint
import android.app.Activity
import android.content.ContentValues
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.net.Uri
import android.os.Bundle
import android.text.style.LineBackgroundSpan
import android.util.Log
import android.view.MenuItem
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.drawerlayout.widget.DrawerLayout
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CircleCrop
import com.bumptech.glide.request.RequestOptions
import com.github.clans.fab.FloatingActionButton
import com.github.clans.fab.FloatingActionMenu
import com.google.android.material.navigation.NavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.ktx.storage
import com.jakewharton.threetenabp.AndroidThreeTen
import com.prolificinteractive.materialcalendarview.CalendarDay
import com.prolificinteractive.materialcalendarview.DayViewDecorator
import com.prolificinteractive.materialcalendarview.DayViewFacade
import com.prolificinteractive.materialcalendarview.MaterialCalendarView
import java.io.ByteArrayOutputStream
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.util.Calendar
import java.util.Locale
import android.text.Html
import android.widget.ImageButton
import androidx.activity.result.ActivityResult
import java.io.File
import java.io.FileOutputStream


class Home:AppCompatActivity() {
    private lateinit var drawerLayout: DrawerLayout
    private lateinit var actionBarDrawerToggle: ActionBarDrawerToggle
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.home)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        securGuard()
        val eventName = intent.getStringExtra("eventName")
        val eventDate = intent.getStringExtra("eventDate")
        val eventDescription = intent.getStringExtra("eventDescription")
        val eventAddress = intent.getStringExtra("eventAddress")
        val eventStartTime = intent.getStringExtra("eventStartTime")
        val eventEndTime = intent.getStringExtra("eventEndTime")
        val eventChurch = intent.getStringExtra("eventChurch")
        val eventNameTextView = findViewById<TextView>(R.id.eventNameTextView)
        val eventDateTextView = findViewById<TextView>(R.id.eventDateTextView)
        val eventDescriptionTextView = findViewById<TextView>(R.id.eventDescriptionTextView)
        val eventAddressTextView = findViewById<TextView>(R.id.eventAddressTextView)
        val eventStartTimeTextView = findViewById<TextView>(R.id.eventStartTimeTextView)
        val eventEndTimeTextView = findViewById<TextView>(R.id.eventEndTimeTextView)
        val eventChurchTextView = findViewById<TextView>(R.id.eventChurchTextView)
        // Populate the TextView elements with event data
        eventNameTextView.text = eventName
        eventDateTextView.text = eventDate
        eventDescriptionTextView.text = eventDescription
        eventAddressTextView.text = eventAddress
        eventStartTimeTextView.text = eventStartTime
        eventEndTimeTextView.text = eventEndTime
        eventChurchTextView.text = eventChurch
        val navigationView: NavigationView = findViewById(R.id.nav_view)
        navigationView.setNavigationItemSelectedListener { menuItem ->
            when (menuItem.itemId) {
                R.id.menu_profile -> {
                    val intent = Intent(this, Profile::class.java)

                    startActivity(intent)
                    overridePendingTransition(0, 0)
                    finish()
                    true
                }

                R.id.menu_event -> {
                    val intent = Intent(this, Events::class.java)
                    startActivity(intent)
                    overridePendingTransition(0, 0)
                    finish()
                    true
                }

                R.id.menu_logout -> {
                    signout()
                    val intent = Intent(this, Login::class.java)

                    startActivity(intent)
                    overridePendingTransition(0, 0)
                    finish()
                    true
                }

                R.id.menu_pastor-> {

                    val intent = Intent(this, ViewPastors::class.java)

                    startActivity(intent)
                    overridePendingTransition(0, 0)
                    finish()
                    true
                }

                R.id.menu_member-> {

                    val intent = Intent(this, Members::class.java)

                    startActivity(intent)
                    overridePendingTransition(0, 0)
                    finish()
                    true
                }

                R.id.menu_finance -> {

                    val intent = Intent(this, Finances::class.java)

                    startActivity(intent)
                    overridePendingTransition(0, 0)
                    finish()
                    true
                }

                R.id.menu_secretary -> {
                    val intent = Intent(this, secratary::class.java)

                    startActivity(intent)
                    overridePendingTransition(0, 0)
                    finish()
                    true
                }

                R.id.menu_church_details-> {

                    val intent = Intent(this,ChurchDetails::class.java)

                    startActivity(intent)
                    overridePendingTransition(0, 0)
                    finish()
                    true
                }
                else -> false
            }


        }


        val eventsAddButton: ImageButton = findViewById(R.id.eventsCreate_btn)
        eventsAddButton.setOnClickListener {
            // Navigate to another page
            val intent = Intent(this,Events::class.java)
            startActivity(intent)
            finish()
        }

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

        val exit: FloatingActionButton = findViewById(R.id.exit)
        exit.setOnClickListener {
            signout()
            val intent = Intent(this, Login::class.java)

            startActivity(intent)
            overridePendingTransition(0, 0)
            finish()
            menu.close(true)
        }
        val events: FloatingActionButton = findViewById(R.id.event)
        events.setOnClickListener {

            val intent = Intent(this, Events::class.java)

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
        drawerLayout = findViewById(R.id.drawer_layout)
        actionBarDrawerToggle =
            ActionBarDrawerToggle(this, drawerLayout, R.string.nav_open, R.string.nav_close)
        drawerLayout.addDrawerListener(actionBarDrawerToggle)
        actionBarDrawerToggle.syncState()
        details()
        EventClosestToCurrentDate()
        GetChurchDetails()
        AndroidThreeTen.init(this)
        val calendarView: MaterialCalendarView = findViewById(R.id.calendarViewHonme)
        val currentDate = CalendarDay.today()
        calendarView.setDateSelected(currentDate, true)
        val db = FirebaseFirestore.getInstance()
        db.collection("events")
            .get()
            .addOnSuccessListener { result ->
                for (document in result) {
                    val dateString = document.data["date"] as String
                    val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.US)
                    val date = sdf.parse(dateString)
                    val calendar = Calendar.getInstance()
                    calendar.time = date
                    val zonedDateTime = org.threeten.bp.ZonedDateTime.ofInstant(
                        org.threeten.bp.Instant.ofEpochMilli(calendar.timeInMillis),
                        org.threeten.bp.ZoneId.systemDefault()
                    )
                    val localDate = zonedDateTime.toLocalDate()
                    val day = CalendarDay.from(localDate)
                    val decorator = EventDecorator(Color.BLUE, listOf(day))
                    calendarView.addDecorator(decorator)
                }
            }

        fetchAndDisplayFinancialData()
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


    fun fetchAndDisplayFinancialData() {
        val user = FirebaseAuth.getInstance().currentUser
        val userEmail = user?.email
        val parts = userEmail!!.split('@', '.')
        val userID = parts[0] + parts[1]

        Log.d("userid", userID)
        val db = FirebaseFirestore.getInstance()
        val docRef = db.collection("pastors").document(userID)

        docRef.get()
            .addOnSuccessListener { doc ->
                if (doc != null) {
                    Log.d(ContentValues.TAG, "DocumentSnapshot data: ${doc.data}")
                    val userDet = doc.get("userDetails") as Map<String, Any>
                    // Extract the church ID
                    val churchId = userDet["churchid"].toString()
                    Log.d("Church ID", "Church ID: $churchId")

                    db.collection("churchs").document(churchId)
                        .get()
                        .addOnSuccessListener { churchDoc ->
                            if (churchDoc != null) {
                                Log.d(ContentValues.TAG, "Church DocumentSnapshot data: ${churchDoc.data}")
                                val financeData = churchDoc.get("finance") as Map<String, Any>?

                                if (financeData != null) {
                                    val entries = financeData["entries"] as List<Map<String, Any>>?

                                    if (entries != null) {
                                        var totalTithes = 0.0
                                        var totalDonations = 0.0
                                        var totalFundRaised = 0.0

                                        for (entryMap in entries) {
                                            val tithesValue = entryMap["tithes"] as Double
                                            val donationsValue = entryMap["donations"] as Double
                                            val fundRaiserValue = entryMap["fundRaiser"] as Double

                                            totalTithes += tithesValue
                                            totalDonations += donationsValue
                                            totalFundRaised += fundRaiserValue
                                        }

                                        // Get references to the TextViews
                                        val tithesTextView: TextView = findViewById(R.id.tithesTextViewHome)
                                        val donationsTextView: TextView = findViewById(R.id.donationsTextViewHome)
                                        val fundRaisedTextView: TextView = findViewById(R.id.fundraisedTextViewHome)

                                        // Set the text of the TextViews with the overall totals
                                        tithesTextView.text = String.format("Total Tithes:\nR %.2f", totalTithes)
                                        donationsTextView.text = String.format("Total Donations:\nR %.2f", totalDonations)
                                        fundRaisedTextView.text = String.format("Total Fund Raised:\nR %.2f", totalFundRaised)
                                    }
                                } else {
                                    Log.d("No finance document", "No finance document")
                                }
                            }
                        }
                        .addOnFailureListener { exception ->
                            Log.d("get failed with ", "get failed with ", exception)
                        }
                }
            }
    }


    fun GetChurchDetails() {
        val user = FirebaseAuth.getInstance().currentUser
        val userEmail = user?.email
        if (userEmail != null) {
            val parts = userEmail.split('@', '.')
            val userID = parts[0] + parts[1]
            Log.d("userid", userID)
            val db = FirebaseFirestore.getInstance()
            val docRef = db.collection("pastors").document(userID)
            docRef.get()
                .addOnSuccessListener { doc ->
                    if (doc != null) {
                        Log.d(ContentValues.TAG, "DocumentSnapshot data: ${doc.data}")
                        val userDet = doc.get("userDetails") as? Map<String, Any>
                        // Extract the church ID
                        val id = userDet?.get("churchid")?.toString()
                        if (id != null) {
                            Log.d("Church ID", "Church ID: $id")
                            db.collection("churchs").document(id)
                                .get()
                                .addOnSuccessListener { churchDoc ->
                                    if (churchDoc != null) {
                                        Log.d(ContentValues.TAG, "Church DocumentSnapshot data: ${churchDoc.data}")
                                        val churchDet = churchDoc.get("churchDetails") as? Map<String, Any>
                                        val churchname = churchDet?.get("churchname")?.toString()
                                        val membersNum = churchDet?.get("members")?.toString()
                                        val pastors = churchDet?.get("pastors")?.toString()
                                        val location = churchDet?.get("location")?.toString()
                                        if (churchname != null && membersNum != null && pastors != null && location != null) {
                                            Log.d("Location", "Location: $location")
                                            Log.d("churchname", "Location: $churchname")
                                            Log.d("membersNum", "membersNum: $membersNum")
                                            Log.d("pastors", "pastors: $pastors")
                                            // Get references to the TextViews
                                            val churchNameTextView: TextView = findViewById(R.id.churchNameTextView)
                                            val churchLocationTextView: TextView = findViewById(R.id.churchLocationTextView)
                                            val churchMembersTextView: TextView = findViewById(R.id.churchMembersTextView)
                                            val churchPastorsTextView: TextView = findViewById(R.id.churchPastorsTextView)
                                            churchNameTextView.text = Html.fromHtml("<b>Church Name:</b><br>$churchname", Html.FROM_HTML_MODE_COMPACT)
                                            churchLocationTextView.text = Html.fromHtml("<b>Location:</b><br>$location", Html.FROM_HTML_MODE_COMPACT)
                                            churchMembersTextView.text = Html.fromHtml("<b>Members:</b><br>$membersNum", Html.FROM_HTML_MODE_COMPACT)
                                            churchPastorsTextView.text = Html.fromHtml("<b>Pastors:</b><br>$pastors", Html.FROM_HTML_MODE_COMPACT)
                                        } else {
                                            Log.d("No such document", "No such document")
                                        }
                                    } else {
                                        Log.d("No such document", "No such document")
                                    }
                                }
                                .addOnFailureListener { exception ->
                                    Log.d("get failed with ", "get failed with ", exception)
                                }
                        } else {
                            Log.d("No such document", "No such document")
                        }
                    } else {
                        Log.d("No such document", "No such document")
                    }
                }
                .addOnFailureListener { exception ->
                    Log.d("get failed with ", "get failed with ", exception)
                }
        }
    }




    fun EventClosestToCurrentDate() {
        val db = FirebaseFirestore.getInstance()
        val eventList = ArrayList<Event>()
        db.collection("events")
            .get()
            .addOnSuccessListener { documents ->
                for (document in documents) {
                    val name = document.getString("name") ?: ""
                    val description = document.getString("description") ?: ""
                    val church = document.getString("church") ?: ""
                    val location = document.getString("address") ?: ""
                    val eventdate = document.getString("date") ?: ""
                    val startTime = document.getString("start_time") ?: ""
                    val endTime = document.getString("end_time") ?: ""
                    val event = Event(name, description, church, location, eventdate, startTime, endTime)
                    eventList.add(event)
                }
                val eventsSorted = eventList.sortedBy { LocalDate.parse(it.date) }
                val currentDay = LocalDate.now()
                val closestEvent = eventsSorted.firstOrNull { LocalDate.parse(it.date) > currentDay }
                // Initialize the TextView elements
                val eventNameTextView = findViewById<TextView>(R.id.eventNameTextView)
                val eventDateTextView = findViewById<TextView>(R.id.eventDateTextView)
                val eventDescriptionTextView = findViewById<TextView>(R.id.eventDescriptionTextView)
                val eventAddressTextView = findViewById<TextView>(R.id.eventAddressTextView)
                val eventStartTimeTextView = findViewById<TextView>(R.id.eventStartTimeTextView)
                val eventEndTimeTextView = findViewById<TextView>(R.id.eventEndTimeTextView)
                val eventChurchTextView = findViewById<TextView>(R.id.eventChurchTextView)
                if (closestEvent != null) {
                    // Populate the TextView elements with the closest event data
                    eventNameTextView.text = Html.fromHtml("<b>Name:</b><br>${closestEvent.name}", Html.FROM_HTML_MODE_COMPACT)
                    eventDateTextView.text = Html.fromHtml("<b>Date:</b><br>${closestEvent.date}", Html.FROM_HTML_MODE_COMPACT)
                    eventDescriptionTextView.text = Html.fromHtml("<b>Description:</b><br>${closestEvent.description}", Html.FROM_HTML_MODE_COMPACT)
                    eventAddressTextView.text = Html.fromHtml("<b>Address:</b><br>${closestEvent.location}", Html.FROM_HTML_MODE_COMPACT)
                    eventStartTimeTextView.text = Html.fromHtml("<b>Start Time:</b><br>${closestEvent.startTime}", Html.FROM_HTML_MODE_COMPACT)
                    eventEndTimeTextView.text = Html.fromHtml("<b>End Time:</b><br>${closestEvent.endTime}", Html.FROM_HTML_MODE_COMPACT)
                    eventChurchTextView.text = Html.fromHtml("<b>Church:</b><br>${closestEvent.church}", Html.FROM_HTML_MODE_COMPACT)


                } else {
                    // Display a message to the user when no event is found
                    val message = "Please add an event"

                    eventDescriptionTextView.text = message

                }
            }
    }



    fun details() {
        val user = FirebaseAuth.getInstance().currentUser
        val userEmail = user?.email
        if(userEmail != null) {
            val parts = userEmail!!.split('@', '.')
            val userID = parts[0] + parts[1]
            val storage = FirebaseStorage.getInstance()
            val imageRef = storage.getReference().child(userID)
            val navigationView: NavigationView = findViewById(R.id.nav_view)
            val email =
                navigationView.getHeaderView(0).findViewById<TextView>(R.id.nav_header_email)
            val name = navigationView.getHeaderView(0).findViewById<TextView>(R.id.nav_header_name)
            val image =
                navigationView.getHeaderView(0).findViewById<ImageView>(R.id.nav_header_image)
            email.text = userEmail
            imageRef.downloadUrl.addOnSuccessListener { Uri ->
                val url = Uri.toString()
                val profileimage = RequestOptions().transform(CircleCrop())
                Glide.with(this)
                    .load(url)
                    .apply(profileimage)
                    .into(image)
            }
            image.setOnClickListener()
            {


                val profileimage = AlertDialog.Builder(this)
                profileimage.setTitle("Choose an option")
                profileimage.setItems(
                    arrayOf(
                        "Take a photo?",
                        "Pick from gallery?",
                        "Default Picture"

                        )
                ) { _, which ->
                    when (which) {


                        0 -> camera.launch(null)
                        1 -> {
                            val intent = Intent(Intent.ACTION_PICK)
                            intent.type = "image/*"
                            galleryContent.launch(intent)
                        }
                        2 -> noPic()
                    }
                }
                val actionshow = profileimage.create()
                actionshow.show()

            }



            Log.d("userid", userID)
            val userfiles = FirebaseFirestore.getInstance()
            val data = userfiles.collection("pastors").document(userID)
            data.get()
                .addOnSuccessListener { document ->
                    if (document != null) {
                        Log.d(ContentValues.TAG, "DocumentSnapshot data: ${document.data}")
                        val profiledata = document.get("userDetails") as Map<String, Any>
                        val names = profiledata["firstname"].toString()
                        val surnames = profiledata["surname"].toString()
                        name.text = names + "" + surnames

                    } else {
                        Log.d(ContentValues.TAG, "No such document")
                    }
                }
                .addOnFailureListener { exception ->
                    Log.d(ContentValues.TAG, "get failed with ", exception)
                }
        }

    }
    private val galleryContent =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result: ActivityResult ->
            if (result.resultCode == Activity.RESULT_OK) {
                val url: Uri? = result.data?.data

                when {
                    url != null -> {
                        val user = FirebaseAuth.getInstance().currentUser
                        val userEmail = user?.email
                        val parts = userEmail!!.split('@', '.')
                        val userID = parts[0] + parts[1]
                        val navigationView: NavigationView = findViewById(R.id.nav_view)
                        var image = navigationView.getHeaderView(0).findViewById<ImageView>(R.id.nav_header_image)

                        image.setImageURI(url)

                        val store =
                            Firebase.storage.reference.child(userID)

                        val choice = store.putFile(url)
                        choice.addOnSuccessListener {
                            Thread(Runnable {
                                Glide.get(applicationContext).clearDiskCache()
                            }).start()

                        }.addOnFailureListener {

                        }
                    }
                }
            }
        }

    private val camera =
        registerForActivityResult(ActivityResultContracts.TakePicturePreview()) { photo: Bitmap? ->
            val user = FirebaseAuth.getInstance().currentUser

            val userEmail = user?.email
            val parts = userEmail!!.split('@', '.')
            val userID = parts[0] + parts[1]
            val navigationView: NavigationView = findViewById(R.id.nav_view)
            var image = navigationView.getHeaderView(0).findViewById<ImageView>(R.id.nav_header_image)


            val store =
                Firebase.storage.reference.child(userID.lowercase())

            image.setImageBitmap(photo)


            val imageRef = Firebase.storage.reference.child(userID.lowercase())


            val imageStream = ByteArrayOutputStream()
            photo?.compress(Bitmap.CompressFormat.JPEG, 100, imageStream)
            val data = imageStream.toByteArray()

            val uploadDP = imageRef.putBytes(data)
            uploadDP.addOnSuccessListener {
                val message = "IMAGE UPLOADED "
                Toast.makeText(applicationContext, message, Toast.LENGTH_SHORT).show()
            }.addOnFailureListener {
                val message = "INVALID IMAGE!"
                Toast.makeText(applicationContext, message, Toast.LENGTH_SHORT).show()
            }
        }
    fun noPic() {
        val navigationView: NavigationView = findViewById(R.id.nav_view)
        var image = navigationView.getHeaderView(0).findViewById<ImageView>(R.id.nav_header_image)
        val user = FirebaseAuth.getInstance().currentUser

        val userEmail = user?.email
        val parts = userEmail!!.split('@', '.')
        val userID = parts[0] + parts[1]
        val noPicstore = Firebase.storage.reference.child(userID)

        image.setImageResource(R.drawable.icon)

        val convertNoImage = BitmapFactory.decodeResource(resources, R.drawable.icon)


        val drawPic = File(cacheDir, "bird.png")
        val gotten = FileOutputStream(drawPic)
        convertNoImage.compress(Bitmap.CompressFormat.PNG, 100, gotten)
        gotten.close()

        val url = Uri.fromFile(drawPic)
        val picsUpload = noPicstore.putFile(url)

        picsUpload.addOnSuccessListener {

        }.addOnFailureListener {

        }

    }
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return if (actionBarDrawerToggle.onOptionsItemSelected(item)) {
            true
        } else super.onOptionsItemSelected(item)
    }
    private fun signout() {
        FirebaseAuth.getInstance().signOut()
    }
    @SuppressLint("WrongViewCast")
    fun IDload() {

        val churchid: EditText = findViewById(R.id.church_id)

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

                    churchid.setText(id)

                } else {
                    Log.d(ContentValues.TAG, "No such document")
                }
            }
            .addOnFailureListener { exception ->
                Log.d(ContentValues.TAG, "get failed with ", exception)
            }


    }
    class EventDecorator(private val color: Int, dates: Collection<CalendarDay>) :
        DayViewDecorator {
        private val dates: HashSet<CalendarDay> = HashSet(dates)
        override fun shouldDecorate(day: CalendarDay): Boolean {
            return dates.contains(day)
        }
        override fun decorate(view: DayViewFacade) {
            view.addSpan(BottomTextSpan(color))
        }
    }
    class BottomTextSpan(private val color: Int) : LineBackgroundSpan {
        override fun drawBackground(
            c: Canvas, p: Paint, left: Int, right: Int,
            top: Int, baseline: Int, bottom: Int,
            text: CharSequence, start: Int, end: Int,
            lnum: Int
        ) {
            val oldColor = p.color
            p.color = color
            c.drawCircle((right - left) / 2f, (top + -20).toFloat(), 10f, p) // Adjusted y-coordinate
            p.color = oldColor
        }
    }

}

