<template>
  <div class="police-page">
    <!-- 筛选栏 -->
    <div class="filter-bar">
      <div class="filter-buttons">
        <button
          v-for="(f, i) in filters"
          :key="i"
          :class="['filter-btn', { active: currentFilter === i }]"
          @click="currentFilter = i"
        >
          {{ f }}
        </button>
      </div>
      <div class="filter-actions">
        <button class="action-btn success" @click="handleAdd">
          <LucidePlus class="icon-small" />
          <span>新增预警</span>
        </button>
        <button class="action-btn" @click="showAdvancedFilter = true">
          <LucideFilter class="icon-small" />
          <span>高级筛选</span>
        </button>
        <button class="action-btn primary" @click="handleExport">
          <LucideDownload class="icon-small" />
          <span>导出报表</span>
        </button>
      </div>
    </div>

    <!-- 统计概览 -->
    <div class="stats-row">
      <div class="stat-item">
        <LucideBell class="stat-icon warning" />
        <div class="stat-info">
          <span class="stat-label">预警总数</span>
          <span class="stat-value">{{ statistics.todayCount || 0 }}</span>
        </div>
      </div>
      <div class="stat-item">
        <LucideClock class="stat-icon pending" />
        <div class="stat-info">
          <span class="stat-label">待处理</span>
          <span class="stat-value">{{ statistics.pendingCount || 0 }}</span>
        </div>
      </div>
      <div class="stat-item">
        <LucideCheckCircle class="stat-icon success" />
        <div class="stat-info">
          <span class="stat-label">已处置</span>
          <span class="stat-value">{{ statistics.resolvedCount || 0 }}</span>
        </div>
      </div>
      <div class="stat-item">
        <LucideAlertCircle class="stat-icon danger" />
        <div class="stat-info">
          <span class="stat-label">待核实</span>
          <span class="stat-value">{{ statistics.verifyCount || 0 }}</span>
        </div>
      </div>
    </div>

    <!-- 预警卡片网格 -->
    <div class="alert-grid" v-loading="loading">
      <div
        v-for="alert in alertList"
        :key="alert.id"
        class="alert-card"
      >
        <!-- 预警图片区域 -->
        <div class="alert-media">
          <img v-if="alert.pic1" :src="alert.pic1" class="media-img" alt="预警图片" />
          <div v-else class="media-placeholder">
            <LucideVideo class="media-icon" />
          </div>
          <div class="level-badge" :class="getLevelClass(alert.type || alert.mainType)">{{ getLevelText(alert.type || alert.mainType) }}</div>
          <div class="media-overlay">
            <span class="alert-time">
              <LucideClock class="icon-small" />
              {{ formatTime(alert.time) }}
            </span>
            <span class="alert-type">{{ alert.warningtype || alert.msgtitle }}</span>
          </div>
        </div>

        <!-- 预警信息 -->
        <div class="alert-body">
          <div class="alert-location">
            <LucideMapPin class="icon-small" />
            <span>{{ alert.deptName || alert.policename }}</span>
          </div>
          <div class="alert-status-row">
            <div class="alert-deal-status" :class="getDealClass(alert.deal)">
              {{ getDealText(alert.deal) }}
            </div>
            <div class="alert-verify-status" v-if="alert.isResolved === 1 && alert.deal !== '01'">
              待核实
            </div>
          </div>
          <div class="alert-actions">
            <!-- 待处理状态：显示处置按钮 -->
            <button class="handle-btn primary" @click="handleDeal(alert)" v-if="alert.deal === '03'">
              <LucideClipboardEdit class="icon-small" />
              <span>处置</span>
            </button>
            <!-- 已处理状态(is_resolved=1)：显示核实按钮 -->
            <button class="handle-btn success" @click="handleVerify(alert)" v-if="alert.isResolved === 1 && alert.deal !== '01'">
              <LucideCheck class="icon-small" />
              <span>核实</span>
            </button>
            <button class="handle-btn" @click="handleDetail(alert)">
              <LucideFileText class="icon-small" />
              <span>详情</span>
            </button>
          </div>
        </div>
      </div>

      <!-- 空状态 -->
      <div v-if="!loading && alertList.length === 0" class="empty-state">
        <LucideInbox class="empty-icon" />
        <span>暂无预警数据</span>
      </div>
    </div>

    <!-- 分页 -->
    <div class="pagination-bar" v-if="total > 0">
      <span class="page-info">显示 {{ (pageNum - 1) * pageSize + 1 }}-{{ Math.min(pageNum * pageSize, total) }} 条，共 {{ total }} 条预警</span>
      <div class="page-buttons">
        <button class="page-btn" :disabled="pageNum === 1" @click="handlePageChange(pageNum - 1)">
          <LucideChevronLeft class="icon-small" />
        </button>
        <button
          v-for="p in visiblePages"
          :key="p"
          :class="['page-btn', { active: pageNum === p }]"
          @click="handlePageChange(p)"
        >
          {{ p }}
        </button>
        <button class="page-btn" :disabled="pageNum >= totalPages" @click="handlePageChange(pageNum + 1)">
          <LucideChevronRight class="icon-small" />
        </button>
      </div>
    </div>

    <!-- 高级筛选对话框 -->
    <el-dialog v-model="showAdvancedFilter" title="高级筛选" width="520px" class="filter-dialog">
      <el-form :model="queryParams" label-width="90px" label-position="left">
        <el-form-item label="预警类型">
          <el-input v-model="queryParams.warningtype" placeholder="请输入预警类型" clearable size="default" />
        </el-form-item>
        <el-form-item label="处置状态">
          <el-select v-model="queryParams.deal" placeholder="请选择处置状态" clearable size="default" style="width: 100%">
            <el-option label="待处理" value="03" />
            <el-option label="已核实" value="01" />
            <el-option label="已处置" value="02" />
          </el-select>
        </el-form-item>
        <el-form-item label="预警时间">
          <el-date-picker
            v-model="timeRange"
            type="daterange"
            range-separator="至"
            start-placeholder="开始日期"
            end-placeholder="结束日期"
            value-format="YYYY-MM-DD"
            style="width: 100%"
            @change="handleTimeRangeChange"
          />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="showAdvancedFilter = false">取消</el-button>
        <el-button type="primary" @click="handleFilter">筛选</el-button>
        <el-button @click="handleReset">重置</el-button>
      </template>
    </el-dialog>

    <!-- 详情对话框 -->
    <el-dialog v-model="showDetailDialog" title="预警详情" width="700px" class="detail-dialog">
      <div class="detail-content" v-if="currentAlert">
        <div class="detail-row">
          <span class="detail-label">预警内容：</span>
          <span class="detail-value">{{ currentAlert.msgtitle }}</span>
        </div>
        <div class="detail-row">
          <span class="detail-label">预警类型：</span>
          <span class="detail-value">{{ currentAlert.warningtype }}</span>
        </div>
        <div class="detail-row">
          <span class="detail-label">预警级别：</span>
          <span class="detail-value">{{ currentAlert.type || currentAlert.mainType }}</span>
        </div>
        <div class="detail-row">
          <span class="detail-label">预警时间：</span>
          <span class="detail-value">{{ formatTime(currentAlert.time) }}</span>
        </div>
        <div class="detail-row">
          <span class="detail-label">所属单位：</span>
          <span class="detail-value">{{ currentAlert.deptName || currentAlert.policename }}</span>
        </div>
        <div class="detail-row">
          <span class="detail-label">摄像机：</span>
          <span class="detail-value">{{ currentAlert.cameraName || '未知' }}</span>
        </div>
        <div class="detail-row">
          <span class="detail-label">处置状态：</span>
          <span class="detail-value">{{ getDealText(currentAlert.deal) }}</span>
        </div>
        <div class="detail-row" v-if="currentAlert.result">
          <span class="detail-label">处置结果：</span>
          <span class="detail-value">{{ currentAlert.result }}</span>
        </div>
        <div class="detail-images" v-if="currentAlert.pic1 || currentAlert.pic2">
          <img v-if="currentAlert.pic1" :src="currentAlert.pic1" class="detail-img" />
          <img v-if="currentAlert.pic2" :src="currentAlert.pic2" class="detail-img" />
        </div>
      </div>
    </el-dialog>

    <!-- 处置对话框 -->
    <el-dialog v-model="showHandleDialog" title="预警核实" width="600px" class="handle-dialog">
      <el-form :model="handleForm" label-width="100px" label-position="left" ref="handleFormRef">
        <el-form-item label="核实状态">
          <el-select v-model="handleForm.deal" placeholder="请选择核实状态" size="default" style="width: 100%">
            <el-option label="已核实" value="01" />
            <el-option label="已处置" value="02" />
          </el-select>
        </el-form-item>
        <el-form-item label="核实结果">
          <el-input v-model="handleForm.result" type="textarea" :rows="4" placeholder="请输入核实结果" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="showHandleDialog = false">取消</el-button>
        <el-button type="primary" @click="submitHandle">确认核实</el-button>
      </template>
    </el-dialog>

    <!-- 处置表单对话框 -->
    <el-dialog v-model="showDealDialog" title="预警处置" width="850px" class="deal-dialog">
      <div class="deal-alert-info" v-if="currentDealAlert">
        <div class="info-row">
          <span class="info-label">预警内容：</span>
          <span class="info-value">{{ currentDealAlert.msgtitle }}</span>
        </div>
        <div class="info-row">
          <span class="info-label">问题单位：</span>
          <span class="info-value">{{ currentDealAlert.deptName || currentDealAlert.policename }}</span>
        </div>
      </div>
      <el-form :model="dealForm" label-width="100px" label-position="left" ref="dealFormRef">
        <el-form-item label="处置方式">
          <el-select v-model="dealForm.dealtype" placeholder="请选择处置方式" clearable size="default" style="width: 100%">
            <el-option v-for="dict in spdc_deal_type" :key="dict.value" :label="dict.label" :value="dict.value" />
          </el-select>
        </el-form-item>
        <!-- 选择无需处置时，隐藏其他字段 -->
        <template v-if="dealForm.dealtype !== '0'">
          <el-form-item label="问题性质">
            <el-select v-model="dealForm.wtxz" placeholder="请选择问题性质" clearable size="default" style="width: 100%">
              <el-option v-for="dict in spdc_wtxz" :key="dict.value" :label="dict.label" :value="dict.value" />
            </el-select>
          </el-form-item>
          <el-form-item label="整改要求">
            <el-select v-model="dealForm.adjust" placeholder="请选择整改要求" clearable size="default" style="width: 100%">
              <el-option v-for="dict in spdc_adjust" :key="dict.value" :label="dict.label" :value="dict.value" />
            </el-select>
          </el-form-item>
          <el-form-item label="处理意见">
            <el-select v-model="dealForm.dealPropose" placeholder="请选择处理意见" clearable size="default" style="width: 100%">
              <el-option v-for="dict in spdc_deal_propose" :key="dict.value" :label="dict.label" :value="dict.value" />
            </el-select>
          </el-form-item>
          <el-form-item label="问题大类">
            <el-select v-model="dealForm.typeLevelone" placeholder="请选择问题大类" clearable size="default" style="width: 100%" @change="dealForm.typeLeveltwo = ''">
              <el-option v-for="item in problemCategories" :key="item.id" :label="item.name || ''" :value="item.name || ''" />
            </el-select>
          </el-form-item>
          <el-form-item label="详细问题">
            <el-select v-model="dealForm.typeLeveltwo" placeholder="请选择详细问题" clearable size="default" style="width: 100%" :disabled="!dealForm.typeLevelone">
            <el-option v-for="item in problemDetails" :key="item.id" :label="item.name || ''" :value="item.name || ''" />
          </el-select>
        </el-form-item>
        <el-form-item label="处置时限">
          <el-input v-model="dealForm.deadline" placeholder="请输入处置时限（如：3天）" size="default" />
        </el-form-item>
        </template>
      </el-form>
      <template #footer>
        <el-button @click="showDealDialog = false">取消</el-button>
        <el-button type="primary" @click="submitDeal">提交处置</el-button>
      </template>
    </el-dialog>

    <!-- 新增预警对话框 -->
    <el-dialog v-model="showAddDialog" title="新增预警" width="650px" class="add-dialog">
      <el-form :model="addForm" :rules="addFormRules" label-width="100px" label-position="left" ref="addFormRef">
        <el-form-item label="预警内容" prop="msgtitle">
          <el-input v-model="addForm.msgtitle" type="textarea" :rows="3" placeholder="请输入预警内容" />
        </el-form-item>
        <el-form-item label="预警级别" prop="type">
          <el-select v-model="addForm.type" placeholder="请选择预警级别" clearable size="default" style="width: 100%" @change="addForm.warningtype = ''">
            <el-option v-for="item in alertTypeCategories" :key="item.id" :label="item.name || ''" :value="item.name || ''" />
          </el-select>
        </el-form-item>
        <el-form-item label="预警类型" prop="warningtype">
          <el-select v-model="addForm.warningtype" placeholder="请选择预警类型" clearable size="default" style="width: 100%" :disabled="!addForm.type">
            <el-option v-for="item in alertTypeDetails" :key="item.id" :label="item.name || ''" :value="item.name || ''" />
          </el-select>
        </el-form-item>
        <el-form-item label="所属部门" prop="departId">
          <el-tree-select
            v-model="addForm.departId"
            :data="deptTreeData"
            :props="{ label: 'label', children: 'children' }"
            node-key="id"
            placeholder="请选择所属部门"
            check-strictly
            clearable
            size="default"
            style="width: 100%"
          />
        </el-form-item>
        <el-form-item label="关联摄像机">
          <el-select v-model="addForm.cameraId" placeholder="请选择关联摄像机" clearable size="default" style="width: 100%" :disabled="!addForm.departId">
            <el-option v-for="cam in cameraList" :key="cam.pointId" :label="cam.pointName || ''" :value="cam.pointId || 0" />
          </el-select>
        </el-form-item>
        <el-form-item label="预警图片1">
          <el-input v-model="addForm.pic1" placeholder="请输入图片URL（可选）" size="default" />
        </el-form-item>
        <el-form-item label="预警图片2">
          <el-input v-model="addForm.pic2" placeholder="请输入图片URL（可选）" size="default" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="showAddDialog = false">取消</el-button>
        <el-button type="primary" @click="submitAdd">确认新增</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, onMounted, watch } from 'vue'
