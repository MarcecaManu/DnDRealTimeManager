package com.example.ddrealtimemanager.dm

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.ddrealtimemanager.R
import com.example.ddrealtimemanager.shared.FireBaseHelper
import kotlinx.android.synthetic.main.activity_new_game.*

class NewGameActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_new_game)


        btnGameCreationStart.setOnClickListener {

            //Save the game in the database


            //Start the game
        }
    }
}