<script setup lang="ts">
import { nextTick, onBeforeUnmount, ref, watch, watchEffect } from 'vue'
import { feedApi } from '@/api/modules/feed'
import type { VideoItem } from '@/api/types'
import Icon from '@/components/common/Icon.vue'
import { useAppStore } from '@/stores/app'
import { useFeedStore } from '@/stores/feed'
import { useUserStore } from '@/stores/user'
import { MAX_SOURCE_RETRY, pickFallbackSource } from '@/utils/video-fallback'
import ProgressBar from './ProgressBar.vue'
import VideoSideBar from './VideoSideBar.vue'

/**
 * 单个视频槽位（滑窗复用核心）：
 * 组件实例随槽位复用不销毁，切换视频仅替换 item props -> video.src 变更，
 * 避免频繁创建/销毁 video 元素导致的卡顿与解码器句柄耗尽
 */
const props = defineProps<{
  item: VideoItem
  /** 是否处于滑窗中位（当前可见槽位） */
  active: boolean
  /** 滑窗是否静止：拖动/位移动画中为 false，静止后才允许起播 */
  settled: boolean
  /** 预加载策略：当前/下一条 auto，上一条 metadata */
  preload: 'auto' | 'metadata'
}>()

const emit = defineEmits<{ openComments: [] }>()

const feed = useFeedStore()
const app = useAppStore()
const user = useUserStore()

const rootRef = ref<HTMLElement>()
const videoRef = ref<HTMLVideoElement>()
const paused = ref(true)
const buffering = ref(false)
const currentTime = ref(0)
const duration = ref(0)

// ==================== 播放源容错（演示环境多 CDN 兜底） ====================

/** 当前实际播放地址：默认 item.playUrl，源不可达时切换备用源 */
const currentSrc = ref(props.item.playUrl)
/** 全部备用源耗尽后置位，展示"点击重试"覆盖层 */
const playFailed = ref(false)
/** 本次播放已失败的源（网络错误或长时间缓冲均计入） */
let failedSrcs: string[] = []
/** 缓冲看门狗：waiting 超过阈值视为源不可达，自动换源 */
let stallTimer: number | undefined

function switchToFallback() {
  if (!failedSrcs.includes(currentSrc.value)) failedSrcs.push(currentSrc.value)
  const next = pickFallbackSource(failedSrcs)
  if (next && failedSrcs.length <= MAX_SOURCE_RETRY) {
    currentSrc.value = next
  } else {
    playFailed.value = true
    buffering.value = false
    window.clearTimeout(stallTimer)
  }
}

/** 视频元素 error 事件：源 404/网络中断/解码失败 */
function onVideoError() {
  if (props.active) switchToFallback()
}

/** 重试：清空失败记录，从首源重新开始 */
function retryPlay() {
  failedSrcs = []
  playFailed.value = false
  currentSrc.value = props.item.playUrl
}

function onWaiting() {
  buffering.value = true
  window.clearTimeout(stallTimer)
  // 弱网/不可达源兜底：缓冲超过 4s 未恢复播放，自动切换备用源（防无限转圈）
  stallTimer = window.setTimeout(() => {
    if (buffering.value && props.active) switchToFallback()
  }, 4000)
}

function onPlaying() {
  buffering.value = false
  playFailed.value = false
  window.clearTimeout(stallTimer)
}

// ==================== 播放调度 ====================

// 可见且滑窗静止时起播；离开中位或开始滑动立即暂停
watchEffect(async () => {
  const v = videoRef.value
  if (!v) return
  if (props.active && props.settled) {
    await nextTick()
    // 浏览器自动播放策略：muted 状态下允许 autoplay，首次用户手势后解锁声音
    v.play().catch(() => {})
  } else {
    v.pause()
  }
})

// 槽位复用切换到新视频：重置播放状态（src 变更会中断旧请求并触发新资源加载）
watch(
  () => props.item.id,
  () => {
    const v = videoRef.value
    if (!v) return
    v.pause()
    // 同步重置兜底链：新视频从首源重新开始
    failedSrcs = []
    playFailed.value = false
    currentSrc.value = props.item.playUrl
    window.clearTimeout(stallTimer)
    currentTime.value = 0
    paused.value = true
    buffering.value = false
  },
  { flush: 'sync' }
)

function togglePlay() {
  const v = videoRef.value
  if (!v || !props.active) return
  v.paused ? v.play().catch(() => {}) : v.pause()
}

