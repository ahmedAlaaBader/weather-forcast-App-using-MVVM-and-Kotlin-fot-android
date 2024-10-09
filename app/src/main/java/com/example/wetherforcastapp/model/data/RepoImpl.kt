package com.example.wetherforcastapp.model.data

import com.example.wetherforcastapp.model.data.database.ILocalDataBase
import com.example.wetherforcastapp.model.data.database.LocalDataBaseImp
import com.example.wetherforcastapp.model.data.database.intyty.DataBaseEntity
import com.example.wetherforcastapp.model.data.database.intyty.EntityAlarm
import com.example.wetherforcastapp.model.data.network.IRemoteDataSource
import com.example.wetherforcastapp.model.data.network.IRemoteDataSourceImpl
import com.example.wetherforcastapp.model.data.network.forcastresponse.ForecastResponse
import com.example.wetherforcastapp.model.data.network.response.CurrentWeatherResponse
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class RepoImpl(
    private val remoteDataSource: IRemoteDataSource,
    private val localDataBase: ILocalDataBase
): IRepo {
    companion object{
        private var instance : RepoImpl ?=null
        fun getInstance (iRemoteDataSourceImpl: IRemoteDataSourceImpl,localDataBase: LocalDataBaseImp):RepoImpl{
            return instance ?: synchronized(this){
                val temp = RepoImpl(iRemoteDataSourceImpl,localDataBase)
                instance =temp
                temp
            }
        }}
    // Remote data source methods
    override fun getCurrentWeatherRemote(latitude: Double, longitude: Double, units: String, lang: String): Flow<CurrentWeatherResponse> {
        return remoteDataSource.getCurrentWeather(latitude, longitude, units, lang)
    }

    override fun getForecastWeatherRemote(latitude: Double, longitude: Double, units: String, lang: String): Flow<ForecastResponse> {
        return remoteDataSource.getForecastWeather(latitude, longitude, units, lang)
    }



    // Local data source methods
    override fun getCurrentWeatherLocal(): Flow<DataBaseEntity> {
        return localDataBase.getCurrent()
    }

    override fun getAllFavorites(): Flow<List<DataBaseEntity>> {
        return localDataBase.getAllFav()
    }

    override suspend fun upsertWeather(dataBaseEntity: DataBaseEntity) {
        localDataBase.upsertWeather(dataBaseEntity)
    }

    override suspend fun deleteWeatherByAddress(address: String) {
        localDataBase.deleteByAddress(address)
    }

    override fun getAllAlarm() = flow {
        localDataBase.getAllAlarm().collect { favList ->
            emit(favList)
        }
    }

    override suspend fun deleteByTime(time: String) = localDataBase.deleteByTime(time)

    override fun getAlarmByTime(time: String): Flow<EntityAlarm> = flow {
        localDataBase.getAlarmByTime(time).collect{
            emit(it)
        }
    }

    override suspend fun insertAlarm(entityAlarm: EntityAlarm) = localDataBase.insertAlarm(entityAlarm)
}
