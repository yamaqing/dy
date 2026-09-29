/**
 * 与后端 DTO 对齐的 TS 类型定义
 * 标注 TODO 待联调确认 的字段为前端按合理假设先行定义，联调时以后端实际为准
 */

/** 统一响应体，对齐 live-api WebResDTO（当前后端无 msg 字段，错误文案塞在 data 中） */
export interface WebResDTO<T = unknown> {
  code: number
  data: T
  /** TODO 待联调确认：推动后端补充独立 msg 字段 */
  msg?: string
}

/** 对齐 WebResDTO.SUCCESS_CODE */
export const BIZ_SUCCESS = 200

// ==================== 用户域 ====================

/** JWT 双 token 结构（后端当前为 Cookie tltk，统一 JWT 后使用）TODO 待联调确认 */
export interface LoginTokens {
  accessToken: string
  refreshToken: string
  /** accessToken 有效期（秒） */
  expiresIn: number
}

/** 用户信息，对齐 UserDTO 的 Web 展示子集 */
export interface UserInfo {
  userId: number
  nickName: string
  avatar: string
  /** TODO 待联调确认：UserDTO 中是否存在以下字段 */
  followCount?: number
  fansCount?: number
  likeCount?: number
}

/** 手机验证码登录入参，对齐 MobileLoginParam */
export interface MobileLoginParam {
  mobile: string
  code: number
}

/** 登录响应，对齐 UserLoginDTO 扩展 token 字段 */
export interface UserLoginDTO {
  userId: number
  nickName: string
  avatar: string
  /** TODO 待联调确认：JWT 改造后由登录接口下发 */
  tokens: LoginTokens
}

// ==================== Feed / 视频域（live-video 模块待新建，以下全部为前端预定义） ====================

export interface VideoAuthor {
  userId: number
  nickName: string
  avatar: string
}

/** Feed 流视频项 */
export interface VideoItem {
  id: number
  /** 播放地址（mock 为 MP4，生产为 CDN 多码率/HLS） */
  playUrl: string
  /** 封面图 */
  coverUrl: string
  width: number
  height: number
  /** 视频文案 */
  title: string
  author: VideoAuthor
  musicName: string
  likeCount: number
  commentCount: number
  favoriteCount: number
  shareCount: number
  isLiked: boolean
  isFavorited: boolean
}

export type FeedType = 'recommend' | 'follow'

export interface FeedPageReq {
  /** 游标分页，禁止页码分页（防深分页与数据漂移） */
  cursor: string
  size: number
  type: FeedType
}

export interface FeedPage {
  list: VideoItem[]
  nextCursor: string
  hasMore: boolean
}

// ==================== 评论域 ====================

export interface CommentUser {
  userId: number
  nickName: string
  avatar: string
}

export interface CommentItem {
  id: number
  videoId: number
  content: string
  user: CommentUser
  likeCount: number
  /** 毫秒时间戳 */
  createTime: number
  replyCount: number
}

export interface CommentPage {
  list: CommentItem[]
  nextCursor: string
  hasMore: boolean
}

// ==================== 商城域（mall-product 模块，以下为前端预定义） TODO 待联调确认 ====================

/** 商品卡片项 */
export interface ProductItem {
  id: number
  /** 商品主图 */
  coverUrl: string
  /** 商品标题 */
  title: string
  /** 店铺名 */
  shopName: string
  /** 售价（分） */
  price: number
  /** 原价（分），无优惠时与 price 相同 */
  originPrice: number
  /** 已售件数 */
  sales: number
  /** 商品标签，如"直播价""秒杀" */
  tag?: string
}

export interface ProductPage {
  list: ProductItem[]
  nextCursor: string
  hasMore: boolean
}
