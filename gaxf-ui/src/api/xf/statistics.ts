import request from '@/utils/request'
import type { AjaxResult } from '@/types'
import type {
  StatisticsOverview,
  OverdueStatistics,
  CompletionRateStatistics,
  ReturnRateStatistics,
  MonthlyTrend,
  StatisticsQueryParams
} from '@/types/api/xf/statistics'

// 统计概览
export function getOverview(query?: StatisticsQueryParams): Promise<AjaxResult<StatisticsOverview>> {
  return request({
    url: '/xf/statistics/overview',
    method: 'get',
    params: query
  })
}

// 超期统计
export function getOverdueStatistics(query?: StatisticsQueryParams): Promise<AjaxResult<OverdueStatistics[]>> {
  return request({
    url: '/xf/statistics/overdue',
    method: 'get',
    params: query
  })
}

// 办理率统计
export function getCompletionRate(query?: StatisticsQueryParams): Promise<AjaxResult<CompletionRateStatistics[]>> {
  return request({
    url: '/xf/statistics/completionRate',
    method: 'get',
    params: query
  })
}

// 退回率统计
export function getReturnRate(query?: StatisticsQueryParams): Promise<AjaxResult<ReturnRateStatistics[]>> {
  return request({
    url: '/xf/statistics/returnRate',
    method: 'get',
    params: query
  })
}

// 月度趋势
export function getMonthlyTrend(query?: StatisticsQueryParams): Promise<AjaxResult<MonthlyTrend[]>> {
  return request({
    url: '/xf/statistics/monthlyTrend',
    method: 'get',
    params: query
  })
}
