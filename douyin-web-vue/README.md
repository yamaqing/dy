# 抖音网页版（douyin-web）

网页端抖音 C 端主站：短视频 Feed + 直播电商聚合前端，对接 `tiktok-live-mall` Spring Boot 3 + Spring Cloud Alibaba 微服务后端。

## 技术栈

| 分类 | 选型 | 版本 | 说明 |
|------|------|------|------|
| 框架 | Vue | 3.5 | Composition API + `<script setup>` |
| 类型系统 | TypeScript | 5.6 | strict 模式 |
| 构建工具 | Vite | 5.4 | dev server 端口 5173 |
| 状态管理 | Pinia | 2.3 | 按业务域拆分 store |
| 路由 | Vue Router | 4.5 | hash 模式，keep-alive 缓存 Feed 页 |
| 样式 | Tailwind CSS | 3.4 | 自定义设计 token（surface/txt/primary） |
| HTTP 客户端 | Axios | 1.7 | 二次封装：拦截器 + JWT 双 token 静默刷新 |
| 视频播放 | 原生 `<video>` | - | 元素池复用（3 槽位滑窗） |

## 项目结构

```
src/
├── api/                    # 接口层
│   ├── types.ts            #   TS 类型定义（与后端 DTO 对齐）
│   ├── request.ts          #   Axios 二次封装（token 注入/401 刷新/统一响应解包）
│   ├── mock.ts             #   Mock 适配器（实现 axios Adapter，mock 模式全链路拦截）
│   └── modules/            #   按业务域拆分的 API 模块
│       ├── user.ts         #     用户（登录/短信/刷新 token）
│       ├── feed.ts         #     Feed 流（推荐列表/点赞/收藏）
│       ├── comment.ts      #     评论（列表/发表）
│       └── mall.ts         #     商城（商品列表）
├── components/
│   ├── common/             # 通用组件
│   │   ├── Icon.vue        #   统一 SVG 图标（Material 风格填充图标）
│   │   ├── AppToast.vue    #   全局轻提示（aria-live 无障碍）
│   │   └── LoginModal.vue  #   短信登录弹窗（验证码倒计时/协议勾选）
│   ├── feed/               # Feed 流组件
│   │   ├── FeedItem.vue    #   单个视频槽位（播放调度/手势/点赞/评论入口）
│   │   ├── VideoSideBar.vue#   右侧操作栏（头像/点赞/评论/收藏/分享）
│   │   ├── ProgressBar.vue #   进度条（拖拽 seek/键盘左右 ±5%）
│   │   ├── CommentDrawer.vue#  评论抽屉（无限滚动/乐观上屏/失败回滚）
│   │   └── FeedSkeleton.vue#   首屏骨架屏
│   └── layout/             # 布局组件
│       ├── TopBar.vue      #   顶部导航（移动端 Tab + 桌面端搜索框）
│       └── LeftNav.vue     #   桌面端左侧 72px 紧凑导航（推荐/关注/商城/直播/我的）
├── composables/
│   └── useCountdown.ts     # 验证码倒计时 composable
├── router/
│   └── index.ts            # 路由（/ -> Feed, /user -> 个人页, /mall -> 商城）
├── stores/                 # Pinia 状态
│   ├── app.ts              #   全局（toast/登录弹窗/评论目标视频）
│   ├── feed.ts             #   Feed 流（items/cursor/activeIndex/muted/feedType）
│   ├── user.ts             #   用户（登录态/短信发送/登录/登出）
│   └── mall.ts             #   商城（商品列表/游标分页）
├── styles/
│   └── main.css            # 全局样式（设计 token/安全区/滚动条/动画）
├── utils/
│   ├── token.ts            # Token 存储（accessToken → sessionStorage, refreshToken → localStorage）
│   ├── format.ts           # 格式化（计数 w/万、相对时间、时长）
│   └── video-fallback.ts   # 视频源池与失败兜底策略
├── views/
│   ├── FeedView.vue        # Feed 流容器（3 槽位滑窗 + 滚轮/触摸/键盘切换）
│   ├── MallView.vue        # 商城页（商品网格 + 触底加载）
│   └── UserView.vue        # 个人页
├── App.vue                 # 根组件（LeftNav + TopBar + router-view + 全局弹窗）
└── main.ts                 # 入口
```

## 核心设计

### 视频播放：3 槽位滑窗复用

```
slots = [prev, active, next]   // 固定 3 个槽位
轨道常驻中位 -100%，滑动通过 translateY 偏移驱动
动画结束才提交索引，无过渡回正 → 视觉无缝
video 元素随槽位复用不销毁，仅替换 src（防解码器句柄耗尽）
```

