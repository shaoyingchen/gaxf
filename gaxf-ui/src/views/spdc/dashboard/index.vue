<template>
  <div class="police-page">
    <!-- 统计卡片区域 -->
    <div class="stats-grid">
      <div class="stat-card">
        <div class="stat-info">
          <h4><LucideDatabase class="icon-small" /> 点位总数</h4>
          <div class="stat-number">12,840</div>
          <small>覆盖率 +12%</small>
        </div>
        <div class="stat-icon">
          <LucideCamera class="icon" />
        </div>
      </div>
      <div class="stat-card">
        <div class="stat-info">
          <h4><LucideAlertTriangle class="icon-small" /> 今日预警</h4>
          <div class="stat-number">156</div>
          <small>较昨日 -5%</small>
        </div>
        <div class="stat-icon alarm-icon">
          <LucideBell class="icon" />
        </div>
      </div>
      <div class="stat-card">
        <div class="stat-info">
          <h4><LucideActivity class="icon-small" /> 在线率</h4>
          <div class="stat-number">99.2%</div>
          <small>在线设备 12,738</small>
        </div>
        <div class="stat-icon">
          <LucideWifi class="icon" />
        </div>
      </div>
      <div class="stat-card">
        <div class="stat-info">
          <h4><LucideCheckCircle class="icon-small" /> 整改率</h4>
          <div class="stat-number">88.5%</div>
          <small>较上周 +2.4%</small>
        </div>
        <div class="stat-icon">
          <LucideFileCheck class="icon" />
        </div>
      </div>
    </div>

    <!-- 图表区域 -->
    <div class="row-charts">
      <!-- 督察违规趋势图 -->
      <div class="card">
        <div class="card-header">
          <span><LucideBarChart3 class="icon-small" /> 督察违规趋势（近12月）</span>
          <LucideMoreHorizontal class="icon-muted" />
        </div>
        <div ref="trendChartRef" class="chart-container"></div>
      </div>

      <!-- 终端在线分布 -->
      <div class="card">
        <div class="card-header">
          <span><LucideSmartphone class="icon-small" /> 终端在线分布</span>
          <LucideRefreshCw class="icon-btn" />
        </div>
        <div class="distribution-list">
          <div class="distribution-item" v-for="(item, i) in distributionData" :key="i">
            <div class="distribution-header">
              <span class="distribution-name">{{ item.name }}</span>
              <span class="distribution-value">{{ item.percent }}%</span>
            </div>
            <div class="distribution-bar">
              <div class="distribution-progress" :style="{ width: `${item.percent}%` }"></div>
            </div>
          </div>
        </div>
      </div>
    </div>

    <!-- 最新预警列表 -->
    <div class="card">
      <div class="card-header">
        <span><LucideBell class="icon-small" /> 最新预警事件</span>
        <span class="btn-outline"><LucideArrowRight class="icon-small" /> 查看全部</span>
      </div>
      <div class="alert-table">
        <div class="alert-row alert-header">
          <div class="alert-col">预警类型</div>
          <div class="alert-col">等级</div>
          <div class="alert-col">位置</div>
          <div class="alert-col">时间</div>
          <div class="alert-col">状态</div>
        </div>
        <div class="alert-row" v-for="(alert, i) in recentAlerts" :key="i">
          <div class="alert-col alert-type">
            <LucideAlertCircle class="icon-small" />
            <span>{{ alert.type }}</span>
          </div>
          <div class="alert-col">
            <span class="level-tag" :class="alert.levelClass">{{ alert.level }}</span>
          </div>
          <div class="alert-col alert-location">
            <LucideMapPin class="icon-small" />
            <span>{{ alert.location }}</span>
          </div>
          <div class="alert-col alert-time">
            <LucideClock class="icon-small" />
            <span>{{ alert.time }}</span>
          </div>
          <div class="alert-col">
            <span class="status-tag" :class="alert.statusClass">{{ alert.status }}</span>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted, onUnmounted } from 'vue'
import * as echarts from 'echarts'
import {
  LucideDatabase, LucideAlertTriangle, LucideActivity, LucideCheckCircle,
  LucideCamera, LucideBell, LucideWifi, LucideFileCheck, LucideBarChart3,
  LucideSmartphone, LucideMoreHorizontal, LucideRefreshCw, LucideArrowRight,
  LucideAlertCircle, LucideMapPin, LucideClock
} from 'lucide-vue-next'

const trendChartRef = ref<HTMLElement>()
let trendChart: echarts.ECharts | null = null

const distributionData = [
  { name: '办公区监控', percent: 92 },
  { name: '办案区', percent: 85 },
  { name: '服务大厅', percent: 78 },
  { name: '外部卡口', percent: 65 }
]

const recentAlerts = [
  { type: '未按规定佩戴单警装备', level: '中危', levelClass: 'level-mid', location: '办案区2号室', time: '14:20:33', status: '待处理', statusClass: 'status-pending' },
  { type: '办公区域吸烟行为', level: '低危', levelClass: 'level-low', location: '3层公共休息区', time: '13:15:02', status: '已确认', statusClass: 'status-done' },
  { type: '非法闯入核心机房', level: '高危', levelClass: 'level-high', location: '核心机房', time: '12:00:15', status: '待核实', statusClass: 'status-pending' },
  { type: '离岗超时预警', level: '低危', levelClass: 'level-low', location: '1层咨询台', time: '11:45:10', status: '误报', statusClass: 'status-false' },
]

