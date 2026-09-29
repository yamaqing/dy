import { defineStore } from 'pinia'
import { feedApi } from '@/api/modules/feed'
import type { FeedType, VideoItem } from '@/api/types'

/** Feed 流数据与播放调度状态 */
export const useFeedStore = defineStore('feed', {
  state: () => ({
    items: [] as VideoItem[],
    cursor: '',
    hasMore: true,
    loading: false,
    error: '',
    /** 当前播放索引（滑窗中位） */
    activeIndex: 0,
    /** 全局静音：浏览器自动播放策略要求首个视频静音起播，用户手势后解锁 */
    muted: true,
    feedType: 'recommend' as FeedType
  }),
  getters: {
    activeItem: (s): VideoItem | null => s.items[s.activeIndex] ?? null
  },
  actions: {
    async loadMore() {
      // 加载中/无更多时直接返回，配合滚动触发天然节流
      if (this.loading || !this.hasMore) return
      this.loading = true
      this.error = ''
      try {
        const page = await feedApi.recommend({ cursor: this.cursor || '0', size: 5, type: this.feedType })
        this.items.push(...page.list)
        this.cursor = page.nextCursor
        this.hasMore = page.hasMore
      } catch (e) {
        this.error = e instanceof Error ? e.message : '加载失败，请点击重试'
      } finally {
        this.loading = false
      }
    },
    /** 刷新（切换 tab / 下拉刷新）：清空游标重新拉取 */
    async refresh() {
      this.items = []
      this.cursor = ''
      this.hasMore = true
      this.activeIndex = 0
      await this.loadMore()
    },
    async switchType(type: FeedType) {
      if (type === this.feedType) return
      this.feedType = type
      await this.refresh()
    },
    setActive(index: number) {
      this.activeIndex = index
      // 预取策略：距离末尾 2 条时提前加载下一页，保证滑动不中断
      if (this.hasMore && index >= this.items.length - 2) void this.loadMore()
    },
    applyLike(id: number, liked: boolean, likeCount: number) {
      const v = this.items.find((v) => v.id === id)
      if (v) {
        v.isLiked = liked
        v.likeCount = likeCount
      }
    },
    applyFavorite(id: number, favorited: boolean, favoriteCount: number) {
      const v = this.items.find((v) => v.id === id)
      if (v) {
        v.isFavorited = favorited
        v.favoriteCount = favoriteCount
      }
    },
    applyCommentDelta(id: number, delta: number) {
      const v = this.items.find((v) => v.id === id)
      if (v) v.commentCount += delta
    }
  }
})
