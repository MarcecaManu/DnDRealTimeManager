package com.example.ddrealtimemanager.shared

class Utils {

    fun polishString(str: String, maxLength: Int): Any{
        val polishedString = str.trim()
        if (polishedString.length > maxLength){
            return false
        }
        return polishedString
    }


    //This function converts a percentage value in a color from red (0%) to green (100%).
    //It can be used to change the progressbar color based on the health value.
    fun getProgressColorOnPercentage(percentage: Int): String{
        val green: Int = percentage
        val red: Int = 100 - percentage

        val greenRGBformat = ((255 * green).toFloat() / 100.0f).toInt()
        val redRGBformat = ((255 * red).toFloat() / 100.0f).toInt()

        val color = String.format("#%02x%02x%02x", redRGBformat, greenRGBformat, 0)

        return color
    }

}