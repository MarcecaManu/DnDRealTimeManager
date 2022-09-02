package com.example.ddrealtimemanager.dm

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.ddrealtimemanager.R
import com.example.ddrealtimemanager.shared.DBHelper
import com.example.ddrealtimemanager.shared.GameListAdapter
import com.example.ddrealtimemanager.shared.GameVisualizationActivity
import kotlinx.android.synthetic.main.activity_game_list.*


class GameListActivity : AppCompatActivity() {

    private lateinit var gameAdapter: GameListAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_game_list)

        refreshList()

        game_list_view.setOnItemClickListener { parent, view, position, id ->
            val intent = Intent(this, GameVisualizationActivity::class.java)
            val gameId = gameAdapter.getItem(position).id
            intent.putExtra("gameId", gameId)
            intent.putExtra("cloud", false)
            startActivity(intent)
        }

    }

    override fun onResume() {
        super.onResume()

        refreshList()
    }

    private fun refreshList(){
        val db = DBHelper(this)
        gameAdapter = GameListAdapter(this, db.getStoredGames())
        game_list_view.adapter = gameAdapter
    }

}