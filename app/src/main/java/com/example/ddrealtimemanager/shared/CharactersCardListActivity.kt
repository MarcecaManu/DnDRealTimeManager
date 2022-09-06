package com.example.ddrealtimemanager.shared

import CharacterListAdapter
import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.example.ddrealtimemanager.R
import kotlinx.android.synthetic.main.activity_characters_card_list.*


class CharactersCardListActivity : AppCompatActivity() {

    private lateinit var charAdapter: CharacterListAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_characters_card_list)

        refreshList()

        val extras = intent.extras
        var join = false

        if(extras != null){
            if(extras.getBoolean("join")){
                //Choosing a character for joining a game!
                join = true
                card_characters_list_fab.visibility = View.GONE
            }
        }

        characters_card_list_view.setOnItemClickListener { parent, view, position, id ->

            val intent = Intent(this, CharacterVisualizationActivity::class.java)
            val charId = charAdapter.getItem(position).id
            intent.putExtra("charId", charId)

            if(join) {
                intent.putExtra("join", true)
            }else{
                intent.putExtra("join", false)
            }

            startActivity(intent)

        }

        card_characters_list_fab.setOnClickListener {
            startActivity(Intent(this, CharacterCreationActivity::class.java))
        }



    }

    override fun onResume() {
        super.onResume()

        refreshList()
    }

    private fun refreshList(){
        val db = DBHelper(this)
        charAdapter = CharacterListAdapter(this, db.getStoredCharacters())
        characters_card_list_view.adapter = charAdapter
    }

}