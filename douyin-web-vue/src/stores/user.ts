import { defineStore } from 'pinia'
import { userApi } from '@/api/modules/user'
import type { UserInfo } from '@/api/types'
import { clearTokens, clearUserCache, getUserCache, saveUserCache, setTokens } from '@/utils/token'

/** 登录态与用户信息 */
export const useUserStore = defineStore('user', {
  state: () => ({
    userInfo: null as UserInfo | null
  }),
  getters: {
    isLogin: (s) => s.userInfo !== null
  },
  actions: {
    /** 应用启动时从本地缓存恢复登录态（mock 模式下以缓存为准，生产应由 token 有效性驱动） */
    restore() {
      const raw = getUserCache()
      if (!raw) return
      try {
        this.userInfo = JSON.parse(raw) as UserInfo
      } catch {
        clearUserCache()
      }
    },
    async sendCode(mobile: string) {
      if (!/^1[3-9]\d{9}$/.test(mobile)) throw new Error('请输入正确的手机号')
      await userApi.sendSMS(mobile)
    },
    async login(mobile: string, code: number) {
      const res = await userApi.mobileLogin({ mobile, code })
      setTokens(res.tokens.accessToken, res.tokens.refreshToken)
      this.userInfo = { userId: res.userId, nickName: res.nickName, avatar: res.avatar }
      saveUserCache(JSON.stringify(this.userInfo))
    },
    logout() {
      clearTokens()
      clearUserCache()
      this.userInfo = null
    }
  }
})
