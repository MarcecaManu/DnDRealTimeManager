package com.example.ddrealtimemanager.shared

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.example.ddrealtimemanager.R
import com.example.ddrealtimemanager.player.CharCreationForGameActivity
import kotlinx.android.synthetic.main.activity_character_visualization.*

class CharacterVisualizationActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_character_visualization)

        val db = DBHelper(this)

        joinBtn.visibility = View.GONE

        val extras = intent.extras

        val charId = extras!!.getInt("charId")

        val character = db.getSpecificCharacter(charId)

        tvName.text = character.name
        tvRace.text = character.race
        tvClass.text = character.clas
        tvDescr.text = character.desc


        val requestOptions = RequestOptions()
            .placeholder(R.drawable.propic_standard)
            .error(R.drawable.propic_standard)

        Glide.with(this)
            .applyDefaultRequestOptions(requestOptions)
            .load(character.image)
            .into(tv_chara_image)

        val join = extras.getBoolean("join")


        if(join){
            //joining

            fabEdit.visibility = View.GONE
            fabDelete.visibility = View.GONE
            joinBtn.visibility = View.VISIBLE

            joinBtn.setOnClickListener{
                val intent = Intent(this, CharCreationForGameActivity::class.java)
                intent.putExtra("charId", charId)
                startActivity(intent)
            }

        }else {


            fabEdit.setOnClickListener {
                val intent = Intent(this, CharacterCreationActivity::class.java)

                intent.putExtra("charId", character.id)
                intent.putExtra("name", character.name)
                intent.putExtra("race", character.race)
                intent.putExtra("class", character.clas)
                intent.putExtra("description", character.desc)
                intent.putExtra("image", character.image)

                startActivity(intent)
                finish()

            }

            fabDelete.setOnClickListener {
                val adb = AlertDialog.Builder(this@CharacterVisualizationActivity)
                adb.setTitle("Delete ${character.name}?")
                adb.setMessage("Are you sure you want to delete your character?")
                adb.setNegativeButton("Cancel", null)
                adb.setPositiveButton("I am!") { dialog, which ->
                    val success = db.deleteCharacter(charId)
                    if (success) {
                        Toast.makeText(
                            this,
                            "The character was deleted successfully",
                            Toast.LENGTH_SHORT
                        ).show()
                    } else {
                        Toast.makeText(
                            this,
                            "ERROR: The character could not be deleted",
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
}