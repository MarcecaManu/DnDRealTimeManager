package com.example.ddrealtimemanager.shared

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.example.ddrealtimemanager.R
import kotlinx.android.synthetic.main.activity_character_creation.*

class CharacterCreationActivity : AppCompatActivity() {

    var charId: Int = -1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_character_creation)

        val database = DBHelper(this)

        val extras = intent.extras
        if(extras != null){
            charId = extras.getInt("charId")
            etName.setText(extras.getString("name"))
            etRace.setText(extras.getString("race"))
            etClass.setText(extras.getString("class"))
            etDescr.setText(extras.getString("description"))
        }

        btnSave.setOnClickListener {

            //Check if a name was inserted
            if(!etName.text.toString().isBlank()){

                //If this is a new character...
              if(charId == -1) {

                  database.writeData(
                      etName.text.toString(),
                      etRace.text.toString(),
                      etClass.text.toString(),
                      etDescr.text.toString()
                  )
                  Toast.makeText(this, "New character was successfully saved!", Toast.LENGTH_SHORT)
                      .show()
                  finish()
              }else{    //Else, an existing character is being edited
                  database.editCharacter(charId,
                    etName.text.toString(),
                      etRace.text.toString(),
                      etClass.text.toString(),
                      etDescr.text.toString()
                      )
                  Toast.makeText(this, "The character was successfully modified!", Toast.LENGTH_SHORT)
                      .show()
                  finish()
              }
            }
            else{
                Toast.makeText(this, "Insert at least a name", Toast.LENGTH_SHORT).show()
            }
        }

        btnBack.setOnClickListener {
            finish()
        }
    }
}