function onSeek(ratio: number) {
  const v = videoRef.value
  if (v && Number.isFinite(v.duration)) v.currentTime = ratio * v.duration
}

function onTime() {
  currentTime.value = videoRef.value?.currentTime ?? 0
}

function onMeta() {
  duration.value = videoRef.value?.duration ?? 0
}

// ==================== 单击暂停 / 双击点赞手势 ====================

let lastTapAt = 0
let tapTimer: number | undefined
let downX = 0
let downY = 0

function onPointerDown(e: PointerEvent) {
  downX = e.clientX
  downY = e.clientY
}

function onTap(e: PointerEvent) {
  if (!props.active) return
  // 滑动超过 10px 视为滑屏手势，不触发点按逻辑（兼容移动端 touch 合成的 pointerup）
  if (Math.hypot(e.clientX - downX, e.clientY - downY) > 10) return
  const now = Date.now()
  if (now - lastTapAt < 260) {
    window.clearTimeout(tapTimer)
    lastTapAt = 0
    onDoubleTap(e)
  } else {
    lastTapAt = now
    tapTimer = window.setTimeout(() => togglePlay(), 260)
  }
}

interface Heart {
  id: number
  x: number
  y: number
}
const hearts = ref<Heart[]>([])
let heartSeq = 0

function onDoubleTap(e: PointerEvent) {
  // 双击：爱心爆发动画（无论是否已赞都展示）
  const rect = rootRef.value!.getBoundingClientRect()
  const h: Heart = { id: ++heartSeq, x: e.clientX - rect.left, y: e.clientY - rect.top }
  hearts.value.push(h)
  setTimeout(() => {
    hearts.value = hearts.value.filter((x) => x.id !== h.id)
  }, 900)
  if (!props.item.isLiked) void doLike(true)
}

// ==================== 点赞 / 收藏 / 分享 / 关注 ====================

let likePending = false

async function doLike(fromDoubleTap = false) {
  if (!user.isLogin) {
    // 双击场景弹 Toast 轻提示，点按钮场景直接拉起登录弹窗
    if (fromDoubleTap) app.toast('登录后才能点赞哦')
    else app.openLogin()
    return
  }
  // 请求合并：防快速连点产生并发写
  if (likePending) return
  const target = !props.item.isLiked
  const snapshot = { liked: props.item.isLiked, count: props.item.likeCount }
  // 乐观更新：先上屏，失败回滚
  feed.applyLike(props.item.id, target, snapshot.count + (target ? 1 : -1))
  likePending = true
  try {
    const res = await feedApi.toggleLike(props.item.id, target)
    feed.applyLike(props.item.id, res.isLiked, res.likeCount)
  } catch (e) {
    feed.applyLike(props.item.id, snapshot.liked, snapshot.count)
    app.toast(e instanceof Error ? e.message : '操作失败，请重试', 'error')
  } finally {
    likePending = false
  }
}

let favPending = false

async function doFavorite() {
  if (!user.isLogin) return app.openLogin()
  if (favPending) return
  const target = !props.item.isFavorited
  const snapshot = { favorited: props.item.isFavorited, count: props.item.favoriteCount }
  feed.applyFavorite(props.item.id, target, snapshot.count + (target ? 1 : -1))
  favPending = true
  try {
    const res = await feedApi.toggleFavorite(props.item.id, target)
    feed.applyFavorite(props.item.id, res.isFavorited, res.favoriteCount)
    if (res.isFavorited) app.toast('已收藏', 'success')
  } catch (e) {
    feed.applyFavorite(props.item.id, snapshot.favorited, snapshot.count)
    app.toast(e instanceof Error ? e.message : '操作失败，请重试', 'error')
  } finally {
    favPending = false
  }
}

async function doShare() {
  const url = `${location.origin}${location.pathname}#/video/${props.item.id}`
  try {
    await navigator.clipboard.writeText(url)
  } catch {
    // 非安全上下文（如 IP 访问）剪贴板 API 不可用的降级方案
    const ta = document.createElement('textarea')
    ta.value = url
    document.body.appendChild(ta)
    ta.select()
    document.execCommand('copy')
    document.body.removeChild(ta)
  }
  app.toast('链接已复制，快去分享吧', 'success')
}

