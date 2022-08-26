package com.example.ddrealtimemanager.dm.real_time

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.example.ddrealtimemanager.R
import com.example.ddrealtimemanager.shared.real_time.RT_Character
import kotlinx.android.synthetic.main.layout_rt_player_card_item.view.*

class RT_CharactersCardListAdapter(private val context: Context, private var charactersList: ArrayList<RT_Character>): BaseAdapter() {

    override fun getCount(): Int {
        return charactersList.count()
    }

    override fun getItem(position: Int): RT_Character {
        return charactersList[position]
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View? {

        var newView = convertView

        if(newView == null)
            newView = LayoutInflater.from(context).inflate(
                R.layout.layout_rt_player_card_item, parent, false
            )

        val character = charactersList[position]
        val requestOptions = RequestOptions()
            .placeholder(R.drawable.propic_standard)
            .error(R.drawable.propic_standard)

        Glide.with(context)
            .applyDefaultRequestOptions(requestOptions)
            .load(character.image)
            .into(newView!!.iv_rt_player_image)

        newView.tv_rt_player_name?.text = character.name
        newView.tv_rt_player_level?.text = "Lv ${character.level}"
        newView.tv_rt_player_hp?.text = "${character.currentHp} / ${character.maxHp} HP"
        newView.tv_rt_player_armor_class?.text = "AC ${character.ac}"
        val percentageHP = (character.currentHp.toFloat() / character.maxHp.toFloat() * 100.0f).toInt()
        newView.pb_rt_player_healthbar.progress = percentageHP

        return newView
    }


}