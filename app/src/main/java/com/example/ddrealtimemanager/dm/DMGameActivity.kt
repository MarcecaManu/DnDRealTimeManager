package com.example.ddrealtimemanager.dm

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.ddrealtimemanager.R
import kotlinx.android.synthetic.main.activity_dmgame.*

class DMGameActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_dmgame)

        btnContinue.setOnClickListener {
            startActivity(Intent(this, GameListActivity::class.java))
        }

        btnNewGame.setOnClickListener {
            startActivity(Intent(this, NewGameActivity::class.java))
        }
    }
}