import { defineStore } from 'pinia'
import type { VideoItem } from '@/api/types'

export interface Toast {
  id: number
  text: string
  type: 'info' | 'success' | 'error'
}

/** 全局 UI 状态：Toast / 登录弹窗 / 评论抽屉 */
export const useAppStore = defineStore('app', {
  state: () => ({
    toasts: [] as Toast[],
    loginVisible: false,
    /** 当前打开评论抽屉的视频，null 表示抽屉关闭 */
    commentVideo: null as VideoItem | null
  }),
  actions: {
    toast(text: string, type: Toast['type'] = 'info') {
      const id = Date.now() + Math.random()
      this.toasts.push({ id, text, type })
      // 最多保留 3 条，防止连续触发时堆叠
      if (this.toasts.length > 3) this.toasts.shift()
      setTimeout(() => {
        this.toasts = this.toasts.filter((t) => t.id !== id)
      }, 2400)
    },
    openLogin() {
      this.loginVisible = true
    },
    closeLogin() {
      this.loginVisible = false
    },
    openComments(video: VideoItem) {
      this.commentVideo = video
    },
    closeComments() {
      this.commentVideo = null
    }
  }
})
