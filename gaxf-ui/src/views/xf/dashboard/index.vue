<template>
  <div class="police-page">
    <!-- 统计卡片区域 -->
    <div class="stats-grid">
      <div class="stat-card">
        <div class="stat-info">
          <h4><LucideDatabase class="icon-small" /> 工单总数</h4>
          <div class="stat-number">{{ overview.totalCount || 0 }}</div>
          <small>全系统工单</small>
        </div>
        <div class="stat-icon">
          <LucideClipboardList class="icon" />
        </div>
      </div>
      <div class="stat-card">
        <div class="stat-info">
          <h4><LucideLoader class="icon-small" /> 待提交</h4>
          <div class="stat-number blue">{{ overview.inProgressCount || 0 }}</div>
          <small>正在处理</small>
        </div>
        <div class="stat-icon blue-icon">
          <LucideClock class="icon" />
        </div>
      </div>
      <div class="stat-card">
        <div class="stat-info">
          <h4><LucideAlertTriangle class="icon-small" /> 已超期</h4>
          <div class="stat-number red">{{ overview.overdueCount || 0 }}</div>
          <small>需要关注</small>
        </div>
        <div class="stat-icon alarm-icon">
          <LucideBell class="icon" />
        </div>
      </div>
      <div class="stat-card">
        <div class="stat-info">
          <h4><LucideCheckCircle class="icon-small" /> 已办结</h4>
          <div class="stat-number green">{{ overview.completedCount || 0 }}</div>
          <small>完成率 {{ completionRate }}%</small>
        </div>
        <div class="stat-icon green-icon">
          <LucideFileCheck class="icon" />
        </div>
      </div>
    </div>

    <!-- 图表区域 -->
    <div class="row-charts">
      <!-- 月度趋势图 -->
      <div class="card">
        <div class="card-header">
          <span><LucideBarChart3 class="icon-small" /> 工单月度趋势</span>
        </div>
        <div ref="trendChartRef" class="chart-container"></div>
      </div>

      <!-- 状态分布 -->
      <div class="card">
        <div class="card-header">
          <span><LucidePieChart class="icon-small" /> 工单状态分布</span>
        </div>
        <div class="distribution-list">
          <div class="distribution-item" v-for="(item, i) in statusDistribution" :key="i">
            <div class="distribution-header">
              <span class="distribution-name">{{ item.name }}</span>
              <span class="distribution-value">{{ item.count }}</span>
            </div>
            <div class="distribution-bar">
              <div class="distribution-progress" :style="{ width: `${item.percent}%`, background: item.color }"></div>
            </div>
          </div>
        </div>
      </div>
    </div>

    <!-- 最近工单列表 -->
    <div class="card">
      <div class="card-header">
        <span><LucideClipboardList class="icon-small" /> 最近工单</span>
      </div>
      <div class="alert-table">
        <div class="alert-row alert-header">
          <div class="alert-col">工单编号</div>
          <div class="alert-col">工单标题</div>
          <div class="alert-col">信访人</div>
          <div class="alert-col">状态</div>
          <div class="alert-col">登记时间</div>
          <div class="alert-col">办理期限</div>
        </div>
        <div class="alert-row" v-for="order in recentOrders" :key="order.id">
          <div class="alert-col order-no" :class="getOrderNoClass(order)">
            {{ order.xfCaseNo }}
          </div>
          <div class="alert-col title-col" :class="getOrderNoClass(order)">
            {{ order.title }}
          </div>
          <div class="alert-col">{{ order.petitionerName || '-' }}</div>
          <div class="alert-col">
            <span class="status-tag" :class="getStatusClass(order.status)">{{ getStatusText(order.status) }}</span>
          </div>
          <div class="alert-col time-col">{{ formatTime(order.registerTime) }}</div>
          <div class="alert-col time-col">{{ order.deadline || '-' }}</div>
        </div>
      </div>
    </div>

    <!-- 超期预警 -->
    <div class="card overdue-card" v-if="overdueOrders.length > 0">
      <div class="card-header overdue-header">
        <span><LucideAlertTriangle class="icon-small" /> 超期预警</span>
        <span class="overdue-count">{{ overdueOrders.length }} 项超期</span>
      </div>
      <div class="overdue-list">
        <div class="overdue-item" v-for="order in overdueOrders" :key="order.id">
          <div class="overdue-info">
            <span class="overdue-no">{{ order.xfCaseNo }}</span>
            <span class="overdue-title">{{ order.title }}</span>
            <span class="overdue-dept">{{ order.petitionerName }}</span>
          </div>
          <div class="overdue-badge">超期{{ order.overdueCount || 1 }}次</div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, onMounted, onUnmounted } from 'vue'
