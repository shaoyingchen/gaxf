import type { PageDomain, BaseEntity } from "../common";

/** 点位管理配置分页查询参数 */
export interface CameraQueryParams extends PageDomain {
  /** 国标编码 */
  gbCode?: string;
  /** 部门ID（所属单位，关联 system_dept） */
  deptId?: number;
  /** 部门名称 */
  deptName?: string;
  /** 点位名称 */
  pointName?: string;
  /** 房间类型 */
  roomKind?: string;
  /** 所属区域 */
  zoneType?: string;
  /** 视频分析类型 */
  upAlarm?: number;
  /** 点位状态（1在线 0离线） */
  pointStatus?: number;
  /** 摄像机类型(1海康 2大华) */
  cameraType?: number;
  /** IP地址 */
  ipAddress?: string;
  /** 端口号 */
  port?: string;
  /** 用户名 */
  username?: string;
  /** 密码 */
  password?: string;
  /** 通道号 */
  channelNo?: number;
  /** 设备编码 */
  code?: string;
  /** 分析状态 */
  detect?: number;
  /** 责任人 */
  dutyUser?: string;
  /** 启用状态 */
  enable?: number;
}

/** 点位管理配置信息 */
export interface DcCamera extends BaseEntity {
  /** 点位主键 */
  pointId?: number;
  /** 国标编码 */
  gbCode?: string;
  /** 部门ID（所属单位，关联 system_dept） */
  deptId?: number;
  /** 部门名称 */
  deptName?: string;
  /** 点位名称 */
  pointName?: string;
  /** 房间类型 */
  roomKind?: string;
  /** 设备类型ID */
  zoneTypeId?: number;
  /** 所属区域 */
  zoneType?: string;
  /** 视频分析类型 */
  upAlarm?: number;
  /** IP地址 */
  ipAddress?: string;
  /** 端口号 */
  port?: string;
  /** 摄像机用户名 */
  username?: string;
  /** 摄像机密码 */
  password?: string;
  /** 摄像机通道 */
  channelNo?: number;
  /** 摄像机类型(1海康 2大华) */
  cameraType?: number;
  /** 县级公安局 */
  county?: string;
  /** 市局名称 */
  city?: string;
  /** 警种 */
  jz?: string;
  /** 点位状态（1在线 0离线） */
  pointStatus?: number;
  /** 状态原因备注 */
  connState?: string;
  /** 检测人数 */
  personNum?: number;
  /** 设备编码 */
  code?: string;
  /** 国标状态 */
  gbState?: number;
  /** 单位编码 */
  parentCode?: string;
  /** 分析状态 */
  detect?: number;
  /** 必须接入 */
  mustAcc?: number;
  /** 分析备注 */
  detectTag?: string;
  /** 计数 */
  cCount?: number;
  /** 责任人 */
  dutyUser?: string;
  /** 责任电话 */
  dutyTel?: string;
  /** 删除标志（0正常 1删除） */
  delFlag?: string;
  /** 启用状态（0禁用 1启用） */
  enable?: number;
  /** 备注 */
  remark?: string;
  /** 创建者 */
  createBy?: string;
  /** 创建时间 */
  createTime?: string;
  /** 更新者 */
  updateBy?: string;
  /** 更新时间 */
  updateTime?: string;
}
