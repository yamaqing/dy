/**
 * Mock 适配器：实现 axios Adapter 接口，拦截全部请求返回模拟数据
 * 优势：mock 模式下请求依然完整经过 axios 拦截器链路（token 注入、统一响应解码、401 刷新），
 *      切换到真实后端只需将 VITE_USE_MOCK 置 false，业务代码零改动
 */
import type { AxiosResponse, InternalAxiosRequestConfig } from 'axios'
import type { CommentItem, CommentPage, FeedPage, ProductItem, ProductPage, UserLoginDTO, VideoItem, WebResDTO } from './types'
import { BIZ_SUCCESS } from './types'
import { LOCAL_SOURCES } from '@/utils/video-fallback'

// ==================== 可复现伪随机（保证同一视频每次进入数据一致） ====================

function mulberry32(seed: number) {
  return function () {
    seed |= 0
    seed = (seed + 0x6d2b79f5) | 0
    let t = Math.imul(seed ^ (seed >>> 15), 1 | seed)
    t = (t + Math.imul(t ^ (t >>> 7), 61 | t)) ^ t
    return ((t ^ (t >>> 14)) >>> 0) / 4294967296
  }
}

function hashStr(s: string): number {
  let h = 0
  for (let i = 0; i < s.length; i++) h = (h * 31 + s.charCodeAt(i)) | 0
  return Math.abs(h)
}

const sleep = (ms: number) => new Promise((r) => setTimeout(r, ms))
/** 模拟网络延迟 150~450ms */
const randDelay = () => sleep(150 + Math.random() * 300)

const ok = <T>(data: T): WebResDTO<T> => ({ code: BIZ_SUCCESS, data })
const fail = (msg: string): WebResDTO<string> => ({ code: 500, data: msg })

// ==================== 图片资源（文生图 CDN） ====================

/** 封面/头像图：走 vite proxy 同源代理，避免跨域被浏览器拦截 */
const img = (prompt: string, size: 'landscape_16_9' | 'square' = 'landscape_16_9') =>
  `/api/ide/v1/text_to_image?prompt=${encodeURIComponent(prompt)}&image_size=${size}`

// ==================== 静态素材池 ====================
// 视频源统一走 utils/video-fallback 的共享池（mock 下发首源，播放器层持有完整兜底链）

/** 封面 prompt 与横版视频内容匹配（landscape_16_9），避免竖版封面配横版视频的割裂感 */
const COVER_PROMPTS = [
  'cinematic widescreen video cover, young woman street dancing in neon-lit city at night, vibrant lighting',
  'cinematic widescreen video cover, chef cooking flame wok in street food stall, sparks flying, appetizing',
  'cinematic widescreen video cover, cute corgi dog running on grass in slow motion, sunny day',
  'cinematic widescreen video cover, skateboarder doing trick at sunset skate park, dynamic action shot',
  'cinematic widescreen video cover, fashion model in modern minimalist outfit, studio photography, clean background',
  'cinematic widescreen video cover, aerial view of coastal highway with waves crashing, travel vlog style',
  'cinematic widescreen video cover, barista pouring latte art in cozy coffee shop, warm tones',
  'cinematic widescreen video cover, girl playing electric guitar on stage with colorful lights, concert atmosphere',
  'cinematic widescreen video cover, mountain hiker standing on cliff edge above clouds, epic landscape',
  'cinematic widescreen video cover, close-up of hands crafting pottery on wheel, artisan workshop'
]

