package com.example.rocketlocalizator



import android.content.DialogInterface
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import androidx.appcompat.app.AlertDialog
import com.example.rocketlocalizator.Database.DBHelper

var currentLogin: String = ""

class LogOrRegister : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_log_or_register)


        val signInButton = findViewById<Button>(R.id.button4)

        val signUpButton = findViewById<Button>(R.id.button5)

        val goToLeaderboard = findViewById<Button>(R.id.button7)

        val textEntered = findViewById<EditText>(R.id.editTextTextPersonName2)

        signInButton.setOnClickListener() {
            if (textEntered.length() > 0) {
                val login = textEntered.text
                //check if user exist and add
                val db =   DBHelper(this)
                if(db.testUserInDB(login.toString())){
                    messageScreen("DONE", "WELCOME ${login.toString()}")
                    currentLogin = login.toString()
                    val intent = Intent(this, HistoryAndStatistics::class.java)
                    intent.putExtra("LOGIN", currentLogin)

                    startActivity(intent)
                    finish()
                    onStop()
                } else {
                    messageScreen("ERROR", "This user doesnt exist. Create new user")
                }
                textEntered.text.clear()
            } else {
                messageScreen("ERROR", "Write a login name")
            }
        }

        signUpButton.setOnClickListener(){
            if (textEntered.length() > 0){
                val login = textEntered.text
                // create user
                // check if user exist
                val db =   DBHelper(this)
                if(db.testUserInDB(login.toString())){
                    messageScreen("ERROR","This user already exists")
                } else {
                    db.addUser(login.toString())
                    messageScreen("Sign up went successfully", "Sing in with this login to play")
                }
            } else {
                messageScreen("ERROR", "Write a login name")
            }
        }




    }


    fun messageScreen(title: String, text: String) {
        val builder = AlertDialog.Builder(this@LogOrRegister)
        builder.setTitle(title)
        builder.setMessage(text)

        builder.setPositiveButton("OK") { dialogInterface: DialogInterface, i: Int ->
        }

        val dialog: AlertDialog = builder.create()
        dialog.show()
        dialog.dismiss()
    }



}