<template>
  <div class="app-container">
    <!-- ===== 侧边栏 ===== -->
    <div class="sidebar">
      <div class="logo-area">
        <div class="logo-icon">
          <LucideShield class="icon" />
        </div>
        <div class="logo-text">
          <h2>智慧·公安</h2>
          <p>智能视频监控系统</p>
        </div>
      </div>

      <div class="nav-menu">
        <!-- 驾驶舱 - 叶子项 -->
        <div class="nav-item leaf" :class="{ active: activePage === 'dashboard' }" @click="setPage('dashboard', '驾驶舱')">
          <div class="menu-row">
            <div class="menu-row-left">
              <LucideLayoutDashboard class="icon" />
              <span>驾驶舱</span>
            </div>
          </div>
        </div>

        <!-- 实时监控 - 叶子项 -->
        <div class="nav-item leaf" :class="{ active: activePage === 'live' }" @click="setPage('live', '实时监控')">
          <div class="menu-row">
            <div class="menu-row-left">
              <LucideVideo class="icon" />
              <span>实时监控</span>
            </div>
          </div>
        </div>

        <!-- 智能分析 - 可展开 -->
        <div class="nav-item parent">
          <div class="menu-row" @click="toggleSubmenu('analytics')">
            <div class="menu-row-left">
              <LucideBrain class="icon" />
              <span>智能分析</span>
            </div>
            <LucideChevronRight class="arrow-icon" :class="{ rotated: expandedMenus.analytics }" />
          </div>
          <div class="submenu" :class="{ show: expandedMenus.analytics }">
            <div class="sub-item" :class="{ active: activeSubPage === '实时分析' }" @click="setSubPage('智能分析', '实时分析')">实时分析</div>
            <div class="sub-item" :class="{ active: activeSubPage === '结构化检索' }" @click="setSubPage('智能分析', '结构化检索')">结构化检索</div>
            <div class="sub-item" :class="{ active: activeSubPage === '告警研判' }" @click="setSubPage('智能分析', '告警研判')">告警研判</div>
          </div>
        </div>

        <!-- 告警中心 - 叶子项 -->
        <div class="nav-item leaf" :class="{ active: activePage === 'alarmCenter' }" @click="setPage('alarmCenter', '告警中心')">
          <div class="menu-row">
            <div class="menu-row-left">
              <LucideBell class="icon" />
              <span>告警中心</span>
            </div>
          </div>
        </div>

        <!-- 设备管理 - 可展开 -->
        <div class="nav-item parent">
          <div class="menu-row" @click="toggleSubmenu('device')">
            <div class="menu-row-left">
              <LucideCpu class="icon" />
              <span>设备管理</span>
            </div>
            <LucideChevronRight class="arrow-icon" :class="{ rotated: expandedMenus.device }" />
          </div>
          <div class="submenu" :class="{ show: expandedMenus.device }">
            <div class="sub-item" :class="{ active: activeSubPage === '摄像头管理' }" @click="setSubPage('设备管理', '摄像头管理')">摄像头管理</div>
            <div class="sub-item" :class="{ active: activeSubPage === '边缘计算节点' }" @click="setSubPage('设备管理', '边缘计算节点')">边缘计算节点</div>
            <div class="sub-item" :class="{ active: activeSubPage === '算法仓库' }" @click="setSubPage('设备管理', '算法仓库')">算法仓库</div>
          </div>
        </div>

        <!-- 数据研判 - 叶子项 -->
        <div class="nav-item leaf" :class="{ active: activePage === 'dataAnalysis' }" @click="setPage('dataAnalysis', '数据研判')">
          <div class="menu-row">
            <div class="menu-row-left">
              <LucideTrendingUp class="icon" />
              <span>数据研判</span>
            </div>
          </div>
        </div>

        <!-- 系统设置 - 叶子项 -->
        <div class="nav-item leaf" :class="{ active: activePage === 'settings' }" @click="setPage('settings', '系统设置')">
          <div class="menu-row">
            <div class="menu-row-left">
              <LucideSettings class="icon" />
              <span>系统设置</span>
            </div>
          </div>
        </div>
      </div>

      <div class="bottom-info">
        <LucideFingerprint class="icon-small" /> 安全等级 · 三级等保<br>
        v2.6.0 | 智慧警务
      </div>
    </div>

    <!-- ===== 右侧主内容 ===== -->
    <div class="main-content">
      <!-- 顶部导航栏 -->
      <div class="top-bar">
        <div class="page-title">
          <h3 id="dynamicTitle">
            {{ pageTitle }}
            <span><LucideMapPin class="icon-small" /> 指挥调度中心 · 南京市局</span>
          </h3>
        </div>
        <div class="user-area">
          <div class="notify-badge">
            <LucideBell class="icon" />
            <span class="badge-num">3</span>
          </div>
          <div class="admin-info">
            <div class="avatar">警</div>
            <span class="admin-name">张毅警官</span>
          </div>
        </div>
      </div>

      <!-- 主内容区域 -->
      <div class="content-wrapper">
        <!-- 统计卡片 -->
        <div class="stats-grid">
          <div class="stat-card">
            <div class="stat-info">
              <h4><LucideVideo class="icon-small" /> 监控点总数</h4>
              <div class="stat-number">1,284</div>
              <small>覆盖率 +12%</small>
            </div>
            <div class="stat-icon">
              <LucideCamera class="icon" />
            </div>
          </div>
          <div class="stat-card">
            <div class="stat-info">
              <h4><LucideCheckCircle class="icon-small" /> 在线率</h4>
              <div class="stat-number">98.6%</div>
              <small>在线设备 1266</small>
            </div>
            <div class="stat-icon">
              <LucideWifi class="icon" />
            </div>
          </div>
          <div class="stat-card">
            <div class="stat-info">
              <h4><LucideAlertTriangle class="icon-small" /> 今日告警</h4>
              <div class="stat-number">23</div>
              <small>高危 3 起</small>
            </div>
            <div class="stat-icon alarm-icon">
              <LucideBell class="icon" />
            </div>
          </div>
          <div class="stat-card">
            <div class="stat-info">
              <h4><LucideBarChart3 class="icon-small" /> 处置率</h4>
              <div class="stat-number">91.3%</div>
              <small>较昨日 +5%</small>
            </div>
            <div class="stat-icon">
              <LucideHandshake class="icon" />
            </div>
          </div>
        </div>

        <!-- 图表区域 -->
        <div class="row-charts">
          <!-- 告警趋势图 -->
          <div class="card">
            <div class="card-header">
              <span><LucideTrendingUp class="icon-small" /> 近一周告警趋势</span>
              <LucideMoreHorizontal class="icon-muted" />
            </div>
            <div ref="trendChartRef" class="chart-container"></div>
          </div>

          <!-- 设备在线率 + 告警列表 -->
          <div class="card">
            <div class="card-header">
              <span><LucidePieChart class="icon-small" /> 设备在线率 & 实时告警</span>
              <LucideRefreshCw class="icon-btn" />
            </div>
            <div class="chart-alert-row">
              <!-- 饼图 -->
              <div class="pie-section">
                <div ref="pieChartRef" class="pie-chart"></div>
                <div class="pie-label">
                  <span class="online-badge">在线率 98.6%</span>
                </div>
              </div>
              <!-- 告警列表 -->
              <div class="alert-section">
                <div class="alert-title">
                  <LucideBolt class="icon-small" /> 最新告警事件
                </div>
                <ul class="alarm-list">
                  <li class="alarm-item">
                    <div class="alarm-level high"></div>
                    <div class="alarm-content">
                      <div class="alarm-title-text">重点人员识别 · 南门出入口</div>
                      <div class="alarm-time">14:23:05 · 人脸比中92%</div>
                    </div>
                    <div class="alarm-tag high-tag">高危</div>
                  </li>
                  <li class="alarm-item">
                    <div class="alarm-level high"></div>
                    <div class="alarm-content">
                      <div class="alarm-title-text">车辆布控 · 黑色SUV 套牌嫌疑</div>
                      <div class="alarm-time">14:10:22 · 车牌苏A·K3E29</div>
                    </div>
                    <div class="alarm-tag urgent-tag">紧急</div>
                  </li>
                  <li class="alarm-item">
                    <div class="alarm-level mid"></div>
                    <div class="alarm-content">
                      <div class="alarm-title-text">区域入侵检测 · 东侧围墙</div>
                      <div class="alarm-time">13:57:19 · 越界告警</div>
                    </div>
                    <div class="alarm-tag mid-tag">中危</div>
                  </li>
                  <li class="alarm-item">
                    <div class="alarm-level mid"></div>
                    <div class="alarm-content">
                      <div class="alarm-title-text">异常聚集预警 · 市民广场</div>
                      <div class="alarm-time">13:20:44 · 人群密度超标</div>
                    </div>
                    <div class="alarm-tag watch-tag">关注</div>
                  </li>
                </ul>
                <div class="alert-footer">
                  <span class="btn-outline">
                    查看全部告警 <LucideArrowRight class="icon-small" />
                  </span>
                </div>
              </div>
            </div>
          </div>
        </div>

        <!-- 视频监控网格 -->
        <div class="card">
          <div class="section-header">
            <div class="card-header no-margin">
              <span><LucideCamera class="icon-small" /> 实时智能视频监控 | 关键点位</span>
            </div>
            <span class="btn-outline">
              <LucideExpand class="icon-small" /> 全屏指挥
            </span>
          </div>
          <div class="video-grid">
            <div class="video-card" v-for="(video, i) in videoList" :key="i">
              <div class="video-preview">
                <LucideVideo class="video-icon" />
                <div class="scan-line"></div>
              </div>
              <div class="video-info">
                <span class="camera-name">
                  <LucideMapPin class="icon-small" /> {{ video.name }} · {{ video.quality }}
                </span>
                <span class="ai-badge">
                  <LucideBrain class="icon-small" /> {{ video.aiType }}
                </span>
              </div>
            </div>
          </div>
          <div class="video-footer">
            <LucideCpu class="icon-small" /> 边缘计算节点在线 | 实时结构化分析中
          </div>
        </div>

        <!-- 底部区域 -->
        <div class="bottom-row">
          <!-- 智能研判摘要 -->
          <div class="summary-card">
            <div class="card-header no-margin">
              <LucideBarChart3 class="icon-small" /> 今日智能研判摘要
            </div>
            <div class="summary-items">
              <div class="summary-item">
                <span class="recording-dot"></span> 人脸抓拍: <strong>1,432</strong> 次
              </div>
              <div class="summary-item">
                <LucideCar class="icon-blue" /> 车辆过车: <strong>2,850</strong> 辆
              </div>
              <div class="summary-item">
                <LucideUsers class="icon-blue" /> 重点人员预警: <strong>7</strong> 次
              </div>
              <div class="summary-item">
                <LucideMapPinned class="icon-blue" /> 布控命中: <strong>2</strong> 起
              </div>
            </div>
          </div>

          <!-- 系统运行状态 -->
          <div class="status-card">
            <div class="status-title">
              <LucideClock class="icon-small" /> 系统运行状态
            </div>
            <div class="status-content">
              <div class="status-item"><strong>视频流健康度</strong> 99.2%</div>
              <div class="status-item">AI 分析节点负载: 34%</div>
              <div class="status-item">
                <LucideShieldCheck class="icon-small" /> 公安网边界安全
              </div>
            </div>
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
  LucideShield, LucideLayoutDashboard, LucideVideo, LucideBrain,
  LucideChevronRight, LucideBell, LucideCpu, LucideTrendingUp,
  LucideSettings, LucideFingerprint, LucideMapPin, LucideCheckCircle,
  LucideAlertTriangle, LucideBarChart3, LucideWifi, LucideCamera,
  LucideHandshake, LucidePieChart, LucideMoreHorizontal, LucideRefreshCw,
  LucideBolt, LucideArrowRight, LucideExpand, LucideCar, LucideUsers,
  LucideMapPinned, LucideClock, LucideShieldCheck
} from 'lucide-vue-next'

