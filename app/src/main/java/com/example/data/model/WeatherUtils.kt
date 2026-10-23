package com.example.data.model

import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

object WeatherUtils {

    fun getWeatherDescription(code: Int): String {
        return when (code) {
            0 -> "Clear Sky"
            1 -> "Mainly Clear"
            2 -> "Partly Cloudy"
            3 -> "Overcast"
            45 -> "Foggy"
            48 -> "Depositing Rime Fog"
            51 -> "Light Drizzle"
            53 -> "Moderate Drizzle"
            55 -> "Dense Drizzle"
            56, 57 -> "Freezing Drizzle"
            61 -> "Slight Rain"
            63 -> "Moderate Rain"
            65 -> "Heavy Rain"
            66, 67 -> "Freezing Rain"
            71 -> "Slight Snow Fall"
            73 -> "Moderate Snow Fall"
            75 -> "Heavy Snow Fall"
            77 -> "Snow Grains"
            80 -> "Slight Rain Showers"
            81 -> "Moderate Rain Showers"
            82 -> "Violent Rain Showers"
            85 -> "Slight Snow Showers"
            86 -> "Heavy Snow Showers"
            95 -> "Thunderstorm"
            96, 99 -> "Thunderstorm with Hail"
            else -> "Partly Cloudy"
        }
    }

    fun getWeatherEmoji(code: Int): String {
        return when (code) {
            0 -> "☀️"
            1, 2 -> "🌤️"
            3 -> "☁️"
            45, 48 -> "🌫️"
            51, 53, 55 -> "🌦️"
            56, 57 -> "🌨️"
            61, 63 -> "🌧️"
            65 -> "⛈️"
            66, 67 -> "🌨️"
            71, 73, 75, 77 -> "❄️"
            80, 81, 82 -> "🌧️"
            85, 86 -> "🌨️"
            95, 96, 99 -> "⛈️"
            else -> "🌤️"
        }
    }

    fun formatHour(isoTime: String): String {
        return try {
            // isoTime like "2026-10-23T14:00"
            val parts = isoTime.split("T")
            if (parts.size > 1) {
                val timeParts = parts[1].split(":")
                val hour = timeParts[0].toInt()
                when {
                    hour == 0 -> "12 AM"
                    hour < 12 -> "$hour AM"
                    hour == 12 -> "12 PM"
                    else -> "${hour - 12} PM"
                }
            } else {
                isoTime
            }
        } catch (e: Exception) {
            isoTime
        }
    }

    fun formatDayOfWeek(isoDate: String, index: Int): String {
        if (index == 0) return "Today"
        return try {
            val format = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
            val date = format.parse(isoDate)
            if (date != null) {
                val dayFormat = SimpleDateFormat("EEE", Locale.getDefault())
                dayFormat.format(date)
            } else {
                "Day $index"
            }
        } catch (e: Exception) {
            "Day $index"
        }
    }

    fun toFahrenheit(celsius: Double): Double {
        return (celsius * 9.0 / 5.0) + 32.0
    }

