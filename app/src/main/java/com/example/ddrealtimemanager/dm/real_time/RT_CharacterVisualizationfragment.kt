package com.example.ddrealtimemanager.dm.real_time

import android.app.Activity
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
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

    private var listener: OnBackButtonClickListener? = null

    private var dm = DMRealTimeGameActivity()


    interface OnBackButtonClickListener{
        fun onBackButtonSelected()
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if(context is Activity){
            listener = context as OnBackButtonClickListener
        }else {
            throw ClassCastException(context.toString()
                    + "must implement "
                    + "RT_CharacterVisualizationfragment.OnBackButtonClickListener")
        }
    }


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        return inflater.inflate(R.layout.rt_character_visualization_fragment, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        rt_charvis_tv_name.text = character.name
        rt_charvis_tv_race.text = character.race
        rt_charvis_tv_class.text = character.clas
        rt_charvis_tv_description.text = character.description

        val requestOptions = RequestOptions()
            .placeholder(R.drawable.propic_standard)
            .error(R.drawable.propic_standard)

        Glide.with(this)
            .applyDefaultRequestOptions(requestOptions)
            .load(character.image)
            .into(rt_charvis_iv_charImage)

        rt_charvis_hp.text = "${character.currentHp} / ${character.maxHp}"

        rt_charvis_btn_back.setOnClickListener{
            listener?.onBackButtonSelected()
        }
    }

}