function doFollow() {
  if (!user.isLogin) return app.openLogin()
  // TODO 待联调确认：关注接口（live-user 域）
  app.toast('已关注', 'success')
}

onBeforeUnmount(() => {
  // 清理悬挂定时器：点按延迟器 / 缓冲看门狗
  window.clearTimeout(tapTimer)
  window.clearTimeout(stallTimer)
})

defineExpose({ togglePlay })
</script>

<template>
  <div
    ref="rootRef"
    class="relative h-full w-full select-none overflow-hidden"
    @pointerdown="onPointerDown"
    @pointerup="onTap"
  >
    <!-- 槽位背景透明：黑边/留白区域透出 FeedView 全局模糊背景（单层渲染，省去每槽位一张 blur 大图的合成开销） -->

    <video
      ref="videoRef"
      class="absolute inset-0 h-full w-full object-contain"
      :src="currentSrc"
      :poster="item.coverUrl"
      :muted="feed.muted"
      :preload="preload"
      loop
      playsinline
      webkit-playsinline
      @timeupdate="onTime"
      @loadedmetadata="onMeta"
      @play="paused = false"
      @pause="paused = true"
      @waiting="onWaiting"
      @playing="onPlaying"
      @error="onVideoError"
    />

    <!-- 全部备用源耗尽：失败重试覆盖层 -->
    <div
      v-if="playFailed"
      class="absolute inset-0 z-20 flex flex-col items-center justify-center gap-3 bg-black/70"
      @pointerup.stop
    >
      <p class="text-sm text-white/70">视频加载失败，请检查网络</p>
      <button
        class="rounded-full bg-white/10 px-6 py-1.5 text-sm text-white transition-colors hover:bg-white/20"
        @click="retryPlay"
      >
        点击重试
      </button>
    </div>

    <!-- 双击点赞爱心爆发层 -->
    <span
      v-for="h in hearts"
      :key="h.id"
      class="animate-heart-pop pointer-events-none absolute z-30 text-primary"
      :style="{ left: `${h.x}px`, top: `${h.y}px` }"
    >
      <Icon name="heart" :size="90" />
    </span>

    <!-- 暂停状态指示 -->
    <div
      v-if="active && paused && !buffering"
      class="pointer-events-none absolute inset-0 z-10 flex items-center justify-center"
    >
      <Icon name="play" :size="72" class="text-white/70 drop-shadow-2xl" />
    </div>

    <!-- 缓冲指示 -->
    <div v-if="active && buffering" class="pointer-events-none absolute inset-0 z-10 flex items-center justify-center">
      <div class="h-10 w-10 animate-spin rounded-full border-[3px] border-white/20 border-t-white" />
    </div>

    <!-- 取消静音引导（浏览器自动播放策略要求首视频静音起播） -->
    <button
      v-if="active && feed.muted"
      class="absolute bottom-44 left-1/2 z-20 flex -translate-x-1/2 items-center gap-2 rounded-full bg-black/50 px-4 py-1.5 text-sm text-white backdrop-blur transition-colors hover:bg-black/70"
      @pointerup.stop="feed.muted = false"
    >
      <Icon name="volume-off" :size="16" />
      点击取消静音
    </button>

    <!-- 底部信息区：不拦截点按，让手势穿透到视频区 -->
    <div class="pointer-events-none absolute inset-x-0 bottom-5 z-20 px-3 pr-20">
      <p class="mb-1.5 text-base font-semibold drop-shadow">@{{ item.author.nickName }}</p>
      <p class="mb-2 line-clamp-2 text-sm leading-5 text-white/95 drop-shadow">{{ item.title }}</p>
      <div class="flex items-center gap-1.5 text-xs text-white/80">
        <Icon name="music" :size="14" class="shrink-0" />
        <div class="w-36 overflow-hidden">
          <div class="animate-marquee inline-block whitespace-nowrap">{{ item.musicName }}&nbsp;·&nbsp;{{ item.musicName }}</div>
        </div>
      </div>
    </div>

    <!-- 右侧操作栏 -->
    <VideoSideBar
      :item="item"
      @like="doLike()"
      @comment="emit('openComments')"
      @favorite="doFavorite"
      @share="doShare"
      @follow="doFollow"
    />

    <!-- 进度条仅当前槽位渲染，避免隐藏槽位响应事件 -->
    <ProgressBar v-if="active" :current="currentTime" :duration="duration" @seek="onSeek" />
  </div>
</template>