const AUTHORS = [
  { userId: 1001, nickName: '街舞小柠', avatar: img('portrait avatar of young chinese female street dancer, friendly smile', 'square') },
  { userId: 1002, nickName: '深夜食堂阿Ken', avatar: img('portrait avatar of middle-aged male chef with apron, warm smile', 'square') },
  { userId: 1003, nickName: '柯基大队长', avatar: img('portrait avatar of corgi dog face, cute', 'square') },
  { userId: 1004, nickName: '滑板老炮儿', avatar: img('portrait avatar of young male skateboarder wearing cap', 'square') },
  { userId: 1005, nickName: '穿搭研究所', avatar: img('portrait avatar of fashionable young woman, minimalist style', 'square') },
  { userId: 1006, nickName: '航拍中国小分队', avatar: img('portrait avatar of drone pilot with sunglasses outdoors', 'square') },
  { userId: 1007, nickName: '咖啡拉花师Momo', avatar: img('portrait avatar of female barista in coffee shop', 'square') },
  { userId: 1008, nickName: ' livehouse阿哲', avatar: img('portrait avatar of male guitarist on stage, dramatic lighting', 'square') }
]

const TITLES = [
  '这个动作练了三个月，终于成了！#街舞 #日常练功',
  '凌晨两点的街头炒饭，锅气才是灵魂🔥',
  '狗子今天学会了接飞盘，奖励鸡腿一根',
  'ollie 上台阶一次过，滑手才懂的快乐',
  '初秋穿搭公式：白衬衫 + 直筒牛仔，干净利落',
  '沿着海岸线开了 300 公里，每一帧都是壁纸',
  '客人说想要一只天鹅，安排！',
  '昨晚 livehouse 氛围拉满，下次现场见',
  '在海拔 4200 米看云海翻涌，值了',
  '一只杯子的诞生，从揉泥开始'
]

const MUSIC_NAMES = ['原声 · 街舞小柠', '夜空中最亮的星 - 逃跑计划', '晴天 - 周杰伦', '溯 (Reverse)', '夏日漱石 - 橘子海', 'Faded - Alan Walker', '起风了 - 买辣椒也用券', 'Lemon - 米津玄師', '平凡之路 - 朴树', '海阔天空 - Beyond']

const COMMENT_CONTENTS = [
  '前排！这也太厉害了吧',
  'bgm 求告知！',
  '看饿了，现在点外卖来得及吗',
  '第一百遍看了，依然上头',
  '这才是真正的技术流',
  '关注了，求持续更新',
  '哈哈哈哈笑死我了',
  '求教程！跪求教程！',
  '这狗子比我还会玩',
  '每一帧都能当壁纸',
  '已三连，up 主加油',
  '深夜放毒是吧，举报了（狗头）',
  '同款已经下单了！',
  '这运镜绝了',
  '隔着屏幕都闻到香味了',
  '教练我想学这个',
  '爷青回！',
  '这配色太舒服了',
  '建议反复观看 0:23 秒',
  '有被治愈到，谢谢'
]

// ==================== 商城素材池 ====================

const PRODUCT_TITLES = [
  '2025新款夏季纯棉短袖T恤女宽松百搭学生韩版半袖上衣ins潮',
  '三只松鼠坚果大礼包每日坚果混合装30包休闲零食送礼盒装',
  '苹果iPhone 16 Pro Max 256GB 沙漠钛金属 官方正品5G手机',
  '花西子空气蜜粉散粉定妆粉饼持久控油防水防汗遮瑕不脱妆',
  '苏泊尔电饭煲家用4L大容量智能预约多功能不粘锅煮饭锅',
  '李宁跑步鞋男2025新款轻便减震透气运动鞋学生百搭跑鞋',
  '完美日记动物眼影盘十二色大地色珠光哑光持久不飞粉',
  '美的空气炸锅家用大容量无油低脂智能电炸锅多功能烤箱',
  '南极人四件套全棉纯棉床单被套简约北欧风床品套件',
  '安踏运动裤男2025夏季薄款冰丝速干休闲长裤跑步健身裤',
  '百草味水果干大礼包芒果干草莓干混合装休闲蜜饯零食',
  '华为FreeBuds Pro 4无线蓝牙耳机主动降噪高音质长续航',
  '得力中性笔0.5mm黑色签字笔学生考试专用办公文具30支装',
  '天堂伞全自动折叠雨伞晴雨两用防紫外线遮阳伞大号加固',
  '小熊养生壶家用多功能煮茶壶全自动玻璃电热水壶花茶壶',
  '洁柔抽纸整箱批家用实惠装餐巾纸面巾纸卫生纸24包3层',
  '罗技G304无线鼠标电竞游戏专用轻量化人体工学办公鼠标',
  '富光保温杯316不锈钢大容量便携水杯男女学生简约杯子',
  '维达卷纸蓝色经典有芯卷筒纸卫生纸家用实惠装整箱4层',
  '海飞丝去屑洗发水丝质柔滑型750ml持久留香止痒控油'
]

