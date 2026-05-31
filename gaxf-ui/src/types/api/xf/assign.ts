import type { PageDomain, BaseEntity } from "../common"

/** 交办记录 */
export interface XfAssignRecord extends BaseEntity {
  /** 主键 */
  id?: number
  /** 工单ID */
  orderId?: number
  /** 承办部门ID */
  deptId?: number
  /** 承办部门名称 */
  deptName?: string
  /** 办理期限 */
  deadline?: string
  /** 接收时间 */
  receiveTime?: string
  /** 上报时间 */
  reportTime?: string
  /** 状态 '0'=待接收 '1'=办理中 '2'=已上报 '3'=已退回 */
  status?: string
  /** 办理报告 */
  reportContent?: string
  /** 办理附件 */
  reportAttachment?: string
  /** 关联工单 */
  workOrder?: import('./workOrder').XfWorkOrder
}

/** 交办记录分页查询参数 */
export interface AssignQueryParams extends PageDomain {
  /** 工单ID */
  orderId?: number
  /** 承办部门ID */
  deptId?: number
  /** 状态 */
  status?: string
}

/** 上报参数 */
export interface SubmitReportParams {
  /** 交办记录ID */
  id: number
  /** 办理报告 */
  reportContent: string
  /** 办理附件 */
  reportAttachment?: string
}

/** 县局审核参数 */
export interface CountyReviewParams {
  /** 交办记录ID */
  id: number
  /** 审核意见 */
  opinion: string
  /** 是否通过 */
  passed: boolean
}