切换方式：触摸滑动（位移 >18% 或速度 >0.4 翻页）、鼠标滚轮（360ms 锁）、键盘方向键。

### 画框宽高比自适应

横版视频给横向画框，竖版视频给竖向画框，避免黑边：

```css
/* main.css */
.feed-frame {
  width: min(100%, calc((100dvh - 112px) * var(--ar)));
  aspect-ratio: var(--ar);
}
```

`--ar` 由 `FeedView` 按当前视频 `item.width / item.height` 注入。

### 视频源容错（演示环境）

- 本地源：`public/demo-videos/` 下 3 个 MP4，秒开零依赖
- 兜底链：5 个外部 CDN 按可达性排序
- 缓冲看门狗：`waiting` 超 4s 自动换源，最多换 4 次后展示重试按钮

### 手势系统

- 单击：260ms 延迟后触发暂停/播放（与双击区分）
- 双击：爱心爆发动画 + 点赞（无论是否已赞都展示动画）
- 滑动 >10px：视为滑屏手势，不触发点按（兼容移动端 touch 合成 pointerup）

### 评论系统

- 触底 120px 自动加载下一页
- `content-visibility: auto` 轻量虚拟化（替代 JS 虚拟滚动）
- 发评论乐观上屏，失败回滚
- 单视频评论上限 180 条（mock）

### 登录与鉴权

- 短信验证码登录（mock：任意 4 位验证码可登录）
- JWT 双 token：accessToken（sessionStorage，2h）+ refreshToken（localStorage）
- 401 → 单例锁静默刷新 → 重放原请求
- 后端当前为 Cookie `tltk`，统一 JWT 后无缝切换

## 环境变量

| 变量 | 开发环境 | 生产环境 | 说明 |
|------|---------|---------|------|
| `VITE_USE_MOCK` | `true` | `false` | mock 模式开关 |
| `VITE_GATEWAY_TARGET` | `http://localhost:80` | - | live-gateway 地址 |

## 命令

```bash
npm run dev          # 启动开发服务器（端口 5173）
npm run build        # 生产构建
npm run type-check   # TypeScript 类型检查
npm run preview      # 预览生产构建
```

## 接口清单

| 接口路径 | 方法 | 说明 | 状态 |
|---------|------|------|------|
| `POST /user/sendSMS` | POST | 发送短信验证码 | mock |
| `POST /user/mobileLogin` | POST | 手机验证码登录 | mock |
| `POST /user/queryUser` | POST | 查询用户信息 | mock |
| `POST /user/refreshToken` | POST | 刷新 accessToken | mock |
| `GET /feed/recommend` | GET | 推荐 Feed 列表（游标分页） | mock |
| `POST /video/like` | POST | 点赞/取消点赞 | mock |
| `POST /video/favorite` | POST | 收藏/取消收藏 | mock |
| `GET /video/comments` | GET | 评论列表（游标分页） | mock |
| `POST /video/comment` | POST | 发表评论 | mock |
| `GET /mall/product/list` | GET | 商品列表（游标分页） | mock |

> mock 模式下所有请求完整经过 axios 拦截器链路，切换真实后端只需将 `VITE_USE_MOCK` 置 `false`。

## 设计规范

- 品牌色：primary `#FE2C55`（抖音红），secondary `#25F4EE`（抖音青）
- 背景：`#161823`（surface），纯黑 `#000`（视频区）
- 文字：白 100%（标题）/ 60%（正文）/ 35%（辅助）
- 圆角：卡片 `rounded-xl`，按钮 `rounded-full`，画框 `rounded-2xl`
- 图标：Material 风格填充图标，统一 24x24 viewBox

## 性能优化

| 场景 | 方案 |
|------|------|
| 短视频流 | 3 槽位滑窗复用 video 元素，仅切 src 不销毁 |
| 视频切换 | 动画结束才提交索引，无过渡回正实现视觉无缝 |
| 视频加载 | 本地源优先 + 外部 CDN 兜底链 + 4s 缓冲看门狗自动换源 |
| 长列表 | 评论 `content-visibility: auto` 轻量虚拟化 |
| 请求 | 点赞/收藏请求合并防连点，乐观更新 + 失败回滚 |
| 图片 | `loading="lazy"` + 固定宽高防 CLS |
| 首屏 | 路由级代码分割，Feed 页独立 chunk |
| 无障碍 | `aria-live` / `role="dialog"` / `focus-visible` / `prefers-reduced-motion` |
