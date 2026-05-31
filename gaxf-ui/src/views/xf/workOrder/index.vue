<template>
  <div class="work-order-wrapper">
    <div class="main-card">
      <!-- 头部区域：标题 + 筛选 -->
      <div class="card-header">
        <div class="header-left">
          <div class="title-section">
            <LucideClipboardList class="title-icon" />
            <h2 class="title-text">工单管理</h2>
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
        <div class="header-actions">
          <button class="action-btn primary" @click="handleAdd">
            <LucidePlus :size="16" />
            <span>新建工单</span>
          </button>
          <button class="action-btn" @click="showImportDialog = true">
            <LucideUpload :size="16" />
            <span>导入Excel</span>
          </button>
          <button class="action-btn" @click="showFilterDialog = true">
            <LucideSlidersHorizontal :size="16" />
            <span>筛选</span>
          </button>
          <button class="action-btn" @click="handleExport">
            <LucideDownload :size="16" />
            <span>导出</span>
          </button>
        </div>
      </div>

      <!-- 统计概览 -->
      <div class="stats-section">
        <div class="stat-chip pending">
          <LucideClock :size="16" />
          <span class="chip-value">{{ stats.pendingCount }}</span>
          <span class="chip-label">待派单</span>
        </div>
        <div class="stat-chip progress">
          <LucideLoader :size="16" />
          <span class="chip-value">{{ stats.inProgressCount }}</span>
          <span class="chip-label">办理中</span>
        </div>
        <div class="stat-chip overdue">
          <LucideAlertTriangle :size="16" />
          <span class="chip-value">{{ stats.overdueCount }}</span>
          <span class="chip-label">已超期</span>
        </div>
        <div class="stat-chip done">
          <LucideCheckCircle :size="16" />
          <span class="chip-value">{{ stats.completedCount }}</span>
          <span class="chip-label">已办结</span>
        </div>
        <div class="stat-chip returned">
          <LucideRotateCcw :size="16" />
          <span class="chip-value">{{ stats.returnedCount }}</span>
          <span class="chip-label">已退回</span>
        </div>
      </div>

      <!-- 工单表格 -->
      <div class="table-section" v-loading="loading">
        <el-table :data="workOrderList" style="width: 100%" :row-class-name="tableRowClassName">
          <el-table-column type="index" label="#" width="50" />
          <el-table-column prop="xfCaseNo" label="信访件编号" width="150">
            <template #default="{ row }">
              <span class="order-no" :class="getOrderNoClass(row)">{{ row.xfCaseNo }}</span>
            </template>
          </el-table-column>
          <el-table-column prop="petitionerName" label="信访人" width="100" />
          <el-table-column prop="xfForm" label="信访形式" width="100">
            <template #default="{ row }">
              {{ row.xfForm || '-' }}
            </template>
          </el-table-column>
          <el-table-column prop="businessCategory" label="公安业务分类" width="120" show-overflow-tooltip>
            <template #default="{ row }">
              {{ row.businessCategory || '-' }}
            </template>
          </el-table-column>
          <el-table-column prop="xfDemand" label="信访诉求" min-width="200" show-overflow-tooltip>
            <template #default="{ row }">
              <span :class="getOrderNoClass(row)">{{ row.xfDemand || '-' }}</span>
              <el-tag v-if="row.isTop === '1'" type="danger" size="small" class="top-tag">置顶</el-tag>
              <el-tag v-if="row.status === '5'" type="danger" size="small" class="overdue-tag">
                超期{{ row.overdueCount }}次
              </el-tag>
            </template>
          </el-table-column>
          <el-table-column prop="status" label="状态" width="100">
            <template #default="{ row }">
              <span class="status-badge" :class="getStatusClass(row.status)">
                {{ getStatusText(row.status) }}
              </span>
            </template>
          </el-table-column>
          <el-table-column prop="registerTime" label="登记时间" width="120">
            <template #default="{ row }">
              {{ formatDate(row.registerTime) }}
            </template>
          </el-table-column>
          <el-table-column prop="deadline" label="办理期限" width="120">
            <template #default="{ row }">
              {{ formatDate(row.deadline) }}
            </template>
          </el-table-column>
          <el-table-column label="操作" width="120" fixed="right">
            <template #default="{ row }">
              <div class="table-actions">
                <el-tooltip content="详情" placement="top">
                  <button class="action-icon-btn" @click.stop="handleDetail(row)">
                    <LucideEye :size="14" />
                  </button>
                </el-tooltip>
                <el-tooltip v-if="row.status === '0'" content="交办" placement="top">
                  <button class="action-icon-btn assign" @click.stop="handleAssign(row)">
                    <LucideSend :size="14" />
                  </button>
                </el-tooltip>
                <el-tooltip v-if="row.status === '0'" content="删除" placement="top">
                  <button class="action-icon-btn danger" @click.stop="handleDelete(row)">
                    <LucideTrash2 :size="14" />
                  </button>
                </el-tooltip>
              </div>
            </template>
          </el-table-column>
        </el-table>
      </div>

      <!-- 分页 -->
      <div class="pagination-section" v-if="total > 0">
        <div class="page-stats">
          共 <strong>{{ total }}</strong> 条记录
        </div>
        <el-pagination
          v-model:current-page="pageNum"
          v-model:page-size="pageSize"
          :page-sizes="[10, 20, 50]"
          :total="total"
          layout="prev, pager, next, sizes"
          @size-change="loadWorkOrderList"
          @current-change="loadWorkOrderList"
        />
      </div>
    </div>

    <!-- 新建工单对话框 -->
    <el-dialog v-model="showAddDialog" title="新建工单" width="750px" class="xf-dialog" :close-on-click-modal="false">
      <el-form :model="addForm" :rules="addFormRules" label-width="100px" ref="addFormRef">
        <el-form-item label="工单标题" prop="title">
          <el-input v-model="addForm.title" placeholder="请输入工单标题" />
        </el-form-item>
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="信访人姓名" prop="petitionerName">
              <el-input v-model="addForm.petitionerName" placeholder="请输入信访人姓名" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="信访人电话">
              <el-input v-model="addForm.petitionerPhone" placeholder="请输入电话" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="身份证号">
              <el-input v-model="addForm.petitionerIdcard" placeholder="请输入身份证号" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="信访形式" prop="xfForm">
              <el-select v-model="addForm.xfForm" placeholder="请选择信访形式" style="width: 100%">
                <el-option label="来信" value="来信" />
                <el-option label="来访" value="来访" />
                <el-option label="网上信访" value="网上信访" />
                <el-option label="电话信访" value="电话信访" />
                <el-option label="其他" value="其他" />
              </el-select>
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="信访日期">
              <el-date-picker v-model="addForm.xfDate" type="date" placeholder="选择信访日期" value-format="YYYY-MM-DD" style="width: 100%" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="信访人数">
              <el-input v-model="addForm.petitionerCount" type="number" placeholder="请输入信访人数" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-form-item label="信访人地址">
          <el-input v-model="addForm.petitionerAddress" placeholder="请输入信访人地址" />
        </el-form-item>
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="问题发生地">
              <el-input v-model="addForm.problemLocation" placeholder="请输入问题发生地" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="信访目的">
              <el-input v-model="addForm.xfPurpose" placeholder="请输入信访目的" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="公安业务分类">
              <el-input v-model="addForm.businessCategory" placeholder="请输入公安业务分类" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="警种名称">
              <el-input v-model="addForm.policeTypeName" placeholder="请输入警种名称" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-form-item label="信访诉求">
          <el-input v-model="addForm.xfDemand" type="textarea" :rows="2" placeholder="请输入信访诉求" />
        </el-form-item>
        <el-form-item label="信访内容" prop="xfContent">
          <el-input v-model="addForm.xfContent" type="textarea" :rows="2" placeholder="请输入信访内容" />
        </el-form-item>
        <el-form-item label="工单内容" prop="content">
          <QuillEditor v-model:content="addForm.content" content-type="html" :toolbar="editorToolbar" placeholder="请输入工单详细内容" style="height: 200px; margin-bottom: 40px;" />
        </el-form-item>
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="办理期限">
              <el-date-picker v-model="addForm.deadline" type="date" placeholder="选择办理期限" value-format="YYYY-MM-DD" style="width: 100%" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="登记时间">
              <el-date-picker v-model="addForm.registerTime" type="datetime" placeholder="选择登记时间" value-format="YYYY-MM-DD HH:mm:ss" style="width: 100%" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-form-item label="备注">
          <el-input v-model="addForm.remark" type="textarea" :rows="2" placeholder="请输入备注" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="showAddDialog = false">取消</el-button>
        <el-button type="primary" @click="submitAdd">确认新建</el-button>
      </template>
    </el-dialog>

    <!-- 导入Excel对话框 -->
    <el-dialog v-model="showImportDialog" title="导入工单" width="500px" class="xf-dialog">
      <div class="import-section">
        <el-upload
          ref="uploadRef"
          :auto-upload="false"
          :limit="1"
          accept=".xlsx,.xls"
          :on-change="handleFileChange"
          :on-exceed="handleExceed"
          drag
          :disabled="importLoading"
        >
          <div class="upload-drag-content">
            <LucideFileSpreadsheet :size="48" class="upload-icon" />
            <div class="el-upload__text">将Excel文件拖到此处，或<em>点击上传</em></div>
          </div>
          <template #tip>
            <div class="el-upload__tip">仅支持 .xlsx / .xls 格式文件</div>
          </template>
        </el-upload>
        <!-- 导入进度 -->
        <div v-if="importProgress" class="import-progress">
          <el-progress
            :percentage="importProgress.total > 0 ? Math.round((importProgress.success + importProgress.fail) / importProgress.total * 100) : 0"
            :status="importProgress.fail > 0 ? 'exception' : (importProgress.status === 'done' ? 'success' : undefined)"
            :stroke-width="20"
            :text-inside="true"
          />
          <div class="progress-detail">
            <span>总计 {{ importProgress.total }} 条</span>
            <span class="success-text">成功 {{ importProgress.success }} 条</span>
            <span v-if="importProgress.fail > 0" class="fail-text">失败 {{ importProgress.fail }} 条</span>
          </div>
        </div>
      </div>
      <template #footer>
        <el-button @click="showImportDialog = false">取消</el-button>
        <el-button type="primary" @click="submitImport" :loading="importLoading">确认导入</el-button>
      </template>
    </el-dialog>

    <!-- 交办派单对话框 -->
    <el-dialog v-model="showAssignDialog" title="交办派单" width="650px" class="xf-dialog">
      <div class="assign-order-info" v-if="currentOrder">
        <div class="info-row"><span class="info-label">信访件编号：</span><span>{{ currentOrder.xfCaseNo }}</span></div>
        <div class="info-row"><span class="info-label">信访诉求：</span><span>{{ currentOrder.xfDemand }}</span></div>
      </div>
      <el-form :model="assignForm" :rules="assignFormRules" label-width="100px" ref="assignFormRef">
        <el-form-item label="承办单位" prop="deptIds">
          <div class="dept-tree-wrapper">
            <el-input
              v-model="deptFilterText"
              placeholder="输入关键字筛选"
              clearable
              class="dept-filter-input"
            />
            <div class="dept-tree-scroll" v-loading="deptTreeLoading">
              <el-tree
                ref="deptTreeRef"
                :data="deptTreeData"
                :props="{ label: 'label', children: 'children' }"
                node-key="id"
                show-checkbox
                check-strictly
                :default-expand-all="true"
                :filter-node-method="filterDeptNode"
                @check="handleDeptCheck"
              />
              <el-empty v-if="!deptTreeLoading && deptTreeData.length === 0" description="暂无部门数据" :image-size="60" />
            </div>
          </div>
        </el-form-item>
        <el-form-item label="办理期限" prop="deadline">
          <el-date-picker v-model="assignForm.deadline" type="date" placeholder="选择办理期限" value-format="YYYY-MM-DD" style="width: 100%" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="showAssignDialog = false">取消</el-button>
        <el-button type="primary" @click="submitAssign">确认交办</el-button>
      </template>
    </el-dialog>

    <!-- 高级筛选对话框 -->
    <el-dialog v-model="showFilterDialog" title="高级筛选" width="480px" class="xf-dialog">
      <el-form :model="queryParams" label-width="80px">
        <el-form-item label="信访件编号">
          <el-input v-model="queryParams.xfCaseNo" placeholder="请输入信访件编号" clearable />
        </el-form-item>
        <el-form-item label="工单标题">
          <el-input v-model="queryParams.title" placeholder="请输入标题关键字" clearable />
        </el-form-item>
        <el-form-item label="信访形式">
          <el-select v-model="queryParams.xfForm" placeholder="请选择" clearable>
            <el-option label="来信" value="来信" />
            <el-option label="来访" value="来访" />
            <el-option label="网上信访" value="网上信访" />
            <el-option label="电话信访" value="电话信访" />
            <el-option label="其他" value="其他" />
          </el-select>
        </el-form-item>
        <el-form-item label="业务分类">
          <el-input v-model="queryParams.businessCategory" placeholder="请输入公安业务分类" clearable />
        </el-form-item>
        <el-form-item label="登记单位">
          <el-input v-model="queryParams.registerUnit" placeholder="请输入登记单位" clearable />
        </el-form-item>
        <el-form-item label="警种名称">
          <el-input v-model="queryParams.policeTypeName" placeholder="请输入警种名称" clearable />
        </el-form-item>
        <el-form-item label="来源类型">
          <el-select v-model="queryParams.sourceType" placeholder="请选择" clearable>
            <el-option label="Excel导入" value="0" />
            <el-option label="手动新建" value="1" />
          </el-select>
        </el-form-item>
        <el-form-item label="登记时间">
          <el-date-picker
            v-model="timeRange"
            type="daterange"
            range-separator="至"
            start-placeholder="开始"
            end-placeholder="结束"
            value-format="YYYY-MM-DD"
            @change="handleTimeRangeChange"
          />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="handleResetFilter">重置</el-button>
        <el-button type="primary" @click="handleFilter">筛选</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, onMounted, watch } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import { QuillEditor } from '@vueup/vue-quill'