import * as echarts from 'echarts'
import {
  LucideDatabase, LucideLoader, LucideAlertTriangle, LucideCheckCircle,
  LucideClipboardList, LucideClock, LucideBell, LucideFileCheck,
  LucideBarChart3, LucidePieChart
} from 'lucide-vue-next'
import { getOverview, getMonthlyTrend } from '@/api/xf/statistics'
import { listWorkOrder } from '@/api/xf/workOrder'
import type { StatisticsOverview, MonthlyTrend } from '@/types/api/xf/statistics'
import type { XfWorkOrder } from '@/types/api/xf/workOrder'

const overview = ref<StatisticsOverview>({
  totalCount: 0, inProgressCount: 0, overdueCount: 0,
  completedCount: 0, pendingCount: 0, returnedCount: 0
})

const recentOrders = ref<XfWorkOrder[]>([])
const overdueOrders = ref<XfWorkOrder[]>([])

const completionRate = computed(() => {
  if (!overview.value.totalCount) return 0
  return Math.round((overview.value.completedCount / overview.value.totalCount) * 100)
})

const statusDistribution = computed(() => {
  const total = overview.value.totalCount || 1
  return [
    { name: '待派单', count: overview.value.pendingCount, percent: Math.round((overview.value.pendingCount / total) * 100), color: '#F59E0B' },
    { name: '待提交', count: overview.value.inProgressCount, percent: Math.round((overview.value.inProgressCount / total) * 100), color: '#3B82F6' },
    { name: '已超期', count: overview.value.overdueCount, percent: Math.round((overview.value.overdueCount / total) * 100), color: '#EF4444' },
    { name: '已办结', count: overview.value.completedCount, percent: Math.round((overview.value.completedCount / total) * 100), color: '#10B981' },
    { name: '已退回', count: overview.value.returnedCount, percent: Math.round((overview.value.returnedCount / total) * 100), color: '#8B5CF6' }
  ]
})

const trendChartRef = ref<HTMLElement>()
let trendChart: echarts.ECharts | null = null

async function loadOverview() {
  try {
    const res = await getOverview()
    if (res.data) overview.value = res.data
  } catch (error) {
    console.error('加载统计概览失败:', error)
  }
}

async function loadRecentOrders() {
  try {
    const res = await listWorkOrder({ pageNum: 1, pageSize: 10 })
    recentOrders.value = (res.rows || []) as XfWorkOrder[]
  } catch (error) {
    console.error('加载最近工单失败:', error)
  }
}

async function loadOverdueOrders() {
  try {
    const res = await listWorkOrder({ pageNum: 1, pageSize: 20, status: '5' })
    overdueOrders.value = (res.rows || []) as XfWorkOrder[]
  } catch (error) {
    console.error('加载超期工单失败:', error)
  }
}

async function loadTrendData() {
  try {
    const res = await getMonthlyTrend()
    const data = (res.data || []) as MonthlyTrend[]
    initTrendChart(data)
  } catch (error) {
    console.error('加载趋势数据失败:', error)
  }
}

