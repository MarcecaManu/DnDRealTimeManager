package com.example.ddrealtimemanager.dm.real_time

import android.os.Bundle
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
import java.lang.Exception
import kotlin.math.log

class DMRealTimeGameActivity : AppCompatActivity(), RT_ActiveCharactersCardListFragment.OnActiveCharacterSelectedListener,
        RT_CharacterVisualizationfragment.OnBackButtonClickListener,
        RT_CharacterVisualizationfragment.OnDeleteButtonClickListener,
        RT_CharacterVisualizationfragment.OnEditButtonClikListener,
        RT_StoredCharactersCardListFragment.OnStoredCharacterSelectedListener,
        RT_CharacterCreationFragment.OnCharacterCreationClick{

    lateinit var fbGameId: String


    lateinit var gameRef: DatabaseReference
    lateinit var playersRef: DatabaseReference


    private val ACTIVE_CHARACTERS_LIST = 1
    private val CHARACTER_VISUALIZATION = 2
    private val STORED_CHARACTERS_LIST = 3
    private val CHARACTER_CREATION = 4
    //TODO ADD FRAGMENTS IDs
    var currentFragment: Int = ACTIVE_CHARACTERS_LIST






    private var activeListFragment = RT_ActiveCharactersCardListFragment()
    private var storedListFragment = RT_StoredCharactersCardListFragment()

    companion object{

        var heal = false
        var damage = false

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



    ///////////////////////
    override fun onRestart() {
        super.onRestart()
        activeListFragment = RT_ActiveCharactersCardListFragment()
        Log.v("LIFEOFACTIVITY", "OnRestart")
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.v("LIFEOFACTIVITY", "OnDestroy")
    }

    override fun onResume() {
        super.onResume()
        Log.v("LIFEOFACTIVITY", "OnResume")
    }

    override fun onStop() {
        super.onStop()

        Log.v("LIFEOFACTIVITY", "OnStop")
    }

    override fun onStart() {
        super.onStart()

        Log.v("LIFEOFACTIVITY", "OnStart")
    }
///////////////////



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
        rt_damage.setFadingEdgeLength(0)
        rt_heal.setFadingEdgeLength(0)

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


                    activeListFragment.refreshList()
                }


            }

            override fun onCancelled(error: DatabaseError) {
                Log.w("firebase", "Failed to read value.", error.toException())
            }
        })



        rt_heal.setOnClickListener{
            if(currentFragment != ACTIVE_CHARACTERS_LIST){
                putActiveListFragment()
            }
            if(heal == false) {
                heal = true
                damage = false
                rt_damage.setFadingEdgeLength(0)
                rt_heal.setFadingEdgeLength(10)
                activeListFragment.healDmgPressed("heal")
            }
            else if(heal == true){
                heal = false
                damage = false
                rt_damage.setFadingEdgeLength(0)
                rt_heal.setFadingEdgeLength(0)
                activeListFragment.healDmgPressed("cancel")
            }
        }

        rt_damage.setOnClickListener{
            if(damage == false) {
                heal = false
                damage = true
                rt_damage.setFadingEdgeLength(10)
                rt_heal.setFadingEdgeLength(0)
                activeListFragment.healDmgPressed("damage")
            }
            else if(damage == true){
                heal = false
                damage = false
                rt_damage.setFadingEdgeLength(0)
                rt_heal.setFadingEdgeLength(0)
                activeListFragment.healDmgPressed("cancel")
            }
        }




        rt_fab_dm_add_character.setOnClickListener{
            putStoredListFragment()

        }

    }


    override fun onBackPressed() {

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
        activeListFragment = RT_ActiveCharactersCardListFragment()
        val transaction = supportFragmentManager.beginTransaction()
            .replace(R.id.rt_dm_fragment_container, activeListFragment)
            .commit()

        currentFragment = ACTIVE_CHARACTERS_LIST
    }

    fun putStoredListFragment(){
        storedListFragment = RT_StoredCharactersCardListFragment()
        val transaction = supportFragmentManager.beginTransaction()
            .replace(R.id.rt_dm_fragment_container, storedListFragment)
            .commit()

        currentFragment = STORED_CHARACTERS_LIST
    }

    fun putCharCreationFragment(character: RT_Character, previousFragment: Int){
        val characterCreationFragment = RT_CharacterCreationFragment(character,previousFragment)
        val transaction = supportFragmentManager.beginTransaction()
            .replace(R.id.rt_dm_fragment_container, characterCreationFragment)
            .commit()

        currentFragment = CHARACTER_CREATION
    }

    fun putCharVisualizationFragment(character: RT_Character){
        val transaction = supportFragmentManager.beginTransaction()
            .replace(R.id.rt_dm_fragment_container, RT_CharacterVisualizationfragment(character))
            .commit()
        currentFragment = CHARACTER_VISUALIZATION
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
            onBackButtonSelected()
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
