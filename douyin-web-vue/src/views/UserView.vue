<script setup lang="ts">
import { useRouter } from 'vue-router'
import Icon from '@/components/common/Icon.vue'
import { useAppStore } from '@/stores/app'
import { useUserStore } from '@/stores/user'
import { formatCount } from '@/utils/format'

/** 个人主页（M1 简版：登录态展示 + 作品区空态占位） */
const user = useUserStore()
const app = useAppStore()
const router = useRouter()

// TODO 待联调确认：获赞/关注/粉丝统计由后端用户域接口提供
const stats = { like: 128000, follow: 66, fans: 1024 }

function logout() {
  user.logout()
  app.toast('已退出登录')
  router.push('/')
}
</script>

<template>
  <div class="h-full w-full overflow-y-auto bg-surface lg:pl-[72px]">
    <!-- 未登录空态 -->
    <div v-if="!user.isLogin" class="flex h-full flex-col items-center justify-center gap-5">
      <Icon name="user" :size="56" class="text-white/25" />
      <p class="text-sm text-white/50">登录后查看个人主页</p>
      <button
        class="rounded-full bg-primary px-10 py-2 text-sm font-medium transition-opacity hover:opacity-90"
        @click="app.openLogin()"
      >
        立即登录
      </button>
    </div>

    <!-- 已登录 -->
    <div v-else class="mx-auto max-w-xl px-6 pb-16 pt-20">
      <div class="flex items-center gap-5">
        <img
          :src="user.userInfo?.avatar"
          :alt="user.userInfo?.nickName"
          class="h-20 w-20 rounded-full object-cover ring-2 ring-white/20"
        />
        <div class="min-w-0">
          <h1 class="truncate text-xl font-bold">{{ user.userInfo?.nickName }}</h1>
          <p class="mt-1 text-xs text-white/40">抖音号：{{ user.userInfo?.userId }}</p>
        </div>
      </div>

      <!-- 数据统计 -->
      <div class="mt-6 flex gap-8 text-sm">
        <div class="flex items-baseline gap-1.5">
          <span class="text-base font-semibold">{{ formatCount(stats.like) }}</span>
          <span class="text-white/40">获赞</span>
        </div>
        <div class="flex items-baseline gap-1.5">
          <span class="text-base font-semibold">{{ formatCount(stats.follow) }}</span>
          <span class="text-white/40">关注</span>
        </div>
        <div class="flex items-baseline gap-1.5">
          <span class="text-base font-semibold">{{ formatCount(stats.fans) }}</span>
          <span class="text-white/40">粉丝</span>
        </div>
      </div>

      <!-- 作品区（上传能力二期接入） -->
      <div class="mt-8 border-b border-white/10 pb-2">
        <span class="border-b-2 border-white pb-2 text-sm font-medium">作品</span>
      </div>
      <div class="flex flex-col items-center justify-center gap-3 py-20 text-white/30">
        <Icon name="plus" :size="40" />
        <p class="text-sm">还没有作品，期待你的第一条视频</p>
      </div>

      <button
        class="mt-6 w-full rounded-lg bg-white/10 py-2.5 text-sm text-white/80 transition-colors hover:bg-white/15"
        @click="logout"
      >
        退出登录
      </button>
    </div>
  </div>
</template>
