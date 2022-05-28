package com.example.ddrealtimemanager.shared

class Utils {

    fun polishString(str: String, maxLength: Int): Any{
        val polishedString = str.trim()
        if (polishedString.length > maxLength){
            return false
        }
        return polishedString
    }

}