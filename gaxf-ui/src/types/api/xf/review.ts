import type { PageDomain, BaseEntity } from "../common"

/** 审核记录 */
export interface XfReviewRecord extends BaseEntity {
  /** 主键 */
  id?: number
  /** 工单ID */
  orderId?: number
  /** 交办记录ID */
  assignId?: number
  /** 审核人ID */
  reviewerId?: number
  /** 审核人姓名 */
  reviewerName?: string
  /** 审核类型 '1'=派出所审核 '2'=县局审核 '3'=专员审核 '4'=领导审核 */
  reviewType?: string
  /** 审核意见 */
  opinion?: string
  /** 审核结果 '0'=通过 '1'=退回 */
  result?: string
  /** 退回目标 '1'=退回县局 '2'=退回专员 */
  returnTarget?: string
  /** 审核时间 */
  reviewTime?: string
  /** 关联工单 */
  workOrder?: import('./workOrder').XfWorkOrder
}

/** 审核记录分页查询参数 */
export interface ReviewQueryParams extends PageDomain {
  /** 工单ID */
  orderId?: number
  /** 审核类型 */
  reviewType?: string
  /** 审核结果 */
  result?: string
}

/** 审核参数 */
export interface ReviewParams {
  /** 工单ID */
  orderId: number
  /** 交办记录ID */
  assignId?: number
  /** 审核类型 */
  reviewType: string
  /** 审核意见 */
  opinion: string
  /** 审核结果 '0'=通过 '1'=退回 */
  result: string
  /** 退回目标 */
  returnTarget?: string
}
