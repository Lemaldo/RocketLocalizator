package com.example.rocketlocalizator

import android.content.ContentValues
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.example.rocketlocalizator.Database.DBHelper
import java.io.OutputStream
import java.util.*


private const val REQUEST_CODE = 42

class FoundRocket : AppCompatActivity() {

    var imageView: ImageView? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_found_rocket)

        val backButton = findViewById<Button>(R.id.button3)

        val shareButton = findViewById<Button>(R.id.button2)

        val imageButton = findViewById<Button>(R.id.imageButton)

        var scoreLabel = findViewById<TextView>(R.id.scoreText)

        var login = ""
        val intentExtras = intent.extras
        if (intentExtras != null) {
            login = intentExtras.getString("LOGIN").toString()
        }

        val db = DBHelper(this)

        scoreLabel.text = "Znalazleś rakiete w " + db.getScore(login)

        imageView = findViewById(R.id.imageView)

        if (ActivityCompat.checkSelfPermission(
                this,
                android.Manifest.permission.CAMERA
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                this, arrayOf(android.Manifest.permission.CAMERA), REQUEST_CODE
            )
        }



        backButton.setOnClickListener() {

            val intent = Intent(this, HistoryAndStatistics::class.java)
            startActivity(intent)
            finish()
            onStop()
        }

        imageButton.setOnClickListener() {
            val takePictureIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
            startActivityForResult(takePictureIntent, REQUEST_CODE)

        }

        shareButton.setOnClickListener() {
            val sharingIntent = Intent(Intent.ACTION_SEND)
            sharingIntent.type = "text/plain"
            sharingIntent.putExtra(Intent.EXTRA_TEXT, "Rakieta znaleziona w czasie " + db.getScore(login))
            startActivity(Intent.createChooser(sharingIntent, "Share via"))
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(requestCode == REQUEST_CODE){
            val pic:Bitmap? = data?.getParcelableExtra<Bitmap>("data")
            imageView?.setImageBitmap(pic)
            val fos: OutputStream
            try{
                if(Build.VERSION.SDK_INT >=  Build.VERSION_CODES.Q){
                    val resolver = contentResolver
                    val contentValues = ContentValues()
                    contentValues.put(MediaStore.MediaColumns.DISPLAY_NAME,"Image_" + ".jpg")
                    contentValues.put(MediaStore.MediaColumns.MIME_TYPE, "image/jpg")
                    contentValues.put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_PICTURES)
                    val imageUri = resolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,contentValues)
                    fos = resolver.openOutputStream(Objects.requireNonNull(imageUri)!!)!!
                    if (pic != null) {
                        pic.compress(Bitmap.CompressFormat.JPEG, 100, fos)
                    }
                    Objects.requireNonNull<OutputStream?>(fos)
                    if (pic != null) {

                    }


                }
            } catch (e: java.lang.Exception){
                Toast.makeText(this,"Image not saved",Toast.LENGTH_LONG).show()
            }

        }

    }






}