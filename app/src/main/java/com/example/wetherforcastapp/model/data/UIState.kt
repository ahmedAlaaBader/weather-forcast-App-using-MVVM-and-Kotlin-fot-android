package com.example.wetherforcastapp.model.data

import com.example.wetherforcastapp.model.data.database.currentweather.intyty.DataBaseEntity




// Sealed class to represent the various states of the UI
sealed class UIState {
    // Represents a successful state with the retrieved data
    class Success(val dataBaseEntity: DataBaseEntity) : UIState()

    // Represents a failure state with an error message
    class Failure(val msg: Throwable) : UIState()

    // Represents a loading state while data is being fetched
    object Loading : UIState()

    // Represents a state when no data is available
    object NoData : UIState()
}