import { ElMessage } from 'element-plus'
import {
  LucideFilter, LucideDownload, LucideBell, LucideAlertTriangle,
  LucideClock, LucideCheckCircle, LucideVideo, LucideMapPin,
  LucideEye, LucideFileText, LucideChevronLeft, LucideChevronRight, LucideInbox,
  LucideClipboardEdit, LucideCheck, LucideAlertCircle, LucidePlus
} from 'lucide-vue-next'
import { listAlert, getAlertStatistics, getAlert, handleAlert, addAlert } from '@/api/spdc/alert'
import { createFromAlert } from '@/api/spdc/pga'
import { listDictTree } from '@/api/spdc/dict'
import { deptTree } from '@/api/system/dept'
import { listCamera } from '@/api/spdc/camera'
import { useDict } from '@/utils/dict'
import type { DcDxjg, AlertQueryParams, AlertStatistics } from '@/types/api/spdc/alert'
import type { DcPga } from '@/types/api/spdc/pga'
import type { DcDict } from '@/types/api/spdc/dict'
import type { TreeSelect } from '@/types'
import type { DcCamera } from '@/types/api/spdc/camera'

// 字典数据
const { spdc_deal_type, spdc_wtxz, spdc_adjust, spdc_deal_propose } = useDict('spdc_deal_type', 'spdc_wtxz', 'spdc_adjust', 'spdc_deal_propose')

