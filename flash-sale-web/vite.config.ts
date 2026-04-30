import { defineConfig } from 'vite'
import react from '@vitejs/plugin-react'
import path from 'path'

export default defineConfig({
  plugins: [react()],
  resolve: {
    alias: {
      '@': path.resolve(__dirname, 'src'),
    },
  },
  server: {
    port: 5173,
    proxy: {
      '/user': {
        target: 'http://localhost:8080',
        changeOrigin: true,
      },
      '/goods': {
        target: 'http://localhost:8080',
        changeOrigin: true,
      },
      '/seckill': {
        target: 'http://localhost:8080',
        changeOrigin: true,
      },
      '/order': {
        target: 'http://localhost:8080',
        changeOrigin: true,
      },
      '/pay': {
        target: 'http://localhost:8080',
        changeOrigin: true,
      },
    },
  },
})