// 导航状态
const activePage = ref('dashboard')
const activeSubPage = ref('')
const pageTitle = ref('智能视频监控驾驶舱')
const expandedMenus = ref({
  analytics: false,
  device: false
})

// ECharts 引用
const trendChartRef = ref<HTMLElement>()
const pieChartRef = ref<HTMLElement>()
let trendChart: echarts.ECharts | null = null
let pieChart: echarts.ECharts | null = null

// 视频列表数据
const videoList = ref([
  { name: '南门出入口', quality: '4K', aiType: '人脸/车辆' },
  { name: '东侧道路卡口', quality: 'HD', aiType: '车牌识别' },
  { name: '北大厅入口', quality: 'HD', aiType: '行为分析' },
  { name: '地下车库B区', quality: 'SD', aiType: '移动侦测' }
])

// 导航方法
function setPage(key: string, title: string) {
  activePage.value = key
  activeSubPage.value = ''
  pageTitle.value = title === '驾驶舱' ? '智能视频监控驾驶舱' : title
}

function setSubPage(parentName: string, subName: string) {
  activePage.value = ''
  activeSubPage.value = subName
  pageTitle.value = `${parentName} / ${subName}`
}

function toggleSubmenu(key: string) {
  expandedMenus.value[key] = !expandedMenus.value[key]
}

