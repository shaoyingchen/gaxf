import request from '@/utils/request'

// 查询业务字典树结构列表
export function listDictTree(query: any) {
  return request({
    url: '/spdc/dict/tree',
    method: 'get',
    params: query
  })
}

// 查询业务字典列表
export function listDict(query: any) {
  return request({
    url: '/spdc/dict/list',
    method: 'get',
    params: query
  })
}

// 查询业务字典详细
export function getDict(id: number) {
  return request({
    url: '/spdc/dict/' + id,
    method: 'get'
  })
}

// 新增业务字典
export function addDict(data: any) {
  return request({
    url: '/spdc/dict',
    method: 'post',
    data: data
  })
}

// 修改业务字典
export function updateDict(data: any) {
  return request({
    url: '/spdc/dict',
    method: 'put',
    data: data
  })
}

// 删除业务字典
export function delDict(ids: number | number[]) {
  return request({
    url: '/spdc/dict/' + ids,
    method: 'delete'
  })
}