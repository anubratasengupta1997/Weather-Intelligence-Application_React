export interface GeocodingResult {
  id: number;
  name: string;
  latitude: number;
  longitude: number;
  elevation?: number;
  country?: string;
  country_code?: string;
  admin1?: string;
  timezone?: string;
}

export interface GeocodingResponse {
  results?: GeocodingResult[];
}

export interface CurrentWeatherUnits {
  time: string;
  temperature_2m: number;
  relative_humidity_2m: number;
  apparent_temperature: number;
  precipitation: number;
  weather_code: number;
  wind_speed_10m: number;
  surface_pressure: number;
  uv_index: number;
}

export interface HourlyWeatherUnits {
  time: string[];
  temperature_2m: number[];
  precipitation_probability: number[];
  weather_code: number[];
}

export interface DailyWeatherUnits {
  time: string[];
  weather_code: number[];
  temperature_2m_max: number[];
  temperature_2m_min: number[];
  precipitation_sum: number[];
  precipitation_probability_max: number[];
  wind_speed_10m_max: number[];
  uv_index_max: number[];
}

export interface ForecastResponse {
  latitude: number;
  longitude: number;
  timezone: string;
  current: CurrentWeatherUnits;
  hourly: HourlyWeatherUnits;
  daily: DailyWeatherUnits;
}

export interface HourlyItem {
  timeLabel: string;
  temp: number;
  precipProb: number;
  weatherCode: number;
  isNow: boolean;
}

export interface DailyItem {
  date: string;
  dayName: string;
  weatherCode: number;
  description: string;
  tempMax: number;
  tempMin: number;
  precipProb: number;
  uvIndex: number;
  windSpeed: number;
}

export type RecommendationLevel = 'optimal' | 'caution' | 'warning' | 'info';

export interface DailyRecommendation {
  category: string;
  headline: string;
  advice: string;
  icon: string;
  status: RecommendationLevel;
}

export interface WeatherData {
  locationName: string;
  regionCountry: string;
  latitude: number;
  longitude: number;
  currentTemp: number;
  apparentTemp: number;
  humidity: number;
  windSpeed: number;
  surfacePressure: number;
  uvIndex: number;
  precipitation: number;
  weatherCode: number;
  weatherDescription: string;
  highTemp: number;
  lowTemp: number;
  hourly: HourlyItem[];
  daily: DailyItem[];
  recommendations: DailyRecommendation[];
}
