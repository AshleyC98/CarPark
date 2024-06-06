package com.example.carpark

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.drawerlayout.widget.DrawerLayout
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.navigation.NavigationView

class MyLocations : AppCompatActivity() {

    private lateinit var recycler_view: RecyclerView
    private lateinit var locationAdapter: LocationAdapter
    private lateinit var db: DataBaseHelper
    private lateinit var locations: ArrayList<Location>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_my_locations)
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        locations = ArrayList()
        setNavigationDrawer()
        db = DataBaseHelper(this)
        setRecyclerView()
        findViewById<ImageView>(R.id.delete_all).setOnClickListener {
            delete()
        }
    }

    private fun setNavigationDrawer() {
        val drawerLayout = findViewById<DrawerLayout>(R.id.drawerLayout)
        val btn_drawer = findViewById<ImageButton>(R.id.btnDrawer)
        val navigationView = findViewById<NavigationView>(R.id.navigationView)
        findViewById<TextView>(R.id.title).text = "Le mie posizioni"
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
                }
            }
            true
        }
    }

    fun delete() {
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Eliminare tutte le posizioni?")
        builder.setMessage("Confermare l'eleminazione di tutte le posizioni?")
        builder.setPositiveButton(
            "No"
        ) { dialogInterface, i -> }
        builder.setNegativeButton(
            "Si"
        ) { dialogInterface, i ->
            db.deleteAll()
            val intent = Intent(this, MyLocations::class.java)
            startActivity(intent)
            finish()
        }
        builder.create().show()
    }

    fun setRecyclerView() {
        recycler_view = findViewById(R.id.recycler_view)
        locations = storeDataInArray()
        recycler_view.setHasFixedSize(true)

        val layoutManager = LinearLayoutManager(this)
        layoutManager.reverseLayout = true
        layoutManager.stackFromEnd = true
        recycler_view.layoutManager = layoutManager

        locationAdapter = LocationAdapter(this@MyLocations, locations)
        recycler_view.adapter = locationAdapter
    }


    private fun storeDataInArray(): ArrayList<Location> {
        val cursor = db.readAllData()
        if (cursor != null && cursor.count > 0) {
            while (cursor.moveToNext()) {
                val date = cursor.getString(cursor.getColumnIndexOrThrow(DataBaseHelper.COL_DATE))
                val time = cursor.getString(cursor.getColumnIndexOrThrow(DataBaseHelper.COL_TIME))
                val address =
                    cursor.getString(cursor.getColumnIndexOrThrow(DataBaseHelper.COL_ADDRESS))
                val country =
                    cursor.getString(cursor.getColumnIndexOrThrow(DataBaseHelper.COL_COUNTRY))
                val locality =
                    cursor.getString(cursor.getColumnIndexOrThrow(DataBaseHelper.COL_LOCALITY))
                val latitude =
                    cursor.getDouble(cursor.getColumnIndexOrThrow(DataBaseHelper.COL_LATITUDE))
                val longitude =
                    cursor.getDouble(cursor.getColumnIndexOrThrow(DataBaseHelper.COL_LONGITUDE))
                val location = Location(date, time, address, country, locality, latitude, longitude)
                locations.add(location)
            }
        } else {
            findViewById<ImageView>(R.id.img_no_locations).visibility = View.VISIBLE
            findViewById<TextView>(R.id.tv_no_locations).visibility = View.VISIBLE
            findViewById<ImageView>(R.id.delete_all).visibility = View.GONE
        }
        cursor?.close()
        return locations
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(resultCode == 0) {
            recreate()
        }
    }
}

