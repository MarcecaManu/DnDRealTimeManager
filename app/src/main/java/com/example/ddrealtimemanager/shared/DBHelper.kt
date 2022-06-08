package com.example.ddrealtimemanager.shared

import android.content.ClipDescription
import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.util.Log

/* This class is used for communication between application and SQLite database. */


class DBHelper(var context: Context): SQLiteOpenHelper(context, "CharactersDB", null, 1) {

    private val CHAR_TABLE: String = "Character_Data"
    private val GAME_TABLE: String = "Game_Data"

    private val CHAR_ID = "CharID"
    private val CHAR_NAME = "Name"
    private val CHAR_RACE = "Race"
    private val CHAR_CLASS = "Class"
    private val CHAR_DESCRIPTION = "Description"

    private val GAME_ID = "GameID"
    private val GAME_NAME = "GameName"
    private val GAME_SUBTITLE = "GameSubtitle"
    private val GAME_DESCRIPTION = "GameDescription"
    private val GAME_PASSWORD = "GamePassword"
    private val GAME_IMAGE = "GameImage"


    val MAX_LENGTH_CHAR_NAME = 200
    val MAX_LENGTH_CHAR_RACE = 200
    val MAX_LENGTH_CHAR_CLASS = 200
    val MAX_LENGTH_CHAR_DESCRIPTION = 2000

    val MAX_LENGTH_GAME_NAME = 200
    val MAX_LENGTH_GAME_SUBTITLE = 300
    val MAX_LENGTH_GAME_DESCRIPTION = 2000
    val MAX_LENGTH_GAME_PASSWORD = 30
    val MAX_LENGTH_GAME_IMAGE = 3000

    /*Character Data - Table description
    *
    *This table contains:
    *   -CharID: A character identification id, autoincremented bt SQLite at every insert
    *   -Name: Character's name
    *   -Race: Character's race
    *   -Class: Character's class
    *   -Description: Character's description
    * */

    /*Game Data - Table description
    *
    *This table contains:
    *   -GameID: A game identification id, autoincremented bt SQLite at every insert
    *   -GameName: The game's name
    *   -GameDescription: The game's description
    *   -GamePassword: The game's access password
    * */


