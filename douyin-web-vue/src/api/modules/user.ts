import { request } from '../request'
import type { LoginTokens, MobileLoginParam, UserInfo, UserLoginDTO } from '../types'

/** 用户域接口（对齐 live-api UserController 现有路径） */
export const userApi = {
  /** 下发短信验证码：后端为 @RequestParam 形式 */
  sendSMS: (mobile: string) => request.post<string>('/user/sendSMS', null, { mobile }),

  /** 手机验证码登录（未注册自动注册） */
  mobileLogin: (param: MobileLoginParam) => request.post<UserLoginDTO>('/user/mobileLogin', param),

  /** 查询用户信息 */
  queryUser: (userId: number) => request.post<UserInfo>('/user/queryUser', null, { userId }),

  /** 刷新 accessToken TODO 待联调确认：后端接口待新建 */
  refreshToken: (refreshToken: string) => request.post<LoginTokens>('/user/refreshToken', { refreshToken })
}
