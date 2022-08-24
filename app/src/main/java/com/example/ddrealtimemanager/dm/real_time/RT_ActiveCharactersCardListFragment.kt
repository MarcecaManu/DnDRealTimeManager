package com.example.ddrealtimemanager.dm.real_time

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ListView
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import com.example.ddrealtimemanager.R
import com.example.ddrealtimemanager.shared.CharacterCreationActivity
import com.example.ddrealtimemanager.shared.CharacterVisualizationActivity
import com.example.ddrealtimemanager.shared.DBHelper
import com.example.ddrealtimemanager.shared.FireBaseHelper
import com.example.ddrealtimemanager.shared.real_time.RT_Character
import kotlinx.android.synthetic.main.activity_characters_card_list.*
import kotlinx.android.synthetic.main.activity_dmreal_time_game.*
import kotlinx.android.synthetic.main.rt_stored_characters_list_fragment.view.*
import java.lang.ClassCastException

class RT_ActiveCharactersCardListFragment: Fragment(){


    private lateinit var adapter: RT_CharactersCardListAdapter
    private var listener: OnActiveCharacterSelectedListener? = null

    private var dm = DMRealTimeGameActivity()


    interface OnActiveCharacterSelectedListener{
        fun onActiveCharItemSelected(fbCharId: String)
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if(context is Activity){
            listener = context as OnActiveCharacterSelectedListener
        }else {
            throw ClassCastException(context.toString()
                    + "must implement "
                    + "RT_ActiveCharactersCardListFragment.OnActiveCharacterSelectedListener")
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        //var activeCharacters: ArrayList<RT_Character> = dm.getFbCharacters()!!
        refreshList()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.rt_stored_characters_list_fragment, container, false)


    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val lv: ListView = view.rt_stored_characters_card_listview
        lv.adapter = adapter

        characters_card_list_view.setOnItemClickListener { parent, view, position, id ->
            //Change fragment passing the selected fbId
            val fbCharId = adapter.getItem(position).firebaseId
            listener?.onActiveCharItemSelected(fbCharId!!)
        }

    }

    override fun onDetach() {
        super.onDetach()
        this.listener = null
    }


    private fun refreshList(){
        adapter = RT_CharactersCardListAdapter(dm, dm.getFbCharacters()!!)
        characters_card_list_view.adapter = adapter
    }

}