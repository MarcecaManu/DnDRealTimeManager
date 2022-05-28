package com.example.ddrealtimemanager.dm

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.ddrealtimemanager.R
import com.example.ddrealtimemanager.shared.CharactersListActivity
import kotlinx.android.synthetic.main.activity_player.*

class DMActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_dmactivity)

        btnNewGame.setOnClickListener {
            startActivity(Intent(this, DMGameActivity::class.java))
        }

        btnContinue.setOnClickListener {
            startActivity(Intent(this, CharactersListActivity::class.java))
        }
    }
}