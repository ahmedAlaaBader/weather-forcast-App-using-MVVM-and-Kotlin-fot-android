package com.example.wetherforcastapp.ui.alarm.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.wetherforcastapp.model.data.IRepo
import com.example.wetherforcastapp.model.data.UIState
import com.example.wetherforcastapp.model.data.database.intyty.EntityAlarm
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch

class AlarmViewModel (private val repo: IRepo) : ViewModel() {

    private val _uiState = MutableStateFlow<UIState>(UIState.Loading)
    val uiState: StateFlow<UIState> = _uiState

    init {
        fetchAllAlarms()
    }

    fun fetchAllAlarms() {
        viewModelScope.launch {
            repo.getAllAlarm()
                .catch { e ->
                    _uiState.value = UIState.Failure(e)
                }
                .collect { alarms ->
                    _uiState.value = UIState.Success(alarms)
                }
        }
    }

    fun insertalarm(entityAlarm: EntityAlarm) {
        viewModelScope.launch {
            repo.insertAlarm(entityAlarm)
            //fetchAllAlarms()
        }
    }

    fun deleteAlarm(time: String) {
        viewModelScope.launch {
            repo.deleteByTime(time)
            //fetchAllAlarms()
        }
    }
}