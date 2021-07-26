package com.example.rocketlocalizator.Database



import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.os.Build
import androidx.annotation.RequiresApi
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit



class DBHelper(context: Context):SQLiteOpenHelper(context, DATABASE_NAME,
    null, DATABASE_VER
) {
    companion object {
        private val DATABASE_VER = 2
        private val DATABASE_NAME = "Users.db"

        //Table
        private val USERS_TABLE_NAME = "Users"
        private val COL_ID = "Id"
        private val COL_LOGIN = "Login"
        private val COL_SCORE = "Score"



        private val FLIGHTS_TABLE_NAME = "Flights"
        private val COL_ID_FLIGHT = "Id_flights"
        private val COL_LATITUDE = "Latitude"
        private val COL_LONGITUDE = "Longitude"
        private val COL_TIMESTAMP = "Timestamp"

    }


    override fun onCreate(db: SQLiteDatabase?) {
        val CREATE_TABLE_USERS =
            ("CREATE TABLE $USERS_TABLE_NAME ($COL_ID INTEGER PRIMARY KEY AUTOINCREMENT, $COL_LOGIN TEXT, $COL_SCORE TEXT)")
        db!!.execSQL(CREATE_TABLE_USERS)

        val CREATE_TABLE_FLIGHTS =
            ("CREATE TABLE $FLIGHTS_TABLE_NAME ($COL_ID_FLIGHT INTEGER PRIMARY KEY AUTOINCREMENT, $COL_LATITUDE DOUBLE, $COL_LONGITUDE DOUBLE, $COL_TIMESTAMP TEXT")
        db!!.execSQL(CREATE_TABLE_FLIGHTS)
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        db!!.execSQL("DROP TABLE IF EXISTS $USERS_TABLE_NAME")
        db!!.execSQL("DROP TABLE IF EXISTS $FLIGHTS_TABLE_NAME")
        onCreate(db!!)
    }


    fun addUser(userName: String) {


        val db = this.writableDatabase
        val values = ContentValues()
        values.put(COL_LOGIN, userName)

        db.insert(USERS_TABLE_NAME, null, values)
        db.close()
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun addLL(idFlight: Int, latitude: Double, longitude: Double ){
        val db = this.writableDatabase

        val values = ContentValues()

        values.put(COL_ID_FLIGHT,idFlight)
        values.put(COL_LATITUDE, latitude)
        values.put(COL_LONGITUDE, longitude)
        val now = LocalDateTime.now().toString()
        val formatter = DateTimeFormatter.ofPattern("%H:%M:%S")
        val dateFormatted = now.format(formatter)
        values.put(COL_TIMESTAMP, dateFormatted)

        db.insert(FLIGHTS_TABLE_NAME, null, values)
        db.close()


    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun addFound(idFlight: Double, login: String){
        val selectQuery = "SELECT $COL_TIMESTAMP FROM $FLIGHTS_TABLE_NAME WHERE $COL_ID_FLIGHT = '$idFlight'"
        val db = this.writableDatabase
        val cursor = db.rawQuery(selectQuery, null)
        if (cursor.moveToFirst()) {
            val time = cursor.getString(cursor.getColumnIndex(COL_TIMESTAMP)).toString()

            val db2 = this.writableDatabase
            val values2 = ContentValues()
            //TODO
            val now = LocalDateTime.now().toString()
            val formatter = DateTimeFormatter.ofPattern("%H:%M:%S")
            val dateFormatted = now.format(formatter)

   
            val score = "$dateFormatted - $time"
            values2.put(COL_LOGIN, login)
            values2.put(COL_SCORE, score)

            db2.insert(USERS_TABLE_NAME, null, values2)
            db2.close()
        }
    }

    fun testUserInDB(userName: String): Boolean {
        val selectQuery = "SELECT * FROM $USERS_TABLE_NAME WHERE $COL_LOGIN = '$userName'"
        val db = this.writableDatabase
        val cursor = db.rawQuery(selectQuery, null)
        if (cursor.moveToFirst()) {
            if (userName == cursor.getString(cursor.getColumnIndex(COL_LOGIN))) {
                return true
            }
        }
        return false
    }

    fun getLocationFromId(id: Double) : Pair<Double, Double> {
        var latitude: Double = 0.0
        var longitude: Double = 0.0
        val selectQuery = "SELECT * FROM $FLIGHTS_TABLE_NAME WHERE $COL_ID_FLIGHT = '$id'"
        val db = this.writableDatabase
        val cursor = db.rawQuery(selectQuery, null)
        if (cursor.moveToFirst()) {
            latitude = cursor.getDouble(cursor.getColumnIndex(COL_LATITUDE))
            longitude = cursor.getDouble(cursor.getColumnIndex(COL_LONGITUDE))
        }
        return Pair(latitude,longitude)
    }


}


