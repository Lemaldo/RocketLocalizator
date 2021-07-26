package com.example.rocketlocalizator

import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import androidx.annotation.RequiresApi
import com.example.rocketlocalizator.Database.DBHelper
import com.example.rocketlocalizator.databinding.ActivityMapsBinding

import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import kotlin.math.log

class MapsActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var mMap: GoogleMap
    private lateinit var binding: ActivityMapsBinding



    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)


        binding = ActivityMapsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)


        val finishButton = findViewById<Button>(R.id.finishButton)


        finishButton.setOnClickListener(){

            var idFlight = 0.0
            var login = ""
            val intentExtras = intent.extras
            if (intentExtras != null) {
                idFlight = intentExtras.getInt("ID_FLIGHT").toDouble()
                login = intentExtras.getString("LOGIN").toString()

            }



            val db = DBHelper(this)

            db.addFound(idFlight, login)
            val intent = Intent(this, FoundRocket::class.java)
            startActivity(intent)
            finish()
            onStop()
        }

    }
    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap
        var idFlight = 0.0
        val intentExtras = intent.extras
        if (intentExtras != null) {
            idFlight = intentExtras.getInt("ID_FLIGHT").toDouble()
        }

        val db =   DBHelper(this)
        val (latitude, longitude) = db.getLocationFromId(idFlight)
        val sydney = LatLng(latitude, longitude)
        mMap.addMarker(MarkerOptions().position(sydney).title("Rocket"))
        mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney))

    }


}
