<template>
  <div class="pga-wrapper">
    <div class="main-card">
      <!-- 头部区域：标题 + 筛选 -->
      <div class="card-header">
        <div class="header-left">
          <div class="title-section">
            <LucideClipboardList class="title-icon" />
            <h2 class="title-text">整改反馈管理</h2>
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
          <button class="action-btn" @click="showAdvancedFilter = true">
            <LucideSlidersHorizontal :size="16" />
            <span>筛选</span>
          </button>
          <button class="action-btn primary" @click="handleExport">
            <LucideDownload :size="16" />
            <span>导出</span>
          </button>
        </div>
      </div>

      <!-- 统计概览 -->
      <div class="stats-section">
        <div class="stat-chip pending">
          <LucideClock :size="16" />
          <span class="chip-value">{{ stats.pending }}</span>
          <span class="chip-label">待整改</span>
        </div>
        <div class="stat-chip progress">
          <LucideLoader :size="16" />
          <span class="chip-value">{{ stats.inProgress }}</span>
          <span class="chip-label">整改中</span>
        </div>
        <div class="stat-chip done">
          <LucideCheckCircle :size="16" />
          <span class="chip-value">{{ stats.done }}</span>
          <span class="chip-label">已整改</span>
        </div>
        <div class="stat-chip feedback">
          <LucideMessageSquare :size="16" />
          <span class="chip-value">{{ stats.feedback }}</span>
          <span class="chip-label">已反馈</span>
        </div>
      </div>

      <!-- 卡片网格列表 -->
      <div class="card-grid" v-loading="loading">
        <div
          v-for="item in pgaList"
          :key="item.id"
          class="pga-card"
          :class="{ 'urgent': item.state === '01' }"
        >
          <!-- 卡片头部 -->
          <div class="card-top">
            <div class="org-info">
              <LucideBuilding2 :size="16" class="org-icon" />
              <span class="org-name">{{ item.problemDepart }}</span>
            </div>
            <div class="status-badge" :class="getStateClass(item.state)">
              {{ getStateText(item.state) }}
            </div>
          </div>

          <!-- 卡片主体 -->
          <div class="card-body">
            <div class="info-row">
              <LucideMapPin :size="14" class="row-icon" />
              <span class="row-label">县局</span>
              <span class="row-value">{{ item.countyname || '-' }}</span>
            </div>
            <div class="info-row">
              <LucidePhone :size="14" class="row-icon" />
              <span class="row-label">处置</span>
              <span class="row-value">{{ getDictLabel(spdc_deal_type, item.dealtype) }}</span>
            </div>
            <div class="info-row">
              <LucideAlertCircle :size="14" class="row-icon" />
              <span class="row-label">性质</span>
              <span class="row-value">{{ getDictLabel(spdc_wtxz, item.wtxz) }}</span>
            </div>
            <div class="info-row">
              <LucideTag :size="14" class="row-icon" />
              <span class="row-label">大类</span>
              <span class="row-value">{{ item.typeLevelone || '-' }}</span>
            </div>
            <div class="info-row time-row">
              <LucideCalendar :size="14" class="row-icon" />
              <span class="row-label">发现</span>
              <span class="row-value">{{ formatTime(item.findtime) }}</span>
            </div>
          </div>

          <!-- 反馈状态条 -->
          <div class="feedback-bar" :class="{ 'has-feedback': item.dealState === '01' }">
            <LucideMessageCircle :size="14" />
            <span>{{ getDealStateText(item.dealState) }}</span>
          </div>

          <!-- 操作按钮 -->
          <div class="card-actions">
            <button
              class="action-btn feedback-btn"
              @click="handleFeedback(item)"
              v-if="item.state === '01'"
            >
              <LucideReply :size="14" />
              <span>反馈</span>
            </button>
            <button class="action-btn detail-btn" @click="handleDetail(item)">
              <LucideEye :size="14" />
              <span>详情</span>
            </button>
          </div>
        </div>

        <!-- 空状态 -->
        <div v-if="!loading && pgaList.length === 0" class="empty-state">
          <LucideInbox :size="48" class="empty-icon" />
          <span class="empty-text">暂无整改记录</span>
        </div>
      </div>

      <!-- 分页区域 -->
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
          @size-change="loadPgaList"
          @current-change="loadPgaList"
        />
      </div>
    </div>

    <!-- 高级筛选对话框 -->
    <el-dialog v-model="showAdvancedFilter" title="高级筛选" width="420px" class="filter-dialog">
      <el-form :model="queryParams" label-width="80px">
        <el-form-item label="问题单位">
          <el-input v-model="queryParams.problemDepart" placeholder="请输入问题单位" clearable />
        </el-form-item>
        <el-form-item label="处置方式">
          <el-select v-model="queryParams.dealtype" placeholder="请选择" clearable>
            <el-option v-for="d in spdc_deal_type" :key="d.value" :label="d.label" :value="d.value" />
          </el-select>
        </el-form-item>
        <el-form-item label="整改状态">
          <el-select v-model="queryParams.state" placeholder="请选择" clearable>
            <el-option label="待整改" value="01" />
            <el-option label="整改中" value="02" />
            <el-option label="已整改" value="03" />
          </el-select>
        </el-form-item>
        <el-form-item label="发现时间">
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
        <el-button @click="handleReset">重置</el-button>
        <el-button type="primary" @click="handleFilter">筛选</el-button>
      </template>
    </el-dialog>

    <!-- 详情对话框 -->
    <el-dialog v-model="showDetailDialog" title="整改详情" width="600px" class="detail-dialog">
      <div class="detail-content" v-if="currentPga">
        <div class="detail-section">
          <div class="section-title">
            <LucideBuilding2 :size="16" />
            <span>单位信息</span>
          </div>
          <div class="detail-grid">
            <div class="detail-item">
              <span class="item-label">问题单位</span>
              <span class="item-value">{{ currentPga.problemDepart }}</span>
            </div>
            <div class="detail-item">
              <span class="item-label">县局</span>
              <span class="item-value">{{ currentPga.countyname }}</span>
            </div>
            <div class="detail-item">
              <span class="item-label">派出所</span>
              <span class="item-value">{{ currentPga.policename }}</span>
            </div>
          </div>
        </div>
        <div class="detail-section">
          <div class="section-title">
            <LucideClipboardEdit :size="16" />
            <span>处置信息</span>
          </div>
          <div class="detail-grid">
            <div class="detail-item">
              <span class="item-label">处置方式</span>
              <span class="item-value">{{ getDictLabel(spdc_deal_type, currentPga.dealtype) }}</span>
            </div>
            <div class="detail-item">
              <span class="item-label">问题性质</span>
              <span class="item-value">{{ getDictLabel(spdc_wtxz, currentPga.wtxz) }}</span>
            </div>
            <div class="detail-item">
              <span class="item-label">整改要求</span>
              <span class="item-value">{{ getDictLabel(spdc_adjust, currentPga.adjust) }}</span>
            </div>
            <div class="detail-item">
              <span class="item-label">处理意见</span>
              <span class="item-value">{{ getDictLabel(spdc_deal_propose, currentPga.dealPropose) }}</span>
            </div>
            <div class="detail-item">
              <span class="item-label">问题大类</span>
              <span class="item-value">{{ currentPga.typeLevelone || '-' }}</span>
            </div>
            <div class="detail-item">
              <span class="item-label">详细问题</span>
              <span class="item-value">{{ currentPga.typeLeveltwo || '-' }}</span>
            </div>
            <div class="detail-item full">
              <span class="item-label">问题内容</span>
              <span class="item-value">{{ currentPga.problemMsg || '-' }}</span>
            </div>
          </div>
        </div>
        <div class="detail-section">
          <div class="section-title">
            <LucideClock :size="16" />
            <span>时间与状态</span>
          </div>
          <div class="detail-grid">
            <div class="detail-item">
              <span class="item-label">发现时间</span>
              <span class="item-value">{{ formatTime(currentPga.findtime) }}</span>
            </div>
            <div class="detail-item">
              <span class="item-label">整改时限</span>
              <span class="item-value">{{ currentPga.deadline || '-' }}</span>
            </div>
            <div class="detail-item">
              <span class="item-label">整改状态</span>
              <span class="item-value">
                <span class="status-badge small" :class="getStateClass(currentPga.state)">
                  {{ getStateText(currentPga.state) }}
                </span>
              </span>
            </div>
            <div class="detail-item">
              <span class="item-label">反馈状态</span>
              <span class="item-value">
                <span class="status-badge small" :class="getDealStateClass(currentPga.dealState)">
                  {{ getDealStateText(currentPga.dealState) }}
                </span>
              </span>
            </div>
          </div>
          <div class="detail-item full" v-if="currentPga.dealConent">
            <span class="item-label">反馈情况</span>
            <span class="item-value">{{ currentPga.dealConent }}</span>
          </div>
        </div>
        <div class="detail-images" v-if="currentPga.pic1 || currentPga.dealPic">
          <div class="image-block" v-if="currentPga.pic1">
            <span class="image-label">问题图片</span>
            <img :src="currentPga.pic1" class="detail-img" />
          </div>
          <div class="image-block" v-if="currentPga.dealPic">
            <span class="image-label">整改图片</span>
            <img :src="currentPga.dealPic" class="detail-img" />
          </div>
        </div>
      </div>
    </el-dialog>

    <!-- 反馈对话框 -->
    <el-dialog v-model="showFeedbackDialog" title="整改反馈" width="750px" class="feedback-dialog">
      <!-- 上半部分：整改详情 -->
      <div class="feedback-detail" v-if="currentPga">
        <div class="detail-header">
          <LucideFileText class="header-icon" />
          <span>整改详情</span>
        </div>
        <div class="detail-grid">
          <div class="detail-item">
            <span class="item-label">问题单位</span>
            <span class="item-value">{{ currentPga.problemDepart }}</span>
          </div>
          <div class="detail-item">
            <span class="item-label">县局</span>
            <span class="item-value">{{ currentPga.countyname }}</span>
          </div>
          <div class="detail-item">
            <span class="item-label">派出所</span>
            <span class="item-value">{{ currentPga.policename }}</span>
          </div>
          <div class="detail-item">
            <span class="item-label">处置方式</span>
            <span class="item-value">{{ getDictLabel(spdc_deal_type, currentPga.dealtype) }}</span>
          </div>
          <div class="detail-item">
            <span class="item-label">问题性质</span>
            <span class="item-value">{{ getDictLabel(spdc_wtxz, currentPga.wtxz) }}</span>
          </div>
          <div class="detail-item">
            <span class="item-label">整改要求</span>
            <span class="item-value">{{ getDictLabel(spdc_adjust, currentPga.adjust) }}</span>
          </div>
          <div class="detail-item">
            <span class="item-label">处理意见</span>
            <span class="item-value">{{ getDictLabel(spdc_deal_propose, currentPga.dealPropose) }}</span>
          </div>
          <div class="detail-item">
            <span class="item-label">问题大类</span>
            <span class="item-value">{{ currentPga.typeLevelone || '-' }}</span>
          </div>
          <div class="detail-item">
            <span class="item-label">详细问题</span>
            <span class="item-value">{{ currentPga.typeLeveltwo || '-' }}</span>
          </div>
          <div class="detail-item full-width">
            <span class="item-label">问题内容</span>
            <span class="item-value">{{ currentPga.problemMsg || '-' }}</span>
          </div>
          <div class="detail-item">
            <span class="item-label">发现时间</span>
            <span class="item-value">{{ formatTime(currentPga.findtime) }}</span>
          </div>
          <div class="detail-item">
            <span class="item-label">整改时限</span>
            <span class="item-value">{{ currentPga.deadline || '-' }}</span>
          </div>
        </div>
        <div class="detail-image-section" v-if="currentPga.pic1">
          <span class="section-label">问题图片</span>
          <img :src="currentPga.pic1" class="detail-img" />
        </div>
      </div>

      <!-- 分隔线 -->
      <div class="feedback-divider"></div>

      <!-- 下半部分：反馈表单 -->
      <div class="feedback-form-section">
        <div class="form-header">
          <LucideEdit class="header-icon" />
          <span>反馈信息</span>
        </div>
        <el-form :model="feedbackForm" label-width="80px" ref="feedbackFormRef">
          <el-form-item label="整改状态">
            <el-select v-model="feedbackForm.state" placeholder="请选择整改状态">
              <el-option label="已整改" value="03" />
              <el-option label="整改中" value="02" />
            </el-select>
          </el-form-item>
          <el-form-item label="反馈情况">
            <el-input v-model="feedbackForm.dealConent" type="textarea" :rows="4" placeholder="请输入反馈情况" />
          </el-form-item>
          <el-form-item label="整改图片">
            <el-input v-model="feedbackForm.dealPic" placeholder="请输入图片URL" />
          </el-form-item>
          <el-form-item label="反馈附件">
            <el-input v-model="feedbackForm.dealFujian" placeholder="请输入附件URL" />
          </el-form-item>
        </el-form>
      </div>
      <template #footer>
        <el-button @click="showFeedbackDialog = false">取消</el-button>
        <el-button type="primary" @click="submitFeedbackAction">提交反馈</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted, watch, computed } from 'vue'
