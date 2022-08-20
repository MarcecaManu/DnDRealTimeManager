package com.example.ddrealtimemanager.dm.real_time

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.example.ddrealtimemanager.R
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class DMRealTimeGameActivity : AppCompatActivity() {

    val extras = intent.extras
    val fbGameId = extras?.getString("fbGameId")

    val firebase = FirebaseDatabase.getInstance("https://dnd-real-time-manager-default-rtdb.europe-west1.firebasedatabase.app/")
    val gameRef = firebase.getReference(fbGameId!!)
    val playersRef = gameRef.child("players")


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_dmreal_time_game)

        gameRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val value = dataSnapshot.getValue(String::class.java)
            }

            override fun onCancelled(error: DatabaseError) {
                Log.w("firebase", "Failed to read value.", error.toException())
            }
        })

    }





}