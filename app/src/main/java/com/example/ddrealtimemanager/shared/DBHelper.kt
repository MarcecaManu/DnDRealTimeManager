package com.example.ddrealtimemanager.shared

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

/* This class is used for communication between application and SQLite database. */


class DBHelper(var context: Context): SQLiteOpenHelper(context, "CharactersDB", null, 1) {

    private val TABLENAME: String  = "Character_Data"

    /*Character Data - Table description
    *
    *This table contains:
    *   -CharID: A character identification id, autoincremented bt SQLite at every insert
    *   -Name: Character's name
    *   -Race: Character's race
    *   -Class: Character's class
    *   -Description: Character's description
    * */


    override fun onCreate(db: SQLiteDatabase?) {
        db?.execSQL("CREATE TABLE ${TABLENAME} (" +
                "CharID INTEGER PRIMARY KEY AUTOINCREMENT," +
                "Name VARCHAR(200) NOT NULL," +
                "Race VARCHAR(100)," +
                "Class VARCHAR(100)," +
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
    fun writeData(name: String, race: String, clas: String, description: String){
        val contentValues = ContentValues()
        val db = this.writableDatabase
        contentValues.put("Name", name)
        contentValues.put("Race", race)
        contentValues.put("Class", clas)
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
        val query = "select CharID, * from $TABLENAME"
        val cursor = db.rawQuery(query, null)

        val idIndex = cursor.getColumnIndex("CharID")
        val nameIndex = cursor.getColumnIndex("Name")
        val raceIndex = cursor.getColumnIndex("Race")
        val classIndex = cursor.getColumnIndex("Class")
        val descIndex = cursor.getColumnIndex("Description")

        if(cursor.moveToFirst()) {
            do {
                val character = Character(0, "", "", "", "")

                character.id = cursor.getInt(idIndex)
                character.name = cursor.getString(nameIndex)
                character.race = cursor.getString(raceIndex)
                character.clas = cursor.getString(classIndex)
                character.desc = cursor.getString(descIndex)

                charactersList.add(character)

            } while (cursor.moveToNext())
        }

        return charactersList
    }

    fun getSpecificCharacter(charId: Int): Character {
        val db = this.readableDatabase
        val query = "select * " +
                "FROM $TABLENAME " +
                "WHERE CharID = $charId"
        val cursor = db.rawQuery(query, null)

        val nameIndex = cursor.getColumnIndex("Name")
        val raceIndex = cursor.getColumnIndex("Race")
        val classIndex = cursor.getColumnIndex("Class")
        val descIndex = cursor.getColumnIndex("Description")

        val character = Character(charId, "", "", "", "")

        if (cursor.moveToFirst()) {

            character.name = cursor.getString(nameIndex)
            character.race = cursor.getString(raceIndex)
            character.clas = cursor.getString(classIndex)
            character.desc = cursor.getString(descIndex)
        }

        return character
    }

    fun deleteCharacter(charId: Int): Boolean{
        val db = this.writableDatabase
        return db.delete(TABLENAME, "CharID = $charId", null) > 0
    }

    fun editCharacter(charId: Int, name: String, race: String, clas: String, description: String){
        val db = this.writableDatabase

        val values = ContentValues()
        values.put("Name", name)
        values.put("Race", race)
        values.put("Class", clas)
        values.put("Description", description)

        db.update(TABLENAME, values, "charID = $charId", null)
    }




}