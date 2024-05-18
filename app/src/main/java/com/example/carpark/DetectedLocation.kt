package com.example.carpark

import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
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

        extractIntentData()

        setupUI()

        initializeMap()

        setupClickListeners()
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