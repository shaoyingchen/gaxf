import request from '@/utils/request'
import type { AjaxResult, TableDataInfo } from '@/types'
import type { XfDocument, DocumentQueryParams, DocumentGenerateParams } from '@/types/api/xf/document'

// 查询文书列表
export function listDocument(query: DocumentQueryParams): Promise<TableDataInfo<XfDocument[]>> {
  return request({
    url: '/xf/document/list',
    method: 'get',
    params: query
  })
}

// 生成文书
export function generateDocument(data: DocumentGenerateParams): Promise<AjaxResult<XfDocument>> {
  return request({
    url: '/xf/document/generate',
    method: 'post',
    data: data
  })
}

// 签署文书
export function signDocument(id: number): Promise<AjaxResult> {
  return request({
    url: '/xf/document/sign/' + id,
    method: 'put'
  })
}

// 下载文书
export function downloadDocument(id: number): Promise<Blob> {
  return request({
    url: '/xf/document/download/' + id,
    method: 'get',
    responseType: 'blob'
  })
}
