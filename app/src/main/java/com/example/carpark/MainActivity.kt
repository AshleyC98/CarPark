package com.example.carpark

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Address
import android.location.Geocoder
import android.location.Location
import android.location.LocationManager
import android.net.ConnectivityManager
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.provider.Settings
import android.view.View
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.app.ActivityCompat
import androidx.drawerlayout.widget.DrawerLayout
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.android.material.navigation.NavigationView
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class MainActivity : AppCompatActivity() {
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var locationCallback: LocationCallback
    private lateinit var locationRequest: LocationRequest
    private lateinit var tv_loading: TextView
    private lateinit var progress_bar: ProgressBar
    private lateinit var tv_hint: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        setNavigationDrawer()
        setClient()
        getLocation()
    }

    private fun setNavigationDrawer() {
        findViewById<TextView>(R.id.title).text = "Home"
        val drawerLayout = findViewById<DrawerLayout>(R.id.drawerLayout)
        val btn_drawer = findViewById<ImageButton>(R.id.btnDrawer)
        val navigationView = findViewById<NavigationView>(R.id.navigationView)
        btn_drawer.setOnClickListener {
            drawerLayout.open()
        }
        navigationView.setNavigationItemSelectedListener { menuItem ->
            when (menuItem.itemId) {
                R.id.home -> {
                }

                R.id.locations -> {
                    val intent = Intent(this, MyLocations::class.java)
                    startActivity(intent)
                }
            }
            true
        }
    }

    private fun setClient() {
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        locationRequest = LocationRequest.create().apply {
            priority = LocationRequest.PRIORITY_HIGH_ACCURACY
            interval = 10000
            fastestInterval = 500
        }
    }

    private fun requestPermissions() {
        ActivityCompat.requestPermissions(
            this,
            arrayOf(
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION,
            ), 1
        )
    }

    private fun isLocationEnabled(context: Context): Boolean {
        val locationManager = context.getSystemService(Context.LOCATION_SERVICE) as LocationManager
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) ||
                locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)
    }

    private fun isInternetEnabled(context: Context): Boolean {
        val connectivityManager =
            context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val networkInfo = connectivityManager.activeNetworkInfo
        return networkInfo != null && networkInfo.isConnected
    }

    private fun getLocation() {
        tv_hint = findViewById(R.id.tv_hint)
        tv_loading = findViewById(R.id.tv_loading)
        progress_bar = findViewById(R.id.progressBar)
        findViewById<ImageView>(R.id.btn_park).setOnClickListener {
            progress_bar.progress = 0
            progress_bar.visibility = View.VISIBLE
            tv_loading.visibility = View.VISIBLE
            tv_hint.visibility = View.GONE
            if (isInternetEnabled(this)) {
                progress_bar.progress = 20
                Handler().postDelayed({
                    if (isLocationEnabled(this)) {
                        progress_bar.progress = 50
                        Handler().postDelayed({
                            requestLocationUpdates()
                        }, 200) // Ritardo di 200 millisecondi dopo aver impostato il progresso al 50%
                    } else {
                        Toast.makeText(this, "Attivare la posizione", Toast.LENGTH_LONG).show()
                        val settingsIntent =
                            Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
                        startActivity(settingsIntent)
                        tv_loading.visibility = View.GONE
                        progress_bar.visibility = View.GONE
                        progress_bar.progress = 0
                    }
                }, 200) // Ritardo di 200 millisecondi dopo aver impostato il progresso al 20%
            } else {
                Toast.makeText(this, "Nessun accesso ad Internet", Toast.LENGTH_LONG).show()
                tv_loading.visibility = View.GONE
                progress_bar.visibility = View.GONE
                progress_bar.progress = 0
            }
        }
    }


    private fun requestLocationUpdates() {
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            requestPermissions()
            tv_loading.visibility = View.GONE
            progress_bar.visibility = View.GONE
            progress_bar.progress = 0
            return
        }
        locationCallback = object : LocationCallback() {
            override fun onLocationResult(locationResult: LocationResult) {
                super.onLocationResult(locationResult)
                val location = locationResult.lastLocation
                if (location != null) {
                    progress_bar.progress = 80
                    Handler().postDelayed({
                        updateLocation(location)
                        fusedLocationClient.removeLocationUpdates(this)
                    }, 300) // Ritardo di 200 millisecondi dopo aver impostato il progresso al 80%
                } else {
                    Toast.makeText(
                        this@MainActivity,
                        "Posizione non disponibile",
                        Toast.LENGTH_LONG
                    ).show()
                    tv_loading.visibility = View.GONE
                    progress_bar.visibility = View.GONE
                    progress_bar.progress = 0
                }
            }
        }
        fusedLocationClient.requestLocationUpdates(
            locationRequest,
            locationCallback,
            Looper.getMainLooper()
        )
    }


    private fun updateLocation(location: Location) {
        try {
            val geocoder = Geocoder(this, Locale.getDefault())
            val list: MutableList<Address>? =
                geocoder.getFromLocation(location.latitude, location.longitude, 1)
            val address = list?.get(0)?.getAddressLine(0)
            val latitude = location.latitude
            val longitude = location.longitude
            val country = list?.get(0)?.countryName
            val locality = list?.get(0)?.locality
            val currentDateTime = Calendar.getInstance()
            val date = SimpleDateFormat("dd/MM/yyyy", Locale.US).format(currentDateTime.time)
            val time = SimpleDateFormat("HH:mm", Locale.US).format(currentDateTime.time)
            val locationIntent = Intent(this, DetectedLocation::class.java)
            locationIntent.putExtra("DATE", date)
            locationIntent.putExtra("TIME", time)
            locationIntent.putExtra("ADDRESS", address)
            locationIntent.putExtra("COUNTRY", country)
            locationIntent.putExtra("LOCALITY", locality)
            locationIntent.putExtra("LATITUDE", "$latitude")
            locationIntent.putExtra("LONGITUDE", "$longitude")

            progress_bar.progress = 100
            Handler().postDelayed({
                progress_bar.visibility = View.GONE
                tv_loading.visibility = View.GONE
                startActivity(locationIntent)
                tv_hint.visibility = View.VISIBLE
            }, 400) // Ritardo di 200 millisecondi dopo aver impostato il progresso al 100%

        } catch (e: IOException) {
            Toast.makeText(this, "Errore durante la geocodifica", Toast.LENGTH_LONG).show()
            tv_loading.visibility = View.GONE
            progress_bar.visibility = View.GONE
            progress_bar.progress = 0
        }
    }


    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == 1) {
            if (grantResults.isNotEmpty() &&
                grantResults[0] == PackageManager.PERMISSION_GRANTED &&
                grantResults[1] == PackageManager.PERMISSION_GRANTED
            ) {
                getLocation()
            }
        }
    }
}
