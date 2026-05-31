import request from '@/utils/request'
import type { AjaxResult, TableDataInfo } from '@/types'
import type { XfReviewRecord, ReviewQueryParams, ReviewParams } from '@/types/api/xf/review'

// 查询审核记录列表
export function listReview(query: ReviewQueryParams): Promise<TableDataInfo<XfReviewRecord[]>> {
  return request({
    url: '/xf/review/list',
    method: 'get',
    params: query
  })
}

// 派出所审核
export function policeReview(data: ReviewParams): Promise<AjaxResult> {
  return request({
    url: '/xf/review/policeReview',
    method: 'post',
    data: data
  })
}

// 专员审核
export function commissionerReview(data: ReviewParams): Promise<AjaxResult> {
  return request({
    url: '/xf/review/commissionerReview',
    method: 'post',
    data: data
  })
}

// 领导审核
export function leaderReview(data: ReviewParams): Promise<AjaxResult> {
  return request({
    url: '/xf/review/leaderReview',
    method: 'post',
    data: data
  })
}
