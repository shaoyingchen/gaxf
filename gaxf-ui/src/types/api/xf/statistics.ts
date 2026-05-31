/** 统计概览数据 */
export interface StatisticsOverview {
  /** 工单总数 */
  totalCount: number
  /** 办理中 */
  inProgressCount: number
  /** 已超期 */
  overdueCount: number
  /** 已办结 */
  completedCount: number
  /** 待派单 */
  pendingCount: number
  /** 已退回 */
  returnedCount: number
}

/** 超期统计 */
export interface OverdueStatistics {
  /** 部门名称 */
  deptName: string
  /** 超期数量 */
  overdueCount: number
  /** 办理中数量 */
  inProgressCount: number
}

/** 办理率统计 */
export interface CompletionRateStatistics {
  /** 部门名称 */
  deptName: string
  /** 办理率 */
  completionRate: number
  /** 已办结数 */
  completedCount: number
  /** 总数 */
  totalCount: number
}

/** 退回率统计 */
export interface ReturnRateStatistics {
  /** 部门名称 */
  deptName: string
  /** 退回率 */
  returnRate: number
  /** 退回数 */
  returnedCount: number
  /** 总数 */
  totalCount: number
}

/** 月度趋势 */
export interface MonthlyTrend {
  /** 月份 */
  month: string
  /** 新增工单数 */
  newCount: number
  /** 办结工单数 */
  completedCount: number
  /** 超期工单数 */
  overdueCount: number
}

/** 统计查询参数 */
export interface StatisticsQueryParams {
  /** 部门ID */
  deptId?: number
  /** 开始时间 */
  beginTime?: string
  /** 结束时间 */
  endTime?: string
  /** 状态 */
  status?: string
}
