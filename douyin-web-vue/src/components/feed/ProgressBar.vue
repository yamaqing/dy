<script setup lang="ts">
import { computed, ref } from 'vue'
import { formatDuration } from '@/utils/format'

/**
 * 视频进度条：点击/拖拽 seek + 键盘方向键微调（role=slider 可访问性）
 * 通过 pointer capture 实现流畅拖拽，拖拽过程不冒泡到视频区避免误触暂停
 */
const props = defineProps<{
  current: number
  duration: number
}>()

const emit = defineEmits<{
  seek: [ratio: number]
}>()

const barRef = ref<HTMLElement>()
const dragging = ref(false)
/** 拖拽中的临时比例，避免 timeupdate 回写造成抖动 */
const dragRatio = ref(-1)

const ratio = computed(() => {
  if (dragRatio.value >= 0) return dragRatio.value
  if (!props.duration || !Number.isFinite(props.duration)) return 0
  return Math.min(props.current / props.duration, 1)
})

/** 气泡中展示的预览时间：拖拽时显示拖到的位置 */
const previewTime = computed(() => ratio.value * (props.duration || 0))

function ratioFromEvent(e: PointerEvent): number {
  const rect = barRef.value!.getBoundingClientRect()
  return Math.min(Math.max((e.clientX - rect.left) / rect.width, 0), 1)
}

function onPointerDown(e: PointerEvent) {
  dragging.value = true
  dragRatio.value = ratioFromEvent(e)
  ;(e.currentTarget as HTMLElement).setPointerCapture(e.pointerId)
}

function onPointerMove(e: PointerEvent) {
  if (!dragging.value) return
  dragRatio.value = ratioFromEvent(e)
}

function onPointerUp(e: PointerEvent) {
  if (!dragging.value) return
  dragging.value = false
  emit('seek', ratioFromEvent(e))
  dragRatio.value = -1
}

/** 键盘微调：←/→ 按 5% 步进 seek */
function onKeydown(e: KeyboardEvent) {
  if (!props.duration || !Number.isFinite(props.duration)) return
  const step = 0.05
  if (e.key === 'ArrowLeft') {
    e.preventDefault()
    e.stopPropagation()
    emit('seek', Math.max(ratio.value - step, 0))
  } else if (e.key === 'ArrowRight') {
    e.preventDefault()
    e.stopPropagation()
    emit('seek', Math.min(ratio.value + step, 1))
  }
}
</script>

<template>
  <!-- 扩大热区（h-4），视觉条本身只有 2px/4px；role=slider 支持屏幕阅读器与键盘 -->
  <div
    ref="barRef"
    class="group absolute inset-x-0 bottom-0 z-20 flex h-4 cursor-pointer items-end"
    role="slider"
    tabindex="0"
    aria-label="播放进度"
    :aria-valuemin="0"
    :aria-valuemax="100"
    :aria-valuenow="Math.round(ratio * 100)"
    :aria-valuetext="`${formatDuration(previewTime)} / ${formatDuration(duration)}`"
    @pointerdown="onPointerDown"
    @pointermove="onPointerMove"
    @pointerup="onPointerUp"
    @pointerup.stop
    @keydown="onKeydown"
  >
    <!-- 拖拽时的时间气泡 -->
    <div
      v-if="dragging"
      class="pointer-events-none absolute bottom-4 -translate-x-1/2 rounded bg-black/80 px-2 py-0.5 text-xs tabular-nums text-txt-1"
      :style="{ left: `${ratio * 100}%` }"
    >
      {{ formatDuration(previewTime) }} / {{ formatDuration(duration) }}
    </div>
    <div class="relative h-[2px] w-full bg-white/25 transition-[height] group-hover:h-[4px]">
      <div class="absolute inset-y-0 left-0 bg-primary" :style="{ width: `${ratio * 100}%` }" />
      <!-- 拖拽手柄：悬停或拖拽时显示 -->
      <div
        class="absolute top-1/2 h-3 w-3 -translate-x-1/2 -translate-y-1/2 rounded-full bg-primary opacity-0 transition-opacity group-hover:opacity-100"
        :class="{ 'opacity-100': dragging }"
        :style="{ left: `${ratio * 100}%` }"
      />
    </div>
  </div>
</template>