import '@vueup/vue-quill/dist/vue-quill.snow.css'
import {
  LucideClipboardList, LucideSlidersHorizontal, LucidePlus, LucideUpload,
  LucideDownload, LucideClock, LucideLoader, LucideAlertTriangle,
  LucideCheckCircle, LucideRotateCcw, LucideEye, LucideSend,
  LucideTrash2, LucideFileSpreadsheet
} from 'lucide-vue-next'
import { listWorkOrder, addWorkOrder, delWorkOrder, assignWorkOrder, workOrderStatistics, importWorkOrder, getImportProgress } from '@/api/xf/workOrder'
import { deptTree } from '@/api/system/dept'
import type { XfWorkOrder, WorkOrderQueryParams, AssignParams } from '@/types/api/xf/workOrder'
import type { TreeSelect } from '@/types'

const router = useRouter()

const loading = ref(false)
const currentFilter = ref(0)
const filters = ['全部', '待派单', '办理中', '已超期', '已办结', '已退回']

const workOrderList = ref<XfWorkOrder[]>([])
const total = ref(0)
const pageNum = ref(1)
const pageSize = ref(10)

const queryParams = ref<WorkOrderQueryParams>({
  pageNum: 1,
  pageSize: 10
})

const timeRange = ref<[string, string] | null>(null)
const showFilterDialog = ref(false)
const showAddDialog = ref(false)
const showImportDialog = ref(false)
const showAssignDialog = ref(false)
const currentOrder = ref<XfWorkOrder | null>(null)
const importLoading = ref(false)
const importProgress = ref<{ total: number; success: number; fail: number; status: string; message: string } | null>(null)
const uploadFile = ref<File | null>(null)

