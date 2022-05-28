package com.example.ddrealtimemanager.shared

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import com.example.ddrealtimemanager.R
import kotlinx.android.synthetic.main.activity_character_visualization.*

class CharacterVisualizationActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_character_visualization)

        val db = DBHelper(this)

        val extras = intent.extras
        //if(extras != null){
            val charId = extras!!.getInt("charId")

            val character = db.getSpecificCharacter(charId)

            tvName.text = character.name
            tvRace.text = character.race
            tvClass.text = character.clas
            tvDescr.text = character.desc
       // }

        fabEdit.setOnClickListener{
            val intent = Intent(this, CharacterCreationActivity::class.java)

            intent.putExtra("charId", character.id)
            intent.putExtra("name", character.name)
            intent.putExtra("race", character.race)
            intent.putExtra("class", character.clas)
            intent.putExtra("description", character.desc)

            startActivity(intent)
            finish()

        }

        fabDelete.setOnClickListener{
            val adb = AlertDialog.Builder(this@CharacterVisualizationActivity)
            adb.setTitle("Delete ${character.name}?")
            adb.setMessage("Are you sure you want to delete your character?")
            adb.setNegativeButton("Cancel", null)
            adb.setPositiveButton("I am!"){ dialog, which ->
                val success = db.deleteCharacter(charId)
                if(success){
                    Toast.makeText(this, "The character was deleted successfully", Toast.LENGTH_SHORT).show()
                }else{
                    Toast.makeText(this, "ERROR: The character could not be deleted", Toast.LENGTH_SHORT).show()
                }
                Thread.sleep(1000)
                finish()
            }
            adb.show()
            true
        }

    }
}