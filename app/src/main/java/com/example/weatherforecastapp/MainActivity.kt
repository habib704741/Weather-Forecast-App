package com.example.weatherforecastapp

import android.os.Bundle
import android.view.WindowManager
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.weatherforecastapp.data.api.ApiClient
import com.example.weatherforecastapp.data.repository.WeatherRepository
import com.example.weatherforecastapp.ui.ViewModelFactory
import com.example.weatherforecastapp.ui.screens.WeatherScreen
import com.example.weatherforecastapp.ui.theme.WeatherForecastAppTheme

class MainActivity : ComponentActivity() {
    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        val repository = WeatherRepository(ApiClient.weatherService)
        val viewModelFactory = ViewModelFactory(repository)
        setContent {
            WeatherForecastAppTheme {
                Surface (
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ){
                    Column (modifier = Modifier.fillMaxSize()){
                        TopAppBar(
                            title = {Text(
                                text = "Weather Forecast App",
                                color = MaterialTheme.colorScheme.onPrimary,
                                fontWeight = FontWeight.Bold
                            )},
                            colors = TopAppBarDefaults.topAppBarColors(
                                containerColor = MaterialTheme.colorScheme.primary
                            )
                        )
                        WeatherScreen(viewModel = viewModel(factory = viewModelFactory))
                    }
                }
            }
        }
    }
}
