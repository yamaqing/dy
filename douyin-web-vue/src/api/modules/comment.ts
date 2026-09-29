import { request } from '../request'
import type { CommentItem, CommentPage } from '../types'

/** 评论域接口（后端待新建，路径为前端预定义） TODO 待联调确认 */
export const commentApi = {
  /** 评论列表：游标分页 */
  list: (videoId: number, cursor: string, size = 20) =>
    request.get<CommentPage>('/video/comments', { videoId, cursor, size }),

  /** 发表评论（当前用户由后端从 token 解析） */
  add: (videoId: number, content: string) => request.post<CommentItem>('/video/comment', { videoId, content })
}
