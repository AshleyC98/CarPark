package com.example.carpark

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.widget.Toast
import java.sql.SQLException

class DataBaseHelper(private val context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, 1) {

    companion object {
        const val DATABASE_NAME = "MyDB"
        const val TABLE_NAME = "Locations"
        const val COL_DATE = "Date"
        const val COL_TIME = "Time"
        const val COL_ADDRESS = "Address"
        const val COL_COUNTRY = "Country"
        const val COL_LOCALITY = "Locality"
        const val COL_LATITUDE = "Latitude"
        const val COL_LONGITUDE = "Longitude"
    }

    override fun onCreate(db: SQLiteDatabase?) {
        val createTable = "CREATE TABLE $TABLE_NAME(" +
                "$COL_DATE VARCHAR(15), " +
                "$COL_TIME VARCHAR(7), " +
                "$COL_ADDRESS VARCHAR(50), " +
                "$COL_COUNTRY VARCHAR(50), " +
                "$COL_LOCALITY VARCHAR(50), " +
                "$COL_LATITUDE REAL, " +
                "$COL_LONGITUDE REAL, " +
                "PRIMARY KEY($COL_DATE, $COL_TIME, $COL_ADDRESS))"
        db?.execSQL(createTable)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL("DROP TABLE IF EXISTS $TABLE_NAME")
        onCreate(db)
    }

    fun insertData(location: Location) {
        val db = writableDatabase
        val cv = ContentValues().apply {
            put(COL_DATE, location.date)
            put(COL_TIME, location.time)
            put(COL_ADDRESS, location.address)
            put(COL_COUNTRY, location.country)
            put(COL_LOCALITY, location.locality)
            put(COL_LATITUDE, location.latitude)
            put(COL_LONGITUDE, location.longitude)
        }
        val result = db.insert(TABLE_NAME, null, cv)
        if (result == -1L)
            Toast.makeText(context, "Salvataggio fallito", Toast.LENGTH_LONG).show()
        else
            Toast.makeText(context, "Salvataggio riuscito", Toast.LENGTH_LONG).show()
    }

    fun readAllData(): Cursor? {
        val db = readableDatabase
        return db.rawQuery("SELECT * FROM $TABLE_NAME", null)
    }

    fun getLocation(date: String, time: String): Location? {
        val db = this.readableDatabase
        val cursor = db.query(
            TABLE_NAME, null, "$COL_DATE = ? AND $COL_TIME = ?",
            arrayOf(date, time), null, null, null
        )
        var location: Location? = null
        if (cursor != null) {
            if (cursor.moveToFirst()) {
                location = Location(
                    cursor.getString(cursor.getColumnIndexOrThrow(COL_DATE)),
                    cursor.getString(cursor.getColumnIndexOrThrow(COL_TIME)),
                    cursor.getString(cursor.getColumnIndexOrThrow(COL_ADDRESS)),
                    cursor.getString(cursor.getColumnIndexOrThrow(COL_COUNTRY)),
                    cursor.getString(cursor.getColumnIndexOrThrow(COL_LOCALITY)),
                    cursor.getDouble(cursor.getColumnIndexOrThrow(COL_LATITUDE)),
                    cursor.getDouble(cursor.getColumnIndexOrThrow(COL_LONGITUDE))
                )
            }
            cursor.close()
        }
        return location
    }

    fun deleteOneRow(date: String, time: String) {
        val db = this.writableDatabase
        val result = db.delete(TABLE_NAME, "$COL_DATE = ? AND $COL_TIME = ?", arrayOf(date, time))
        if(result == -1){
            Toast.makeText(context, "Eliminazione fallita", Toast.LENGTH_SHORT).show()
        }else{
            Toast.makeText(context, "Eliminazione riuscita", Toast.LENGTH_SHORT).show()
        }
    }

    fun deleteAll() {
        val db: SQLiteDatabase = this.writableDatabase
        try {
            db.execSQL("DELETE FROM $TABLE_NAME")
            Toast.makeText(context, "Eliminazione riuscita", Toast.LENGTH_LONG).show()
        } catch (e: SQLException) {
            Toast.makeText(context, "Eliminazione fallita", Toast.LENGTH_LONG).show()
        } finally {
            db.close()
        }
    }
}
