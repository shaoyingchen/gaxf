import request from '@/utils/request'
import type { AjaxResult, TableDataInfo } from '@/types'
import type { XfMessage, MessageQueryParams } from '@/types/api/xf/message'

// 查询消息列表
export function listMessage(query: MessageQueryParams & { pageNum: number; pageSize: number }): Promise<TableDataInfo<XfMessage[]>> {
  return request({
    url: '/xf/message/list',
    method: 'get',
    params: query
  })
}

// 获取未读消息数量
export function unreadCount(): Promise<AjaxResult<number>> {
  return request({
    url: '/xf/message/unreadCount',
    method: 'get'
  })
}

// 标记消息已读
export function readMessage(id: number): Promise<AjaxResult> {
  return request({
    url: '/xf/message/read/' + id,
    method: 'put'
  })
}

// 全部标记已读
export function readAllMessage(): Promise<AjaxResult> {
  return request({
    url: '/xf/message/readAll',
    method: 'put'
  })
}
