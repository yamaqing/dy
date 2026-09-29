<script setup lang="ts">
import { computed, onBeforeUnmount, onMounted, ref, type CSSProperties } from 'vue'
import FeedItem from '@/components/feed/FeedItem.vue'
import FeedSkeleton from '@/components/feed/FeedSkeleton.vue'
import Icon from '@/components/common/Icon.vue'
import { useAppStore } from '@/stores/app'
import { useFeedStore } from '@/stores/feed'

/**
 * Feed 流容器：固定 3 槽位滑窗（prev/active/next）
 * - 轨道常驻中位 -100%，滑动通过偏移百分比驱动，动画结束才提交索引并无过渡回正（视觉无缝）
 * - 槽位按索引 key，video 元素随槽位复用不销毁，仅切换 src（防解码器句柄耗尽与卡顿）
 * - 支持触摸滑动 / 鼠标滚轮 / 键盘方向键三种切换方式
 */
defineOptions({ name: 'FeedView' })

const feed = useFeedStore()
const app = useAppStore()

const frameRef = ref<HTMLElement>()
const itemRefs = ref<(InstanceType<typeof FeedItem> | null)[]>([])

/** 滑窗偏移百分比：0 表示停在中位 */
const offsetPct = ref(0)
const isAnimating = ref(false)
/** 本次位移动画方向；0 = 回弹/取消 */
const animDir = ref<0 | 1 | -1>(0)

const slots = computed(() => [
  feed.items[feed.activeIndex - 1] ?? null,
  feed.items[feed.activeIndex] ?? null,
  feed.items[feed.activeIndex + 1] ?? null
])

/** 滑窗静止（无拖动无动画）才允许当前视频起播 */
const settled = computed(() => !isAnimating.value && offsetPct.value === 0)

const trackStyle = computed(() => ({
  transform: `translateY(calc(-100% + ${offsetPct.value}%))`,
  transition: isAnimating.value ? 'transform 0.32s cubic-bezier(0.25, 0.8, 0.3, 1)' : 'none'
}))

const canPrev = computed(() => feed.activeIndex > 0)
const canNext = computed(() => feed.activeIndex < feed.items.length - 1 || feed.hasMore)

/** 当前视频（驱动全局模糊背景与画框宽高比） */
const activeItem = computed(() => feed.items[feed.activeIndex])

/** 画框宽高比变量：横版视频给横向画框、竖版给竖向画框（对齐抖音网页版，不再硬塞 9:16） */
const frameStyle = computed<CSSProperties>(() => {
  const item = activeItem.value
  const ratio = item?.width && item?.height ? item.width / item.height : 9 / 16
  return { '--ar': String(ratio) } as CSSProperties
})

function go(dir: 1 | -1) {
  if (isAnimating.value) return
  if (dir === 1 && !canNext.value) return springBack()
  if (dir === -1 && !canPrev.value) return springBack()
  isAnimating.value = true
  animDir.value = dir
  offsetPct.value = -100 * dir
}

/** 边界橡皮筋回弹 / 取消滑动：动画回中位 */
function springBack() {
  if (offsetPct.value === 0) return
  isAnimating.value = true
  animDir.value = 0
  offsetPct.value = 0
}

function onTransitionEnd(e: TransitionEvent) {
  // 只响应轨道自身的 transform 过渡结束（子元素过渡会冒泡，必须过滤）
  if (e.target !== e.currentTarget || e.propertyName !== 'transform' || !isAnimating.value) return
  if (animDir.value !== 0) feed.setActive(feed.activeIndex + animDir.value)
  // 无过渡回正轨道：此时中位槽位已是新当前视频，视觉无缝衔接
  isAnimating.value = false
  animDir.value = 0
  offsetPct.value = 0
}

// ==================== 滚轮切换（桌面端） ====================

let wheelLockAt = 0
function onWheel(e: WheelEvent) {
  const now = Date.now()
  if (isAnimating.value || now - wheelLockAt < 360 || Math.abs(e.deltaY) < 24) return
  wheelLockAt = now
  go(e.deltaY > 0 ? 1 : -1)
}

// ==================== 触摸滑动（移动端） ====================

let touchStartY = 0
let touchStartAt = 0

function onTouchStart(e: TouchEvent) {
  if (isAnimating.value) return
  touchStartY = e.touches[0].clientY
  touchStartAt = Date.now()
}

function onTouchMove(e: TouchEvent) {
  if (isAnimating.value || !frameRef.value) return
  const h = frameRef.value.clientHeight
  let pct = ((e.touches[0].clientY - touchStartY) / h) * 100
  // 边界橡皮筋阻尼：超出边界方向阻力加大
  if ((pct > 0 && !canPrev.value) || (pct < 0 && !canNext.value)) pct /= 3
  offsetPct.value = Math.max(-100, Math.min(100, pct))
}

function onTouchEnd(e: TouchEvent) {
  if (isAnimating.value || offsetPct.value === 0) return
  // 位移超阈值或快速甩动均触发翻页，否则回弹
  const dist = Math.abs(e.changedTouches[0].clientY - touchStartY)
  const velocity = dist / Math.max(Date.now() - touchStartAt, 1)
  if (Math.abs(offsetPct.value) > 18 || velocity > 0.4) go(offsetPct.value < 0 ? 1 : -1)
  else springBack()
}

// ==================== 键盘切换（桌面端预览便捷操作） ====================

function onKeydown(e: KeyboardEvent) {
  // 弹窗/抽屉打开时不响应，避免与输入框冲突
  if (app.loginVisible || app.commentVideo) return
  if (e.key === 'ArrowDown') {
    e.preventDefault()
    go(1)
  } else if (e.key === 'ArrowUp') {
    e.preventDefault()
    go(-1)
  } else if (e.key === ' ') {
    e.preventDefault()
    itemRefs.value[1]?.togglePlay()
  }
}

