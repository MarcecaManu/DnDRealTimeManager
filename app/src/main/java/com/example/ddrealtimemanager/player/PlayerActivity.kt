package com.example.ddrealtimemanager.player

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.ddrealtimemanager.R
import com.example.ddrealtimemanager.shared.CharactersListActivity
import kotlinx.android.synthetic.main.activity_player.*

class PlayerActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_player)

        btnChar.setOnClickListener {
            startActivity(Intent(this, CharactersListActivity::class.java))
        }

        btnJoin.setOnClickListener {
            startActivity(Intent(this, PlayerGameActivity::class.java))
        }


    }
}