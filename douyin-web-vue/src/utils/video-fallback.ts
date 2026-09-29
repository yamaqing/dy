/**
 * 视频播放源池与失败兜底策略
 * 演示环境：优先使用本地 public/videos/ 下的 MP4（零网络依赖，秒开）；
 * 外部 CDN 作为兜底链，任一源不可达（跨境超时/404）时播放器自动切换
 * TODO 待联调确认：生产环境播放地址由后端下发（playUrl），本模块仅服务演示环境兜底
 */

/** 本地源（已下载到 public/demo-videos/，构建时随 dist 发布；目录名避开 vite proxy /video 前缀） */
export const LOCAL_SOURCES = [
  '/demo-videos/sample-1.mp4', // 2.6MB，Big Buck Bunny 片段
  '/demo-videos/sample-2.mp4', // 10.8MB，Sintel 预告
  '/demo-videos/sample-3.mp4'  // 4.3MB，Elephants Dream 片段
]

/** 外部 CDN 兜底源（按实测可达性排序） */
const REMOTE_SOURCES = [
  'https://vjs.zencdn.net/v/oceans.mp4',
  'https://media.w3.org/2010/05/video/movie_300.mp4',
  'https://media.w3.org/2010/05/bunny/trailer.mp4',
  'https://media.w3.org/2010/05/sintel/trailer.mp4',
  'https://mdn.github.io/shared-assets/videos/flower.mp4'
]

/** 完整源池：本地优先，外部兜底 */
export const VIDEO_SOURCES: string[] = [...LOCAL_SOURCES, ...REMOTE_SOURCES]

/** 单个槽位允许的最大换源次数（超出后展示失败重试态，避免无限循环切源） */
export const MAX_SOURCE_RETRY = 4

/**
 * 从未失败过的源中挑选下一个备用源
 * @param failedSrcs 本次播放已失败（网络错误/长时间缓冲）的源列表
 * @returns 可用备用源；全部失败返回 null
 */
export function pickFallbackSource(failedSrcs: string[]): string | null {
  return VIDEO_SOURCES.find((src) => !failedSrcs.includes(src)) ?? null
}
