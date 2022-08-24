package com.example.ddrealtimemanager.dm.real_time

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import com.example.ddrealtimemanager.R
import com.example.ddrealtimemanager.shared.FireBaseHelper
import com.example.ddrealtimemanager.shared.real_time.RT_Character
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.getValue
import kotlinx.android.synthetic.main.activity_dmreal_time_game.*

class DMRealTimeGameActivity : AppCompatActivity() , RT_ActiveCharactersCardListFragment.OnActiveCharacterSelectedListener{

    val extras = intent.extras
    val fbGameId = extras?.getString("fbGameId")

    val firebase = FirebaseDatabase.getInstance("https://dnd-real-time-manager-default-rtdb.europe-west1.firebasedatabase.app/")
    val gameRef = firebase.getReference(fbGameId!!)
    val playersRef = gameRef.child("players")

    private lateinit var charAdapter: RT_CharactersCardListAdapter


    private val activeListFragment = RT_ActiveCharactersCardListFragment()
    private val storedListFragment = RT_StoredCharactersCardListFragment()
    private val characterCreationFragment = RT_CharacterCreationFragment()
    private val characterVisualizationFragment = RT_CharacterVisualizationfragment()


    private var charactersList: ArrayList<RT_Character>? = ArrayList<RT_Character>()


    private var activeCharactersListFragment: RT_ActiveCharactersCardListFragment? = null

    override fun onActiveCharItemSelected(fbCharId: String) {
        if(activeCharactersListFragment != null && activeCharactersListFragment!!.isInLayout){

            //get selected character
            val character: RT_Character = getSpecificFbCharacter(fbCharId)

            //Change fragment and show selected character
            val initFragTransaction :FragmentTransaction = supportFragmentManager.beginTransaction()
            initFragTransaction.replace(R.id.rt_dm_fragment_container, RT_CharacterVisualizationfragment(character))

        }
    }




    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_dmreal_time_game)


        //Firebase initialization

        val fb = FireBaseHelper()


        playersRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {

                //val value = dataSnapshot.getValue(String::class.java)
                //val fb = FireBaseHelper()
                //val charactersList: ArrayList<RT_Character> =
                //    getCharactersList(dataSnapshot.value as Map<String, Any>)
                charactersList = dataSnapshot.getValue<ArrayList<RT_Character>>()
                refreshList(charactersList!!)

            }

            override fun onCancelled(error: DatabaseError) {
                Log.w("firebase", "Failed to read value.", error.toException())
            }
        })
        ////


        activeCharactersListFragment = supportFragmentManager
            .findFragmentById(R.id.rt_dm_fragment_container) as RT_ActiveCharactersCardListFragment



        //Set active characters list first

        val initFragTransaction :FragmentTransaction = supportFragmentManager.beginTransaction()
        initFragTransaction.replace(R.id.rt_dm_fragment_container, activeListFragment)


        rt_fab_dm_add_character.setOnClickListener{

            //TODO
            //Show stored characters list

            //Once a character is selected, let the user set RT_Character missing attributes (starting from a Character type)
            var selectedCharacter : RT_Character


            //Once confirmed, push the character in the players folder of the current game
            fb.fbPushCharacter(selectedCharacter)

        }

    }


    override fun onBackPressed() {
        //Ask if you want the quit the game
            //No --> END
            //YES -->
                //Terminate connection
                //Exit
                //END
    }

   /* private fun getCharactersList(characters: Map<String,Any>) : ArrayList<RT_Character>{

        var charactersList: ArrayList<RT_Character>

        for(Map.Entry<String, Object> entry : users.entrySet())

    }*/


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
