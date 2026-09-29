/// <reference types="vite/client" />

interface ImportMetaEnv {
  /** 是否启用内置 mock 适配器 */
  readonly VITE_USE_MOCK: string
  /** live-gateway 地址（mock 关闭时经 vite proxy 使用） */
  readonly VITE_GATEWAY_TARGET?: string
}

interface ImportMeta {
  readonly env: ImportMetaEnv
}
