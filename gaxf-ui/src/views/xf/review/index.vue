<template>
  <div class="review-wrapper">
    <div class="main-card">
      <!-- 头部区域 -->
      <div class="card-header">
        <div class="header-left">
          <div class="title-section">
            <LucideShieldCheck class="title-icon" />
            <h2 class="title-text">审核管理</h2>
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

      <!-- 审核列表 -->
      <div class="table-section" v-loading="loading">
        <el-table :data="reviewList" style="width: 100%">
          <el-table-column type="index" label="#" width="50" />
          <el-table-column prop="workOrder?.xfCaseNo" label="信访件编号" width="150">
            <template #default="{ row }">
              <span class="order-link" @click="handleDetail(row)">{{ row.workOrder?.xfCaseNo || '-' }}</span>
            </template>
          </el-table-column>
          <el-table-column prop="workOrder?.title" label="工单标题" min-width="200" show-overflow-tooltip />
          <el-table-column prop="reviewType" label="审核类型" width="120">
            <template #default="{ row }">
              <span class="review-type-tag">{{ getReviewTypeText(row.reviewType) }}</span>
            </template>
          </el-table-column>
          <el-table-column prop="result" label="审核结果" width="100">
            <template #default="{ row }">
              <span v-if="row.result" class="status-badge" :class="row.result === '0' ? 'status-pass' : 'status-return'">
                {{ row.result === '0' ? '通过' : '退回' }}
              </span>
              <span v-else class="status-badge status-pending">待审核</span>
            </template>
          </el-table-column>
          <el-table-column prop="reviewerName" label="审核人" width="100" />
          <el-table-column prop="reviewTime" label="审核时间" width="160">
            <template #default="{ row }">
              {{ formatTime(row.reviewTime) }}
            </template>
          </el-table-column>
          <el-table-column label="操作" width="160" fixed="right">
            <template #default="{ row }">
              <div class="table-actions">
                <button v-if="!row.result" class="action-link primary" @click="handleReview(row)">
                  <LucideCheckSquare :size="14" /> 审核
                </button>
                <button class="action-link" @click="handleDetail(row)">
                  <LucideEye :size="14" /> 详情
                </button>
              </div>
            </template>
          </el-table-column>
        </el-table>
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
          @size-change="loadReviewList"
          @current-change="loadReviewList"
        />
      </div>
    </div>

    <!-- 审核对话框 -->
    <el-dialog v-model="showReviewDialog" title="审核" width="600px" class="xf-dialog">
      <div class="review-order-info" v-if="currentReview">
        <div class="info-row"><span class="info-label">信访件编号：</span><span>{{ currentReview.workOrder?.xfCaseNo }}</span></div>
        <div class="info-row"><span class="info-label">工单标题：</span><span>{{ currentReview.workOrder?.title }}</span></div>
        <div class="info-row"><span class="info-label">审核类型：</span><span>{{ getReviewTypeText(currentReview.reviewType) }}</span></div>
      </div>
      <el-form :model="reviewForm" label-width="100px" ref="reviewFormRef">
        <el-form-item label="审核意见" prop="opinion">
          <el-input v-model="reviewForm.opinion" type="textarea" :rows="4" placeholder="请输入审核意见" />
        </el-form-item>
        <el-form-item label="审核结果">
          <el-radio-group v-model="reviewForm.result">
            <el-radio value="0">通过</el-radio>
            <el-radio value="1">退回</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item v-if="reviewForm.result === '1'" label="退回至">
          <el-select v-model="reviewForm.returnTarget" placeholder="请选择退回目标" style="width: 100%">
            <el-option label="退回县局" value="1" />
            <el-option label="退回专员" value="2" />
          </el-select>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="showReviewDialog = false">取消</el-button>
        <el-button type="primary" @click="submitReview">提交审核</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted, watch } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { LucideShieldCheck, LucideCheckSquare, LucideEye } from 'lucide-vue-next'
import { listReview, policeReview, commissionerReview, leaderReview } from '@/api/xf/review'
import type { XfReviewRecord, ReviewQueryParams, ReviewParams } from '@/types/api/xf/review'

const router = useRouter()

const loading = ref(false)
const currentFilter = ref(0)
const filters = ['全部', '派出所审核', '县局审核', '专员审核', '领导审核']

const reviewList = ref<XfReviewRecord[]>([])
const total = ref(0)
const pageNum = ref(1)
const pageSize = ref(10)

const showReviewDialog = ref(false)
const currentReview = ref<XfReviewRecord | null>(null)
const reviewForm = ref<ReviewParams>({
  orderId: 0,
  reviewType: '',
  opinion: '',
  result: '0',
  returnTarget: undefined
})

watch(currentFilter, (val) => {
  const query: ReviewQueryParams = { pageNum: 1, pageSize: pageSize.value }
  if (val > 0) query.reviewType = String(val)
  pageNum.value = 1
  loadReviewList(query)
})

