<template>
  <div class="my-todo-wrapper">
    <div class="main-card">
      <!-- 头部区域 -->
      <div class="card-header">
        <div class="header-left">
          <div class="title-section">
            <LucideListTodo class="title-icon" />
            <h2 class="title-text">我的待办</h2>
          </div>
          <div class="filter-pills">
            <button
              v-for="(f, i) in filters"
              :key="i"
              :class="['pill-btn', { active: currentFilter === i }]"
              @click="currentFilter = i"
            >
              <span class="pill-dot" v-if="currentFilter === i"></span>
              {{ f }}
            </button>
          </div>
        </div>
      </div>

      <!-- 统计概览 -->
      <div class="stats-section">
        <div class="stat-chip pending">
          <LucideClock :size="16" />
          <span class="chip-value">{{ stats.pendingCount }}</span>
          <span class="chip-label">待接收</span>
        </div>
        <div class="stat-chip progress">
          <LucideLoader :size="16" />
          <span class="chip-value">{{ stats.inProgressCount }}</span>
          <span class="chip-label">办理中</span>
        </div>
        <div class="stat-chip reported">
          <LucideSend :size="16" />
          <span class="chip-value">{{ stats.reportedCount }}</span>
          <span class="chip-label">已上报</span>
        </div>
      </div>

      <!-- 卡片列表 -->
      <div class="card-grid" v-loading="loading">
        <div
          v-for="item in todoList"
          :key="item.id"
          class="todo-card"
          :class="{ overdue: item.status === '5' }"
        >
          <!-- 卡片头部 -->
          <div class="card-top">
            <div class="order-info">
              <span class="order-no" :class="{ 'text-overdue': item.status === '5' }">{{ item.xfCaseNo }}</span>
              <el-tag v-if="item.status === '5'" type="danger" size="small">超期</el-tag>
            </div>
            <span class="status-badge" :class="getStatusClass(item.status)">
              {{ getStatusText(item.status) }}
            </span>
          </div>

          <!-- 卡片主体 -->
          <div class="card-body">
            <h4 class="card-title" :class="{ 'text-overdue': item.status === '5' }">{{ item.title }}</h4>
            <div class="info-row">
              <LucideUser :size="14" class="row-icon" />
              <span class="row-label">信访人</span>
              <span class="row-value">{{ item.petitionerName || '-' }}</span>
            </div>
            <div class="info-row">
              <LucideCalendar :size="14" class="row-icon" />
              <span class="row-label">期限</span>
              <span class="row-value">{{ item.deadline || '-' }}</span>
            </div>
            <div class="info-row">
              <LucideClock :size="14" class="row-icon" />
              <span class="row-label">登记</span>
              <span class="row-value">{{ formatTime(item.registerTime) }}</span>
            </div>
            <div class="info-row" v-if="item.xfForm">
              <LucideFileText :size="14" class="row-icon" />
              <span class="row-label">形式</span>
              <span class="row-value">{{ item.xfForm }}</span>
            </div>
            <div class="info-row" v-if="item.businessCategory">
              <LucideClipboardList :size="14" class="row-icon" />
              <span class="row-label">分类</span>
              <span class="row-value">{{ item.businessCategory }}</span>
            </div>
          </div>

          <!-- 操作按钮 -->
          <div class="card-actions">
            <button
              v-if="item.status === '0' || item.status === '1'"
              :class="['action-btn', item.status === '0' ? 'primary' : 'disabled']"
              :disabled="item.status === '1'"
              @click="item.status === '0' && handleReceive(item)"
            >
              <LucideCheckCircle :size="14" />
              <span>{{ item.status === '0' ? '接收' : '已接收' }}</span>
            </button>
            <button
              v-if="item.status === '1'"
              class="action-btn success"
              @click="handleReport(item)"
            >
              <LucideFileText :size="14" />
              <span>填写报告</span>
            </button>
            <button class="action-btn" @click="handleDetail(item)">
              <LucideEye :size="14" />
              <span>详情</span>
            </button>
          </div>
        </div>

        <!-- 空状态 -->
        <div v-if="!loading && todoList.length === 0" class="empty-state">
          <LucideInbox :size="48" class="empty-icon" />
          <span class="empty-text">暂无待办工单</span>
        </div>
      </div>

      <!-- 分页 -->
      <div class="pagination-section" v-if="total > 0">
        <div class="page-stats">共 <strong>{{ total }}</strong> 条记录</div>
        <el-pagination
          v-model:current-page="pageNum"
          v-model:page-size="pageSize"
          :page-sizes="[10, 20, 50]"
          :total="total"
          layout="prev, pager, next, sizes"
          @size-change="loadTodoList"
          @current-change="loadTodoList"
        />
      </div>
    </div>

    <!-- 填写报告对话框 -->
    <el-dialog v-model="showReportDialog" title="填写办理报告" width="700px" class="xf-dialog">
      <div class="report-order-info" v-if="currentTodo">
        <div class="info-row"><span class="info-label">信访件编号：</span><span>{{ currentTodo.xfCaseNo }}</span></div>
        <div class="info-row"><span class="info-label">信访诉求：</span><span>{{ currentTodo.xfDemand }}</span></div>
      </div>
      <el-form :model="reportForm" label-width="120px" ref="reportFormRef">
        <el-form-item label="办理报告" prop="reportFile">
          <el-upload
            ref="reportFileRef"
            :auto-upload="false"
            :limit="1"
            accept=".doc,.docx,.pdf"
            :on-change="handleReportFileChange"
            :on-exceed="handleReportFileExceed"
          >
            <el-button type="primary" plain size="small">选择文件</el-button>
            <template #tip><div class="el-upload__tip">支持 Word、PDF 格式</div></template>
          </el-upload>
        </el-form-item>
        <el-form-item label="案件电子卷宗">
          <el-upload
            ref="dossierFileRef"
            :auto-upload="false"
            :limit="5"
            accept=".pdf,.jpg,.jpeg,.png,.zip,.rar"
            :on-change="handleDossierFileChange"
            multiple
          >
            <el-button type="primary" plain size="small">选择文件</el-button>
            <template #tip><div class="el-upload__tip">支持 PDF、图片、压缩包格式</div></template>
          </el-upload>
        </el-form-item>
        <el-form-item label="办理意见" prop="reportContent">
          <el-input v-model="reportForm.reportContent" type="textarea" :rows="4" placeholder="请输入办理意见" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="showReportDialog = false">取消</el-button>
        <el-button type="primary" @click="submitReport" :loading="reportSubmitting">提交报告</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted, watch } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import {
  LucideListTodo, LucideClock, LucideLoader, LucideSend,
  LucideUser, LucideCalendar, LucideCheckCircle, LucideFileText,
  LucideEye, LucideInbox, LucideClipboardList
} from 'lucide-vue-next'
import { myTodoList, myTodoStatistics } from '@/api/xf/workOrder'
import { receiveAssign, submitReport as submitReportApi } from '@/api/xf/assign'
import type { XfWorkOrder, WorkOrderQueryParams } from '@/types/api/xf/workOrder'