const stats = ref({
  pendingCount: 0,
  inProgressCount: 0,
  overdueCount: 0,
  completedCount: 0,
  returnedCount: 0,
  totalCount: 0
})

// Rich text editor toolbar
const editorToolbar = [
  ['bold', 'italic', 'underline', 'strike'],
  [{ header: 1 }, { header: 2 }],
  [{ list: 'ordered' }, { list: 'bullet' }],
  ['clean']
]

// Add form
const addFormRef = ref()
const addForm = ref<Partial<XfWorkOrder>>({
  title: '',
  petitionerName: '',
  petitionerPhone: '',
  petitionerIdcard: '',
  petitionerAddress: '',
  xfForm: '',
  xfDate: '',
  petitionerCount: undefined,
  xfDemand: '',
  businessCategory: '',
  problemLocation: '',
  xfPurpose: '',
  policeTypeName: '',
  xfContent: '',
  content: '',
  deadline: '',
  registerTime: '',
  sourceType: '1',
  remark: ''
})
const addFormRules = {
  title: [{ required: true, message: '请输入工单标题', trigger: 'blur' }],
  petitionerName: [{ required: true, message: '请输入信访人姓名', trigger: 'blur' }],
  xfContent: [{ required: true, message: '请输入信访内容', trigger: 'blur' }]
}

