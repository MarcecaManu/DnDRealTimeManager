package com.example.ddrealtimemanager.shared.real_time

import android.app.Activity
import android.content.Context
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.example.ddrealtimemanager.R
import com.example.ddrealtimemanager.dm.real_time.DMRealTimeGameActivity
import com.example.ddrealtimemanager.player.real_time.PlayerRealTimeGameActivity
import kotlinx.android.synthetic.main.rt_character_visualization_fragment.*


class RT_CharacterVisualizationfragment(selectedCharacterFBid: String, dm: Boolean) : Fragment() {

    val isDm = dm

    private val character =
        if(dm) DMRealTimeGameActivity.getSpecificFbCharacter(selectedCharacterFBid)
        else PlayerRealTimeGameActivity.updatedCharacter


    private var deleteListener: OnCharVisualizationListener? = null
    private var editListener: OnCharVisualizationListener? = null

    interface OnCharVisualizationListener{
        fun onDeleteButtonSelected(fbCharId: String)
        fun onEditButtonSelected(character: RT_Character)

    }


    override fun onAttach(context: Context) {
        super.onAttach(context)
        if(context is Activity){
            deleteListener = context as OnCharVisualizationListener
            editListener = context as OnCharVisualizationListener
        }else {
            throw ClassCastException(context.toString()
                    + "must implement "
                    + "RT_CharacterVisualizationfragment.OnCharVisualizationListener")
        }
    }


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val view: View = inflater.inflate(R.layout.rt_character_visualization_fragment, container, false)



        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        rt_charvis_fab_delete.visibility = View.GONE
        rt_charvis_fab_edit.visibility = View.GONE

        if(!isDm && PlayerRealTimeGameActivity.fight){
            //Fight!
            setFightForPlayer()
            if(PlayerRealTimeGameActivity.yourTurn){
                setTurnLayout()
            }else{
                endTurnLayout()
            }
        }

        val requestOptions = RequestOptions()
            .placeholder(R.drawable.propic_standard)
            .error(R.drawable.propic_standard)

        Glide.with(this)
            .applyDefaultRequestOptions(requestOptions)
            .load(character!!.image)
            .into(rt_charvis_iv_charImage)

        rt_charvis_hp.text = "${character!!.currentHp} / ${character.maxHp}"

        val percentageHP = (character!!.currentHp.toFloat() / character.maxHp.toFloat() * 100.0f).toInt()

        rt_charvis_pb_healthbar_item.progress = percentageHP


        rt_charvis_tv_name.text = character.name
        rt_charvis_tv_race.text = character.race
        rt_charvis_tv_class.text = character.clas
        rt_charvis_tv_ac.text = "AC: " + character.ac.toString()
        rt_charvis_tv_level.text = "Level " + character.level.toString()
        rt_charvis_tv_initiative.text = "Init: " + character.initiative.toString()
        rt_charvis_tv_description.text = character.description



        rt_charvis_fab_more.setOnClickListener{
            if(rt_charvis_fab_delete.visibility == View.GONE){
                rt_charvis_fab_delete.visibility = View.VISIBLE
                rt_charvis_fab_edit.visibility = View.VISIBLE
            }else{
                rt_charvis_fab_delete.visibility = View.GONE
                rt_charvis_fab_edit.visibility = View.GONE
            }
        }

        rt_charvis_fab_delete.setOnClickListener{
            deleteListener?.onDeleteButtonSelected(character.firebaseId!!)
        }

        rt_charvis_fab_edit.setOnClickListener{
            editListener?.onEditButtonSelected(character)
        }


    }

    override fun onDetach() {
        super.onDetach()
        this.deleteListener = null
        this.editListener = null
    }

    fun setFightForPlayer(){
        rt_charvis_iv_charImage.background = resources.getDrawable(R.drawable.imageview_border)
        rt_charvis_fab_more.visibility = View.GONE
        rt_charvis_fab_delete.visibility = View.GONE
        rt_charvis_fab_edit.visibility = View.GONE
    }

    fun setTurnLayout(){
        rt_charvis_iv_charImage.background = resources.getDrawable(R.drawable.imageview_border_turn)

    }

    fun endTurnLayout(){
        rt_charvis_iv_charImage.background = resources.getDrawable(R.drawable.imageview_border)
    }

    fun damageAnimation(){

    }

    fun healAnimation(){

    }

    fun endFightForPlayer(){
        rt_charvis_iv_charImage.background = null
        rt_charvis_fab_more.visibility = View.VISIBLE
        //rt_charvis_fab_delete.visibility = View.GONE
        //rt_charvis_fab_edit.visibility = View.GONE
    }



}
