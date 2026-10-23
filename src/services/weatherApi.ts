import {
  GeocodingResponse,
  GeocodingResult,
  ForecastResponse,
  WeatherData,
  HourlyItem,
  DailyItem,
  DailyRecommendation
} from '../types/weather';

export function getWeatherDescription(code: number): string {
  switch (code) {
    case 0: return 'Clear Sky';
    case 1: return 'Mainly Clear';
    case 2: return 'Partly Cloudy';
    case 3: return 'Overcast';
    case 45: return 'Foggy';
    case 48: return 'Depositing Rime Fog';
    case 51: return 'Light Drizzle';
    case 53: return 'Moderate Drizzle';
    case 55: return 'Dense Drizzle';
    case 56:
    case 57: return 'Freezing Drizzle';
    case 61: return 'Slight Rain';
    case 63: return 'Moderate Rain';
    case 65: return 'Heavy Rain';
    case 66:
    case 67: return 'Freezing Rain';
    case 71: return 'Slight Snow Fall';
    case 73: return 'Moderate Snow Fall';
    case 75: return 'Heavy Snow Fall';
    case 77: return 'Snow Grains';
    case 80: return 'Slight Rain Showers';
    case 81: return 'Moderate Rain Showers';
    case 82: return 'Violent Rain Showers';
    case 85: return 'Slight Snow Showers';
    case 86: return 'Heavy Snow Showers';
    case 95: return 'Thunderstorm';
    case 96:
    case 99: return 'Thunderstorm with Hail';
    default: return 'Partly Cloudy';
  }
}

export function getWeatherEmoji(code: number): string {
  switch (code) {
    case 0: return '☀️';
    case 1:
    case 2: return '🌤️';
    case 3: return '☁️';
    case 45:
    case 48: return '🌫️';
    case 51:
    case 53:
    case 55: return '🌦️';
    case 56:
    case 57: return '🌨️';
    case 61:
    case 63: return '🌧️';
    case 65: return '⛈️';
    case 66:
    case 67: return '🌨️';
    case 71:
    case 73:
    case 75:
    case 77: return '❄️';
    case 80:
    case 81:
    case 82: return '🌧️';
    case 85:
    case 86: return '🌨️';
    case 95:
    case 96:
    case 99: return '⛈️';
    default: return '🌤️';
  }
}

function formatHour(isoTime: string): string {
  try {
    const parts = isoTime.split('T');
    if (parts.length > 1) {
      const hour = parseInt(parts[1].split(':')[0], 10);
      if (hour === 0) return '12 AM';
      if (hour < 12) return `${hour} AM`;
      if (hour === 12) return '12 PM';
      return `${hour - 12} PM`;
    }
    return isoTime;
  } catch {
    return isoTime;
  }
}

function formatDayName(isoDate: string, index: number): string {
  if (index === 0) return 'Today';
  try {
    const date = new Date(isoDate + 'T00:00:00');
    return date.toLocaleDateString('en-US', { weekday: 'short' });
  } catch {
    return `Day ${index + 1}`;
  }
}

