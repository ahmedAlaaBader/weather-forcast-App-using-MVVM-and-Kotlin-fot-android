package com.example.wetherforcastapp.model.data.database

import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import android.content.Context
import androidx.room.TypeConverters
import com.example.wetherforcastapp.model.data.database.currentweather.intyty.CurrentWeatherResponsesConverter
import com.example.wetherforcastapp.model.data.database.currentweather.intyty.DataBaseEntity
import com.example.wetherforcastapp.model.data.database.currentweather.intyty.ForecastResponseConverter

@Database(entities = [DataBaseEntity::class], version = 1, exportSchema = false)
@TypeConverters(CurrentWeatherResponsesConverter::class, ForecastResponseConverter::class)
abstract class WeatherDatabase : RoomDatabase() {
    abstract fun iWeatherDao(): IWeatherDao

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
                    .fallbackToDestructiveMigration() // Handles migration issues by wiping and rebuilding the database
                    .build()
                instance = tempInstance
                tempInstance
            }
        }
    }
}
