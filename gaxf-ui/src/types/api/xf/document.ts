import type { BaseEntity } from "../common"

/** 文书信息 */
export interface XfDocument extends BaseEntity {
  /** 主键 */
  id?: number
  /** 工单ID */
  orderId?: number
  /** 文书模板ID */
  templateId?: number
  /** 文书名称 */
  docName?: string
  /** 文书类型 */
  docType?: string
  /** 文书内容（HTML） */
  docContent?: string
  /** 文件路径 */
  filePath?: string
  /** 签名状态 '0'=未签名 '1'=已签名 */
  signStatus?: string
  /** 签名人 */
  signBy?: string
  /** 签名时间 */
  signTime?: string
}

/** 文书查询参数 */
export interface DocumentQueryParams {
  /** 工单ID */
  orderId: number
}

/** 文书生成参数 */
export interface DocumentGenerateParams {
  /** 工单ID */
  orderId: number
  /** 模板ID */
  templateId: number
}
