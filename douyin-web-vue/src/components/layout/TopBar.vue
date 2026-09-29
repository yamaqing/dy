<script setup lang="ts">
import { computed, ref } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import Icon from '@/components/common/Icon.vue'
import { useAppStore } from '@/stores/app'
import { useFeedStore } from '@/stores/feed'
import { useUserStore } from '@/stores/user'
import type { FeedType } from '@/api/types'

/**
 * 顶部导航：
 * - 移动端（<lg）：logo + 推荐/关注 Tab + 搜索图标 + 用户入口
 * - 桌面端（≥lg）：logo 与导航移入 LeftNav，此处居中搜索框 + 用户入口
 */
const feed = useFeedStore()
const app = useAppStore()
const user = useUserStore()
const router = useRouter()
const route = useRoute()

const isFeedPage = computed(() => route.name === 'feed')

const tabs: { key: FeedType; label: string }[] = [
  { key: 'recommend', label: '推荐' },
  { key: 'follow', label: '关注' }
]

function switchTab(t: FeedType) {
  if (t === 'follow' && !user.isLogin) {
    app.openLogin()
    return
  }
  void feed.switchType(t)
}

function goUser() {
  if (!user.isLogin) return app.openLogin()
  router.push('/user')
}

// ---------- 搜索（UI 先行，接口二期接入） ----------
const keyword = ref('')

function onSearch() {
  // TODO 待联调确认：搜索接口（mall-search ES 适配完成后接入）
  if (keyword.value.trim()) {
    app.toast(`搜索「${keyword.value.trim()}」：功能即将上线`)
  } else {
    app.toast('搜索功能即将上线')
  }
}
</script>

<template>
  <header
    class="pt-safe absolute inset-x-0 top-0 z-30 flex items-center justify-between bg-gradient-to-b from-black/70 to-transparent px-4 pb-6 pt-3 lg:left-[72px]"
  >
    <!-- 品牌区（仅移动端，桌面端在 LeftNav） -->
    <router-link to="/" class="flex items-baseline gap-1 text-white lg:hidden" aria-label="抖音网页版首页">
      <span class="text-xl font-bold italic tracking-wide">抖音</span>
      <span class="text-xs text-txt-3">网页版</span>
    </router-link>

    <!-- 非 Feed 页返回按钮（移动端） -->
    <button
      v-if="!isFeedPage"
      class="absolute left-24 top-1/2 flex -translate-y-1/2 items-center rounded-full p-1.5 text-txt-2 transition-colors hover:text-txt-1 lg:hidden"
      aria-label="返回首页"
      @click="router.push('/')"
    >
      <Icon name="back" :size="22" />
    </button>

    <!-- Feed Tab（仅移动端 Feed 页；桌面端由 LeftNav 承担） -->
    <nav v-if="isFeedPage" class="absolute left-1/2 flex -translate-x-1/2 items-center gap-8 lg:hidden" aria-label="Feed 切换">
      <button
        v-for="t in tabs"
        :key="t.key"
        class="relative py-1 text-base transition-colors"
        :class="feed.feedType === t.key ? 'font-semibold text-txt-1' : 'text-txt-2 hover:text-txt-1'"
        :aria-current="feed.feedType === t.key ? 'page' : undefined"
        @click="switchTab(t.key)"
      >
        {{ t.label }}
        <span
          v-if="feed.feedType === t.key"
          class="absolute -bottom-0.5 left-1/2 h-[3px] w-6 -translate-x-1/2 rounded-full bg-white"
        />
      </button>
    </nav>

    <!-- 搜索框（桌面端居中组合，对齐抖音网页版：白底输入区 + 灰底搜索按钮） -->
    <div class="hidden flex-1 justify-center lg:flex">
      <div class="flex h-10 w-[380px] overflow-hidden rounded-md bg-white/5 ring-1 ring-white/10 focus-within:ring-white/20">
        <input
          v-model.trim="keyword"
          type="text"
          name="keyword"
          aria-label="搜索视频"
          placeholder="搜索你感兴趣的内容"
          autocomplete="off"
          class="h-full flex-1 bg-transparent px-3 text-sm text-txt-1 outline-none placeholder:text-txt-3"
          @keyup.enter="onSearch"
        />
        <button
          class="flex h-full w-14 items-center justify-center rounded-r-md bg-white/10 text-txt-2 transition-colors hover:bg-white/15 hover:text-txt-1"
          aria-label="搜索"
          @click="onSearch"
        >
          <Icon name="search" :size="18" />
        </button>
      </div>
    </div>

    <!-- 右侧操作区 -->
    <div class="flex items-center gap-3">
      <button
        class="rounded-full p-2 text-txt-2 transition-colors hover:bg-white/10 hover:text-txt-1 lg:hidden"
        aria-label="搜索"
        @click="onSearch"
      >
        <Icon name="search" :size="22" />
      </button>
      <button
        v-if="user.isLogin && user.userInfo"
        class="block h-8 w-8 overflow-hidden rounded-full ring-1 ring-white/40 transition-transform hover:scale-105 active:scale-95"
        aria-label="个人主页"
        @click="goUser"
      >
        <img :src="user.userInfo.avatar" :alt="user.userInfo.nickName" width="32" height="32" class="h-full w-full object-cover" />
      </button>
      <button
        v-else
        class="rounded-full bg-primary px-5 py-1.5 text-sm font-medium text-white transition-opacity hover:opacity-90"
        @click="app.openLogin()"
      >
        登录
      </button>
    </div>
  </header>
</template>
