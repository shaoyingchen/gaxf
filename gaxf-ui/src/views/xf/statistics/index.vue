<template>
  <div class="statistics-wrapper">
    <!-- 统计卡片 -->
    <div class="stats-grid">
      <div class="stat-card">
        <div class="stat-icon-area total">
          <LucideDatabase :size="24" />
        </div>
        <div class="stat-info">
          <span class="stat-label">工单总数</span>
          <span class="stat-value">{{ overview.totalCount || 0 }}</span>
        </div>
      </div>
      <div class="stat-card">
        <div class="stat-icon-area progress">
          <LucideLoader :size="24" />
        </div>
        <div class="stat-info">
          <span class="stat-label">待提交</span>
          <span class="stat-value">{{ overview.inProgressCount || 0 }}</span>
        </div>
      </div>
      <div class="stat-card">
        <div class="stat-icon-area overdue">
          <LucideAlertTriangle :size="24" />
        </div>
        <div class="stat-info">
          <span class="stat-label">已超期</span>
          <span class="stat-value danger">{{ overview.overdueCount || 0 }}</span>
        </div>
      </div>
      <div class="stat-card">
        <div class="stat-icon-area done">
          <LucideCheckCircle :size="24" />
        </div>
        <div class="stat-info">
          <span class="stat-label">已办结</span>
          <span class="stat-value success">{{ overview.completedCount || 0 }}</span>
        </div>
      </div>
    </div>

    <!-- 筛选条件 -->
    <div class="filter-bar">
      <el-form :model="filterParams" class="filter-form">
        <div class="filter-row">
          <el-form-item label="单位">
            <el-tree-select
              v-model="filterParams.deptId"
              :data="deptTreeData"
              :props="{ label: 'label', children: 'children' }"
              node-key="id"
              placeholder="请选择单位"
              check-strictly
              clearable
              style="width: 220px"
            />
          </el-form-item>
          <el-form-item label="时间范围">
            <el-date-picker
              v-model="timeRange"
              type="daterange"
              range-separator="至"
              start-placeholder="开始日期"
              end-placeholder="结束日期"
              value-format="YYYY-MM-DD"
              @change="handleTimeChange"
            />
          </el-form-item>
          <div class="filter-actions">
            <button class="filter-btn primary" @click.prevent="loadAllData">
              <LucideSearch :size="13" />
              <span>查询</span>
            </button>
            <button class="filter-btn" @click.prevent="handleResetFilter">
              <LucideRefreshCcw :size="13" />
              <span>重置</span>
            </button>
          </div>
        </div>
      </el-form>
    </div>

    <!-- 图表区域 -->
    <div class="charts-row">
      <!-- 各单位办理率 - 柱状图 -->
      <div class="chart-card">
        <div class="chart-header">
          <span><LucideBarChart3 :size="16" /> 各单位办理率</span>
        </div>
        <div ref="completionChartRef" class="chart-container"></div>
      </div>

      <!-- 状态分布 - 饼图 -->
      <div class="chart-card">
        <div class="chart-header">
          <span><LucidePieChart :size="16" /> 状态分布</span>
        </div>
        <div ref="pieChartRef" class="chart-container"></div>
      </div>
    </div>

    <!-- 月度趋势图 -->
    <div class="chart-card full-width">
      <div class="chart-header">
        <span><LucideTrendingUp :size="16" /> 月度趋势</span>
      </div>
      <div ref="trendChartRef" class="chart-container trend-chart"></div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted, onUnmounted } from 'vue'
import * as echarts from 'echarts'
import {
  LucideDatabase, LucideLoader, LucideAlertTriangle, LucideCheckCircle,
  LucideBarChart3, LucidePieChart, LucideTrendingUp, LucideSearch, LucideRefreshCcw
} from 'lucide-vue-next'
import { getOverview, getCompletionRate, getMonthlyTrend } from '@/api/xf/statistics'
import { deptTree } from '@/api/system/dept'
import type { StatisticsOverview, CompletionRateStatistics, MonthlyTrend, StatisticsQueryParams } from '@/types/api/xf/statistics'
import type { TreeSelect } from '@/types'

const overview = ref<StatisticsOverview>({
  totalCount: 0, inProgressCount: 0, overdueCount: 0,
  completedCount: 0, pendingCount: 0, returnedCount: 0
})

const filterParams = ref<StatisticsQueryParams>({})
const timeRange = ref<[string, string] | null>(null)
const deptTreeData = ref<TreeSelect[]>([])

const completionChartRef = ref<HTMLElement>()
const pieChartRef = ref<HTMLElement>()
const trendChartRef = ref<HTMLElement>()

let completionChart: echarts.ECharts | null = null
let pieChart: echarts.ECharts | null = null
let trendChart: echarts.ECharts | null = null

function handleTimeChange(val: [string, string] | null) {
  if (val) {
    filterParams.value.beginTime = val[0]
    filterParams.value.endTime = val[1]
  } else {
    filterParams.value.beginTime = undefined
    filterParams.value.endTime = undefined
  }
}

