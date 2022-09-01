package com.example.ddrealtimemanager.dm.real_time

import CharacterListAdapter
import android.app.Activity
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ListView
import androidx.fragment.app.Fragment
import com.example.ddrealtimemanager.R
import com.example.ddrealtimemanager.shared.Character
import com.example.ddrealtimemanager.shared.DBHelper
import kotlinx.android.synthetic.main.activity_characters_card_list.*
import kotlinx.android.synthetic.main.rt_stored_charaters_list_fragment.*
import java.lang.ClassCastException

class RT_StoredCharactersCardListFragment: Fragment(){


    private lateinit var currentAdapter: CharacterListAdapter
    private var listener: OnStoredCharacterSelectedListener? = null
    private var backListener: OnStoredCharacterSelectedListener? = null



    var db: DBHelper? = null



    interface OnStoredCharacterSelectedListener{
        fun onStoredCharItemSelected(character: Character)
        fun onStoredListBackButtonSelected()
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if(context is Activity){
            listener = context as OnStoredCharacterSelectedListener
            backListener = context as OnStoredCharacterSelectedListener
            db = DBHelper(activity as Activity)
        }else {
            throw ClassCastException(context.toString()
                    + "must implement "
                    + "RT_StoredCharactersCardListFragment.OnStoredCharacterSelectedListener")
        }
    }



    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val view: View = inflater.inflate(R.layout.rt_stored_charaters_list_fragment, container, false)
        val lv: ListView = view.findViewById(R.id.rt_stored_characters_card_listview)

        currentAdapter = CharacterListAdapter(activity as Activity, db!!.getStoredCharacters())




        lv.setOnItemClickListener { parent, view, position, id ->
            //Change fragment passing the selected fbId
            val character = currentAdapter.getItem(position)
            listener?.onStoredCharItemSelected(character)
        }


        return view


    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        //rt_stored_characters_list_btn_back.setOnClickListener{
        //    backListener?.onStoredListBackButtonSelected()
        //}

        refreshList()
    }


    override fun onDetach() {
        super.onDetach()
        this.listener = null
        this.db = null


    }



    private fun refreshList(){
        currentAdapter = CharacterListAdapter(activity as Activity, db!!.getStoredCharacters())
        rt_stored_characters_card_listview.adapter = currentAdapter
    }


}