package com.example.ddrealtimemanager.dm.real_time

import android.app.Activity
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.example.ddrealtimemanager.R
import com.example.ddrealtimemanager.shared.real_time.RT_Character
import kotlinx.android.synthetic.main.layout_rt_player_card_item.view.*
import kotlinx.android.synthetic.main.rt_character_visualization_fragment.*
import kotlinx.android.synthetic.main.rt_character_visualization_fragment.view.*
import java.lang.ClassCastException

class RT_CharacterVisualizationfragment(selectedCharacter: RT_Character) : Fragment() {


    private val character = selectedCharacter

    private var backListener: OnBackButtonClickListener? = null
    private var deleteListener: OnDeleteButtonClickListener? = null
    private var editListener: OnEditButtonClikListener? = null

    interface OnBackButtonClickListener{
        fun onBackButtonSelected()
    }

    interface OnDeleteButtonClickListener{
        fun onDeleteButtonSelected(fbCharId: String)
    }

    interface OnEditButtonClikListener{
        fun onEditButtonSelected()
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if(context is Activity){
            backListener = context as OnBackButtonClickListener
            deleteListener = context as OnDeleteButtonClickListener
            editListener = context as OnEditButtonClikListener
        }else {
            throw ClassCastException(context.toString()
                    + "must implement "
                    + "RT_CharacterVisualizationfragment.OnBackButtonClickListener, "
                    + "RT_CharacterVisualizationfragment.OnDeleteButtonClickListener and "
                    + "RT_CharacterVisualizationfragment.OnEditButtonClickListener")
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
            .load(character.image)
            .into(rt_charvis_iv_charImage)

        rt_charvis_hp.text = "${character.currentHp} / ${character.maxHp}"

        val percentageHP = (character.currentHp.toFloat() / character.maxHp.toFloat() * 100.0f).toInt()

        rt_charvis_pb_healthbar_item.progress = percentageHP

        rt_charvis_tv_name.text = character.name
        rt_charvis_tv_race.text = character.race
        rt_charvis_tv_class.text = character.clas
        rt_charvis_tv_ac.text = "AC: " + character.ac.toString()
        rt_charvis_tv_level.text = "Level " + character.level.toString()
        rt_charvis_tv_initiative.text = "Init: " + character.initiative.toString()
        rt_charvis_tv_description.text = character.description


        rt_charvis_btn_back.setOnClickListener{
            backListener?.onBackButtonSelected()
        }


        rt_charvis_fab_delete.setOnClickListener{
            deleteListener?.onDeleteButtonSelected(character.firebaseId!!)
        }

        rt_charvis_fab_edit.setOnClickListener{
            editListener?.onEditButtonSelected()
        }


    }

    override fun onDetach() {
        super.onDetach()
        this.backListener = null
        this.deleteListener = null
        this.editListener = null
    }

}
