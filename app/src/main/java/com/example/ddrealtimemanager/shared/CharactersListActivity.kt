package com.example.ddrealtimemanager.shared

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.SimpleAdapter
import com.example.ddrealtimemanager.R
import kotlinx.android.synthetic.main.activity_characters_list.*

/* This activity shows all the characters created by the user. It first checks the local
* database for the stored characters, and then prints them in a listview.
*
* TO DO:
* When a listview element is pressed, the app must display all the character's information, and
* also to buttons: edit and delete character.
* (fragments should be fine) */

class CharactersListActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_characters_list)

        val database = DBHelper(this)                           //recall database

        //charactersList is an arraylist which loads all the characters in the database and
        //stores them in a Character objects list.
        val charactersList: ArrayList<Character> = database.getStoredCharacters()


        /* The next lines of code are used to organize the data that must be shown on every
        * list's row.
        * For now I'm going with name, race, class and an image.
        *
        *TO DO:
        * - Needs a better design
        * - Implement image import
         */
        val data = ArrayList<HashMap<String, Any>>()

        for(character in charactersList){
            val item = HashMap<String, Any>()
            item["name"] = character.name
            item["race_class"] = character.race + " - " + character.clas
            data.add(item)
        }

        listView.adapter = SimpleAdapter(this, data,
            R.layout.list_row_items,
            arrayOf("name", "race_class"),
            intArrayOf(R.id.tvName, R.id.tvRace)
        )
    }
}