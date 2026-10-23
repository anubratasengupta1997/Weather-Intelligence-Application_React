package com.example.data.model

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class GeocodingResponse(
    @Json(name = "results") val results: List<GeocodingResult>? = null
)

@JsonClass(generateAdapter = true)
data class GeocodingResult(
    @Json(name = "id") val id: Long? = null,
    @Json(name = "name") val name: String,
    @Json(name = "latitude") val latitude: Double,
    @Json(name = "longitude") val longitude: Double,
    @Json(name = "country") val country: String? = null,
    @Json(name = "admin1") val admin1: String? = null,
    @Json(name = "country_code") val countryCode: String? = null,
    @Json(name = "timezone") val timezone: String? = null
)

@JsonClass(generateAdapter = true)
data class ForecastResponse(
    @Json(name = "latitude") val latitude: Double,
    @Json(name = "longitude") val longitude: Double,
    @Json(name = "timezone") val timezone: String? = null,
    @Json(name = "current") val current: CurrentWeatherUnits? = null,
    @Json(name = "hourly") val hourly: HourlyWeatherUnits? = null,
    @Json(name = "daily") val daily: DailyWeatherUnits? = null
)

@JsonClass(generateAdapter = true)
data class CurrentWeatherUnits(
    @Json(name = "time") val time: String? = null,
    @Json(name = "temperature_2m") val temperature2m: Double? = null,
    @Json(name = "relative_humidity_2m") val relativeHumidity2m: Int? = null,
    @Json(name = "apparent_temperature") val apparentTemperature: Double? = null,
    @Json(name = "precipitation") val precipitation: Double? = null,
    @Json(name = "weather_code") val weatherCode: Int? = null,
    @Json(name = "wind_speed_10m") val windSpeed10m: Double? = null,
    @Json(name = "surface_pressure") val surfacePressure: Double? = null,
    @Json(name = "uv_index") val uvIndex: Double? = null
)

@JsonClass(generateAdapter = true)
data class HourlyWeatherUnits(
    @Json(name = "time") val time: List<String>? = null,
    @Json(name = "temperature_2m") val temperature2m: List<Double>? = null,
    @Json(name = "precipitation_probability") val precipitationProbability: List<Int>? = null,
    @Json(name = "weather_code") val weatherCode: List<Int>? = null
)

@JsonClass(generateAdapter = true)
data class DailyWeatherUnits(
    @Json(name = "time") val time: List<String>? = null,
    @Json(name = "weather_code") val weatherCode: List<Int>? = null,
    @Json(name = "temperature_2m_max") val temperature2mMax: List<Double>? = null,
    @Json(name = "temperature_2m_min") val temperature2mMin: List<Double>? = null,
    @Json(name = "precipitation_sum") val precipitationSum: List<Double>? = null,
    @Json(name = "precipitation_probability_max") val precipitationProbabilityMax: List<Int>? = null,
    @Json(name = "wind_speed_10m_max") val windSpeed10mMax: DoubleListWrapper? = null,
    @Json(name = "uv_index_max") val uvIndexMax: List<Double>? = null
)

// In some Open-Meteo responses wind_speed_10m_max is list of double
typealias DoubleListWrapper = List<Double>

data class HourlyForecastItem(
    val timeLabel: String,
    val temperature: Double,
    val precipitationProbability: Int,
    val weatherCode: Int,
    val isCurrentHour: Boolean = false
)

data class DailyForecastItem(
    val date: String,
    val dayOfWeek: String,
    val weatherCode: Int,
    val description: String,
    val tempMax: Double,
    val tempMin: Double,
    val precipitationProbability: Int,
    val uvIndex: Double,
    val windSpeed: Double
)

data class DailyPlanningRecommendation(
    val category: String,
    val headline: String,
    val advice: String,
    val iconEmoji: String,
    val statusLevel: RecommendationStatus
)

enum class RecommendationStatus {
    OPTIMAL,
    CAUTION,
    WARNING,
    INFO
}

data class WeatherData(
    val locationName: String,
    val regionCountry: String,
    val latitude: Double,
    val longitude: Double,
    val currentTemp: Double,
    val apparentTemp: Double,
    val humidity: Int,
    val windSpeed: Double,
    val surfacePressure: Double,
    val uvIndex: Double,
    val precipitation: Double,
    val weatherCode: Int,
    val weatherDescription: String,
    val highTemp: Double,
    val lowTemp: Double,
    val hourlyForecast: List<HourlyForecastItem>,
    val dailyForecast: List<DailyForecastItem>,
    val recommendations: List<DailyPlanningRecommendation>
)