// 初始化 ECharts
function initCharts() {
  // 告警趋势折线图
  if (trendChartRef.value) {
    trendChart = echarts.init(trendChartRef.value)
    trendChart.setOption({
      tooltip: { trigger: 'axis' },
      grid: { top: 30, left: 45, right: 20, bottom: 20 },
      xAxis: {
        type: 'category',
        data: ['05.20', '05.21', '05.22', '05.23', '05.24', '05.25', '今日'],
        axisLabel: { fontSize: 11, color: '#5F7A9A' }
      },
      yAxis: {
        type: 'value',
        name: '告警数量',
        nameTextStyle: { color: '#3A5E88' },
        splitLine: { lineStyle: { color: '#EAEFF4' } }
      },
      series: [{
        data: [9, 12, 8, 15, 22, 18, 23],
        type: 'line',
        smooth: true,
        symbol: 'circle',
        symbolSize: 8,
        lineStyle: { width: 3, color: '#3B82F6' },
        areaStyle: {
          opacity: 0.1,
          color: new echarts.graphic.LinearGradient(0, 0, 0, 1, [
            { offset: 0, color: '#3B82F6' },
            { offset: 1, color: '#DBEAFE' }
          ])
        },
        itemStyle: { color: '#2563EB', borderColor: '#fff' }
      }]
    })
  }

  // 设备在线率饼图
  if (pieChartRef.value) {
    pieChart = echarts.init(pieChartRef.value)
    pieChart.setOption({
      tooltip: { trigger: 'item' },
      legend: { show: false },
      series: [{
        name: '设备在线率',
        type: 'pie',
        radius: ['60%', '80%'],
        data: [
          { value: 1266, name: '在线设备', itemStyle: { color: '#3B82F6' } },
          { value: 18, name: '离线/故障', itemStyle: { color: '#CBD5E1' } }
        ],
        labelLine: { show: false }
      }],
      graphic: [{
        type: 'text',
        left: 'center',
        top: 'center',
        style: { text: '98.6%', fill: '#1E3A8A', fontSize: 15, fontWeight: 'bold' },
        z: 100
      }]
    })
  }
}