    override fun onCreate(db: SQLiteDatabase?) {

        //db?.execSQL("DROP TABLE IF EXISTS " + CHAR_TABLE)
        //db?.execSQL("DROP TABLE IF EXISTS " + GAME_TABLE)

        //Characters' table
        db?.execSQL("CREATE TABLE $CHAR_TABLE (" +
                "$CHAR_ID INTEGER PRIMARY KEY AUTOINCREMENT," +
                "$CHAR_NAME VARCHAR($MAX_LENGTH_CHAR_NAME) NOT NULL," +
                "$CHAR_RACE VARCHAR($MAX_LENGTH_CHAR_RACE)," +
                "$CHAR_CLASS VARCHAR($MAX_LENGTH_CHAR_CLASS)," +
                "$CHAR_DESCRIPTION VARCHAR($MAX_LENGTH_CHAR_DESCRIPTION)" +
                ")")

        //Games' table
        db?.execSQL("CREATE TABLE $GAME_TABLE (" +
                "$GAME_ID INTEGER PRIMARY KEY AUTOINCREMENT," +
                "$GAME_NAME VARCHAR($MAX_LENGTH_GAME_NAME) NOT NULL," +
                "$GAME_SUBTITLE VARCHAR($MAX_LENGTH_GAME_SUBTITLE)," +
                "$GAME_DESCRIPTION VARCHAR($MAX_LENGTH_GAME_DESCRIPTION)," +
                "$GAME_PASSWORD VARCHAR($MAX_LENGTH_GAME_PASSWORD)," +
                "$GAME_IMAGE VARCHAR($MAX_LENGTH_GAME_IMAGE)" +
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
        contentValues.put(CHAR_NAME, name)
        contentValues.put(CHAR_RACE, race)
        contentValues.put(CHAR_CLASS, clas)
        contentValues.put(CHAR_DESCRIPTION, description)
        db?.insert(CHAR_TABLE, null, contentValues)
        db.close()

    }


    /* Returns a ArrayList of Character items, based on the database contents.
    *
    * TO DO:
    * - Update attributes as character creation is developed
    */
    fun getStoredCharacters(): ArrayList<Character>{

        var charactersList: ArrayList<Character> = ArrayList()

        val db = this.readableDatabase
        val query = "select $CHAR_ID, * from $CHAR_TABLE"
        val cursor = db.rawQuery(query, null)

        val idIndex = cursor.getColumnIndex(CHAR_ID)
        val nameIndex = cursor.getColumnIndex(CHAR_NAME)
        val raceIndex = cursor.getColumnIndex(CHAR_RACE)
        val classIndex = cursor.getColumnIndex(CHAR_CLASS)
        val descIndex = cursor.getColumnIndex(CHAR_DESCRIPTION)

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
        db.close()

        return charactersList
    }

    fun getSpecificCharacter(charId: Int): Character {
        val db = this.readableDatabase
        val query = "select * " +
                "FROM $CHAR_TABLE " +
                "WHERE $CHAR_ID = $charId"
        val cursor = db.rawQuery(query, null)

        val nameIndex = cursor.getColumnIndex(CHAR_NAME)
        val raceIndex = cursor.getColumnIndex(CHAR_RACE)
        val classIndex = cursor.getColumnIndex(CHAR_CLASS)
        val descIndex = cursor.getColumnIndex(CHAR_DESCRIPTION)

        val character = Character(charId, "", "", "", "")

        if (cursor.moveToFirst()) {

            character.name = cursor.getString(nameIndex)
            character.race = cursor.getString(raceIndex)
            character.clas = cursor.getString(classIndex)
            character.desc = cursor.getString(descIndex)
        }
        cursor.close()
        db.close()

        return character
    }

    fun deleteCharacter(charId: Int): Boolean{
        val db = this.writableDatabase
        val result = db.delete(CHAR_TABLE, "$CHAR_ID = $charId", null) > 0
        db.close()
        return result
    }

    fun editCharacter(charId: Int, name: String, race: String, clas: String, description: String){
        val db = this.writableDatabase

        val values = ContentValues()
        values.put(CHAR_NAME, name)
        values.put(CHAR_RACE, race)
        values.put(CHAR_CLASS, clas)
        values.put(CHAR_DESCRIPTION, description)

        db.update(CHAR_TABLE, values, "$CHAR_ID = $charId", null)
        db.close()
    }


    fun writeNewGame(gameName: String, gameSubtitle: String, gameDescription: String, gamePassword: String, gameImage: String){
        val contentValues = ContentValues()
        val db = this.writableDatabase

        contentValues.put(GAME_NAME, gameName)
        contentValues.put(GAME_SUBTITLE, gameSubtitle)
        contentValues.put(GAME_DESCRIPTION, gameDescription)
        contentValues.put(GAME_PASSWORD, gamePassword)
        contentValues.put(GAME_IMAGE, gameImage)

        db?.insert(GAME_TABLE, null, contentValues)
        db.close()
    }

    fun getStoredGames(): ArrayList<Game>{
        var gamesList: ArrayList<Game> = ArrayList()

        val db = this.readableDatabase
        val query = "select $GAME_ID, * from $GAME_TABLE"
        val cursor = db.rawQuery(query, null)

        val idIndex = cursor.getColumnIndex(GAME_ID)
        val nameIndex = cursor.getColumnIndex(GAME_NAME)
        val subtitleIndex = cursor.getColumnIndex(GAME_SUBTITLE)
        val descrIndex = cursor.getColumnIndex(GAME_DESCRIPTION)
        val passIndex = cursor.getColumnIndex(GAME_PASSWORD)
        val imageIndex = cursor.getColumnIndex(GAME_IMAGE)

        if(cursor.moveToFirst()) {
            do {
                val game = Game(0, "", "", "", "", "")

                game.id = cursor.getInt(idIndex)
                game.name = cursor.getString(nameIndex)
                game.subtitle = cursor.getString(subtitleIndex)
                game.description = cursor.getString(descrIndex)
                game.password = cursor.getString(passIndex)
                game.image = cursor.getString(imageIndex)

                gamesList.add(game)

            } while (cursor.moveToNext())
            cursor.close()
        }
        db.close()

        return gamesList
    }

    fun getGame(gameId: Int): Game{
        val db = this.readableDatabase
        val query = "select * " +
                "FROM $GAME_TABLE " +
                "WHERE $GAME_ID = $gameId"
        val cursor = db.rawQuery(query, null)

        val gameNameIndex = cursor.getColumnIndex(GAME_NAME)
        val gameSubtIndex = cursor.getColumnIndex(GAME_SUBTITLE)
        val gameDescrIndex = cursor.getColumnIndex(GAME_DESCRIPTION)
        val gamePassIndex = cursor.getColumnIndex(GAME_PASSWORD)
        val gameImageIndex = cursor.getColumnIndex(GAME_IMAGE)

        val game = Game(gameId, "", "", "", "", "")

        if (cursor.moveToFirst()) {

            game.name = cursor.getString(gameNameIndex)
            game.subtitle = cursor.getString(gameSubtIndex)
            game.description = cursor.getString(gameDescrIndex)
            game.password = cursor.getString(gamePassIndex)
            game.image = cursor.getString(gameImageIndex)
        }
        cursor.close()
        db.close()

        return game
    }

    fun deleteGame(gameId: Int): Boolean{
        val db = this.writableDatabase
        val result = db.delete(GAME_TABLE, "$GAME_ID = $gameId", null) > 0
        db.close()
        return result
    }

    fun editGame(gameId: Int, gameName: String, gameSubtitle: String, gameDescription: String, gamePassword: String, gameImage: String){
        val db = this.writableDatabase
        val values = ContentValues()

        values.put(GAME_NAME, gameName)
        values.put(GAME_SUBTITLE, gameSubtitle)
        values.put(GAME_DESCRIPTION, gameDescription)
        values.put(GAME_PASSWORD, gamePassword)
        values.put(GAME_IMAGE, gameImage)

        db.update(GAME_TABLE, values, "$GAME_ID = $gameId", null)
        db.close()
    }


}