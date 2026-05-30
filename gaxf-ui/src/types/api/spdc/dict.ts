// 业务字典类型定义

// 字典实体
export interface DcDict {
  id?: number
  parentId?: number
  parentName?: string
  name?: string
  kind?: number
  sort?: number
  deleted?: number
  createBy?: string
  createTime?: string
  updateBy?: string
  updateTime?: string
  children?: DcDict[]
}

// 字典查询参数
export interface DictQueryParams {
  kind?: number
  parentId?: number
  name?: string
}

// 字典种类枚举
export const DICT_KIND_OPTIONS = [
  { value: 1, label: '问题分类' },
  { value: 2, label: '设备分类' },
  { value: 3, label: '预警分类' }
]

// 根据kind获取名称
export function getKindName(kind: number): string {
  const option = DICT_KIND_OPTIONS.find(o => o.value === kind)
  return option ? option.label : '未知'
}