function generateRecommendations(
  currentTemp: number,
  apparentTemp: number,
  weatherCode: number,
  precipProbMax: number,
  windSpeed: number,
  uvIndex: number
): DailyRecommendation[] {
  const recommendations: DailyRecommendation[] = [];

  // 1. Clothing & Layers
  if (currentTemp < 5) {
    recommendations.push({
      category: 'Clothing & Layers',
      headline: 'Heavy Winter Coat & Layers',
      advice: `Cold weather (${Math.round(currentTemp)}°C). Wear a thermal base, warm coat, gloves, and a beanie.`,
      icon: '🧥',
      status: 'warning'
    });
  } else if (currentTemp < 15) {
    recommendations.push({
      category: 'Clothing & Layers',
      headline: 'Medium Jacket or Sweater',
      advice: `Crisp air feels like ${Math.round(apparentTemp)}°C. A sweater, light trench, or zip jacket is recommended.`,
      icon: '🧥',
      status: 'info'
    });
  } else if (currentTemp < 23) {
    recommendations.push({
      category: 'Clothing & Layers',
      headline: 'Comfortable Light Layers',
      advice: `Pleasant mild conditions (${Math.round(currentTemp)}°C). T-shirt with a light outer layer for the evening.`,
      icon: '👕',
      status: 'optimal'
    });
  } else {
    recommendations.push({
      category: 'Clothing & Layers',
      headline: 'Light Breathable Clothing',
      advice: `Warm weather (${Math.round(currentTemp)}°C). Wear breathable cotton or linen fabrics and keep water handy.`,
      icon: '🩳',
      status: 'optimal'
    });
  }

  // 2. Umbrella & Rain
  const isRainy = [51, 53, 55, 56, 57, 61, 63, 65, 66, 67, 80, 81, 82, 95, 96, 99].includes(weatherCode);
  if (isRainy || precipProbMax >= 60) {
    recommendations.push({
      category: 'Precipitation Alert',
      headline: 'Pack an Umbrella Today',
      advice: `High precipitation probability (${precipProbMax}%). Keep an umbrella in hand and wear waterproof footwear.`,
      icon: '☂️',
      status: 'warning'
    });
  } else if (precipProbMax >= 25) {
    recommendations.push({
      category: 'Precipitation Alert',
      headline: 'Spotty Showers Possible',
      advice: `Moderate rain probability (${precipProbMax}%). Carrying a compact umbrella is a good precaution.`,
      icon: '🌦️',
      status: 'caution'
    });
  } else {
    recommendations.push({
      category: 'Precipitation Alert',
      headline: 'Dry Conditions Expected',
      advice: `Low precipitation chance (${precipProbMax}%). You can safely leave the umbrella at home today.`,
      icon: '☀️',
      status: 'optimal'
    });
  }

  // 3. Outdoor Fitness & Activities
  if ([95, 96, 99].includes(weatherCode)) {
    recommendations.push({
      category: 'Outdoor Activities',
      headline: 'Move Workouts Indoors',
      advice: 'Thunderstorm activity detected. Avoid open fields and outdoor training for lightning safety.',
      icon: '⚡',
      status: 'warning'
    });
  } else if (windSpeed > 35) {
    recommendations.push({
      category: 'Outdoor Activities',
      headline: 'Windy Conditions',
      advice: `Gusts reaching ${Math.round(windSpeed)} km/h. Outdoor cycling and tennis may be challenging.`,
      icon: '💨',
      status: 'caution'
    });
  } else if (currentTemp >= 12 && currentTemp <= 26 && precipProbMax < 30) {
    recommendations.push({
      category: 'Outdoor Activities',
      headline: 'Great for Running & Sports',
      advice: 'Crisp, comfortable air with low rain risk. Optimal window for jogging, cycling, or walking.',
      icon: '🏃',
      status: 'optimal'
    });
  } else {
    recommendations.push({
      category: 'Outdoor Activities',
      headline: 'Moderate Outdoor Conditions',
      advice: 'Standard outdoor activities are fine. Stay hydrated and monitor changing afternoon skies.',
      icon: '🚴',
      status: 'info'
    });
  }

  // 4. Sun & UV Protection
  if (uvIndex >= 8) {
    recommendations.push({
      category: 'Sun & UV Protection',
      headline: `Very High UV Index (${uvIndex.toFixed(1)})`,
      advice: 'Intense ultraviolet radiation. Apply SPF 50+ sunscreen, wear UV sunglasses, and avoid midday direct sun.',
      icon: '🧴',
      status: 'warning'
    });
  } else if (uvIndex >= 5) {
    recommendations.push({
      category: 'Sun & UV Protection',
      headline: `Moderate UV Index (${uvIndex.toFixed(1)})`,
      advice: 'Sun protection recommended if spending more than 30 minutes outside between 11 AM and 4 PM.',
      icon: '🕶️',
      status: 'caution'
    });
  } else {
    recommendations.push({
      category: 'Sun & UV Protection',
      headline: `Low UV Index (${uvIndex.toFixed(1)})`,
      advice: 'Minimal sun hazard today. Enjoy the daylight safely without special high-factor protection.',
      icon: '😎',
      status: 'optimal'
    });
  }

  return recommendations;
}

export async function searchCities(query: string): Promise<GeocodingResult[]> {
  const trimmed = query.trim();
  if (trimmed.length < 2) return [];

  const url = `https://geocoding-api.open-meteo.com/v1/search?name=${encodeURIComponent(trimmed)}&count=10&language=en&format=json`;
  const res = await fetch(url);
  if (!res.ok) {
    throw new Error(`Geocoding API responded with status ${res.status}`);
  }
  const data: GeocodingResponse = await res.json();
  return data.results || [];
}

export async function fetchWeatherForCity(cityName: string): Promise<WeatherData> {
  const trimmed = cityName.trim();
  if (!trimmed) {
    throw new Error('City not found. Please enter a city name.');
  }

  // Step 1: Geocode city name to lat & lon
  const cities = await searchCities(trimmed);
  if (!cities || cities.length === 0) {
    throw new Error(`City not found: "${trimmed}". Please check the spelling.`);
  }

  const topCity = cities[0];
  return fetchWeatherForLocation(topCity);
}

