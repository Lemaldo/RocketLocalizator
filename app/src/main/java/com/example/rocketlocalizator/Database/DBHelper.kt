package com.example.rocketlocalizator.Database



import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit
import java.util.*


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
        val now = LocalDateTime.now()
        val formatter = DateTimeFormatter.ofPattern("hh:mm:ss")

        values.put(COL_TIMESTAMP, now.format(formatter))

        db.insert(FLIGHTS_TABLE_NAME, null, values)
        db.close()


    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun addFound(idFlight: Double, login: String){
        val selectQuery = "SELECT $COL_TIMESTAMP FROM $FLIGHTS_TABLE_NAME WHERE $COL_ID_FLIGHT = '$idFlight'"
        val db = this.writableDatabase
        val cursor = db.rawQuery(selectQuery, null)
        if (cursor.moveToFirst()) {
            val time = cursor.getString(cursor.getColumnIndex(COL_TIMESTAMP))

            val db2 = this.writableDatabase
            val values2 = ContentValues()

            val now = LocalDateTime.now()
            val formatter = DateTimeFormatter.ofPattern("hh:mm:ss")

            val score = calcScore(now.format(formatter),time.format(formatter))
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

    fun calcScore(now: String, then: String): String{
        val hoursNow = now.substring(0,2)
        val hoursThen = then.substring(0,2)

        val minuteNow = now.substring(3,5)
        val minuteThen = then.substring(3,5)

        val hoursInterval = (hoursNow.toInt() - hoursThen.toInt()) * 60
        val minutesInterval = minuteNow.toInt() - minuteThen.toInt()
        val score = hoursInterval + minutesInterval
        return "$score minutes"
    }

    fun getScore(userName: String): String? {
        val selectQuery = "SELECT $COL_SCORE FROM $USERS_TABLE_NAME WHERE $COL_LOGIN = '$userName'"
        val db = this.writableDatabase
        val cursor = db.rawQuery(selectQuery, null)
        if (cursor.moveToLast()) {
                return cursor.getString(cursor.getColumnIndex(COL_SCORE))

        }
        return "error"
    }





}


