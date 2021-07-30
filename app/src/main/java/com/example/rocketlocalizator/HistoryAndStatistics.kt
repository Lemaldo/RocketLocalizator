package com.example.rocketlocalizator

import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationManager
import android.os.Build
import android.os.Bundle
import android.os.Looper
import android.util.Log
import android.widget.Button
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.example.rocketlocalizator.Database.DBHelper
import com.google.android.gms.location.*
import java.util.*
import kotlin.properties.Delegates

fun getLocationInLatLngRad(radiusInMeters: Double, currentLocation: Location): Location {
    val x0: Double = currentLocation.getLongitude()
    val y0: Double = currentLocation.getLatitude()
    val random = Random()

    // Convert radius from meters to degrees.
    val radiusInDegrees = radiusInMeters / 111320f

    // Get a random distance and a random angle.
    val u: Double = random.nextDouble()
    val v: Double = random.nextDouble()
    val w = radiusInDegrees * Math.sqrt(u)
    val t = 2 * Math.PI * v
    // Get the x and y delta values.
    val x = w * Math.cos(t)
    val y = w * Math.sin(t)

    // Compensate the x value.
    val new_x = x / Math.cos(Math.toRadians(y0))
    val foundLatitude: Double
    val foundLongitude: Double
    foundLatitude = y0 + y
    foundLongitude = x0 + new_x
    val copy = Location(currentLocation)
    copy.setLatitude(foundLatitude)
    copy.setLongitude(foundLongitude)
    return copy
}



class HistoryAndStatistics : AppCompatActivity(){
    private  var permissionID = 10
    lateinit var fusedLocationProviderClient: FusedLocationProviderClient
    lateinit var locationRequest: LocationRequest

    lateinit var rock_loc:Location

    var lat by Delegates.notNull<Double>()
    var long by Delegates.notNull<Double>()

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {

        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this)
//        getLastLocation()


        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_history_and_statistics)

        val mapButton = findViewById<Button>(R.id.button)

        var login = ""
        val intentExtras = intent.extras
        if (intentExtras != null) {
            login = intentExtras.getString("LOGIN").toString()
        }

        mapButton.setOnClickListener(){

            // TODO:
            // generate Lat and Long and ID
            // latitude -90:0 South  0:90 North
            // longitude -180:0 West 0:180 East

//            getLastLocation()




            val db = DBHelper(this)
//            val idFlight = db.getIDFlight()

            var idFlight  = db.getIDFlight()+1

            messageDebug("1","!!!!")
            val rock_loc = Location("dummy")


            val r = Random()
            var rangeMin =49.29899
            var rangeMax = 54.79086

            rock_loc.latitude = rangeMin + (rangeMax - rangeMin) * r.nextDouble()

            rangeMin = 14.24712
            rangeMax = 23.89251
            rock_loc.longitude = rangeMin + (rangeMax - rangeMin) * r.nextDouble()

            var idmin = 200
            var idmax= 50000
            idFlight = idmin+(idmax-idmin) * r.nextInt()

            if (rock_loc != null) {
                db.addLL(idFlight, rock_loc.latitude,rock_loc.longitude)
                val intent = Intent(this, MapsActivity::class.java)
                intent.putExtra("ID_FLIGHT", idFlight)

                intent.putExtra("LOGIN", login)
                startActivity(intent)
                finish()
                onStop()
            }

        }
    }
    fun messageDebug(title: String, text: String){
        val builder = AlertDialog.Builder(this@HistoryAndStatistics)
        builder.setTitle(title)
        builder.setMessage(text)

        builder.setPositiveButton("OK"){ dialogInterface: DialogInterface, i: Int ->
        }

        val dialog: AlertDialog = builder.create()
        dialog.show()
        dialog.dismiss()
    }


    private fun getLastLocation(): Location?{
        var curloc:Location?
        curloc = null
        if(checkPermission()){
            if(isLocationEnabled()){
                fusedLocationProviderClient.lastLocation
                    .addOnSuccessListener { location : Location? ->
                        // Got last known location. In some rare situations this can be null.
                        getNewLocation()
                        curloc = location
                        curloc?.let { print(it.latitude) }
                    }

                }
                return curloc
            }
         else {
            requestPermission()
        }

        return curloc
    }

    private fun  getNewLocation(){
        locationRequest = LocationRequest()
        locationRequest.priority = LocationRequest.PRIORITY_HIGH_ACCURACY
        locationRequest.interval = 0
        locationRequest.fastestInterval = 0
        locationRequest.numUpdates = 2
        if (ActivityCompat.checkSelfPermission(
                this,
                android.Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                this,
                android.Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            return
        }
        fusedLocationProviderClient.requestLocationUpdates(
            locationRequest, locationCallback,
            Looper.myLooper()
        )
    }

    private val locationCallback = object : LocationCallback(){
        override fun onLocationResult(p0: LocationResult) {
            var lastLocation: Location = p0.lastLocation
            rock_loc = lastLocation
            rock_loc = getLocationInLatLngRad(300.0, rock_loc)

            messageScreen(
                "Coordinates",
                "Your Current Coordinates are :\nLat:" + lastLocation.latitude + "\nLong: " + lastLocation.longitude


            )

        }
    }
    fun messageScreen(title: String, text: String){
        val builder = AlertDialog.Builder(this@HistoryAndStatistics)
        builder.setTitle(title)
        builder.setMessage(text)

        builder.setPositiveButton("OK"){ dialogInterface: DialogInterface, i: Int ->
        }

        val dialog: AlertDialog = builder.create()
        dialog.show()
        dialog.dismiss()
    }

    private fun checkPermission():Boolean {
        if (
            ActivityCompat.checkSelfPermission(
                this,
                android.Manifest.permission.ACCESS_COARSE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED ||
            ActivityCompat.checkSelfPermission(
                this,
                android.Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ){
            return true
        }
        return false
    }

    private fun requestPermission(){
        ActivityCompat.requestPermissions(
            this,
            arrayOf(
                android.Manifest.permission.ACCESS_FINE_LOCATION,
                android.Manifest.permission.ACCESS_COARSE_LOCATION
            ), permissionID
        )
    }

    private fun isLocationEnabled():Boolean {
        val locationManager : LocationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) || locationManager.isProviderEnabled(
            LocationManager.NETWORK_PROVIDER
        )
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if(requestCode == permissionID){
            if(grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                Log.d("Permissions", "Permissions granted")
            }
        }
    }





}