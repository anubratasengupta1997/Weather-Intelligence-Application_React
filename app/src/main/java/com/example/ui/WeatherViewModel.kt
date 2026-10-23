package com.example.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.model.GeocodingResult
import com.example.data.model.WeatherData
import com.example.data.repository.CityNotFoundException
import com.example.data.repository.WeatherRepository
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

enum class TemperatureUnit {
    CELSIUS,
    FAHRENHEIT
}

sealed interface WeatherUiState {
    object Idle : WeatherUiState
    object Loading : WeatherUiState
    data class Success(val data: WeatherData) : WeatherUiState
    data class CityNotFound(val query: String, val message: String) : WeatherUiState
    data class Error(val message: String) : WeatherUiState
}

data class WeatherScreenState(
    val uiState: WeatherUiState = WeatherUiState.Loading,
    val searchQuery: String = "",
    val suggestions: List<GeocodingResult> = emptyList(),
    val isSearchingSuggestions: Boolean = false,
    val tempUnit: TemperatureUnit = TemperatureUnit.CELSIUS,
    val isRefreshing: Boolean = false,
    val recentCities: List<String> = listOf("Tokyo", "New York", "London", "Paris", "Sydney", "San Francisco")
)

class WeatherViewModel(
    private val repository: WeatherRepository = WeatherRepository()
) : ViewModel() {

    private val _state = MutableStateFlow(WeatherScreenState())
    val state: StateFlow<WeatherScreenState> = _state.asStateFlow()

    private var suggestionJob: Job? = null
    private var currentCityQuery: String = "London"

    init {
        // Load default city on launch
        fetchWeather("London")
    }

    fun onSearchQueryChanged(newQuery: String) {
        _state.update { it.copy(searchQuery = newQuery) }
        suggestionJob?.cancel()
        if (newQuery.trim().length >= 2) {
            suggestionJob = viewModelScope.launch {
                delay(300) // Debounce typing
                _state.update { it.copy(isSearchingSuggestions = true) }
                val results = repository.searchCitySuggestions(newQuery)
                _state.update { it.copy(suggestions = results, isSearchingSuggestions = false) }
            }
        } else {
            _state.update { it.copy(suggestions = emptyList(), isSearchingSuggestions = false) }
        }
    }

    fun clearSearchQuery() {
        suggestionJob?.cancel()
        _state.update { it.copy(searchQuery = "", suggestions = emptyList(), isSearchingSuggestions = false) }
    }

    fun searchCity(cityName: String) {
        val trimmed = cityName.trim()
        if (trimmed.isEmpty()) return
        suggestionJob?.cancel()
        _state.update { it.copy(searchQuery = trimmed, suggestions = emptyList(), isSearchingSuggestions = false) }
        fetchWeather(trimmed)
    }

    fun selectLocation(location: GeocodingResult) {
        suggestionJob?.cancel()
        _state.update { it.copy(searchQuery = location.name, suggestions = emptyList(), isSearchingSuggestions = false) }
        currentCityQuery = location.name
        viewModelScope.launch {
            _state.update { it.copy(uiState = WeatherUiState.Loading) }
            try {
                val data = repository.getWeatherDataForLocation(location)
                _state.update { it.copy(uiState = WeatherUiState.Success(data)) }
            } catch (e: Exception) {
                _state.update {
                    it.copy(
                        uiState = WeatherUiState.Error(
                            e.localizedMessage ?: "Failed to retrieve forecast for ${location.name}."
                        )
                    )
                }
            }
        }
    }

    fun refresh() {
        fetchWeather(currentCityQuery, isRefresh = true)
    }

    fun toggleTempUnit() {
        _state.update {
            it.copy(
                tempUnit = if (it.tempUnit == TemperatureUnit.CELSIUS) TemperatureUnit.FAHRENHEIT else TemperatureUnit.CELSIUS
            )
        }
    }

    private fun fetchWeather(city: String, isRefresh: Boolean = false) {
        currentCityQuery = city
        viewModelScope.launch {
            if (isRefresh) {
                _state.update { it.copy(isRefreshing = true) }
            } else {
                _state.update { it.copy(uiState = WeatherUiState.Loading) }
            }

            try {
                val weatherData = repository.getWeatherData(city)
                _state.update {
                    it.copy(
                        uiState = WeatherUiState.Success(weatherData),
                        isRefreshing = false
                    )
                }
            } catch (e: CityNotFoundException) {
                _state.update {
                    it.copy(
                        uiState = WeatherUiState.CityNotFound(
                            query = city,
                            message = "City not found: \"$city\". Please check the spelling or try another location."
                        ),
                        isRefreshing = false
                    )
                }
            } catch (e: Exception) {
                _state.update {
                    it.copy(
                        uiState = WeatherUiState.Error(
                            message = "Unable to connect to Open-Meteo services. Please check your internet connection."
                        ),
                        isRefreshing = false
                    )
                }
            }
        }
    }
}