// 问题分类字典（kind=1）
const problemCategories = ref<DcDict[]>([])
const problemDetails = ref<DcDict[]>([])

const loading = ref(false)
const currentFilter = ref(0)
const filters = ['全部预警', '待处理', '已处置', '待核实', '已核实']

const alertList = ref<DcDxjg[]>([])
const total = ref(0)
const pageNum = ref(1)
const pageSize = ref(12)

const queryParams = ref<AlertQueryParams>({
  pageNum: 1,
  pageSize: 12
})

const timeRange = ref<[string, string] | null>(null)
const showAdvancedFilter = ref(false)
const showDetailDialog = ref(false)
const showHandleDialog = ref(false)
const currentAlert = ref<DcDxjg | null>(null)

const statistics = ref<AlertStatistics>({
  todayCount: 0,
  pendingCount: 0,
  resolvedCount: 0,
  verifyCount: 0
})

const handleForm = ref({
  id: 0,
  deal: '',
  result: ''
})

const dealForm = ref({
  dealtype: '',
  wtxz: '',
  adjust: '',
  dealPropose: '',
  typeLevelone: '',
  typeLeveltwo: '',
  deadline: ''
})

const showDealDialog = ref(false)
const currentDealAlert = ref<DcDxjg | null>(null)

// 新增预警相关变量
const showAddDialog = ref(false)
const addFormRef = ref()
const addForm = ref<DcDxjg>({
  msgtitle: '',
  warningtype: '',
  type: '',
  mainType: '',
  departId: undefined,
  cameraId: undefined,
  pic1: '',
  pic2: ''
})
const addFormRules = {
  msgtitle: [{ required: true, message: '请输入预警内容', trigger: 'blur' }],
  warningtype: [{ required: true, message: '请选择预警类型', trigger: 'change' }],
  type: [{ required: true, message: '请选择预警级别', trigger: 'change' }],
  departId: [{ required: true, message: '请选择所属部门', trigger: 'change' }]
}
const alertTypeCategories = ref<DcDict[]>([])
const alertTypeDetails = ref<DcDict[]>([])
const deptTreeData = ref<TreeSelect[]>([])
const cameraList = ref<DcCamera[]>([])
const currentDeptId = ref<number | undefined>(undefined)

