package com.example.weatherforecastapp.ui.screens

import androidx.compose.foundation.gestures.scrollable
import com.example.weatherforecastapp.data.ForecastResponse
import com.example.weatherforecastapp.data.WeatherResponse
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.example.weatherforecastapp.data.ForecastItem
import com.example.weatherforecastapp.util.DateUtils
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WeatherScreen(viewModel: WeatherViewModel = viewModel()) {
    var cityName by remember { mutableStateOf("") }
    val uiState by viewModel.uiState.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
//            .statusBarsPadding()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        OutlinedTextField(
            value = cityName,
            onValueChange = { cityName = it },
            label = { Text("Enter City") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            trailingIcon = {
                IconButton(onClick = { viewModel.getWeatherForCity(cityName) }) {
                    Icon(
                        painter = painterResource(id = android.R.drawable.ic_menu_search),
                        contentDescription = "Search"
                    )
                }
            }
        )

        Spacer(modifier = Modifier.height(24.dp))

        when (val state = uiState) {
            is WeatherUiState.Initial -> {
                Text(
                    text = "Search for a city to get the weather forecast.",
                    modifier = Modifier.fillMaxWidth(),
                    textAlign = TextAlign.Center
                )
            }
            is WeatherUiState.Loading -> {
                Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
            }
            is WeatherUiState.Success -> {
                CurrentWeatherDisplay(weatherResponse = state.currentWeather)
                Spacer(modifier = Modifier.height(24.dp))
                ForecastDisplay(forecastResponse = state.forecast)
            }
            is WeatherUiState.Error -> {
                Text(
                    text = "Error: ${state.message}",
                    color = MaterialTheme.colorScheme.error,
                    modifier = Modifier.fillMaxWidth(),
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}

@Composable
fun CurrentWeatherDisplay(weatherResponse: WeatherResponse) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp), // Add some horizontal padding to the card itself
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "${weatherResponse.name}, ${weatherResponse.sys.country}",
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center, // Center align text
                modifier = Modifier.padding(bottom = 4.dp)
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "${weatherResponse.main.temp}°C",
                fontSize = 72.sp,
                fontWeight = FontWeight.Light,
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.padding(vertical = 4.dp)
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = weatherResponse.weather.firstOrNull()?.description?.replaceFirstChar { if (it.isLowerCase()) it.titlecase(Locale.getDefault()) else it.toString() } ?: "",
                fontSize = 24.sp
            )
            Spacer(modifier = Modifier.height(16.dp))
            AsyncImage(
                model = "https://openweathermap.org/img/wn/${weatherResponse.weather.firstOrNull()?.icon}@4x.png",
                contentDescription = "Weather Icon",
                modifier = Modifier.size(100.dp)
            )
            Spacer(modifier = Modifier.height(16.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceAround,
                verticalAlignment = Alignment.CenterVertically
            ) {
                WeatherDetailItem(label = "Wind", value = "${weatherResponse.wind.speed} km/h")
                WeatherDetailItem(label = "Humidity", value = "${weatherResponse.main.humidity}%")
                WeatherDetailItem(label = "Pressure", value = "${weatherResponse.main.pressure} hPa")
            }
        }
    }
}

@Composable
fun ForecastDisplay(forecastResponse: ForecastResponse) {

    // Process the forecast data to get one entry per day
    val dailyForecasts = forecastResponse.list
        .groupBy { it.dtTxt.substringBefore(" ") }
        .mapValues { (_, forecasts) -> forecasts.maxByOrNull { it.main.tempMax } }
        .values
        .filterNotNull()
        .sortedBy { it.dt }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp), // Consistent horizontal padding for the whole forecast section
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "5-Day Forecast",
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier
                .align(Alignment.Start)
                .padding(start = 8.dp, bottom = 8.dp) // Align and pad header
        )
        Card(
            modifier = Modifier.fillMaxWidth(),
            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
        ) {
            LazyColumn(modifier = Modifier.padding(16.dp)) {
                items(dailyForecasts) { forecastItem ->
                    ForecastItem(forecastItem = forecastItem)
                    // Add a small divider between items for better separation
                    if (forecastItem != dailyForecasts.last()) {
                        Divider(modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp))
                    }
                }
            }
        }
    }
}

@Composable
fun ForecastItem(forecastItem: ForecastItem) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp), // Adjust vertical padding for each item
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = DateUtils.convertTimestampToDay(forecastItem.dt),
            style = MaterialTheme.typography.bodyMedium,
            modifier = Modifier.weight(1f).align(Alignment.CenterVertically) // Ensure alignment
        )

        // Weather Icon
        AsyncImage(
            model = "https://openweathermap.org/img/wn/${forecastItem.weather.firstOrNull()?.icon}@2x.png",
            contentDescription = "Weather Icon",
            modifier = Modifier.size(50.dp)
        )

        // Temperatures
        Row(
            modifier = Modifier.weight(1f),
            horizontalArrangement = Arrangement.End,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "${forecastItem.main.tempMax.toInt()}°C",
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = "${forecastItem.main.tempMin.toInt()}°C",
                fontSize = 16.sp,
                color = Color.Gray
            )
        }
    }
}
@Composable
fun WeatherDetailItem(label: String, value: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(text = label, style = MaterialTheme.typography.labelMedium)
        Text(text = value, style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Bold)
    }
}
