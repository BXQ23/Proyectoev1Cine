import { defineConfig } from 'vite'
import react from '@vitejs/plugin-react'

// Config minima: Vite + plugin de React.
// El puerto 3000 lo dejamos fijo porque el backend (CorsConfig) ya
// tiene autorizado ese origen especifico.
export default defineConfig({
  plugins: [react()],
  server: {
    port: 3000,
  },
})
