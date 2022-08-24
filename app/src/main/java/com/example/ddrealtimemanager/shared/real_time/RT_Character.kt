package com.example.ddrealtimemanager.shared.real_time

data class RT_Character(
    var firebaseId: String?,
    var name: String,
    var race: String,
    var clas: String,
    var description: String,
    var image: String,

    var maxHp: Int,
    var currentHp: Int,

    var initiative: Int,
    var ac: Int,
    var level: Int
){
}