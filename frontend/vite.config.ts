import { defineConfig } from 'vite'
import react from '@vitejs/plugin-react'

export default defineConfig({
  base: '/new/',
  plugins: [react()],
  build: {
    outDir: '../src/main/resources/static/new',
    emptyOutDir: true,
  },
  server: {
    proxy: {
      '/api': {
        target: 'https://localhost:8443',
        changeOrigin: true,
        secure: false
      },
      '/images': {
        target: 'https://localhost:8443',
        changeOrigin: true,
        secure: false
      },
      '/login': {
        target: 'https://localhost:8443',
        changeOrigin: true,
        secure: false
      },
      '/logout': {
        target: 'https://localhost:8443',
        changeOrigin: true,
        secure: false
      }
    }
  }
})
