package com.example.ddrealtimemanager.shared


import android.content.ContentValues
import android.content.Context

import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper


/* This class is used for communication between application and SQLite database. */


class DBHelper(var context: Context): SQLiteOpenHelper(context, "CharactersDB", null, 1) {

    private val CHAR_TABLE: String = "Character_Data"
    private val GAME_TABLE: String = "Game_Data"
    private val ID_TABLE: String = "Ids_Data"

    private val CHAR_ID = "CharID"
    private val CHAR_NAME = "Name"
    private val CHAR_RACE = "Race"
    private val CHAR_CLASS = "Class"
    private val CHAR_DESCRIPTION = "Description"
    private val CHAR_IMAGE = "Image"

    private val GAME_ID = "GameID"
    private val GAME_NAME = "GameName"
    private val GAME_SUBTITLE = "GameSubtitle"
    private val GAME_DESCRIPTION = "GameDescription"
    private val GAME_IMAGE = "GameImage"
    private val GAME_FIREBASE_ID = "FirebaseId"

    private val PLAYER_CHAR_ID = "PlayerCharId"
    private val PLAYER_FB_ID = "PlayerFbId"
    private val GAME_FB_ID = "GameFbId"


    val MAX_LENGTH_CHAR_NAME = 15
    val MAX_LENGTH_CHAR_RACE = 15
    val MAX_LENGTH_CHAR_CLASS = 15
    val MAX_LENGTH_CHAR_DESCRIPTION = 2000
    val MAX_LENGTH_CHAR_IMAGE = 10000

    val MAX_LENGTH_GAME_NAME = 200
    val MAX_LENGTH_GAME_SUBTITLE = 300
    val MAX_LENGTH_GAME_DESCRIPTION = 2000
    val MAX_LENGTH_GAME_IMAGE = 3000
    val MAX_LENGTH_GAME_FIREBASE_ID = 1000

