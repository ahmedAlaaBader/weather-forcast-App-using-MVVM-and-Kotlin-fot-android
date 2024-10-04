package com.example.wetherforcastapp.model.data

import com.example.wetherforcastapp.model.data.network.response.CurrentWeatherResponse
import com.example.wetherforcastapp.model.data.network.response.ForecastResponse
import com.jakewharton.retrofit2.adapter.kotlin.coroutines.CoroutineCallAdapterFactory
import kotlinx.coroutines.Deferred
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query

 private const val API_KEY="23fcb7358724ab32ace0a9e4b2398cf1"

interface ApiWeatherService {
    @GET("weather")
    fun getCurrentWeather(@Query("lat") latitude: Double,
                          @Query("lon") longitude: Double,
                          @Query("units") units: String = "metric",
                          @Query("lang") lang: String = "en"):Deferred<CurrentWeatherResponse>
    @GET("forecast")
    fun getForecastWeather(@Query("lat") latitude: Double,
                          @Query("lon") longitude: Double,
                          @Query("units") units: String = "metric",
                          @Query("lang") lang: String = "en"):Deferred<ForecastResponse>

companion object{
    private const val BASE_URL = "https://api.openweathermap.org/data/2.5/"
        operator fun invoke():ApiWeatherService{
            val requestInterceptor = Interceptor{
                chain -> val url =chain
                    .request()
                    .url()
                    .newBuilder()
                    .addQueryParameter("appid",API_KEY)
                    .build()
                val request = chain.request()
                    .newBuilder()
                    .url(url)
                    .build()
                return@Interceptor chain.proceed(request)

            }
            val okHttpClient = OkHttpClient
                .Builder()
                .addInterceptor(requestInterceptor)
                .build()
            return Retrofit
                .Builder()
                .client(okHttpClient)
                .baseUrl(BASE_URL)
                .addCallAdapterFactory(CoroutineCallAdapterFactory())
                .addConverterFactory(GsonConverterFactory.create())
                .build()
                .create(ApiWeatherService::class.java)
        }
}
}