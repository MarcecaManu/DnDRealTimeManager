package com.example.ddrealtimemanager.shared

import android.content.ContentValues.TAG
import android.util.Log
import com.example.ddrealtimemanager.shared.real_time.RT_Character
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import java.util.*
import kotlin.collections.ArrayList

class FireBaseHelper {

    val BASE_DIR = "dnd_realtime"
    val GAMES_DIR = "games"
    val PLAYERS_DIR = "players"

    val firebase = FirebaseDatabase.getInstance("https://dnd-real-time-manager-default-rtdb.europe-west1.firebasedatabase.app/")

    //DM SECTION

    val ref = firebase.getReference(BASE_DIR)
    val gamesRef = ref.child(GAMES_DIR)

    fun fbCreateNewGame(game: Game): String{
        val newGameRef = gamesRef.push()
        newGameRef.setValue(game)

        val fbGameId = newGameRef.key

        return fbGameId!!

    }

    fun fbUpdateGame(fbGameId: String, game: Game){
        val gameRef = gamesRef.child(fbGameId)
        gameRef.setValue(game)
    }

    fun fbResumeGame(id: String){
        TODO()
    }


    fun fbPushCharacter(character: RT_Character, fbGameId: String): String{

        val currentGameRef = gamesRef.child(fbGameId)
        val playersDirRef = currentGameRef.child(PLAYERS_DIR)

        val characterRef = playersDirRef.push()
        character.firebaseId = characterRef.key
        characterRef.setValue(character)

        val fbCharId = characterRef.key

        return fbCharId!!

    }


    fun test(str: String){
        val myRef = firebase.getReference("prova1")
        myRef.setValue(str)

        myRef.addValueEventListener(object: ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val value = snapshot.getValue(String::class.java)
                Log.d(TAG, "Value is $value")

            }

            override fun onCancelled(error: DatabaseError) {
                Log.w(TAG, "Failed to read", error.toException())
            }
        })
    }

}