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
        RT_CharacterVisualizationfragment.OnEditButtonClikListener{

    lateinit var fbGameId: String


    lateinit var gameRef: DatabaseReference
    lateinit var playersRef: DatabaseReference


    private val ACTIVE_CHARACTERS_LIST = 1
    private val CHARACTER_VISUALIZATION = 2
    private val STORED_CHARACTERS_LIST = 3
    //TODO ADD FRAGMENTS IDs
    var currentFragment: Int = ACTIVE_CHARACTERS_LIST




    private var activeListFragment = RT_ActiveCharactersCardListFragment()
    private val storedListFragment = RT_StoredCharactersCardListFragment()
    private val characterCreationFragment = RT_CharacterCreationFragment()
    //private val characterVisualizationFragment = RT_CharacterVisualizationfragment(null)

    companion object{

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
        val transaction = supportFragmentManager.beginTransaction()
            .replace(R.id.rt_dm_fragment_container, RT_CharacterVisualizationfragment(character))
            .commit()
        currentFragment = CHARACTER_VISUALIZATION

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
        ////

        activeListFragment = RT_ActiveCharactersCardListFragment()
        val transaction = supportFragmentManager.beginTransaction()
            .replace(R.id.rt_dm_fragment_container, activeListFragment)
            .commit()

        currentFragment = ACTIVE_CHARACTERS_LIST

        rt_fab_dm_add_character.setOnClickListener{

            //TODO
            //Show stored characters list

            //Once a character is selected, let the user set RT_Character missing attributes (starting from a Character type)
            //var selectedCharacter : RT_Character


            //Once confirmed, push the character in the players folder of the current game
            //fb.fbPushCharacter(selectedCharacter)

            /////simplified test
            var addCharacter = RT_Character("" ,"Name", "race", "class", "Dramatic description",
                "https://i.imgur.com/rNilHgV_d.webp?maxwidth=640&shape=thumb&fidelity=medium", 30, 25, 4, 18, 18)
            FireBaseHelper.fbPushCharacter(addCharacter, fbGameId!!)

            var addCharacter2 = RT_Character("", "Mandarino", "race", "class", "Description", "", 50, 2, 2, 20, 2)
            FireBaseHelper.fbPushCharacter(addCharacter2, fbGameId!!)

        /////

        }

    }


    override fun onBackPressed() {
        //TODO

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



    override fun onBackButtonSelected() {
        activeListFragment = RT_ActiveCharactersCardListFragment()
        val transaction = supportFragmentManager.beginTransaction()
            .replace(R.id.rt_dm_fragment_container, activeListFragment)
            .commit()

        currentFragment = ACTIVE_CHARACTERS_LIST

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

    override fun onEditButtonSelected() {
        TODO("Not yet implemented")
        //Show character creation phase
    }


}
