package com.example.rocketlocalizator

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import androidx.core.app.ActivityCompat
import com.example.rocketlocalizator.Database.DBHelper
import com.example.rocketlocalizator.databinding.ActivityMapsBinding
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices

import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions

class MapsActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var mMap: GoogleMap
    private lateinit var binding: ActivityMapsBinding



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)


        binding = ActivityMapsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)


        val finishButton = findViewById<Button>(R.id.finishButton)


        finishButton.setOnClickListener(){
            val idFlight = 1

            val db = DBHelper(this)
            val latitude = 40.7127281
            val longitude = -74.0060152
            //db.addLL(idFlight, latitude, longitude)
            val intent = Intent(this, FoundRocket::class.java)
            intent.putExtra("ID_FLIGHT", idFlight)
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