// 窗口大小调整
function handleResize() {
  trendChart?.resize()
  pieChart?.resize()
}

onMounted(() => {
  initCharts()
  window.addEventListener('resize', handleResize)
})

onUnmounted(() => {
  trendChart?.dispose()
  pieChart?.dispose()
  window.removeEventListener('resize', handleResize)
})
</script>

<style lang="scss" scoped>
// ===== 全局变量 =====
$police-primary: #1E3A8A;
$police-dark: #172554;
$police-blue: #2563EB;
$police-light-blue: #3B82F6;
$police-bg: #F8FAFE;
$police-card: #FFFFFF;
$police-border: #E2E8F0;
$police-text: #1E293B;
$police-text-muted: #5B6E8C;
$police-danger: #EF4444;
$police-warning: #F59E0B;

// ===== 主容器 =====
.app-container {
  display: flex;
  height: 100vh;
  width: 100%;
  overflow: hidden;
  background: $police-bg;
}

// ===== 侧边栏 =====
.sidebar {
  width: 260px;
  background: linear-gradient(180deg, $police-primary 0%, $police-dark 100%);
  color: #F0F4FA;
  display: flex;
  flex-direction: column;
  box-shadow: 4px 0 20px rgba(0, 0, 0, 0.08);
  transition: all 0.2s;
  z-index: 10;
  overflow-y: auto;

  &::-webkit-scrollbar {
    width: 6px;
  }
  &::-webkit-scrollbar-track {
    background: rgba(255, 255, 255, 0.05);
  }
  &::-webkit-scrollbar-thumb {
    background: rgba(255, 255, 255, 0.2);
    border-radius: 4px;
  }
}

