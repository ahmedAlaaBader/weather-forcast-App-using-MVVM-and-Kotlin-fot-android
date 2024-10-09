package com.example.wetherforcastapp.ui.alarm.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.wetherforcastapp.model.data.IRepo

class AlarmViewModelFactory(private val repo: IRepo) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return if (modelClass.isAssignableFrom(AlarmViewModel::class.java)) {
            AlarmViewModel(repo) as T
        } else {
            throw IllegalArgumentException(" class view  model not found ")
        }
    }
}