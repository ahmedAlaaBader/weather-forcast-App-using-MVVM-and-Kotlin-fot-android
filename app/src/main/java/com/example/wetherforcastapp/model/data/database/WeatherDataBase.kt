package com.example.wetherforcastapp.model.data.database

import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import android.content.Context
import androidx.room.TypeConverters
import com.example.wetherforcastapp.model.data.database.intyty.CurrentWeatherResponsesConverter
import com.example.wetherforcastapp.model.data.database.intyty.DataBaseEntity
import com.example.wetherforcastapp.model.data.database.intyty.EntityAlarm
import com.example.wetherforcastapp.model.data.database.intyty.ForecastResponseConverter

@Database(entities = [DataBaseEntity::class,EntityAlarm::class], version = 2, exportSchema = false)
@TypeConverters(CurrentWeatherResponsesConverter::class, ForecastResponseConverter::class)
abstract class WeatherDatabase : RoomDatabase() {
    abstract fun iWeatherDao(): IWeatherDao
    abstract fun iAlarmDao():IAlarmDao
    companion object {
        @Volatile
        private var instance: WeatherDatabase? = null

        fun getInstance(context: Context): WeatherDatabase {
            return instance ?: synchronized(this) {
                val tempInstance = Room.databaseBuilder(
                    context.applicationContext,
                    WeatherDatabase::class.java,
                    "TWeather"
                )
                    .fallbackToDestructiveMigration()
                    .build()
                instance = tempInstance
                tempInstance
            }
        }
    }
}
