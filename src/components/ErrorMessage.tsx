import React from 'react';
import { SearchX, AlertTriangle, RotateCw } from 'lucide-react';

interface ErrorMessageProps {
  type: 'not_found' | 'network_error';
  query?: string;
  message?: string;
  onSelectPopularCity: (city: string) => void;
  popularCities: string[];
  onRetry?: () => void;
}

export const ErrorMessage: React.FC<ErrorMessageProps> = ({
  type,
  query,
  message,
  onSelectPopularCity,
  popularCities,
  onRetry
}) => {
  if (type === 'not_found') {
    return (
      <div className="rounded-3xl bg-slate-900/80 border border-rose-500/30 p-8 shadow-2xl backdrop-blur-xl text-center">
        <div className="w-16 h-16 mx-auto rounded-full bg-rose-500/15 border border-rose-500/30 flex items-center justify-center text-rose-400 mb-4">
          <SearchX className="w-8 h-8" />
        </div>

        <h3 className="text-xl font-bold text-white mb-2">
          City not found
        </h3>

        <p className="text-sm text-slate-300 max-w-md mx-auto leading-relaxed">
          {query
            ? `We couldn't locate any meteorological data for "${query}". Please check the spelling or try searching another location.`
            : 'City not found. Please enter a valid city name.'}
        </p>

        <div className="mt-6 pt-6 border-t border-slate-800/80">
          <p className="text-xs font-semibold text-sky-400 uppercase tracking-wider mb-3">
            Try one of these popular cities:
          </p>
          <div className="flex flex-wrap items-center justify-center gap-2">
            {popularCities.slice(0, 5).map((city) => (
              <button
                key={city}
                type="button"
                onClick={() => onSelectPopularCity(city)}
                className="px-3 py-1.5 bg-slate-800/80 hover:bg-slate-700 border border-slate-700/60 rounded-xl text-xs font-medium text-slate-200 hover:text-sky-300 transition-colors"
              >
                {city}
              </button>
            ))}
          </div>
        </div>
      </div>
    );
  }

  return (
    <div className="rounded-3xl bg-slate-900/80 border border-amber-500/30 p-8 shadow-2xl backdrop-blur-xl text-center">
      <div className="w-16 h-16 mx-auto rounded-full bg-amber-500/15 border border-amber-500/30 flex items-center justify-center text-amber-400 mb-4">
        <AlertTriangle className="w-8 h-8" />
      </div>

      <h3 className="text-xl font-bold text-white mb-2">
        Weather Service Error
      </h3>

      <p className="text-sm text-slate-300 max-w-md mx-auto leading-relaxed mb-6">
        {message || 'Unable to connect to Open-Meteo services. Please check your network connection and try again.'}
      </p>

      {onRetry && (
        <button
          type="button"
          onClick={onRetry}
          className="inline-flex items-center gap-2 px-5 py-2.5 bg-sky-500 hover:bg-sky-400 text-slate-950 font-bold text-sm rounded-xl transition-all shadow-lg"
        >
          <RotateCw className="w-4 h-4" />
          Retry Request
        </button>
      )}
    </div>
  );
};
