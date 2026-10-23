import React from 'react';
import { MapPin, RotateCw } from 'lucide-react';
import { WeatherData } from '../types/weather';
import { getWeatherEmoji } from '../services/weatherApi';

interface CurrentWeatherProps {
  weather: WeatherData;
  tempUnit: 'C' | 'F';
  onToggleUnit: () => void;
  onRefresh: () => void;
  isRefreshing: boolean;
}

export const CurrentWeather: React.FC<CurrentWeatherProps> = ({
  weather,
  tempUnit,
  onToggleUnit,
  onRefresh,
  isRefreshing
}) => {
  const toDisplayTemp = (celsius: number) => {
    return tempUnit === 'C' ? Math.round(celsius) : Math.round((celsius * 9) / 5 + 32);
  };

  const currentTemp = toDisplayTemp(weather.currentTemp);
  const feelsLike = toDisplayTemp(weather.apparentTemp);
  const highTemp = toDisplayTemp(weather.highTemp);
  const lowTemp = toDisplayTemp(weather.lowTemp);
  const symbol = `°${tempUnit}`;

  return (
    <div className="relative overflow-hidden rounded-3xl bg-gradient-to-br from-slate-900/90 via-slate-900/70 to-slate-800/80 border border-slate-700/60 p-6 sm:p-8 shadow-2xl backdrop-blur-xl">
      {/* Background glow circle */}
      <div className="absolute -top-16 -right-16 w-48 h-48 bg-sky-500/10 rounded-full blur-3xl pointer-events-none" />

      {/* Header Row */}
      <div className="flex items-start justify-between gap-4">
        <div>
          <div className="flex items-center gap-2">
            <MapPin className="w-5 h-5 text-sky-400" />
            <h2 className="text-2xl sm:text-3xl font-extrabold text-slate-100">
              {weather.locationName}
            </h2>
          </div>
          {weather.regionCountry && (
            <p className="text-xs sm:text-sm text-slate-400 mt-1 pl-7">
              {weather.regionCountry}
            </p>
          )}
        </div>

        <div className="flex items-center gap-2">
          {/* Unit Toggle */}
          <button
            type="button"
            onClick={onToggleUnit}
            className="px-3 py-1.5 bg-sky-500/10 hover:bg-sky-500/20 border border-sky-400/30 rounded-xl text-sky-400 font-bold text-xs transition-colors"
          >
            {tempUnit === 'C' ? '°C | °F' : '°F | °C'}
          </button>

          {/* Refresh button */}
          <button
            type="button"
            onClick={onRefresh}
            className="p-2 hover:bg-slate-800/80 border border-slate-700/50 rounded-xl text-slate-300 hover:text-white transition-colors"
            title="Refresh weather data"
          >
            <RotateCw className={`w-4 h-4 ${isRefreshing ? 'animate-spin text-sky-400' : ''}`} />
          </button>
        </div>
      </div>

      {/* Main Temperature and Icon Display */}
      <div className="mt-6 flex flex-col sm:flex-row items-start sm:items-center justify-between gap-6">
        <div>
          <div className="flex items-baseline">
            <span className="text-6xl sm:text-7xl font-extrabold text-white tracking-tight">
              {currentTemp}
            </span>
            <span className="text-3xl font-semibold text-sky-400 ml-1">
              {symbol}
            </span>
          </div>
          <div className="text-lg font-semibold text-slate-200 mt-1">
            {weather.weatherDescription}
          </div>
          <div className="text-sm text-slate-400 mt-0.5">
            Feels like {feelsLike}{symbol}
          </div>
        </div>

        <div className="w-24 h-24 sm:w-28 sm:h-28 rounded-3xl bg-slate-800/60 border border-slate-700/50 flex items-center justify-center text-5xl sm:text-6xl shadow-inner">
          {getWeatherEmoji(weather.weatherCode)}
        </div>
      </div>

      {/* Coordinates and High / Low pill */}
      <div className="mt-6 pt-4 border-t border-slate-800/60 flex flex-wrap items-center justify-between gap-3 text-xs sm:text-sm text-slate-400">
        <div className="flex items-center gap-3">
          <span className="font-semibold text-slate-200">
            H: {highTemp}{symbol}
          </span>
          <span className="font-semibold text-slate-400">
            L: {lowTemp}{symbol}
          </span>
        </div>
        <div className="text-slate-500 font-mono text-xs">
          Coordinates: {weather.latitude.toFixed(2)}°, {weather.longitude.toFixed(2)}°
        </div>
      </div>
    </div>
  );
};
