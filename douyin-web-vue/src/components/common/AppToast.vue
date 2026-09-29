<script setup lang="ts">
import { useAppStore } from '@/stores/app'

const app = useAppStore()

const typeClass: Record<string, string> = {
  info: 'bg-white/10 text-txt-1',
  success: 'bg-primary/90 text-white',
  error: 'bg-red-500/90 text-white'
}
</script>

<template>
  <!-- 全局 Toast：顶部居中堆叠，自动消失；aria-live 向屏幕阅读器播报异步状态 -->
  <div
    class="pointer-events-none fixed left-1/2 top-14 z-[100] flex -translate-x-1/2 flex-col items-center gap-2"
    role="status"
    aria-live="polite"
  >
    <TransitionGroup name="toast">
      <div
        v-for="t in app.toasts"
        :key="t.id"
        class="rounded-full px-5 py-2 text-sm backdrop-blur-md"
        :class="typeClass[t.type]"
      >
        {{ t.text }}
      </div>
    </TransitionGroup>
  </div>
</template>

<style scoped>
.toast-enter-active,
.toast-leave-active {
  transition:
    opacity 0.25s ease,
    transform 0.25s ease;
}
.toast-enter-from,
.toast-leave-to {
  opacity: 0;
  transform: translateY(-8px);
}
</style>
