package com.example.carpark

import android.content.Intent
import android.os.Bundle
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.drawerlayout.widget.DrawerLayout
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.material.navigation.NavigationView
import kotlin.properties.Delegates

class DetectedLocation : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var address: String
    private lateinit var locality: String
    private lateinit var country: String
    private var latitude by Delegates.notNull<Double>()
    private var longitude by Delegates.notNull<Double>()
    private lateinit var date: String
    private lateinit var time: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detected_location)
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);

        extractIntentData()

        setupUI()

        initializeMap()

        setupClickListeners()

        setNavigationDrawer()

    }

    private fun setNavigationDrawer() {
        val drawerLayout = findViewById<DrawerLayout>(R.id.drawerLayout)
        val btn_drawer = findViewById<ImageButton>(R.id.btnDrawer)
        val navigationView = findViewById<NavigationView>(R.id.navigationView)
        findViewById<TextView>(R.id.title).text = locality
        btn_drawer.setOnClickListener {
            drawerLayout.open()
        }
        navigationView.setNavigationItemSelectedListener { menuItem ->
            when (menuItem.itemId) {
                R.id.home -> {
                    val intent = Intent(this, MainActivity::class.java)
                    startActivity(intent)
                    finish()
                }
                R.id.locations -> {
                    val intent = Intent(this, MyLocations::class.java)
                    startActivity(intent)
                    finish()
                }
            }
            true
        }
    }

    private fun extractIntentData() {
        address = intent.getStringExtra("ADDRESS").toString()
        latitude = intent.getStringExtra("LATITUDE")!!.toDouble()
        longitude = intent.getStringExtra("LONGITUDE")!!.toDouble()
        date = intent.getStringExtra("DATE").toString()
        time = intent.getStringExtra("TIME").toString()
        locality = intent.getStringExtra("LOCALITY").toString()
        country = intent.getStringExtra("COUNTRY").toString()
    }

    private fun setupUI() {
        findViewById<TextView>(R.id.tv_address).text = "Indirizzo: $address"
        findViewById<TextView>(R.id.tv_latitude).text = "Latitudine: $latitude"
        findViewById<TextView>(R.id.tv_longitude).text = "Longitudine: $longitude"
        findViewById<TextView>(R.id.tv_country).text = "Nazione: $country"
        findViewById<TextView>(R.id.tv_locality).text = "Città: $locality"
        findViewById<TextView>(R.id.tv_date_time).text = "$date, $time"
    }

    private fun initializeMap() {
        val mapFragment: SupportMapFragment? = supportFragmentManager.findFragmentById(R.id.id_map) as SupportMapFragment
        mapFragment?.getMapAsync(this)
    }

    private fun setupClickListeners() {
        findViewById<ImageView>(R.id.img_share).setOnClickListener {
            LocationUtils.shareLocation(this, address)
        }

        findViewById<ImageView>(R.id.img_save).setOnClickListener {
            val location = Location(date, time, address, country, locality, latitude, longitude)
            LocationUtils.saveLocation(this, location)
        }
    }

    override fun onMapReady(googleMap: GoogleMap) {
        LocationUtils.onMapReady(googleMap, latitude, longitude, address)
    }
}