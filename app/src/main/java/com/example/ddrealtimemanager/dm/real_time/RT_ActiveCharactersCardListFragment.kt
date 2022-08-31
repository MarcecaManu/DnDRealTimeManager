package com.example.ddrealtimemanager.dm.real_time

import android.app.Activity
import android.content.Context
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ListView
import androidx.fragment.app.Fragment
import com.example.ddrealtimemanager.R
import kotlinx.android.synthetic.main.activity_dmreal_time_game.*
import kotlinx.android.synthetic.main.layout_rt_player_card_item.*
import kotlinx.android.synthetic.main.layout_rt_player_card_item.view.*
import kotlinx.android.synthetic.main.rt_active_characters_list_fragment.*

class RT_ActiveCharactersCardListFragment: Fragment(){


    private lateinit var currentAdapter: RT_CharactersCardListAdapter
    private var listener: OnActiveCharacterSelectedListener? = null
    private var healDamageListener: OnActiveCharacterSelectedListener? = null

    private var selectedCharactersFBid: ArrayList<String> = ArrayList<String>()

    private var myContext: Context? = null

    fun resetSelected(){
        this.selectedCharactersFBid.clear()
    }

    interface OnActiveCharacterSelectedListener{
        fun onActiveCharItemSelected(fbCharId: String)
        fun onActiveCharItemMultipleSelection(fbCharIdList: ArrayList<String>, damage: Int)
    }



    private var charactersAreSelected = false


    fun checkCharactersAreSelected(): Boolean{
        return charactersAreSelected
    }


    fun selectFighters(): ArrayList<String>{
        charactersAreSelected = false

        val fbIdsToSend: ArrayList<String> = ArrayList<String>()

        selectedCharactersFBid.forEach{
            fbIdsToSend.add(it)
        }

        selectedCharactersFBid.clear()

        return fbIdsToSend
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if(context is Activity){
            listener = context as OnActiveCharacterSelectedListener
        }else {
            throw ClassCastException(context.toString()
                    + "must implement "
                    + "RT_ActiveCharactersCardListFragment.OnActiveCharacterSelectedListener")
        }
    }



    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val view: View = inflater.inflate(R.layout.rt_active_characters_list_fragment, container, false)
        val lv: ListView = view.findViewById(R.id.rt_active_characters_card_listview)

        currentAdapter = RT_CharactersCardListAdapter(activity as DMRealTimeGameActivity, DMRealTimeGameActivity.getFbCharacters()!!)





        lv.setOnItemClickListener { parent, view, position, id ->

            val fbCharId = currentAdapter.getItem(position).firebaseId


            if(!DMRealTimeGameActivity.heal && !DMRealTimeGameActivity.damage) {

                if(DMRealTimeGameActivity.fightBtnSelected){


                    if(selectedCharactersFBid.contains(fbCharId)){
                        selectedCharactersFBid.remove(fbCharId)
                        view.container1.setBackgroundColor(Color.WHITE)

                        if(selectedCharactersFBid.isEmpty()){
                            charactersAreSelected = false
                        }

                    }else{
                        view.container1.setBackgroundColor(resources.getColor(R.color.purple_500))

                        selectedCharactersFBid.add(fbCharId!!)

                        charactersAreSelected = true
                    }


                }else{
                    //Change fragment passing the selected fbId
                    listener?.onActiveCharItemSelected(fbCharId!!)
                }
            }else{
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


                rt_active_characters_btn_dmgheal.setOnClickListener{
                    val value = rt_active_characters_et_damage_heal.text.toString().trim()
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

                    rt_active_characters_btn_dmgheal.setOnClickListener(null)

                    healDmgPressed("cancel", "none")
                }
            }
        }

        return view


    }

    fun healDmgPressed(next: String, previous: String){

        if(next == "heal" || next == "damage"){
            //rt_active_characters_card_listview.choiceMode = ListView.CHOICE_MODE_MULTIPLE
            rt_active_characters_card_listview.itemsCanFocus = false

            rt_active_characters_et_damage_heal.transformationMethod = null
            rt_active_characters_et_damage_heal.visibility = View.VISIBLE
            rt_active_characters_btn_dmgheal.visibility = View.VISIBLE

            if(next == "heal"){
                if(previous == "damage"){refreshList()}
                rt_active_characters_btn_dmgheal.setBackgroundResource(R.drawable.heal)
                rt_active_characters_et_damage_heal.hint = "Heal points"

            }else if(next == "damage"){
                if(previous == "heal"){refreshList()}
                rt_active_characters_btn_dmgheal.setBackgroundResource(R.drawable.damage)
                rt_active_characters_et_damage_heal.hint = "Damage points"
            }
        }
        else if(next == "cancel"){
            rt_active_characters_card_listview.itemsCanFocus = true
            rt_active_characters_et_damage_heal.visibility = View.GONE
            rt_active_characters_btn_dmgheal.visibility = View.GONE

            DMRealTimeGameActivity.heal = false
            DMRealTimeGameActivity.damage = false

            refreshList()
        }

        selectedCharactersFBid.clear()
        rt_active_characters_et_damage_heal.text.clear()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        //Hide damage/heal items
        rt_active_characters_et_damage_heal.visibility = View.GONE
        rt_active_characters_btn_dmgheal.visibility = View.GONE

        myContext = context

        refreshList()
    }


    override fun onDetach() {
        super.onDetach()
        this.listener = null
        this.healDamageListener = null

    }



    fun refreshList(){
        val newAdapter = RT_CharactersCardListAdapter(myContext as DMRealTimeGameActivity, DMRealTimeGameActivity.charactersList!!)
        rt_active_characters_card_listview.adapter = newAdapter
        currentAdapter = newAdapter
    }






}