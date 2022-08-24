package com.example.ddrealtimemanager.dm.real_time

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.ddrealtimemanager.R

class RT_StoredCharactersCardListFragment: Fragment(){

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater!!.inflate(R.layout.rt_stored_characters_list_fragment, container, false)
    }

}