package com.example.ddrealtimemanager.dm.real_time

import android.graphics.Color
import android.os.Bundle
import android.text.Html
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.ddrealtimemanager.R
import kotlinx.android.synthetic.main.rt_dice_fragment.*
import kotlin.collections.ArrayList
import kotlin.random.Random

class RT_DiceFragment(prevFragment: Int): Fragment() {

    val previousFragment = prevFragment

    var diceToThrow: ArrayList<Dice> = ArrayList<Dice>()


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val view: View = inflater.inflate(R.layout.rt_dice_fragment, container, false)



        return view
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        rt_dice_btn_d4.setOnClickListener{
            diceToThrow.add(Dice(4))

            val currentThrows = rt_dice_tv_d4.text

            if(currentThrows.isBlank()){
                rt_dice_tv_d4.text = "x1"
            }else{
                val newValue = currentThrows.substring(1,currentThrows.length).toInt() + 1
               rt_dice_tv_d4.text = "x" + newValue.toString()
            }
        }

        rt_dice_btn_d6.setOnClickListener{
            diceToThrow.add(Dice(6))

            val currentThrows = rt_dice_tv_d6.text

            if(currentThrows.isBlank()){
                rt_dice_tv_d6.text = "x1"
            }else{
                val newValue = currentThrows.substring(1,currentThrows.length).toInt() + 1
                rt_dice_tv_d6.text = "x" + newValue.toString()
            }

        }

        rt_dice_btn_d8.setOnClickListener{
            diceToThrow.add(Dice(8))

            val currentThrows = rt_dice_tv_d8.text

            if(currentThrows.isBlank()){
                rt_dice_tv_d8.text = "x1"
            }else{
                val newValue = currentThrows.substring(1,currentThrows.length).toInt() + 1
                rt_dice_tv_d8.text = "x" + newValue.toString()
            }
        }

        rt_dice_btn_d10.setOnClickListener{
            diceToThrow.add(Dice(10))

            val currentThrows = rt_dice_tv_d10.text

            if(currentThrows.isBlank()){
                rt_dice_tv_d10.text = "x1"
            }else{
                val newValue = currentThrows.substring(1,currentThrows.length).toInt() + 1
                rt_dice_tv_d10.text = "x" + newValue.toString()
            }
        }

        rt_dice_btn_d12.setOnClickListener{
            diceToThrow.add(Dice(12))

            val currentThrows = rt_dice_tv_d12.text

            if(currentThrows.isBlank()){
                rt_dice_tv_d12.text = "x1"
            }else{
                val newValue = currentThrows.substring(1,currentThrows.length).toInt() + 1
                rt_dice_tv_d12.text = "x" + newValue.toString()
            }
        }

        rt_dice_btn_d20.setOnClickListener{
            diceToThrow.add(Dice(20))

            val currentThrows = rt_dice_tv_d20.text

            if(currentThrows.isBlank()){
                rt_dice_tv_d20.text = "x1"
            }else{
                val newValue = currentThrows.substring(1,currentThrows.length).toInt() + 1
                rt_dice_tv_d20.text = "x" + newValue.toString()
            }
        }




        rt_dice_throw.setOnClickListener{


            //CLEAR EVERYTHING
            clearDice()


            if(diceToThrow.isEmpty()){
                rt_dice_tv_result.text = ""

                rt_dice_tv_result_allthrows.text = ""
            }else{

                var result = 0

                var countedThrows = ""

                val onlyOneDiceThrown = diceToThrow.size == 1

                diceToThrow.forEach{
                    val throwResult = it.throwDice()
                    var color = "#000000"       //black

                    if(throwResult == it.diceType){
                        color = "#FFA600"        //gold
                    }
                    if(throwResult == 1){
                        color = "#FF4040"        //red
                    }

                    if(onlyOneDiceThrown){rt_dice_tv_result.setTextColor(Color.parseColor(color))}

                    result += throwResult

                    if(countedThrows.isBlank()){
                        countedThrows += "<font color=$color>$throwResult</font>"
                    }else{
                        countedThrows += " + <font color=$color>$throwResult</font>"
                    }
                }


                rt_dice_tv_result.text = result.toString()

                if(!onlyOneDiceThrown) {
                    rt_dice_tv_result_allthrows.text = Html.fromHtml(countedThrows)
                }

                diceToThrow.clear()
            }

        }


    }

    fun clearDice(){

        rt_dice_tv_d4.text = ""
        rt_dice_tv_d6.text = ""
        rt_dice_tv_d8.text = ""
        rt_dice_tv_d10.text = ""
        rt_dice_tv_d12.text = ""
        rt_dice_tv_d20.text = ""
        rt_dice_tv_result_allthrows.text = ""

        rt_dice_tv_result.setTextColor(resources.getColor(R.color.purple_500))

    }

}


class Dice(number: Int){

    val diceType = number

    fun throwDice(): Int{
        return Random.nextInt(1, diceType+1)
    }

}