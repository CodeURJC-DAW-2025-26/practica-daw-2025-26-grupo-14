import { defineConfig } from 'vite'
import react from '@vitejs/plugin-react'

export default defineConfig({
  plugins: [react()],
  server: {
    proxy: {
      '/api': { target: 'https://localhost:8443', secure: false, changeOrigin: true, followRedirects: false },
      '/images': { target: 'https://localhost:8443', secure: false, changeOrigin: true, followRedirects: false },
      '/login': { target: 'https://localhost:8443', secure: false, changeOrigin: true, followRedirects: false },
      '/logout': { target: 'https://localhost:8443', secure: false, changeOrigin: true, followRedirects: false }
    }
  }
})