function handleResetFilter() {
  filterParams.value = {}
  timeRange.value = null
  loadAllData()
}

async function loadOverview() {
  try {
    const res = await getOverview(filterParams.value)
    if (res.data) overview.value = res.data
  } catch (error) {
    console.error('加载统计概览失败:', error)
  }
}

async function loadCompletionRate() {
  try {
    const res = await getCompletionRate(filterParams.value)
    const data = (res.data || []) as CompletionRateStatistics[]
    initCompletionChart(data)
  } catch (error) {
    console.error('加载办理率失败:', error)
  }
}

async function loadMonthlyTrend() {
  try {
    const res = await getMonthlyTrend(filterParams.value)
    const data = (res.data || []) as MonthlyTrend[]
    initTrendChart(data)
  } catch (error) {
    console.error('加载趋势数据失败:', error)
  }
}

async function loadDeptTree() {
  try {
    const res = await deptTree()
    deptTreeData.value = (res.data || []) as TreeSelect[]
  } catch (error) {
    console.error('加载部门树失败:', error)
  }
}

async function loadAllData() {
  await loadOverview()
  loadCompletionRate()
  loadMonthlyTrend()
  initPieChart()
}

function initCompletionChart(data: CompletionRateStatistics[]) {
  if (!completionChartRef.value) return
  if (!completionChart) {
    completionChart = echarts.init(completionChartRef.value)
  }

  const deptNames = data.map(d => d.deptName)
  const rates = data.map(d => d.completionRate)

  completionChart.setOption({
    tooltip: { trigger: 'axis', formatter: '{b}: {c}%' },
    grid: { top: 30, left: 60, right: 20, bottom: 40 },
    xAxis: {
      type: 'category',
      data: deptNames,
      axisLabel: { fontSize: 11, color: '#5B6E8C', rotate: deptNames.length > 6 ? 30 : 0 }
    },
    yAxis: {
      type: 'value',
      name: '办理率(%)',
      nameTextStyle: { color: '#3A5E88' },
      max: 100,
      splitLine: { lineStyle: { color: '#EAEFF4' } }
    },
    series: [{
      data: rates,
      type: 'bar',
      barWidth: 24,
      itemStyle: {
        color: new echarts.graphic.LinearGradient(0, 0, 0, 1, [
          { offset: 0, color: '#3B82F6' },
          { offset: 1, color: '#93C5FD' }
        ]),
        borderRadius: [4, 4, 0, 0]
      }
    }]
  })
}

function initPieChart() {
  if (!pieChartRef.value) return
  if (!pieChart) {
    pieChart = echarts.init(pieChartRef.value)
  }

  const data = [
    { value: overview.value.pendingCount, name: '待派单', itemStyle: { color: '#F59E0B' } },
    { value: overview.value.inProgressCount, name: '待提交', itemStyle: { color: '#3B82F6' } },
    { value: overview.value.overdueCount, name: '已超期', itemStyle: { color: '#EF4444' } },
    { value: overview.value.completedCount, name: '已办结', itemStyle: { color: '#10B981' } },
    { value: overview.value.returnedCount, name: '已退回', itemStyle: { color: '#8B5CF6' } }
  ].filter(d => d.value > 0)

  pieChart.setOption({
    tooltip: { trigger: 'item', formatter: '{b}: {c} ({d}%)' },
    legend: {
      orient: 'vertical',
      right: 20,
      top: 'center',
      textStyle: { color: '#5B6E8C', fontSize: 12 }
    },
    series: [{
      type: 'pie',
      radius: ['40%', '70%'],
      center: ['40%', '50%'],
      avoidLabelOverlap: false,
      itemStyle: { borderRadius: 8, borderColor: '#fff', borderWidth: 2 },
      label: { show: false },
      emphasis: { label: { show: true, fontSize: 14, fontWeight: 'bold' } },
      data: data.length > 0 ? data : [{ value: 0, name: '暂无数据', itemStyle: { color: '#E2E8F0' } }]
    }]
  })
}

function initTrendChart(data: MonthlyTrend[]) {
  if (!trendChartRef.value) return
  if (!trendChart) {
    trendChart = echarts.init(trendChartRef.value)
  }

  const months = data.map(d => d.month)
  const newCounts = data.map(d => d.newCount)
  const completedCounts = data.map(d => d.completedCount)
  const overdueCounts = data.map(d => d.overdueCount)

  trendChart.setOption({
    tooltip: { trigger: 'axis' },
    legend: { data: ['新增工单', '办结工单', '超期工单'], textStyle: { color: '#5B6E8C' } },
    grid: { top: 40, left: 50, right: 20, bottom: 30 },
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
        name: '新增工单', type: 'line', data: newCounts, smooth: true,
        itemStyle: { color: '#3B82F6' },
        areaStyle: { color: new echarts.graphic.LinearGradient(0, 0, 0, 1, [
          { offset: 0, color: 'rgba(59, 130, 246, 0.3)' },
          { offset: 1, color: 'rgba(59, 130, 246, 0.02)' }
        ]) }
      },
      {
        name: '办结工单', type: 'line', data: completedCounts, smooth: true,
        itemStyle: { color: '#10B981' }
      },
      {
        name: '超期工单', type: 'line', data: overdueCounts, smooth: true,
        itemStyle: { color: '#EF4444' },
        lineStyle: { type: 'dashed' }
      }
    ]
  })
}

