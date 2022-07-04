package com.bekhruz.weatherforecast.presentation.viewmodels

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager
import android.location.Geocoder
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bekhruz.weatherforecast.data.remote.dto.currentweatherdto.CurrentForecast
import com.google.android.gms.location.FusedLocationProviderClient
import kotlinx.coroutines.launch
import android.util.Log
import androidx.core.app.ActivityCompat
import com.bekhruz.weatherforecast.data.remote.dto.geocodingdto.Location
import com.bekhruz.weatherforecast.data.remote.dto.sixteendayweatherdto.SixteenDayForecast
import com.bekhruz.weatherforecast.data.remote.repositories.WeatherRepository
import com.bekhruz.weatherforecast.domain.models.SearchedLocation
import com.bekhruz.weatherforecast.domain.models.SixteenDay
import com.bekhruz.weatherforecast.domain.models.Weather
import com.google.android.gms.location.LocationServices
import dagger.hilt.android.lifecycle.HiltViewModel
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject
@HiltViewModel
class WeatherViewModel @Inject constructor(
    private val weatherRepository: WeatherRepository
) : ViewModel() {
    private lateinit var fusedLocationProviderClient: FusedLocationProviderClient
    private val _currentWeatherData = MutableLiveData<Weather>()
    val currentWeatherData: LiveData<Weather> = _currentWeatherData
    private val _sixteenDayWeatherData = MutableLiveData<SixteenDay>()
    val sixteenDayWeatherData:LiveData<SixteenDay> = _sixteenDayWeatherData
    private val _searchedLocation = MutableLiveData<SearchedLocation>()
    val searchedLocation:LiveData<SearchedLocation> = _searchedLocation

    private fun getCurrentWeather(latLon: String) {
        viewModelScope.launch {
           val response =  weatherRepository.getCurrentWeather(latLon)
                _currentWeatherData.value = response
        }
    }
    private fun getSixteenDayWeather(latitude:String, longitude:String){
        viewModelScope.launch {
            val response = weatherRepository.getSixteenDayWeather(latitude, longitude)
                _sixteenDayWeatherData.value = response
        }
    }
    fun getSearchedLocationInfo(searchedLocation:String):LiveData<SearchedLocation>{
        viewModelScope.launch {
            val response = weatherRepository.getFullLocationInfo(searchedLocation)
                _searchedLocation.value = response
        }
        return _searchedLocation
    }


    fun getIconsOfSixteenDayData(iconId:String):String{
       return String.format("https://www.weatherbit.io/static/img/icons/$iconId.png")
    }
     fun getDeviceLocationData(context: Context,activity: Activity){
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(context)
        val task = fusedLocationProviderClient.lastLocation
        if (ActivityCompat.checkSelfPermission(
                context,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                context,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                activity,
                arrayOf(
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                ),
                2003)
            return
        }
        task.addOnSuccessListener{
            val geoCoder = Geocoder(context, Locale.getDefault())
            if (it != null){
                val fullAddress = geoCoder.getFromLocation(it.latitude,it.longitude,1)
                val currentCityName = fullAddress[0].getAddressLine(0)
                getCurrentWeather("${it.latitude},${it.longitude}")
                getSixteenDayWeather(it.latitude.toString(), it.longitude.toString())
                Log.d(TAG,"LOCATION IS $currentCityName, lat: ${it.latitude} and lon: ${it.longitude}")
            }
        }
    }

    fun getTime(epochSecond: Long, type:String):String{
        val time = Date(epochSecond * 1000)
        val timeFormat = when (type) {
            "date" -> SimpleDateFormat("EEEE | MMMM d", Locale.getDefault())
            "time" -> SimpleDateFormat("HH:mm", Locale.getDefault())
            else -> SimpleDateFormat("MMMM d", Locale.getDefault())
        }
        timeFormat.timeZone = TimeZone.getTimeZone("UTC")
        return timeFormat.format(time)
    }

    companion object {
        private const val TAG = "WEATHER VIEW MODEL"
    }
}