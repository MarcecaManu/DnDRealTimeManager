package com.example.ddrealtimemanager.dm.real_time

import android.app.Activity
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ListView
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.ddrealtimemanager.R
import com.example.ddrealtimemanager.shared.FireBaseHelper
import com.example.ddrealtimemanager.shared.real_time.RT_Character
import com.example.ddrealtimemanager.shared.real_time.Dice
import kotlinx.android.synthetic.main.rt_fight_fragment.*

class RT_FightFragment(fightersFBlist: ArrayList<String>? = null, turn: Int = 1, currentPosition: Int = 0, fightingCharactersList: ArrayList<RT_Character>? = null): Fragment() {

    private lateinit var currentAdapter: RT_CharactersCardListAdapter
    private var listener: OnFightingCharacterSelectedListener? = null
    private var healDamageListener: OnFightingCharacterSelectedListener? = null

    companion object {
        var selectedCharactersFBid: ArrayList<String> = ArrayList<String>()
        var standbyCharacters: ArrayList<RT_Character> = DMRealTimeGameActivity.charactersList!!.clone() as ArrayList<RT_Character>
    }


    private var fightersFBlist = fightersFBlist

    var fightingCharacters = fightingCharactersList
    var turn = turn
    var currentPosition = currentPosition




    private var myContext: Context? = null



    interface OnFightingCharacterSelectedListener{
        fun onFightingCharItemSelected(fbCharId: String)
        fun onActiveCharItemMultipleSelection(fbCharIdList: ArrayList<String>, damage: Int)
    }





    /*
     * Based on the situation, the fighters list is created and ordered, or just taken from the arguments.
     */

    init{

        if(fightingCharacters == null){
            fightingCharacters = ArrayList()
        }

        if(fightersFBlist != null) {
            val charactersListWithResults: ArrayList<Pair<String, Int>> = ArrayList()


            fightersFBlist.forEach {
                val character = DMRealTimeGameActivity.getSpecificFbCharacter(it)

                val thisCharactersResult = character!!.initiative + Dice(20).throwDice()

                charactersListWithResults.add(Pair(it, thisCharactersResult))

            }

            charactersListWithResults.sortBy { it.second }
            charactersListWithResults.reverse()


            val orderedCharactersList: ArrayList<RT_Character> = ArrayList()

            charactersListWithResults.forEach {

                val character = DMRealTimeGameActivity.getSpecificFbCharacter(it.first)

                orderedCharactersList.add(character!!)
            }

            fightingCharacters = orderedCharactersList


        }

        fightingCharacters!!.forEach {
            standbyCharacters.remove(it)
        }

        FireBaseHelper.fbFightSetTurn(null, fightingCharacters!![0].firebaseId!!, DMRealTimeGameActivity.fbGameId)

    }


    //This function shifts the characters list by 1 "against the fight order"

    fun previousFighterList(){

        if(turn == 1 && currentPosition == 0){

            Toast.makeText(myContext, "This is the start of the fight!", Toast.LENGTH_SHORT).show()

        }else {

            val charPrevTurn = fightingCharacters!![0]

            val size = fightingCharacters!!.size - 1

            var currentChar: RT_Character = fightingCharacters!![0]
            var nextChar: RT_Character


            for (i in 0..size) {

                if (i != size) {
                    nextChar = fightingCharacters!![i + 1]

                    fightingCharacters!![i + 1] = currentChar

                    currentChar = nextChar

                } else {
                    fightingCharacters!![0] = currentChar
                }

            }

            if(currentPosition == 0){
                currentPosition = size
                turn--
                rt_fight_tv_turn.text = "TURN \n$turn"
            }else{
                currentPosition -= 1
            }

            FireBaseHelper.fbFightSetTurn(charPrevTurn.firebaseId,
                fightingCharacters!![0].firebaseId!!, DMRealTimeGameActivity.fbGameId)


        }


    }


    //This functions shifts the characters list by 1 "progressing the fight order"

    fun nextFighterList(){

        val charPrevTurn = fightingCharacters!![0]

        val size = fightingCharacters!!.size-1

        var currentChar: RT_Character = fightingCharacters!![size]
        var nextChar: RT_Character

        for(i in size downTo 0){

            if(i != 0){
                nextChar = fightingCharacters!![i-1]

                fightingCharacters!![i-1] = currentChar

                currentChar = nextChar

            }else
            {
                fightingCharacters!![size] = currentChar
            }

        }

        currentPosition = (currentPosition+1)%fightingCharacters!!.size
        if(currentPosition == 0){
            turn++
            rt_fight_tv_turn.text = "TURN \n$turn"
        }

        FireBaseHelper.fbFightSetTurn(charPrevTurn.firebaseId,
            fightingCharacters!![0].firebaseId!!, DMRealTimeGameActivity.fbGameId)

    }



    override fun onAttach(context: Context) {
        super.onAttach(context)
        if(context is Activity){
            listener = context as OnFightingCharacterSelectedListener
        }else {
            throw ClassCastException(context.toString()
                    + "must implement "
                    + "RT_FightFragment.OnFightingCharacterSelectedListener")
        }
    }



    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val view: View = inflater.inflate(R.layout.rt_fight_fragment, container, false)
        val lv: ListView = view.findViewById(R.id.rt_fight_listview_characters)


