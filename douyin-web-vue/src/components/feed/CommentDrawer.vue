<script setup lang="ts">
import { computed, onBeforeUnmount, ref, watch } from 'vue'
import { commentApi } from '@/api/modules/comment'
import type { CommentItem } from '@/api/types'
import Icon from '@/components/common/Icon.vue'
import { useAppStore } from '@/stores/app'
import { useFeedStore } from '@/stores/feed'
import { useUserStore } from '@/stores/user'
import { formatCount, formatRelativeTime } from '@/utils/format'

/**
 * 评论抽屉：移动端底部弹出（72vh），桌面端右侧固定面板（400px）
 * 长列表优化：滚动触底自动加载（loading 标志天然节流）+ content-visibility 跳过屏外渲染
 */
const app = useAppStore()
const feed = useFeedStore()
const user = useUserStore()

const video = computed(() => app.commentVideo)
const list = ref<CommentItem[]>([])
const cursor = ref('0')
const hasMore = ref(true)
const loading = ref(false)
const loadError = ref(false)
const input = ref('')
const sending = ref(false)
const scrollRef = ref<HTMLElement>()

// 抽屉打开时重置并加载首屏；打开期间监听 Esc 关闭
watch(video, (v) => {
  if (v) {
    list.value = []
    cursor.value = '0'
    hasMore.value = true
    loadError.value = false
    void loadMore()
    window.addEventListener('keydown', onEscClose)
  } else {
    window.removeEventListener('keydown', onEscClose)
  }
})

function onEscClose(e: KeyboardEvent) {
  if (e.key === 'Escape') app.closeComments()
}

onBeforeUnmount(() => window.removeEventListener('keydown', onEscClose))

async function loadMore() {
  const v = video.value
  if (!v || loading.value || !hasMore.value) return
  loading.value = true
  loadError.value = false
  try {
    const page = await commentApi.list(v.id, cursor.value, 20)
    list.value.push(...page.list)
    cursor.value = page.nextCursor
    hasMore.value = page.hasMore
  } catch {
    loadError.value = true
  } finally {
    loading.value = false
  }
}

/** 滚动到底部附近自动加载下一页 */
function onScroll() {
  const el = scrollRef.value
  if (!el) return
  if (el.scrollTop + el.clientHeight >= el.scrollHeight - 120) void loadMore()
}

/** 发表评论：乐观更新（先上屏再请求，失败回滚） */
async function send() {
  const v = video.value
  const content = input.value.trim()
  if (!v || !content || sending.value) return
  if (!user.isLogin || !user.userInfo) return app.openLogin()
  sending.value = true
  const optimistic: CommentItem = {
    id: Date.now(),
    videoId: v.id,
    content,
    user: { userId: user.userInfo.userId, nickName: user.userInfo.nickName, avatar: user.userInfo.avatar },
    likeCount: 0,
    createTime: Date.now(),
    replyCount: 0
  }
  list.value.unshift(optimistic)
  input.value = ''
  scrollRef.value?.scrollTo({ top: 0, behavior: 'smooth' })
  try {
    await commentApi.add(v.id, content)
    feed.applyCommentDelta(v.id, 1)
  } catch {
    list.value = list.value.filter((c) => c.id !== optimistic.id)
    app.toast('评论失败，请重试', 'error')
  } finally {
    sending.value = false
  }
}
</script>

