package com.example.ddrealtimemanager.dm.real_time

import android.app.Activity
import android.content.Context
import android.graphics.Color
import android.graphics.PorterDuff
import android.graphics.drawable.*
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.core.graphics.toColor
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.example.ddrealtimemanager.R
import com.example.ddrealtimemanager.shared.Utils
import com.example.ddrealtimemanager.shared.real_time.RT_Character
import kotlinx.android.synthetic.main.layout_rt_player_card_item.view.*
import kotlinx.android.synthetic.main.rt_character_visualization_fragment.*
import kotlinx.android.synthetic.main.rt_character_visualization_fragment.view.*
import java.lang.ClassCastException

class RT_CharacterVisualizationfragment(selectedCharacterFBid: String) : Fragment() {


    private val character = DMRealTimeGameActivity.getSpecificFbCharacter(selectedCharacterFBid)

    private var backListener: OnCharVisualizationListener? = null
    private var deleteListener: OnCharVisualizationListener? = null
    private var editListener: OnCharVisualizationListener? = null

    interface OnCharVisualizationListener{
        fun onDeleteButtonSelected(fbCharId: String)
        fun onEditButtonSelected(character: RT_Character)

    }


    override fun onAttach(context: Context) {
        super.onAttach(context)
        if(context is Activity){
            backListener = context as OnCharVisualizationListener
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

        ////////////COLOR TEST
        //val healthBarColor = Utils().getProgressColorOnPercentage(percentageHP)
       // val convertedColor = Color.parseColor(healthBarColor)


        //val progressbarDrawable: LayerDrawable = rt_charvis_pb_healthbar_item.progressDrawable as LayerDrawable
        //val progressDrawable: Drawable = progressbarDrawable.getDrawable(1)

        //R.color.dynamic.progressDrawable.setColorFilter(ContextCompat.getColor(requireContext(), R.color.dynamic), PorterDuff.Mode.SRC_IN)

////////////////////////

        rt_charvis_tv_name.text = character.name
        rt_charvis_tv_race.text = character.race
        rt_charvis_tv_class.text = character.clas
        rt_charvis_tv_ac.text = "AC: " + character.ac.toString()
        rt_charvis_tv_level.text = "Level " + character.level.toString()
        rt_charvis_tv_initiative.text = "Init: " + character.initiative.toString()
        rt_charvis_tv_description.text = character.description




        //rt_charvis_btn_back.setOnClickListener{
        //    backListener?.onBackButtonSelected()
        //}


        rt_charvis_fab_delete.setOnClickListener{
            deleteListener?.onDeleteButtonSelected(character.firebaseId!!)
        }

        rt_charvis_fab_edit.setOnClickListener{
            editListener?.onEditButtonSelected(character)
        }


    }

    override fun onDetach() {
        super.onDetach()
        this.backListener = null
        this.deleteListener = null
        this.editListener = null
    }

}
