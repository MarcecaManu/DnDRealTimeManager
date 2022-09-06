package com.example.ddrealtimemanager.dm.real_time

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.example.ddrealtimemanager.R
import com.example.ddrealtimemanager.shared.Character
import com.example.ddrealtimemanager.shared.FireBaseHelper
import com.example.ddrealtimemanager.shared.real_time.RT_Character
import com.example.ddrealtimemanager.shared.real_time.RT_CharacterCreationFragment
import com.example.ddrealtimemanager.shared.real_time.RT_CharacterVisualizationfragment
import com.google.firebase.database.*
import kotlinx.android.synthetic.main.activity_dmreal_time_game.*
import com.example.ddrealtimemanager.shared.real_time.RT_DiceFragment
import java.lang.Exception


class DMRealTimeGameActivity : AppCompatActivity(), RT_ActiveCharactersCardListFragment.OnActiveCharacterSelectedListener,
        RT_CharacterVisualizationfragment.OnCharVisualizationListener,
        RT_StoredCharactersCardListFragment.OnStoredCharacterSelectedListener,
        RT_CharacterCreationFragment.OnCharacterCreationClick,
        RT_FightFragment.OnFightingCharacterSelectedListener{



    lateinit var gameRef: DatabaseReference
    lateinit var playersRef: DatabaseReference



    private var activeListFragment: RT_ActiveCharactersCardListFragment? = null
    private var storedListFragment: RT_StoredCharactersCardListFragment? = null
    private var charVisFragment: RT_CharacterVisualizationfragment? = null
    private var diceFragment: RT_DiceFragment? = null
    private var charCreationFragment: RT_CharacterCreationFragment? = null
    private var fightFragment: RT_FightFragment? = null

    private var charVisCharacterFBid: String? = null

    private var charCreationCharacter: RT_Character? = null
    private var charcreationPrevFragment: Int? = null


    private var recordFightCharacters: ArrayList<RT_Character>? = null
    private var recordFightTurn: Int? = null
    private var recordFightPosition: Int? = null
    private var recordFightersFbList: ArrayList<String>? = null

    companion object{

        lateinit var fbGameId: String

        val ACTIVE_CHARACTERS_LIST = 1
        val CHARACTER_VISUALIZATION = 2
        val STORED_CHARACTERS_LIST = 3
        val CHARACTER_CREATION = 4
        val DICE = 5
        val FIGHT = 6

        var currentFragment: Int = ACTIVE_CHARACTERS_LIST

        var heal = false
        var damage = false

        var fightBtnSelected = false
        var fight = false

        /*
        * Probably the most important variable in the activity.
        * charactersList is updated so that the characters it contains are the same
        * found in the Firebase database, in real time
        */
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




    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_dmreal_time_game)


        //Firebase initialization

        val extras = intent.extras!!
        if(extras != null) {
            fbGameId = extras.getString("fbGameId")!!
            gameRef = FireBaseHelper.gamesRef.child(fbGameId)
            playersRef = gameRef.child(FireBaseHelper.PLAYERS_DIR)
        }else{
            throw Exception("No extras?")
        }

        rt_fight_back.visibility = View.GONE

        /*
        * This function is called at the start of the activity, so that if the application
        * is closed while a fight is going on, the database will just remove everything related
        * to the ongoing fight
        */
        FireBaseHelper.fbEndFight(fbGameId)

        //First fragment to be shown in this activity
        putActiveListFragment()


        //This function retrieves any data changed related to characters and an eventual fight

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

                if(fight){
                    if(currentFragment == FIGHT){
                        fightFragment!!.refreshList()
                    }else{
                        fightFragment!!.refreshCharacters()
                    }
                }


            }

            override fun onCancelled(error: DatabaseError) {
                Log.w("firebase", "Failed to read value.", error.toException())
            }
        })


        /*
         * Pressing the "i" button in top left, will show the game ID, so that the DM can
         * send it to the players who want to join
         */


        rt_fab_dm_gameid.setOnClickListener{
            val adb = AlertDialog.Builder(this@DMRealTimeGameActivity)
            adb.setTitle("Game ID:")
            adb.setMessage(fbGameId.substring(1, fbGameId.length))
            adb.setNegativeButton("Back", null)
            adb.setPositiveButton("Copy to clipboard") { dialog, which ->
                val clipboard: ClipboardManager = getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                val clip: ClipData = ClipData.newPlainText("Game Id", fbGameId.substring(1, fbGameId.length))
                clipboard.setPrimaryClip(clip)

            }
            adb.show()
            true
        }


        //The dice button generates a "seamless" parallel functionality for dice to be shown and used

        rt_dice.setOnClickListener{
            if(currentFragment != DICE){

                if(fight){
                    recordFightCharacters = fightFragment!!.fightingCharacters
                    recordFightTurn = fightFragment!!.turn
                    recordFightPosition = fightFragment!!.currentPosition
                }


                rt_fight.visibility = View.GONE
                rt_heal.visibility = View.GONE
                rt_damage.visibility = View.GONE
                rt_fab_dm_add_character.visibility = View.GONE
                rt_fight_back.visibility = View.GONE
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
                    FIGHT -> {
                        putFightFragment(recordFightersFbList, recordFightTurn!!, recordFightPosition!!, recordFightCharacters!!)
                    }


                }


                rt_dice.text = "Dice"


                diceFragment = null
            }

        }

        /*
         * This button will show only on character visualization (both fight and normal state),
         * on character selection for a fight,
         * and on character selection during a fight.
         */

        rt_fight_back.setOnClickListener {

            //I

            if(fightBtnSelected) {
                //Cancel
                fightBtnSelected = false
                //Restore everything
                rt_heal.visibility = View.VISIBLE
                rt_damage.visibility = View.VISIBLE
                rt_fab_dm_add_character.visibility = View.VISIBLE
                rt_dice.visibility = View.VISIBLE
                rt_fight_back.visibility = View.GONE


                if(!fight) {
                    rt_fight.setBackgroundColor(resources.getColor(R.color.purple_700))
                    rt_fight.text = "Fight"

                    activeListFragment!!.resetSelected()
                    activeListFragment!!.refreshList()
                }else if(fight){
                    rt_fight.text = "End fight"
                    putFightFragment(recordFightersFbList, recordFightTurn!!, recordFightPosition!!, recordFightCharacters)
                }
            }else if(currentFragment == CHARACTER_VISUALIZATION){

                rt_fight_back.visibility = View.GONE


                if(fight){
                    putFightFragment(recordFightersFbList,recordFightTurn!!, recordFightPosition!!, recordFightCharacters)
                }else {
                    putActiveListFragment()
                }
            }else if(currentFragment == STORED_CHARACTERS_LIST){
                rt_fight_back.visibility = View.GONE

                putActiveListFragment()
            }
        }


        /*
         * This button starts or stops a fight
         */

        rt_fight.setOnClickListener{

            if(!fight || fightBtnSelected) {

                //If there's no fight going on, OR the fight button has been pressed before

                //Popup prompting to select the characters
                if (!fightBtnSelected) {

                    //The fight button wasn't pressed before! It's a new fight: start char selection

                    Toast.makeText(this, "Select the fighters!", Toast.LENGTH_SHORT).show()
                    if (currentFragment != ACTIVE_CHARACTERS_LIST) {
                        putActiveListFragment()
                    }

                    fightBtnSelected = true


                    rt_heal.visibility = View.GONE
                    rt_damage.visibility = View.GONE
                    rt_fab_dm_add_character.visibility = View.GONE
                    rt_dice.visibility = View.GONE

                    rt_fight_back.visibility = View.VISIBLE

                    rt_fight.setBackgroundColor(Color.RED)
                    rt_fight.text = "Select the fighters!"



                } else if (fightBtnSelected) {

                    /* The fight button was pressed before. This means that:
                     *  - We're in the middle of character selection, and the user wants to start
                     *    the fight using the selected characters
                     *
                     *  - We're in the middle of a fight, and the user is adding characters
                     *    to the fighting ones.
                     */

                    if (activeListFragment!!.checkCharactersAreSelected()) {
                        if(!fight) {
                            val adb = AlertDialog.Builder(this@DMRealTimeGameActivity)
                            adb.setTitle("Start the fight?")
                            adb.setMessage("Did you select all the participant characters?")
                            adb.setNegativeButton("Wait, go back!", null)
                            adb.setPositiveButton("All ready!") { dialog, which ->

                                //Restore everything
                                activeListFragment!!.refreshList()

                                rt_heal.visibility = View.VISIBLE
                                rt_damage.visibility = View.VISIBLE
                                rt_fab_dm_add_character.visibility = View.VISIBLE
                                rt_dice.visibility = View.VISIBLE
                                rt_fight_back.visibility = View.GONE

                                rt_fight.setBackgroundColor(Color.RED)
                                rt_fight.text = "End fight"

                                fightBtnSelected = false
                                fight = true
                                val fighters = activeListFragment!!.selectFighters()


                                FireBaseHelper.fbCreateNewFight(fighters, fbGameId)

                                putFightFragment(fighters)

                            }
                            adb.show()
                            true
                        }else if (fight){

                            //Fight is going on, the user wants to add the characters they selected.

                            //Restore everything
                            activeListFragment!!.refreshList()

                            rt_heal.visibility = View.VISIBLE
                            rt_damage.visibility = View.VISIBLE
                            rt_fab_dm_add_character.visibility = View.VISIBLE
                            rt_dice.visibility = View.VISIBLE
                            rt_fight_back.visibility = View.GONE

                            rt_fight.setBackgroundColor(Color.RED)
                            rt_fight.text = "End fight"

                            fightBtnSelected = false


                            val newFighters = activeListFragment!!.selectFighters()

                            FireBaseHelper.fbAddCharactersMidFight(newFighters, fbGameId)

                            newFighters?.forEach {
                                recordFightCharacters!!.add(getSpecificFbCharacter(it)!!)
                            }


                            putFightFragment(recordFightersFbList, recordFightTurn!!, recordFightPosition!!, recordFightCharacters)
                        }

                    } else {

                        //No characters selected

                        Toast.makeText(
                            this,
                            "You have to select at least a character!",
                            Toast.LENGTH_SHORT
                        ).show()
                    }

                }

            }else if(fight){

                //We're in a fight, which means that the user wants to end it.

                val adb = AlertDialog.Builder(this@DMRealTimeGameActivity)
                adb.setTitle("End the fight?")
                adb.setMessage("Are you sure you want to end the fight?")
                adb.setNegativeButton("Cancel", null)
                adb.setPositiveButton("I am!") { dialog, which ->

                    //End fight
                    fight = false

                    FireBaseHelper.fbEndFight(fbGameId)

                    //Restore layout
                    /* ? no layout to restore? */
                    recordFightPosition = null
                    recordFightCharacters = null
                    recordFightTurn = null


                    rt_fight.text = "Fight"
                    rt_fight.setBackgroundColor(resources.getColor(R.color.purple_700))

                    //Put active list
                    putActiveListFragment()
                    fightFragment = null


                }
                adb.show()
                true

            }

            //when at least a character is selected:
                //text changes in "start fight"

                //by pressing "start fight", start = true
                //to the fight fragment!
        }




        /*
         * Here we have the heal and damage button. Their usage has to be exclusive in both
         * functionality and layout, and that's why I needed so much lines of code.
         */

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
                    rt_fab_dm_add_character.visibility = View.VISIBLE
                }else if(currentFragment == FIGHT){
                    fightFragment!!.healDmgPressed("cancel", "none")
                    rt_fab_dm_add_character.visibility = View.VISIBLE

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



        // Pressing this button shows the local characters from the users, and gives the possibility
        // to add them to the current game.

        rt_fab_dm_add_character.setOnClickListener{
            if(!fight){
            putStoredListFragment()
            }
            else if(fight){
                //Add character to fight from stored characters or active

                //Hide all but fight button
                rt_dice.visibility = View.GONE
                rt_heal.visibility = View.GONE
                rt_damage.visibility = View.GONE
                rt_fab_dm_add_character.visibility = View.GONE

                rt_fight_back.visibility = View.VISIBLE

                rt_fight.text = "Add character"

                fightBtnSelected = true

                putActiveListFragment()
            }

        }

    }



    /*
     ****************  FRAGMENT SWITCH FUNCTIONS  ****************
     *
     * These functions are an a attempt to modularize the many fragment interchanges in this
     * activity.
     *
     * They basically do what it's written in their name, with some consideration on the possible
     * fragments which they're substituting.
     */

    fun putActiveListFragment(){

        if(!(fight && fightBtnSelected)) {
            rt_heal.visibility = View.VISIBLE
            rt_damage.visibility = View.VISIBLE
            rt_fab_dm_add_character.visibility = View.VISIBLE
            rt_fight.visibility = View.VISIBLE
            rt_fight_back.visibility = View.GONE
        }

        if(fight){
            recordFightTurn = fightFragment!!.turn
            recordFightPosition = fightFragment!!.currentPosition
            recordFightCharacters = fightFragment!!.fightingCharacters
        }



        activeListFragment = RT_ActiveCharactersCardListFragment()
        if(!supportFragmentManager.isDestroyed) {
            val transaction = supportFragmentManager.beginTransaction()
                .replace(R.id.rt_dm_fragment_container, activeListFragment!!)
                .commit()

            currentFragment = ACTIVE_CHARACTERS_LIST
        }


    }


    fun putStoredListFragment(){

        rt_heal.visibility = View.GONE
        rt_damage.visibility = View.GONE
        rt_fab_dm_add_character.visibility = View.GONE
        rt_fight.visibility = View.GONE
        rt_fight_back.visibility = View.VISIBLE


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
        rt_fight_back.visibility = View.GONE

        if(fight){
            recordFightTurn = fightFragment!!.turn
            recordFightPosition = fightFragment!!.currentPosition
            recordFightCharacters = fightFragment!!.fightingCharacters
        }

        charCreationFragment = RT_CharacterCreationFragment(character,previousFragment)
        val transaction = supportFragmentManager.beginTransaction()
            .replace(R.id.rt_dm_fragment_container, charCreationFragment!!)
            .commit()

        charCreationCharacter = character
        charcreationPrevFragment = previousFragment

        currentFragment = CHARACTER_CREATION
    }


    fun putCharVisualizationFragment(character: RT_Character){


        rt_fight_back.visibility = View.VISIBLE

        if(fight){
            recordFightTurn = fightFragment!!.turn
            recordFightPosition = fightFragment!!.currentPosition
            recordFightCharacters = fightFragment!!.fightingCharacters
        }

        rt_heal.visibility = View.GONE
        rt_damage.visibility = View.GONE
        rt_fab_dm_add_character.visibility = View.GONE

        if(!fight) {
            rt_fight.visibility = View.GONE
        }

        charVisFragment = RT_CharacterVisualizationfragment(character.firebaseId!!, true)
        val transaction = supportFragmentManager.beginTransaction()
            .replace(R.id.rt_dm_fragment_container, charVisFragment!!)
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


    // The many optional arguments of this function are needed to restore the fight state whenever
    // other fragments substitute it while a fight is still going on.

    fun putFightFragment(fightersFBlist: ArrayList<String>?, turn: Int = 1, position: Int = 0, fightingCharacters: ArrayList<RT_Character>? = null){

        rt_heal.visibility = View.VISIBLE
        rt_damage.visibility = View.VISIBLE
        rt_fab_dm_add_character.visibility = View.VISIBLE
        rt_fight.visibility = View.VISIBLE


        fightFragment = RT_FightFragment(fightersFBlist, turn, position, fightingCharacters)
        val transaction = supportFragmentManager.beginTransaction()
            .replace(R.id.rt_dm_fragment_container, fightFragment!!)
            .commit()
        currentFragment = FIGHT
    }




    /*
     ****************  USER INPUT SECTION  ****************
     *
     * Here we have functions which control the button selection of the many buttons which cannot
     * be controlled from the onCreate functions, which means:
     *
     *  - Buttons which are part of fragments
     *
     *  - The android back button, which was overridden so that the user accessibility to the backstack
     *    is controlled.
     */


    // Android back button override. If the game is going on, the user will be asked a confirmation
    // to quit the game.
    // During a fight, it will simply do nothing and hint the user to end the fight before leaving.

    override fun onBackPressed() {


        if(fight){
            Toast.makeText(this, "End the fight before leaving!", Toast.LENGTH_SHORT).show()
        }else {


            //Ask if you want the quit the game
            val adb = AlertDialog.Builder(this@DMRealTimeGameActivity)
            adb.setTitle("Quit the game?")
            adb.setMessage("Are you sure you want to quit the game?")
            adb.setNegativeButton("Cancel", null)
            adb.setPositiveButton("I am!") { dialog, which ->

                currentFragment = 0
                finish()
            }
            adb.show()
            true
        }

    }


    // Part of the Active fragment, is triggered when a fight is occurring and the user
    // clicks a character, so that its info can be shown in a char visualization fragment.

    override fun onFightingCharItemSelected(fbCharId: String) {

        //get selected character
        val character: RT_Character = getSpecificFbCharacter(fbCharId)!!

        //Change fragment and show selected character
        putCharVisualizationFragment(character)

    }

    // Part of the Active fragment, it's triggered when the user clicks a character,
    // so that its info can be shown in a char visualization fragment.

    override fun onActiveCharItemSelected(fbCharId: String) {
        val character: RT_Character = getSpecificFbCharacter(fbCharId)!!

        //Change fragment and show selected character
        putCharVisualizationFragment(character)
    }


    // Part of the Active fragment, it's triggered when multiple characters are being selected
    // and the user has confirmed the heal/damage action on them.

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
        rt_fab_dm_add_character.visibility = View.VISIBLE
        rt_heal.setBackgroundColor(resources.getColor(R.color.purple_500))
        rt_damage.setBackgroundColor(resources.getColor(R.color.purple_500))

    }



    // Part of the char visualization fragment, it's clicked when the user wants to
    // delete a particular character.

    override fun onDeleteButtonSelected(fbCharId: String) {
        val adb = AlertDialog.Builder(this@DMRealTimeGameActivity)
        adb.setTitle("Remove character from the game?")
        adb.setMessage("This will not remove it from local storage.")
        adb.setNegativeButton("Cancel", null)
        adb.setPositiveButton("Yes!"){ dialog, which ->

            FireBaseHelper.fbRemoveCharacterFromGame(fbCharId, fbGameId)

            if(fight){
                putFightFragment(recordFightersFbList, recordFightTurn!!, recordFightPosition!!, recordFightCharacters!!)
            }else {
                putActiveListFragment()
            }
        }
        adb.show()
        true
    }

    // Part of the char visualization fragment, it's clicked when the user wants to edit
    // a particular character.
    // Char creation fragment is shown.

    override fun onEditButtonSelected(character: RT_Character) {
        //Show character creation phase
        if(fight){
            recordFightTurn = fightFragment!!.turn
            recordFightPosition = fightFragment!!.currentPosition
            recordFightCharacters = fightFragment!!.fightingCharacters
            putCharCreationFragment(character, FIGHT)
        }else{

            putCharCreationFragment(character, CHARACTER_VISUALIZATION)
        }

    }


    // Part of the stored list fragment, it's triggered when a character from the list
    // of local characters is clicked, meaning that the user wants to add them to the
    // current game.
    // This character needs more data to be considered an active character, so a char creation
    // fragment is called.

    override fun onStoredCharItemSelected(character: Character) {

        val rt_character = RT_Character("", character.name, character.race, character.clas, character.desc, character.image)
        putCharCreationFragment(rt_character, STORED_CHARACTERS_LIST)

    }

    // Part of the stored list fragment, leads back to the active list fragment.

    override fun onStoredListBackButtonSelected() {
        putActiveListFragment()
    }

    /*
     * Part of the char creation fragment, it's clicked when the user has inserted all the data
     * needed for an active character. This data is checked for validity, and in case of success,
     * the character is added to the game and the active list fragment is shown (which now shows the
     * new character).
     * This can also happen during a fight (a character is being edited), and in that case the
     * fight must be taken from where it was.
     */

    override fun onCharacterCreationAddSelected(character: RT_Character) {
        //push the new character, or edit it
        if(character.firebaseId!!.isBlank()){
            var newCharacter = FireBaseHelper.fbPushCharacter(character, fbGameId)
        }else{
            FireBaseHelper.fbUpdatecharacter(character, fbGameId)
        }

        //Change fragment to fight or active list
        if(fight){
            putFightFragment(recordFightersFbList, recordFightTurn!!, recordFightPosition!!, recordFightCharacters!!)
        }else {
            putActiveListFragment()
        }

    }

    // Part of the char creation fragment, leads back to the previous fragment
    // or just to the fight fragment, is a fight is going on.

    override fun onCharacterCreationBackSelected(previousFragment: Int, character: RT_Character?) {
        if(fight){
            putFightFragment(recordFightersFbList, recordFightTurn!!, recordFightPosition!!, recordFightCharacters!!)
        }else {

            if (previousFragment == STORED_CHARACTERS_LIST) {
                putStoredListFragment()
            }
            if (previousFragment == CHARACTER_VISUALIZATION) {
                putCharVisualizationFragment(character!!)
            }
        }

    }



}
