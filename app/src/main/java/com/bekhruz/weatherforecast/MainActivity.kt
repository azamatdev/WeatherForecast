package com.bekhruz.weatherforecast

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.net.ConnectivityManager
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.activity.viewModels
import androidx.annotation.RequiresApi
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import com.bekhruz.weatherforecast.databinding.ActivityMainBinding
import com.bekhruz.weatherforecast.presentation.viewmodels.WeatherViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var navController: NavController
    private lateinit var networkChangedReceiver: NetworkChangedReceiver
    private val viewModel: WeatherViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        val navHostFragment = supportFragmentManager
            .findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        navController = navHostFragment.navController
        networkChangedReceiver = NetworkChangedReceiver()
    }

    override fun onResume() {
        super.onResume()
        val filter = IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION)
        registerReceiver(networkChangedReceiver, filter)
    }

    override fun onPause() {
        super.onPause()
        unregisterReceiver(networkChangedReceiver)
    }

    inner class NetworkChangedReceiver : BroadcastReceiver() {
        @RequiresApi(Build.VERSION_CODES.M)
        override fun onReceive(context: Context?, intent: Intent?) {
            val connectivityManager =
                context?.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
            val activeNetwork = connectivityManager.activeNetwork
            if (activeNetwork != null) {
                viewModel.checkInternetConnection()
                Toast.makeText(context, "we have network connection", Toast.LENGTH_LONG).show()
            } else {
                viewModel.changeConnection(false)
                Toast.makeText(context, "we don't have network connection", Toast.LENGTH_LONG)
                    .show()
            }
        }
    }
}