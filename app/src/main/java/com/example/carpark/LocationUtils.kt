package com.example.carpark

import android.content.Context
import android.content.Intent
import android.net.Uri
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions

object LocationUtils {

    fun openMaps(context: Context, address: String) {
        val mapsIntent = Intent(Intent.ACTION_VIEW, Uri.parse("https://www.google.com/maps?q=${Uri.encode(address)}"))
        context.startActivity(mapsIntent)
    }

    fun shareLocation(context: Context, address: String) {
        val mapsUrl = "https://www.google.com/maps?q=${Uri.encode(address)}"
        val shareIntent = Intent(Intent.ACTION_SEND)
        shareIntent.type = "text/plain"
        shareIntent.putExtra(Intent.EXTRA_TEXT, mapsUrl)
        context.startActivity(Intent.createChooser(shareIntent, "Condividi posizione tramite"))
    }

    fun saveLocation(context: Context, location: Location) {
        val db = DataBaseHelper(context)
        db.insertData(location)
    }

    fun onMapReady(googleMap: GoogleMap, latitude: Double, longitude: Double, address: String) {
        val location = LatLng(latitude, longitude)
        googleMap.addMarker(MarkerOptions().position(location).title(address))
        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(location, 15f))
    }
}