function initTrendChart(data: MonthlyTrend[]) {
  if (!trendChartRef.value) return
  trendChart = echarts.init(trendChartRef.value)

  const months = data.map(d => d.month)
  const newCounts = data.map(d => d.newCount)
  const completedCounts = data.map(d => d.completedCount)

  trendChart.setOption({
    tooltip: { trigger: 'axis' },
    legend: { data: ['新增工单', '办结工单'], textStyle: { color: '#5B6E8C' } },
    grid: { top: 30, left: 45, right: 20, bottom: 30 },
    xAxis: {
      type: 'category',
      data: months,
      axisLabel: { fontSize: 11, color: '#5B6E8C' }
    },
    yAxis: {
      type: 'value',
      name: '数量',
      nameTextStyle: { color: '#3A5E88' },
      splitLine: { lineStyle: { color: '#EAEFF4' } }
    },
    series: [
      {
        name: '新增工单', data: newCounts, type: 'bar', barWidth: 20,
        itemStyle: {
          color: new echarts.graphic.LinearGradient(0, 0, 0, 1, [
            { offset: 0, color: '#3B82F6' },
            { offset: 1, color: '#93C5FD' }
          ]),
          borderRadius: [4, 4, 0, 0]
        }
      },
      {
        name: '办结工单', data: completedCounts, type: 'line', smooth: true,
        itemStyle: { color: '#10B981' },
        areaStyle: { color: new echarts.graphic.LinearGradient(0, 0, 0, 1, [
          { offset: 0, color: 'rgba(16, 185, 129, 0.3)' },
          { offset: 1, color: 'rgba(16, 185, 129, 0.02)' }
        ]) }
      }
    ]
  })
}

function handleResize() { trendChart?.resize() }

function getOrderNoClass(order: XfWorkOrder) {
  if (order.status === '5') return 'text-overdue'
  if (order.status === '1' || order.status === '2') return 'text-progress'
  return ''
}

function getStatusClass(status: string | undefined) {
  if (!status) return ''
  const map: Record<string, string> = {
    '0': 'status-pending', '1': 'status-progress', '2': 'status-reported',
    '3': 'status-done', '4': 'status-returned', '5': 'status-overdue'
  }
  return map[status] || ''
}

function getStatusText(status: string | undefined) {
  if (!status) return '未知'
  const map: Record<string, string> = {
    '0': '待派单', '1': '待提交', '2': '审批中',
    '3': '已办结', '4': '已退回', '5': '已超期'
  }
  return map[status] || status
}

function formatTime(time: string | undefined) {
  if (!time) return ''
  return time.replace('T', ' ').substring(0, 16)
}

onMounted(() => {
  loadOverview()
  loadRecentOrders()
  loadOverdueOrders()
  loadTrendData()
  window.addEventListener('resize', handleResize)
})

