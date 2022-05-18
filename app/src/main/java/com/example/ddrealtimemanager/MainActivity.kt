package com.example.ddrealtimemanager

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        btnDM.setOnClickListener {
            startActivity(Intent(this, DMActivity::class.java))
        }

        btnPlayer.setOnClickListener {
            startActivity(Intent(this, PlayerActivity::class.java))
        }


    }
}