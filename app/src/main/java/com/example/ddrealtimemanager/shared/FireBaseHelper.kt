package com.example.ddrealtimemanager.shared

import android.content.ContentValues.TAG
import android.util.Log
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class FireBaseHelper {

    val firebase = FirebaseDatabase.getInstance("https://dnd-real-time-manager-default-rtdb.europe-west1.firebasedatabase.app/")

    fun test(str: String){
        val myRef = firebase.getReference("prova1")
        myRef.setValue(str)

        myRef.addValueEventListener(object: ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val value = snapshot.getValue(String::class.java)
                Log.d(TAG, "Il valore è $value")

            }

            override fun onCancelled(error: DatabaseError) {
                Log.w(TAG, "Failed to read", error.toException())
            }
        })
    }

}