const totalPages = computed(() => Math.ceil(total.value / pageSize.value))

const visiblePages = computed(() => {
  const pages: (number | string)[] = []
  const current = pageNum.value
  const total = totalPages.value

  if (total <= 7) {
    for (let i = 1; i <= total; i++) pages.push(i)
  } else {
    if (current <= 4) {
      pages.push(1, 2, 3, 4, 5, '...', total)
    } else if (current >= total - 3) {
      pages.push(1, '...', total - 4, total - 3, total - 2, total - 1, total)
    } else {
      pages.push(1, '...', current - 1, current, current + 1, '...', total)
    }
  }
  return pages
})

// 加载预警列表
async function loadAlerts() {
  loading.value = true
  try {
    const res = await listAlert(queryParams.value)
    alertList.value = (res.rows || []) as DcDxjg[]
    total.value = res.total || 0
  } catch (error) {
    console.error('加载预警列表失败:', error)
    ElMessage.error('加载预警列表失败')
  } finally {
    loading.value = false
  }
}

// 加载统计数据
async function loadStatistics() {
  try {
    const res = await getAlertStatistics()
    if (res.data) {
      statistics.value = res.data
    }
  } catch (error) {
    console.error('加载统计数据失败:', error)
  }
}

// 根据筛选按钮更新查询条件
watch(currentFilter, (val) => {
  queryParams.value.deal = undefined
  queryParams.value.type = undefined
  queryParams.value.isResolved = undefined

  if (val === 1) {
    queryParams.value.deal = '03' // 待处理
  } else if (val === 2) {
    queryParams.value.deal = '02' // 已处置
  } else if (val === 3) {
    // 待核实：已处置但未核实 (isResolved=1 且 deal≠01)
    queryParams.value.isResolved = 1
    queryParams.value.deal = '02'
  } else if (val === 4) {
    queryParams.value.deal = '01' // 已核实
  }

  pageNum.value = 1
  queryParams.value.pageNum = 1
  loadAlerts()
})

// 时间范围变化
function handleTimeRangeChange(val: [string, string] | null) {
  if (val) {
    queryParams.value.params = {
      beginTime: val[0],
      endTime: val[1]
    }
  } else {
    queryParams.value.params = undefined
  }
}

// 高级筛选
function handleFilter() {
  showAdvancedFilter.value = false
  pageNum.value = 1
  queryParams.value.pageNum = 1
  loadAlerts()
}

// 重置筛选
function handleReset() {
  queryParams.value = {
    pageNum: 1,
    pageSize: 12
  }
  timeRange.value = null
  showAdvancedFilter.value = false
  currentFilter.value = 0
  loadAlerts()
}

