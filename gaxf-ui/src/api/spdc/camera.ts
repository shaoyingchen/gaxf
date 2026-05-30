import request from '@/utils/request'
import type { AjaxResult, TableDataInfo, CameraQueryParams, DcCamera } from '@/types'

// 查询点位管理列表
export function listCamera(query: CameraQueryParams): Promise<TableDataInfo<DcCamera>> {
  return request({
    url: '/spdc/camera/list',
    method: 'get',
    params: query
  })
}

// 查询点位管理详细
export function getCamera(pointId: number): Promise<AjaxResult<DcCamera>> {
  return request({
    url: '/spdc/camera/' + pointId,
    method: 'get'
  })
}

// 新增点位管理
export function addCamera(data: DcCamera): Promise<AjaxResult> {
  return request({
    url: '/spdc/camera',
    method: 'post',
    data: data
  })
}

// 修改点位管理
export function updateCamera(data: DcCamera): Promise<AjaxResult> {
  return request({
    url: '/spdc/camera',
    method: 'put',
    data: data
  })
}

// 删除点位管理
export function delCamera(pointId: number | number[]): Promise<AjaxResult> {
  return request({
    url: '/spdc/camera/' + pointId,
    method: 'delete'
  })
}

// 根据部门查询点位列表
export function listCameraByDept(deptId: number, query?: Partial<CameraQueryParams>): Promise<TableDataInfo<DcCamera>> {
  return request({
    url: '/spdc/camera/list',
    method: 'get',
    params: { ...query, deptId }
  })
}


