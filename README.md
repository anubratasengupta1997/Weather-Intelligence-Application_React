# Weather Intelligence App (React + Vite) 🌤️

A modern, high-precision weather application and daily intelligence planner built with React, Vite, TypeScript, and Tailwind CSS, optimized for zero-config deployment to **Cloudflare Pages**.

Powered by the **Open-Meteo Geocoding API** and **Open-Meteo Forecast API**.

---

## ⚡ Cloudflare Pages Deployment (Step-by-Step)

### Option 1: Git Integration (Recommended)
1. Push this repository to **GitHub** or **GitLab**.
2. Log in to the [Cloudflare Dashboard](https://dash.cloudflare.com/).
3. Navigate to **Workers & Pages** > **Create application** > **Pages** > **Connect to Git**.
4. Select your repository.
5. In the **Build configuration** settings, configure:
   - **Framework preset:** `Vite`
   - **Build command:** `npm run build`
   - **Build output directory:** `dist`
   - **Root directory:** `/` (leave blank)
   - **Environment variables:** None required (Open-Meteo is free & keyless)
6. Click **Save and Deploy**. Cloudflare Pages will build and deploy your application with global edge CDN caching and automatic SSL.

Client-side SPA routing (`_redirects`) and security headers (`_headers`) are pre-configured in `public/` and automatically emitted to `dist/`.

---

### Option 2: Direct Upload via Wrangler CLI
You can also deploy directly from your terminal using Cloudflare's Wrangler CLI:

1. Authenticate with Cloudflare:
   ```bash
   npx wrangler login
   ```
2. Build the production assets:
   ```bash
   npm run build
   ```
3. Deploy to Cloudflare Pages:
   ```bash
   npm run deploy
   # Or: npx wrangler pages deploy dist --project-name=weather-intelligence-app
   ```

---

## 🛠️ Local Development

### Prerequisites
- Node.js (v18 or higher)
- npm, yarn, or pnpm

### Run Locally
```bash
# Install dependencies
npm install

# Start development server
npm run dev
```
Open `http://localhost:5173` in your browser.

### Production Build & Preview
```bash
# Typecheck and bundle
npm run build

# Preview production build locally
npm run preview
```

---

## 🌟 Application Features

1. **City Search & Geocoding:**
   - Converts city names into geographical coordinates using Open-Meteo Geocoding (`https://geocoding-api.open-meteo.com/v1/search`).
   - Debounced autocomplete suggestions and quick chips for popular cities (*Tokyo, New York, London, Paris, Sydney, San Francisco*).

2. **Accurate Weather Forecasts:**
   - Real-time current weather metrics: temperature, feels-like, humidity, wind speed, air pressure, UV index, and precipitation via Open-Meteo Forecast (`https://api.open-meteo.com/v1/forecast`).
   - Unit toggle between Celsius (°C) and Fahrenheit (°F).

3. **24-Hour Weather Data Chart:**
   - Interactive SVG area curve showing temperature fluctuations and rain probability percentages over the next 24 hours.

4. **Daily Intelligence Briefing & Planning:**
   - Clothing and layering recommendations based on temperatures and wind chill.
   - Rain and umbrella alerts based on precipitation probability.
   - Outdoor fitness and sports safety indicators.
   - UV and sun protection guidance.

5. **7-Day Forecast Cards:**
   - 7-day projection with condition summaries, rain chances, and visual high/low temperature range bars.

6. **Graceful Error Handling:**
   - Friendly **"City not found"** view with spelling suggestions and quick recovery chips.

---

## 📁 Repository Structure

```
├── public/
│   ├── _headers         # Cloudflare Pages security headers
│   ├── _redirects       # Cloudflare Pages SPA client-side routing
│   └── favicon.svg      # App icon
├── src/
│   ├── components/
│   │   ├── CurrentWeather.tsx  # Hero card with temperature & condition
│   │   ├── DailyForecast.tsx   # 7-day cards with min/max range bars
│   │   ├── DailyPlanning.tsx   # Intelligence briefing recommendations
│   │   ├── ErrorMessage.tsx    # "City not found" & error state handler
│   │   ├── SearchBar.tsx       # Search bar with suggestions & chips
│   │   ├── WeatherChart.tsx    # 24h interactive SVG data chart
│   │   └── WeatherMetrics.tsx  # Humidity, wind, UV, pressure, rain
│   ├── services/
│   │   └── weatherApi.ts       # Open-Meteo Geocoding & Forecast client
│   ├── types/
│   │   └── weather.ts          # TypeScript domain interfaces
│   ├── App.tsx                 # Core application controller
│   ├── index.css               # Tailwind CSS & theme styling
│   └── main.tsx                # React entry point
├── dist/                       # Production output (for Cloudflare)
├── package.json                # Dependencies & build scripts
├── tsconfig.json               # TypeScript compiler config
├── vite.config.ts              # Vite bundler config
└── wrangler.toml               # Cloudflare Pages / Wrangler config
```
