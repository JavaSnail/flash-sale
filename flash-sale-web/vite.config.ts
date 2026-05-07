import { defineConfig } from 'vite'
import react from '@vitejs/plugin-react'
import path from 'path'
import type { IncomingMessage } from 'http'

// 浏览器直接访问（刷新页面）时 Accept 包含 text/html，应回退到 index.html 而非代理到后端
function bypassHtmlRequest(_req: IncomingMessage) {
  if (_req.headers.accept?.includes('text/html')) {
    return '/index.html';
  }
}

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
        bypass: bypassHtmlRequest,
      },
      '/goods': {
        target: 'http://localhost:8080',
        changeOrigin: true,
        bypass: bypassHtmlRequest,
      },
      '/seckill': {
        target: 'http://localhost:8080',
        changeOrigin: true,
        bypass: bypassHtmlRequest,
      },
      '/order': {
        target: 'http://localhost:8080',
        changeOrigin: true,
        bypass: bypassHtmlRequest,
      },
      '/pay': {
        target: 'http://localhost:8080',
        changeOrigin: true,
        bypass: bypassHtmlRequest,
      },
    },
  },
})