import { ElMessage } from 'element-plus'
import {
  LucideFilter, LucideDownload, LucideReply, LucideEye, LucideFileText, LucideEdit,
  LucideClipboardList, LucideSlidersHorizontal, LucideBuilding2, LucideMapPin,
  LucidePhone, LucideAlertCircle, LucideTag, LucideCalendar, LucideMessageCircle,
  LucideMessageSquare, LucideClock, LucideLoader, LucideCheckCircle, LucideInbox,
  LucideClipboardEdit
} from 'lucide-vue-next'
import { listPga, getPga, submitFeedback as submitFeedbackApi } from '@/api/spdc/pga'
import { useDict } from '@/utils/dict'
import type { DcPga, PgaQueryParams } from '@/types/api/spdc/pga'

const { spdc_deal_type, spdc_wtxz, spdc_adjust, spdc_deal_propose } = useDict('spdc_deal_type', 'spdc_wtxz', 'spdc_adjust', 'spdc_deal_propose')

function getDictLabel(dictList: any[], value: string | undefined): string {
  if (!value) return '-'
  const dict = dictList.find(d => d.value === value)
  return dict ? dict.label : value
}

const loading = ref(false)
const currentFilter = ref(0)
const filters = ['全部', '待整改', '整改中', '已整改', '已反馈']

