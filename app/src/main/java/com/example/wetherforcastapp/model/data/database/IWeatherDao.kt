package com.example.wetherforcastapp.model.data.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.wetherforcastapp.model.data.database.currentweather.intyty.DataBaseEntity
import kotlinx.coroutines.flow.Flow


@Dao
interface IWeatherDao {

    @Query("SELECT * FROM TWeather ")
     fun getAllFav(): Flow<List<DataBaseEntity>>
    @Query("SELECT * FROM TWeather WHERE address = :current ")
    fun getCurrent(current: String = "current"): Flow<DataBaseEntity>
    @Query("DELETE FROM TWeather WHERE address = :address")
    suspend fun deleteByAddress(address: String)
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertWeather(dataBaseEntity: DataBaseEntity)
}