package com.example.ddrealtimemanager.shared

/* This data class stores temporarily the character's data, normally from the database
   or during character creation. */

data class Character (
    var id: Int,
    var name: String,
    var race: String,
    var clas: String,
    var desc: String
    ){
}