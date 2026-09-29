import { ref } from 'vue'
import { defineStore } from 'pinia'
import { mallApi } from '@/api/modules/mall'
import type { ProductItem } from '@/api/types'

export const useMallStore = defineStore('mall', () => {
  // ==================== 状态 ====================
  const items = ref<ProductItem[]>([])
  const cursor = ref('0')
  const hasMore = ref(true)
  const loading = ref(false)
  const error = ref('')

  // ==================== 动作 ====================
  async function loadMore() {
    if (loading.value || !hasMore.value) return
    loading.value = true
    error.value = ''
    try {
      const page = await mallApi.listProducts(cursor.value)
      items.value.push(...page.list)
      cursor.value = page.nextCursor
      hasMore.value = page.hasMore
    } catch (e) {
      error.value = e instanceof Error ? e.message : '加载失败'
    } finally {
      loading.value = false
    }
  }

  function reset() {
    items.value = []
    cursor.value = '0'
    hasMore.value = true
    error.value = ''
  }

  return { items, cursor, hasMore, loading, error, loadMore, reset }
})