.logo-area {
  padding: 24px 20px;
  border-bottom: 1px solid rgba(255, 255, 255, 0.12);
  margin-bottom: 20px;
  display: flex;
  align-items: center;
  gap: 12px;

  .logo-icon {
    background: $police-light-blue;
    width: 38px;
    height: 38px;
    border-radius: 12px;
    display: flex;
    align-items: center;
    justify-content: center;
    color: white;
    box-shadow: 0 6px 12px -6px rgba(0, 0, 0, 0.3);

    .icon {
      width: 22px;
      height: 22px;
    }
  }

  .logo-text h2 {
    font-size: 18px;
    font-weight: 600;
    letter-spacing: 1px;
    color: white;
  }

  .logo-text p {
    font-size: 11px;
    opacity: 0.8;
    margin-top: 4px;
  }
}

.nav-menu {
  flex: 1;
  padding: 0 12px 20px 12px;
}

.nav-item {
  margin-bottom: 6px;
  border-radius: 12px;
  transition: all 0.2s;

  &.leaf .menu-row {
    justify-content: flex-start;
  }

  &.leaf .menu-row .menu-row-left {
    flex: 1;
  }

  &.leaf.active .menu-row {
    background: $police-blue;
    color: white;
    box-shadow: 0 4px 8px rgba(37, 99, 235, 0.3);
  }
}

.menu-row {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 12px 16px;
  border-radius: 12px;
  color: #CBD5E1;
  font-weight: 500;
  cursor: pointer;
  transition: all 0.2s;

  &:hover {
    background: #2E4A8A;
    color: #F1F5F9;
  }

  .icon {
    width: 22px;
    height: 22px;
  }
}

.menu-row-left {
  display: flex;
  align-items: center;
  gap: 14px;
}

.arrow-icon {
  width: 12px;
  height: 12px;
  transition: transform 0.2s;
  color: #9BB5E0;

  &.rotated {
    transform: rotate(90deg);
  }
}

.submenu {
  margin-left: 24px;
  padding-left: 16px;
  border-left: 1px solid rgba(59, 130, 246, 0.4);
  display: none;
  flex-direction: column;
  gap: 4px;
  margin-top: 8px;
  margin-bottom: 8px;

  &.show {
    display: flex;
  }
}

.sub-item {
  padding: 10px 12px 10px 28px;
  border-radius: 10px;
  font-size: 13px;
  font-weight: 500;
  color: #CBD5E1;
  cursor: pointer;
  transition: all 0.2s;

  &:hover {
    background: #2D4A7C;
    color: white;
  }

  &.active {
    background: $police-blue;
    color: white;
    box-shadow: 0 2px 6px rgba(37, 99, 235, 0.3);
  }
}

.bottom-info {
  padding: 20px 20px 30px;
  font-size: 12px;
  border-top: 1px solid rgba(255, 255, 255, 0.12);
  margin-top: 20px;
  color: #A5C0E0;
  line-height: 1.6;

  .icon-small {
    width: 14px;
    height: 14px;
    margin-right: 4px;
    vertical-align: middle;
  }
}

// ===== 主内容区 =====
.main-content {
  flex: 1;
  overflow-y: auto;
  background: $police-bg;

  &::-webkit-scrollbar {
    width: 6px;
  }
  &::-webkit-scrollbar-track {
    background: #E2E8F0;
    border-radius: 4px;
  }
  &::-webkit-scrollbar-thumb {
    background: #94A3B8;
    border-radius: 4px;
  }
}

.top-bar {
  background: white;
  padding: 16px 28px;
  display: flex;
  justify-content: space-between;
  align-items: center;
  box-shadow: 0 1px 4px rgba(0, 0, 0, 0.04);
  position: sticky;
  top: 0;
  z-index: 5;
}

.page-title h3 {
  font-size: 22px;
  font-weight: 600;
  color: $police-primary;
  letter-spacing: 0.5px;

  span {
    font-size: 13px;
    color: $police-text-muted;
    margin-left: 8px;
  }
}

.user-area {
  display: flex;
  align-items: center;
  gap: 24px;
}

.notify-badge {
  position: relative;
  cursor: pointer;

  .icon {
    width: 20px;
    height: 20px;
    color: #3B6CB0;
  }

  .badge-num {
    position: absolute;
    top: -6px;
    right: -10px;
    background: $police-danger;
    color: white;
    font-size: 10px;
    font-weight: bold;
    border-radius: 30px;
    padding: 2px 6px;
  }
}