// Assign form
const assignFormRef = ref()
const deptTreeRef = ref()
const deptTreeData = ref<TreeSelect[]>([])
const deptFilterText = ref('')
const deptTreeLoading = ref(false)
const assignForm = ref<AssignParams>({
  orderId: 0,
  deptIds: [],
  deadline: ''
})
const assignFormRules = {
  deptIds: [{ required: true, message: '请选择承办单位', trigger: 'change' }],
  deadline: [{ required: true, message: '请选择办理期限', trigger: 'change' }]
}

// Sort list: overdue > in-progress > completed
const sortedList = computed(() => {
  const priority: Record<string, number> = { '5': 0, '1': 1, '2': 2, '0': 3, '4': 4, '3': 5 }
  return [...workOrderList.value].sort((a, b) => {
    const aP = priority[a.status || '0'] ?? 9
    const bP = priority[b.status || '0'] ?? 9
    if (aP !== bP) return aP - bP
    // Within same status, top items first
    if (a.isTop === '1' && b.isTop !== '1') return -1
    if (a.isTop !== '1' && b.isTop === '1') return 1
    return 0
  })
})

// Watch filter changes
watch(currentFilter, (val) => {
  queryParams.value.status = undefined
  if (val === 1) queryParams.value.status = '0'
  else if (val === 2) queryParams.value.status = '1'
  else if (val === 3) queryParams.value.status = '5'
  else if (val === 4) queryParams.value.status = '3'
  else if (val === 5) queryParams.value.status = '4'
  pageNum.value = 1
  loadWorkOrderList()
})

