import { createRouter, createWebHashHistory } from 'vue-router'

/**
 * 路由设计：
 * - Feed 页 keep-alive 缓存，从评论页/个人页返回时恢复播放位置
 * - 需要登录的页面通过 meta.auth 标记（本期交互均以全局登录弹窗拦截，不做路由级强制跳转）
 */
const router = createRouter({
  history: createWebHashHistory(),
  routes: [
    {
      path: '/',
      name: 'feed',
      component: () => import('@/views/FeedView.vue'),
      meta: { title: '推荐' }
    },
    {
      path: '/user',
      name: 'user',
      component: () => import('@/views/UserView.vue'),
      meta: { title: '我' }
    },
    {
      path: '/mall',
      name: 'mall',
      component: () => import('@/views/MallView.vue'),
      meta: { title: '商城' }
    },
    { path: '/:pathMatch(.*)*', redirect: '/' }
  ],
  scrollBehavior: (_to, _from, savedPosition) => savedPosition ?? { top: 0 }
})

router.afterEach((to) => {
  document.title = to.meta.title ? `${String(to.meta.title)} - 抖音网页版` : '抖音-网页版'
})

export default router