.admin-info {
  display: flex;
  align-items: center;
  gap: 12px;

  .avatar {
    width: 42px;
    height: 42px;
    background: $police-blue;
    border-radius: 40px;
    display: flex;
    align-items: center;
    justify-content: center;
    color: white;
    font-weight: bold;
    font-size: 18px;
  }

  .admin-name {
    font-weight: 500;
    color: $police-text;
  }
}

.content-wrapper {
  padding: 24px 28px;
}

// ===== 统计卡片 =====
.stats-grid {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: 20px;
  margin-bottom: 28px;
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
}

.stat-number {
  font-size: 32px;
  font-weight: 700;
  color: $police-primary;

  small {
    font-size: 12px;
    color: $police-text-muted;
    margin-top: 4px;
    display: block;
  }
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

  .icon {
    width: 26px;
    height: 26px;
  }

  &.alarm-icon {
    background: rgba(239, 68, 68, 0.1);
    color: $police-danger;
  }
}

// ===== 卡片通用 =====
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

  &.no-margin {
    margin-bottom: 10px;
    border-left: none;
    padding-left: 0;
  }

  span {
    display: flex;
    align-items: center;
    gap: 6px;
  }
}

.icon-small {
  width: 16px;
  height: 16px;
}

.icon-muted {
  width: 16px;
  height: 16px;
  color: #94A3B8;
}

.icon-btn {
  width: 16px;
  height: 16px;
  color: $police-blue;
  cursor: pointer;
  transition: color 0.2s;

  &:hover {
    color: $police-light-blue;
  }
}

.icon-blue {
  width: 16px;
  height: 16px;
  color: $police-blue;
}

// ===== 图表区域 =====
.row-charts {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 24px;
  margin-bottom: 28px;
}

.chart-container {
  height: 280px;
  width: 100%;
}

.chart-alert-row {
  display: flex;
  flex-wrap: wrap;
  gap: 20px;
}

.pie-section {
  flex: 1;
  min-width: 140px;

  .pie-chart {
    height: 160px;
    width: 100%;
  }

  .pie-label {
    text-align: center;
    margin-top: -5px;
  }

  .online-badge {
    background: #EFF4FF;
    padding: 3px 12px;
    border-radius: 18px;
    font-size: 12px;
  }
}

.alert-section {
  flex: 2;

  .alert-title {
    font-size: 13px;
    font-weight: 600;
    margin-bottom: 8px;
    display: flex;
    align-items: center;
    gap: 6px;
  }
}

.alarm-list {
  list-style: none;
}

.alarm-item {
  display: flex;
  align-items: center;
  gap: 14px;
  padding: 14px 0;
  border-bottom: 1px solid #EDF2F7;
}

.alarm-level {
  width: 8px;
  height: 38px;
  border-radius: 6px;

  &.high { background: $police-danger; }
  &.mid { background: $police-warning; }
}

.alarm-content {
  flex: 1;

  .alarm-title-text {
    font-weight: 600;
    margin-bottom: 5px;
    font-size: 14px;
    color: $police-text;
  }

  .alarm-time {
    font-size: 12px;
    color: #6C86A3;
  }
}

.alarm-tag {
  background: #F1F5F9;
  padding: 4px 8px;
  border-radius: 20px;
  font-size: 11px;
  color: #2C4F8C;

  &.high-tag {
    background: rgba(239, 68, 68, 0.1);
    color: $police-danger;
  }
  &.urgent-tag {
    background: rgba(239, 68, 68, 0.1);
    color: $police-danger;
  }
  &.mid-tag {
    background: rgba(245, 158, 11, 0.1);
    color: $police-warning;
  }
  &.watch-tag {
    background: #F1F5F9;
    color: #2C4F8C;
  }
}

.alert-footer {
  margin-top: 8px;
  text-align: right;
}

.btn-outline {
  border: 1px solid #CBD5E1;
  background: white;
  border-radius: 30px;
  padding: 5px 12px;
  font-size: 12px;
  color: $police-blue;
  cursor: pointer;
  transition: all 0.2s;
  display: inline-flex;
  align-items: center;
  gap: 4px;

  &:hover {
    background: #EFF6FF;
    border-color: #93C5FD;
  }
}

