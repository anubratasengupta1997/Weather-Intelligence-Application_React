import React from 'react';
import { DailyRecommendation, RecommendationLevel } from '../types/weather';

interface DailyPlanningProps {
  recommendations: DailyRecommendation[];
}

export const DailyPlanning: React.FC<DailyPlanningProps> = ({ recommendations }) => {
  if (!recommendations || recommendations.length === 0) return null;

  const getStatusBadge = (status: RecommendationLevel) => {
    switch (status) {
      case 'optimal':
        return { text: 'Optimal', bg: 'bg-emerald-500/15 border-emerald-500/30 text-emerald-400' };
      case 'caution':
        return { text: 'Caution', bg: 'bg-amber-500/15 border-amber-500/30 text-amber-400' };
      case 'warning':
        return { text: 'Alert', bg: 'bg-rose-500/15 border-rose-500/30 text-rose-400' };
      case 'info':
      default:
        return { text: 'Notice', bg: 'bg-sky-500/15 border-sky-500/30 text-sky-400' };
    }
  };

  return (
    <div className="rounded-3xl bg-slate-900/80 border border-slate-800/80 p-5 sm:p-6 shadow-xl backdrop-blur-xl">
      <div className="flex items-center justify-between mb-4">
        <h3 className="text-xs font-bold uppercase tracking-wider text-slate-400">
          Daily Intelligence Briefing
        </h3>
        <span className="px-2.5 py-0.5 rounded-full text-[11px] font-bold bg-sky-500/10 text-sky-400 border border-sky-500/20">
          Smart Advice
        </span>
      </div>

      <div className="grid grid-cols-1 md:grid-cols-2 gap-3.5">
        {recommendations.map((rec, idx) => {
          const badge = getStatusBadge(rec.status);
          return (
            <div
              key={idx}
              className="flex items-start gap-3.5 p-4 rounded-2xl bg-slate-800/40 border border-slate-700/40 hover:border-slate-700/80 transition-all"
            >
              <div className="w-11 h-11 rounded-2xl bg-slate-800/80 border border-slate-700/60 flex items-center justify-center text-2xl flex-shrink-0 shadow">
                {rec.icon}
              </div>

              <div className="flex-1 min-w-0">
                <div className="flex items-center justify-between gap-2">
                  <span className="text-[10px] font-bold uppercase tracking-wider text-slate-400">
                    {rec.category}
                  </span>
                  <span className={`px-2 py-0.5 rounded-full text-[10px] font-bold border ${badge.bg}`}>
                    {badge.text}
                  </span>
                </div>

                <h4 className="text-sm font-bold text-slate-100 mt-1 truncate">
                  {rec.headline}
                </h4>

                <p className="text-xs text-slate-300/90 mt-1 leading-relaxed">
                  {rec.advice}
                </p>
              </div>
            </div>
          );
        })}
      </div>
    </div>
  );
};
