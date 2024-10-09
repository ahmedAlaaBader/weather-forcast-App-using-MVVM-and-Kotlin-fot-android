package com.example.wetherforcastapp.model.data

import com.example.wetherforcastapp.model.data.database.FakeLocalDataBaseImp
import com.example.wetherforcastapp.model.data.database.ILocalDataBase
import com.example.wetherforcastapp.model.data.network.FakeIRemoteDataSourceImpl
import com.example.wetherforcastapp.model.data.network.IRemoteDataSource
import com.example.wetherforcastapp.model.data.database.intyty.DataBaseEntity
import com.example.wetherforcastapp.model.data.database.currentweather.pojos.Clouds
import com.example.wetherforcastapp.model.data.database.currentweather.pojos.Coord
import com.example.wetherforcastapp.model.data.database.currentweather.pojos.Main
import com.example.wetherforcastapp.model.data.database.currentweather.pojos.Sys
import com.example.wetherforcastapp.model.data.database.currentweather.pojos.Weather
import com.example.wetherforcastapp.model.data.database.currentweather.pojos.Wind
import com.example.wetherforcastapp.model.data.database.forcastweather.ForecastItem
import com.example.wetherforcastapp.model.data.network.forcastresponse.ForecastResponse
import com.example.wetherforcastapp.model.data.network.response.CurrentWeatherResponse
import junit.framework.TestCase.assertEquals
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.runBlocking
import org.junit.Before
import org.junit.Test

class RepoImplTest {

    private lateinit var fakeLocalDataBaseImp: ILocalDataBase
    private lateinit var fakeIRemoteDataSourceImpl: IRemoteDataSource
    private lateinit var repoImpl: RepoImpl
    private val testAddress = "ismailia"

    private val currentWeatherResponse = CurrentWeatherResponse(
        base = "stations",
        clouds = Clouds(all = 100),
        cod = 200,
        coord = Coord(lon = 30.5, lat = 50.5),
        dt = 1625236900,
        id = 2172797,
        main = Main(25.5, 0, 0, 0, 0, 101.3, 25.5, 25.5),
        name = "ismailia",
        sys = Sys("EG", ""),
        timezone = 7200,
        visibility = 10000,
        weather = listOf(Weather(id = 801, main = "Clouds", description = "few clouds", icon = "02d")),
        wind = Wind(0, 5.5, 22.0)
    )

    private val forecastResponse = ForecastResponse(
        list = listOf(
            ForecastItem(main = Main(25.5, 0, 0, 0, 0, 101.3, 25.5, 25.5),
                weather = listOf(Weather(id = 801, main = "Clouds", description = "few clouds", icon = "02d")),
                dt_txt = "2024-10-08 12:00:00")
        )
    )

    private val testEntity = DataBaseEntity(
        address = testAddress,
        currentWeatherResponses = currentWeatherResponse,
        forecastResponse = forecastResponse
    )

    @Before
    fun setUp() {

        fakeLocalDataBaseImp = FakeLocalDataBaseImp()


        (fakeLocalDataBaseImp as FakeLocalDataBaseImp).addWeatherEntity(testEntity)


        fakeIRemoteDataSourceImpl = FakeIRemoteDataSourceImpl()


        repoImpl = RepoImpl(fakeIRemoteDataSourceImpl, fakeLocalDataBaseImp)
    }

    @Test
    fun getAllFav_returnLocal() = runBlocking {
        // When
        val result = repoImpl.getAllFavorites().toList()

        // Then
        assertEquals(listOf(testEntity), result.first())
    }
}