const pgaList = ref<DcPga[]>([])
const total = ref(0)
const pageNum = ref(1)
const pageSize = ref(10)

const queryParams = ref<PgaQueryParams>({
  pageNum: 1,
  pageSize: 10
})

const timeRange = ref<[string, string] | null>(null)
const showAdvancedFilter = ref(false)
const showDetailDialog = ref(false)
const showFeedbackDialog = ref(false)
const currentPga = ref<DcPga | null>(null)

const feedbackForm = ref({
  id: 0,
  dxjgId: 0,
  state: '03',
  dealConent: '',
  dealPic: '',
  dealFujian: ''
})

// 统计数据（从列表计算）
const stats = computed(() => {
  const pending = pgaList.value.filter(p => p.state === '01').length
  const inProgress = pgaList.value.filter(p => p.state === '02').length
  const done = pgaList.value.filter(p => p.state === '03').length
  const feedback = pgaList.value.filter(p => p.dealState === '01').length
  return { pending, inProgress, done, feedback }
})

async function loadPgaList() {
  loading.value = true
  try {
    queryParams.value.pageNum = pageNum.value
    queryParams.value.pageSize = pageSize.value
    const res = await listPga(queryParams.value)
    pgaList.value = (res.rows || []) as DcPga[]
    total.value = res.total || 0
  } catch (error) {
    console.error('加载列表失败:', error)
    ElMessage.error('加载列表失败')
  } finally {
    loading.value = false
  }
}

