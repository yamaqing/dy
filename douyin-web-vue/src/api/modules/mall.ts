import type { ProductPage } from '../types'
import { request } from '../request'

/** 商城接口（mall-product 模块） TODO 待联调确认 */
export const mallApi = {
  /** 商品列表（游标分页） */
  listProducts(cursor = '0', size = 10) {
    return request.get<ProductPage>('/mall/product/list', { cursor, size })
  }
}
