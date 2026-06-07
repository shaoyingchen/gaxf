<template>
  <div class="approve-todo-wrapper">
    <div class="main-card">
      <div class="card-header">
        <h2 class="title-text">审批待办</h2>
      </div>

      <el-tabs v-model="activeTab" class="approve-tabs">
        <el-tab-pane name="pending">
          <template #label>
            <div class="tab-label">
              <LucideClock :size="14" />
              <span>待审批</span>
            </div>
          </template>
          <el-table :data="pendingList" v-loading="loading">
            <el-table-column prop="xfCaseNo" label="信访件编号" width="160" />
            <el-table-column prop="xfDemand" label="信访诉求" min-width="220">
              <template #default="{ row }">{{ row.xfDemand || '-' }}</template>
            </el-table-column>
            <el-table-column prop="stageName" label="当前阶段" width="140" />
            <el-table-column prop="undertakeDeptName" label="承办单位" width="150">
              <template #default="{ row }">{{ row.undertakeDeptName || '-' }}</template>
            </el-table-column>
            <el-table-column prop="approveDeptName" label="审批部门" width="140" />
            <el-table-column label="操作" width="180">
              <template #default="{ row }">
                <el-button link type="primary" @click="openDialog(row, 'pass')">通过</el-button>
                <el-button link type="danger" @click="openDialog(row, 'reject')">驳回</el-button>
                <el-button link @click="goDetail(row.orderId)">详情</el-button>
              </template>
            </el-table-column>
          </el-table>
        </el-tab-pane>
        <el-tab-pane name="handled">
          <template #label>
            <div class="tab-label">
              <LucideCheckCircle :size="14" />
              <span>我已审批</span>
            </div>
          </template>
          <el-table :data="handledList" v-loading="loading">
            <el-table-column prop="xfCaseNo" label="信访件编号" width="160" />
            <el-table-column prop="xfDemand" label="信访诉求" min-width="220">
              <template #default="{ row }">{{ row.xfDemand || '-' }}</template>
            </el-table-column>
            <el-table-column prop="stageName" label="当前阶段" width="140" />
            <el-table-column prop="undertakeDeptName" label="承办单位" width="150">
              <template #default="{ row }">{{ row.undertakeDeptName || '-' }}</template>
            </el-table-column>
            <el-table-column prop="reviewerName" label="审批人" width="120" />
            <el-table-column label="结果" width="100">
              <template #default="{ row }">
                <el-tag :type="row.status === '1' ? 'success' : 'danger'">{{ row.status === '1' ? '通过' : '驳回' }}</el-tag>
              </template>
            </el-table-column>
            <el-table-column prop="actionTime" label="审批时间" min-width="180" />
          </el-table>
        </el-tab-pane>
      </el-tabs>
    </div>

    <el-dialog v-model="showDialog" :title="dialogAction === 'pass' ? '审批通过' : '审批驳回'" width="560px">
      <el-form :model="actionForm" label-width="90px">
        <el-form-item label="审批意见">
          <el-input v-model="actionForm.opinion" type="textarea" :rows="4" placeholder="请输入审批意见" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="showDialog = false">取消</el-button>
        <el-button type="primary" @click="submitAction">确认</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { onMounted, ref } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { LucideClock, LucideCheckCircle } from 'lucide-vue-next'
import { getApproveTodo, passApproveTask, rejectApproveTask } from '@/api/xf/approve'
import type { ApproveTask } from '@/types/api/xf/approve'

const router = useRouter()
const loading = ref(false)
const activeTab = ref('pending')
const pendingList = ref<ApproveTask[]>([])
const handledList = ref<ApproveTask[]>([])
const showDialog = ref(false)
const dialogAction = ref<'pass' | 'reject'>('pass')
const actionForm = ref({ taskId: 0, opinion: '' })

async function loadTodo() {
  loading.value = true
  try {
    const res = await getApproveTodo()
    pendingList.value = ((res.data as any)?.pending || []) as ApproveTask[]
    handledList.value = ((res.data as any)?.handled || []) as ApproveTask[]
  } finally {
    loading.value = false
  }
}

function openDialog(row: ApproveTask, action: 'pass' | 'reject') {
  dialogAction.value = action
  actionForm.value = { taskId: row.id, opinion: '' }
  showDialog.value = true
}

async function submitAction() {
  if (!actionForm.value.opinion) {
    ElMessage.warning('请输入审批意见')
    return
  }
  if (dialogAction.value === 'pass') {
    await passApproveTask(actionForm.value)
    ElMessage.success('审批通过成功')
  } else {
    await rejectApproveTask(actionForm.value)
    ElMessage.success('审批驳回成功')
  }
  showDialog.value = false
  loadTodo()
}

function goDetail(orderId: number) {
  router.push({ name: 'XfWorkOrderDetail', params: { id: orderId } })
}

onMounted(() => {
  loadTodo()
})
</script>

<style scoped>
.approve-todo-wrapper { padding: 0; background: #F0F4F8; min-height: calc(100vh - 48px); }
.main-card { background: #fff; border-radius: 12px; border: 1px solid #E2E8F0; overflow: hidden; box-shadow: 0 2px 12px rgba(30, 58, 138, 0.06); min-height: calc(100vh - 48px); display: flex; flex-direction: column; }
.card-header { padding: 16px 18px; background: linear-gradient(135deg, #1E3A8A 0%, #2563EB 100%); color: white; flex-shrink: 0; }
.title-text { margin: 0; font-size: 18px; font-weight: 700; color: white; }
:deep(.el-tabs__content) { padding: 12px 0 0; flex: 1; overflow: auto; }
:deep(.el-table) { border-radius: 0; }

/* Tabs 样式优化 */
.approve-tabs {
  display: flex;
  flex-direction: column;
  flex: 1;
}

:deep(.approve-tabs .el-tabs__header) {
  margin: 0;
  background: #F8FAFE;
  border-bottom: 1px solid #E2E8F0;
  padding: 0 18px;
}

:deep(.approve-tabs .el-tabs__nav-wrap::after) {
  display: none;
}

:deep(.approve-tabs .el-tabs__item) {
  height: 44px;
  font-size: 14px;
  font-weight: 500;
  color: #64748B;
  transition: all 0.2s;
  padding: 0 16px;
}

:deep(.approve-tabs .el-tabs__item:hover) {
  color: #2563EB;
}

:deep(.approve-tabs .el-tabs__item.is-active) {
  color: #2563EB;
  font-weight: 600;
}

:deep(.approve-tabs .el-tabs__active-bar) {
  background-color: #2563EB;
  height: 3px;
  border-radius: 2px;
}

.tab-label {
  display: flex;
  align-items: center;
  gap: 6px;
}
</style>
