import request from '@/utils/request'
import type { AjaxResult } from '@/types'

export interface DcDict {
  id?: number
  parentId?: number
  name?: string
  kind?: number
  sort?: number
  deleted?: number
  parentName?: string
  children?: DcDict[]
  createBy?: string
  createTime?: string
  updateBy?: string
  updateTime?: string
}

export interface DcDictQuery {
  parentId?: number
  name?: string
  kind?: number
}

// 查询业务字典树结构
export function treeDcDict(query?: DcDictQuery): Promise<AjaxResult<DcDict[]>> {
  return request({
    url: '/spdc/dict/tree',
    method: 'get',
    params: query
  })
}

// 查询业务字典列表
export function listDcDict(query?: DcDictQuery): Promise<AjaxResult<DcDict[]>> {
  return request({
    url: '/spdc/dict/list',
    method: 'get',
    params: query
  })
}

// 查询业务字典详细
export function getDcDict(id: number): Promise<AjaxResult<DcDict>> {
  return request({
    url: '/spdc/dict/' + id,
    method: 'get'
  })
}

// 新增业务字典
export function addDcDict(data: DcDict): Promise<AjaxResult> {
  return request({
    url: '/spdc/dict',
    method: 'post',
    data: data
  })
}

// 修改业务字典
export function updateDcDict(data: DcDict): Promise<AjaxResult> {
  return request({
    url: '/spdc/dict',
    method: 'put',
    data: data
  })
}

// 删除业务字典
export function delDcDict(ids: Array<number | string>): Promise<AjaxResult> {
  return request({
    url: '/spdc/dict/' + ids,
    method: 'delete'
  })
}
