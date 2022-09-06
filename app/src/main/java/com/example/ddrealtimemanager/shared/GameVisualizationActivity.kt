package com.example.ddrealtimemanager.shared

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.example.ddrealtimemanager.R
import com.example.ddrealtimemanager.dm.NewGameActivity
import com.example.ddrealtimemanager.dm.real_time.DMRealTimeGameActivity
import com.example.ddrealtimemanager.player.real_time.PlayerRealTimeGameActivity
import kotlinx.android.synthetic.main.activity_game_visualization.*

class GameVisualizationActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_game_visualization)

        val db = DBHelper(this)

        val isDm: Boolean

        val extras = intent.extras

        val gameId = extras!!.getInt("gameId")

        val isCloud = extras!!.getBoolean("cloud")

        var game: Game?

        if(!isCloud) {
            game = db.getGame(gameId)

            isDm = true

        }else{
            val fbGameId = extras!!.getString("fbGameId")
            Log.v("CHECKVALUE", fbGameId!!)

            val name = extras!!.getString("name")
            val subtitle = extras!!.getString("subtitle")
            val description = extras!!.getString("description")
            val image = extras!!.getString("image")

            game = Game(-1, name!!, subtitle!!, description!!, image!!, fbGameId!!)



            isDm = false

            btn_game_visualization_start.text = "Join"
            fab_game_visualization_delete.visibility = View.GONE
            fab_game_visualization_edit.visibility = View.GONE
        }

            tv_game_visualization_title.text = game?.name
            tv_game_visualization_subtitle.text = game?.subtitle
            tv_game_visualization_description.text = game?.description

            val requestOptions = RequestOptions()
                .placeholder(R.drawable.fantasy_standard_bg)
                .error(R.drawable.fantasy_standard_bg)

            Glide.with(this)
                .applyDefaultRequestOptions(requestOptions)
                .load(game?.image)
                .into(game_visualization_image)



            btn_game_visualization_start.setOnClickListener {
                if(isDm) {
                    val intent = Intent(this, DMRealTimeGameActivity::class.java)
                    intent.putExtra("newGame", false)
                    intent.putExtra("fbGameId", game!!.firebaseId)
                    startActivity(intent)
                }else{
                    //It's a player, join

                    //Get the fbCharId and charId connected to id
                    Log.v("CHECKVALUEBEFOREERROR", game!!.firebaseId)
                    val relatedCharIds = db.getPlayerInfoForGame(game!!.firebaseId)

                    val fbCharId = relatedCharIds?.first        //fbCharId
                    val charId = relatedCharIds?.second         //charId

                    val intent = Intent(this, PlayerRealTimeGameActivity::class.java)
                    intent.putExtra("fbGameId", game!!.firebaseId)
                    intent.putExtra("fbCharId", fbCharId)
                    intent.putExtra("charId", charId)
                    startActivity(intent)
                    finish()
                }
            }


            fab_game_visualization_edit.setOnClickListener {
                val intent = Intent(this, NewGameActivity::class.java)


                intent.putExtra("gameId", game!!.id)
                intent.putExtra("gameName", game!!.name)
                intent.putExtra("gameSubtitle", game!!.subtitle)
                intent.putExtra("gameDescription", game!!.description)
                intent.putExtra("gameImage", game!!.image)

                startActivity(intent)
                finish()

            }

            fab_game_visualization_delete.setOnClickListener {
                val adb = AlertDialog.Builder(this@GameVisualizationActivity)
                adb.setTitle("Delete ${game!!.name}?")
                adb.setMessage("Are you sure you want to delete your game?")
                adb.setNegativeButton("Cancel", null)
                adb.setPositiveButton("I am!") { dialog, which ->
                    val success = db.deleteGame(gameId)
                    if (success) {

                        FireBaseHelper.fbDeleteGame(game!!.firebaseId)

                        Toast.makeText(
                            this,
                            "The game was deleted successfully",
                            Toast.LENGTH_SHORT
                        ).show()
                    } else {
                        Toast.makeText(
                            this,
                            "ERROR: The game could not be deleted",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                    Thread.sleep(1000)
                    finish()
                }
                adb.show()
                true
            }

    }
}