onUnmounted(() => {
  trendChart?.dispose()
  window.removeEventListener('resize', handleResize)
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

.police-page { padding: 24px; background: $police-bg; min-height: 100%; }

.stats-grid { display: grid; grid-template-columns: repeat(4, 1fr); gap: 20px; margin-bottom: 24px; }

.stat-card {
  background: white; border-radius: 20px; padding: 20px; border: 1px solid $police-border;
  display: flex; align-items: center; justify-content: space-between; transition: all 0.2s;
  &:hover { transform: translateY(-2px); box-shadow: 0 4px 12px rgba(0, 0, 0, 0.08); }
}

.stat-info {
  h4 { font-size: 14px; font-weight: 500; color: $police-text-muted; margin-bottom: 8px; display: flex; align-items: center; gap: 6px; }
  small { font-size: 12px; color: $police-text-muted; margin-top: 4px; display: block; }
}

.stat-number {
  font-size: 32px; font-weight: 700; color: $police-primary;
  &.blue { color: $police-blue; }
  &.red { color: #EF4444; }
  &.green { color: #10B981; }
}

.stat-icon {
  width: 52px; height: 52px; background: #EFF4FF; border-radius: 30px;
  display: flex; align-items: center; justify-content: center; color: $police-light-blue;
  .icon { width: 26px; height: 26px; }
  &.blue-icon { background: #DBEAFE; color: $police-blue; }
  &.alarm-icon { background: rgba(239, 68, 68, 0.1); color: #EF4444; }
  &.green-icon { background: rgba(16, 185, 129, 0.1); color: #10B981; }
}

.icon-small { width: 16px; height: 16px; }

.card {
  background: white; border-radius: 20px; padding: 20px 24px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.02); border: 1px solid $police-border;
  margin-bottom: 24px;
}

.card-header {
  display: flex; justify-content: space-between; align-items: center;
  margin-bottom: 18px; font-weight: 600; color: #0F2B4D;
  border-left: 4px solid $police-light-blue; padding-left: 14px;
  span { display: flex; align-items: center; gap: 6px; }

  &.overdue-header { border-left-color: #EF4444; }
}

.row-charts { display: grid; grid-template-columns: 1fr 1fr; gap: 24px; margin-bottom: 24px; }
.chart-container { height: 280px; width: 100%; }

.distribution-list { padding: 8px 0; }
.distribution-item { margin-bottom: 16px; &:last-child { margin-bottom: 0; } }
.distribution-header { display: flex; justify-content: space-between; margin-bottom: 8px; }
.distribution-name { font-size: 13px; color: $police-text; }
.distribution-value { font-size: 13px; font-weight: 600; color: $police-blue; }
.distribution-bar { height: 8px; background: #E2E8F0; border-radius: 8px; overflow: hidden; }
.distribution-progress { height: 100%; border-radius: 8px; transition: width 0.3s; }

.alert-table { border: 1px solid $police-border; border-radius: 12px; overflow: hidden; }

.alert-row {
  display: flex; align-items: center; padding: 14px 16px; border-bottom: 1px solid $police-border;
  &:last-child { border-bottom: none; }
  &.alert-header { background: #FAFAFA; font-weight: 600; color: $police-text-muted; font-size: 12px; }
  &:not(.alert-header):hover { background: #F5F5F5; }
}

.alert-col {
  flex: 1; font-size: 13px; color: $police-text;
  &.order-no { font-weight: 600; }
  &.order-no.text-overdue, &.title-col.text-overdue { color: #F56C6C; }
  &.order-no.text-progress, &.title-col.text-progress { color: #409EFF; }
  &.title-col { overflow: hidden; text-overflow: ellipsis; white-space: nowrap; }
  &.time-col { color: $police-text-muted; font-size: 12px; }
}

.status-tag {
  padding: 4px 10px; border-radius: 20px; font-size: 11px; font-weight: 600;
  &.status-pending { background: rgba(245, 158, 11, 0.1); color: #F59E0B; }
  &.status-progress { background: rgba(59, 130, 246, 0.1); color: #3B82F6; }
  &.status-reported { background: rgba(79, 70, 229, 0.1); color: #4F46E5; }
  &.status-done { background: rgba(16, 185, 129, 0.1); color: #10B981; }
  &.status-returned { background: rgba(139, 92, 246, 0.1); color: #8B5CF6; }
  &.status-overdue { background: rgba(239, 68, 68, 0.1); color: #EF4444; }
}

.overdue-card { border-color: #FEE2E2; }
.overdue-count { font-size: 13px; color: #EF4444; font-weight: 500; }

.overdue-list { display: flex; flex-direction: column; gap: 8px; }

.overdue-item {
  display: flex; justify-content: space-between; align-items: center;
  padding: 12px 16px; border-radius: 10px; border: 1px solid #FEE2E2;
  background: #FEF2F2; transition: all 0.2s;

  &:hover { background: #FEE2E2; }
}

.overdue-info {
  display: flex; gap: 16px; align-items: center;
  .overdue-no { font-size: 13px; font-weight: 600; color: #EF4444; }
  .overdue-title { font-size: 13px; color: $police-text; }
  .overdue-dept { font-size: 12px; color: $police-text-muted; }
}

.overdue-badge {
  padding: 4px 10px; border-radius: 10px; font-size: 11px; font-weight: 600;
  background: #EF4444; color: white;
}

@media (max-width: 1100px) {
  .stats-grid { grid-template-columns: repeat(2, 1fr); }
  .row-charts { grid-template-columns: 1fr; }
}
</style>
