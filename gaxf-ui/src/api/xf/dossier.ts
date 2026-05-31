import request from '@/utils/request'
import type { AjaxResult, TableDataInfo } from '@/types'
import type { XfDossier, DossierQueryParams } from '@/types/api/xf/dossier'

// 查询卷宗列表
export function listDossier(query: DossierQueryParams): Promise<TableDataInfo<XfDossier[]>> {
  return request({
    url: '/xf/dossier/list',
    method: 'get',
    params: query
  })
}

// 上传卷宗
export function uploadDossier(data: FormData): Promise<AjaxResult> {
  return request({
    url: '/xf/dossier/upload',
    method: 'post',
    headers: { 'Content-Type': 'multipart/form-data' },
    data: data
  })
}

// 删除卷宗
export function delDossier(id: number): Promise<AjaxResult> {
  return request({
    url: '/xf/dossier/' + id,
    method: 'delete'
  })
}