const router = useRouter()

const loading = ref(false)
const currentFilter = ref(0)
const filters = ['全部', '待接收', '办理中', '已上报']

const todoList = ref<XfWorkOrder[]>([])
const total = ref(0)
const pageNum = ref(1)
const pageSize = ref(10)
const queryParams = ref<WorkOrderQueryParams>({ pageNum: 1, pageSize: 10 })

const stats = ref({ pendingCount: 0, inProgressCount: 0, reportedCount: 0 })

async function loadStats() {
  try {
    const res = await myTodoStatistics()
    if (res.data) {
      stats.value = {
        pendingCount: (res.data as any).pendingCount || 0,
        inProgressCount: (res.data as any).inProgressCount || 0,
        reportedCount: (res.data as any).reportedCount || 0
      }
    }
  } catch (error) {
    console.error('加载统计失败:', error)
  }
}

const showReportDialog = ref(false)
const currentTodo = ref<XfWorkOrder | null>(null)
const reportForm = ref({ id: 0, reportContent: '' })
const reportFileRef = ref()
const dossierFileRef = ref()
const reportFile = ref<File | null>(null)
const dossierFiles = ref<File[]>([])
const reportSubmitting = ref(false)

watch(currentFilter, (val) => {
  queryParams.value.status = undefined
  if (val === 1) queryParams.value.status = '0'
  else if (val === 2) queryParams.value.status = '1'
  else if (val === 3) queryParams.value.status = '2'
  pageNum.value = 1
  loadTodoList()
})

async function loadTodoList() {
  loading.value = true
  try {
    queryParams.value.pageNum = pageNum.value
    queryParams.value.pageSize = pageSize.value
    const res = await myTodoList(queryParams.value)
    todoList.value = (res.rows || []) as XfWorkOrder[]
    total.value = res.total || 0
  } catch (error) {
    ElMessage.error('加载待办列表失败')
  } finally {
    loading.value = false
  }
  loadStats()
}

async function handleReceive(item: XfWorkOrder) {
  if (!item.assignId) {
    ElMessage.warning('交办记录不存在')
    return
  }
  try {
    await receiveAssign(item.assignId)
    ElMessage.success('接收成功')
    loadTodoList()
  } catch (error) {
    ElMessage.error('接收失败')
  }
}