// 分页变化
function handlePageChange(page: number | string) {
  if (typeof page === 'string') return // Skip '...' placeholder
  if (page < 1 || page > totalPages.value) return
  pageNum.value = page
  queryParams.value.pageNum = page
  loadAlerts()
}

// 获取级别样式
function getLevelClass(type: string | undefined) {
  if (!type) return 'level-mid'
  if (type.includes('高危')) return 'level-high'
  if (type.includes('中危')) return 'level-mid'
  return 'level-low'
}

// 获取级别文本
function getLevelText(type: string | undefined) {
  if (!type) return '中危'
  if (type.includes('高危')) return '高危'
  if (type.includes('中危')) return '中危'
  return '低危'
}

// 获取处置状态样式
function getDealClass(deal: string | undefined) {
  if (!deal) return ''
  if (deal === '03') return 'deal-pending'
  if (deal === '01') return 'deal-verified'
  if (deal === '02') return 'deal-resolved'
  return ''
}

// 获取处置状态文本
function getDealText(deal: string | undefined) {
  if (!deal) return '未知'
  if (deal === '03') return '待处理'
  if (deal === '01') return '已核实'
  if (deal === '02') return '已处置'
  return deal
}

// 格式化时间
function formatTime(time: string | undefined) {
  if (!time) return ''
  return time.replace('T', ' ').substring(0, 19)
}

// 核实预警
function handleVerify(alert: DcDxjg) {
  currentAlert.value = alert
  handleForm.value = {
    id: alert.id || 0,
    deal: alert.deal === '03' ? '01' : alert.deal || '01',
    result: alert.result || ''
  }
  showHandleDialog.value = true
}

// 处置预警（创建整改记录）
function handleDeal(alert: DcDxjg) {
  currentDealAlert.value = alert
  dealForm.value = {
    dealtype: '',
    wtxz: '',
    adjust: '',
    dealPropose: '',
    typeLevelone: '',
    typeLeveltwo: '',
    deadline: ''
  }
  showDealDialog.value = true
}

// 提交处置
async function submitDeal() {
  if (!currentDealAlert.value) return
  if (!dealForm.value.dealtype) {
    ElMessage.warning('请选择处置方式')
    return
  }

  try {
    // 无需处置：直接更新预警状态为已核实，流程结束
    if (dealForm.value.dealtype === '0') {
      await handleAlert({
        id: currentDealAlert.value.id,
        deal: '01', // 已核实
        result: '无需处置，直接核实完成'
      } as DcDxjg)
      ElMessage.success('处置完成，预警流程已结束')
    } else {
      // 需要处置：创建整改记录
      const pgaData: DcPga = {
        ...dealForm.value,
        dealtype: dealForm.value.dealtype || undefined,
        wtxz: dealForm.value.wtxz || undefined,
        adjust: dealForm.value.adjust || undefined,
        dealPropose: dealForm.value.dealPropose || undefined,
        typeLevelone: dealForm.value.typeLevelone || undefined,
        typeLeveltwo: dealForm.value.typeLeveltwo || undefined,
        deadline: dealForm.value.deadline || undefined
      }
      await createFromAlert(currentDealAlert.value.id || 0, pgaData)
      ElMessage.success('处置成功，已创建整改记录')
    }
    showDealDialog.value = false
    loadAlerts()
    loadStatistics()
  } catch (error) {
    ElMessage.error('处置失败')
  }
}

// 查看详情
async function handleDetail(alert: DcDxjg) {
  try {
    const res = await getAlert(alert.id || 0)
    currentAlert.value = res.data || null
    showDetailDialog.value = true
  } catch (error) {
    ElMessage.error('获取详情失败')
  }
}

// 提交处置
async function submitHandle() {
  if (!handleForm.value.deal) {
    ElMessage.warning('请选择处置状态')
    return
  }

  try {
    await handleAlert({
      id: handleForm.value.id,
      deal: handleForm.value.deal,
      result: handleForm.value.result
    } as DcDxjg)
    ElMessage.success('处置成功')
    showHandleDialog.value = false
    loadAlerts()
    loadStatistics()
  } catch (error) {
    ElMessage.error('处置失败')
  }
}

// 新增预警
function handleAdd() {
  addForm.value = {
    msgtitle: '',
    warningtype: '',
    type: '',
    mainType: '',
    departId: undefined,
    cameraId: undefined,
    pic1: '',
    pic2: ''
  }
  loadAlertTypeDict()
  loadDeptTree()
  showAddDialog.value = true
}

// 加载预警分类字典（kind=3）
async function loadAlertTypeDict() {
  try {
    const res = await listDictTree({ kind: 3 })
    alertTypeCategories.value = (res.data || []) as DcDict[]
  } catch (error) {
    console.error('加载预警分类失败:', error)
  }
}

// 加载部门树（本部门及下级）
async function loadDeptTree() {
  try {
    const res = await deptTree()
    deptTreeData.value = (res.data || []) as TreeSelect[]
  } catch (error) {
    console.error('加载部门树失败:', error)
  }
}

// 加载部门下的摄像机列表
async function loadCameraByDept(deptId: number) {
  try {
    const res = await listCamera({ deptId, pageNum: 1, pageSize: 100 })
    cameraList.value = (res.rows || []) as DcCamera[]
  } catch (error) {
    console.error('加载摄像机列表失败:', error)
    cameraList.value = []
  }
}

