package com.example.ddrealtimemanager.shared

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

/* This class is used for communication between application and SQLite database. */


class DBHelper(var context: Context): SQLiteOpenHelper(context, "CharactersDB", null, 1) {

    private val CHAR_TABLE: String = "Character_Data"
    private val GAME_TABLE: String = "Game_Data"

    val MAX_LENGTH_CHAR_NAME = 200
    val MAX_LENGTH_CHAR_RACE = 200
    val MAX_LENGTH_CHAR_CLASS = 200
    val MAX_LENGTH_CHAR_DESCRIPTION = 2000

    val MAX_LENGTH_GAME_NAME = 300
    val MAX_LENGTH_GAME_DESCRIPTION = 2000
    val MAX_LENGTH_GAME_PASSWORD = 30

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

        db?.execSQL("DROP TABLE IF EXISTS " + CHAR_TABLE)
        db?.execSQL("DROP TABLE IF EXISTS " + GAME_TABLE)

        //Characters' table
        db?.execSQL("CREATE TABLE ${CHAR_TABLE} (" +
                "CharID INTEGER PRIMARY KEY AUTOINCREMENT," +
                "Name VARCHAR($MAX_LENGTH_CHAR_NAME) NOT NULL," +
                "Race VARCHAR($MAX_LENGTH_CHAR_RACE)," +
                "Class VARCHAR($MAX_LENGTH_CHAR_CLASS)," +
                "Description VARCHAR($MAX_LENGTH_CHAR_DESCRIPTION)" +
                ")")

        //Games' table
        db?.execSQL("CREATE TABLE ${GAME_TABLE} (" +
                "GameID INTEGER PRIMARY KEY AUTOINCREMENT," +
                "GameName VARCHAR($MAX_LENGTH_GAME_NAME) NOT NULL," +
                "GameDescription VARCHAR($MAX_LENGTH_GAME_DESCRIPTION)," +
                "GamePassword VARCHAR($MAX_LENGTH_GAME_PASSWORD)" +
                ")")

    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        db?.execSQL("DROP TABLE IF EXISTS " + CHAR_TABLE)
        db?.execSQL("DROP TABLE IF EXISTS " + GAME_TABLE)
        onCreate(db)

    }

    fun resetDatabase(){                        //Resets the character's table
        val db = this.writableDatabase
        db?.execSQL("DROP TABLE IF EXISTS " + CHAR_TABLE)
        db?.execSQL("DROP TABLE IF EXISTS " + GAME_TABLE)
        onCreate(db)
    }

    /* Writes the character's information given as parameters in the database.
    *
    * TO DO:
        - Update attributes as character creation is developed
     */
    fun writeNewCharacter(name: String, race: String, clas: String, description: String){
        val contentValues = ContentValues()
        val db = this.writableDatabase
        contentValues.put("Name", name)
        contentValues.put("Race", race)
        contentValues.put("Class", clas)
        contentValues.put("Description", description)
        db?.insert(CHAR_TABLE, null, contentValues)

    }


    /* Returns a ArrayList of Character items, based on the database contents.
    *
    * TO DO:
    * - Update attributes as character creation is developed
    */
    fun getStoredCharacters(): ArrayList<Character>{

        var charactersList: ArrayList<Character> = ArrayList()

        val db = this.readableDatabase
        val query = "select CharID, * from $CHAR_TABLE"
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
            cursor.close()
        }

        return charactersList
    }

    fun getSpecificCharacter(charId: Int): Character {
        val db = this.readableDatabase
        val query = "select * " +
                "FROM $CHAR_TABLE " +
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
        cursor.close()

        return character
    }

    fun deleteCharacter(charId: Int): Boolean{
        val db = this.writableDatabase
        return db.delete(CHAR_TABLE, "CharID = $charId", null) > 0
    }

    fun editCharacter(charId: Int, name: String, race: String, clas: String, description: String){
        val db = this.writableDatabase

        val values = ContentValues()
        values.put("Name", name)
        values.put("Race", race)
        values.put("Class", clas)
        values.put("Description", description)

        db.update(CHAR_TABLE, values, "charID = $charId", null)
    }


    fun writeNewGame(){
        TODO()
    }

    fun getGame(){
        TODO()
    }

    fun deleteGame(){
        TODO()
    }

    fun editGame(){
        TODO()
    }


}