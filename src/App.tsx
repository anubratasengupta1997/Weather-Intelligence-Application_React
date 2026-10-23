import React, { useState, useEffect, useCallback } from 'react';
import { CloudSun, Loader2 } from 'lucide-react';
import { WeatherData, GeocodingResult } from './types/weather';
import { fetchWeatherForCity, fetchWeatherForLocation } from './services/weatherApi';
import { SearchBar } from './components/SearchBar';
import { CurrentWeather } from './components/CurrentWeather';
import { WeatherMetrics } from './components/WeatherMetrics';
import { WeatherChart } from './components/WeatherChart';
import { DailyPlanning } from './components/DailyPlanning';
import { DailyForecast } from './components/DailyForecast';
import { ErrorMessage } from './components/ErrorMessage';

const POPULAR_CITIES = [
  'Tokyo',
  'New York',
  'London',
  'Paris',
  'Sydney',
  'San Francisco',
  'Dubai',
  'Singapore'
];

export const App: React.FC = () => {
  const [weather, setWeather] = useState<WeatherData | null>(null);
  const [loading, setLoading] = useState<boolean>(true);
  const [isRefreshing, setIsRefreshing] = useState<boolean>(false);
  const [errorType, setErrorType] = useState<'not_found' | 'network_error' | null>(null);
  const [failedQuery, setFailedQuery] = useState<string>('');
  const [errorMessage, setErrorMessage] = useState<string>('');
  const [tempUnit, setTempUnit] = useState<'C' | 'F'>('C');
  const [currentCityName, setCurrentCityName] = useState<string>('London');

  const loadCityWeather = useCallback(async (city: string, isRefresh: boolean = false) => {
    setCurrentCityName(city);
    if (isRefresh) {
      setIsRefreshing(true);
    } else {
      setLoading(true);
    }
    setErrorType(null);

    try {
      const data = await fetchWeatherForCity(city);
      setWeather(data);
    } catch (err: unknown) {
      const msg = err instanceof Error ? err.message : 'Unknown error';
      if (msg.toLowerCase().includes('city not found')) {
        setErrorType('not_found');
        setFailedQuery(city);
      } else {
        setErrorType('network_error');
        setErrorMessage(msg);
      }
    } finally {
      setLoading(false);
      setIsRefreshing(false);
    }
  }, []);

  const handleSelectLocation = async (location: GeocodingResult) => {
    setCurrentCityName(location.name);
    setLoading(true);
    setErrorType(null);

    try {
      const data = await fetchWeatherForLocation(location);
      setWeather(data);
    } catch (err: unknown) {
      const msg = err instanceof Error ? err.message : 'Failed to retrieve forecast';
      setErrorType('network_error');
      setErrorMessage(msg);
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    loadCityWeather('London');
  }, [loadCityWeather]);

  const toggleTempUnit = () => {
    setTempUnit((prev) => (prev === 'C' ? 'F' : 'C'));
  };

  const handleRefresh = () => {
    if (currentCityName) {
      loadCityWeather(currentCityName, true);
    }
  };

  return (
    <div className="min-h-screen bg-slate-950 text-slate-100 flex flex-col items-center">
      {/* Background ambient lighting */}
      <div className="fixed inset-0 overflow-hidden pointer-events-none">
        <div className="absolute top-0 left-1/2 -translate-x-1/2 w-[600px] h-[350px] bg-sky-600/10 rounded-full blur-[120px]" />
        <div className="absolute top-[400px] right-10 w-[400px] h-[400px] bg-blue-600/10 rounded-full blur-[140px]" />
      </div>

      <div className="w-full max-w-4xl px-4 py-8 sm:px-6 sm:py-10 flex flex-col gap-6 relative z-10">
        {/* App Header */}
        <header className="flex items-center justify-between">
          <div className="flex items-center gap-3">
            <div className="w-10 h-10 rounded-2xl bg-sky-500/15 border border-sky-400/30 flex items-center justify-center text-sky-400 shadow-md">
              <CloudSun className="w-6 h-6" />
            </div>
            <div>
              <h1 className="text-xl sm:text-2xl font-black text-white tracking-tight">
                Weather Intelligence
              </h1>
              <p className="text-xs text-slate-400 font-medium">
                Open-Meteo Powered Forecast & Smart Daily Planning
              </p>
            </div>
          </div>

          <button
            type="button"
            onClick={toggleTempUnit}
            className="flex items-center gap-1.5 px-3 py-1.5 rounded-xl bg-slate-900 border border-slate-700/80 hover:border-sky-400/50 text-xs font-semibold text-slate-200 transition-all shadow"
          >
            <span className="text-sky-400 font-bold">°{tempUnit}</span>
            <span className="text-slate-400">Switch</span>
          </button>
        </header>

        {/* City Search Bar */}
        <SearchBar
          onSearch={(city) => loadCityWeather(city)}
          onSelectLocation={handleSelectLocation}
          popularCities={POPULAR_CITIES}
        />

        {/* Content Area */}
        <main className="flex flex-col gap-6">
          {loading ? (
            <div className="h-72 rounded-3xl bg-slate-900/60 border border-slate-800 flex flex-col items-center justify-center gap-3 shadow-xl">
              <Loader2 className="w-8 h-8 text-sky-400 animate-spin" />
              <p className="text-sm font-medium text-slate-300">
                Fetching weather intelligence from Open-Meteo...
              </p>
            </div>
          ) : errorType ? (
            <ErrorMessage
              type={errorType}
              query={failedQuery}
              message={errorMessage}
              onSelectPopularCity={(city) => loadCityWeather(city)}
              popularCities={POPULAR_CITIES}
              onRetry={handleRefresh}
            />
          ) : weather ? (
            <>
              {/* Current Weather Card */}
              <CurrentWeather
                weather={weather}
                tempUnit={tempUnit}
                onToggleUnit={toggleTempUnit}
                onRefresh={handleRefresh}
                isRefreshing={isRefreshing}
              />

              {/* Weather Metrics Grid */}
              <WeatherMetrics weather={weather} />

              {/* 24-Hour Forecast Chart */}
              <WeatherChart hourly={weather.hourly} tempUnit={tempUnit} />

              {/* Daily Intelligence Planning Briefing */}
              <DailyPlanning recommendations={weather.recommendations} />

              {/* 7-Day Forecast Cards */}
              <DailyForecast daily={weather.daily} tempUnit={tempUnit} />
            </>
          ) : null}
        </main>

        {/* Footer */}
        <footer className="mt-8 pt-6 border-t border-slate-800/80 text-center text-xs text-slate-500">
          <p>
            Weather data sourced live from{' '}
            <a
              href="https://open-meteo.com/"
              target="_blank"
              rel="noopener noreferrer"
              className="text-sky-400 hover:underline"
            >
              Open-Meteo Geocoding & Forecast APIs
            </a>
            .
          </p>
          <p className="mt-1">
            Free high-resolution meteorological models • Built with React & Jetpack Compose
          </p>
        </footer>
      </div>
    </div>
  );
};

export default App;