// 部门选择变化时，加载摄像机列表
watch(() => addForm.value.departId, (deptId) => {
  if (deptId) {
    loadCameraByDept(deptId)
    currentDeptId.value = deptId
  } else {
    cameraList.value = []
    currentDeptId.value = undefined
  }
  addForm.value.cameraId = undefined
})

// 预警级别变化时，更新预警类型列表
watch(() => addForm.value.type, (parentName) => {
  if (parentName) {
    const parent = alertTypeCategories.value.find(p => p.name === parentName)
    if (parent && parent.children) {
      alertTypeDetails.value = parent.children
    } else {
      alertTypeDetails.value = []
    }
  } else {
    alertTypeDetails.value = []
  }
  addForm.value.warningtype = ''
})

// 提交新增预警
async function submitAdd() {
  try {
    await addFormRef.value.validate()
  } catch {
    return
  }

  try {
    // 设置预警大类为选中的级别
    addForm.value.mainType = addForm.value.type
    await addAlert(addForm.value)
    ElMessage.success('新增预警成功')
    showAddDialog.value = false
    loadAlerts()
    loadStatistics()
  } catch (error) {
    ElMessage.error('新增预警失败')
  }
}

// 导出（暂未实现）
function handleExport() {
  ElMessage.info('导出功能暂未实现')
}

// 加载问题分类字典
async function loadProblemCategories() {
  try {
    const res = await listDictTree({ kind: 1 })
    problemCategories.value = (res.data || []) as DcDict[]
  } catch (error) {
    console.error('加载问题分类失败:', error)
  }
}

// 问题大类变化时，更新详细问题列表
watch(() => dealForm.value.typeLevelone, (parentName) => {
  if (parentName) {
    const parent = problemCategories.value.find(p => p.name === parentName)
    if (parent && parent.children) {
      problemDetails.value = parent.children
    } else {
      problemDetails.value = []
    }
  } else {
    problemDetails.value = []
  }
  dealForm.value.typeLeveltwo = ''
})

onMounted(() => {
  loadAlerts()
  loadStatistics()
  loadProblemCategories()
})
</script>

<style lang="scss" scoped>
$police-primary: #1E3A8A;
$police-blue: #2563EB;
$police-light-blue: #3B82F6;
$police-bg: #F8FAFE;
$police-card: #FFFFFF;
$police-border: #E2E8F0;
$police-text: #1E293B;
$police-text-muted: #5B6E8C;

.police-page {
  padding: 24px;
  background: $police-bg;
  min-height: 100%;
}

.icon-small { width: 16px; height: 16px; }

.filter-bar {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 24px;
}

.filter-buttons {
  display: flex;
  gap: 12px;
}

.filter-btn {
  padding: 8px 20px;
  border-radius: 20px;
  font-size: 13px;
  font-weight: 500;
  border: 1px solid $police-border;
  background: white;
  color: $police-text-muted;
  cursor: pointer;
  transition: all 0.2s;

  &:hover {
    border-color: $police-text-muted;
  }

  &.active {
    background: $police-blue;
    color: white;
    border-color: $police-blue;
  }
}

.filter-actions {
  display: flex;
  gap: 12px;
}

.action-btn {
  padding: 8px 16px;
  border-radius: 20px;
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

  &:hover {
    background: #F5F5F5;
  }

  &.primary {
    background: $police-blue;
    color: white;
    border-color: $police-blue;

    &:hover {
      background: $police-light-blue;
    }
  }

  &.success {
    background: #10B981;
    color: white;
    border-color: #10B981;

    &:hover {
      background: #059669;
    }
  }
}

.stats-row {
  display: flex;
  gap: 20px;
  margin-bottom: 24px;
}

.stat-item {
  flex: 1;
  background: white;
  border-radius: 16px;
  border: 1px solid $police-border;
  padding: 16px 20px;
  display: flex;
  align-items: center;
  gap: 16px;
  transition: all 0.2s;

  &:hover {
    transform: translateY(-2px);
    box-shadow: 0 4px 12px rgba(0, 0, 0, 0.08);
  }
}

