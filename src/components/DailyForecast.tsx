import React from 'react';
import { DailyItem } from '../types/weather';
import { getWeatherEmoji } from '../services/weatherApi';

interface DailyForecastProps {
  daily: DailyItem[];
  tempUnit: 'C' | 'F';
}

export const DailyForecast: React.FC<DailyForecastProps> = ({ daily, tempUnit }) => {
  if (!daily || daily.length === 0) return null;

  const toDisplayTemp = (celsius: number) => {
    return tempUnit === 'C' ? Math.round(celsius) : Math.round((celsius * 9) / 5 + 32);
  };

  const allMin = Math.min(...daily.map(d => d.tempMin));
  const allMax = Math.max(...daily.map(d => d.tempMax));
  const totalRange = Math.max(1, allMax - allMin);

  return (
    <div className="rounded-3xl bg-slate-900/80 border border-slate-800/80 p-5 sm:p-6 shadow-xl backdrop-blur-xl">
      <div className="flex items-center justify-between mb-4">
        <h3 className="text-xs font-bold uppercase tracking-wider text-slate-400">
          7-Day Weather Forecast
        </h3>
        <span className="text-xs font-medium text-slate-400">
          Min / Max
        </span>
      </div>

      <div className="flex flex-col gap-2.5">
        {daily.map((item, idx) => {
          const isToday = idx === 0;
          const minTemp = toDisplayTemp(item.tempMin);
          const maxTemp = toDisplayTemp(item.tempMax);

          const startPct = Math.max(0, Math.min(100, ((item.tempMin - allMin) / totalRange) * 100));
          const endPct = Math.max(0, Math.min(100, ((item.tempMax - allMin) / totalRange) * 100));
          const barWidth = Math.max(15, endPct - startPct);

          return (
            <div
              key={item.date}
              className={`flex items-center justify-between px-4 py-3 rounded-2xl border transition-all ${
                isToday
                  ? 'bg-sky-500/10 border-sky-400/40'
                  : 'bg-slate-800/40 border-slate-700/40 hover:bg-slate-800/60'
              }`}
            >
              {/* Day & Date */}
              <div className="w-20">
                <div className={`text-sm font-bold ${isToday ? 'text-sky-300' : 'text-slate-100'}`}>
                  {item.dayName}
                </div>
                <div className="text-[11px] text-slate-400 font-medium">
                  {item.date.split('-').slice(1).join('/')}
                </div>
              </div>

              {/* Emoji & Rain Chance */}
              <div className="w-20 flex items-center gap-1.5">
                <span className="text-2xl">{getWeatherEmoji(item.weatherCode)}</span>
                {item.precipProb > 15 && (
                  <span className="text-xs font-semibold text-sky-400">
                    {item.precipProb}%
                  </span>
                )}
              </div>

              {/* Condition text */}
              <div className="hidden sm:block flex-1 text-xs text-slate-300 font-medium truncate px-2">
                {item.description}
              </div>

              {/* Min Temp */}
              <div className="w-10 text-right text-xs font-medium text-slate-400">
                {minTemp}°
              </div>

              {/* Visual Range Bar */}
              <div className="w-20 sm:w-28 mx-3 h-2 bg-slate-800 rounded-full relative overflow-hidden">
                <div
                  className="absolute h-full rounded-full bg-gradient-to-r from-sky-400 to-amber-400"
                  style={{
                    left: `${startPct}%`,
                    width: `${barWidth}%`
                  }}
                />
              </div>

              {/* Max Temp */}
              <div className="w-10 text-right text-sm font-bold text-slate-100">
                {maxTemp}°
              </div>
            </div>
          );
        })}
      </div>
    </div>
  );
};
