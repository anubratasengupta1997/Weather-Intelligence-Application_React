import React from 'react';
import { Droplets, Wind, Sun, Compass, CloudRain, ShieldCheck } from 'lucide-react';
import { WeatherData } from '../types/weather';

interface WeatherMetricsProps {
  weather: WeatherData;
}

export const WeatherMetrics: React.FC<WeatherMetricsProps> = ({ weather }) => {
  const uvLevel = () => {
    if (weather.uvIndex >= 8) return { label: 'Very High', color: 'text-rose-400' };
    if (weather.uvIndex >= 6) return { label: 'High', color: 'text-amber-400' };
    if (weather.uvIndex >= 3) return { label: 'Moderate', color: 'text-sky-400' };
    return { label: 'Low', color: 'text-emerald-400' };
  };

  const todayPrecipProb = weather.daily[0]?.precipProb ?? 0;
  const uv = uvLevel();

  const metrics = [
    {
      title: 'HUMIDITY',
      value: `${weather.humidity}%`,
      subtitle: weather.humidity > 70 ? 'High humidity' : weather.humidity < 30 ? 'Dry air' : 'Comfortable',
      icon: <Droplets className="w-5 h-5 text-sky-400" />
    },
    {
      title: 'WIND SPEED',
      value: `${Math.round(weather.windSpeed)} km/h`,
      subtitle: weather.windSpeed > 30 ? 'Strong gusts' : 'Gentle breeze',
      icon: <Wind className="w-5 h-5 text-sky-400" />
    },
    {
      title: 'UV INDEX',
      value: weather.uvIndex.toFixed(1),
      subtitle: uv.label,
      subtitleColor: uv.color,
      icon: <Sun className="w-5 h-5 text-amber-400" />
    },
    {
      title: 'AIR PRESSURE',
      value: `${Math.round(weather.surfacePressure)} hPa`,
      subtitle: weather.surfacePressure > 1018 ? 'High pressure' : 'Normal pressure',
      icon: <Compass className="w-5 h-5 text-sky-400" />
    },
    {
      title: 'PRECIPITATION',
      value: `${weather.precipitation.toFixed(1)} mm`,
      subtitle: weather.precipitation > 0 ? 'Rainfall detected' : 'No rain now',
      icon: <CloudRain className="w-5 h-5 text-sky-400" />
    },
    {
      title: 'RAIN PROBABILITY',
      value: `${todayPrecipProb}%`,
      subtitle: todayPrecipProb > 50 ? 'High probability today' : 'Low chance today',
      icon: <ShieldCheck className="w-5 h-5 text-sky-400" />
    }
  ];

  return (
    <div className="grid grid-cols-2 sm:grid-cols-3 gap-3.5">
      {metrics.map((m, idx) => (
        <div
          key={idx}
          className="rounded-2xl bg-slate-900/70 border border-slate-800/80 p-4 shadow-lg hover:border-slate-700/80 transition-all backdrop-blur-md"
        >
          <div className="flex items-center justify-between">
            <span className="text-[11px] font-bold text-slate-400 uppercase tracking-wider">
              {m.title}
            </span>
            {m.icon}
          </div>
          <div className="mt-2 text-xl font-bold text-slate-100">
            {m.value}
          </div>
          <div className={`mt-0.5 text-xs font-medium ${m.subtitleColor || 'text-slate-400'}`}>
            {m.subtitle}
          </div>
        </div>
      ))}
    </div>
  );
};
