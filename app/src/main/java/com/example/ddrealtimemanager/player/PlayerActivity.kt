package com.example.ddrealtimemanager.player

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.ddrealtimemanager.R
import com.example.ddrealtimemanager.player.real_time.PlayerRealTimeGameActivity
import com.example.ddrealtimemanager.shared.CharactersCardListActivity
import com.example.ddrealtimemanager.shared.CharactersListActivity
import kotlinx.android.synthetic.main.activity_player.*

class PlayerActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_player)

        btn_player_characters.setOnClickListener {
            startActivity(Intent(this, CharactersCardListActivity::class.java))
        }

        btn_player_join.setOnClickListener {
            startActivity(Intent(this, PlayerRealTimeGameActivity::class.java))
        }


    }
}