    val MAX_LENGTH_CHAR_ID = 1000
    val MAX_LENGTH_FB_ID = 30

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
                "$CHAR_DESCRIPTION VARCHAR($MAX_LENGTH_CHAR_DESCRIPTION)," +
                "$CHAR_IMAGE VARCHAR($MAX_LENGTH_CHAR_IMAGE)" +
                ")")

        //Games' table
        db?.execSQL("CREATE TABLE $GAME_TABLE (" +
                "$GAME_ID INTEGER PRIMARY KEY AUTOINCREMENT," +
                "$GAME_NAME VARCHAR($MAX_LENGTH_GAME_NAME) NOT NULL," +
                "$GAME_SUBTITLE VARCHAR($MAX_LENGTH_GAME_SUBTITLE)," +
                "$GAME_DESCRIPTION VARCHAR($MAX_LENGTH_GAME_DESCRIPTION)," +
                "$GAME_IMAGE VARCHAR($MAX_LENGTH_GAME_IMAGE)," +
                "$GAME_FIREBASE_ID VARCHAR($MAX_LENGTH_GAME_FIREBASE_ID)" +
                ")")

        db?.execSQL("CREATE TABLE $ID_TABLE (" +
                "$GAME_FB_ID VARCHAR($MAX_LENGTH_FB_ID), " +
                "$PLAYER_FB_ID VARCHAR($MAX_LENGTH_FB_ID)," +
                "$PLAYER_CHAR_ID INTEGER($MAX_LENGTH_CHAR_ID))")

    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        db?.execSQL("DROP TABLE IF EXISTS " + CHAR_TABLE)
        db?.execSQL("DROP TABLE IF EXISTS " + GAME_TABLE)
        db?.execSQL("DROP TABLE IF EXISTS" + ID_TABLE)
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
    fun writeNewCharacter(character: Character){
        val contentValues = ContentValues()
        val db = this.writableDatabase
        contentValues.put(CHAR_NAME, character.name)
        contentValues.put(CHAR_RACE, character.race)
        contentValues.put(CHAR_CLASS, character.clas)
        contentValues.put(CHAR_DESCRIPTION, character.desc)
        contentValues.put(CHAR_IMAGE, character.image)
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
        val imageIndex = cursor.getColumnIndex(CHAR_IMAGE)

        if(cursor.moveToFirst()) {
            do {
                val character = Character(0, "", "", "", "", "")

                character.id = cursor.getInt(idIndex)
                character.name = cursor.getString(nameIndex)
                character.race = cursor.getString(raceIndex)
                character.clas = cursor.getString(classIndex)
                character.desc = cursor.getString(descIndex)
                character.image = cursor.getString(imageIndex)

                charactersList.add(character)

            } while (cursor.moveToNext())
            cursor.close()
        }
        db.close()

        return charactersList
    }



    //Returns the Character object associated with the given charId

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
        val imageIndex = cursor.getColumnIndex(CHAR_IMAGE)

        val character = Character(charId, "", "", "", "", "")

        if (cursor.moveToFirst()) {

            character.name = cursor.getString(nameIndex)
            character.race = cursor.getString(raceIndex)
            character.clas = cursor.getString(classIndex)
            character.desc = cursor.getString(descIndex)
            character.image = cursor.getString(imageIndex)
        }
        cursor.close()
        db.close()

        return character
    }

    //Deletes the character identified by the given charId from the database
    fun deleteCharacter(charId: Int): Boolean{
        val db = this.writableDatabase
        val result = db.delete(CHAR_TABLE, "$CHAR_ID = $charId", null) > 0
        db.close()
        return result
    }

    //Edits the character specified by the charId

    fun editCharacter(character: Character){
        val db = this.writableDatabase

        val values = ContentValues()
        values.put(CHAR_NAME, character.name)
        values.put(CHAR_RACE, character.race)
        values.put(CHAR_CLASS, character.clas)
        values.put(CHAR_DESCRIPTION, character.desc)
        values.put(CHAR_IMAGE, character.image)

        db.update(CHAR_TABLE, values, "$CHAR_ID = ${character.id}", null)
        db.close()
    }

    //Stores a new game in the database

    fun writeNewGame(game: Game){
        val contentValues = ContentValues()
        val db = this.writableDatabase

        contentValues.put(GAME_NAME, game.name)
        contentValues.put(GAME_SUBTITLE, game.subtitle)
        contentValues.put(GAME_DESCRIPTION, game.description)
        contentValues.put(GAME_IMAGE, game.image)
        contentValues.put(GAME_FIREBASE_ID, game.firebaseId)

        db?.insert(GAME_TABLE, null, contentValues)
        db.close()
    }


    //Returns an ArrayList of all the games stored in the database.
    fun getStoredGames(): ArrayList<Game>{
        var gamesList: ArrayList<Game> = ArrayList()

        val db = this.readableDatabase
        val query = "select $GAME_ID, * from $GAME_TABLE"
        val cursor = db.rawQuery(query, null)

        val idIndex = cursor.getColumnIndex(GAME_ID)
        val nameIndex = cursor.getColumnIndex(GAME_NAME)
        val subtitleIndex = cursor.getColumnIndex(GAME_SUBTITLE)
        val descrIndex = cursor.getColumnIndex(GAME_DESCRIPTION)
        val imageIndex = cursor.getColumnIndex(GAME_IMAGE)
        val firebaseIdIndex = cursor.getColumnIndex(GAME_FIREBASE_ID)

        if(cursor.moveToFirst()) {
            do {
                val game = Game(0, "", "", "", "", "")

                game.id = cursor.getInt(idIndex)
                game.name = cursor.getString(nameIndex)
                game.subtitle = cursor.getString(subtitleIndex)
                game.description = cursor.getString(descrIndex)
                game.image = cursor.getString(imageIndex)
                game.firebaseId = cursor.getString(firebaseIdIndex)

                gamesList.add(game)

            } while (cursor.moveToNext())
            cursor.close()
        }
        db.close()

        return gamesList
    }

    //Returns the game specified by the given gameId.
    fun getGame(gameId: Int): Game{
        val db = this.readableDatabase
        val query = "select * " +
                "FROM $GAME_TABLE " +
                "WHERE $GAME_ID = $gameId"
        val cursor = db.rawQuery(query, null)

        val gameNameIndex = cursor.getColumnIndex(GAME_NAME)
        val gameSubtIndex = cursor.getColumnIndex(GAME_SUBTITLE)
        val gameDescrIndex = cursor.getColumnIndex(GAME_DESCRIPTION)
        val gameImageIndex = cursor.getColumnIndex(GAME_IMAGE)
        val gameFirebaseIdIndex = cursor.getColumnIndex(GAME_FIREBASE_ID)


        val game = Game(gameId, "", "", "", "", "")

        if (cursor.moveToFirst()) {

            game.name = cursor.getString(gameNameIndex)
            game.subtitle = cursor.getString(gameSubtIndex)
            game.description = cursor.getString(gameDescrIndex)
            game.image = cursor.getString(gameImageIndex)
            game.firebaseId = cursor.getString(gameFirebaseIdIndex)
        }
        cursor.close()
        db.close()

        return game
    }

    //Deletes the game specified by the gameId.
    fun deleteGame(gameId: Int): Boolean{
        val db = this.writableDatabase
        val result = db.delete(GAME_TABLE, "$GAME_ID = $gameId", null) > 0

        //TODO DELETE GAME FROM FIREBASE

        db.close()
        return result
    }

    //Edits the game specified by the gameId.
    fun editGame(game: Game){
        val db = this.writableDatabase
        val values = ContentValues()

        values.put(GAME_NAME, game.name)
        values.put(GAME_SUBTITLE, game.subtitle)
        values.put(GAME_DESCRIPTION, game.description)
        values.put(GAME_IMAGE, game.image)

        db.update(GAME_TABLE, values, "$GAME_ID = ${game.id}", null)
        db.close()

        //TODO EDIT GAME IN FIREBASE

    }


    //Returns the row_id of the last inserted game. This function is used in
    //the game creation phase, so that the firebase id can be bound with
    //the game object.
    fun getLastId(): Int{
        val db = this.readableDatabase
        val query = "SELECT $GAME_ID from $GAME_TABLE order by $GAME_ID"
        var id: Int = -1

        val cursor = db.rawQuery(query, null)
        val index = cursor.getColumnIndex(GAME_ID)

        if (cursor.moveToLast()) {
            id = cursor.getInt(index)
        }

        cursor.close()
        db.close()

        return id
    }

    //Sets the firebase id associated with the game
    fun setFirebaseId(gameId: Int, fbGameId: String){
        val contentValues = ContentValues()
        val db = this.writableDatabase

        contentValues.put(GAME_FIREBASE_ID, fbGameId)

        db.update(GAME_TABLE, contentValues, "$GAME_ID = $gameId", null)
        db.close()
    }

    fun playerJoinGame(fbGameId: String, fbCharId: String, charId: Int){
        val contentValues = ContentValues()
        val db = this.writableDatabase

        contentValues.put(GAME_FB_ID, fbGameId)
        contentValues.put(PLAYER_FB_ID, fbCharId)
        contentValues.put(PLAYER_CHAR_ID, charId)


        db.insert(ID_TABLE, null, contentValues)

        db.close()
    }


    /*  This functions returns a list of triples, where the first element is the fbGameId, the second
     *  element the fbCharId of the player in that game, and the third element is the charId of the
     *  character related to the fbCharId
     */
    fun getPlayerGames(): ArrayList<Triple<String, String, Int>>{

        val gamesList: ArrayList<Triple<String, String, Int>> = ArrayList()

        val db = this.readableDatabase
        val query = "select $PLAYER_FB_ID, * from $ID_TABLE"
        val cursor = db.rawQuery(query, null)

        val fbGameIdIndex = cursor.getColumnIndex(GAME_FB_ID)
        val fbCharIdIndex = cursor.getColumnIndex(PLAYER_FB_ID)
        val charIdIndex = cursor.getColumnIndex(PLAYER_CHAR_ID)

        if(cursor.moveToFirst()) {
            do {

                val fbGameId = cursor.getString(fbGameIdIndex)
                val fbCharId = cursor.getString(fbCharIdIndex)
                val charId = cursor.getInt(charIdIndex)

                val triple: Triple<String, String, Int> = Triple(fbGameId, fbCharId, charId)

                gamesList.add(triple)

            } while (cursor.moveToNext())
            cursor.close()
        }
        db.close()

        return gamesList
    }


}