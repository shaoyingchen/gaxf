import request from '@/utils/request'
import type { AjaxResult, TableDataInfo } from '@/types'
import type { PgaQueryParams, DcPga } from '@/types/api/spdc/pga'

// 查询问题整改列表
export function listPga(query: PgaQueryParams): Promise<TableDataInfo<DcPga[]>> {
  return request({
    url: '/spdc/pga/list',
    method: 'get',
    params: query
  })
}

// 查询问题整改详细
export function getPga(id: number): Promise<AjaxResult<DcPga>> {
  return request({
    url: '/spdc/pga/' + id,
    method: 'get'
  })
}

// 从预警创建问题整改（处置预警）
export function createFromAlert(alertId: number, data: DcPga): Promise<AjaxResult> {
  return request({
    url: '/spdc/pga/createFromAlert/' + alertId,
    method: 'post',
    data: data
  })
}

// 整改反馈提交
export function submitFeedback(data: DcPga): Promise<AjaxResult> {
  return request({
    url: '/spdc/pga/feedback',
    method: 'put',
    data: data
  })
}

// 修改问题整改
export function updatePga(data: DcPga): Promise<AjaxResult> {
  return request({
    url: '/spdc/pga',
    method: 'put',
    data: data
  })
}

// 删除问题整改
export function delPga(id: number | number[]): Promise<AjaxResult> {
  return request({
    url: '/spdc/pga/' + id,
    method: 'delete'
  })
}