        currentAdapter = RT_CharactersCardListAdapter(activity as DMRealTimeGameActivity, fightingCharacters!!)




        // The itemclicklistener must behave in the preferred way, which can be for heal/damage
        // functionality, or just for visualizing a character.

        lv.setOnItemClickListener { parent, view, position, id ->

            val fbCharId = currentAdapter.getItem(position).firebaseId


            if(!DMRealTimeGameActivity.heal && !DMRealTimeGameActivity.damage) {
                //Change fragment passing the selected fbId
                listener?.onFightingCharItemSelected(fbCharId!!)

            }else {
                //Heal or Damage has been selected!

                var heal = DMRealTimeGameActivity.heal
                var damage = DMRealTimeGameActivity.damage


                if(selectedCharactersFBid.contains(fbCharId)){
                    selectedCharactersFBid.remove(fbCharId)
                    currentAdapter.notifyDataSetChanged()

                }
                else{
                    //if(heal){view.container1.setBackgroundColor(Color.GREEN)}
                    //if(damage){view.container1.setBackgroundColor(Color.RED)}

                    selectedCharactersFBid.add(fbCharId!!)
                    currentAdapter.notifyDataSetChanged()

                }


                // Whenever a heal/damage operation has begun, a button to confirm changes is shown.
                // If pressed, these actions are performed.

                rt_fight_btn_damageheal.setOnClickListener{
                    val value = rt_fight_et_damageheal.text.toString().trim()
                    var change: Int = 0

                    if(!value.isBlank()){
                        if(heal){
                            change = value.toInt()
                        }
                        if(damage){
                            change = value.toInt() * -1
                        }
                    }
                    listener?.onActiveCharItemMultipleSelection(selectedCharactersFBid, change)

                    refreshList()


                    healDmgPressed("cancel", "none")
                }
            }
        }


        return view


    }


    /*
     * This function works as a kind of event listener from the DM Activity, whenever a
     * heal/damage operation is requested.
     *
     * Its basic function is to set the layout based on the requested action.
     */

    fun healDmgPressed(next: String, previous: String){

        if(next == "heal" || next == "damage"){
            //rt_active_characters_card_listview.choiceMode = ListView.CHOICE_MODE_MULTIPLE
            rt_fight_listview_characters.itemsCanFocus = false

            rt_fight_et_damageheal.transformationMethod = null
            rt_fight_et_damageheal.visibility = View.VISIBLE
            rt_fight_btn_damageheal.visibility = View.VISIBLE

            if(next == "heal"){
                if(previous == "damage"){
                    refreshList()
                }
                rt_fight_btn_damageheal.setBackgroundResource(R.drawable.heal)
                rt_fight_et_damageheal.hint = "Heal points"

            }else if(next == "damage"){
                if(previous == "heal"){refreshList()}
                rt_fight_btn_damageheal.setBackgroundResource(R.drawable.damage)
                rt_fight_et_damageheal.hint = "Damage points"
            }
        }
        else if(next == "cancel"){
            rt_fight_listview_characters.itemsCanFocus = true
            rt_fight_et_damageheal.visibility = View.GONE
            rt_fight_btn_damageheal.visibility = View.GONE
            refreshList()
        }

        selectedCharactersFBid.clear()
        rt_fight_et_damageheal.text.clear()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        //Hide damage/heal items
        rt_fight_et_damageheal.visibility = View.GONE
        rt_fight_btn_damageheal.visibility = View.GONE

        rt_fight_tv_turn.text = "TURN \n$turn"


        rt_fight_btn_nextfighter.setOnClickListener{
            nextFighterList()
            refreshList()
        }

        rt_fight_btn_undo.setOnClickListener{
            previousFighterList()
            refreshList()
        }



        myContext = context
        refreshList()
    }


    override fun onDetach() {
        super.onDetach()
        this.listener = null
        this.healDamageListener = null

    }


    /*
     * This function is called whenever there's a change in the firebase database data.
     * The fighting characters are updated.
     */

    fun refreshCharacters(){
        val updatedCharacters = DMRealTimeGameActivity.charactersList

        updatedCharacters!!.forEach { updatedChar ->

            for(i in 0..fightingCharacters!!.size-1){
                if(fightingCharacters!![i].firebaseId == updatedChar.firebaseId){
                    fightingCharacters!![i] = updatedChar
                }
            }

        }

    }


    /*
     * This important function resets the adapter, and it's normally called whenever some data has changed.
     */

    fun refreshList(){


        refreshCharacters()
        val newAdapter = RT_CharactersCardListAdapter(myContext as DMRealTimeGameActivity, fightingCharacters!!)


        rt_fight_listview_characters.adapter = newAdapter

        val firstChar = rt_fight_listview_characters.adapter.getView(0, null, rt_fight_listview_characters)

        firstChar.setBackgroundColor(resources.getColor(R.color.purple_200))


        currentAdapter = newAdapter
    }


}