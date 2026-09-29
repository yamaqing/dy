import { defineConfig, loadEnv } from 'vite'
import vue from '@vitejs/plugin-vue'
import { fileURLToPath, URL } from 'node:url'

export default defineConfig(({ mode }) => {
  const env = loadEnv(mode, process.cwd())
  // live-gateway 端口 80，路由在 Nacos 维护，网关不做路径重写，前端直接透传业务路径
  const gatewayTarget = env.VITE_GATEWAY_TARGET || 'http://localhost:80'
  return {
    plugins: [vue()],
    resolve: {
      alias: { '@': fileURLToPath(new URL('./src', import.meta.url)) }
    },
    server: {
      port: 5173,
      proxy: {
        // 按业务域前缀代理到 live-gateway（与网关路由表保持一致）
        '/user': { target: gatewayTarget, changeOrigin: true },
        '/feed': { target: gatewayTarget, changeOrigin: true },
        '/video': { target: gatewayTarget, changeOrigin: true },
        '/im': { target: gatewayTarget, changeOrigin: true },
        // 文生图 CDN 代理：解决封面图跨域（NotSameOrigin）问题
        '/api/ide/v1/text_to_image': {
          target: 'https://trae-api-cn.mchost.guru',
          changeOrigin: true,
          secure: true
        }
      }
    },
    build: {
      target: 'es2018',
      // 首屏只加载核心 chunk，播放器/评论等重组件在代码中动态 import
      chunkSizeWarningLimit: 800
    }
  }
})
