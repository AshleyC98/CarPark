package com.example.carpark

import android.content.Intent
import android.os.Bundle
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.drawerlayout.widget.DrawerLayout
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.material.navigation.NavigationView
import kotlin.properties.Delegates


class LocationDetails : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var address: String
    private lateinit var locality: String
    private lateinit var country: String
    private var latitude by Delegates.notNull<Double>()
    private var longitude by Delegates.notNull<Double>()
    private lateinit var date: String
    private lateinit var time: String
    private lateinit var db:DataBaseHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_location_details)
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        db = DataBaseHelper(this)

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
        if(intent.hasExtra("DATE") && intent.hasExtra("TIME") && intent.hasExtra("ADDRESS")) {
            date = intent.getStringExtra("DATE").toString()
            time = intent.getStringExtra("TIME").toString()
            address = intent.getStringExtra("ADDRESS").toString()
            val location: Location? = db.getLocation(date, time)
            latitude = location!!.latitude
            longitude = location!!.longitude
            locality = location!!.locality
            country = location!!.country
        }
        else
            Toast.makeText(this, "Dati non disponibili", Toast.LENGTH_LONG).show()
    }

    private fun setupUI() {
        findViewById<TextView>(R.id.tv_date_time).text = "$date, $time"
        findViewById<TextView>(R.id.tv_address).text = "Indirizzo: $address"
        findViewById<TextView>(R.id.tv_latitude).text = "Latitudine: $latitude"
        findViewById<TextView>(R.id.tv_longitude).text = "Longitudine: $longitude"
        findViewById<TextView>(R.id.tv_country).text = "Nazione: $country"
        findViewById<TextView>(R.id.tv_locality).text = "Città: $locality"
    }

    private fun initializeMap() {
        val mapFragment: SupportMapFragment? = supportFragmentManager.findFragmentById(R.id.id_map) as SupportMapFragment
        mapFragment?.getMapAsync(this)
    }

    private fun setupClickListeners() {
        findViewById<ImageView>(R.id.img_go).setOnClickListener {
            LocationUtils.openMaps(this, address)
        }

        findViewById<ImageView>(R.id.img_share).setOnClickListener {
            LocationUtils.shareLocation(this, address)
        }

        findViewById<ImageView>(R.id.img_delete).setOnClickListener {
            confirmDialog()
        }
    }

    override fun onMapReady(googleMap: GoogleMap) {
        LocationUtils.onMapReady(googleMap, latitude, longitude, address)
    }

    private fun confirmDialog() {
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Eliminare la posizione?")
        builder.setMessage("Confermare l'eliminazione di $address ?")
        builder.setPositiveButton(
            "No"
        ) { dialogInterface, i -> }
        builder.setNegativeButton(
            "Si"
        ) { dialogInterface, i ->
            val db = DataBaseHelper(this@LocationDetails)
            db.deleteOneRow(date, time)
            finish()
        }
        builder.create().show()
    }

}