const SHOP_NAMES = [
  '抖音自营旗舰店',
  '品牌官方严选',
  '源头工厂店',
  '优选生活馆',
  '潮流前线',
  '品质家居馆'
]

const PRODUCT_TAGS = ['直播价', '秒杀', '新品', '热卖', '包邮', '百亿补贴']

// ==================== 数据生成 ====================

/** 会话内点赞状态维护（模拟服务端真实状态） */
const likeState = new Map<number, { liked: boolean; count: number }>()
const favState = new Map<number, { favorited: boolean; count: number }>()
/** 会话内新增评论（保证评论计数一致） */
const postedComments = new Map<number, CommentItem[]>()

const FEED_TOTAL = 40

function genVideo(id: number): VideoItem {
  const rng = mulberry32(hashStr(`video:${id}`))
  const base = id % LOCAL_SOURCES.length
  const author = AUTHORS[Math.floor(rng() * AUTHORS.length)]
  const likeCount = Math.floor(rng() * 480000) + 20000
  const commentCount = Math.floor(rng() * 18000) + 200
  const favoriteCount = Math.floor(rng() * 60000) + 1000
  if (!likeState.has(id)) likeState.set(id, { liked: false, count: likeCount })
  if (!favState.has(id)) favState.set(id, { favorited: false, count: favoriteCount })
  return {
    id,
    playUrl: LOCAL_SOURCES[base],
    coverUrl: img(COVER_PROMPTS[base]),
    // 宽高必须标注真实值：画框按此比例自适应（演示源均为 16:9 横版）
    width: 1280,
    height: 720,
    title: TITLES[base],
    author,
    musicName: MUSIC_NAMES[base],
    likeCount: likeState.get(id)!.count,
    commentCount,
    favoriteCount: favState.get(id)!.count,
    shareCount: Math.floor(rng() * 20000),
    isLiked: likeState.get(id)!.liked,
    isFavorited: favState.get(id)!.favorited
  }
}

const PRODUCT_TOTAL = 60

function genProduct(id: number): ProductItem {
  const rng = mulberry32(hashStr(`product:${id}`))
  const price = Math.floor(rng() * 90000) + 999 // 9.99 ~ 909.99 元
  const hasDiscount = rng() > 0.4
  return {
    id,
    coverUrl: img(
      `e-commerce product photography, ${['fashion clothing on model', 'snack gift box packaging', 'smartphone product shot', 'cosmetics makeup palette', 'kitchen appliance', 'running shoes sneaker', 'home bedding textile', 'electronics headphone', 'stationery pen set', 'umbrella product'][id % 10]}, clean studio background, high quality`,
      'square'
    ),
    title: PRODUCT_TITLES[id % PRODUCT_TITLES.length],
    shopName: SHOP_NAMES[Math.floor(rng() * SHOP_NAMES.length)],
    price,
    originPrice: hasDiscount ? Math.floor(price * (1.3 + rng() * 0.7)) : price,
    sales: Math.floor(rng() * 50000) + 100,
    tag: rng() > 0.5 ? PRODUCT_TAGS[Math.floor(rng() * PRODUCT_TAGS.length)] : undefined
  }
}

