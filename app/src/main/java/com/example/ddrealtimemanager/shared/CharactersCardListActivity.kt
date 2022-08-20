package com.example.ddrealtimemanager.shared

import CharacterListAdapter
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.ddrealtimemanager.R
import com.example.ddrealtimemanager.dm.GameListAdapter
import com.example.ddrealtimemanager.dm.GameVisualizationActivity
import kotlinx.android.synthetic.main.activity_characters_card_list.*
import kotlinx.android.synthetic.main.activity_game_list.*

class CharactersCardListActivity : AppCompatActivity() {

    private lateinit var charAdapter: CharacterListAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_characters_card_list)

        refreshList()

        characters_card_list_view.setOnItemClickListener { parent, view, position, id ->
            val intent = Intent(this, CharacterVisualizationActivity::class.java)
            val charId = charAdapter.getItem(position).id
            intent.putExtra("charId", charId)
            startActivity(intent)
        }

        card_characters_list_fab.setOnClickListener{
            startActivity(Intent(this,CharacterCreationActivity::class.java))
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