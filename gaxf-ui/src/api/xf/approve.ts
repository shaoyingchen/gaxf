import request from '@/utils/request'
import type { AjaxResult, TableDataInfo } from '@/types'
import type { ApproveFlowConfig, ApproveTask, ApproveProgress, ApproveActionBody } from '@/types/api/xf/approve'

export function listApproveFlowConfig(query?: any): Promise<TableDataInfo<ApproveFlowConfig[]>> {
  return request({
    url: '/xf/approve/config/list',
    method: 'get',
    params: query
  })
}

export function getApproveFlowConfig(id: number): Promise<AjaxResult<ApproveFlowConfig>> {
  return request({
    url: '/xf/approve/config/' + id,
    method: 'get'
  })
}

export function addApproveFlowConfig(data: ApproveFlowConfig): Promise<AjaxResult> {
  return request({
    url: '/xf/approve/config',
    method: 'post',
    data
  })
}

export function updateApproveFlowConfig(data: ApproveFlowConfig): Promise<AjaxResult> {
  return request({
    url: '/xf/approve/config',
    method: 'put',
    data
  })
}

export function delApproveFlowConfig(id: number | number[]): Promise<AjaxResult> {
  return request({
    url: '/xf/approve/config/' + id,
    method: 'delete'
  })
}

export function getApproveTodo(): Promise<AjaxResult<{ pending: ApproveTask[]; handled: ApproveTask[] }>> {
  return request({
    url: '/xf/approve/task/myTodo',
    method: 'get'
  })
}

export function passApproveTask(data: ApproveActionBody): Promise<AjaxResult> {
  return request({
    url: '/xf/approve/task/pass',
    method: 'post',
    data
  })
}

export function rejectApproveTask(data: ApproveActionBody): Promise<AjaxResult> {
  return request({
    url: '/xf/approve/task/reject',
    method: 'post',
    data
  })
}

export type { ApproveProgress }
