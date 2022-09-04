package com.example.ddrealtimemanager.player.real_time

import android.app.ProgressDialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.*
import androidx.appcompat.app.AppCompatActivity
import android.util.Log
import android.view.View
import android.widget.ProgressBar
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import com.example.ddrealtimemanager.R
import com.example.ddrealtimemanager.dm.real_time.DMRealTimeGameActivity
import com.example.ddrealtimemanager.shared.DBHelper
import com.example.ddrealtimemanager.shared.FireBaseHelper
import com.example.ddrealtimemanager.shared.real_time.RT_Character
import com.example.ddrealtimemanager.shared.real_time.RT_CharacterCreationFragment
import com.example.ddrealtimemanager.shared.real_time.RT_CharacterVisualizationfragment
import com.example.ddrealtimemanager.shared.real_time.RT_DiceFragment
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.core.Context
import kotlinx.android.synthetic.main.activity_character_visualization.*
import kotlinx.android.synthetic.main.activity_dmreal_time_game.*
import kotlinx.android.synthetic.main.activity_player_real_time_game.*
import kotlinx.android.synthetic.main.rt_character_visualization_fragment.*

class PlayerRealTimeGameActivity : AppCompatActivity(),
        RT_CharacterVisualizationfragment.OnCharVisualizationListener,
        RT_CharacterCreationFragment.OnCharacterCreationClick{


    private val CHAR_VISUALIZATION = 1
    private val CHAR_EDIT = 2
    private val DICE = 3

    var currentFragment = CHAR_VISUALIZATION

    var charVisFragment: RT_CharacterVisualizationfragment? = null
    var charEditFragment: RT_CharacterCreationFragment? = null
    var diceFragment: RT_DiceFragment? = null


    companion object {

        var updatedCharacter: RT_Character? = null

        //if true, hide the edit button
        var fight = false
        var yourTurn = false

        lateinit var fbGameId: String
        lateinit var fbCharId: String
        var localCharId = -1

    }




    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_player_real_time_game)

        val extras = intent.extras

        fbGameId = extras!!.getString("fbGameId")!!
        fbCharId = extras!!.getString("fbCharId")!!
        localCharId = extras!!.getInt("charId")!!

        val gameRef = FireBaseHelper.gamesRef.child(fbGameId!!)
        val playersRef = gameRef.child(FireBaseHelper.PLAYERS_DIR)


        gameRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                //Update updatedCharacter

                val character = snapshot.child(FireBaseHelper.PLAYERS_DIR).child(fbCharId!!)
                    .getValue(RT_Character::class.java)

                if(character == null){
                    //The cloud charater has been deleted!

                    Toast.makeText(this@PlayerRealTimeGameActivity,
                        "The DM has deleted your character! Leaving the game...",
                        Toast.LENGTH_LONG).show()

                    val db = DBHelper(this@PlayerRealTimeGameActivity)
                    db.playerLeaveGame(fbGameId)
                    finish()
                }

                updatedCharacter = character


                if(currentFragment == CHAR_VISUALIZATION){
                    putCharVisFragment()
                }

                //Check if a fight is going on.
                val checkFight = snapshot.child("fight").exists()
                if(checkFight && !fight){
                    fight = true

                    //Fight Changes!
                    when(currentFragment){
                       // DICE -> {diceFragment!!.previousFragment = CHAR_VISUALIZATION}
                        CHAR_EDIT -> {
                            putCharVisFragment()
                            Toast.makeText(this@PlayerRealTimeGameActivity,
                                "A fight has started! Character edit isn't allowed until it's finished.",
                                Toast.LENGTH_SHORT).show()
                            rt_player_btn_dice.visibility = View.VISIBLE
                        }
                        CHAR_VISUALIZATION -> {putCharVisFragment()}
                    }

                }else if(!checkFight && fight){
                    //The fight has finished!
                    fight = false

                    if(currentFragment == CHAR_VISUALIZATION){
                        putCharVisFragment()
                    }

                    Toast.makeText(this@PlayerRealTimeGameActivity,
                        "The fight has finished!",
                        Toast.LENGTH_SHORT).show()

                }else if(checkFight && fight){
                    val turnCheck = snapshot.child("fight").child(fbCharId).getValue(Boolean::class.java)


                    yourTurn = turnCheck!!

                    if(yourTurn){
                        itsYourTurn()
                    }
                }

            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

        })

        rt_player_btn_dice.setOnClickListener{

            when(currentFragment){
                DICE ->{
                    putCharVisFragment()
                    rt_player_btn_dice.text = "Dice"
                }

                CHAR_VISUALIZATION -> {
                    putDiceFragment()
                    rt_player_btn_dice.text = "Back"
                }

                CHAR_EDIT -> {
                    putCharVisFragment()
                    rt_player_btn_dice.text = "Dice"
                }
            }

        }



    }

    override fun onDeleteButtonSelected(fbCharId: String) {
        val adb = AlertDialog.Builder(this@PlayerRealTimeGameActivity)
        adb.setTitle("Remove your character from the game?")
        adb.setMessage("Are you sure? You will leave the game and loose your saves.")
        adb.setNegativeButton("Wait, go back!", null)
        adb.setPositiveButton("I'm out!") { dialog, which ->

            Toast.makeText(this, "Leaving the game...", Toast.LENGTH_SHORT).show()

            val db = DBHelper(this@PlayerRealTimeGameActivity)
            db.playerLeaveGame(fbGameId)
            finish()

        }
        adb.show()
        true
    }

    override fun onEditButtonSelected(character: RT_Character) {
        //Change fragment to character edit
        putCharEditFragment()

    }

    fun putCharVisFragment(){


        charVisFragment = RT_CharacterVisualizationfragment("", false)
        val transaction = supportFragmentManager.beginTransaction()
            .replace(R.id.rt_player_fragment_container, charVisFragment!!)
            .commit()

        currentFragment = CHAR_VISUALIZATION
    }

    fun putCharEditFragment(){

        rt_player_btn_dice.visibility = View.GONE

        charEditFragment = RT_CharacterCreationFragment(updatedCharacter!!, CHAR_VISUALIZATION)
        val transaction = supportFragmentManager.beginTransaction()
            .replace(R.id.rt_player_fragment_container, charEditFragment!!)
            .commit()

        currentFragment = CHAR_EDIT
    }

    fun putDiceFragment(){

        diceFragment = RT_DiceFragment(CHAR_VISUALIZATION)

        val transaction = supportFragmentManager.beginTransaction()
            .replace(R.id.rt_player_fragment_container, diceFragment!!)
            .commit()


        currentFragment = DICE
    }

    private fun itsYourTurn(){
        if (Build.VERSION.SDK_INT >= 26) {
            (this.getSystemService(VIBRATOR_SERVICE) as Vibrator).vibrate(VibrationEffect.createOneShot(150, VibrationEffect.DEFAULT_AMPLITUDE))
        } else {
            (this.getSystemService(VIBRATOR_SERVICE) as Vibrator).vibrate(150)
        }

        Toast.makeText(this, "It's your turn!", Toast.LENGTH_SHORT).show()

        //if(currentFragment == CHAR_VISUALIZATION){
        //    putCharVisFragment()
       // }
    }

    private fun endTurn(){
        if(currentFragment == CHAR_VISUALIZATION){
            charVisFragment!!.endTurnLayout()
        }
    }

    override fun onCharacterCreationAddSelected(character: RT_Character) {
        FireBaseHelper.fbUpdatecharacter(character, fbGameId)
        putCharVisFragment()
        rt_player_btn_dice.visibility = View.VISIBLE
    }

    override fun onCharacterCreationBackSelected(previousFrag: Int, character: RT_Character?) {
        putCharVisFragment()
        rt_player_btn_dice.visibility = View.VISIBLE
    }
}