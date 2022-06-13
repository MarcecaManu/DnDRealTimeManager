package com.example.ddrealtimemanager.shared

/* This data class stores temporarily the game's data, normally from the database
   or during game creation. */

data class Game (
    var id: Int,
    var name: String,
    var subtitle: String,
    var description: String,
    var password: String,
    var image: String,
    var firebaseId: String
    ){

}