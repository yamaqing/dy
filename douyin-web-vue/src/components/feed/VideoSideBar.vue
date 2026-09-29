<script setup lang="ts">
import Icon from '@/components/common/Icon.vue'
import { formatCount } from '@/utils/format'
import type { VideoItem } from '@/api/types'

/** 视频右侧操作栏（纯展示组件，交互逻辑由父组件 FeedItem 处理） */
defineProps<{ item: VideoItem }>()

const emit = defineEmits<{
  like: []
  comment: []
  favorite: []
  share: []
  follow: []
}>()
</script>

<template>
  <!-- 阻止冒泡，避免触发视频区的单击暂停/双击点赞 -->
  <div class="absolute bottom-24 right-2 z-20 flex flex-col items-center gap-5" @pointerup.stop>
    <!-- 作者头像 + 关注按钮 -->
    <div class="relative mb-1">
      <img
        :src="item.author.avatar"
        :alt="item.author.nickName"
        width="48"
        height="48"
        class="h-12 w-12 rounded-full border-2 border-white object-cover"
        loading="lazy"
      />
      <button
        class="absolute -bottom-2.5 left-1/2 flex h-5 w-5 -translate-x-1/2 items-center justify-center rounded-full bg-primary text-white transition-transform active:scale-90"
        aria-label="关注"
        @click="emit('follow')"
      >
        <Icon name="plus" :size="12" />
      </button>
    </div>

    <!-- 点赞 -->
    <button
      class="flex flex-col items-center gap-1 transition-transform duration-150 hover:scale-110 active:scale-95"
      aria-label="点赞"
      :aria-pressed="item.isLiked"
      @click="emit('like')"
    >
      <Icon
        name="heart"
        :size="34"
        class="drop-shadow-lg transition-colors duration-200"
        :class="item.isLiked ? 'animate-like-bounce text-primary' : 'text-white'"
      />
      <span class="text-xs tabular-nums text-white/90">{{ formatCount(item.likeCount) }}</span>
    </button>

    <!-- 评论 -->
    <button
      class="flex flex-col items-center gap-1 transition-transform duration-150 hover:scale-110 active:scale-95"
      aria-label="查看评论"
      @click="emit('comment')"
    >
      <Icon name="comment" :size="32" class="text-white drop-shadow-lg" />
      <span class="text-xs tabular-nums text-white/90">{{ formatCount(item.commentCount) }}</span>
    </button>

    <!-- 收藏 -->
    <button
      class="flex flex-col items-center gap-1 transition-transform duration-150 hover:scale-110 active:scale-95"
      aria-label="收藏"
      :aria-pressed="item.isFavorited"
      @click="emit('favorite')"
    >
      <Icon
        name="star"
        :size="32"
        class="drop-shadow-lg transition-colors duration-200"
        :class="item.isFavorited ? 'animate-like-bounce text-yellow-400' : 'text-white'"
      />
      <span class="text-xs tabular-nums text-white/90">{{ formatCount(item.favoriteCount) }}</span>
    </button>

    <!-- 分享 -->
    <button
      class="flex flex-col items-center gap-1 transition-transform duration-150 hover:scale-110 active:scale-95"
      aria-label="分享"
      @click="emit('share')"
    >
      <Icon name="share" :size="32" class="text-white drop-shadow-lg" />
      <span class="text-xs tabular-nums text-white/90">{{ formatCount(item.shareCount) }}</span>
    </button>

    <!-- 旋转音乐唱片 -->
    <div class="mt-1 flex h-11 w-11 items-center justify-center rounded-full border-4 border-white/20 bg-black/40">
      <Icon name="music" :size="18" class="animate-[spin_4s_linear_infinite] text-white/90" />
    </div>
  </div>
</template>
