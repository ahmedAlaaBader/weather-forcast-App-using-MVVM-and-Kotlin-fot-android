import androidx.lifecycle.viewModelScope
import com.example.wetherforcastapp.model.data.FakeRepoImpl
import com.example.wetherforcastapp.model.data.IRepo
import com.example.wetherforcastapp.model.data.UIState
import com.example.wetherforcastapp.model.data.database.FakeLocalDataBaseImp
import com.example.wetherforcastapp.model.data.database.ILocalDataBase
import com.example.wetherforcastapp.model.data.database.intyty.DataBaseEntity
import com.example.wetherforcastapp.model.data.database.currentweather.pojos.Clouds
import com.example.wetherforcastapp.model.data.database.currentweather.pojos.Coord
import com.example.wetherforcastapp.model.data.database.currentweather.pojos.Main
import com.example.wetherforcastapp.model.data.database.currentweather.pojos.Sys
import com.example.wetherforcastapp.model.data.database.currentweather.pojos.Weather
import com.example.wetherforcastapp.model.data.database.currentweather.pojos.Wind
import com.example.wetherforcastapp.model.data.database.forcastweather.ForecastItem
import com.example.wetherforcastapp.model.data.network.FakeIRemoteDataSourceImpl
import com.example.wetherforcastapp.model.data.network.IRemoteDataSource
import com.example.wetherforcastapp.model.data.network.forcastresponse.ForecastResponse
import com.example.wetherforcastapp.model.data.network.response.CurrentWeatherResponse
import com.example.wetherforcastapp.ui.favorite.viewmodel.FavViewModel
import junit.framework.TestCase.assertEquals
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.runBlocking
import org.junit.Before
import org.junit.Test

class FavViewModelTest {
    private lateinit var fakeLocalDataBaseImp: ILocalDataBase
    private lateinit var fakeIRemoteDataSourceImpl: IRemoteDataSource
    private lateinit var repoImpl: IRepo
    private lateinit var viewModel: FavViewModel
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
            ForecastItem(
                main = Main(25.5, 0, 0, 0, 0, 101.3, 25.5, 25.5),
                weather = listOf(Weather(id = 801, main = "Clouds", description = "few clouds", icon = "02d")),
                dt_txt = "2024-10-08 12:00:00"
            )
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

        repoImpl = FakeRepoImpl(fakeIRemoteDataSourceImpl as FakeIRemoteDataSourceImpl,
            fakeLocalDataBaseImp as FakeLocalDataBaseImp
        )
        viewModel = FavViewModel(repoImpl)
    }

    @Test
    fun getAllFav_returnLocal() = runBlocking {

        val result = viewModel.uiState.toList()


        assertEquals(UIState.Success(listOf(testEntity)), result.first())
    }
}