.stat-icon {
  width: 40px;
  height: 40px;
  border-radius: 12px;
  display: flex;
  align-items: center;
  justify-content: center;

  &.warning { background: rgba(245, 158, 11, 0.1); color: #F59E0B; }
  &.danger { background: rgba(239, 68, 68, 0.1); color: #EF4444; }
  &.pending { background: rgba(59, 130, 246, 0.1); color: #3B82F6; }
  &.success { background: rgba(16, 185, 129, 0.1); color: #10B981; }
}

.stat-info {
  display: flex;
  flex-direction: column;
}

.stat-label {
  font-size: 13px;
  color: $police-text-muted;
}

.stat-value {
  font-size: 24px;
  font-weight: 700;
  color: $police-text;
}

.alert-grid {
  display: grid;
  grid-template-columns: repeat(3, 1fr);
  gap: 20px;
  min-height: 200px;
}

.alert-card {
  background: white;
  border-radius: 20px;
  border: 1px solid $police-border;
  overflow: hidden;
  transition: all 0.2s;

  &:hover {
    transform: translateY(-3px);
    box-shadow: 0 6px 16px rgba(0, 0, 0, 0.1);
  }
}

.alert-media {
  height: 160px;
  background: linear-gradient(145deg, #111C2E 0%, #0A1222 100%);
  position: relative;
  overflow: hidden;
}

.media-img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.media-placeholder {
  position: absolute;
  inset: 0;
  display: flex;
  align-items: center;
  justify-content: center;

  .media-icon {
    width: 40px;
    height: 40px;
    color: rgba(255, 255, 255, 0.2);
  }
}

.level-badge {
  position: absolute;
  top: 12px;
  right: 12px;
  padding: 6px 12px;
  border-radius: 20px;
  font-size: 11px;
  font-weight: 700;
  z-index: 10;

  &.level-high { background: #EF4444; color: white; }
  &.level-mid { background: #F59E0B; color: white; }
  &.level-low { background: #3B82F6; color: white; }
}

.media-overlay {
  position: absolute;
  bottom: 0;
  left: 0;
  right: 0;
  padding: 12px 16px;
  background: linear-gradient(transparent, rgba(0, 0, 0, 0.8));
  z-index: 10;
}

.alert-time {
  font-size: 11px;
  color: rgba(255, 255, 255, 0.8);
  display: flex;
  align-items: center;
  gap: 4px;
}

.alert-type {
  font-size: 13px;
  font-weight: 600;
  color: white;
  margin-top: 4px;
  display: block;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.alert-body {
  padding: 16px;
}

.alert-location {
  display: flex;
  align-items: center;
  gap: 6px;
  font-size: 12px;
  color: $police-text-muted;
  margin-bottom: 8px;
}

.alert-deal-status {
  font-size: 11px;
  padding: 4px 8px;
  border-radius: 12px;
  display: inline-block;

  &.deal-pending { background: rgba(59, 130, 246, 0.1); color: #3B82F6; }
  &.deal-verified { background: rgba(245, 158, 11, 0.1); color: #F59E0B; }
  &.deal-resolved { background: rgba(16, 185, 129, 0.1); color: #10B981; }
}

.alert-status-row {
  display: flex;
  align-items: center;
  gap: 8px;
  margin-bottom: 12px;
}

.alert-verify-status {
  font-size: 11px;
  padding: 4px 8px;
  border-radius: 12px;
  background: rgba(59, 130, 246, 0.1);
  color: #3B82F6;
  margin-left: 8px;
}

.alert-actions {
  display: flex;
  gap: 12px;
  justify-content: center;
}

.handle-btn {
  padding: 8px 16px;
  border-radius: 20px;
  font-size: 12px;
  font-weight: 500;
  border: 1px solid $police-border;
  background: white;
  color: $police-text;
  cursor: pointer;
  transition: all 0.2s;
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 4px;
  flex: 1;

  &:hover {
    background: #F5F5F5;
  }

  &.primary {
    background: $police-blue;
    color: white;
    border-color: $police-blue;

    &:hover {
      background: $police-light-blue;
    }
  }

  &.success {
    background: #10B981;
    color: white;
    border-color: #10B981;

    &:hover {
      background: #059669;
    }
  }
}

.deal-alert-info {
  background: #F0F9FF;
  border: 1px solid #BAE6FD;
  border-radius: 12px;
  padding: 16px 20px;
  margin-bottom: 24px;

  .info-row {
    display: flex;
    padding: 8px 0;

    .info-label {
      width: 100px;
      color: $police-text-muted;
      font-size: 14px;
    }

    .info-value {
      flex: 1;
      color: $police-text;
      font-size: 14px;
    }
  }
}

// 处置对话框样式
.deal-dialog {
  :deep(.el-dialog) {
    border-radius: 16px;
  }

  :deep(.el-dialog__header) {
    background: linear-gradient(135deg, $police-primary 0%, $police-blue 100%);
    color: white;
    border-radius: 16px 16px 0 0;
    padding: 20px 24px;
    margin: 0;

    .el-dialog__title {
      color: white;
      font-weight: 600;
      font-size: 18px;
    }

    .el-dialog__headerbtn {
      top: 20px;
      right: 24px;

      .el-dialog__close {
        color: white;
        font-size: 20px;
      }
    }
  }

  :deep(.el-dialog__body) {
    padding: 28px 32px;
    background: white;
  }

  :deep(.el-dialog__footer) {
    padding: 20px 32px;
    background: white;
    border-top: 1px solid $police-border;
    border-radius: 0 0 16px 16px;
  }

  :deep(.el-form-item__label) {
    font-size: 14px;
    color: $police-text;
  }

  :deep(.el-input__inner),
  :deep(.el-select__wrapper) {
    font-size: 14px;
  }
}

// 新增预警对话框样式
.add-dialog {
  :deep(.el-dialog) {
    border-radius: 16px;
  }

  :deep(.el-dialog__header) {
    background: linear-gradient(135deg, #10B981 0%, #059669 100%);
    color: white;
    border-radius: 16px 16px 0 0;
    padding: 20px 24px;
    margin: 0;

    .el-dialog__title {
      color: white;
      font-weight: 600;
      font-size: 18px;
    }

    .el-dialog__headerbtn {
      top: 20px;
      right: 24px;

      .el-dialog__close {
        color: white;
        font-size: 20px;
      }
    }
  }

  :deep(.el-dialog__body) {
    padding: 28px 32px;
    background: white;
  }

  :deep(.el-dialog__footer) {
    padding: 20px 32px;
    background: white;
    border-top: 1px solid $police-border;
    border-radius: 0 0 16px 16px;
  }

  :deep(.el-form-item__label) {
    font-size: 14px;
    color: $police-text;
  }

  :deep(.el-input__inner),
  :deep(.el-select__wrapper) {
    font-size: 14px;
  }
}

.pagination-bar {
  margin-top: 24px;
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.page-info {
  font-size: 13px;
  color: $police-text-muted;
}

.page-buttons {
  display: flex;
  gap: 8px;
}

.page-btn {
  padding: 8px 12px;
  border-radius: 12px;
  font-size: 13px;
  border: 1px solid $police-border;
  background: white;
  color: $police-text;
  cursor: pointer;
  transition: all 0.2s;
  display: flex;
  align-items: center;
  justify-content: center;

  &:hover:not(:disabled) {
    background: #F5F5F5;
  }

  &.active {
    background: $police-blue;
    color: white;
    border-color: $police-blue;
  }

  &:disabled {
    opacity: 0.5;
    cursor: not-allowed;
  }
}

.empty-state {
  grid-column: 1 / -1;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: 60px 20px;
  color: $police-text-muted;

  .empty-icon {
    width: 48px;
    height: 48px;
    margin-bottom: 16px;
  }
}

// 详情对话框样式
.detail-dialog {
  :deep(.el-dialog) {
    border-radius: 16px;
  }

  :deep(.el-dialog__header) {
    background: linear-gradient(135deg, #6366F1 0%, #8B5CF6 100%);
    color: white;
    border-radius: 16px 16px 0 0;
    padding: 20px 24px;
    margin: 0;

    .el-dialog__title {
      color: white;
      font-weight: 600;
      font-size: 18px;
    }

    .el-dialog__headerbtn {
      top: 20px;
      right: 24px;

      .el-dialog__close {
        color: white;
        font-size: 20px;
      }
    }
  }

  :deep(.el-dialog__body) {
    padding: 28px 32px;
    background: white;
  }
}

.detail-content {
  .detail-row {
    display: flex;
    padding: 14px 0;
    border-bottom: 1px solid $police-border;

    .detail-label {
      width: 120px;
      color: $police-text-muted;
      font-size: 14px;
    }

    .detail-value {
      flex: 1;
      color: $police-text;
      font-size: 14px;
    }
  }

  .detail-images {
    display: flex;
    gap: 16px;
    margin-top: 20px;

    .detail-img {
      width: 240px;
      height: 180px;
      object-fit: cover;
      border-radius: 12px;
      border: 1px solid $police-border;
    }
  }
}

// 高级筛选对话框样式
.filter-dialog {
  :deep(.el-dialog) {
    border-radius: 16px;
  }

  :deep(.el-dialog__header) {
    background: linear-gradient(135deg, #F59E0B 0%, #D97706 100%);
    color: white;
    border-radius: 16px 16px 0 0;
    padding: 20px 24px;
    margin: 0;

    .el-dialog__title {
      color: white;
      font-weight: 600;
      font-size: 18px;
    }

    .el-dialog__headerbtn {
      top: 20px;
      right: 24px;

      .el-dialog__close {
        color: white;
        font-size: 20px;
      }
    }
  }

  :deep(.el-dialog__body) {
    padding: 28px 32px;
    background: white;
  }

  :deep(.el-dialog__footer) {
    padding: 20px 32px;
    background: white;
    border-top: 1px solid $police-border;
    border-radius: 0 0 16px 16px;
  }

  :deep(.el-form-item__label) {
    font-size: 14px;
    color: $police-text;
  }
}

// 处置核实对话框样式
.handle-dialog {
  :deep(.el-dialog) {
    border-radius: 16px;
  }

  :deep(.el-dialog__header) {
    background: linear-gradient(135deg, #10B981 0%, #059669 100%);
    color: white;
    border-radius: 16px 16px 0 0;
    padding: 20px 24px;
    margin: 0;

    .el-dialog__title {
      color: white;
      font-weight: 600;
      font-size: 18px;
    }

    .el-dialog__headerbtn {
      top: 20px;
      right: 24px;

      .el-dialog__close {
        color: white;
        font-size: 20px;
      }
    }
  }

  :deep(.el-dialog__body) {
    padding: 28px 32px;
    background: white;
  }

  :deep(.el-dialog__footer) {
    padding: 20px 32px;
    background: white;
    border-top: 1px solid $police-border;
    border-radius: 0 0 16px 16px;
  }

  :deep(.el-form-item__label) {
    font-size: 14px;
    color: $police-text;
  }
}

@media (max-width: 1100px) {
  .alert-grid { grid-template-columns: repeat(2, 1fr); }
  .stats-row { flex-wrap: wrap; }
  .stat-item { flex: 1 1 calc(50% - 10px); min-width: 200px; }
}

@media (max-width: 768px) {
  .alert-grid { grid-template-columns: 1fr; }
  .filter-bar { flex-direction: column; gap: 16px; }
}
</style>