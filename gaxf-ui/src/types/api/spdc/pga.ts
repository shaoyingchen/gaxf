import type { PageDomain, BaseEntity } from "../common";

/** 问题整改分页查询参数 */
export interface PgaQueryParams extends PageDomain {
  /** 请求参数（时间范围等） */
  params?: {
    beginTime?: string;
    endTime?: string;
  };
  /** 问题单位名称 */
  problemDepart?: string;
  /** 处置方式（字典） */
  dealtype?: string;
  /** 问题性质（字典） */
  wtxz?: string;
  /** 整改状态 */
  state?: string;
  /** 反馈状态 */
  dealState?: string;
  /** 部门ID */
  departId?: number;
  /** 数据状态 0草稿 1提交 */
  status?: number;
  /** 审核状态 */
  aduitStatus?: number;
  /** 开始时间 */
  beginTime?: string;
  /** 结束时间 */
  endTime?: string;
}

/** 问题整改信息 */
export interface DcPga extends BaseEntity {
  /** 主键 */
  id?: number;
  /** 关联预警ID */
  dxjgId?: number;
  /** 问题单位名称 */
  problemDepart?: string;
  /** 处置方式（字典） */
  dealtype?: string;
  /** 问题性质（字典） */
  wtxz?: string;
  /** 整改要求（字典） */
  adjust?: string;
  /** 处理意见（字典） */
  dealPropose?: string;
  /** 问题大分类（一级） */
  typeLevelone?: string;
  /** 详细问题（二级分类） */
  typeLeveltwo?: string;
  /** 问题图片 */
  pic1?: string;
  /** 问题附件 */
  fujian?: string;
  /** 设备编码 */
  camareCode?: number;
  /** 问题内容 */
  problemMsg?: string;
  /** 发现时间 */
  findtime?: string;
  /** 县局 */
  countyname?: string;
  /** 派出所 */
  policename?: string;
  /** 单位全称 */
  danweiallname?: string;
  /** 警种 */
  jz?: string;
  /** 单位编码 */
  orgcode?: string;
  /** 整改状态 */
  state?: string;
  /** 处置时间 */
  dealTime?: string;
  /** 反馈状态 */
  dealState?: string;
  /** 整改图片 */
  dealPic?: string;
  /** 反馈附件 */
  dealFujian?: string;
  /** 处置时限 */
  deadline?: string;
  /** 反馈情况 */
  dealConent?: string;
  /** 部门ID */
  departId?: number;
  /** 设备ID */
  camareId?: number;
  /** 问题大类code */
  typeLeveloneCode?: number;
  /** 详细问题code */
  typeLeveltwoCode?: number;
  /** 数据状态 */
  status?: number;
  /** 审核状态 */
  aduitStatus?: number;
  /** 审核备注 */
  remark?: string;
  /** 问题处理部门 */
  receiveDeptId?: number;
  /** 结果 */
  result?: number;
  /** 部门名称 */
  deptName?: string;
}