async function loadWorkOrderList() {
  loading.value = true
  try {
    queryParams.value.pageNum = pageNum.value
    queryParams.value.pageSize = pageSize.value
    const res = await listWorkOrder(queryParams.value)
    workOrderList.value = (res.rows || []) as XfWorkOrder[]
    total.value = res.total || 0
  } catch (error) {
    console.error('加载工单列表失败:', error)
    ElMessage.error('加载工单列表失败')
  } finally {
    loading.value = false
  }
}

async function loadStatistics() {
  try {
    const res = await workOrderStatistics()
    if (res.data) {
      stats.value = res.data as any
    }
  } catch (error) {
    console.error('加载统计数据失败:', error)
  }
}

function handleTimeRangeChange(val: [string, string] | null) {
  if (val) {
    queryParams.value.params = { beginTime: val[0], endTime: val[1] }
  } else {
    queryParams.value.params = undefined
  }
}

function handleFilter() {
  showFilterDialog.value = false
  pageNum.value = 1
  loadWorkOrderList()
}

function handleResetFilter() {
  queryParams.value = { pageNum: 1, pageSize: 10 }
  timeRange.value = null
  showFilterDialog.value = false
  currentFilter.value = 0
  loadWorkOrderList()
}

function handleAdd() {
  addForm.value = {
    title: '', petitionerName: '', petitionerPhone: '', petitionerIdcard: '',
    petitionerAddress: '', xfForm: '', xfDate: '', petitionerCount: undefined,
    xfDemand: '', businessCategory: '', problemLocation: '', xfPurpose: '',
    policeTypeName: '', xfContent: '', content: '',
    deadline: '', registerTime: '', sourceType: '1', remark: ''
  }
  showAddDialog.value = true
}

