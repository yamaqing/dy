/**
 * Axios 二次封装：
 * 1. 统一响应体 WebResDTO 解码（code !== 200 抛 BizError，兼容错误文案塞 data 的现状）
 * 2. 请求拦截自动携带 accessToken（JWT Bearer 模式，兼容 Cookie 过渡方案）
 * 3. 401 双 token 静默刷新：单例 Promise 锁，并发请求只刷新一次，成功后重放原请求队列
 */
import axios, { AxiosError } from 'axios'
import type { AxiosRequestConfig, InternalAxiosRequestConfig } from 'axios'
import type { LoginTokens, WebResDTO } from './types'
import { BIZ_SUCCESS } from './types'
import { mockAdapter } from './mock'
import { clearTokens, getAccessToken, getRefreshToken, setTokens } from '@/utils/token'

const USE_MOCK = import.meta.env.VITE_USE_MOCK === 'true'

/** 业务异常（code !== 200） */
export class BizError extends Error {
  code: number
  constructor(code: number, message: string) {
    super(message)
    this.code = code
  }
}

const baseConfig: AxiosRequestConfig = {
  timeout: 10000,
  // mock 模式下走内置适配器，请求仍完整经过拦截器链路
  ...(USE_MOCK ? { adapter: mockAdapter } : {})
}

const instance = axios.create(baseConfig)
/** 裸实例：仅用于刷新 token，避免刷新请求本身进入 401 拦截造成死循环 */
const rawInstance = axios.create(baseConfig)

// ---------- 请求拦截：携带 accessToken ----------
instance.interceptors.request.use((config) => {
  const token = getAccessToken()
  if (token) config.headers.Authorization = `Bearer ${token}`
  return config
})

// ---------- 双 token 静默刷新 ----------
let refreshing: Promise<string> | null = null

async function refreshAccessToken(): Promise<string> {
  const refreshToken = getRefreshToken()
  if (!refreshToken) throw new Error('no refresh token')
  // TODO 待联调确认：刷新接口路径与入参格式
  const resp = await rawInstance.post<WebResDTO<LoginTokens>>('/user/refreshToken', { refreshToken })
  const body = resp.data
  if (body.code !== BIZ_SUCCESS) throw new Error('refresh token expired')
  setTokens(body.data.accessToken, body.data.refreshToken)
  return body.data.accessToken
}

// ---------- 响应拦截：统一解码 + 401 静默刷新重放 ----------
instance.interceptors.response.use(
  (resp) => {
    const body = resp.data as WebResDTO
    if (body && typeof body.code === 'number') {
      if (body.code === BIZ_SUCCESS) return body.data as never
      // 兼容现状：后端错误文案可能塞在 data 字段；TODO 待联调确认 msg 字段
      const msg = body.msg ?? (typeof body.data === 'string' ? body.data : '请求失败，请稍后重试')
      return Promise.reject(new BizError(body.code, msg))
    }
    return resp.data as never
  },
  async (error: AxiosError) => {
    const { response, config } = error
    const retryConfig = config as (InternalAxiosRequestConfig & { _retry?: boolean }) | undefined
    if (response?.status === 401 && retryConfig && !retryConfig._retry) {
      retryConfig._retry = true
      try {
        // 单例锁：并发 401 共享同一次刷新
        refreshing ??= refreshAccessToken().finally(() => {
          refreshing = null
        })
        const token = await refreshing
        retryConfig.headers.Authorization = `Bearer ${token}`
        return instance(retryConfig)
      } catch {
        // 刷新失败：清登录态，通知全局弹登录框
        clearTokens()
        window.dispatchEvent(new CustomEvent('app:login-required'))
      }
    }
    return Promise.reject(error)
  }
)

/** 对外暴露的简化请求方法（响应已在拦截器中解包为 data） */
export const request = {
  get: <T>(url: string, params?: Record<string, unknown>) => instance.get<unknown, T>(url, { params }),
  post: <T>(url: string, data?: unknown, params?: Record<string, unknown>) =>
    instance.post<unknown, T>(url, data, { params })
}
