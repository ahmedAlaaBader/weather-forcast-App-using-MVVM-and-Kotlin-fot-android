package com.example.wetherforcastapp.model.data.database

import android.content.Context
import com.example.wetherforcastapp.model.data.database.intyty.DataBaseEntity
import com.example.wetherforcastapp.model.data.database.intyty.EntityAlarm
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class LocalDataBaseImp(private val iWeatherDao: IWeatherDao,private val iAlarmDao: IAlarmDao) : ILocalDataBase {
    companion object {
        @Volatile
        private var instance: LocalDataBaseImp? = null

        fun getInstance(context: Context): LocalDataBaseImp {

            return instance ?: synchronized(this) {

                val weatherDao = WeatherDatabase.getInstance(context).iWeatherDao()
                val alarmDao = WeatherDatabase.getInstance(context).iAlarmDao()
                LocalDataBaseImp(weatherDao,alarmDao).also { instance = it }
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

    override fun getAllAlarm() = flow {
        iAlarmDao.getAllAlarm().collect { favList ->
            emit(favList)
        }
    }

    override suspend fun deleteByTime(time: String) = iAlarmDao.deleteByTime(time)

    override fun getAlarmByTime(time: String): Flow<EntityAlarm> = flow {
        iAlarmDao.getAlarmByTime(time).collect{
            emit(it)
        }
    }

    override suspend fun insertAlarm(entityAlarm: EntityAlarm) = iAlarmDao.insertAlarm(entityAlarm)
}
