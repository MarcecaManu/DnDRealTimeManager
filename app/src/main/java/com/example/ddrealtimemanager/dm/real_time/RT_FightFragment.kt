package com.example.ddrealtimemanager.dm.real_time

import android.app.Activity
import android.content.Context
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ListView
import androidx.fragment.app.Fragment
import com.example.ddrealtimemanager.R
import com.example.ddrealtimemanager.shared.real_time.RT_Character
import kotlinx.android.synthetic.main.activity_dmreal_time_game.*
import kotlinx.android.synthetic.main.layout_rt_player_card_item.view.*
import kotlinx.android.synthetic.main.rt_active_characters_list_fragment.*
import kotlinx.android.synthetic.main.rt_fight_fragment.*

class RT_FightFragment(fightersFBlist: ArrayList<String>): Fragment() {

    private lateinit var currentAdapter: RT_CharactersCardListAdapter
    private var listener: OnFightingCharacterSelectedListener? = null
    private var healDamageListener: OnFightingCharacterSelectedListener? = null

    private var selectedCharactersFBid: ArrayList<String> = ArrayList<String>()


    private var fightersFBlist = fightersFBlist
    private var fightingCharacters: ArrayList<RT_Character> = ArrayList()


    private var myContext: Context? = null


    interface OnFightingCharacterSelectedListener{
        fun onActiveCharItemSelected(fbCharId: String)
        fun onActiveCharItemMultipleSelection(fbCharIdList: ArrayList<String>, damage: Int)
    }


    //This functions works by setting the first element in the middle of the list

    fun orderFightersByInitiativeAndThrow(charactersFBlist: ArrayList<String>) : ArrayList<RT_Character>{

        val charactersListWithResults: ArrayList<Pair<String, Int>> = ArrayList()


        charactersFBlist.forEach{
            val character = DMRealTimeGameActivity.getSpecificFbCharacter(it)

            val thisCharactersResult = character!!.initiative + Dice(20).throwDice()

            charactersListWithResults.add(Pair(it, thisCharactersResult))
        }

        charactersListWithResults.sortBy { it.second }
        charactersListWithResults.reverse()



        val orderedCharactersList: ArrayList<RT_Character> = ArrayList()

        charactersListWithResults.forEach {

            val character = DMRealTimeGameActivity.getSpecificFbCharacter(it.first)
            Log.v("CHARACTERSORDERED", character!!.name + ": " + it.second.toString())

            orderedCharactersList.add(character!!)
        }

        return orderedCharactersList
    }


    //This functions shifts the characters list by 1 "to the right"
    fun previousFighterList(){
        val size = fightingCharacters.size-1

        var currentChar: RT_Character = fightingCharacters[0]
        var nextChar: RT_Character

        for(i in 0..size){

            if(i != size){
                nextChar = fightingCharacters[i+1]

                fightingCharacters[i+1] = currentChar

                currentChar = nextChar

            }else
            {
                fightingCharacters[0] = currentChar
            }

        }
    }

    fun nextFighterList(){
        val size = fightingCharacters.size-1

        var currentChar: RT_Character = fightingCharacters[size]
        var nextChar: RT_Character

        for(i in size downTo 0){

            if(i != 0){
                nextChar = fightingCharacters[i-1]

                fightingCharacters[i-1] = currentChar

                currentChar = nextChar

            }else
            {
                fightingCharacters[size] = currentChar
            }

        }
    }



    override fun onAttach(context: Context) {
        super.onAttach(context)
        if(context is Activity){
            listener = context as OnFightingCharacterSelectedListener
        }else {
            throw ClassCastException(context.toString()
                    + "must implement "
                    + "RT_FightGragment.OnFightingCharacterSelectedListener")
        }
    }



    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val view: View = inflater.inflate(R.layout.rt_fight_fragment, container, false)
        val lv: ListView = view.findViewById(R.id.rt_fight_listview_characters)

        fightingCharacters = orderFightersByInitiativeAndThrow(fightersFBlist)

        currentAdapter = RT_CharactersCardListAdapter(activity as DMRealTimeGameActivity, fightingCharacters)





        lv.setOnItemClickListener { parent, view, position, id ->

            val fbCharId = currentAdapter.getItem(position).firebaseId


            if(!DMRealTimeGameActivity.heal && !DMRealTimeGameActivity.damage) {
                //Change fragment passing the selected fbId
                listener?.onActiveCharItemSelected(fbCharId!!)

            }else {
                //Heal or Damage has been selected!

                var heal = DMRealTimeGameActivity.heal
                var damage = DMRealTimeGameActivity.damage


                if(selectedCharactersFBid.contains(fbCharId)){
                    selectedCharactersFBid.remove(fbCharId)
                    view.container1.setBackgroundColor(Color.WHITE)
                }
                else{
                    if(heal){view.container1.setBackgroundColor(Color.GREEN)}
                    if(damage){view.container1.setBackgroundColor(Color.RED)}

                    selectedCharactersFBid.add(fbCharId!!)
                }



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

    fun healDmgPressed(next: String, previous: String){

        if(next == "heal" || next == "damage"){
            //rt_active_characters_card_listview.choiceMode = ListView.CHOICE_MODE_MULTIPLE
            rt_fight_listview_characters.itemsCanFocus = false

            rt_fight_et_damageheal.transformationMethod = null
            rt_fight_et_damageheal.visibility = View.VISIBLE
            rt_fight_btn_damageheal.visibility = View.VISIBLE

            if(next == "heal"){
                if(previous == "damage"){refreshList()}
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



    fun refreshList(){
        val newAdapter = RT_CharactersCardListAdapter(myContext as DMRealTimeGameActivity, fightingCharacters)
        rt_fight_listview_characters.adapter = newAdapter

        rt_fight_listview_characters.post(Runnable { rt_fight_listview_characters.setSelection(0) })

        currentAdapter = newAdapter
    }

}