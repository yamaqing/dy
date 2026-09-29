/**
 * Token 存取策略（JWT 双 token 设计，兼容过渡期的 Cookie tltk 模式）：
 * - accessToken 存 sessionStorage：页签关闭即失效，降低 XSS 窃取后的存活窗口
 * - refreshToken 存 localStorage：TODO 待联调确认，生产应改为 HttpOnly Cookie 由后端下发
 */
const AT_KEY = 'tk_access'
const RT_KEY = 'tk_refresh'
const USER_KEY = 'tk_user'

export function getAccessToken(): string {
  return sessionStorage.getItem(AT_KEY) ?? ''
}

export function getRefreshToken(): string {
  return localStorage.getItem(RT_KEY) ?? ''
}

export function setTokens(accessToken: string, refreshToken: string): void {
  sessionStorage.setItem(AT_KEY, accessToken)
  localStorage.setItem(RT_KEY, refreshToken)
}

export function clearTokens(): void {
  sessionStorage.removeItem(AT_KEY)
  localStorage.removeItem(RT_KEY)
}

export function saveUserCache(json: string): void {
  localStorage.setItem(USER_KEY, json)
}

export function getUserCache(): string {
  return localStorage.getItem(USER_KEY) ?? ''
}

export function clearUserCache(): void {
  localStorage.removeItem(USER_KEY)
}
