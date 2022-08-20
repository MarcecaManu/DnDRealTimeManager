package com.example.ddrealtimemanager.shared

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.SimpleAdapter
import com.example.ddrealtimemanager.R
import kotlinx.android.synthetic.main.activity_characters_list.*

/* This activity shows all the characters created by the user. It first checks the local
* database for the stored characters, and then prints them in a listview.
*/

class CharactersListActivity : AppCompatActivity() {

    lateinit var data: ArrayList<HashMap<String, Any>>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_characters_list)

        refreshData()           //Checks for the characters in the database and prints them
                                //on the listview

        listView.adapter = SimpleAdapter(this, data,
            R.layout.list_row_items,
            arrayOf("name", "race_class"),
            intArrayOf(R.id.tvListName, R.id.tvListRace)
        )

        fab.setOnClickListener{
            startActivity(Intent(this,CharacterCreationActivity::class.java))
        }

        listView.setOnItemClickListener { parent, view, position, id ->
            val intent = Intent(this, CharacterVisualizationActivity::class.java)
            val charId: Int = data[position].get("id").toString().toInt()
            intent.putExtra("charId", charId)
            startActivity(intent)
        }
    }


    //Refreshes the list when saving a new character from CharacterCreationActivity
    override fun onResume() {
        super.onResume()
        refreshData()
    }

    fun refreshData(){
        val database = DBHelper(this)
        data = ArrayList<HashMap<String, Any>>()

        //charactersList is an arraylist which loads all the characters in the database and
        //stores them in a Character objects list.
        val charactersList = database.getStoredCharacters()
        database.close()

        charactersList.sortBy { item -> item.name.uppercase() }

        /* The next lines of code are used to organize the data that must be shown on every
        * list's row.
        * For now I'm going with name, race, class and an image.
        *
        *TO DO:
        * - Needs a better design
        * - Implement image import
         */

        for(character in charactersList){
            val item = HashMap<String, Any>()
            item["name"] = character.name //+ " - id: " + character.id

            if(character.race != "" && character.clas != "") {
                item["race_class"] = character.clas + " " + character.race
            }else{
                item["race_class"] = character.race + character.clas
            }
            item["id"] = character.id
            data.add(item)
        }



        listView.adapter = SimpleAdapter(this, data,
            R.layout.list_row_items,
            arrayOf("name", "race_class"),
            intArrayOf(R.id.tvListName, R.id.tvListRace)
        )
    }


}