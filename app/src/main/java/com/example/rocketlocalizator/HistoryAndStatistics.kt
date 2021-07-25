package com.example.rocketlocalizator

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import com.example.rocketlocalizator.Database.DBHelper
class HistoryAndStatistics : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_history_and_statistics)

        val mapButton = findViewById<Button>(R.id.button)

        mapButton.setOnClickListener(){

            Log.d("test1","here")
            // TODO:
            // generate Lat and Long and ID
            // latitde -90:0 South  0:90 North
            // longitude -180:0 West 0:180 East


            val idFlight = 1

            val db = DBHelper(this)
            val latitude = 40.7127281
            val longitude = -74.0060152
            db.addLL(idFlight, latitude, longitude)
            val intent = Intent(this, MapsActivity::class.java)
            intent.putExtra("ID_FLIGHT", idFlight)
            startActivity(intent)
            finish()
            onStop()
        }
    }
}