export async function fetchWeatherForLocation(location: GeocodingResult): Promise<WeatherData> {
  // Step 2: Fetch forecast using lat and lon
  const forecastUrl = `https://api.open-meteo.com/v1/forecast?latitude=${location.latitude}&longitude=${location.longitude}&current=temperature_2m,relative_humidity_2m,apparent_temperature,precipitation,weather_code,wind_speed_10m,surface_pressure,uv_index&hourly=temperature_2m,precipitation_probability,weather_code&daily=weather_code,temperature_2m_max,temperature_2m_min,precipitation_sum,precipitation_probability_max,wind_speed_10m_max,uv_index_max&timezone=auto`;

  const res = await fetch(forecastUrl);
  if (!res.ok) {
    throw new Error(`Forecast API responded with status ${res.status}`);
  }

  const data: ForecastResponse = await res.json();
  const current = data.current;
  if (!current) {
    throw new Error('Current weather conditions could not be retrieved.');
  }

  const currentTemp = current.temperature_2m ?? 0;
  const apparentTemp = current.apparent_temperature ?? currentTemp;
  const humidity = current.relative_humidity_2m ?? 0;
  const windSpeed = current.wind_speed_10m ?? 0;
  const surfacePressure = current.surface_pressure ?? 1013;
  const uvIndex = current.uv_index ?? 0;
  const precipitation = current.precipitation ?? 0;
  const weatherCode = current.weather_code ?? 0;
  const weatherDescription = getWeatherDescription(weatherCode);

  // Daily 7-day forecast
  const daily: DailyItem[] = [];
  const daysCount = Math.min(data.daily?.time?.length ?? 0, 7);
  let todayMax = currentTemp;
  let todayMin = currentTemp;
  let todayPrecipProbMax = 0;

  for (let i = 0; i < daysCount; i++) {
    const dateStr = data.daily.time[i];
    const code = data.daily.weather_code[i] ?? weatherCode;
    const tMax = data.daily.temperature_2m_max[i] ?? currentTemp;
    const tMin = data.daily.temperature_2m_min[i] ?? currentTemp;
    const pProb = data.daily.precipitation_probability_max[i] ?? 0;
    const uv = data.daily.uv_index_max[i] ?? uvIndex;
    const wind = data.daily.wind_speed_10m_max[i] ?? windSpeed;

    if (i === 0) {
      todayMax = tMax;
      todayMin = tMin;
      todayPrecipProbMax = pProb;
    }

    daily.push({
      date: dateStr,
      dayName: formatDayName(dateStr, i),
      weatherCode: code,
      description: getWeatherDescription(code),
      tempMax: tMax,
      tempMin: tMin,
      precipProb: pProb,
      uvIndex: uv,
      windSpeed: wind
    });
  }

  // Hourly 24 hours
  const hourly: HourlyItem[] = [];
  if (data.hourly?.time) {
    const nowIsoPrefix = new Date().toISOString().substring(0, 13);
    let startIdx = data.hourly.time.findIndex(t => t.startsWith(nowIsoPrefix));
    if (startIdx < 0) startIdx = 0;

    const endIdx = Math.min(startIdx + 24, data.hourly.time.length);
    for (let idx = startIdx; idx < endIdx; idx++) {
      const isNow = idx === startIdx;
      hourly.push({
        timeLabel: isNow ? 'Now' : formatHour(data.hourly.time[idx]),
        temp: data.hourly.temperature_2m[idx] ?? currentTemp,
        precipProb: data.hourly.precipitation_probability[idx] ?? 0,
        weatherCode: data.hourly.weather_code[idx] ?? weatherCode,
        isNow
      });
    }
  }

  const recommendations = generateRecommendations(
    currentTemp,
    apparentTemp,
    weatherCode,
    todayPrecipProbMax,
    windSpeed,
    uvIndex
  );

  const regionParts = [location.admin1, location.country].filter(Boolean);
  const regionCountry = regionParts.length > 0 ? regionParts.join(', ') : location.country || '';

  return {
    locationName: location.name,
    regionCountry,
    latitude: location.latitude,
    longitude: location.longitude,
    currentTemp,
    apparentTemp,
    humidity,
    windSpeed,
    surfacePressure,
    uvIndex,
    precipitation,
    weatherCode,
    weatherDescription,
    highTemp: todayMax,
    lowTemp: todayMin,
    hourly,
    daily,
    recommendations
  };
}