function handleReport(item: XfWorkOrder) {
  currentTodo.value = item
  reportForm.value = { id: item.assignId || 0, reportContent: '' }
  reportFile.value = null
  dossierFiles.value = []
  showReportDialog.value = true
}

async function submitReport() {
  if (!reportFile.value) {
    ElMessage.warning('请上传办理报告')
    return
  }
  if (!reportForm.value.reportContent) {
    ElMessage.warning('请填写办理意见')
    return
  }
  reportSubmitting.value = true
  try {
    const formData = new FormData()
    formData.append('id', String(reportForm.value.id))
    formData.append('reportContent', reportForm.value.reportContent)
    formData.append('reportFile', reportFile.value)
    dossierFiles.value.forEach(file => {
      formData.append('dossierFiles', file)
    })
    await submitReportApi(formData)
    ElMessage.success('报告提交成功')
    showReportDialog.value = false
    loadTodoList()
  } catch (error) {
    ElMessage.error('提交报告失败')
  } finally {
    reportSubmitting.value = false
  }
}

function handleReportFileChange(file: any) {
  reportFile.value = file.raw
}

function handleReportFileExceed() {
  ElMessage.warning('只能上传一个办理报告文件')
}

function handleDossierFileChange(file: any, fileList: any[]) {
  dossierFiles.value = fileList.map((f: any) => f.raw)
}

function handleDetail(item: XfWorkOrder) {
  router.push({ name: 'XfWorkOrderDetail', params: { id: item.id } })
}

function getStatusClass(status: string | undefined) {
  if (!status) return ''
  const map: Record<string, string> = {
    '0': 'status-pending', '1': 'status-progress', '2': 'status-reported', '5': 'status-overdue'
  }
  return map[status] || ''
}

function getStatusText(status: string | undefined) {
  if (!status) return '未知'
  const map: Record<string, string> = {
    '0': '待接收', '1': '办理中', '2': '已上报', '5': '已超期'
  }
  return map[status] || status
}

function formatTime(time: string | undefined) {
  if (!time) return ''
  return time.replace('T', ' ').substring(0, 19)
}

onMounted(() => {
  loadTodoList()
})
</script>

<style lang="scss" scoped>
$police-primary: #1E3A8A;
$police-blue: #2563EB;
$police-light-blue: #3B82F6;
$police-bg: #F0F4F8;
$police-card: #FFFFFF;
$police-border: #E2E8F0;
$police-text: #1E293B;
$police-text-muted: #64748B;

.my-todo-wrapper {
  padding: 24px;
  background: $police-bg;
  min-height: calc(100vh - 48px);
}

.main-card {
  background: white;
  border-radius: 20px;
  border: 1px solid $police-border;
  overflow: hidden;
  box-shadow: 0 4px 20px rgba(30, 58, 138, 0.08);
  display: flex;
  flex-direction: column;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 20px 24px;
  background: linear-gradient(135deg, $police-primary 0%, $police-blue 100%);
  color: white;
  flex-shrink: 0;
}

.header-left { display: flex; align-items: center; gap: 24px; flex-wrap: wrap; }

.title-section {
  display: flex;
  align-items: center;
  gap: 12px;
  .title-icon { width: 24px; height: 24px; color: white; }
  .title-text { font-size: 18px; font-weight: 700; margin: 0; letter-spacing: 1px; }
}

.filter-pills { display: flex; gap: 8px; }

.pill-btn {
  padding: 6px 16px;
  border-radius: 20px;
  font-size: 13px;
  font-weight: 500;
  border: 1px solid rgba(255, 255, 255, 0.3);
  background: transparent;
  color: rgba(255, 255, 255, 0.8);
  cursor: pointer;
  transition: all 0.2s;
  display: flex;
  align-items: center;
  gap: 6px;

  &:hover { background: rgba(255, 255, 255, 0.1); }
  &.active {
    background: white; color: $police-primary; border-color: white;
    .pill-dot { width: 6px; height: 6px; background: $police-blue; border-radius: 50%; }
  }
}

.stats-section {
  display: flex;
  gap: 16px;
  padding: 16px 24px;
  background: #F8FAFE;
  border-bottom: 1px solid $police-border;
  flex-shrink: 0;
}

