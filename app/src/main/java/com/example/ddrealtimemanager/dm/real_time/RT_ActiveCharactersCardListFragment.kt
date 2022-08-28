package com.example.ddrealtimemanager.dm.real_time

import android.app.Activity
import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ListView
import androidx.fragment.app.Fragment
import com.example.ddrealtimemanager.R
import kotlinx.android.synthetic.main.rt_active_characters_list_fragment.*
import java.lang.ClassCastException

class RT_ActiveCharactersCardListFragment: Fragment(){


    private lateinit var currentAdapter: RT_CharactersCardListAdapter
    private var listener: OnActiveCharacterSelectedListener? = null


    override fun onStart() {
        super.onStart()

        Log.v("LIFEOFACTIVITY", "Fragment - OnStart")
    }

    override fun onResume() {
        super.onResume()

        Log.v("LIFEOFACTIVITY", "Fragment - OnResume")

    }

    override fun onDestroy() {
        super.onDestroy()

        Log.v("LIFEOFACTIVITY", "Fragment - OnDestroy")

    }

    override fun onStop() {
        super.onStop()

        Log.v("LIFEOFACTIVITY", "Fragment - OnStop")
    }


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



    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val view: View = inflater.inflate(R.layout.rt_active_characters_list_fragment, container, false)
        val lv: ListView = view.findViewById(R.id.rt_active_characters_card_listview)

        currentAdapter = RT_CharactersCardListAdapter(activity as DMRealTimeGameActivity, DMRealTimeGameActivity.getFbCharacters()!!)




        lv.setOnItemClickListener { parent, view, position, id ->
            //Change fragment passing the selected fbId
            val fbCharId = currentAdapter.getItem(position).firebaseId
            listener?.onActiveCharItemSelected(fbCharId!!)
        }


        return view


    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        refreshList()
    }


    override fun onDetach() {
        super.onDetach()
        this.listener = null

    }



    fun refreshList(){
        val newAdapter = RT_CharactersCardListAdapter(activity as DMRealTimeGameActivity, DMRealTimeGameActivity.charactersList!!)
        rt_active_characters_card_listview.adapter = newAdapter
        currentAdapter = newAdapter
    }




}