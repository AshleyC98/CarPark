package com.example.carpark

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView

class LocationAdapter(val context: Context, val locationList: List<Location>
) : RecyclerView.Adapter<LocationAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.activity_location_adapter, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int = locationList.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val location: Location = locationList[position]
        holder.bind(location)

    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val dateTextView: TextView = itemView.findViewById(R.id.tv_date)
        private val timeTextView: TextView = itemView.findViewById(R.id.tv_time)
        private val addressTextView: TextView = itemView.findViewById(R.id.tv_address_txt)
        private val cardView: ConstraintLayout = itemView.findViewById(R.id.cardView)

        fun bind(location: Location) {
            dateTextView.text = location.date
            timeTextView.text = location.time
            addressTextView.text = location.address

            cardView.setOnClickListener {
                val intent = Intent(itemView.context, LocationDetails::class.java).apply {
                    putExtra("DATE", location.date)
                    putExtra("TIME", location.time)
                    putExtra("ADDRESS", location.address)
                }
                (itemView.context as Activity).startActivityForResult(intent, 1)
            }
        }
    }
}
