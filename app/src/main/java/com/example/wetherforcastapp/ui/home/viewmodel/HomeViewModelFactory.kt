package com.example.wetherforcastapp.ui.home.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.wetherforcastapp.model.data.IRepo
import com.example.wetherforcastapp.model.data.RepoImpl

class HomeViewModelFactory (private val repo: IRepo) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return if (modelClass.isAssignableFrom(HomeViewModel::class.java)) {
            HomeViewModel(repo) as T
        } else {
            throw IllegalArgumentException(" class view  model not found ")
        }
    }
}