watch(currentFilter, (val) => {
  queryParams.value.state = undefined
  queryParams.value.dealState = undefined

  if (val === 1) {
    queryParams.value.state = '01'
  } else if (val === 2) {
    queryParams.value.state = '02'
  } else if (val === 3) {
    queryParams.value.state = '03'
  } else if (val === 4) {
    queryParams.value.dealState = '01'
  }

  pageNum.value = 1
  loadPgaList()
})

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

function handleFilter() {
  showAdvancedFilter.value = false
  pageNum.value = 1
  loadPgaList()
}

function handleReset() {
  queryParams.value = { pageNum: 1, pageSize: 10 }
  timeRange.value = null
  showAdvancedFilter.value = false
  currentFilter.value = 0
  loadPgaList()
}

function handleDetail(row: DcPga) {
  currentPga.value = row
  showDetailDialog.value = true
}

function handleFeedback(row: DcPga) {
  currentPga.value = row
  feedbackForm.value = {
    id: row.id || 0,
    dxjgId: row.dxjgId || 0,
    state: '03',
    dealConent: '',
    dealPic: '',
    dealFujian: ''
  }
  showFeedbackDialog.value = true
}

async function submitFeedbackAction() {
  if (!feedbackForm.value.dealConent) {
    ElMessage.warning('请输入反馈情况')
    return
  }

  try {
    await submitFeedbackApi(feedbackForm.value as DcPga)
    ElMessage.success('反馈提交成功')
    showFeedbackDialog.value = false
    loadPgaList()
  } catch (error) {
    ElMessage.error('反馈提交失败')
  }
}

