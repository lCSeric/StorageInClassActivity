package com.example.networkapp

import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import com.android.volley.Request
import com.android.volley.RequestQueue
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.squareup.picasso.Picasso
import org.json.JSONObject
import java.io.BufferedReader
import java.io.File
import java.io.FileOutputStream
import java.io.FileReader
import java.io.IOException
import java.lang.Exception
import java.lang.StringBuilder
import java.util.prefs.Preferences

// TODO (1: Fix any bugs)
// TODO (2: Add function saveComic(...) to save and load comic info automatically when app starts)

private const val AUTO_SAVE_KEY = "auto_save"
class MainActivity : AppCompatActivity() {

    private lateinit var requestQueue: RequestQueue
    lateinit var titleTextView: TextView
    lateinit var descriptionTextView: TextView
    lateinit var numberEditText: EditText
    lateinit var showButton: Button
    lateinit var comicImageView: ImageView

    lateinit var preferences: SharedPreferences
    lateinit var file: File
    var internalFilename = "my_file"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        requestQueue = Volley.newRequestQueue(this)

        titleTextView = findViewById<TextView>(R.id.comicTitleTextView)
        descriptionTextView = findViewById<TextView>(R.id.comicDescriptionTextView)
        numberEditText = findViewById<EditText>(R.id.comicNumberEditText)
        showButton = findViewById<Button>(R.id.showComicButton)
        comicImageView = findViewById<ImageView>(R.id.comicImageView)

        preferences = getPreferences(MODE_PRIVATE)
        file = File(filesDir, internalFilename)



        showButton.setOnClickListener {
            downloadComic(numberEditText.text.toString())
        }
        if (file.exists()){
            loadComic()
        }
    }

    private fun downloadComic(comicId: String) {
        val url = "https://xkcd.com/$comicId/info.0.json"
        requestQueue.add(
            JsonObjectRequest(url, { showComic(it);saveComic(it)}, {
            })
        )
    }

    private fun showComic(comicObject: JSONObject) {
        titleTextView.text = comicObject.getString("title")
        descriptionTextView.text = comicObject.getString("alt")
        Picasso.get().load(comicObject.getString("img")).into(comicImageView)
    }

    private fun saveComic(comicObject: JSONObject) {
            try {
                val outputStream = FileOutputStream(file)
                outputStream.write(comicObject.toString().toByteArray())
                outputStream.close()
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }


    private fun loadComic() {

        try {
            val br = BufferedReader(FileReader(file))
            var line: String?
            val text = StringBuilder()
            while (br.readLine().also { line = it } != null) {
                Log.d("TEST", line.toString())
                text.append(line)
            }
            br.close()
            showComic(JSONObject(text.toString()))
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }
}