    fun generateRecommendations(
        currentTemp: Double,
        apparentTemp: Double,
        weatherCode: Int,
        precipProbMax: Int,
        windSpeed: Double,
        uvIndex: Double
    ): List<DailyPlanningRecommendation> {
        val list = mutableListOf<DailyPlanningRecommendation>()

        // 1. Clothing & Layering
        when {
            currentTemp < 5 -> {
                list.add(
                    DailyPlanningRecommendation(
                        category = "Clothing & Layers",
                        headline = "Heavy Winter Coat",
                        advice = "Chilly conditions (${currentTemp.toInt()}°C). Wear thermal base layers, warm coat, gloves, and a beanie.",
                        iconEmoji = "🧥",
                        statusLevel = RecommendationStatus.WARNING
                    )
                )
            }
            currentTemp in 5.0..14.9 -> {
                list.add(
                    DailyPlanningRecommendation(
                        category = "Clothing & Layers",
                        headline = "Jacket or Sweater Needed",
                        advice = "Crisp air with feels like ${apparentTemp.toInt()}°C. A medium jacket, trench, or knit sweater is ideal.",
                        iconEmoji = "🧥",
                        statusLevel = RecommendationStatus.INFO
                    )
                )
            }
            currentTemp in 15.0..22.9 -> {
                list.add(
                    DailyPlanningRecommendation(
                        category = "Clothing & Layers",
                        headline = "Comfortable Light Layers",
                        advice = "Pleasant mild weather (${currentTemp.toInt()}°C). T-shirt with a light cardigan or fleece layer for evening.",
                        iconEmoji = "👕",
                        statusLevel = RecommendationStatus.OPTIMAL
                    )
                )
            }
            else -> {
                list.add(
                    DailyPlanningRecommendation(
                        category = "Clothing & Layers",
                        headline = "Light Breathable Fabrics",
                        advice = "Warm temperatures (${currentTemp.toInt()}°C). Wear loose cotton, linen, and stay hydrated throughout the day.",
                        iconEmoji = "🩳",
                        statusLevel = RecommendationStatus.OPTIMAL
                    )
                )
            }
        }

        // 2. Umbrella & Rain Alert
        val isRainyCode = weatherCode in listOf(51, 53, 55, 56, 57, 61, 63, 65, 66, 67, 80, 81, 82, 95, 96, 99)
        when {
            isRainyCode || precipProbMax >= 60 -> {
                list.add(
                    DailyPlanningRecommendation(
                        category = "Precipitation Alert",
                        headline = "Pack an Umbrella Today",
                        advice = "High chance of rain (${precipProbMax}%). Keep an umbrella handy and opt for water-resistant shoes.",
                        iconEmoji = "☂️",
                        statusLevel = RecommendationStatus.WARNING
                    )
                )
            }
            precipProbMax in 25..59 -> {
                list.add(
                    DailyPlanningRecommendation(
                        category = "Precipitation Alert",
                        headline = "Spotty Showers Possible",
                        advice = "Moderate rain probability (${precipProbMax}%). Stash a compact umbrella in your bag just in case.",
                        iconEmoji = "🌦️",
                        statusLevel = RecommendationStatus.CAUTION
                    )
                )
            }
            else -> {
                list.add(
                    DailyPlanningRecommendation(
                        category = "Precipitation Alert",
                        headline = "Dry Conditions Expected",
                        advice = "Low precipitation probability (${precipProbMax}%). No umbrella needed for today's outings.",
                        iconEmoji = "☀️",
                        statusLevel = RecommendationStatus.OPTIMAL
                    )
                )
            }
        }

        // 3. Outdoor Fitness & Activities
        when {
            weatherCode in listOf(95, 96, 99) -> {
                list.add(
                    DailyPlanningRecommendation(
                        category = "Outdoor Activities",
                        headline = "Avoid Outdoor Workouts",
                        advice = "Thunderstorms detected in the area. Shift runs and workouts indoors for lightning safety.",
                        iconEmoji = "⚡",
                        statusLevel = RecommendationStatus.WARNING
                    )
                )
            }
            windSpeed > 35 -> {
                list.add(
                    DailyPlanningRecommendation(
                        category = "Outdoor Activities",
                        headline = "High Wind Advisory",
                        advice = "Brisk winds at ${windSpeed.toInt()} km/h. Cycling and tennis might be challenging today.",
                        iconEmoji = "💨",
                        statusLevel = RecommendationStatus.CAUTION
                    )
                )
            }
            currentTemp in 12.0..26.0 && precipProbMax < 30 -> {
                list.add(
                    DailyPlanningRecommendation(
                        category = "Outdoor Activities",
                        headline = "Perfect For Running & Walks",
                        advice = "Prime weather for jogging, park walks, or cycling with calm breeze and pleasant temps.",
                        iconEmoji = "🏃",
                        statusLevel = RecommendationStatus.OPTIMAL
                    )
                )
            }
            else -> {
                list.add(
                    DailyPlanningRecommendation(
                        category = "Outdoor Activities",
                        headline = "Moderate Conditions",
                        advice = "Outdoor activities are fine with proper pacing and checking radar updates before heading out.",
                        iconEmoji = "🚴",
                        statusLevel = RecommendationStatus.INFO
                    )
                )
            }
        }

        // 4. Sun & UV Protection
        when {
            uvIndex >= 8 -> {
                list.add(
                    DailyPlanningRecommendation(
                        category = "Sun & UV Index",
                        headline = "Very High UV Alert (Index ${String.format(Locale.US, "%.1f", uvIndex)})",
                        advice = "Harmful UV rays. Apply SPF 50+ sunscreen, wear polarized sunglasses, and seek shade midday.",
                        iconEmoji = "🧴",
                        statusLevel = RecommendationStatus.WARNING
                    )
                )
            }
            uvIndex in 5.0..7.9 -> {
                list.add(
                    DailyPlanningRecommendation(
                        category = "Sun & UV Index",
                        headline = "Moderate UV Exposure (${String.format(Locale.US, "%.1f", uvIndex)})",
                        advice = "Sun protection recommended. Put on sunscreen and a hat if outside between 11 AM and 4 PM.",
                        iconEmoji = "🕶️",
                        statusLevel = RecommendationStatus.CAUTION
                    )
                )
            }
            else -> {
                list.add(
                    DailyPlanningRecommendation(
                        category = "Sun & UV Index",
                        headline = "Low UV Index (${String.format(Locale.US, "%.1f", uvIndex)})",
                        advice = "Minimal sun hazard today. Enjoy the daylight safely without special high-factor protection.",
                        iconEmoji = "😎",
                        statusLevel = RecommendationStatus.OPTIMAL
                    )
                )
            }
        }

        return list
    }
}