function handleExport() {
  ElMessage.info('导出功能暂未实现')
}

function formatTime(time: string | undefined) {
  if (!time) return ''
  return time.replace('T', ' ').substring(0, 19)
}

function getStateClass(state: string | undefined) {
  if (!state) return ''
  if (state === '01') return 'state-pending'
  if (state === '02') return 'state-progress'
  if (state === '03') return 'state-done'
  return ''
}

function getStateText(state: string | undefined) {
  if (!state) return '未知'
  if (state === '01') return '待整改'
  if (state === '02') return '整改中'
  if (state === '03') return '已整改'
  return state
}

function getDealStateClass(dealState: string | undefined) {
  if (!dealState) return ''
  if (dealState === '01') return 'state-feedback'
  return ''
}

function getDealStateText(dealState: string | undefined) {
  if (!dealState) return '未反馈'
  if (dealState === '01') return '已反馈'
  return dealState
}

onMounted(() => {
  loadPgaList()
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

// 外层容器 - 铺满页面
.pga-wrapper {
  padding: 24px;
  background: $police-bg;
  min-height: calc(100vh - 48px);
}

// 主卡片容器 - 铺满页面
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

// 头部区域
.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 20px 24px;
  background: linear-gradient(135deg, $police-primary 0%, $police-blue 100%);
  color: white;
  flex-shrink: 0;
}

.header-left {
  display: flex;
  align-items: center;
  gap: 24px;
}

.title-section {
  display: flex;
  align-items: center;
  gap: 12px;

  .title-icon {
    width: 24px;
    height: 24px;
    color: white;
  }

  .title-text {
    font-size: 18px;
    font-weight: 700;
    margin: 0;
    letter-spacing: 1px;
  }
}

.filter-pills {
  display: flex;
  gap: 8px;
}

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

  &:hover {
    background: rgba(255, 255, 255, 0.1);
    border-color: rgba(255, 255, 255, 0.5);
  }

  &.active {
    background: white;
    color: $police-primary;
    border-color: white;

    .pill-dot {
      width: 6px;
      height: 6px;
      background: $police-blue;
      border-radius: 50%;
    }
  }
}

.header-actions {
  display: flex;
  gap: 12px;

  .action-btn {
    padding: 8px 16px;
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

    &:hover {
      background: rgba(255, 255, 255, 0.2);
    }

    &.primary {
      background: white;
      color: $police-primary;
      border-color: white;

      &:hover {
        background: #F0F4F8;
      }
    }
  }
}