.stat-chip {
  display: flex;
  align-items: center;
  gap: 8px;
  padding: 8px 16px;
  border-radius: 12px;
  background: white;
  border: 1px solid $police-border;

  .chip-value { font-size: 16px; font-weight: 700; color: $police-text; }
  .chip-label { font-size: 12px; color: $police-text-muted; }

  &.pending { border-color: #FEF3C7; svg { color: #F59E0B; } }
  &.progress { border-color: #DBEAFE; svg { color: #3B82F6; } }
  &.reported { border-color: #E0E7FF; svg { color: #4F46E5; } }
}

.card-grid {
  padding: 24px;
  display: grid;
  grid-template-columns: repeat(3, 1fr);
  gap: 20px;
  flex: 1;
}

.todo-card {
  background: white;
  border-radius: 16px;
  border: 1px solid $police-border;
  overflow: hidden;
  transition: all 0.3s;
  display: flex;
  flex-direction: column;

  &:hover { transform: translateY(-4px); box-shadow: 0 8px 24px rgba(30, 58, 138, 0.12); border-color: $police-light-blue; }
  &.overdue { border-color: #FEE2E2; .card-top { background: linear-gradient(135deg, #FEE2E2 0%, #FECACA 100%); } }
}

.card-top {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 14px 16px;
  background: linear-gradient(135deg, #F8FAFE 0%, #EFF6FF 100%);
  border-bottom: 1px solid $police-border;
}

.order-info {
  display: flex;
  align-items: center;
  gap: 8px;

  .order-no { font-size: 13px; font-weight: 600; color: $police-text; }
  .order-no.text-overdue { color: #F56C6C; }
}

.status-badge {
  padding: 4px 12px;
  border-radius: 12px;
  font-size: 12px;
  font-weight: 600;

  &.status-pending { background: #FEF3C7; color: #D97706; }
  &.status-progress { background: #DBEAFE; color: #2563EB; }
  &.status-reported { background: #E0E7FF; color: #4F46E5; }
  &.status-overdue { background: #FEE2E2; color: #DC2626; }
}

.card-body { padding: 14px 16px; }

.card-title {
  font-size: 14px;
  font-weight: 600;
  color: $police-text;
  margin: 0 0 12px 0;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;

  &.text-overdue { color: #F56C6C; }
}

.info-row {
  display: flex;
  align-items: center;
  gap: 8px;
  padding: 4px 0;

  .row-icon { color: $police-text-muted; flex-shrink: 0; }
  .row-label { font-size: 12px; color: $police-text-muted; width: 32px; }
  .row-value { font-size: 13px; color: $police-text; font-weight: 500; }
}

.card-actions {
  display: flex;
  gap: 10px;
  padding: 12px 16px;
  justify-content: center;
  border-top: 1px solid $police-border;

  .action-btn {
    padding: 8px 18px;
    border-radius: 12px;
    font-size: 13px;
    font-weight: 500;
    border: 1px solid $police-border;
    background: white;
    color: $police-text;
    cursor: pointer;
    transition: all 0.2s;
    display: flex;
    align-items: center;
    gap: 6px;

    &:hover { background: #F5F5F5; }
    &.primary { background: $police-blue; color: white; border-color: $police-blue; &:hover { background: $police-light-blue; } }
    &.success { background: #10B981; color: white; border-color: #10B981; &:hover { background: #059669; } }
    &.disabled { background: #E2E8F0; color: #94A3B8; border-color: #E2E8F0; cursor: not-allowed; &:hover { background: #E2E8F0; } }
  }
}

.empty-state {
  grid-column: 1 / -1;
  display: flex;
  flex-direction: column;
  align-items: center;
  padding: 60px 20px;
  color: $police-text-muted;
  .empty-icon { margin-bottom: 16px; opacity: 0.5; }
  .empty-text { font-size: 14px; }
}

.pagination-section {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 16px 24px;
  background: #F8FAFE;
  border-top: 1px solid $police-border;
  flex-shrink: 0;
  .page-stats { font-size: 13px; color: $police-text-muted; strong { color: $police-text; } }
}

.report-order-info {
  background: #F0F9FF;
  border: 1px solid #BAE6FD;
  border-radius: 12px;
  padding: 16px 20px;
  margin-bottom: 20px;
  .info-row { display: flex; padding: 6px 0; .info-label { width: 100px; color: $police-text-muted; } span { color: $police-text; } }
}

.xf-dialog {
  :deep(.el-dialog) { border-radius: 16px; }
  :deep(.el-dialog__header) {
    background: linear-gradient(135deg, $police-primary 0%, $police-blue 100%);
    color: white; border-radius: 16px 16px 0 0; padding: 20px 24px; margin: 0;
    .el-dialog__title { color: white; font-weight: 600; font-size: 18px; }
    .el-dialog__headerbtn { top: 20px; right: 24px; .el-dialog__close { color: white; font-size: 20px; } }
  }
  :deep(.el-dialog__body) { padding: 28px 32px; background: white; }
  :deep(.el-dialog__footer) { padding: 20px 32px; background: white; border-top: 1px solid $police-border; border-radius: 0 0 16px 16px; }
}

@media (max-width: 1200px) { .card-grid { grid-template-columns: repeat(2, 1fr); } }
@media (max-width: 768px) { .card-grid { grid-template-columns: 1fr; } }
</style>
