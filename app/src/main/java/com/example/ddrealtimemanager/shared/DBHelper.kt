package com.example.ddrealtimemanager.shared

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

/* This class is used for communication between application and SQLite database. */


class DBHelper(var context: Context): SQLiteOpenHelper(context, "CharactersDB", null, 1) {

    private val TABLENAME: String  = "Character_Data"

    override fun onCreate(db: SQLiteDatabase?) {
        db?.execSQL("CREATE TABLE ${TABLENAME} (" +
                "Name VARCHAR(100) NOT NULL," +
                "Race VARCHAR(100)," +
                "Description VARCHAR(1000)" +
                ")")
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        db?.execSQL("DROP TABLE IF EXISTS " + TABLENAME)
        onCreate(db)

    }

    fun resetDatabase(){                        //Resets the character's table
        val db = this.writableDatabase
        db?.execSQL("DROP TABLE IF EXISTS $TABLENAME")
        onCreate(db)
    }

    /* Writes the character's information given as parameters in the database.
    *
    * TO DO:
        - Update attributes as character creation is developed
     */
    fun writeData(name: String, race: String, description: String){
        val contentValues = ContentValues()
        val db = this.readableDatabase
        contentValues.put("Name", name)
        contentValues.put("Race", race)
        contentValues.put("Description", description)
        db?.insert(TABLENAME, null, contentValues)
    }

    /* Returns a ArrayList of Character items, based on the database contents.
    *
    * TO DO:
    * - Update attributes as character creation is developed
    */
    fun getStoredCharacters(): ArrayList<Character>{

        var charactersList: ArrayList<Character> = ArrayList()

        val db = this.readableDatabase
        val query = "select * from $TABLENAME"
        val cursor = db.rawQuery(query, null)

        val nameIndex = cursor.getColumnIndex("Name")
        val raceIndex = cursor.getColumnIndex("Race")
        val descIndex = cursor.getColumnIndex("Description")

        if(cursor.moveToFirst()) {
            do {
                val character = Character("", "", "", "")

                character.name = cursor.getString(nameIndex)
                character.race = cursor.getString(raceIndex)
                character.desc = cursor.getString(descIndex)

                charactersList.add(character)

            } while (cursor.moveToNext())
        }

        return charactersList
    }




}