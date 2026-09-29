/** @type {import('tailwindcss').Config} */
export default {
  content: ['./index.html', './src/**/*.{vue,ts}'],
  theme: {
    extend: {
      colors: {
        // 抖音品牌色
        primary: '#FE2C55',
        secondary: '#25F4EE',
        ink: '#161823',
        // 深色分层表面与文字三级体系（对齐抖音网页版）
        surface: '#161823',
        'surface-2': '#1F1F29',
        'surface-3': '#2A2A36',
        line: 'rgba(255, 255, 255, 0.08)',
        'txt-1': 'rgba(255, 255, 255, 1)',
        'txt-2': 'rgba(255, 255, 255, 0.6)',
        'txt-3': 'rgba(255, 255, 255, 0.35)'
      },
      keyframes: {
        // 表单校验失败抖动
        shake: {
          '0%, 100%': { transform: 'translateX(0)' },
          '20%, 60%': { transform: 'translateX(-6px)' },
          '40%, 80%': { transform: 'translateX(6px)' }
        },
        // 双击点赞爱心爆发动画
        heartPop: {
          '0%': { transform: 'translate(-50%, -50%) scale(0) rotate(0deg)', opacity: '0' },
          '15%': { transform: 'translate(-50%, -50%) scale(1.2) rotate(-8deg)', opacity: '1' },
          '30%': { transform: 'translate(-50%, -50%) scale(0.95) rotate(6deg)' },
          '45%': { transform: 'translate(-50%, -50%) scale(1) rotate(0deg)' },
          '80%': { opacity: '1' },
          '100%': { transform: 'translate(-50%, -120%) scale(1.1)', opacity: '0' }
        },
        // 音乐名跑马灯
        marquee: {
          '0%': { transform: 'translateX(0)' },
          '100%': { transform: 'translateX(-50%)' }
        },
        // 点赞按钮弹跳
        likeBounce: {
          '0%': { transform: 'scale(1)' },
          '40%': { transform: 'scale(1.35)' },
          '100%': { transform: 'scale(1)' }
        }
      },
      animation: {
        'heart-pop': 'heartPop 0.9s ease-out forwards',
        marquee: 'marquee 8s linear infinite',
        'like-bounce': 'likeBounce 0.35s ease-out',
        shake: 'shake 0.4s ease-in-out'
      }
    }
  },
  plugins: []
}
