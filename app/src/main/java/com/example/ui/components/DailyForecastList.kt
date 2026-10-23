package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.DailyForecastItem
import com.example.data.model.WeatherUtils
import com.example.ui.TemperatureUnit

@Composable
fun DailyForecastList(
    forecastItems: List<DailyForecastItem>,
    tempUnit: TemperatureUnit,
    modifier: Modifier = Modifier
) {
    if (forecastItems.isEmpty()) return

    // Calculate global min and max for consistent range bars
    val allMin = forecastItems.minOfOrNull { it.tempMin } ?: 0.0
    val allMax = forecastItems.maxOfOrNull { it.tempMax } ?: 30.0
    val totalRange = (allMax - allMin).coerceAtLeast(1.0)

    Box(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(24.dp))
            .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
            .border(
                1.dp,
                MaterialTheme.colorScheme.outline.copy(alpha = 0.35f),
                RoundedCornerShape(24.dp)
            )
            .padding(18.dp)
            .testTag("seven_day_forecast_section")
    ) {
        Column {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "7-DAY WEATHER FORECAST",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    letterSpacing = 0.8.sp
                )
                Text(
                    text = "High / Low",
                    fontSize = 11.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Spacer(modifier = Modifier.height(14.dp))

            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                forecastItems.forEachIndexed { index, item ->
                    val minDisplay = if (tempUnit == TemperatureUnit.CELSIUS) {
                        item.tempMin
                    } else {
                        WeatherUtils.toFahrenheit(item.tempMin)
                    }

                    val maxDisplay = if (tempUnit == TemperatureUnit.CELSIUS) {
                        item.tempMax
                    } else {
                        WeatherUtils.toFahrenheit(item.tempMax)
                    }

                    DailyForecastCard(
                        dayOfWeek = item.dayOfWeek,
                        date = item.date,
                        weatherCode = item.weatherCode,
                        description = item.description,
                        minTemp = minDisplay,
                        maxTemp = maxDisplay,
                        precipProb = item.precipitationProbability,
                        normalizedMin = ((item.tempMin - allMin) / totalRange).toFloat(),
                        normalizedMax = ((item.tempMax - allMin) / totalRange).toFloat(),
                        isToday = (index == 0),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("forecast_day_$index")
                    )
                }
            }
        }
    }
}

@Composable
private fun DailyForecastCard(
    dayOfWeek: String,
    date: String,
    weatherCode: Int,
    description: String,
    minTemp: Double,
    maxTemp: Double,
    precipProb: Int,
    normalizedMin: Float,
    normalizedMax: Float,
    isToday: Boolean,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .clip(RoundedCornerShape(16.dp))
            .background(
                if (isToday)
                    MaterialTheme.colorScheme.primary.copy(alpha = 0.12f)
                else
                    MaterialTheme.colorScheme.surface.copy(alpha = 0.6f)
            )
            .border(
                1.dp,
                if (isToday)
                    MaterialTheme.colorScheme.primary.copy(alpha = 0.45f)
                else
                    MaterialTheme.colorScheme.outline.copy(alpha = 0.15f),
                RoundedCornerShape(16.dp)
            )
            .padding(horizontal = 14.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Day name & date
        Column(modifier = Modifier.width(72.dp)) {
            Text(
                text = dayOfWeek,
                fontWeight = if (isToday) FontWeight.Bold else FontWeight.SemiBold,
                color = if (isToday) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface,
                fontSize = 14.sp
            )
            Text(
                text = date.substringAfter("-"),
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                fontSize = 11.sp
            )
        }

        // Icon + Rain chance
        Row(
            modifier = Modifier.width(76.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = WeatherUtils.getWeatherEmoji(weatherCode),
                fontSize = 22.sp
            )
            if (precipProb > 15) {
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = "$precipProb%",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = Color(0xFF38BDF8)
                )
            }
        }

        // Condition summary text
        Text(
            text = description,
            fontSize = 12.sp,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.weight(1f),
            maxLines = 1
        )

        // Min temp
        Text(
            text = "${minTemp.toInt()}°",
            fontSize = 13.sp,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            fontWeight = FontWeight.Medium,
            modifier = Modifier.width(32.dp)
        )

        // Visual Range Bar
        Box(
            modifier = Modifier
                .width(64.dp)
                .height(6.dp)
                .clip(CircleShape)
                .background(MaterialTheme.colorScheme.outline.copy(alpha = 0.25f))
        ) {
            val startRatio = normalizedMin.coerceIn(0f, 1f)
            val endRatio = normalizedMax.coerceIn(0f, 1f)
            val barWidthRatio = (endRatio - startRatio).coerceAtLeast(0.15f)

            Box(
                modifier = Modifier
                    .fillMaxWidth(fraction = (startRatio + barWidthRatio).coerceAtMost(1f))
                    .height(6.dp)
                    .clip(CircleShape)
                    .background(
                        Brush.horizontalGradient(
                            listOf(
                                Color(0xFF38BDF8),
                                Color(0xFFFFB300)
                            )
                        )
                    )
            )
        }

        Spacer(modifier = Modifier.width(8.dp))

        // Max temp
        Text(
            text = "${maxTemp.toInt()}°",
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurface,
            modifier = Modifier.width(32.dp)
        )
    }
}
