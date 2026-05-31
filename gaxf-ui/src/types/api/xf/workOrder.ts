import type { PageDomain, BaseEntity } from "../common"

/** 工单信息 */
export interface XfWorkOrder extends BaseEntity {
  /** 主键 */
  id?: number
  /** 信访件编号 */
  xfCaseNo?: string
  /** 工单标题 */
  title?: string
  /** 来源类型 '0'=Excel导入 '1'=手动新建 */
  sourceType?: string
  /** 工单内容 */
  content?: string
  /** 信访人姓名 */
  petitionerName?: string
  /** 信访人身份证号 */
  petitionerIdcard?: string
  /** 信访人电话 */
  petitionerPhone?: string
  /** 信访人地址 */
  petitionerAddress?: string
  /** 信访内容 */
  xfContent?: string
  /** 信访形式 */
  xfForm?: string
  /** 信访日期 */
  xfDate?: string
  /** 问题发生地 */
  problemLocation?: string
  /** 异常动态 */
  abnormalDynamic?: string
  /** 问题发生日期 */
  problemDate?: string
  /** 信访人数 */
  petitionerCount?: number
  /** 信访诉求 */
  xfDemand?: string
  /** 公安业务分类 */
  businessCategory?: string
  /** 登记单位 */
  registerUnit?: string
  /** 档案编号 */
  archiveNo?: string
  /** 转往处 */
  transferDest?: string
  /** 办理方式 */
  handleMethod?: string
  /** 具体承办单位 */
  specificHandleUnit?: string
  /** 信访件状态 */
  xfCaseStatus?: string
  /** 办结状态 */
  completionStatus?: string
  /** 责任部门 */
  responsibleDept?: string
  /** 办结时间 */
  completionTime?: string
  /** 信访事项编号 */
  xfItemNo?: string
  /** 警种名称 */
  policeTypeName?: string
  /** 信访目的 */
  xfPurpose?: string
  /** 登记时间 */
  registerTime?: string
  /** 办理期限 */
  deadline?: string
  /** 状态 '0'=待派单 '1'=办理中 '2'=已上报 '3'=已办结 '4'=已退回 '5'=已超期 */
  status?: string
  /** 超期次数 */
  overdueCount?: number
  /** 是否锁定 '0'=否 '1'=是 */
  isLocked?: string
  /** 是否置顶 '0'=否 '1'=是 */
  isTop?: string
  /** 创建部门ID */
  createDeptId?: number
  /** 交办记录 */
  assignRecords?: XfAssignRecord[]
  /** 当前用户交办记录ID（myTodo查询使用） */
  assignId?: number
}

/** 工单分页查询参数 */
export interface WorkOrderQueryParams extends PageDomain {
  /** 请求参数（时间范围等） */
  params?: {
    beginTime?: string
    endTime?: string
  }
  /** 信访件编号 */
  xfCaseNo?: string
  /** 工单标题 */
  title?: string
  /** 状态 */
  status?: string
  /** 来源类型 */
  sourceType?: string
  /** 信访形式 */
  xfForm?: string
  /** 公安业务分类 */
  businessCategory?: string
  /** 登记单位 */
  registerUnit?: string
  /** 警种名称 */
  policeTypeName?: string
  /** 创建部门ID */
  createDeptId?: number
  /** 开始时间 */
  beginTime?: string
  /** 结束时间 */
  endTime?: string
}

/** 交办参数 */
export interface AssignParams {
  /** 工单ID */
  orderId: number
  /** 部门ID列表 */
  deptIds: number[]
  /** 办理期限 */
  deadline: string
}

/** 交办记录（简要，内嵌在工单中） */
export interface XfAssignRecord {
  id?: number
  orderId?: number
  deptId?: number
  deptName?: string
  deadline?: string
  status?: string
  receiveTime?: string
  reportTime?: string
}
