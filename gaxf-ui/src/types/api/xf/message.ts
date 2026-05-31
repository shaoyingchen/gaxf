import type { BaseEntity } from "../common"

/** 站内消息 */
export interface XfMessage extends BaseEntity {
  /** 主键 */
  id?: number
  /** 接收人ID */
  receiverId?: number
  /** 消息标题 */
  title?: string
  /** 消息内容 */
  content?: string
  /** 消息类型 '1'=工单通知 '2'=审核通知 '3'=超期提醒 '4'=系统通知 */
  msgType?: string
  /** 关联业务ID */
  bizId?: number
  /** 是否已读 '0'=未读 '1'=已读 */
  isRead?: string
  /** 读取时间 */
  readTime?: string
}

/** 消息查询参数 */
export interface MessageQueryParams {
  /** 消息类型 */
  msgType?: string
  /** 是否已读 */
  isRead?: string
}
