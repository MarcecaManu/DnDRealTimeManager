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
import com.google.firebase.database.*
import com.google.firebase.database.ktx.getValue
import kotlinx.android.synthetic.main.activity_characters_card_list.*
import kotlinx.android.synthetic.main.activity_dmreal_time_game.*
import java.lang.Exception

class DMRealTimeGameActivity : AppCompatActivity(), RT_ActiveCharactersCardListFragment.OnActiveCharacterSelectedListener,
        RT_CharacterVisualizationfragment.OnBackButtonClickListener{

    lateinit var fbGameId: String

    //val fb = FireBaseHelper()


    //val firebase = FirebaseDatabase.getInstance("https://dnd-real-time-manager-default-rtdb.europe-west1.firebasedatabase.app/")
    lateinit var gameRef: DatabaseReference
    lateinit var playersRef: DatabaseReference


    private val ACTIVE_CHARACTERS_LIST = 1
    private val CHARACTER_VISUALIZATION = 2
    //TODO ADD FRAGMENTS IDs
    var currentFragment: Int = ACTIVE_CHARACTERS_LIST


    private lateinit var charAdapter: RT_CharactersCardListAdapter


    private val activeListFragment = RT_ActiveCharactersCardListFragment()
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




    //private var activeCharactersListFragment: RT_ActiveCharactersCardListFragment? = null

    override fun onActiveCharItemSelected(fbCharId: String) {
        //if(activeListFragment != null && activeListFragment!!.isInLayout){

            //get selected character
            val character: RT_Character = getSpecificFbCharacter(fbCharId)!!

            //Change fragment and show selected character
        val transaction = supportFragmentManager.beginTransaction()
            .replace(R.id.rt_dm_fragment_container, RT_CharacterVisualizationfragment(character))
            .commit()

        //}
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

    //fun refreshList(){
        //val adapter = RT_CharactersCardListAdapter(this, DMRealTimeGameActivity.getFbCharacters()!!)
   // }


    override fun onBackPressed() {
        //TODO
        //Ask if you want the quit the game
            //No --> END
            //YES -->
                //Terminate connection
                //Exit
                //END
    }

    override fun onBackButtonSelected() {
        val transaction = supportFragmentManager.beginTransaction()
            .replace(R.id.rt_dm_fragment_container, activeListFragment)
            .commit()

        //activeListFragment.refreshList()
    }

    /* private fun getCharactersList(characters: Map<String,Any>) : ArrayList<RT_Character>{

         var charactersList: ArrayList<RT_Character>

         for(Map.Entry<String, Object> entry : users.entrySet())

     }*/







}