function initCharts() {
  if (trendChartRef.value) {
    trendChart = echarts.init(trendChartRef.value)
    trendChart.setOption({
      tooltip: { trigger: 'axis' },
      grid: { top: 30, left: 45, right: 20, bottom: 30 },
      xAxis: {
        type: 'category',
        data: ['01月', '02月', '03月', '04月', '05月', '06月', '07月', '08月', '09月', '10月', '11月', '12月'],
        axisLabel: { fontSize: 11, color: '#5B6E8C' }
      },
      yAxis: {
        type: 'value',
        name: '违规次数',
        nameTextStyle: { color: '#3A5E88' },
        splitLine: { lineStyle: { color: '#EAEFF4' } }
      },
      series: [{
        data: [40, 25, 60, 80, 55, 90, 70, 45, 30, 85, 65, 50],
        type: 'bar',
        barWidth: 20,
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
}

function handleResize() {
  trendChart?.resize()
}

onMounted(() => {
  initCharts()
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

.police-page {
  padding: 24px;
  background: $police-bg;
  min-height: 100%;
}

.stats-grid {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: 20px;
  margin-bottom: 24px;
}

.stat-card {
  background: white;
  border-radius: 20px;
  padding: 20px;
  border: 1px solid $police-border;
  display: flex;
  align-items: center;
  justify-content: space-between;
  transition: all 0.2s;

  &:hover {
    transform: translateY(-2px);
    box-shadow: 0 4px 12px rgba(0, 0, 0, 0.08);
  }
}

.stat-info h4 {
  font-size: 14px;
  font-weight: 500;
  color: $police-text-muted;
  margin-bottom: 8px;
  display: flex;
  align-items: center;
  gap: 6px;

  small {
    font-size: 12px;
    color: $police-text-muted;
    margin-top: 4px;
    display: block;
  }
}

.stat-number {
  font-size: 32px;
  font-weight: 700;
  color: $police-primary;
}

.stat-icon {
  width: 52px;
  height: 52px;
  background: #EFF4FF;
  border-radius: 30px;
  display: flex;
  align-items: center;
  justify-content: center;
  color: $police-light-blue;

  .icon { width: 26px; height: 26px; }
  &.alarm-icon { background: rgba(239, 68, 68, 0.1); color: #EF4444; }
}

.icon-small { width: 16px; height: 16px; }
.icon-muted { width: 16px; height: 16px; color: #94A3B8; }
.icon-btn { width: 16px; height: 16px; color: $police-blue; cursor: pointer; }

.card {
  background: white;
  border-radius: 20px;
  padding: 20px 24px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.02);
  border: 1px solid $police-border;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 18px;
  font-weight: 600;
  color: #0F2B4D;
  border-left: 4px solid $police-light-blue;
  padding-left: 14px;

  span { display: flex; align-items: center; gap: 6px; }
}

.row-charts {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 24px;
  margin-bottom: 24px;
}

.chart-container {
  height: 280px;
  width: 100%;
}

.distribution-list {
  padding: 8px 0;
}

.distribution-item {
  margin-bottom: 16px;

  &:last-child { margin-bottom: 0; }
}

.distribution-header {
  display: flex;
  justify-content: space-between;
  margin-bottom: 8px;
}

.distribution-name {
  font-size: 13px;
  color: $police-text;
}

.distribution-value {
  font-size: 13px;
  font-weight: 600;
  color: $police-blue;
}

.distribution-bar {
  height: 8px;
  background: #E2E8F0;
  border-radius: 8px;
  overflow: hidden;
}

.distribution-progress {
  height: 100%;
  background: linear-gradient(90deg, $police-blue, $police-light-blue);
  border-radius: 8px;
  transition: width 0.3s;
}

.btn-outline {
  border: 1px solid #CBD5E1;
  background: white;
  border-radius: 30px;
  padding: 5px 12px;
  font-size: 12px;
  color: $police-blue;
  cursor: pointer;
  display: inline-flex;
  align-items: center;
  gap: 4px;

  &:hover { background: #EFF6FF; border-color: #93C5FD; }
}

.alert-table {
  border: 1px solid $police-border;
  border-radius: 12px;
  overflow: hidden;
}

.alert-row {
  display: flex;
  align-items: center;
  padding: 14px 16px;
  border-bottom: 1px solid $police-border;

  &:last-child { border-bottom: none; }
  &.alert-header { background: #FAFAFA; font-weight: 600; color: $police-text-muted; font-size: 12px; }
  &:not(.alert-header):hover { background: #F5F5F5; }
}

.alert-col {
  flex: 1;
  display: flex;
  align-items: center;
  gap: 6px;
  font-size: 13px;
  color: $police-text;

  &.alert-type { font-weight: 500; }
  &.alert-location, &.alert-time { color: $police-text-muted; font-size: 12px; }
}

.level-tag {
  padding: 4px 10px;
  border-radius: 20px;
  font-size: 11px;
  font-weight: 600;

  &.level-high { background: rgba(239, 68, 68, 0.1); color: #EF4444; }
  &.level-mid { background: rgba(245, 158, 11, 0.1); color: #F59E0B; }
  &.level-low { background: rgba(59, 130, 246, 0.1); color: #3B82F6; }
}

.status-tag {
  padding: 4px 10px;
  border-radius: 20px;
  font-size: 11px;
  font-weight: 600;

  &.status-pending { background: rgba(245, 158, 11, 0.1); color: #F59E0B; }
  &.status-done { background: rgba(16, 185, 129, 0.1); color: #10B981; }
  &.status-false { background: rgba(100, 116, 139, 0.1); color: #64748B; }
}

@media (max-width: 1100px) {
  .stats-grid { grid-template-columns: repeat(2, 1fr); }
  .row-charts { grid-template-columns: 1fr; }
}

@media (max-width: 768px) {
  .stats-grid { grid-template-columns: 1fr 1fr; }
  .police-page { padding: 16px; }
}
</style>