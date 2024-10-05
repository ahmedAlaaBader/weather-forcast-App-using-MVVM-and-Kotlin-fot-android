package com.example.wetherforcastapp.model.data.database

import android.content.Context
import com.example.wetherforcastapp.model.data.database.currentweather.intyty.DataBaseEntity
import kotlinx.coroutines.flow.Flow

class LocalDataBaseImp(private val iWeatherDao: IWeatherDao) : ILocalDataBase {
    companion object {
        @Volatile
        private var instance: LocalDataBaseImp? = null

        fun getInstance(context: Context): LocalDataBaseImp {
            // Prevent recursion by calling the correct database instance
            return instance ?: synchronized(this) {
                // Retrieve the DAO from the Room database instance
                val dao = WeatherDatabase.getInstance(context).iWeatherDao()  // Correct method to call
                LocalDataBaseImp(dao).also { instance = it }  // Initialize LocalDataBaseImp with DAO
            }
        }
    }

    override fun getAllFav(): Flow<List<DataBaseEntity>> = iWeatherDao.getAllFav()

    override fun getCurrent(current: String): Flow<DataBaseEntity> = iWeatherDao.getCurrent(current)

    override suspend fun deleteByAddress(address: String) {
        iWeatherDao.deleteByAddress(address)
    }

    override suspend fun upsertWeather(dataBaseEntity: DataBaseEntity) {
        iWeatherDao.upsertWeather(dataBaseEntity)
    }
}
