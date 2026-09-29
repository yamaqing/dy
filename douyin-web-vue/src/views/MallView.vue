<script setup lang="ts">
import { computed, onBeforeUnmount, onMounted, ref } from 'vue'
import { useRouter } from 'vue-router'
import Icon from '@/components/common/Icon.vue'
import { useMallStore } from '@/stores/mall'
import { formatCount } from '@/utils/format'

defineOptions({ name: 'MallView' })

const mall = useMallStore()
const router = useRouter()
const scrollRef = ref<HTMLElement>()
const loadingMore = computed(() => mall.loading && mall.items.length > 0)

// ==================== 滚动触底加载 ====================

function onScroll() {
  const el = scrollRef.value
  if (!el || mall.loading || !mall.hasMore) return
  // 距底部 200px 时预加载下一页
  if (el.scrollTop + el.clientHeight >= el.scrollHeight - 200) void mall.loadMore()
}

onMounted(() => {
  if (!mall.items.length) void mall.loadMore()
  scrollRef.value?.addEventListener('scroll', onScroll, { passive: true })
})

onBeforeUnmount(() => {
  scrollRef.value?.removeEventListener('scroll', onScroll)
})

function goBack() {
  router.push('/')
}

/** 价格展示：分 -> 元，保留两位小数 */
function formatPrice(fen: number): string {
  return (fen / 100).toFixed(2)
}
</script>

<template>
  <div ref="scrollRef" class="h-full w-full overflow-y-auto bg-surface lg:pl-[72px]">
    <!-- 顶部标题栏 -->
    <div class="sticky top-0 z-20 flex h-14 items-center gap-3 border-b border-line bg-surface/95 px-4 backdrop-blur">
      <button class="flex h-8 w-8 items-center justify-center rounded-full text-txt-2 transition-colors hover:bg-white/10 hover:text-txt-1" aria-label="返回" @click="goBack">
        <Icon name="back" :size="20" />
      </button>
      <h1 class="text-lg font-semibold text-txt-1">商城</h1>
      <span class="text-xs text-txt-3">发现好物</span>
    </div>

    <!-- 商品网格：移动端 2 列，桌面端 3/4/5 列 -->
    <div class="grid grid-cols-2 gap-3 p-3 sm:grid-cols-3 md:grid-cols-4 lg:grid-cols-5">
      <div
        v-for="item in mall.items"
        :key="item.id"
        class="group overflow-hidden rounded-xl bg-surface-2 transition-shadow hover:ring-1 hover:ring-white/20"
      >
        <!-- 商品主图 -->
        <div class="relative aspect-square w-full overflow-hidden">
          <img
            :src="item.coverUrl"
            :alt="item.title"
            class="h-full w-full object-cover transition-transform duration-300 group-hover:scale-105"
            loading="lazy"
          />
          <!-- 标签 -->
          <span
            v-if="item.tag"
            class="absolute left-2 top-2 rounded bg-primary/90 px-1.5 py-0.5 text-[10px] font-medium text-white"
          >
            {{ item.tag }}
          </span>
        </div>

        <!-- 商品信息 -->
        <div class="flex flex-col gap-1.5 p-2.5">
          <p class="line-clamp-2 text-sm leading-5 text-txt-1">{{ item.title }}</p>
          <p class="text-xs text-txt-3">{{ item.shopName }}</p>
          <div class="mt-1 flex items-baseline gap-1.5">
            <span class="text-base font-bold text-primary">¥{{ formatPrice(item.price) }}</span>
            <span v-if="item.originPrice > item.price" class="text-xs text-txt-3 line-through">
              ¥{{ formatPrice(item.originPrice) }}
            </span>
          </div>
          <div class="flex items-center justify-between">
            <span class="text-xs text-txt-3">已售 {{ formatCount(item.sales) }}</span>
            <button class="rounded-full bg-primary/10 px-3 py-1 text-xs font-medium text-primary transition-colors hover:bg-primary/20">
              去购买
            </button>
          </div>
        </div>
      </div>
    </div>

    <!-- 加载中 -->
    <div v-if="loadingMore" class="flex items-center justify-center gap-2 py-6">
      <div class="h-5 w-5 animate-spin rounded-full border-2 border-white/30 border-t-white" />
      <span class="text-sm text-txt-3">加载中...</span>
    </div>

    <!-- 到底 -->
    <div v-if="!mall.hasMore && mall.items.length" class="py-6 text-center text-xs text-txt-3">没有更多商品了</div>

    <!-- 首屏加载骨架 -->
    <div v-if="mall.loading && !mall.items.length" class="grid grid-cols-2 gap-3 p-3 sm:grid-cols-3 md:grid-cols-4 lg:grid-cols-5">
      <div v-for="i in 10" :key="i" class="animate-pulse overflow-hidden rounded-xl bg-surface-2">
        <div class="aspect-square w-full bg-white/5" />
        <div class="flex flex-col gap-2 p-2.5">
          <div class="h-4 w-full rounded bg-white/5" />
          <div class="h-4 w-2/3 rounded bg-white/5" />
          <div class="h-5 w-1/3 rounded bg-white/5" />
        </div>
      </div>
    </div>

    <!-- 错误态 -->
    <div v-if="mall.error && !mall.items.length" class="flex flex-col items-center justify-center gap-4 py-20">
      <Icon name="retry" :size="40" class="text-white/40" />
      <p class="text-sm text-white/60">{{ mall.error }}</p>
      <button class="rounded-full bg-primary px-8 py-2 text-sm font-medium transition-opacity hover:opacity-90" @click="mall.loadMore()">
        点击重试
      </button>
    </div>
  </div>
</template>
