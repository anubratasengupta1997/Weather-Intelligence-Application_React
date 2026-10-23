package com.example.ui

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.ui.components.CityNotFoundCard
import com.example.ui.components.CurrentWeatherCard
import com.example.ui.components.DailyForecastList
import com.example.ui.components.DailyPlanningCard
import com.example.ui.components.HourlyWeatherChart
import com.example.ui.components.WeatherErrorCard
import com.example.ui.components.WeatherMetricsGrid
import com.example.ui.components.WeatherSearchBar

@Composable
fun WeatherScreen(
    viewModel: WeatherViewModel = viewModel(),
    modifier: Modifier = Modifier
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    val statusBarPadding = WindowInsets.statusBars.asPaddingValues().calculateTopPadding()
    val navBarPadding = WindowInsets.navigationBars.asPaddingValues().calculateBottomPadding()

    Scaffold(
        modifier = modifier.fillMaxSize(),
        containerColor = MaterialTheme.colorScheme.background,
        contentWindowInsets = WindowInsets(0, 0, 0, 0)
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            MaterialTheme.colorScheme.background,
                            MaterialTheme.colorScheme.surface
                        )
                    )
                ),
            contentAlignment = Alignment.TopCenter
        ) {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .widthIn(max = 680.dp)
                    .padding(horizontal = 16.dp)
                    .testTag("weather_main_scroll"),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Top Inset Spacer
                item {
                    Spacer(modifier = Modifier.height(statusBarPadding + 8.dp))
                }

                // App Header
                item {
                    AppHeader(
                        tempUnit = state.tempUnit,
                        onToggleUnit = viewModel::toggleTempUnit
                    )
                }

                // Search Bar with autocomplete and quick chips
                item {
                    WeatherSearchBar(
                        query = state.searchQuery,
                        onQueryChanged = viewModel::onSearchQueryChanged,
                        onSearch = viewModel::searchCity,
                        onClear = viewModel::clearSearchQuery,
                        suggestions = state.suggestions,
                        isSearchingSuggestions = state.isSearchingSuggestions,
                        onSelectSuggestion = viewModel::selectLocation,
                        popularCities = state.recentCities,
                        onSelectCityChip = viewModel::searchCity
                    )
                }

                // Dynamic UI State Content
                item {
                    AnimatedContent(
                        targetState = state.uiState,
                        transitionSpec = { fadeIn() togetherWith fadeOut() },
                        label = "weather_content_state"
                    ) { uiState ->
                        when (uiState) {
                            is WeatherUiState.Loading -> {
                                LoadingStateView()
                            }
                            is WeatherUiState.CityNotFound -> {
                                CityNotFoundCard(
                                    query = uiState.query,
                                    onSelectPopularCity = viewModel::searchCity,
                                    popularCities = state.recentCities
                                )
                            }
                            is WeatherUiState.Error -> {
                                WeatherErrorCard(
                                    message = uiState.message,
                                    onRetry = viewModel::refresh
                                )
                            }
                            is WeatherUiState.Success -> {
                                val weather = uiState.data
                                Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                                    // 1. Current Weather Hero Card
                                    CurrentWeatherCard(
                                        weather = weather,
                                        tempUnit = state.tempUnit,
                                        onToggleUnit = viewModel::toggleTempUnit,
                                        onRefresh = viewModel::refresh,
                                        isRefreshing = state.isRefreshing
                                    )

                                    // 2. Weather Metrics Grid (Wind, Humidity, UV, Pressure, Rain)
                                    WeatherMetricsGrid(weather = weather)

                                    // 3. 24-Hour Forecast Chart & Hourly List
                                    HourlyWeatherChart(
                                        hourlyItems = weather.hourlyForecast,
                                        tempUnit = state.tempUnit
                                    )

                                    // 4. Daily Intelligence Planning Recommendations
                                    DailyPlanningCard(
                                        recommendations = weather.recommendations
                                    )

                                    // 5. 7-Day Forecast Cards
                                    DailyForecastList(
                                        forecastItems = weather.dailyForecast,
                                        tempUnit = state.tempUnit
                                    )

                                    // Footer attribution
                                    WeatherFooter()
                                }
                            }
                            is WeatherUiState.Idle -> {
                                Spacer(modifier = Modifier.height(20.dp))
                            }
                        }
                    }
                }

                // Bottom Navigation Inset Spacer
                item {
                    Spacer(modifier = Modifier.height(navBarPadding + 24.dp))
                }
            }
        }
    }
}

@Composable
private fun AppHeader(
    tempUnit: TemperatureUnit,
    onToggleUnit: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.2f)),
                contentAlignment = Alignment.Center
            ) {
                Text(text = "🌤️", fontSize = 20.sp)
            }
            Spacer(modifier = Modifier.width(10.dp))
            Column {
                Text(
                    text = "Weather Intelligence",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = "Real-time forecast & smart planning",
                    fontSize = 11.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        Surface(
            onClick = onToggleUnit,
            shape = RoundedCornerShape(12.dp),
            color = MaterialTheme.colorScheme.surfaceVariant,
            tonalElevation = 2.dp,
            modifier = Modifier.testTag("header_unit_switch")
        ) {
            Row(
                modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = if (tempUnit == TemperatureUnit.CELSIUS) "°C" else "°F",
                    fontWeight = FontWeight.Bold,
                    fontSize = 13.sp,
                    color = MaterialTheme.colorScheme.primary
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = "Switch",
                    fontSize = 11.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

@Composable
private fun LoadingStateView() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(280.dp)
            .clip(RoundedCornerShape(24.dp))
            .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f)),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            CircularProgressIndicator(
                color = MaterialTheme.colorScheme.primary,
                strokeWidth = 3.dp,
                modifier = Modifier.size(44.dp)
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = "Fetching live weather intelligence...",
                fontSize = 14.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                fontWeight = FontWeight.Medium
            )
        }
    }
}

@Composable
private fun WeatherFooter() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 12.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Data provided by Open-Meteo Geocoding & Forecast APIs",
            fontSize = 11.sp,
            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
        )
        Spacer(modifier = Modifier.height(2.dp))
        Text(
            text = "High resolution meteorological modeling • Updated in real time",
            fontSize = 10.sp,
            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f)
        )
    }
}
