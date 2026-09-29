<script setup lang="ts">
import { onBeforeUnmount, onMounted } from 'vue'
import AppToast from '@/components/common/AppToast.vue'
import LoginModal from '@/components/common/LoginModal.vue'
import CommentDrawer from '@/components/feed/CommentDrawer.vue'
import LeftNav from '@/components/layout/LeftNav.vue'
import TopBar from '@/components/layout/TopBar.vue'
import { useAppStore } from '@/stores/app'
import { useUserStore } from '@/stores/user'

const app = useAppStore()
const user = useUserStore()

/** token 刷新失败等场景：清登录态并统一拉起登录弹窗 */
function onLoginRequired() {
  user.logout()
  app.openLogin()
}

function onOffline() {
  app.toast('网络已断开，请检查网络连接', 'error')
}

function onOnline() {
  app.toast('网络已恢复', 'success')
}

onMounted(() => {
  // 启动时恢复本地登录态
  user.restore()
  window.addEventListener('app:login-required', onLoginRequired)
  window.addEventListener('offline', onOffline)
  window.addEventListener('online', onOnline)
})

onBeforeUnmount(() => {
  window.removeEventListener('app:login-required', onLoginRequired)
  window.removeEventListener('offline', onOffline)
  window.removeEventListener('online', onOnline)
})
</script>

<template>
  <div class="h-full w-full overflow-hidden bg-black text-white">
    <LeftNav />
    <TopBar />
    <main class="h-full w-full">
      <!-- Feed 页缓存：从个人页返回时恢复滑动位置与播放状态 -->
      <router-view v-slot="{ Component }">
        <keep-alive include="FeedView">
          <component :is="Component" />
        </keep-alive>
      </router-view>
    </main>
    <!-- 全局挂载的抽屉/弹窗/Toast -->
    <CommentDrawer />
    <LoginModal />
    <AppToast />
  </div>
</template>
