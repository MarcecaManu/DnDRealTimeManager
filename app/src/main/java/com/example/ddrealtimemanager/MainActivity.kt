package com.example.ddrealtimemanager

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.core.text.isDigitsOnly
import com.example.ddrealtimemanager.dm.DMActivity
import com.example.ddrealtimemanager.player.PlayerActivity
import com.example.ddrealtimemanager.shared.DBHelper
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)


        val database = DBHelper(this)

        //database.resetDatabase()

        btnDM.setOnClickListener {
            startActivity(Intent(this, DMActivity::class.java))
        }

        btnPlayer.setOnClickListener {
            startActivity(Intent(this, PlayerActivity::class.java))
        }

    }
}