async function loadReviewList(query?: ReviewQueryParams) {
  loading.value = true
  try {
    const params = query || { pageNum: pageNum.value, pageSize: pageSize.value }
    const res = await listReview(params)
    reviewList.value = (res.rows || []) as XfReviewRecord[]
    total.value = res.total || 0
  } catch (error) {
    ElMessage.error('加载审核列表失败')
  } finally {
    loading.value = false
  }
}

function handleReview(row: XfReviewRecord) {
  currentReview.value = row
  reviewForm.value = {
    orderId: row.orderId || 0,
    assignId: row.assignId,
    reviewType: row.reviewType || '',
    opinion: '',
    result: '0',
    returnTarget: undefined
  }
  showReviewDialog.value = true
}

async function submitReview() {
  if (!reviewForm.value.opinion) {
    ElMessage.warning('请输入审核意见')
    return
  }

  try {
    const reviewType = reviewForm.value.reviewType
    if (reviewType === '1') {
      await policeReview(reviewForm.value)
    } else if (reviewType === '2') {
      await commissionerReview(reviewForm.value)
    } else if (reviewType === '3' || reviewType === '4') {
      await leaderReview(reviewForm.value)
    }
    ElMessage.success('审核提交成功')
    showReviewDialog.value = false
    loadReviewList()
  } catch (error) {
    ElMessage.error('审核提交失败')
  }
}

function handleDetail(row: XfReviewRecord) {
  if (row.orderId) {
    router.push({ name: 'XfWorkOrderDetail', params: { id: row.orderId } })
  }
}

function getReviewTypeText(type: string | undefined) {
  if (!type) return '未知'
  const map: Record<string, string> = { '1': '派出所审核', '2': '县局审核', '3': '专员审核', '4': '领导审核' }
  return map[type] || type
}

function formatTime(time: string | undefined) {
  if (!time) return '-'
  return time.replace('T', ' ').substring(0, 19)
}

onMounted(() => {
  loadReviewList()
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

.review-wrapper { padding: 24px; background: $police-bg; min-height: calc(100vh - 48px); }

.main-card {
  background: white; border-radius: 20px; border: 1px solid $police-border;
  overflow: hidden; box-shadow: 0 4px 20px rgba(30, 58, 138, 0.08);
  display: flex; flex-direction: column;
}

.card-header {
  display: flex; justify-content: space-between; align-items: center;
  padding: 20px 24px; background: linear-gradient(135deg, $police-primary 0%, $police-blue 100%);
  color: white; flex-shrink: 0;
}

.header-left { display: flex; align-items: center; gap: 24px; flex-wrap: wrap; }

.title-section {
  display: flex; align-items: center; gap: 12px;
  .title-icon { width: 24px; height: 24px; color: white; }
  .title-text { font-size: 18px; font-weight: 700; margin: 0; letter-spacing: 1px; }
}

.filter-pills { display: flex; gap: 8px; }

.pill-btn {
  padding: 6px 16px; border-radius: 20px; font-size: 13px; font-weight: 500;
  border: 1px solid rgba(255, 255, 255, 0.3); background: transparent;
  color: rgba(255, 255, 255, 0.8); cursor: pointer; transition: all 0.2s;
  display: flex; align-items: center; gap: 6px;

  &:hover { background: rgba(255, 255, 255, 0.1); }
  &.active {
    background: white; color: $police-primary; border-color: white;
    .pill-dot { width: 6px; height: 6px; background: $police-blue; border-radius: 50%; }
  }
}

.table-section { padding: 16px 24px; flex: 1; }

.order-link { color: $police-blue; cursor: pointer; font-weight: 600; &:hover { text-decoration: underline; } }

.review-type-tag {
  padding: 3px 10px; border-radius: 8px; font-size: 12px; font-weight: 500;
  background: #EFF6FF; color: $police-blue;
}

.status-badge {
  padding: 4px 12px; border-radius: 12px; font-size: 12px; font-weight: 600; display: inline-block;
  &.status-pending { background: #FEF3C7; color: #D97706; }
  &.status-pass { background: #D1FAE5; color: #059669; }
  &.status-return { background: #FEE2E2; color: #DC2626; }
}

.table-actions { display: flex; gap: 8px; }

.action-link {
  display: flex; align-items: center; gap: 4px; padding: 4px 10px; border-radius: 8px;
  font-size: 12px; font-weight: 500; border: 1px solid $police-border; background: white;
  color: $police-text; cursor: pointer; transition: all 0.2s;
  &:hover { background: #F5F5F5; }
  &.primary { border-color: $police-light-blue; color: $police-blue; &:hover { background: #EFF6FF; } }
}

.pagination-section {
  display: flex; justify-content: space-between; align-items: center;
  padding: 16px 24px; background: #F8FAFE; border-top: 1px solid $police-border; flex-shrink: 0;
  .page-stats { font-size: 13px; color: $police-text-muted; strong { color: $police-text; } }
}

.review-order-info {
  background: #F0F9FF; border: 1px solid #BAE6FD; border-radius: 12px;
  padding: 16px 20px; margin-bottom: 20px;
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
</style>