async function submitAdd() {
  try {
    await addFormRef.value.validate()
  } catch { return }

  try {
    await addWorkOrder(addForm.value as XfWorkOrder)
    ElMessage.success('新建工单成功')
    showAddDialog.value = false
    loadWorkOrderList()
    loadStatistics()
  } catch (error) {
    ElMessage.error('新建工单失败')
  }
}

function handleAssign(row: XfWorkOrder) {
  currentOrder.value = row
  assignForm.value = { orderId: row.id || 0, deptIds: [], deadline: '' }
  deptFilterText.value = ''
  loadDeptTree()
  showAssignDialog.value = true
}

async function loadDeptTree() {
  deptTreeLoading.value = true
  try {
    const res = await deptTree()
    deptTreeData.value = (res.data || []) as TreeSelect[]
  } catch (error) {
    console.error('加载部门树失败:', error)
  } finally {
    deptTreeLoading.value = false
  }
}

watch(deptFilterText, (val) => {
  deptTreeRef.value?.filter(val)
})

function filterDeptNode(value: string, data: any) {
  if (!value) return true
  return data.label?.includes(value)
}

function handleDeptCheck() {
  const checkedNodes = deptTreeRef.value?.getCheckedNodes() || []
  assignForm.value.deptIds = checkedNodes.map((n: any) => n.id)
}

async function submitAssign() {
  try {
    await assignFormRef.value.validate()
  } catch { return }

  if (assignForm.value.deptIds.length === 0) {
    ElMessage.warning('请选择至少一个承办单位')
    return
  }

  try {
    await assignWorkOrder(assignForm.value)
    ElMessage.success('交办成功')
    showAssignDialog.value = false
    loadWorkOrderList()
    loadStatistics()
  } catch (error) {
    ElMessage.error('交办失败')
  }
}

function handleDetail(row: XfWorkOrder) {
  router.push({ name: 'XfWorkOrderDetail', params: { id: row.id } })
}

async function handleDelete(row: XfWorkOrder) {
  try {
    await ElMessageBox.confirm('确定要删除该工单吗？', '提示', { type: 'warning' })
    await delWorkOrder(row.id!)
    ElMessage.success('删除成功')
    loadWorkOrderList()
    loadStatistics()
  } catch { /* cancelled */ }
}

function handleExport() {
  ElMessage.info('导出功能暂未实现')
}

function handleFileChange(file: any) {
  uploadFile.value = file.raw
}

function handleExceed() {
  ElMessage.warning('只能上传一个文件，请先移除已有文件')
}

async function submitImport() {
  if (!uploadFile.value) {
    ElMessage.warning('请选择要导入的Excel文件')
    return
  }
  importLoading.value = true
  importProgress.value = null
  try {
    const formData = new FormData()
    formData.append('file', uploadFile.value)
    const res = await importWorkOrder(formData)
    const taskId = res.taskId
    if (!taskId) {
      ElMessage.error('导入任务提交失败')
      return
    }
    // 开始轮询进度
    pollImportProgress(taskId)
  } catch (error: any) {
    ElMessage.error('导入失败：' + (error.msg || error.message || '未知错误'))
    importLoading.value = false
  }
}

function pollImportProgress(taskId: string) {
  const timer = setInterval(async () => {
    try {
      const res = await getImportProgress(taskId)
      const data = res.data as any
      if (data) {
        importProgress.value = {
          total: data.total || 0,
          success: data.success || 0,
          fail: data.fail || 0,
          status: data.status || 'processing',
          message: data.message || ''
        }
        if (data.status === 'done') {
          clearInterval(timer)
          importLoading.value = false
          if (data.fail > 0) {
            ElMessage.warning(data.message)
          } else {
            ElMessage.success(data.message)
          }
          showImportDialog.value = false
          uploadFile.value = null
          importProgress.value = null
          loadWorkOrderList()
          loadStatistics()
        }
      }
    } catch {
      clearInterval(timer)
      importLoading.value = false
      ElMessage.error('查询导入进度失败')
    }
  }, 1500)
}