// 统计概览
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
  transition: all 0.2s;

  &:hover {
    transform: translateY(-2px);
    box-shadow: 0 2px 8px rgba(0, 0, 0, 0.06);
  }

  .chip-value {
    font-size: 16px;
    font-weight: 700;
    color: $police-text;
  }

  .chip-label {
    font-size: 12px;
    color: $police-text-muted;
  }

  &.pending {
    border-color: #FEE2E2;
    svg { color: #EF4444; }
  }

  &.progress {
    border-color: #FEF3C7;
    svg { color: #F59E0B; }
  }

  &.done {
    border-color: #D1FAE5;
    svg { color: #10B981; }
  }

  &.feedback {
    border-color: #DBEAFE;
    svg { color: #3B82F6; }
  }
}

// 卡片网格
.card-grid {
  padding: 24px;
  display: grid;
  grid-template-columns: repeat(3, 1fr);
  gap: 20px;
  flex: 1;
  overflow-y: auto;
  align-items: start;
}

.pga-card {
  background: white;
  border-radius: 16px;
  border: 1px solid $police-border;
  overflow: hidden;
  transition: all 0.3s;
  display: flex;
  flex-direction: column;

  &:hover {
    transform: translateY(-4px);
    box-shadow: 0 8px 24px rgba(30, 58, 138, 0.12);
    border-color: $police-light-blue;
  }

  &.urgent {
    border-color: #FEE2E2;
    .card-top { background: linear-gradient(135deg, #FEE2E2 0%, #FECACA 100%); }
  }
}

.card-top {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 14px 16px;
  background: linear-gradient(135deg, #F8FAFE 0%, #EFF6FF 100%);
  border-bottom: 1px solid $police-border;
}

.org-info {
  display: flex;
  align-items: center;
  gap: 8px;

  .org-icon {
    color: $police-blue;
  }

  .org-name {
    font-size: 14px;
    font-weight: 600;
    color: $police-text;
    max-width: 180px;
    overflow: hidden;
    text-overflow: ellipsis;
    white-space: nowrap;
  }
}

.status-badge {
  padding: 4px 12px;
  border-radius: 12px;
  font-size: 12px;
  font-weight: 600;

  &.state-pending {
    background: #FEE2E2;
    color: #DC2626;
  }

  &.state-progress {
    background: #FEF3C7;
    color: #D97706;
  }

  &.state-done {
    background: #D1FAE5;
    color: #059669;
  }

  &.state-feedback {
    background: #DBEAFE;
    color: #2563EB;
  }

  &.small {
    padding: 2px 8px;
    font-size: 11px;
  }
}

.card-body {
  padding: 12px 16px;
}

.info-row {
  display: flex;
  align-items: center;
  gap: 8px;
  padding: 6px 0;

  &:last-child {
    padding-bottom: 0;
  }

  .row-icon {
    color: $police-text-muted;
    flex-shrink: 0;
  }

  .row-label {
    font-size: 12px;
    color: $police-text-muted;
    width: 32px;
  }

  .row-value {
    font-size: 13px;
    color: $police-text;
    font-weight: 500;
  }

  &.time-row .row-value {
    font-size: 12px;
    color: $police-text-muted;
  }
}

.feedback-bar {
  display: flex;
  align-items: center;
  gap: 6px;
  padding: 10px 16px;
  background: #F8FAFE;
  border-top: 1px solid $police-border;
  font-size: 12px;
  color: $police-text-muted;

  svg {
    color: $police-text-muted;
  }

  &.has-feedback {
    background: #EFF6FF;
    color: $police-blue;
    svg { color: $police-blue; }
  }
}

.card-actions {
  display: flex;
  gap: 12px;
  padding: 12px 16px;
  justify-content: center;
  background: white;
  border-top: 1px solid $police-border;

  .action-btn {
    padding: 8px 20px;
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

    &:hover {
      background: #F5F5F5;
    }

    &.feedback-btn {
      background: $police-blue;
      color: white;
      border-color: $police-blue;

      &:hover {
        background: $police-light-blue;
      }
    }

    &.detail-btn {
      border-color: $police-light-blue;
      color: $police-blue;

      &:hover {
        background: #EFF6FF;
      }
    }
  }
}

// 空状态
.empty-state {
  grid-column: 1 / -1;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: 60px 20px;
  color: $police-text-muted;

  .empty-icon {
    margin-bottom: 16px;
    opacity: 0.5;
  }

  .empty-text {
    font-size: 14px;
  }
}

// 分页
.pagination-section {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 16px 24px;
  background: #F8FAFE;
  border-top: 1px solid $police-border;
  flex-shrink: 0;

  .page-stats {
    font-size: 13px;
    color: $police-text-muted;

    strong {
      color: $police-text;
    }
  }
}

// 详情对话框
.detail-dialog {
  .el-dialog__header {
    background: linear-gradient(135deg, $police-primary 0%, $police-blue 100%);
    color: white;
    border-radius: 12px 12px 0 0;
    padding: 16px 20px;

    .el-dialog__title {
      color: white;
      font-weight: 600;
    }

    .el-dialog__headerbtn .el-dialog__close {
      color: white;
    }
  }

  .el-dialog__body {
    padding: 0;
  }
}

.detail-content {
  padding: 20px;

  .detail-section {
    margin-bottom: 20px;

    &:last-child {
      margin-bottom: 0;
    }

    .section-title {
      display: flex;
      align-items: center;
      gap: 8px;
      font-size: 14px;
      font-weight: 600;
      color: $police-primary;
      margin-bottom: 12px;
      padding-bottom: 8px;
      border-bottom: 1px solid $police-border;

      svg {
        color: $police-blue;
      }
    }

    .detail-grid {
      display: grid;
      grid-template-columns: repeat(2, 1fr);
      gap: 8px 20px;
    }

    .detail-item {
      display: flex;
      gap: 12px;

      &.full {
        grid-column: 1 / -1;
      }

      .item-label {
        width: 80px;
        font-size: 13px;
        color: $police-text-muted;
        flex-shrink: 0;
      }

      .item-value {
        font-size: 13px;
        color: $police-text;
        flex: 1;
      }
    }
  }

  .detail-images {
    display: flex;
    gap: 20px;
    margin-top: 16px;

    .image-block {
      .image-label {
        font-size: 12px;
        color: $police-text-muted;
        margin-bottom: 8px;
        display: block;
      }

      .detail-img {
        width: 200px;
        height: 150px;
        object-fit: cover;
        border-radius: 12px;
        border: 1px solid $police-border;
      }
    }
  }
}

// 反馈对话框样式
.feedback-dialog {
  .el-dialog__header {
    background: linear-gradient(135deg, $police-primary 0%, $police-blue 100%);
    color: white;
    border-radius: 12px 12px 0 0;
    padding: 16px 20px;

    .el-dialog__title {
      color: white;
      font-weight: 600;
    }

    .el-dialog__headerbtn .el-dialog__close {
      color: white;
    }
  }

  .el-dialog__body {
    padding: 0;
    background: white;
  }

  .el-dialog__footer {
    padding: 16px 20px;
    background: white;
    border-top: 1px solid $police-border;
    border-radius: 0 0 12px 12px;
  }
}

.feedback-detail {
  padding: 20px;

  .detail-header {
    display: flex;
    align-items: center;
    gap: 8px;
    font-size: 14px;
    font-weight: 600;
    color: $police-primary;
    margin-bottom: 16px;

    .header-icon {
      color: $police-blue;
    }
  }

  .detail-grid {
    display: grid;
    grid-template-columns: repeat(2, 1fr);
    gap: 12px 20px;

    .detail-item {
      display: flex;
      gap: 12px;

      &.full-width {
        grid-column: 1 / -1;
      }

      .item-label {
        width: 80px;
        font-size: 13px;
        color: $police-text-muted;
      }

      .item-value {
        font-size: 13px;
        color: $police-text;
        flex: 1;
      }
    }
  }

  .detail-image-section {
    margin-top: 16px;

    .section-label {
      font-size: 13px;
      color: $police-text-muted;
      margin-bottom: 8px;
      display: block;
    }

    .detail-img {
      width: 200px;
      height: 150px;
      object-fit: cover;
      border-radius: 12px;
      border: 1px solid $police-border;
    }
  }
}

.feedback-divider {
  height: 1px;
  background: $police-border;
  margin: 0 20px;
}

.feedback-form-section {
  padding: 20px;

  .form-header {
    display: flex;
    align-items: center;
    gap: 8px;
    font-size: 14px;
    font-weight: 600;
    color: $police-primary;
    margin-bottom: 16px;

    .header-icon {
      color: $police-blue;
    }
  }
}

@media (max-width: 1200px) {
  .card-grid {
    grid-template-columns: repeat(2, 1fr);
  }
}

@media (max-width: 768px) {
  .card-grid {
    grid-template-columns: 1fr;
  }

  .card-header {
    flex-direction: column;
    gap: 16px;
  }

  .header-left {
    flex-direction: column;
    align-items: flex-start;
  }

  .stats-section {
    flex-wrap: wrap;
  }
}
</style>