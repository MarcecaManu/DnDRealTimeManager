package com.example.ddrealtimemanager.shared.real_time

import android.app.Activity
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.URLUtil
import android.widget.Toast
import androidx.core.text.isDigitsOnly
import androidx.fragment.app.Fragment
import com.example.ddrealtimemanager.R
import kotlinx.android.synthetic.main.rt_character_creation_fragment.*
import java.lang.ClassCastException

class RT_CharacterCreationFragment(character: RT_Character, previousFrag: Int): Fragment(){

    //TODO
    private val newCharacter = character
    private val previousFragment = previousFrag

    private var addListener: OnCharacterCreationClick? = null
    private var backListener: OnCharacterCreationClick? = null

    interface OnCharacterCreationClick{
        fun onCharacterCreationAddSelected(character: RT_Character)
        fun onCharacterCreationBackSelected(previousFrag: Int, character: RT_Character?)
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if(context is Activity){
            addListener = context as OnCharacterCreationClick
            backListener = context as OnCharacterCreationClick
        }else {
            throw ClassCastException(context.toString()
                    + "must implement "
                    + "RT_CharacterCreationfragment.OnCharacterCreationClick")
        }
    }


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val view: View = inflater.inflate(R.layout.rt_character_creation_fragment, container, false)


        return view
    }



    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val new: Boolean

        if(newCharacter.level == -1000){
            rt_charcre_et_armorclass.setText("")
            rt_charcre_et_armorclass.setText("")
            rt_charcre_et_maxhp.setText("")
            rt_charcre_et_initiative.setText("")
            rt_charcre_btn_save.setText("Add")
        }else {
            rt_charcre_et_level.setText(newCharacter.level.toString())
            rt_charcre_et_armorclass.setText(newCharacter.ac.toString())
            rt_charcre_et_maxhp.setText(newCharacter.maxHp.toString())
            rt_charcre_et_initiative.setText(newCharacter.initiative.toString())
            rt_charcre_btn_save.setText("Edit")
        }

        rt_charcre_et_level.transformationMethod = null
        rt_charcre_et_armorclass.transformationMethod = null
        rt_charcre_et_maxhp.transformationMethod = null
        rt_charcre_et_initiative.transformationMethod = null

        rt_charcre_et_name.setText(newCharacter.name)

        rt_charcre_et_imageurl.setText(newCharacter.image)


        rt_charcre_btn_back.setOnClickListener{
            backListener?.onCharacterCreationBackSelected(previousFragment, newCharacter)
        }


        rt_charcre_btn_save.setOnClickListener{

            //DATA CHECK
            var errorList = arrayListOf<String>()

            val name = rt_charcre_et_name.text.toString().trim()
            val level = rt_charcre_et_level.text.toString().trim()
            val armorclass = rt_charcre_et_armorclass.text.toString().trim()
            val maxhp = rt_charcre_et_maxhp.text.toString().trim()
            val initiative = rt_charcre_et_initiative.text.toString().trim()
            val imageurl = rt_charcre_et_imageurl.text.toString().trim()

            //check!
            errorList.add(checkStatFieldValidity(name, "name"))
            errorList.add(checkStatFieldValidity(level, "level"))
            errorList.add(checkStatFieldValidity(armorclass, "armor class"))
            errorList.add(checkStatFieldValidity(maxhp, "max health points"))
            errorList.add(checkStatFieldValidity(initiative, "initiative"))
            errorList.add(checkStatFieldValidity(imageurl, "image"))

            var error = false

            errorList.forEach{
                if(it != ""){
                    error = true
                    Toast.makeText(activity, it, Toast.LENGTH_SHORT).show()
                }
            }

            if(!error){
                newCharacter.name = rt_charcre_et_name.text.toString()
                newCharacter.level = level.toInt()
                newCharacter.ac = armorclass.toInt()
                newCharacter.maxHp = maxhp.toInt()

                //currentHp must not be edited if maxHp is changed
                if(newCharacter.currentHp == -1000) {
                    newCharacter.currentHp = maxhp.toInt()
                }else if(newCharacter.currentHp > newCharacter.maxHp){
                    newCharacter.currentHp = newCharacter.maxHp
                }

                newCharacter.initiative = initiative.toInt()
                newCharacter.image = imageurl

                addListener?.onCharacterCreationAddSelected(newCharacter)
            }

        }

    }

    override fun onDetach() {
        super.onDetach()
        addListener = null
        backListener = null
    }

    private fun checkStatFieldValidity(field: String, type: String) : String {

        var result: String = ""

        //Check if the field contains a number, being it positive or negative, and if it's not an image
        if(!field.isBlank()){

            if((field.isDigitsOnly() || (field[0].equals('-') && field.substring(1,field.length).isDigitsOnly()) ) && type != "image") {
                when (type) {
                    "level" ->
                        if (field.toInt() < 1 || field.toInt() > 20) {
                            result = "The level field must be between 1 and 20!"
                        }

                    "hp" -> if(field.toInt() < 0){
                        result = "The health points field must be higher than 0!"
                    }
                }
            }else{

                if(type == "image"){
                    if(!field.isBlank() && !URLUtil.isValidUrl(field)){
                        result = "The image URL is invalid!"
                    }
                }else if(type == "name") {
                    if(field.isBlank()){
                        result = "The $type field must be filled!"
                    }else if(field.length > 15){
                        result = "The $type is too long!"
                    }
                }else {
                    result = "The $type field must contain numbers only!"
                }
            }
    }else{

        if(type == "image"){
            return result
        }
        result = "The $type field must be filled!"
        }

        return result
    }

}