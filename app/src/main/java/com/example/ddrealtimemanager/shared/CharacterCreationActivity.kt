package com.example.ddrealtimemanager.shared

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.example.ddrealtimemanager.R
import kotlinx.android.synthetic.main.activity_character_creation.*

/* This activity contains the character creation process.*/

class CharacterCreationActivity : AppCompatActivity() {

    var charId: Int = -1
    val database = DBHelper(this)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_character_creation)

        val extras = intent.extras
        if(extras != null){
            charId = extras.getInt("charId")
            etNameChar.setText(extras.getString("name"))
            etRaceChar.setText(extras.getString("race"))
            etClassChar.setText(extras.getString("class"))
            etDescrChar.setText(extras.getString("description"))
        }

        btnSaveChar.setOnClickListener {

            //Check if a name was inserted
            if(!etNameChar.text.toString().isBlank()){
                saveCharacter(charId)
            }
            else{
                Toast.makeText(this, "Insert a name!", Toast.LENGTH_SHORT).show()
            }
        }

        btnCharBack.setOnClickListener {
            finish()
        }
    }


    /* This function saves or updates the character in the database. The application
    *  checks if the character is being edited or not based on the presence of a valid
    * character id (so the character already exists), or an invalid one (charId = -1, a new character)
    * */

    fun saveCharacter(charId: Int){
        var err: String = ""

        //character length check

        val name = Utils().
        polishString(etNameChar.text.toString(), database.MAX_LENGTH_CHAR_NAME)
        if(name == false) err = "Name"

        val race = Utils().
        polishString(etRaceChar.text.toString(), database.MAX_LENGTH_CHAR_RACE)
        if(race == false) err = "Race"

        val clas = Utils().
        polishString(etClassChar.text.toString(), database.MAX_LENGTH_CHAR_CLASS)
        if(clas == false) err = "Class"

        val descr = Utils().
        polishString(etDescrChar.text.toString(), database.MAX_LENGTH_CHAR_DESCRIPTION)
        if(descr == false) err = "Description"

        val image = Utils().polishString(etImgChar.text.toString(), database.MAX_LENGTH_CHAR_DESCRIPTION)

        ////
        if(err.isBlank()) {     //err indicates the field where too many characteres were found

            val char: Character = Character(charId, name.toString(), race.toString(), clas.toString(), descr.toString(), image.toString())

            if(charId == -1) {  //Invalid id -> this is a new character
                database.writeNewCharacter(char)
                Toast.makeText(this, "New character was saved successfully!", Toast.LENGTH_SHORT)
                    .show()
                finish()

            }else{  //valid charId -> the character is being edited

                database.editCharacter(char)
                Toast.makeText(this, "The character was modified successfully!", Toast.LENGTH_SHORT)
                    .show()
                finish()

            }
        }
        else{    //A field had too many characters

            var maxLength: Int = 0
            when(err){
                "Name" -> maxLength = database.MAX_LENGTH_CHAR_NAME
                "Race" -> maxLength = database.MAX_LENGTH_CHAR_RACE
                "Class" -> maxLength = database.MAX_LENGTH_CHAR_CLASS
                "Description" -> maxLength = database.MAX_LENGTH_CHAR_DESCRIPTION
            }

            Toast.makeText(this, "${err}'s text is too long! Max $maxLength characters", Toast.LENGTH_SHORT)
                .show()
        }
    }

}