function genComment(videoId: number, index: number): CommentItem {
  const rng = mulberry32(hashStr(`comment:${videoId}:${index}`))
  const author = AUTHORS[Math.floor(rng() * AUTHORS.length)]
  return {
    id: videoId * 100000 + index,
    videoId,
    content: COMMENT_CONTENTS[Math.floor(rng() * COMMENT_CONTENTS.length)],
    user: { userId: author.userId + 100, nickName: `${author.nickName}的粉丝${Math.floor(rng() * 900 + 100)}`, avatar: author.avatar },
    likeCount: Math.floor(rng() * 5000),
    createTime: Date.now() - Math.floor(rng() * 7 * 24 * 3600 * 1000),
    replyCount: Math.floor(rng() * 30)
  }
}

// ==================== 路由表 ====================

type MockHandler = (config: InternalAxiosRequestConfig, match: RegExpMatchArray) => Promise<WebResDTO> | WebResDTO

interface MockRoute {
  method: 'get' | 'post'
  pattern: RegExp
  handler: MockHandler
}

function parseBody<T>(config: InternalAxiosRequestConfig): T {
  if (!config.data) return {} as T
  return typeof config.data === 'string' ? JSON.parse(config.data) : (config.data as T)
}

const routes: MockRoute[] = [
  // ---------- 用户域（对齐现有 live-api UserController） ----------
  {
    method: 'post',
    pattern: /^\/user\/sendSMS$/,
    handler: (config) => {
      const mobile = String(config.params?.mobile ?? '')
      if (!/^1[3-9]\d{9}$/.test(mobile)) return fail('请求参数异常')
      return ok('SUCCESSED')
    }
  },
  {
    method: 'post',
    pattern: /^\/user\/mobileLogin$/,
    handler: (config) => {
      const { mobile, code } = parseBody<{ mobile: string; code: number }>(config)
      if (!mobile) return fail('请求参数异常')
      if (code < 1000 || code > 9999) return fail('验证码格式错误')
      // mock：任意 4 位验证码均可登录
      const data: UserLoginDTO = {
        userId: 9527,
        nickName: `用户${mobile.slice(-4)}`,
        avatar: img('portrait avatar of cheerful young person, gradient background, flat illustration', 'square'),
        tokens: { accessToken: `mock-at-${Date.now()}`, refreshToken: `mock-rt-${Date.now()}`, expiresIn: 7200 }
      }
      return ok(data)
    }
  },
  {
    method: 'post',
    pattern: /^\/user\/queryUser$/,
    handler: () => {
      const author = AUTHORS[0]
      return ok({ userId: author.userId, nickName: author.nickName, avatar: author.avatar })
    }
  },
  {
    method: 'post',
    pattern: /^\/user\/refreshToken$/,
    handler: () =>
      ok({ accessToken: `mock-at-${Date.now()}`, refreshToken: `mock-rt-${Date.now()}`, expiresIn: 7200 })
  },

  // ---------- Feed / 视频域（live-video 模块待新建，路径为前端预定义） TODO 待联调确认 ----------
  {
    method: 'get',
    pattern: /^\/feed\/recommend$/,
    handler: (config) => {
      const type = String(config.params?.type ?? 'recommend')
      // 关注流 mock 空数据，用于验证空态 UI
      if (type === 'follow') {
        const page: FeedPage = { list: [], nextCursor: '', hasMore: false }
        return ok(page)
      }
      const cursor = Math.max(0, parseInt(String(config.params?.cursor ?? '0'), 10) || 0)
      const size = Math.min(Math.max(1, Number(config.params?.size ?? 5) || 5), 20)
      const list: VideoItem[] = []
      for (let i = cursor; i < Math.min(cursor + size, FEED_TOTAL); i++) list.push(genVideo(i + 1))
      const next = cursor + size
      const page: FeedPage = { list, nextCursor: String(next), hasMore: next < FEED_TOTAL }
      return ok(page)
    }
  },
  {
    method: 'post',
    pattern: /^\/video\/like$/,
    handler: (config) => {
      const { videoId, liked } = parseBody<{ videoId: number; liked: boolean }>(config)
      // genVideo 副作用：确保 likeState 已初始化
      genVideo(videoId)
      const state = likeState.get(videoId)!
      if (state.liked !== liked) {
        state.liked = liked
        state.count += liked ? 1 : -1
      }
      return ok({ isLiked: state.liked, likeCount: state.count })
    }
  },
  {
    method: 'post',
    pattern: /^\/video\/favorite$/,
    handler: (config) => {
      const { videoId, favorited } = parseBody<{ videoId: number; favorited: boolean }>(config)
      genVideo(videoId)
      const state = favState.get(videoId)!
      if (state.favorited !== favorited) {
        state.favorited = favorited
        state.count += favorited ? 1 : -1
      }
      return ok({ isFavorited: state.favorited, favoriteCount: state.count })
    }
  },
  {
    method: 'get',
    pattern: /^\/video\/comments$/,
    handler: (config) => {
      const videoId = Number(config.params?.videoId ?? 0)
      const offset = Math.max(0, parseInt(String(config.params?.cursor ?? '0'), 10) || 0)
      const size = Math.min(Math.max(1, Number(config.params?.size ?? 20) || 20), 50)
      // mock 单视频评论上限 180 条，便于验证"加载到底"状态
      const total = Math.min(genVideo(videoId).commentCount, 180)
      const posted = postedComments.get(videoId) ?? []
      const list: CommentItem[] = []
      // 用户新发的评论置顶于第一页
      if (offset === 0) list.push(...posted)
      for (let i = offset; i < Math.min(offset + size, total) && list.length < size + posted.length; i++) {
        list.push(genComment(videoId, i))
      }
      const next = offset + size
      const page: CommentPage = { list, nextCursor: String(next), hasMore: next < total }
      return ok(page)
    }
  },
  {
    method: 'post',
    pattern: /^\/video\/comment$/,
    handler: (config) => {
      const { videoId, content } = parseBody<{ videoId: number; content: string }>(config)
      if (!content?.trim()) return fail('评论内容不能为空')
      const comment: CommentItem = {
        id: Date.now(),
        videoId,
        content: content.trim(),
        // TODO 待联调确认：生产由后端从 token 解析当前用户，前端不传用户信息
        user: { userId: 9527, nickName: '我', avatar: '' },
        likeCount: 0,
        createTime: Date.now(),
        replyCount: 0
      }
      const posted = postedComments.get(videoId) ?? []
      posted.unshift(comment)
      postedComments.set(videoId, posted)
      return ok(comment)
    }
  },

  // ---------- 商城域（mall-product 模块，路径为前端预定义） TODO 待联调确认 ----------
  {
    method: 'get',
    pattern: /^\/mall\/product\/list$/,
    handler: (config) => {
      const cursor = Math.max(0, parseInt(String(config.params?.cursor ?? '0'), 10) || 0)
      const size = Math.min(Math.max(1, Number(config.params?.size ?? 10) || 10), 20)
      const list: ProductItem[] = []
      for (let i = cursor; i < Math.min(cursor + size, PRODUCT_TOTAL); i++) list.push(genProduct(i + 1))
      const next = cursor + size
      const page: ProductPage = { list, nextCursor: String(next), hasMore: next < PRODUCT_TOTAL }
      return ok(page)
    }
  }
]

// ==================== 适配器入口 ====================

export async function mockAdapter(config: InternalAxiosRequestConfig): Promise<AxiosResponse> {
  await randDelay()
  const url = config.url ?? ''
  const method = (config.method ?? 'get').toLowerCase()
  for (const route of routes) {
    if (route.method !== method) continue
    const match = url.match(route.pattern)
    if (match) {
      const body = await route.handler(config, match)
      return {
        data: body,
        status: 200,
        statusText: 'OK',
        headers: {},
        config
      }
    }
  }
  // 未匹配路由按业务错误返回，便于前端验证错误兜底
  return {
    data: fail(`mock: 未实现的接口 ${method.toUpperCase()} ${url}`),
    status: 200,
    statusText: 'OK',
    headers: {},
    config
  }
}
