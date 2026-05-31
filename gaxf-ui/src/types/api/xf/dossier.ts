import type { BaseEntity } from "../common"

/** 卷宗文件 */
export interface XfDossier extends BaseEntity {
  /** 主键 */
  id?: number
  /** 工单ID */
  orderId?: number
  /** 文件名 */
  fileName?: string
  /** 文件路径 */
  filePath?: string
  /** 文件大小 */
  fileSize?: number
  /** 文件类型 */
  fileType?: string
  /** 上传人 */
  uploadBy?: string
  /** 上传时间 */
  uploadTime?: string
}

/** 卷宗查询参数 */
export interface DossierQueryParams {
  /** 工单ID */
  orderId: number
}
