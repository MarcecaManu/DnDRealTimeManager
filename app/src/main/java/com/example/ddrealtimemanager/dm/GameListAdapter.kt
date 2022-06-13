package com.example.ddrealtimemanager.dm

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.example.ddrealtimemanager.R
import com.example.ddrealtimemanager.shared.Game
import kotlinx.android.synthetic.main.layout_game_card_item.view.*

class GameListAdapter(private val context: Context, private var gameList: List<Game>): BaseAdapter() {
    override fun getCount(): Int {
        return gameList.count()
    }

    override fun getItem(position: Int): Game {
        return gameList[position]
    }

    override fun getItemId(position: Int): Long {
        val game: Game = gameList[position]
        return game.id.toLong()
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View? {

        var newView = convertView

        if(newView == null)
            newView = LayoutInflater.from(context).inflate(
                R.layout.layout_game_card_item, parent, false
            )

        val game = gameList[position]
        val requestOptions = RequestOptions()
            .placeholder(R.drawable.ic_launcher_background)
            .error(R.drawable.ic_launcher_background)

        Glide.with(context)
            .applyDefaultRequestOptions(requestOptions)
            .load(game.image)
            .into(newView!!.game_image)

        newView.tv_game_title?.text = game.name
        newView.tv_game_subtitle?.text = game.subtitle + game.firebaseId

        return newView
    }

}