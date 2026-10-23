/** @type {import('tailwindcss').Config} */
export default {
  content: [
    "./index.html",
    "./src/**/*.{js,ts,jsx,tsx}",
  ],
  theme: {
    extend: {
      colors: {
        weather: {
          dark: '#070E1E',
          card: '#131F3D',
          border: '#27447D',
          accent: '#00E5FF'
        }
      }
    },
  },
  plugins: [],
}
