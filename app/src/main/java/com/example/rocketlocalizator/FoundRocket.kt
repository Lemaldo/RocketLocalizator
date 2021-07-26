package com.example.rocketlocalizator

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import com.example.rocketlocalizator.Database.DBHelper

class FoundRocket : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_found_rocket)

        val backButton = findViewById<Button>(R.id.button3)

        val facebookButton = findViewById<Button>(R.id.button2)


        backButton.setOnClickListener(){

            val intent = Intent(this, HistoryAndStatistics::class.java)
            startActivity(intent)
            finish()
            onStop()
        }

        facebookButton.setOnClickListener(){

        }
    }
}