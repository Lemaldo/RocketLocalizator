package com.example.rocketlocalizator.Database



import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.util.Log
import com.example.rocketlocalizator.LogOrRegister

import kotlin.random.Random


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
            ("CREATE TABLE $FLIGHTS_TABLE_NAME ($COL_ID_FLIGHT INTEGER PRIMARY KEY AUTOINCREMENT, $COL_LATITUDE DOUBLE, $COL_LONGITUDE DOUBLE, $COL_TIMESTAMP DATE)")
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

    fun addLL(idFlight: Int, latitude: Double, longitude: Double ){
        val db = this.writableDatabase

        val values = ContentValues()

        values.put(COL_ID_FLIGHT,idFlight)
        values.put(COL_LATITUDE, latitude)
        values.put(COL_LONGITUDE, longitude)


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


}