function handleResize() {
  completionChart?.resize()
  pieChart?.resize()
  trendChart?.resize()
}

onMounted(async () => {
  await loadDeptTree()
  loadAllData()
  window.addEventListener('resize', handleResize)
})

onUnmounted(() => {
  completionChart?.dispose()
  pieChart?.dispose()
  trendChart?.dispose()
  window.removeEventListener('resize', handleResize)
})
</script>

<style lang="scss" scoped>
$police-primary: #1E3A8A;
$police-blue: #2563EB;
$police-light-blue: #3B82F6;
$police-bg: #F8FAFE;
$police-border: #E2E8F0;
$police-text: #1E293B;
$police-text-muted: #5B6E8C;

.statistics-wrapper {
  padding: 12px;
  background: $police-bg;
  min-height: 100%;
}

.stats-grid {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: 16px;
  margin-bottom: 16px;
}

.stat-card {
  background: white;
  border-radius: 12px;
  padding: 16px;
  border: 1px solid $police-border;
  display: flex;
  align-items: center;
  gap: 14px;
  transition: all 0.2s;

  &:hover { transform: translateY(-2px); box-shadow: 0 4px 12px rgba(0, 0, 0, 0.08); }
}

.stat-icon-area {
  width: 52px;
  height: 52px;
  border-radius: 14px;
  display: flex;
  align-items: center;
  justify-content: center;
  flex-shrink: 0;

  &.total { background: #EFF4FF; color: $police-light-blue; }
  &.progress { background: #DBEAFE; color: $police-blue; }
  &.overdue { background: #FEE2E2; color: #EF4444; }
  &.done { background: #D1FAE5; color: #10B981; }
}

.stat-info {
  display: flex;
  flex-direction: column;
  gap: 4px;
}

.stat-label { font-size: 14px; color: $police-text-muted; }
.stat-value {
  font-size: 28px;
  font-weight: 700;
  color: $police-primary;

  &.danger { color: #EF4444; }
  &.success { color: #10B981; }
}

.filter-bar {
  background: white;
  border-radius: 12px;
  padding: 12px 16px;
  border: 1px solid $police-border;
  margin-bottom: 16px;
}

.filter-form {
  :deep(.el-form-item) {
    margin-bottom: 0;
    margin-right: 0;
  }

  :deep(.el-form-item__label) {
    font-size: 12px;
    color: $police-text-muted;
    font-weight: 500;
    padding-right: 8px;
  }

  :deep(.el-input__wrapper),
  :deep(.el-select__wrapper),
  :deep(.el-tree-select),
  :deep(.el-date-editor.el-input__wrapper),
  :deep(.el-range-editor.el-input__wrapper) {
    border-radius: 6px;
    background: white;
    box-shadow: 0 0 0 1px #E2E8F0 inset;
    transition: all 0.2s;
    &:hover { box-shadow: 0 0 0 1px $police-light-blue inset; }
    &.is-focus { box-shadow: 0 0 0 1px $police-blue inset !important; }
  }
}

.filter-row {
  display: flex;
  align-items: center;
  gap: 16px;
}

.filter-actions {
  display: flex;
  gap: 8px;
  margin-left: auto;
}

.filter-btn {
  display: inline-flex;
  align-items: center;
  gap: 4px;
  padding: 6px 14px;
  border-radius: 6px;
  font-size: 13px;
  font-weight: 500;
  border: 1px solid $police-border;
  background: white;
  color: $police-text;
  cursor: pointer;
  transition: all 0.2s;
  height: 32px;
  line-height: 1;

  &:hover { border-color: $police-light-blue; color: $police-blue; }

  &.primary {
    background: linear-gradient(135deg, $police-primary 0%, $police-blue 100%);
    border-color: transparent;
    color: white;
    box-shadow: 0 2px 6px rgba(37, 99, 235, 0.2);
    &:hover { color: white; box-shadow: 0 3px 10px rgba(37, 99, 235, 0.3); }
  }
}

.charts-row {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 16px;
  margin-bottom: 16px;
}

.chart-card {
  background: white;
  border-radius: 12px;
  padding: 16px 18px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.02);
  border: 1px solid $police-border;

  &.full-width { grid-column: 1 / -1; }
}

.chart-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 18px;
  font-weight: 600;
  color: #0F2B4D;
  border-left: 4px solid $police-light-blue;
  padding-left: 14px;

  span { display: flex; align-items: center; gap: 6px; font-size: 14px; }
}

.chart-container {
  height: 280px;
  width: 100%;

  &.trend-chart { height: 320px; }
}

@media (max-width: 1100px) {
  .stats-grid { grid-template-columns: repeat(2, 1fr); }
  .charts-row { grid-template-columns: 1fr; }
}
</style>
