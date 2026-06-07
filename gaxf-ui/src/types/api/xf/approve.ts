import type { BaseEntity } from '../common'

export interface ApproveFlowStageDept {
  id?: number
  stageId?: number
  approveDeptId: number
  approveDeptName: string
}

export interface ApproveFlowStage {
  id?: number
  flowConfigId?: number
  stageNo: number
  stageName: string
  stageType?: '1'
  parallelMode: '1'
  deptList: ApproveFlowStageDept[]
}

export interface ApproveFlowConfig extends BaseEntity {
  id?: number
  flowName: string
  applyDeptId: number
  applyDeptName: string
  status: '0' | '1'
  stages: ApproveFlowStage[]
}

export interface ApproveTask extends BaseEntity {
  id: number
  instanceId: number
  orderId: number
  stageNo: number
  stageName: string
  taskType?: '0' | '1'
  undertakeDeptId?: number
  undertakeDeptName?: string
  approveDeptId: number
  approveDeptName: string
  reviewerId?: number
  reviewerName?: string
  opinion?: string
  status: '0' | '1' | '2'
  actionTime?: string
  orderTitle?: string
  xfDemand?: string
  xfCaseNo?: string
}

export interface ApproveProgress {
  instanceId: number
  flowName: string
  status: '0' | '1' | '2'
  currentStageNo: number
  taskList: ApproveTask[]
  branchTaskList?: ApproveTask[]
}

export interface ApproveActionBody {
  taskId: number
  opinion: string
}
