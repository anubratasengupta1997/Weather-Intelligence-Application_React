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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.WeatherData
import java.util.Locale

@Composable
fun WeatherMetricsGrid(
    weather: WeatherData,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        // Row 1: Humidity & Wind
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            MetricCard(
                title = "HUMIDITY",
                value = "${weather.humidity}%",
                subtitle = when {
                    weather.humidity > 70 -> "High humidity"
                    weather.humidity < 30 -> "Dry air"
                    else -> "Comfortable"
                },
                iconEmoji = "💧",
                modifier = Modifier
                    .weight(1f)
                    .testTag("metric_humidity")
            )

            MetricCard(
                title = "WIND SPEED",
                value = "${weather.windSpeed.toInt()} km/h",
                subtitle = when {
                    weather.windSpeed > 30 -> "Brisk wind"
                    weather.windSpeed > 15 -> "Moderate breeze"
                    else -> "Gentle breeze"
                },
                iconEmoji = "💨",
                modifier = Modifier
                    .weight(1f)
                    .testTag("metric_wind")
            )
        }

        // Row 2: UV Index & Pressure
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            val uvDesc = when {
                weather.uvIndex >= 8 -> "Very High"
                weather.uvIndex >= 6 -> "High"
                weather.uvIndex >= 3 -> "Moderate"
                else -> "Low"
            }
            MetricCard(
                title = "UV INDEX",
                value = String.format(Locale.US, "%.1f", weather.uvIndex),
                subtitle = "$uvDesc protection",
                iconEmoji = "☀️",
                modifier = Modifier
                    .weight(1f)
                    .testTag("metric_uv")
            )

            MetricCard(
                title = "AIR PRESSURE",
                value = "${weather.surfacePressure.toInt()} hPa",
                subtitle = when {
                    weather.surfacePressure > 1020 -> "High pressure"
                    weather.surfacePressure < 1005 -> "Low pressure"
                    else -> "Normal pressure"
                },
                iconEmoji = "🧭",
                modifier = Modifier
                    .weight(1f)
                    .testTag("metric_pressure")
            )
        }

        // Row 3: Precipitation & Today's Rain Sum
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            MetricCard(
                title = "PRECIPITATION",
                value = "${String.format(Locale.US, "%.1f", weather.precipitation)} mm",
                subtitle = if (weather.precipitation > 0) "Rain currently active" else "No rainfall detected",
                iconEmoji = "🌧️",
                modifier = Modifier
                    .weight(1f)
                    .testTag("metric_precipitation")
            )

            val dayPrecipProb = weather.dailyForecast.firstOrNull()?.precipitationProbability ?: 0
            MetricCard(
                title = "RAIN PROBABILITY",
                value = "$dayPrecipProb%",
                subtitle = if (dayPrecipProb > 50) "High chance today" else "Low chance today",
                iconEmoji = "☂️",
                modifier = Modifier
                    .weight(1f)
                    .testTag("metric_rain_chance")
            )
        }
    }
}

@Composable
private fun MetricCard(
    title: String,
    value: String,
    subtitle: String,
    iconEmoji: String,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(18.dp))
            .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
            .border(
                1.dp,
                MaterialTheme.colorScheme.outline.copy(alpha = 0.35f),
                RoundedCornerShape(18.dp)
            )
            .padding(14.dp)
    ) {
        Column {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = title,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    letterSpacing = 0.8.sp
                )
                Text(
                    text = iconEmoji,
                    fontSize = 16.sp
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = value,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )

            Spacer(modifier = Modifier.height(2.dp))

            Text(
                text = subtitle,
                fontSize = 12.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}
