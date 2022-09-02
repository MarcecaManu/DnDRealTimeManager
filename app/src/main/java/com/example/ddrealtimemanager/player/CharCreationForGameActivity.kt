package com.example.ddrealtimemanager.player

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.InputType
import android.webkit.URLUtil
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.text.isDigitsOnly
import com.example.ddrealtimemanager.R
import com.example.ddrealtimemanager.shared.Character
import com.example.ddrealtimemanager.shared.DBHelper
import com.example.ddrealtimemanager.shared.FireBaseHelper
import com.example.ddrealtimemanager.shared.real_time.RT_Character
import kotlinx.android.synthetic.main.activity_char_creation_for_join.*

class CharCreationForGameActivity() : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_char_creation_for_join)

        val extras = intent.extras

        val charId = extras!!.getInt("charId")

        val db = DBHelper(this)

        val character: Character = db.getSpecificCharacter(charId)

        player_charcre_et_name.setText(character.name)
        player_charcre_et_imageurl.setText(character.image)

        player_charcre_et_level.transformationMethod = null
        player_charcre_et_armorclass.transformationMethod = null
        player_charcre_et_maxhp.transformationMethod = null
        player_charcre_et_initiative.transformationMethod = null

        player_charcre_btn_save.setOnClickListener{

            val name = player_charcre_et_name.text.toString().trim()
            val level = player_charcre_et_level.text.toString().trim()
            val ac = player_charcre_et_armorclass.text.toString().trim()
            val maxHp = player_charcre_et_maxhp.text.toString().trim()
            val initiative = player_charcre_et_initiative.text.toString().trim()
            val imageurl = player_charcre_et_imageurl.text.toString().trim()

            val errorList: ArrayList<String> = ArrayList()

            errorList.add(checkStatFieldValidity(name, "name"))
            errorList.add(checkStatFieldValidity(level, "level"))
            errorList.add(checkStatFieldValidity(ac, "armor class"))
            errorList.add(checkStatFieldValidity(maxHp, "max health points"))
            errorList.add(checkStatFieldValidity(initiative, "initiative"))
            errorList.add(checkStatFieldValidity(imageurl, "image"))

            var error = false

            errorList.forEach{
                if(it != ""){
                    error = true
                    Toast.makeText(this, it, Toast.LENGTH_SHORT).show()
                }
            }

            if(!error){

                val newRtCharacter: RT_Character = RT_Character("", name, character.race,
                    character.clas, character.desc, imageurl, maxHp.toInt(), maxHp.toInt(),
                    initiative.toInt(), ac.toInt(), level.toInt() )

                var fbGameId: String

                val adb = AlertDialog.Builder(this@CharCreationForGameActivity)
                adb.setTitle("Insert a game ID")

                val input: EditText = EditText(this)
                input.inputType = InputType.TYPE_CLASS_TEXT
                adb.setView(input)

                //adb.setMessage("This will not remove it from local storage.")

                adb.setNegativeButton("Cancel", null)
                adb.setPositiveButton("Done!"){ dialog, which ->

                    fbGameId = input.text.toString()
                    //fbGameId = "-NA_TWYGanLoOhIBil5J"

                    val result: Boolean? = FireBaseHelper.fbCheckGameExists(fbGameId)

                    var alreadyIn: Boolean = false

                    db.getPlayerGames().forEach {
                        if(it.first == fbGameId){
                            alreadyIn = true
                            return@forEach
                        }
                    }

                    if(result == null){
                        Toast.makeText(this, "Can't read lol", Toast.LENGTH_SHORT).show()
                    }else if(alreadyIn){
                        Toast.makeText(this, "You already have a character in this game!", Toast.LENGTH_SHORT).show()
                    }
                    else if(result && !alreadyIn){
                        //push character in the game and get the Id
                        val fbCharId = FireBaseHelper.fbPushCharacter(newRtCharacter, fbGameId)

                        db.playerJoinGame(fbGameId, fbCharId, charId)

                        val intent = Intent(this, PlayerGamesListActivity::class.java)
                        startActivity(intent)
                        finish()

                    }else{
                        Toast.makeText(this, "The game could not be found!", Toast.LENGTH_SHORT).show()
                    }

                }
                adb.show()
                true
            }

        }


    }

    private fun checkStatFieldValidity(field: String, type: String) : String {

        var result: String = ""

        //Check if the field contains a number, being it positive or negative, and if it's not an image
        if(!field.isBlank()){

            if((field.isDigitsOnly() || (field[0].equals('-') && field.substring(1,field.length).isDigitsOnly()) ) && type != "image") {
                when (type) {
                    "level" ->
                        if (field.toInt() < 1 || field.toInt() > 20) {
                            result = "The level field must be between 1 and 20!"
                        }

                    "hp" -> if(field.toInt() < 0){
                        result = "The health points field must be higher than 0!"
                    }
                }
            }else{

                if(type == "image"){
                    if(!field.isBlank() && !URLUtil.isValidUrl(field)){
                        result = "The image URL is invalid!"
                    }
                }else if(type == "name") {
                    if(field.isBlank()){
                        result = "The $type field must be filled!"
                    }else if(field.length > 15){
                        result = "The $type is too long!"
                    }
                }
            }
        }else{
            result = "The $type field must be filled!"
        }

        return result
    }
}