function setItemRef(el: unknown, i: number) {
  itemRefs.value[i] = (el as InstanceType<typeof FeedItem> | null) ?? null
}

onMounted(() => {
  // keep-alive 回退时不重复拉取，保留播放位置
  if (!feed.items.length) void feed.loadMore()
  window.addEventListener('keydown', onKeydown)
})

onBeforeUnmount(() => {
  window.removeEventListener('keydown', onKeydown)
})
</script>

<template>
  <div class="relative flex h-full w-full items-center justify-center overflow-hidden bg-ink lg:pl-[72px]">
    <!-- 全局模糊背景：单层渲染并跟随当前视频封面（替代每槽位各挂一张 blur 大图，降低 GPU 合成开销） -->
    <img
      v-if="activeItem"
      :src="activeItem.coverUrl"
      class="pointer-events-none absolute inset-0 h-full w-full scale-125 object-cover opacity-40 blur-3xl"
      alt=""
      aria-hidden="true"
      draggable="false"
    />
    <div class="pointer-events-none absolute inset-0 bg-black/50" aria-hidden="true" />

    <!-- 画框：移动端全屏；桌面端按当前视频宽高比自适应（.feed-frame 见 main.css） -->
    <div
      ref="frameRef"
      class="feed-frame relative z-10 h-full w-full touch-none overflow-hidden bg-black md:rounded-2xl md:shadow-2xl md:ring-1 md:ring-white/10"
      :style="frameStyle"
      @wheel.passive="onWheel"
      @touchstart.passive="onTouchStart"
      @touchmove.passive="onTouchMove"
      @touchend.passive="onTouchEnd"
    >
      <!-- 三槽位轨道 -->
      <div class="h-full w-full will-change-transform" :style="trackStyle" @transitionend="onTransitionEnd">
        <div v-for="(slotItem, i) in slots" :key="i" class="h-full w-full">
          <FeedItem
            v-if="slotItem"
            :ref="(el) => setItemRef(el, i)"
            :item="slotItem"
            :active="i === 1"
            :settled="settled"
            :preload="i === 0 ? 'metadata' : 'auto'"
            @open-comments="app.openComments(slotItem)"
          />
        </div>
      </div>

      <!-- 首屏骨架屏 -->
      <FeedSkeleton v-if="feed.loading && !feed.items.length" />

      <!-- 错误态：首屏加载失败可重试 -->
      <div
        v-if="feed.error && !feed.items.length"
        class="absolute inset-0 z-30 flex flex-col items-center justify-center gap-4 bg-ink"
      >
        <Icon name="retry" :size="40" class="text-white/40" />
        <p class="text-sm text-white/60">{{ feed.error }}</p>
        <button
          class="rounded-full bg-primary px-8 py-2 text-sm font-medium transition-opacity hover:opacity-90"
          @click="feed.loadMore()"
        >
          点击重试
        </button>
      </div>

      <!-- 空态：关注流无内容等 -->
      <div
        v-if="!feed.loading && !feed.error && !feed.items.length"
        class="absolute inset-0 z-30 flex flex-col items-center justify-center gap-4 bg-ink px-10 text-center"
      >
        <Icon name="user" :size="48" class="text-white/30" />
        <p class="text-sm leading-6 text-white/60">
          {{ feed.feedType === 'follow' ? '你关注的人还没有发布作品，去看看推荐吧' : '暂时没有内容了' }}
        </p>
        <button
          v-if="feed.feedType === 'follow'"
          class="rounded-full bg-primary px-8 py-2 text-sm font-medium transition-opacity hover:opacity-90"
          @click="feed.switchType('recommend')"
        >
          去看看推荐
        </button>
      </div>

      <!-- 加载更多指示（不阻断滑动） -->
      <div
        v-if="feed.loading && feed.items.length"
        class="pointer-events-none absolute bottom-6 left-1/2 z-30 -translate-x-1/2"
      >
        <div class="h-5 w-5 animate-spin rounded-full border-2 border-white/30 border-t-white" />
      </div>

      <!-- 到底提示 -->
      <div
        v-if="!feed.hasMore && feed.items.length && feed.activeIndex === feed.items.length - 1"
        class="pointer-events-none absolute bottom-16 left-1/2 z-30 -translate-x-1/2 rounded-full bg-black/60 px-5 py-2 text-sm text-white/80"
      >
        已经到底啦
      </div>
    </div>

    <!-- 桌面端上下切换按钮（对齐抖音网页版，画框右侧悬浮） -->
    <div class="ml-6 hidden flex-col gap-3 md:flex lg:ml-10">
      <button
        class="flex h-11 w-11 items-center justify-center rounded-full transition-colors"
        :class="canPrev ? 'bg-white/10 text-txt-1 hover:bg-white/20' : 'cursor-not-allowed bg-white/5 text-txt-3'"
        :disabled="!canPrev"
        aria-label="上一个视频"
        @click="go(-1)"
      >
        <Icon name="chevron-up" :size="22" />
      </button>
      <button
        class="flex h-11 w-11 items-center justify-center rounded-full transition-colors"
        :class="canNext ? 'bg-white/10 text-txt-1 hover:bg-white/20' : 'cursor-not-allowed bg-white/5 text-txt-3'"
        :disabled="!canNext"
        aria-label="下一个视频"
        @click="go(1)"
      >
        <Icon name="chevron-down" :size="22" />
      </button>
    </div>
  </div>
</template>
