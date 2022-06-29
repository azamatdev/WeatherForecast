package com.bekhruz.weatherforecast.repositories


import com.bekhruz.weatherforecast.data.network.SixteenDayForecastApi
import com.bekhruz.weatherforecast.data.network.CurrentWeatherApi
import com.bekhruz.weatherforecast.data.network.GeocodingApi
import com.bekhruz.weatherforecast.data.network.currentweather.CurrentForecast
import com.bekhruz.weatherforecast.data.network.geocoding.Location
import com.bekhruz.weatherforecast.data.network.sixteendayweather.SixteenDayForecast
import com.bekhruz.weatherforecast.utils.Constants.API_KEY_CURRENT_WEATHER
import com.bekhruz.weatherforecast.utils.Constants.API_KEY_GEOCODING
import com.bekhruz.weatherforecast.utils.Constants.API_KEY_SIXTEEN_DAY_WEATHER
import retrofit2.Response
import javax.inject.Inject

interface WeatherRepository{
    suspend fun getCurrentWeather(latLon: String): Response<CurrentForecast>
    suspend fun getSixteenDayWeather(
        latitude: String,
        longitude: String
    ): Response<SixteenDayForecast>
    suspend fun getFullLocationInfo(location: String): Response<Location>

}
class WeatherRepositoryImpl @Inject constructor():WeatherRepository  {
    override suspend fun getCurrentWeather(latLon: String): Response<CurrentForecast> {
        return CurrentWeatherApi.retrofitService.getCurrentWeather(
            API_KEY_CURRENT_WEATHER,
            latLon,
            3,
            "no",
            "no"
        )
    }

    override suspend fun getSixteenDayWeather(
        latitude: String,
        longitude: String
    ): Response<SixteenDayForecast> {
        return SixteenDayForecastApi.retrofitService.getSixteenDayWeather(
            latitude,
            longitude,
            API_KEY_SIXTEEN_DAY_WEATHER
        )
    }

    override suspend fun getFullLocationInfo(location: String): Response<Location> {
        return GeocodingApi.retrofitService.getFullLocationInfo(
            location,
            "en",
            10,
            "city",
            "json",
            API_KEY_GEOCODING
        )
    }
}