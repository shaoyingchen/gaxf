import request from '@/utils/request'
import type { AjaxResult, TableDataInfo, AlertQueryParams, DcDxjg, AlertStatistics } from '@/types'

// 查询预警管理列表
export function listAlert(query: AlertQueryParams): Promise<TableDataInfo<DcDxjg[]>> {
  return request({
    url: '/spdc/alert/list',
    method: 'get',
    params: query
  })
}

// 查询预警统计数据
export function getAlertStatistics(): Promise<AjaxResult<AlertStatistics>> {
  return request({
    url: '/spdc/alert/statistics',
    method: 'get'
  })
}

// 查询预警管理详细
export function getAlert(id: number): Promise<AjaxResult<DcDxjg>> {
  return request({
    url: '/spdc/alert/' + id,
    method: 'get'
  })
}

// 处置预警（更新处置状态和结果）
export function handleAlert(data: DcDxjg): Promise<AjaxResult> {
  return request({
    url: '/spdc/alert',
    method: 'put',
    data: data
  })
}

// 删除预警管理
export function delAlert(id: number | number[]): Promise<AjaxResult> {
  return request({
    url: '/spdc/alert/' + id,
    method: 'delete'
  })
}

// 新增预警
export function addAlert(data: DcDxjg): Promise<AjaxResult> {
  return request({
    url: '/spdc/alert',
    method: 'post',
    data: data
  })
}