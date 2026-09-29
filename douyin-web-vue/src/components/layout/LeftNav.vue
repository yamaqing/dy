<script setup lang="ts">
import { computed } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import Icon, { type IconName } from '@/components/common/Icon.vue'
import { useAppStore } from '@/stores/app'
import { useFeedStore } from '@/stores/feed'
import { useUserStore } from '@/stores/user'
import type { FeedType } from '@/api/types'

/** 桌面端左侧导航（≥lg 显示）：对齐抖音网页版结构 */
const route = useRoute()
const router = useRouter()
const feed = useFeedStore()
const app = useAppStore()
const user = useUserStore()

interface NavItem {
  key: string
  label: string
  icon: IconName
}

const navItems: NavItem[] = [
  { key: 'recommend', label: '推荐', icon: 'home' },
  { key: 'follow', label: '关注', icon: 'users' },
  { key: 'mall', label: '商城', icon: 'cart' },
  { key: 'live', label: '直播', icon: 'live' },
  { key: 'mine', label: '我的', icon: 'user' }
]

const activeKey = computed(() => {
  if (route.name === 'user') return 'mine'
  if (route.name === 'mall') return 'mall'
  if (route.name === 'feed') return feed.feedType
  return ''
})

function onNav(key: string) {
  switch (key) {
    case 'recommend':
    case 'follow':
      if (key === 'follow' && !user.isLogin) return app.openLogin()
      if (route.name !== 'feed') router.push('/')
      void feed.switchType(key as FeedType)
      break
    case 'mall':
      router.push('/mall')
      break
    case 'live':
      // TODO 待联调确认：直播间能力（im-server WS + flv.js）二期接入
      app.toast('直播功能即将上线')
      break
    case 'mine':
      if (!user.isLogin) return app.openLogin()
      router.push('/user')
      break
  }
}
</script>

<template>
  <nav
    class="fixed inset-y-0 left-0 z-40 hidden w-[72px] flex-col items-center border-r border-line bg-surface px-2 py-4 lg:flex"
    aria-label="主导航"
  >
    <!-- 品牌区 -->
    <router-link to="/" class="mb-6 flex flex-col items-center gap-1 text-white" aria-label="抖音网页版首页">
      <span class="text-lg font-bold italic leading-none tracking-wide">抖音</span>
      <span class="text-[10px] leading-none text-txt-3">网页版</span>
    </router-link>

    <!-- 导航项：图标在上文字在下的紧凑布局，active 态为白色/10 圆角块 -->
    <button
      v-for="item in navItems"
      :key="item.key"
      class="mb-1 flex w-full flex-col items-center gap-1.5 rounded-lg py-2.5 text-xs transition-colors"
      :class="
        activeKey === item.key
          ? 'bg-white/10 font-semibold text-txt-1'
          : 'text-txt-2 hover:bg-white/5 hover:text-txt-1'
      "
      :aria-current="activeKey === item.key ? 'page' : undefined"
      @click="onNav(item.key)"
    >
      <Icon :name="item.icon" :size="22" aria-hidden="true" />
      {{ item.label }}
    </button>
  </nav>
</template>
