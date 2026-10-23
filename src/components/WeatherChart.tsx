import React, { useState } from 'react';
import { HourlyItem } from '../types/weather';
import { getWeatherEmoji } from '../services/weatherApi';

interface WeatherChartProps {
  hourly: HourlyItem[];
  tempUnit: 'C' | 'F';
}

export const WeatherChart: React.FC<WeatherChartProps> = ({ hourly, tempUnit }) => {
  const [metric, setMetric] = useState<'temp' | 'precip'>('temp');

  if (!hourly || hourly.length === 0) return null;

  const displayItems = hourly.slice(0, 24);

  const getMetricValue = (item: HourlyItem) => {
    if (metric === 'temp') {
      return tempUnit === 'C' ? item.temp : (item.temp * 9) / 5 + 32;
    }
    return item.precipProb;
  };

  const values = displayItems.map(getMetricValue);
  const minVal = metric === 'precip' ? 0 : Math.min(...values) - 2;
  const maxVal = metric === 'precip' ? 100 : Math.max(...values) + 2;
  const range = Math.max(1, maxVal - minVal);

  const width = 800;
  const height = 180;
  const paddingX = 30;
  const paddingTop = 30;
  const paddingBottom = 30;
  const chartWidth = width - paddingX * 2;
  const chartHeight = height - paddingTop - paddingBottom;

  const points = values.map((val, idx) => {
    const x = paddingX + (idx / (values.length - 1)) * chartWidth;
    const y = paddingTop + chartHeight - ((val - minVal) / range) * chartHeight;
    return { x, y, val };
  });

  // Build SVG path
  let pathD = `M ${points[0].x} ${points[0].y}`;
  for (let i = 0; i < points.length - 1; i++) {
    const curr = points[i];
    const next = points[i + 1];
    const cx = (curr.x + next.x) / 2;
    pathD += ` C ${cx} ${curr.y}, ${cx} ${next.y}, ${next.x} ${next.y}`;
  }

  const areaD = `${pathD} L ${points[points.length - 1].x} ${height - paddingBottom} L ${points[0].x} ${height - paddingBottom} Z`;

  const chartColor = metric === 'temp' ? '#00e5ff' : '#38bdf8';

  return (
    <div className="rounded-3xl bg-slate-900/80 border border-slate-800/80 p-5 sm:p-6 shadow-xl backdrop-blur-xl">
      {/* Header and Toggle */}
      <div className="flex items-center justify-between mb-4">
        <div>
          <h3 className="text-xs font-bold uppercase tracking-wider text-slate-400">
            24-Hour Weather Data Chart
          </h3>
          <p className="text-sm font-semibold text-slate-200 mt-0.5">
            {metric === 'temp' ? `Hourly Temperature (${tempUnit === 'C' ? '°C' : '°F'})` : 'Rain Probability (%)'}
          </p>
        </div>

        <div className="flex items-center bg-slate-800/80 p-1 rounded-full border border-slate-700/60 text-xs font-bold">
          <button
            type="button"
            onClick={() => setMetric('temp')}
            className={`px-3 py-1 rounded-full transition-all ${
              metric === 'temp'
                ? 'bg-sky-500 text-slate-950 shadow'
                : 'text-slate-400 hover:text-slate-200'
            }`}
          >
            Temp
          </button>
          <button
            type="button"
            onClick={() => setMetric('precip')}
            className={`px-3 py-1 rounded-full transition-all ${
              metric === 'precip'
                ? 'bg-sky-500 text-slate-950 shadow'
                : 'text-slate-400 hover:text-slate-200'
            }`}
          >
            Rain %
          </button>
        </div>
      </div>

      {/* SVG Responsive Chart */}
      <div className="w-full overflow-hidden">
        <svg
          viewBox={`0 0 ${width} ${height}`}
          className="w-full h-40 sm:h-48 drop-shadow"
          preserveAspectRatio="none"
        >
          <defs>
            <linearGradient id="areaGradient" x1="0" y1="0" x2="0" y2="1">
              <stop offset="0%" stopColor={chartColor} stopOpacity="0.35" />
              <stop offset="100%" stopColor={chartColor} stopOpacity="0.0" />
            </linearGradient>
          </defs>

          {/* Area fill */}
          <path d={areaD} fill="url(#areaGradient)" />

          {/* Curve stroke */}
          <path
            d={pathD}
            fill="none"
            stroke={chartColor}
            strokeWidth="3"
            strokeLinecap="round"
          />

          {/* Nodes and Labels */}
          {points.map((pt, idx) => {
            const isNow = displayItems[idx].isNow;
            const showLabel = isNow || idx % 4 === 0 || idx === points.length - 1;
            if (!showLabel) return null;

            return (
              <g key={idx}>
                {isNow && (
                  <circle
                    cx={pt.x}
                    cy={pt.y}
                    r="8"
                    fill="none"
                    stroke={chartColor}
                    strokeWidth="2"
                    opacity="0.6"
                  />
                )}
                <circle
                  cx={pt.x}
                  cy={pt.y}
                  r={isNow ? 5 : 3.5}
                  fill={isNow ? '#ffffff' : chartColor}
                />
                <text
                  x={pt.x}
                  y={pt.y - 10}
                  textAnchor="middle"
                  fill="#ffffff"
                  fontSize="12"
                  fontWeight="600"
                >
                  {metric === 'temp' ? `${Math.round(pt.val)}°` : `${Math.round(pt.val)}%`}
                </text>
                <text
                  x={pt.x}
                  y={height - 8}
                  textAnchor="middle"
                  fill="#94a3b8"
                  fontSize="10"
                  fontWeight="500"
                >
                  {displayItems[idx].timeLabel}
                </text>
              </g>
            );
          })}
        </svg>
      </div>

      {/* Hourly Quick Scroll Cards */}
      <div className="flex items-center gap-2.5 overflow-x-auto mt-4 pt-2 pb-1 scrollbar-thin">
        {displayItems.map((item, idx) => {
          const displayTemp = tempUnit === 'C' ? Math.round(item.temp) : Math.round((item.temp * 9) / 5 + 32);
          return (
            <div
              key={idx}
              className={`flex-shrink-0 flex flex-col items-center px-3 py-2.5 rounded-xl border transition-all ${
                item.isNow
                  ? 'bg-sky-500/15 border-sky-400/50 shadow-md'
                  : 'bg-slate-800/50 border-slate-700/40'
              }`}
            >
              <span className={`text-[11px] font-semibold ${item.isNow ? 'text-sky-300' : 'text-slate-400'}`}>
                {item.timeLabel}
              </span>
              <span className="text-xl my-1">
                {getWeatherEmoji(item.weatherCode)}
              </span>
              <span className="text-sm font-bold text-slate-100">
                {displayTemp}°
              </span>
              {item.precipProb > 0 && (
                <span className="text-[10px] font-semibold text-sky-400 mt-0.5">
                  {item.precipProb}%
                </span>
              )}
            </div>
          );
        })}
      </div>
    </div>
  );
};
