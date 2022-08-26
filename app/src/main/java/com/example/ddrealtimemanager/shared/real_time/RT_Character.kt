package com.example.ddrealtimemanager.shared.real_time

data class RT_Character(
    var firebaseId: String? = "",
    var name: String = "",
    var race: String = "",
    var clas: String = "",
    var description: String = "",
    var image: String = "",

    var maxHp: Int = -1,
    var currentHp: Int = -1,

    var initiative: Int = -1,
    var ac: Int = -1,
    var level: Int = -1
){
}