package com.example.ddrealtimemanager.player

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Adapter
import android.widget.ListView
import com.example.ddrealtimemanager.R
import com.example.ddrealtimemanager.shared.GameVisualizationActivity
import com.example.ddrealtimemanager.shared.*
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import kotlinx.android.synthetic.main.activity_player_games_list.*

class PlayerGamesListActivity : AppCompatActivity() {


    companion object{

        val gamesList: ArrayList<Game> = ArrayList()

        var currentAdapter: GameListAdapter? = null
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_player_games_list)

        val db = DBHelper(this)
        val gamesRef = FireBaseHelper.gamesRef

        refreshList()

        val gamesListRaw: ArrayList<Triple<String, String, Int>>

        gamesListRaw = db.getPlayerGames()


        gamesRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                gamesList.clear()

                gamesListRaw.forEach { localGame ->
                    snapshot.children.forEach { cloudGame ->
                        val foundFbId = cloudGame.key
                        if(foundFbId == localGame.first){
                            val name = cloudGame.child("name").getValue(String::class.java)
                            val subtitle = cloudGame.child("subtitle").getValue(String::class.java)
                            val description = cloudGame.child("description").getValue(String::class.java)
                            val image = cloudGame.child("image").getValue(String::class.java)

                            val game = Game(-1, name!!, subtitle!!, description!!, image!!, foundFbId)
                            gamesList.add(game)
                        }
                    }
                }

                refreshList()
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

        })





        player_games_listview.setOnItemClickListener{ parent, view, position, id ->

            val intent = Intent(this, GameVisualizationActivity::class.java)
            val fbGame = currentAdapter!!.getItem(position)
            intent.putExtra("fbGameId", fbGame.firebaseId)
            intent.putExtra("name", fbGame.name)
            intent.putExtra("subtitle", fbGame.subtitle)
            intent.putExtra("description", fbGame.description)
            intent.putExtra("image", fbGame.image)
            intent.putExtra("cloud", true)

            startActivity(intent)

        }



        player_games_fab_add.setOnClickListener{
            val intent =  Intent(this, CharactersCardListActivity::class.java)
            intent.putExtra("join", true)
            startActivity(intent)
        }

    }

    fun refreshList(){
        val newAdapter = GameListAdapter(this, gamesList)
        player_games_listview.adapter = newAdapter
        currentAdapter = newAdapter
    }
}