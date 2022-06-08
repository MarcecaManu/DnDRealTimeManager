package com.example.ddrealtimemanager.dm

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import com.example.ddrealtimemanager.R
import com.example.ddrealtimemanager.shared.CharacterCreationActivity
import com.example.ddrealtimemanager.shared.DBHelper
import kotlinx.android.synthetic.main.activity_character_visualization.*
import kotlinx.android.synthetic.main.activity_character_visualization.fabDelete
import kotlinx.android.synthetic.main.activity_character_visualization.fabEdit
import kotlinx.android.synthetic.main.activity_game_visualization.*

class GameVisualizationActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_game_visualization)

        val db = DBHelper(this)

        val extras = intent.extras
        //if(extras != null){
        val gameId = extras!!.getInt("gameId")

        val game = db.getGame(gameId)

        //TODO implement image management

        tv_game_visualization_title.text = game.name
        tv_game_visualization_subtitle.text = game.subtitle
        tv_game_visualization_description.text = game.description
        // }


        btn_game_visualization_start.setOnClickListener {
            //TODO Start game
        }


        fab_game_visualization_edit.setOnClickListener{
            val intent = Intent(this, NewGameActivity::class.java)

            //TODO implement image management

            intent.putExtra("gameId", game.id)
            intent.putExtra("gameName", game.name)
            intent.putExtra("gameSubtitle", game.subtitle)
            intent.putExtra("gameDescription", game.description)
            intent.putExtra("gamePassword", game.password)
            intent.putExtra("gameImage", game.image)

            startActivity(intent)
            finish()

        }

        fab_game_visualization_delete.setOnClickListener{
            val adb = AlertDialog.Builder(this@GameVisualizationActivity)
            adb.setTitle("Delete ${game.name}?")
            adb.setMessage("Are you sure you want to delete your game?")
            adb.setNegativeButton("Cancel", null)
            adb.setPositiveButton("I am!"){ dialog, which ->
                val success = db.deleteGame(gameId)
                if(success){
                    Toast.makeText(this, "The game was deleted successfully", Toast.LENGTH_SHORT).show()
                }else{
                    Toast.makeText(this, "ERROR: The game could not be deleted", Toast.LENGTH_SHORT).show()
                }
                Thread.sleep(1000)
                finish()
            }
            adb.show()
            true
        }

    }
}