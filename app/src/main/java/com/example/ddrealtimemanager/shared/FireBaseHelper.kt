package com.example.ddrealtimemanager.shared

import android.content.ContentValues.TAG
import android.util.Log
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class FireBaseHelper {

    val firebase = FirebaseDatabase.getInstance("https://dnd-real-time-manager-default-rtdb.europe-west1.firebasedatabase.app/")

    //DM SECTION

    val ref = firebase.getReference("dnd_realtime")
    val gamesRef = ref.child("games")

    fun fbCreateNewGame(game: Game): String{
        val newGameRef = gamesRef.push()
        newGameRef.setValue(game)

        val fbGameId = newGameRef.key
        //TODO associate key with game in database using the gameid
        Log.i("Firebase ID", "Firebase ID is $fbGameId")

        return fbGameId!!

    }

    fun fbUpdateGame(fbGameId: String, game: Game){
        val gameRef = gamesRef.child(fbGameId)
        gameRef.setValue(game)
    }

    fun fbResumeGame(id: String){
        TODO()
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