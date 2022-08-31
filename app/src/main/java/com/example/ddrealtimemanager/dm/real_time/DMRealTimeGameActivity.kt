package com.example.ddrealtimemanager.dm.real_time

import android.graphics.Color
import android.os.Bundle
import android.os.PersistableBundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import com.example.ddrealtimemanager.R
import com.example.ddrealtimemanager.shared.Character
import com.example.ddrealtimemanager.shared.DBHelper
import com.example.ddrealtimemanager.shared.FireBaseHelper
import com.example.ddrealtimemanager.shared.real_time.RT_Character
import com.google.firebase.database.*
import com.google.firebase.database.ktx.getValue
import kotlinx.android.synthetic.main.activity_characters_card_list.*
import kotlinx.android.synthetic.main.activity_dmreal_time_game.*
import kotlinx.android.synthetic.main.rt_character_creation_fragment.*
import java.lang.Exception
import kotlin.math.log

class DMRealTimeGameActivity : AppCompatActivity(), RT_ActiveCharactersCardListFragment.OnActiveCharacterSelectedListener,
        RT_CharacterVisualizationfragment.OnCharVisualizationListener,
        RT_StoredCharactersCardListFragment.OnStoredCharacterSelectedListener,
        RT_CharacterCreationFragment.OnCharacterCreationClick,
        RT_FightFragment.OnFightingCharacterSelectedListener{

    lateinit var fbGameId: String


    lateinit var gameRef: DatabaseReference
    lateinit var playersRef: DatabaseReference

    private val ACTIVE_CHARACTERS_LIST = 1
    private val CHARACTER_VISUALIZATION = 2
    private val STORED_CHARACTERS_LIST = 3
    private val CHARACTER_CREATION = 4
    private val DICE = 5
    private val FIGHT = 6

    var currentFragment: Int = ACTIVE_CHARACTERS_LIST






    private var activeListFragment: RT_ActiveCharactersCardListFragment? = null
    private var storedListFragment: RT_StoredCharactersCardListFragment? = null
    private var diceFragment: RT_DiceFragment? = null
    private var charCreationFragment: RT_CharacterCreationFragment? = null
    private var fightFragment: RT_FightFragment? = null

    private var charVisCharacterFBid: String? = null

    private var charCreationCharacter: RT_Character? = null
    private var charcreationPrevFragment: Int? = null

    companion object{

        val CHAR_VIS_FBID_KEY = "charvis"


        var heal = false
        var damage = false

        var fightBtnSelected = false
        var fight = false


        var charactersList: ArrayList<RT_Character>? = ArrayList<RT_Character>()

        fun getFbCharacters(): ArrayList<RT_Character>?{
            return charactersList
        }

        fun getSpecificFbCharacter(fbCharId: String): RT_Character?{
            for(character in charactersList!!){
                if(fbCharId == character.firebaseId){
                    return character
                }
            }
            return null
        }


    }




    override fun onActiveCharItemSelected(fbCharId: String) {

            //get selected character
        val character: RT_Character = getSpecificFbCharacter(fbCharId)!!

            //Change fragment and show selected character
        putCharVisualizationFragment(character)

    }

    override fun onActiveCharItemMultipleSelection(fbCharIdList: ArrayList<String>, change: Int) {

        fbCharIdList.forEach{
            val character = getSpecificFbCharacter(it)
            var newCurrentHp = character!!.currentHp + change
            if(newCurrentHp > character.maxHp){newCurrentHp = character.maxHp}
            FireBaseHelper.fbUpdateCharacterHealth(newCurrentHp, it, fbGameId)
        }

        heal = false
        damage = false

        rt_fight.visibility = View.VISIBLE
        rt_dice.visibility = View.VISIBLE
        rt_heal.setBackgroundColor(resources.getColor(R.color.purple_500))
        rt_damage.setBackgroundColor(resources.getColor(R.color.purple_500))



    }




    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_dmreal_time_game)


        //Firebase initialization

        val extras = intent.extras!!
        if(extras != null) {
            fbGameId = extras.getString("fbGameId")!!
            gameRef = FireBaseHelper.gamesRef.child(fbGameId)
            playersRef = gameRef.child("players")
        }else{
            throw Exception("No extras?")
        }

        rt_fight_back.visibility = View.GONE
        putActiveListFragment()


        playersRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {

                charactersList?.clear()

                dataSnapshot.children.forEach{

                    var foundCharacter = it.getValue(RT_Character::class.java)!!
                    charactersList!!.add(foundCharacter)

                }



                /*If the active characters listview is not shown, this line leads to an exception!*/
                if(currentFragment == ACTIVE_CHARACTERS_LIST) {

                    putActiveListFragment()
                    //activeListFragment!!.refreshList()
                }


            }

            override fun onCancelled(error: DatabaseError) {
                Log.w("firebase", "Failed to read value.", error.toException())
            }
        })



        rt_dice.setOnClickListener{
            if(currentFragment != DICE){

/*
                if(currentFragment == CHARACTER_CREATION){
                    //Store inserted data
                    charCreationCharacter!!.level = savedInstanceState?.getInt("level")!!
                    charCreationCharacter!!.ac = savedInstanceState?.getInt("ac")!!
                    charCreationCharacter!!.maxHp = savedInstanceState?.getInt("maxHp")!!
                    charCreationCharacter!!.initiative = savedInstanceState?.getInt("initiative")!!
                    charCreationCharacter!!.image = savedInstanceState?.getString("image")!!
                }
                */

                rt_fight.visibility = View.GONE
                rt_heal.visibility = View.GONE
                rt_damage.visibility = View.GONE
                rt_fab_dm_add_character.visibility = View. GONE
                rt_dice.text = "Back"

                putDiceFragment()
            }
            else{
                when(diceFragment!!.previousFragment){
                    ACTIVE_CHARACTERS_LIST -> {putActiveListFragment()}
                    STORED_CHARACTERS_LIST -> {putStoredListFragment()}
                    CHARACTER_VISUALIZATION -> {
                        val character = getSpecificFbCharacter(charVisCharacterFBid!!)
                        putCharVisualizationFragment(character!!)
                    }
                    CHARACTER_CREATION -> {
                        putCharCreationFragment(charCreationCharacter!!, charcreationPrevFragment!!)
                    }
                }

                rt_fight.visibility = View.VISIBLE
                rt_heal.visibility = View.VISIBLE
                rt_damage.visibility = View.VISIBLE
                rt_fab_dm_add_character.visibility = View.VISIBLE
                rt_dice.text = "Dice"


                diceFragment = null
            }

        }



        rt_fight.setOnClickListener{
            //Popup prompting to select the characters
            if(!fightBtnSelected) {
                Toast.makeText(this, "Select the fighters!", Toast.LENGTH_SHORT).show()
                if(currentFragment != ACTIVE_CHARACTERS_LIST){
                    putActiveListFragment()
                }

                fightBtnSelected = true


                rt_heal.visibility = View.GONE
                rt_damage.visibility = View.GONE
                rt_fab_dm_add_character.visibility = View. GONE
                rt_dice.visibility = View.GONE

                rt_fight_back.visibility = View.VISIBLE

                rt_fight.setBackgroundColor(Color.RED)
                rt_fight.text = "Select the fighters!"

                rt_fight_back.setOnClickListener{
                    //Cancel
                    fightBtnSelected = false
                    //Restore everything
                    rt_heal.visibility = View.VISIBLE
                    rt_damage.visibility = View.VISIBLE
                    rt_fab_dm_add_character.visibility = View. VISIBLE
                    rt_dice.visibility = View.VISIBLE
                    rt_fight_back.visibility = View.GONE

                    rt_fight.setBackgroundColor(resources.getColor(R.color.purple_500))
                    rt_fight.text = "Fight"

                    activeListFragment!!.resetSelected()
                    activeListFragment!!.refreshList()
                }

            }
            else if(fightBtnSelected){


                if(activeListFragment!!.checkCharactersAreSelected()){

                    var ready = false

                    val adb = AlertDialog.Builder(this@DMRealTimeGameActivity)
                    adb.setTitle("Start the fight?")
                    adb.setMessage("Did you select all the participant characters?")
                    adb.setNegativeButton("Wait, go back!", null)
                    adb.setPositiveButton("All ready!"){ dialog, which ->

                        //Restore everything
                        activeListFragment!!.refreshList()

                        rt_heal.visibility = View.VISIBLE
                        rt_damage.visibility = View.VISIBLE
                        rt_fab_dm_add_character.visibility = View. VISIBLE
                        rt_dice.visibility = View.VISIBLE
                        rt_fight_back.visibility = View.GONE

                        rt_fight.setBackgroundColor(Color.RED)
                        rt_fight.text = "End fight"

                        fightBtnSelected = false
                        fight = true
                        val fighters = activeListFragment!!.selectFighters()

                        fighters.forEach{
                            val chara = getSpecificFbCharacter(it)
                            Log.v("FIGHTCHECK", chara!!.name + " - " + chara!!.firebaseId)
                        }
                        putFightFragment(fighters)

                    }
                    adb.show()
                    true

                    //ALL READY!
                    if(!ready) {
                        //TODO

                    }
                }else{
                    Toast.makeText(this, "You have to select at least a character!", Toast.LENGTH_SHORT).show()
                }

            }
            //when at least a character is selected:
                //text changes in "start fight"

                //by pressing "start fight", start = true
                //to the fight fragment!
        }






        rt_heal.setOnClickListener{



            if(heal == false && damage == true) {
                heal = true
                damage = false
                rt_damage.setBackgroundColor(resources.getColor(R.color.purple_500))
                rt_heal.setBackgroundColor(resources.getColor(R.color.green_heal))

                rt_fight.visibility = View.GONE
                rt_dice.visibility = View.GONE
                rt_fab_dm_add_character.visibility = View.GONE

                if(currentFragment == ACTIVE_CHARACTERS_LIST) {
                    activeListFragment!!.healDmgPressed("heal", "damage")
                }else if(currentFragment == FIGHT){
                    fightFragment!!.healDmgPressed("heal", "damage")
                }

            }else if(heal == false && damage == false){
                heal = true
                rt_damage.setBackgroundColor(resources.getColor(R.color.purple_500))
                rt_heal.setBackgroundColor(resources.getColor(R.color.green_heal))

                rt_fight.visibility = View.GONE
                rt_dice.visibility = View.GONE
                rt_fab_dm_add_character.visibility = View.GONE

                if(currentFragment == ACTIVE_CHARACTERS_LIST) {
                    activeListFragment!!.healDmgPressed("heal", "none")
                }else if(currentFragment == FIGHT){
                    fightFragment!!.healDmgPressed("heal", "none")
                }
            }
            else if(heal == true){
                heal = false
                damage = false
                rt_damage.setBackgroundColor(resources.getColor(R.color.purple_500))
                rt_heal.setBackgroundColor(resources.getColor(R.color.purple_500))

                rt_fight.visibility = View.VISIBLE
                rt_dice.visibility = View.VISIBLE
                rt_fab_dm_add_character.visibility = View.VISIBLE

                if(currentFragment == ACTIVE_CHARACTERS_LIST) {
                    activeListFragment!!.healDmgPressed("cancel", "none")
                }else if(currentFragment == FIGHT){
                    fightFragment!!.healDmgPressed("cancel", "none")
                }
            }
        }

        rt_damage.setOnClickListener{

            if(damage == false && heal == true) {
                heal = false
                damage = true
                rt_damage.setBackgroundColor(resources.getColor(R.color.red_damage))
                rt_heal.setBackgroundColor(resources.getColor(R.color.purple_500))

                rt_fight.visibility = View.GONE
                rt_dice.visibility = View.GONE
                rt_fab_dm_add_character.visibility = View.GONE

                if(currentFragment == ACTIVE_CHARACTERS_LIST) {
                    activeListFragment!!.healDmgPressed("damage", "heal")
                }else if(currentFragment == FIGHT){
                    fightFragment!!.healDmgPressed("damage", "heal")
                }
            }
            else if(damage == false && heal == false){
                damage = true
                rt_damage.setBackgroundColor(resources.getColor(R.color.red_damage))
                rt_heal.setBackgroundColor(resources.getColor(R.color.purple_500))

                rt_fight.visibility = View.GONE
                rt_dice.visibility = View.GONE
                rt_fab_dm_add_character.visibility = View.GONE

                if(currentFragment == ACTIVE_CHARACTERS_LIST) {
                    activeListFragment!!.healDmgPressed("damage", "none")
                }else if(currentFragment == FIGHT){
                    fightFragment!!.healDmgPressed("damage", "none")
                }
            }
            else if(damage == true){
                heal = false
                damage = false
                rt_damage.setBackgroundColor(resources.getColor(R.color.purple_500))
                rt_heal.setBackgroundColor(resources.getColor(R.color.purple_500))

                rt_fight.visibility = View.VISIBLE
                rt_dice.visibility = View.VISIBLE
                rt_fab_dm_add_character.visibility = View.VISIBLE

                if(currentFragment == ACTIVE_CHARACTERS_LIST) {
                    activeListFragment!!.healDmgPressed("cancel", "none")
                }else if(currentFragment == FIGHT){
                    fightFragment!!.healDmgPressed("cancel", "none")
                }
            }
        }




        rt_fab_dm_add_character.setOnClickListener{
            putStoredListFragment()

        }

    }



    override fun onBackPressed() {

        //TODO IF FIGHT IS GOING ON, TERMINATE IT!


        //Ask if you want the quit the game
        val adb = AlertDialog.Builder(this@DMRealTimeGameActivity)
        adb.setTitle("Quit the game?")
        adb.setMessage("Are you sure you want to quit the game?")
        adb.setNegativeButton("Cancel", null)
        adb.setPositiveButton("I am!"){ dialog, which ->

            currentFragment = 0
            finish()
        }
        adb.show()
        true

    }

    fun putActiveListFragment(){
        rt_heal.visibility = View.VISIBLE
        rt_damage.visibility = View.VISIBLE
        rt_fab_dm_add_character.visibility = View.VISIBLE
        rt_fight.visibility = View.VISIBLE



        activeListFragment = RT_ActiveCharactersCardListFragment()
        val transaction = supportFragmentManager.beginTransaction()
            .replace(R.id.rt_dm_fragment_container, activeListFragment!!)
            .commit()

        currentFragment = ACTIVE_CHARACTERS_LIST
    }

    fun putStoredListFragment(){

        rt_heal.visibility = View.GONE
        rt_damage.visibility = View.GONE
        rt_fab_dm_add_character.visibility = View.GONE
        rt_fight.visibility = View.GONE


        storedListFragment = RT_StoredCharactersCardListFragment()
        val transaction = supportFragmentManager.beginTransaction()
            .replace(R.id.rt_dm_fragment_container, storedListFragment!!)
            .commit()

        currentFragment = STORED_CHARACTERS_LIST
    }

    fun putCharCreationFragment(character: RT_Character, previousFragment: Int){

        rt_heal.visibility = View.GONE
        rt_damage.visibility = View.GONE
        rt_fab_dm_add_character.visibility = View.GONE
        rt_fight.visibility = View.GONE

        charCreationFragment = RT_CharacterCreationFragment(character,previousFragment)
        val transaction = supportFragmentManager.beginTransaction()
            .replace(R.id.rt_dm_fragment_container, charCreationFragment!!)
            .commit()

        charCreationCharacter = character
        charcreationPrevFragment = previousFragment

        currentFragment = CHARACTER_CREATION
    }

    fun putCharVisualizationFragment(character: RT_Character){

        rt_heal.visibility = View.GONE
        rt_damage.visibility = View.GONE
        rt_fab_dm_add_character.visibility = View.GONE
        rt_fight.visibility = View.GONE


        val transaction = supportFragmentManager.beginTransaction()
            .replace(R.id.rt_dm_fragment_container, RT_CharacterVisualizationfragment(character.firebaseId!!))
            .commit()

        charVisCharacterFBid = character.firebaseId!!

        currentFragment = CHARACTER_VISUALIZATION
    }


    fun putDiceFragment(){
        diceFragment = RT_DiceFragment(currentFragment)
        val transaction = supportFragmentManager.beginTransaction()
            .replace(R.id.rt_dm_fragment_container, diceFragment!!)
            .commit()
        currentFragment = DICE
    }

    fun putFightFragment(fightersFBlist: ArrayList<String>){

        rt_heal.visibility = View.VISIBLE
        rt_damage.visibility = View.VISIBLE
        rt_fab_dm_add_character.visibility = View.VISIBLE


        fightFragment = RT_FightFragment(fightersFBlist)
        val transaction = supportFragmentManager.beginTransaction()
            .replace(R.id.rt_dm_fragment_container, fightFragment!!)
            .commit()
        currentFragment = FIGHT
    }


    override fun onBackButtonSelected() {
        putActiveListFragment()
    }

    override fun onDeleteButtonSelected(fbCharId: String) {
        val adb = AlertDialog.Builder(this@DMRealTimeGameActivity)
        adb.setTitle("Remove character from the game?")
        adb.setMessage("This will not remove it from local storage.")
        adb.setNegativeButton("Cancel", null)
        adb.setPositiveButton("Yes!"){ dialog, which ->

            FireBaseHelper.fbRemoveCharacterFromGame(fbCharId, fbGameId)
            putActiveListFragment()
        }
        adb.show()
        true
    }

    override fun onEditButtonSelected(character: RT_Character) {
        //Show character creation phase
        putCharCreationFragment(character, CHARACTER_VISUALIZATION)

    }

    override fun onStoredCharItemSelected(character: Character) {

        val rt_character = RT_Character("", character.name, character.race, character.clas, character.desc, character.image)
        putCharCreationFragment(rt_character, STORED_CHARACTERS_LIST)

    }

    override fun onStoredListBackButtonSelected() {
        putActiveListFragment()
    }

    override fun onCharacterCreationAddSelected(character: RT_Character) {
        //push the new character, or edit it
        if(character.firebaseId!!.isBlank()){
            var newCharacter = FireBaseHelper.fbPushCharacter(character, fbGameId)
        }else{
            FireBaseHelper.fbUpdatecharacter(character, fbGameId)
        }

        //Change fragment to active list
        putActiveListFragment()




    }

    override fun onCharacterCreationBackSelected(previousFragment: Int, character: RT_Character?) {
        if(previousFragment == STORED_CHARACTERS_LIST){putStoredListFragment()}
        if(previousFragment == CHARACTER_VISUALIZATION){putCharVisualizationFragment(character!!)}
    }


}
