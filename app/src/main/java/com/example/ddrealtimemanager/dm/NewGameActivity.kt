package com.example.ddrealtimemanager.dm

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.example.ddrealtimemanager.R
import com.example.ddrealtimemanager.shared.DBHelper
import com.example.ddrealtimemanager.shared.Utils
import kotlinx.android.synthetic.main.activity_new_game.*
import kotlinx.android.synthetic.main.activity_new_game.btnGameCreationBack

class NewGameActivity : AppCompatActivity() {


    var gameId: Int = -1
    val database = DBHelper(this)

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
        }

        btnGameCreationStart.setOnClickListener {

            //Save the game in the database
            //Check if a name was inserted
            if (!etGameCreationName.text.toString().isBlank()) {
                saveGame(gameId)
            } else {
                Toast.makeText(this, "Insert a title!", Toast.LENGTH_SHORT).show()
            }

            //Start the game
            //TODO()
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

            if (gameId == -1) {  //Invalid gameId -> this is a new character
                database.writeNewGame(
                    gameName.toString(),
                    gameSubtitle.toString(),
                    gameDescription.toString(),
                    gamePassword.toString(),
                    ""
                )
                Toast.makeText(this, "New game was saved successfully!", Toast.LENGTH_SHORT)
                    .show()
                finish()

            } else {  //valid gameId -> the character is being edited

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

}