// ===== 视频网格 =====
.section-header {
  display: flex;
  justify-content: space-between;
  margin-bottom: 18px;
  align-items: baseline;
}

.video-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(300px, 1fr));
  gap: 24px;
  margin-top: 8px;
}

.video-card {
  background: linear-gradient(145deg, #111C2E 0%, #0A1222 100%);
  border-radius: 20px;
  overflow: hidden;
  border: 1px solid #2D4470;
  transition: transform 0.2s;

  &:hover {
    transform: translateY(-3px);
  }
}

.video-preview {
  position: relative;
  background: #07152A;
  height: 200px;
  display: flex;
  align-items: center;
  justify-content: center;
  overflow: hidden;

  .video-icon {
    width: 58px;
    height: 58px;
    color: #5F9DF0;
    opacity: 0.8;
    z-index: 2;
  }
}

.scan-line {
  position: absolute;
  bottom: 0;
  left: 0;
  width: 100%;
  height: 3px;
  background: linear-gradient(90deg, transparent, $police-light-blue, #60A5FA, $police-light-blue, transparent);
  animation: scanMove 2.5s infinite linear;
}

@keyframes scanMove {
  0% { transform: translateX(-100%); }
  100% { transform: translateX(100%); }
}

.video-info {
  padding: 12px 16px;
  background: rgba(5, 15, 28, 0.9);
  display: flex;
  justify-content: space-between;
  align-items: center;
  border-top: 1px solid #2E4470;

  .camera-name {
    font-weight: 600;
    color: #EFF3F9;
    font-size: 14px;
    display: flex;
    align-items: center;
    gap: 4px;
  }

  .ai-badge {
    background: $police-blue;
    padding: 4px 8px;
    border-radius: 30px;
    font-size: 10px;
    font-weight: 500;
    color: white;
    display: flex;
    align-items: center;
    gap: 4px;
  }
}

.video-footer {
  margin-top: 18px;
  text-align: center;
  font-size: 12px;
  color: $police-text-muted;
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 4px;
}

// ===== 底部区域 =====
.bottom-row {
  margin-top: 24px;
  display: flex;
  gap: 20px;
  flex-wrap: wrap;
}

.summary-card {
  flex: 2;
  background: white;
  border-radius: 20px;
  padding: 16px 20px;
  border: 1px solid $police-border;
}

.summary-items {
  display: flex;
  gap: 20px;
  flex-wrap: wrap;

  .summary-item {
    font-size: 14px;
    color: $police-text;
    display: flex;
    align-items: center;
    gap: 4px;
  }
}

.recording-dot {
  display: inline-block;
  width: 8px;
  height: 8px;
  background: #F97316;
  border-radius: 50%;
  margin-right: 6px;
  animation: pulse 1.2s infinite;
}

@keyframes pulse {
  0% { opacity: 0.4; transform: scale(0.8); }
  100% { opacity: 1; transform: scale(1.2); }
}

.status-card {
  flex: 1;
  background: linear-gradient(105deg, #F0F9FF 0%, #FFFFFF 100%);
  border-radius: 20px;
  padding: 16px 20px;
  border-left: 4px solid $police-light-blue;

  .status-title {
    font-size: 14px;
    font-weight: 600;
    color: $police-text;
    margin-bottom: 12px;
    display: flex;
    align-items: center;
    gap: 6px;
  }

  .status-content {
    .status-item {
      font-size: 13px;
      color: $police-text-muted;
      margin-bottom: 8px;

      strong {
        color: $police-text;
      }
    }
  }
}

// ===== 响应式 =====
@media (max-width: 1100px) {
  .stats-grid { grid-template-columns: repeat(2, 1fr); }
  .row-charts { grid-template-columns: 1fr; }
}

@media (max-width: 768px) {
  .sidebar { width: 80px; }
  .logo-text, .bottom-info, .menu-row span, .sub-item { display: none; }
  .menu-row { justify-content: center; padding: 12px; }
  .menu-row-left { gap: 0; }
  .arrow-icon { display: none; }
  .submenu { border-left: none; margin-left: 0; padding-left: 0; }
  .content-wrapper { padding: 16px; }
  .top-bar { padding: 12px 16px; }
  .stats-grid { grid-template-columns: 1fr 1fr; gap: 12px; }
}
</style>