import { request } from '../request'
import type { FeedPage, FeedPageReq } from '../types'

/**
 * Feed / 视频域接口
 * 注：后端 live-video 模块待新建，路径为前端预定义 TODO 待联调确认
 */
export const feedApi = {
  /** 推荐/关注 Feed 流：游标分页 */
  recommend: (param: FeedPageReq) => request.get<FeedPage>('/feed/recommend', { ...param }),

  /** 点赞/取消点赞 */
  toggleLike: (videoId: number, liked: boolean) =>
    request.post<{ isLiked: boolean; likeCount: number }>('/video/like', { videoId, liked }),

  /** 收藏/取消收藏 */
  toggleFavorite: (videoId: number, favorited: boolean) =>
    request.post<{ isFavorited: boolean; favoriteCount: number }>('/video/favorite', { videoId, favorited })
}
