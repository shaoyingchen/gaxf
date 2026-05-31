import request from '@/utils/request'
import type { AjaxResult, TableDataInfo } from '@/types'
import type { WorkOrderQueryParams, XfWorkOrder, AssignParams } from '@/types/api/xf/workOrder'

// 查询工单列表
export function listWorkOrder(query: WorkOrderQueryParams): Promise<TableDataInfo<XfWorkOrder[]>> {
  return request({
    url: '/xf/workOrder/list',
    method: 'get',
    params: query
  })
}

// 查询工单详细
export function getWorkOrder(id: number): Promise<AjaxResult<XfWorkOrder>> {
  return request({
    url: '/xf/workOrder/' + id,
    method: 'get'
  })
}

// 新增工单
export function addWorkOrder(data: XfWorkOrder): Promise<AjaxResult> {
  return request({
    url: '/xf/workOrder',
    method: 'post',
    data: data
  })
}

// 修改工单
export function updateWorkOrder(data: XfWorkOrder): Promise<AjaxResult> {
  return request({
    url: '/xf/workOrder',
    method: 'put',
    data: data
  })
}

// 删除工单
export function delWorkOrder(id: number | number[]): Promise<AjaxResult> {
  return request({
    url: '/xf/workOrder/' + id,
    method: 'delete'
  })
}

// 导出工单
export function exportWorkOrder(query: WorkOrderQueryParams): Promise<AjaxResult> {
  return request({
    url: '/xf/workOrder/export',
    method: 'post',
    params: query
  })
}

// 导入工单（异步，返回taskId）
export function importWorkOrder(data: FormData): Promise<AjaxResult> {
  return request({
    url: '/xf/workOrder/import',
    method: 'post',
    headers: { 'Content-Type': 'multipart/form-data' },
    data: data
  })
}

// 查询导入进度
export function getImportProgress(taskId: string): Promise<AjaxResult> {
  return request({
    url: '/xf/workOrder/importProgress/' + taskId,
    method: 'get'
  })
}

// 交办派单
export function assignWorkOrder(data: AssignParams): Promise<AjaxResult> {
  return request({
    url: '/xf/workOrder/assign',
    method: 'post',
    data: data
  })
}

// 我的待办工单列表
export function myTodoList(query: WorkOrderQueryParams): Promise<TableDataInfo<XfWorkOrder[]>> {
  return request({
    url: '/xf/workOrder/myTodo',
    method: 'get',
    params: query
  })
}

// 工单统计
export function workOrderStatistics(): Promise<AjaxResult> {
  return request({
    url: '/xf/workOrder/statistics',
    method: 'get'
  })
}

// 我的待办统计
export function myTodoStatistics(): Promise<AjaxResult> {
  return request({
    url: '/xf/workOrder/myTodoStatistics',
    method: 'get'
  })
}