<template>
  <!-- 移动端背板 -->
  <Transition name="fade">
    <div v-if="video" class="fixed inset-0 z-40 bg-black/50 md:hidden" @click="app.closeComments()" />
  </Transition>

  <Transition name="drawer">
    <div
      v-if="video"
      role="dialog"
      aria-modal="true"
      aria-label="评论区"
      class="fixed inset-x-0 bottom-0 z-50 flex h-[72vh] flex-col rounded-t-2xl bg-surface md:inset-y-0 md:left-auto md:h-full md:w-[400px] md:rounded-none md:border-l md:border-white/10"
    >
      <!-- 头部 -->
      <div class="relative flex h-12 shrink-0 items-center justify-center border-b border-white/10">
        <span class="text-sm font-medium">全部评论（{{ formatCount(video.commentCount) }}）</span>
        <button
          class="absolute right-3 top-1/2 -translate-y-1/2 rounded-full p-1 text-white/70 transition-colors hover:text-white"
          aria-label="关闭评论"
          @click="app.closeComments()"
        >
          <Icon name="close" :size="20" />
        </button>
      </div>

      <!-- 评论列表 -->
      <div ref="scrollRef" class="flex-1 overscroll-contain overflow-y-auto px-4 py-3" @scroll.passive="onScroll">
        <!-- 空态 -->
        <div
          v-if="!list.length && !loading && !loadError"
          class="flex h-full flex-col items-center justify-center gap-3 text-white/40"
        >
          <Icon name="comment" :size="44" />
          <p class="text-sm">还没有评论，快来抢沙发</p>
        </div>

        <!--
          content-visibility:auto 让浏览器跳过屏外行的渲染，
          contain-intrinsic-size 提供预估高度防止滚动条跳动（轻量级虚拟化方案）
        -->
        <div
          v-for="c in list"
          :key="c.id"
          class="mb-4 flex gap-3 [contain-intrinsic-size:auto_72px] [content-visibility:auto]"
        >
          <img
            :src="c.user.avatar"
            :alt="c.user.nickName"
            width="36"
            height="36"
            class="h-9 w-9 shrink-0 rounded-full object-cover"
            loading="lazy"
          />
          <div class="min-w-0 flex-1">
            <p class="text-xs text-white/50">{{ c.user.nickName }}</p>
            <p class="mt-0.5 break-words text-sm leading-5 text-white/95">{{ c.content }}</p>
            <div class="mt-1.5 flex items-center gap-4 text-xs text-white/40">
              <span>{{ formatRelativeTime(c.createTime) }}</span>
              <span v-if="c.replyCount" class="cursor-pointer hover:text-white/60">{{ c.replyCount }} 条回复</span>
            </div>
          </div>
          <button
            class="flex shrink-0 flex-col items-center gap-0.5 pt-1 text-white/50 transition-colors hover:text-primary"
            aria-label="点赞评论"
          >
            <Icon name="heart" :size="16" />
            <span class="text-[10px] tabular-nums">{{ formatCount(c.likeCount) }}</span>
          </button>
        </div>

        <!-- 加载状态 / 错误重试 / 到底提示 -->
        <div v-if="loading" class="flex justify-center py-3">
          <div class="h-5 w-5 animate-spin rounded-full border-2 border-white/20 border-t-white" />
        </div>
        <button v-if="loadError" class="w-full py-3 text-center text-sm text-primary" @click="loadMore">
          加载失败，点击重试
        </button>
        <p v-if="!hasMore && list.length" class="py-3 text-center text-xs text-white/30">没有更多评论了</p>
      </div>

      <!-- 输入栏 -->
      <div class="flex shrink-0 items-center gap-2 border-t border-white/10 p-3">
        <img
          v-if="user.isLogin && user.userInfo"
          :src="user.userInfo.avatar"
          class="h-8 w-8 shrink-0 rounded-full object-cover"
          alt=""
        />
        <input
          v-model="input"
          type="text"
          :placeholder="user.isLogin ? '善语结善缘，恶语伤人心' : '登录后参与评论'"
          maxlength="200"
          class="h-9 min-w-0 flex-1 rounded-full bg-white/10 px-4 text-sm outline-none transition-colors placeholder:text-white/40 focus:bg-white/15"
          @focus="!user.isLogin && app.openLogin()"
          @keyup.enter="send"
        />
        <button
          class="flex h-9 w-9 shrink-0 items-center justify-center rounded-full transition-colors"
          :class="input.trim() ? 'bg-primary text-white' : 'bg-white/10 text-white/40'"
          :disabled="sending"
          aria-label="发送评论"
          @click="send"
        >
          <Icon name="send" :size="18" />
        </button>
      </div>
    </div>
  </Transition>
</template>

<style scoped>
.fade-enter-active,
.fade-leave-active {
  transition: opacity 0.25s ease;
}
.fade-enter-from,
.fade-leave-to {
  opacity: 0;
}
.drawer-enter-active,
.drawer-leave-active {
  transition: transform 0.3s cubic-bezier(0.25, 0.8, 0.3, 1);
}
.drawer-enter-from,
.drawer-leave-to {
  transform: translateY(100%);
}
@media (min-width: 768px) {
  .drawer-enter-from,
  .drawer-leave-to {
    transform: translateX(100%);
  }
}
</style>
