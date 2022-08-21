package com.example.ddrealtimemanager.shared.real_time

data class RT_Character(
    var firebaseId: Int,
    var name: String,
    var race: String,
    var clas: String,
    var desc: String,

    var hp: Int,
    var initiative: Int,
    var ac: Int
){
}