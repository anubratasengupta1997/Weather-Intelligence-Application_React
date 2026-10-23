import React, { useState, useEffect, useRef } from 'react';
import { Search, X, MapPin, Loader2 } from 'lucide-react';
import { GeocodingResult } from '../types/weather';
import { searchCities } from '../services/weatherApi';

interface SearchBarProps {
  onSearch: (city: string) => void;
  onSelectLocation: (location: GeocodingResult) => void;
  popularCities: string[];
}

export const SearchBar: React.FC<SearchBarProps> = ({
  onSearch,
  onSelectLocation,
  popularCities
}) => {
  const [query, setQuery] = useState('');
  const [suggestions, setSuggestions] = useState<GeocodingResult[]>([]);
  const [loadingSuggestions, setLoadingSuggestions] = useState(false);
  const [showDropdown, setShowDropdown] = useState(false);
  const containerRef = useRef<HTMLDivElement>(null);

  useEffect(() => {
    if (query.trim().length < 2) {
      setSuggestions([]);
      setLoadingSuggestions(false);
      return;
    }

    const timer = setTimeout(async () => {
      setLoadingSuggestions(true);
      try {
        const results = await searchCities(query);
        setSuggestions(results);
        setShowDropdown(results.length > 0);
      } catch {
        setSuggestions([]);
      } finally {
        setLoadingSuggestions(false);
      }
    }, 300);

    return () => clearTimeout(timer);
  }, [query]);

  // Handle outside click to close suggestions
  useEffect(() => {
    const handleClickOutside = (e: MouseEvent) => {
      if (containerRef.current && !containerRef.current.contains(e.target as Node)) {
        setShowDropdown(false);
      }
    };
    document.addEventListener('mousedown', handleClickOutside);
    return () => document.removeEventListener('mousedown', handleClickOutside);
  }, []);

  const handleSubmit = (e: React.FormEvent) => {
    e.preventDefault();
    if (query.trim()) {
      setShowDropdown(false);
      onSearch(query.trim());
    }
  };

  const handleSelectCity = (item: GeocodingResult) => {
    setQuery(item.name);
    setShowDropdown(false);
    onSelectLocation(item);
  };

  const handleChipClick = (city: string) => {
    setQuery(city);
    setShowDropdown(false);
    onSearch(city);
  };

  return (
    <div ref={containerRef} className="w-full relative">
      <form onSubmit={handleSubmit} className="relative flex items-center">
        <Search className="absolute left-4 w-5 h-5 text-sky-400 pointer-events-none" />
        <input
          type="text"
          value={query}
          onChange={(e) => setQuery(e.target.value)}
          onFocus={() => suggestions.length > 0 && setShowDropdown(true)}
          placeholder="Search city (e.g. Paris, Tokyo, London)..."
          className="w-full pl-12 pr-24 py-3.5 bg-slate-900/80 border border-slate-700/60 rounded-2xl text-slate-100 placeholder-slate-400 focus:outline-none focus:border-sky-400 focus:ring-1 focus:ring-sky-400 shadow-lg backdrop-blur-md transition-all text-sm md:text-base"
        />

        <div className="absolute right-3 flex items-center gap-1">
          {loadingSuggestions && (
            <Loader2 className="w-4 h-4 text-sky-400 animate-spin mr-1" />
          )}
          {query && (
            <button
              type="button"
              onClick={() => {
                setQuery('');
                setSuggestions([]);
                setShowDropdown(false);
              }}
              className="p-1 hover:bg-slate-800 rounded-full text-slate-400 hover:text-slate-200 transition-colors"
            >
              <X className="w-4 h-4" />
            </button>
          )}
          <button
            type="submit"
            className="px-3 py-1.5 bg-sky-500 hover:bg-sky-400 text-slate-950 font-medium text-xs rounded-xl transition-all shadow"
          >
            Search
          </button>
        </div>
      </form>

      {/* Dropdown Suggestions */}
      {showDropdown && suggestions.length > 0 && (
        <div className="absolute z-30 left-0 right-0 mt-2 bg-slate-900/95 border border-slate-700/80 rounded-2xl shadow-2xl backdrop-blur-xl overflow-hidden">
          {suggestions.slice(0, 5).map((item) => {
            const subtitle = [item.admin1, item.country].filter(Boolean).join(', ');
            return (
              <button
                key={item.id}
                type="button"
                onClick={() => handleSelectCity(item)}
                className="w-full px-4 py-3 flex items-center gap-3 text-left hover:bg-slate-800/80 transition-colors border-b border-slate-800/40 last:border-0"
              >
                <MapPin className="w-4 h-4 text-sky-400 flex-shrink-0" />
                <div>
                  <div className="text-slate-100 font-semibold text-sm">{item.name}</div>
                  {subtitle && <div className="text-slate-400 text-xs">{subtitle}</div>}
                </div>
              </button>
            );
          })}
        </div>
      )}

      {/* Popular City Chips */}
      <div className="flex items-center gap-2 mt-3 overflow-x-auto pb-1 scrollbar-none">
        <span className="text-xs font-semibold text-slate-400 uppercase tracking-wider flex-shrink-0 mr-1">
          Popular:
        </span>
        {popularCities.map((city) => (
          <button
            key={city}
            type="button"
            onClick={() => handleChipClick(city)}
            className="px-3 py-1 bg-slate-800/70 hover:bg-slate-700/80 border border-slate-700/40 rounded-full text-xs text-slate-300 hover:text-sky-300 font-medium whitespace-nowrap transition-all"
          >
            {city}
          </button>
        ))}
      </div>
    </div>
  );
};
