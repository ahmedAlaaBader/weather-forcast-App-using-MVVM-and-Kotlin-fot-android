package com.example.wetherforcastapp.ui.favorite.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.wetherforcastapp.model.data.IRepo

class FavViewModelFactory (private val repo: IRepo) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return if (modelClass.isAssignableFrom(FavViewModel::class.java)) {
            FavViewModel(repo) as T
        } else {
            throw IllegalArgumentException(" class view  model not found ")
        }
    }
}