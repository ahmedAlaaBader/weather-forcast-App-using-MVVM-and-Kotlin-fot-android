package com.example.wetherforcastapp.ui.alarm.view

import androidx.fragment.app.viewModels
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.wetherforcastapp.ui.alarm.viewmodel.AlarmViewModel
import com.example.wetherforcastapp.R

class alarmFragment : Fragment() {

    companion object {
        fun newInstance() = alarmFragment()
    }

    private val viewModel: AlarmViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // TODO: Use the ViewModel
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(R.layout.fragment_alarm, container, false)
    }
}