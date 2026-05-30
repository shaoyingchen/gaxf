import type { PageDomain, BaseEntity } from "../common";

/** 预警管理分页查询参数 */
export interface AlertQueryParams extends PageDomain {
  /** 请求参数（时间范围等） */
  params?: {
    beginTime?: string;
    endTime?: string;
  };
  /** 预警具体类型（2级） */
  warningtype?: string;
  /** 预警大类（1级） */
  type?: string;
  /** 预警主类型 */
  mainType?: string;
  /** 处置状态（01已核实,02已处置,03待处理） */
  deal?: string;
  /** 部门ID */
  departId?: number;
  /** 摄像机ID */
  cameraId?: number;
  /** 单位名称 */
  policename?: string;
  /** 县局名 */
  countyname?: string;
  /** 区域类型 */
  zoneType?: string;
  /** 房间类型 */
  roomType?: string;
  /** 开始时间 */
  beginTime?: string;
  /** 结束时间 */
  endTime?: string;
  /** 是否处理（0未，1处） */
  isResolved?: number;
}

/** 预警管理信息 */
export interface DcDxjg extends BaseEntity {
  /** 主键 */
  id?: number;
  /** 问题单位编码 */
  orgid?: string;
  /** 预警具体类型（2级） */
  warningtype?: string;
  /** 单位名称 */
  policename?: string;
  /** 县局名 */
  countyname?: string;
  /** 摄像机的编码 */
  orgtableid?: string;
  /** 预警时间 */
  time?: string;
  /** 处置状态（旧） */
  state?: string;
  /** 预警内容 */
  msgtitle?: string;
  /** 问题图片 */
  pic1?: string;
  /** 第二个摄像头预警的图片 */
  pic2?: string;
  /** 预警大分类（1级） */
  type?: string;
  /** 预警接收人 */
  acceptUserid?: number;
  /** 发送状态 */
  sendsate?: number;
  /** 部门ID */
  departId?: number;
  /** 摄像机的ID */
  cameraId?: number;
  /** 事件编号 */
  eventCode?: string;
  /** 处置结果 */
  result?: string;
  /** 处置状态（01已核实,02已处置,03待处理） */
  deal?: string;
  /** 区域类型 */
  zoneType?: string;
  /** 房间类型 */
  roomType?: string;
  /** 县局名字 */
  county?: string;
  /** 预警大类 */
  mainType?: string;
  /** 是否处理（0未，1处） */
  isResolved?: number;
  /** 部门名称（关联查询） */
  deptName?: string;
  /** 摄像机名称（关联查询） */
  cameraName?: string;
}

/** 预警统计数据 */
export interface AlertStatistics {
  /** 今日预警数 */
  todayCount?: number;
  /** 待核实数 */
  verifyCount?: number;
  /** 待处理数 */
  pendingCount?: number;
  /** 已处置数 */
  resolvedCount?: number;
}