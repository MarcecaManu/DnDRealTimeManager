package com.example.ddrealtimemanager.dm

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.example.ddrealtimemanager.R
import com.example.ddrealtimemanager.dm.real_time.DMRealTimeGameActivity
import com.example.ddrealtimemanager.shared.DBHelper
import com.example.ddrealtimemanager.shared.FireBaseHelper
import com.example.ddrealtimemanager.shared.Game
import com.example.ddrealtimemanager.shared.Utils
import kotlinx.android.synthetic.main.activity_new_game.*
import kotlinx.android.synthetic.main.activity_new_game.btnGameCreationBack

class NewGameActivity : AppCompatActivity() {


    var gameId: Int = -1
    val database = DBHelper(this)
    val firebase = FireBaseHelper()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_new_game)

        val extras = intent.extras
        if (extras != null) {
            gameId = extras.getInt("gameId")
            etGameCreationName.setText(extras.getString("gameName"))
            etGameCreationSubtitle.setText(extras.getString("gameSubtitle"))
            etGameCreationDescr.setText(extras.getString("gameDescription"))
            etGameCreationPassword.setText(extras.getString("gamePassword"))

            btnGameCreationStart.setText("Save changes")
        }

        btnGameCreationStart.setOnClickListener {

            //Save the game in the database
            //Check if a name was inserted
            if (!etGameCreationName.text.toString().isBlank()) {
                saveGame(gameId)


                if(gameId != -1){   //If the game is being edited, interrupt the activity
                    finish()
                }
                else {          //Else, this is a new game and a new instance must be created in the firebase database
                    val firebaseId = saveGameInFirebase()
                    startNewGame(firebaseId)
                }
            } else {
                Toast.makeText(this, "Insert a title!", Toast.LENGTH_SHORT).show()
            }

        }

        btnGameCreationBack.setOnClickListener {
            finish()
        }
    }


    fun saveGame(gameId: Int) {
        var err: String = ""

        //character length check

        val gameName = Utils().polishString(etGameCreationName.text.toString(), database.MAX_LENGTH_GAME_NAME)
        if (gameName == false) err = "Game name"

        val gameSubtitle = Utils().polishString(etGameCreationSubtitle.text.toString(), database.MAX_LENGTH_GAME_SUBTITLE)
        if (gameSubtitle == false) err = "Game subtitle"

        val gameDescription = Utils().polishString(etGameCreationDescr.text.toString(), database.MAX_LENGTH_GAME_DESCRIPTION)
        if (gameDescription == false) err = "Game description"

        val gamePassword = Utils().polishString(etGameCreationPassword.text.toString(), database.MAX_LENGTH_GAME_PASSWORD)
        if (gamePassword == false) err = "Game password"


        ////
        if (err.isBlank()) {     //err indicates the field where too many characters were found


            if (gameId == -1) {  //Invalid gameId -> this is a new game
                database.writeNewGame(
                    gameName.toString(),
                    gameSubtitle.toString(),
                    gameDescription.toString(),
                    gamePassword.toString(),
                    "",
                    ""
                )
                Toast.makeText(this, "New game was saved successfully!", Toast.LENGTH_SHORT)
                    .show()


            } else {  //valid gameId -> the game is being edited

                database.editGame(
                    gameId,
                    gameName.toString(),
                    gameSubtitle.toString(),
                    gameDescription.toString(),
                    gamePassword.toString(),
                    ""
                )
                Toast.makeText(this, "The game was modified successfully!", Toast.LENGTH_SHORT)
                    .show()

                val game = database.getGame(gameId)
                firebase.fbUpdateGame(game.firebaseId, game)

                finish()

            }
        } else {    //A field had too many characters

            var maxLength: Int = 0
            when (err) {
                "Game name" -> maxLength = database.MAX_LENGTH_GAME_NAME
                "Game subtitle" -> maxLength = database.MAX_LENGTH_GAME_SUBTITLE
                "Game description" -> maxLength = database.MAX_LENGTH_GAME_DESCRIPTION
                "Game password" -> maxLength = database.MAX_LENGTH_GAME_PASSWORD
            }

            Toast.makeText(
                this,
                "${err}'s text is too long! Max $maxLength characters",
                Toast.LENGTH_SHORT
            )
                .show()
        }
    }

    //Saves the game in the firebase database and binds the firebase game instance with the database game instance
    fun saveGameInFirebase(): String{

        //Get the game's id generated by the database
        val retrievedGameId = database.getLastId()

        val game = database.getGame(retrievedGameId)

        //Create the game and store the game id in firebaseId
        val firebaseId = firebase.fbCreateNewGame(game)

        //Associate the game created in firebase with the game stored in the database
        database.setFirebaseId(retrievedGameId, firebaseId)

        return firebaseId
    }

    fun startNewGame(firebaseId: String){

        val intent = Intent(this, DMRealTimeGameActivity::class.java)
        intent.putExtra("newGame", true)
        intent.putExtra("fbGameId", firebaseId)

        startActivity(intent)

        finish()
    }

}