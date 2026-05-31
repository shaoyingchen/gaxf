import request from '@/utils/request'
import type { AjaxResult, TableDataInfo } from '@/types'
import type { XfAssignRecord, AssignQueryParams, SubmitReportParams, CountyReviewParams } from '@/types/api/xf/assign'

// 查询交办记录列表
export function listAssign(query: AssignQueryParams): Promise<TableDataInfo<XfAssignRecord[]>> {
  return request({
    url: '/xf/assign/list',
    method: 'get',
    params: query
  })
}

// 查询交办记录详细
export function getAssign(id: number): Promise<AjaxResult<XfAssignRecord>> {
  return request({
    url: '/xf/assign/' + id,
    method: 'get'
  })
}

// 接收工单
export function receiveAssign(assignId: number): Promise<AjaxResult> {
  return request({
    url: '/xf/assign/receive',
    method: 'put',
    data: { id: assignId }
  })
}

// 提交办理报告
export function submitReport(data: FormData): Promise<AjaxResult> {
  return request({
    url: '/xf/assign/submitReport',
    method: 'post',
    headers: { 'Content-Type': 'multipart/form-data' },
    data: data
  })
}

// 县局审核
export function countyReview(data: CountyReviewParams): Promise<AjaxResult> {
  return request({
    url: '/xf/assign/countyReview',
    method: 'post',
    data: data
  })
}