// Status helpers
function getStatusClass(status: string | undefined) {
  if (!status) return ''
  const map: Record<string, string> = {
    '0': 'status-pending',
    '1': 'status-progress',
    '2': 'status-reported',
    '3': 'status-done',
    '4': 'status-returned',
    '5': 'status-overdue'
  }
  return map[status] || ''
}

function getStatusText(status: string | undefined) {
  if (!status) return '未知'
  const map: Record<string, string> = {
    '0': '待派单', '1': '办理中', '2': '已上报',
    '3': '已办结', '4': '已退回', '5': '已超期'
  }
  return map[status] || status
}

function getOrderNoClass(row: XfWorkOrder) {
  if (row.status === '5') return 'text-overdue'
  if (row.status === '1' || row.status === '2') return 'text-progress'
  if (row.status === '3') return 'text-done'
  return ''
}

function tableRowClassName({ row }: { row: XfWorkOrder }) {
  if (row.status === '5') return 'overdue-row'
  return ''
}

function formatDate(time: string | undefined) {
  if (!time) return ''
  return time.replace('T', ' ').substring(0, 10)
}

onMounted(() => {
  loadWorkOrderList()
  loadStatistics()
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

.work-order-wrapper {
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
  min-height: calc(100vh - 100px);
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
  flex-wrap: wrap;
  gap: 12px;
}

.header-left {
  display: flex;
  align-items: center;
  gap: 24px;
  flex-wrap: wrap;
}

.title-section {
  display: flex;
  align-items: center;
  gap: 12px;

  .title-icon { width: 24px; height: 24px; color: white; }
  .title-text { font-size: 18px; font-weight: 700; margin: 0; letter-spacing: 1px; }
}

.filter-pills { display: flex; gap: 8px; flex-wrap: wrap; }

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

  &:hover { background: rgba(255, 255, 255, 0.1); border-color: rgba(255, 255, 255, 0.5); }

  &.active {
    background: white;
    color: $police-primary;
    border-color: white;

    .pill-dot { width: 6px; height: 6px; background: $police-blue; border-radius: 50%; }
  }
}

.header-actions {
  display: flex;
  gap: 10px;
  flex-wrap: wrap;

  .action-btn {
    padding: 8px 14px;
    border-radius: 10px;
    font-size: 13px;
    font-weight: 500;
    border: 1px solid rgba(255, 255, 255, 0.3);
    background: rgba(255, 255, 255, 0.1);
    color: white;
    cursor: pointer;
    transition: all 0.2s;
    display: flex;
    align-items: center;
    gap: 6px;

    &:hover { background: rgba(255, 255, 255, 0.2); }

    &.primary { background: white; color: $police-primary; border-color: white; &:hover { background: #F0F4F8; } }
  }
}

.stats-section {
  display: flex;
  gap: 16px;
  padding: 16px 24px;
  background: #F8FAFE;
  border-bottom: 1px solid $police-border;
  flex-shrink: 0;
  flex-wrap: wrap;
}

.stat-chip {
  display: flex;
  align-items: center;
  gap: 8px;
  padding: 8px 16px;
  border-radius: 12px;
  background: white;
  border: 1px solid $police-border;
  transition: all 0.2s;

  &:hover { transform: translateY(-2px); box-shadow: 0 2px 8px rgba(0, 0, 0, 0.06); }

  .chip-value { font-size: 16px; font-weight: 700; color: $police-text; }
  .chip-label { font-size: 12px; color: $police-text-muted; }

  &.pending { border-color: #FEF3C7; svg { color: #F59E0B; } }
  &.progress { border-color: #DBEAFE; svg { color: #3B82F6; } }
  &.overdue { border-color: #FEE2E2; svg { color: #EF4444; } }
  &.done { border-color: #D1FAE5; svg { color: #10B981; } }
  &.returned { border-color: #F3E8FF; svg { color: #8B5CF6; } }
}

.table-section {
  padding: 16px 24px;
  flex: 1;
}

.order-no {
  font-weight: 600;
  font-size: 13px;

  &.text-overdue { color: #F56C6C; }
  &.text-progress { color: #409EFF; }
  &.text-done { color: #303133; }
}

.title-cell {
  display: flex;
  align-items: center;
  gap: 6px;

  .title-text { font-weight: 500; }
  .title-text.text-overdue { color: #F56C6C; }
  .title-text.text-progress { color: #409EFF; }
  .title-text.text-done { color: #303133; }

  .top-tag { margin-left: 4px; }
  .overdue-tag { margin-left: 4px; animation: blink-badge 1.5s infinite; }
}

@keyframes blink-badge {
  0%, 100% { opacity: 1; }
  50% { opacity: 0.6; }
}

.status-badge {
  padding: 4px 12px;
  border-radius: 12px;
  font-size: 12px;
  font-weight: 600;
  display: inline-block;

  &.status-pending { background: #FEF3C7; color: #D97706; }
  &.status-progress { background: #DBEAFE; color: #2563EB; }
  &.status-reported { background: #E0E7FF; color: #4F46E5; }
  &.status-done { background: #D1FAE5; color: #059669; }
  &.status-returned { background: #F3E8FF; color: #7C3AED; }
  &.status-overdue { background: #FEE2E2; color: #DC2626; }
}

.table-actions {
  display: flex;
  gap: 8px;
  justify-content: center;
}

.action-icon-btn {
  display: inline-flex;
  align-items: center;
  justify-content: center;
  width: 28px;
  height: 28px;
  padding: 0;
  border-radius: 6px;
  border: 1px solid $police-border;
  background: white;
  color: $police-text;
  cursor: pointer;
  transition: all 0.2s;
  font-size: 0;
  line-height: 0;
  outline: none;
  overflow: hidden;

  &:hover { background: #F5F5F5; }

  &.assign { border-color: $police-light-blue; color: $police-blue; &:hover { background: #EFF6FF; } }
  &.danger { border-color: #FCA5A5; color: #EF4444; &:hover { background: #FEF2F2; } }
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

.assign-order-info {
  background: #F0F9FF;
  border: 1px solid #BAE6FD;
  border-radius: 12px;
  padding: 16px 20px;
  margin-bottom: 20px;

  .info-row {
    display: flex;
    padding: 6px 0;
    .info-label { width: 100px; color: $police-text-muted; font-size: 14px; }
    span { color: $police-text; font-size: 14px; }
  }
}

.dept-tree-wrapper {
  width: 100%;

  .dept-filter-input {
    margin-bottom: 8px;
  }

  .dept-tree-scroll {
    min-height: 180px;
    max-height: 300px;
    overflow-y: auto;
    border: 1px solid $police-border;
    border-radius: 8px;
    padding: 8px;
  }
}

.import-section {
  .upload-drag-content {
    display: flex;
    flex-direction: column;
    align-items: center;
    padding: 40px 20px;

    .upload-icon { color: $police-light-blue; margin-bottom: 12px; }
  }
}

.import-progress {
  margin-top: 20px;
  padding: 16px;
  background: #F0F9FF;
  border-radius: 12px;
  border: 1px solid #BAE6FD;

  .progress-detail {
    display: flex;
    gap: 16px;
    margin-top: 10px;
    font-size: 13px;
    color: $police-text-muted;

    .success-text { color: #10B981; font-weight: 600; }
    .fail-text { color: #EF4444; font-weight: 600; }
  }
}

// Dialog overrides
.xf-dialog {
  :deep(.el-dialog) { border-radius: 16px; }
  :deep(.el-dialog__header) {
    background: linear-gradient(135deg, $police-primary 0%, $police-blue 100%);
    color: white;
    border-radius: 16px 16px 0 0;
    padding: 20px 24px;
    margin: 0;
    .el-dialog__title { color: white; font-weight: 600; font-size: 18px; }
    .el-dialog__headerbtn { top: 20px; right: 24px; .el-dialog__close { color: white; font-size: 20px; } }
  }
  :deep(.el-dialog__body) { padding: 28px 32px; background: white; }
  :deep(.el-dialog__footer) { padding: 20px 32px; background: white; border-top: 1px solid $police-border; border-radius: 0 0 16px 16px; }
}

// Overdue row highlight
:deep(.el-table .overdue-row) {
  background-color: #FEF2F2 !important;
}
:deep(.el-table .overdue-row:hover > td) {
  background-color: #FEE2E2 !important;
}
</style>
