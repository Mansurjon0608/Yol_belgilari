package com.example.yolbelgilari

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.navigation.findNavController

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        title = "Yo'l belgilari"

    }


    override fun onSupportNavigateUp(): Boolean {
        return super.onSupportNavigateUp()
        findNavController( R.id.my_navigation_host).navigateUp()
    }

}