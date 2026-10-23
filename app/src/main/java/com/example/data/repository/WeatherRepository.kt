package com.example.data.repository

import com.example.data.api.ForecastApi
import com.example.data.api.GeocodingApi
import com.example.data.api.NetworkClient
import com.example.data.model.DailyForecastItem
import com.example.data.model.GeocodingResult
import com.example.data.model.HourlyForecastItem
import com.example.data.model.WeatherData
import com.example.data.model.WeatherUtils
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class CityNotFoundException(message: String) : Exception(message)

class WeatherRepository(
    private val geocodingApi: GeocodingApi = NetworkClient.geocodingApi,
    private val forecastApi: ForecastApi = NetworkClient.forecastApi
) {

    suspend fun searchCitySuggestions(query: String): List<GeocodingResult> = withContext(Dispatchers.IO) {
        if (query.trim().length < 2) return@withContext emptyList()
        try {
            val response = geocodingApi.searchCity(name = query.trim())
            response.results ?: emptyList()
        } catch (e: Exception) {
            emptyList()
        }
    }

    suspend fun getWeatherData(cityQuery: String): WeatherData = withContext(Dispatchers.IO) {
        val trimmed = cityQuery.trim()
        if (trimmed.isEmpty()) {
            throw CityNotFoundException("City not found. Please enter a valid city name.")
        }

        // 1. Convert city name to latitude and longitude coordinates
        val geocodingResponse = try {
            geocodingApi.searchCity(name = trimmed)
        } catch (e: Exception) {
            throw e
        }

        val topResult = geocodingResponse.results?.firstOrNull()
            ?: throw CityNotFoundException("City not found: '$trimmed'. Check the spelling or try another city.")

        getWeatherDataForLocation(topResult)
    }

    suspend fun getWeatherDataForLocation(location: GeocodingResult): WeatherData = withContext(Dispatchers.IO) {
        val forecastResponse = forecastApi.getForecast(
            latitude = location.latitude,
            longitude = location.longitude
        )

        val current = forecastResponse.current
            ?: throw IllegalStateException("Current weather details unavailable from forecast API.")

        val currentTemp = current.temperature2m ?: 0.0
        val apparentTemp = current.apparentTemperature ?: currentTemp
        val humidity = current.relativeHumidity2m ?: 0
        val windSpeed = current.windSpeed10m ?: 0.0
        val pressure = current.surfacePressure ?: 1013.2
        val uvIndex = current.uvIndex ?: 0.0
        val precipitation = current.precipitation ?: 0.0
        val weatherCode = current.weatherCode ?: 0
        val weatherDescription = WeatherUtils.getWeatherDescription(weatherCode)

        // Parse Daily 7-day forecast
        val daily = forecastResponse.daily
        val dailyList = mutableListOf<DailyForecastItem>()
        val maxDays = daily?.time?.size ?: 0
        val daysToTake = minOf(maxDays, 7)

        var todayMax = currentTemp
        var todayMin = currentTemp
        var todayPrecipProbMax = 0

        for (i in 0 until daysToTake) {
            val dateStr = daily?.time?.getOrNull(i) ?: ""
            val code = daily?.weatherCode?.getOrNull(i) ?: weatherCode
            val tMax = daily?.temperature2mMax?.getOrNull(i) ?: currentTemp
            val tMin = daily?.temperature2mMin?.getOrNull(i) ?: currentTemp
            val precipProb = daily?.precipitationProbabilityMax?.getOrNull(i) ?: 0
            val uv = daily?.uvIndexMax?.getOrNull(i) ?: uvIndex
            val wind = daily?.windSpeed10mMax?.getOrNull(i) ?: windSpeed

            if (i == 0) {
                todayMax = tMax
                todayMin = tMin
                todayPrecipProbMax = precipProb
            }

            dailyList.add(
                DailyForecastItem(
                    date = dateStr,
                    dayOfWeek = WeatherUtils.formatDayOfWeek(dateStr, i),
                    weatherCode = code,
                    description = WeatherUtils.getWeatherDescription(code),
                    tempMax = tMax,
                    tempMin = tMin,
                    precipitationProbability = precipProb,
                    uvIndex = uv,
                    windSpeed = wind
                )
            )
        }

        // Parse Hourly forecast for the next 24 hours
        val hourly = forecastResponse.hourly
        val hourlyList = mutableListOf<HourlyForecastItem>()
        if (hourly?.time != null) {
            // Find current hour index or start from recent
            val nowHourPrefix = SimpleDateFormat("yyyy-MM-dd'T'HH", Locale.getDefault()).format(Calendar.getInstance().time)
            var startIndex = 0
            for (idx in hourly.time.indices) {
                if (hourly.time[idx].startsWith(nowHourPrefix)) {
                    startIndex = idx
                    break
                }
            }
            if (startIndex < 0 || startIndex >= hourly.time.size) {
                startIndex = 0
            }

            val endIndex = minOf(startIndex + 24, hourly.time.size)
            for (idx in startIndex until endIndex) {
                val timeRaw = hourly.time[idx]
                val t = hourly.temperature2m?.getOrNull(idx) ?: currentTemp
                val pProb = hourly.precipitationProbability?.getOrNull(idx) ?: 0
                val wCode = hourly.weatherCode?.getOrNull(idx) ?: weatherCode

                hourlyList.add(
                    HourlyForecastItem(
                        timeLabel = if (idx == startIndex) "Now" else WeatherUtils.formatHour(timeRaw),
                        temperature = t,
                        precipitationProbability = pProb,
                        weatherCode = wCode,
                        isCurrentHour = (idx == startIndex)
                    )
                )
            }
        }

        // Generate Daily Planning Recommendations
        val recommendations = WeatherUtils.generateRecommendations(
            currentTemp = currentTemp,
            apparentTemp = apparentTemp,
            weatherCode = weatherCode,
            precipProbMax = todayPrecipProbMax,
            windSpeed = windSpeed,
            uvIndex = uvIndex
        )

        val regionParts = listOfNotNull(location.admin1, location.country).filter { it.isNotBlank() }
        val regionCountry = if (regionParts.isNotEmpty()) regionParts.joinToString(", ") else location.country ?: ""

        WeatherData(
            locationName = location.name,
            regionCountry = regionCountry,
            latitude = location.latitude,
            longitude = location.longitude,
            currentTemp = currentTemp,
            apparentTemp = apparentTemp,
            humidity = humidity,
            windSpeed = windSpeed,
            surfacePressure = pressure,
            uvIndex = uvIndex,
            precipitation = precipitation,
            weatherCode = weatherCode,
            weatherDescription = weatherDescription,
            highTemp = todayMax,
            lowTemp = todayMin,
            hourlyForecast = hourlyList,
            dailyForecast = dailyList,
            recommendations = recommendations
        )
    }
}
