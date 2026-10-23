package com.example.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.asAndroidPath
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.HourlyForecastItem
import com.example.data.model.WeatherUtils
import com.example.ui.TemperatureUnit

enum class ChartMetric {
    TEMPERATURE,
    PRECIPITATION
}

@Composable
fun HourlyWeatherChart(
    hourlyItems: List<HourlyForecastItem>,
    tempUnit: TemperatureUnit,
    modifier: Modifier = Modifier
) {
    if (hourlyItems.isEmpty()) return

    var selectedMetric by remember { mutableStateOf(ChartMetric.TEMPERATURE) }

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
            .testTag("hourly_weather_chart_card")
    ) {
        Column {
            // Header with Metric Selector Tabs
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "24-HOUR FORECAST CHART",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    letterSpacing = 0.8.sp
                )

                Row(
                    modifier = Modifier
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.surface)
                        .padding(3.dp),
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    ChartTogglePill(
                        title = "Temp",
                        selected = selectedMetric == ChartMetric.TEMPERATURE,
                        onClick = { selectedMetric = ChartMetric.TEMPERATURE }
                    )
                    ChartTogglePill(
                        title = "Rain %",
                        selected = selectedMetric == ChartMetric.PRECIPITATION,
                        onClick = { selectedMetric = ChartMetric.PRECIPITATION }
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Custom Canvas Chart for 24-hour Trend
            val primaryColor = MaterialTheme.colorScheme.primary
            val rainColor = Color(0xFF38BDF8)
            val textColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.75f)

            val displayItems = remember(hourlyItems, tempUnit) {
                hourlyItems.take(24)
            }

            HourlyCanvasChart(
                items = displayItems,
                metric = selectedMetric,
                tempUnit = tempUnit,
                chartColor = if (selectedMetric == ChartMetric.TEMPERATURE) primaryColor else rainColor,
                textColor = textColor,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(150.dp)
                    .testTag("hourly_canvas_chart")
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Horizontal Scrollable Cards with Emoji & Values
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                displayItems.forEach { item ->
                    val displayTemp = if (tempUnit == TemperatureUnit.CELSIUS) {
                        item.temperature
                    } else {
                        WeatherUtils.toFahrenheit(item.temperature)
                    }

                    Column(
                        modifier = Modifier
                            .clip(RoundedCornerShape(14.dp))
                            .background(
                                if (item.isCurrentHour)
                                    MaterialTheme.colorScheme.primary.copy(alpha = 0.15f)
                                else
                                    MaterialTheme.colorScheme.surface.copy(alpha = 0.6f)
                            )
                            .border(
                                width = 1.dp,
                                color = if (item.isCurrentHour)
                                    MaterialTheme.colorScheme.primary.copy(alpha = 0.6f)
                                else
                                    Color.Transparent,
                                shape = RoundedCornerShape(14.dp)
                            )
                            .padding(horizontal = 12.dp, vertical = 10.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = item.timeLabel,
                            fontSize = 11.sp,
                            fontWeight = if (item.isCurrentHour) FontWeight.Bold else FontWeight.Medium,
                            color = if (item.isCurrentHour) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant
                        )

                        Spacer(modifier = Modifier.height(6.dp))

                        Text(
                            text = WeatherUtils.getWeatherEmoji(item.weatherCode),
                            fontSize = 20.sp
                        )

                        Spacer(modifier = Modifier.height(6.dp))

                        Text(
                            text = "${displayTemp.toInt()}°",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )

                        if (item.precipitationProbability > 0) {
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "${item.precipitationProbability}%",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = Color(0xFF38BDF8)
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun ChartTogglePill(
    title: String,
    selected: Boolean,
    onClick: () -> Unit
) {
    val bgColor by animateColorAsState(
        if (selected) MaterialTheme.colorScheme.primary else Color.Transparent,
        label = "pill_bg"
    )
    val contentColor by animateColorAsState(
        if (selected) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurfaceVariant,
        label = "pill_content"
    )

    Box(
        modifier = Modifier
            .clip(CircleShape)
            .background(bgColor)
            .clickable(onClick = onClick)
            .padding(horizontal = 10.dp, vertical = 4.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = title,
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold,
            color = contentColor
        )
    }
}

@Composable
private fun HourlyCanvasChart(
    items: List<HourlyForecastItem>,
    metric: ChartMetric,
    tempUnit: TemperatureUnit,
    chartColor: Color,
    textColor: Color,
    modifier: Modifier = Modifier
) {
    Canvas(modifier = modifier) {
        if (items.size < 2) return@Canvas

        val values = items.map { item ->
            if (metric == ChartMetric.TEMPERATURE) {
                if (tempUnit == TemperatureUnit.CELSIUS) item.temperature else WeatherUtils.toFahrenheit(item.temperature)
            } else {
                item.precipitationProbability.toDouble()
            }
        }

        val minVal = (values.minOrNull() ?: 0.0).let { if (metric == ChartMetric.PRECIPITATION) 0.0 else it - 2.0 }
        val maxVal = (values.maxOrNull() ?: 100.0).let { if (metric == ChartMetric.PRECIPITATION) 100.0 else it + 2.0 }
        val range = (maxVal - minVal).coerceAtLeast(1.0)

        val paddingLeft = 16f
        val paddingRight = 16f
        val paddingTop = 28f
        val paddingBottom = 28f

        val chartWidth = size.width - paddingLeft - paddingRight
        val chartHeight = size.height - paddingTop - paddingBottom

        val stepX = chartWidth / (items.size - 1)

        val points = values.mapIndexed { index, v ->
            val x = paddingLeft + index * stepX
            val normalizedY = (v - minVal) / range
            val y = (paddingTop + chartHeight - (normalizedY * chartHeight)).toFloat()
            Offset(x, y)
        }

        // Draw Area Gradient Path
        val areaPath = Path().apply {
            moveTo(points.first().x, paddingTop + chartHeight)
            lineTo(points.first().x, points.first().y)
            for (i in 0 until points.size - 1) {
                val p0 = points[i]
                val p1 = points[i + 1]
                val controlX = (p0.x + p1.x) / 2f
                cubicTo(controlX, p0.y, controlX, p1.y, p1.x, p1.y)
            }
            lineTo(points.last().x, paddingTop + chartHeight)
            close()
        }

        drawPath(
            path = areaPath,
            brush = Brush.verticalGradient(
                colors = listOf(
                    chartColor.copy(alpha = 0.35f),
                    chartColor.copy(alpha = 0.0f)
                ),
                startY = paddingTop,
                endY = paddingTop + chartHeight
            )
        )

        // Draw Smooth Line
        val linePath = Path().apply {
            moveTo(points.first().x, points.first().y)
            for (i in 0 until points.size - 1) {
                val p0 = points[i]
                val p1 = points[i + 1]
                val controlX = (p0.x + p1.x) / 2f
                cubicTo(controlX, p0.y, controlX, p1.y, p1.x, p1.y)
            }
        }

        drawPath(
            path = linePath,
            color = chartColor,
            style = Stroke(width = 3.dp.toPx(), cap = StrokeCap.Round)
        )

        // Draw points & labels every 3 or 4 items to keep chart clean and scannable
        val labelStep = if (items.size > 16) 4 else 3
        val paint = android.graphics.Paint().apply {
            color = android.graphics.Color.WHITE
            textSize = 28f
            textAlign = android.graphics.Paint.Align.CENTER
            isAntiAlias = true
        }

        val axisPaint = android.graphics.Paint().apply {
            color = android.graphics.Color.argb(160, 180, 200, 230)
            textSize = 24f
            textAlign = android.graphics.Paint.Align.CENTER
            isAntiAlias = true
        }

        for (i in points.indices) {
            val point = points[i]
            val isCurrent = items[i].isCurrentHour

            if (isCurrent || i % labelStep == 0 || i == points.lastIndex) {
                // Circle node
                drawCircle(
                    color = if (isCurrent) Color.White else chartColor,
                    radius = if (isCurrent) 5.dp.toPx() else 3.5.dp.toPx(),
                    center = point
                )
                if (isCurrent) {
                    drawCircle(
                        color = chartColor,
                        radius = 8.dp.toPx(),
                        center = point,
                        style = Stroke(width = 2.dp.toPx())
                    )
                }

                // Value Label above point
                val valueText = if (metric == ChartMetric.TEMPERATURE) {
                    "${values[i].toInt()}°"
                } else {
                    "${values[i].toInt()}%"
                }
                drawContext.canvas.nativeCanvas.drawText(
                    valueText,
                    point.x,
                    point.y - 12f,
                    paint
                )

                // Time axis label below chart
                val timeText = items[i].timeLabel
                drawContext.canvas.nativeCanvas.drawText(
                    timeText,
                    point.x,
                    size.height - 4f,
                    axisPaint
                )
            }
        }
    }
}
