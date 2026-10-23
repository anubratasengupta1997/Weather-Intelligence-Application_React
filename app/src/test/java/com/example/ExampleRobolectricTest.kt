package com.example

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.example.data.model.RecommendationStatus
import com.example.data.model.WeatherUtils
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class ExampleRobolectricTest {

    @Test
    fun `read string from context`() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val appName = context.getString(R.string.app_name)
        assertEquals("Weather Intelligence", appName)
    }

    @Test
    fun `weather utils resolves descriptions and planning recommendations`() {
        val descClear = WeatherUtils.getWeatherDescription(0)
        assertEquals("Clear Sky", descClear)

        val descRain = WeatherUtils.getWeatherDescription(63)
        assertEquals("Moderate Rain", descRain)

        val recommendations = WeatherUtils.generateRecommendations(
            currentTemp = 8.0,
            apparentTemp = 6.0,
            weatherCode = 61,
            precipProbMax = 75,
            windSpeed = 12.0,
            uvIndex = 2.0
        )
        assertTrue(recommendations.isNotEmpty())
        assertTrue(recommendations.any { it.category == "Precipitation Alert" && it.statusLevel == RecommendationStatus.WARNING })
    }
}
