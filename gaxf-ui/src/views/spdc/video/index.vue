<template>
  <div class="police-page">
    <!-- 资源树 + 播放器区域 -->
    <div class="content-grid">
      <!-- 左侧：资源目录 -->
      <div class="resource-panel">
        <div class="panel-header">
          <h3><LucideFolder class="icon-small" /> 资源目录</h3>
        </div>
        <ResourceTree
          ref="resourceTreeRef"
          @select-point="handleSelectPoint"
        />
      </div>

      <!-- 右侧：播放器区域 -->
      <div class="player-panel">
        <!-- 控制工具条 -->
        <div class="control-bar">
          <div class="grid-controls">
            <button
              v-for="g in gridOptions"
              :key="g.value"
              :class="['grid-btn', { active: gridSize === g.value }]"
              @click="gridSize = g.value"
            >
              <component :is="g.icon" class="icon-small" />
              <span>{{ g.label }}</span>
            </button>
          </div>
          <div class="live-indicator">
            <span class="live-text">LIVE</span>
            <span class="live-dot"></span>
          </div>
        </div>

        <!-- 视频网格 -->
        <div class="video-grid" :class="`grid-${gridSize}`">
          <div
            v-for="i in gridSize"
            :key="i"
            class="video-cell"
            :class="{ playing: videoSlots[i-1].camera }"
            @click="handleSlotClick(i-1)"
          >
            <!-- 有点位播放 -->
            <template v-if="videoSlots[i-1].camera">
              <div class="video-label">
                <LucideVideo class="icon-small" />
                <span>{{ videoSlots[i-1].camera!.pointName }}</span>
                <span v-if="videoSlots[i-1].camera!.pointStatus === 1" class="online-tag">
                  <span class="status-dot online"></span> LIVE
                </span>
              </div>
              <!-- 视频播放区域 -->
              <div class="video-player">
                <div v-if="videoSlots[i-1].loading" class="loading-overlay">
                  <LucideLoader2 class="icon-spin large" />
                  <span>加载视频...</span>
                </div>
                <div v-else-if="videoSlots[i-1].error" class="error-overlay">
                  <LucideAlertCircle class="error-icon" />
                  <span>{{ videoSlots[i-1].error }}</span>
                  <button class="retry-btn" @click.stop="retryPlay(i-1)">
                    <LucideRefreshCw class="icon-small" /> 重试
                  </button>
                </div>
                <video
                  v-show="!videoSlots[i-1].loading && !videoSlots[i-1].error"
                  :ref="el => setVideoRef(el, i-1)"
                  autoplay
                  muted
                  playsinline
                  controls
                ></video>
                <div v-if="videoSlots[i-1].loading" class="scan-line"></div>
              </div>
              <div class="video-controls">
                <button class="ctrl-btn" @click.stop="toggleMute(i-1)">
                  <LucideVolume2 v-if="!videoSlots[i-1].muted" class="icon-small" />
                  <LucideVolumeX v-else class="icon-small" />
                </button>
                <button class="ctrl-btn" @click.stop="closeSlot(i-1)">
                  <LucideX class="icon-small" />
                </button>
              </div>
            </template>

            <!-- 无点位 -->
            <template v-else>
              <div class="video-placeholder">
                <div class="slot-index">{{ i }}</div>
                <LucidePlay class="play-icon" />
                <span class="hint-text">点击左侧点位播放</span>
              </div>
            </template>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, provide, onMounted, onUnmounted } from 'vue'
import Hls from 'hls.js'
import {
  LucideFolder, LucideVideo, LucidePlay,
  LucideMaximize, LucideGrid2X2, LucideGrid3X3,
  LucideVolume2, LucideVolumeX, LucideX,
  LucideLoader2, LucideAlertCircle,
  LucideRefreshCw
} from 'lucide-vue-next'
import { ElMessage } from 'element-plus'
import ResourceTree from './ResourceTree.vue'
import type { DcCamera } from '@/types'

const gridSize = ref(4)
const resourceTreeRef = ref()
const deptPointsMap = ref<Map<number, DcCamera[]>>(new Map())

// 提供点位数据给子组件
provide('deptPointsMap', deptPointsMap.value)

// 视频格子状态
interface VideoSlot {
  camera: DcCamera | null
  streamUrl: string | null
  loading: boolean
  error: string | null
  muted: boolean
  hls: Hls | null
}

const videoSlots = reactive<VideoSlot[]>(
  Array.from({ length: 9 }, () => ({
    camera: null,
    streamUrl: null,
    loading: false,
    error: null,
    muted: true,
    hls: null
  }))
)

const gridOptions = [
  { value: 1, icon: LucideMaximize, label: '单屏' },
  { value: 4, icon: LucideGrid2X2, label: '四分屏' },
  { value: 9, icon: LucideGrid3X3, label: '九分屏' },
]

// video 元素引用
const videoElements = ref<Map<number, HTMLVideoElement>>(new Map())

function setVideoRef(el: unknown, index: number) {
  if (el && el instanceof HTMLVideoElement) {
    videoElements.value.set(index, el)
    // 如果已有 streamUrl，尝试播放
    if (videoSlots[index].streamUrl && !videoSlots[index].hls) {
      attachHls(index, videoSlots[index].streamUrl!)
    }
  }
}

// 选择点位播放
function handleSelectPoint(camera: DcCamera) {
  // 检查离线
  if (camera.pointStatus !== 1) {
    ElMessage.warning('该点位离线，无法播放视频')
    return
  }

  // 找空闲格子或当前格子（单屏模式）
  let targetSlot = -1

  if (gridSize.value === 1) {
    // 单屏模式：替换第一个
    targetSlot = 0
  } else {
    // 多屏模式：找空闲格子
    for (let i = 0; i < gridSize.value; i++) {
      if (!videoSlots[i].camera) {
        targetSlot = i
        break
      }
    }
    // 没有空闲格子，替换最早的一个
    if (targetSlot === -1) {
      targetSlot = gridSize.value - 1
    }
  }

  // 检查是否已在播放
  const existingSlot = videoSlots.findIndex(s => s.camera?.pointId === camera.pointId)
  if (existingSlot !== -1 && existingSlot < gridSize.value) {
    ElMessage.info('该点位已在播放中')
    return
  }

  // 开始播放
  playVideo(targetSlot, camera)
}

// GB28181 视频流地址配置
const VIDEO_STREAM_BASE_URL = 'http://192.168.1.100:8080/live/'

// GB28181 视频流地址拼接
function buildStreamUrl(camera: DcCamera): string {
  if (!camera.gbCode) {
    throw new Error('点位缺少国标编码')
  }
  // 格式：固定前缀 + _ + gbCode + .m3u8
  return `${VIDEO_STREAM_BASE_URL}_${camera.gbCode}.m3u8`
}

// 使用 HLS 播放视频
function attachHls(slotIndex: number, streamUrl: string) {
  const videoEl = videoElements.value.get(slotIndex)
  if (!videoEl) return

  // 销毁旧的 HLS 实例
  if (videoSlots[slotIndex].hls) {
    videoSlots[slotIndex].hls!.destroy()
    videoSlots[slotIndex].hls = null
  }

  // 检查浏览器原生 HLS 支持（Safari）
  if (videoEl.canPlayType('application/vnd.apple.mpegurl')) {
    videoEl.src = streamUrl
    videoEl.play()
  } else if (Hls.isSupported()) {
    const hls = new Hls({
      enableWorker: true,
      lowLatencyMode: true,
    })
    hls.loadSource(streamUrl)
    hls.attachMedia(videoEl)
    hls.on(Hls.Events.MANIFEST_PARSED, () => {
      videoEl.play()
    })
    hls.on(Hls.Events.ERROR, (event, data) => {
      if (data.fatal) {
        switch (data.type) {
          case Hls.ErrorTypes.NETWORK_ERROR:
            videoSlots[slotIndex].error = '网络错误，无法加载视频'
            break
          case Hls.ErrorTypes.MEDIA_ERROR:
            videoSlots[slotIndex].error = '媒体错误，视频格式不支持'
            break
          default:
            videoSlots[slotIndex].error = '视频播放失败'
            break
        }
        videoSlots[slotIndex].loading = false
      }
    })
    videoSlots[slotIndex].hls = hls
  } else {
    videoSlots[slotIndex].error = '浏览器不支持 HLS 播放'
    videoSlots[slotIndex].loading = false
  }
}

// 播放视频
async function playVideo(slotIndex: number, camera: DcCamera) {
  const slot = videoSlots[slotIndex]

  // 先销毁旧的 HLS 实例
  if (slot.hls) {
    slot.hls.destroy()
    slot.hls = null
  }

  slot.camera = camera
  slot.loading = true
  slot.error = null
  slot.streamUrl = null

  try {
    // 检查国标编码
    if (!camera.gbCode) {
      throw new Error('点位缺少国标编码')
    }
    // 直接拼接视频流地址
    const streamUrl = buildStreamUrl(camera)
    slot.streamUrl = streamUrl
    slot.loading = false

    // 尝试使用 HLS 播放
    attachHls(slotIndex, streamUrl)
  } catch (err) {
    slot.loading = false
    slot.error = (err as Error).message || '视频加载失败'
  }
}

// 点击格子
function handleSlotClick(index: number) {
  // 如果格子有空位，不做处理（等待从左侧选择点位）
  if (!videoSlots[index].camera) return
  // 如果已有视频，可以做一些操作（如全屏等）
}

// 关闭格子
function closeSlot(index: number) {
  const slot = videoSlots[index]
  // 销毁 HLS 实例
  if (slot.hls) {
    slot.hls.destroy()
    slot.hls = null
  }
  slot.camera = null
  slot.streamUrl = null
  slot.loading = false
  slot.error = null
}

// 重试播放
function retryPlay(index: number) {
  const slot = videoSlots[index]
  if (slot.camera) {
    playVideo(index, slot.camera)
  }
}

// 切换静音
function toggleMute(index: number) {
  const videoEl = videoElements.value.get(index)
  if (videoEl) {
    videoEl.muted = !videoEl.muted
    videoSlots[index].muted = videoEl.muted
  }
}

// 监听 ResourceTree 的点位数据更新
onMounted(() => {
  // 当 ResourceTree 加载完点位后，同步到 deptPointsMap
  if (resourceTreeRef.value) {
    deptPointsMap.value = resourceTreeRef.value.deptPointsMap
  }
})

// 清理所有 HLS 实例
onUnmounted(() => {
  for (let i = 0; i < 9; i++) {
    if (videoSlots[i].hls) {
      videoSlots[i].hls!.destroy()
      videoSlots[i].hls = null
    }
  }
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

.content-grid {
  display: grid;
  grid-template-columns: 280px 1fr;
  gap: 24px;
  height: calc(100vh - 140px);
}

.resource-panel {
  background: white;
  border-radius: 20px;
  border: 1px solid $police-border;
  display: flex;
  flex-direction: column;
  overflow: hidden;
}

.panel-header {
  padding: 16px 20px;
  border-bottom: 1px solid $police-border;

  h3 {
    font-size: 14px;
    font-weight: 600;
    color: $police-primary;
    display: flex;
    align-items: center;
    gap: 8px;
  }
}

.icon-small { width: 16px; height: 16px; }
.icon-spin { animation: spin 1s linear infinite; }
.large { width: 32px; height: 32px; }

@keyframes spin {
  from { transform: rotate(0deg); }
  to { transform: rotate(360deg); }
}

.player-panel {
  background: white;
  border-radius: 20px;
  border: 1px solid $police-border;
  display: flex;
  flex-direction: column;
  overflow: hidden;
}

.control-bar {
  padding: 12px 20px;
  border-bottom: 1px solid $police-border;
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.grid-controls {
  display: flex;
  gap: 12px;
}

.grid-btn {
  padding: 8px 16px;
  border-radius: 20px;
  font-size: 12px;
  font-weight: 500;
  border: 1px solid $police-border;
  background: white;
  color: $police-text-muted;
  cursor: pointer;
  transition: all 0.2s;
  display: flex;
  align-items: center;
  gap: 6px;

  &:hover {
    border-color: $police-text-muted;
  }

  &.active {
    background: $police-blue;
    color: white;
    border-color: $police-blue;
  }
}

.live-indicator {
  display: flex;
  align-items: center;
  gap: 8px;

  .live-text {
    font-size: 11px;
    font-weight: 700;
    letter-spacing: 1px;
    color: #EF4444;
  }

  .live-dot {
    width: 8px;
    height: 8px;
    border-radius: 50%;
    background: #EF4444;
    animation: blink 1s infinite;
  }
}

@keyframes blink {
  0%, 100% { opacity: 1; }
  50% { opacity: 0.3; }
}

.video-grid {
  flex: 1;
  padding: 12px;
  gap: 12px;
  display: grid;
  overflow-y: auto;

  &.grid-1 { grid-template-columns: 1fr; }
  &.grid-4 { grid-template-columns: repeat(2, 1fr); }
  &.grid-9 { grid-template-columns: repeat(3, 1fr); }
}

.video-cell {
  background: linear-gradient(145deg, #111C2E 0%, #0A1222 100%);
  border-radius: 16px;
  border: 1px solid #2D4470;
  overflow: hidden;
  position: relative;
  min-height: 180px;
  cursor: pointer;
  transition: all 0.2s;

  &:hover:not(.playing) {
    border-color: $police-light-blue;
  }
}

.video-label {
  position: absolute;
  top: 12px;
  left: 12px;
  padding: 6px 12px;
  background: rgba(0, 0, 0, 0.7);
  backdrop-filter: blur(4px);
  border-radius: 20px;
  font-size: 11px;
  color: white;
  display: flex;
  align-items: center;
  gap: 6px;
  z-index: 10;
}

.online-tag {
  font-size: 10px;
  color: #10B981;
  display: flex;
  align-items: center;
  gap: 4px;
}

.status-dot {
  width: 6px;
  height: 6px;
  border-radius: 50%;
  &.online { background: #10B981; }
}

.video-player {
  position: absolute;
  inset: 0;
  display: flex;
  align-items: center;
  justify-content: center;
  background: #07152A;

  video {
    width: 100%;
    height: 100%;
    object-fit: contain;
  }
}

.loading-overlay, .error-overlay {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  gap: 12px;
  color: white;
  font-size: 13px;
}

.error-icon {
  width: 32px;
  height: 32px;
  color: #EF4444;
}

.retry-btn {
  padding: 8px 16px;
  border-radius: 20px;
  background: rgba(255, 255, 255, 0.2);
  border: none;
  color: white;
  font-size: 12px;
  cursor: pointer;
  display: flex;
  align-items: center;
  gap: 6px;
  transition: background 0.2s;

  &:hover { background: rgba(255, 255, 255, 0.3); }
}

.video-placeholder {
  position: absolute;
  inset: 0;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  background: #07152A;

  .slot-index {
    font-size: 48px;
    font-weight: 700;
    color: rgba(255, 255, 255, 0.1);
  }

  .play-icon {
    width: 32px;
    height: 32px;
    color: rgba(255, 255, 255, 0.3);
    margin-top: 8px;
  }

  .hint-text {
    font-size: 12px;
    color: rgba(255, 255, 255, 0.4);
    margin-top: 8px;
  }
}

.scan-line {
  position: absolute;
  bottom: 0;
  left: 0;
  width: 100%;
  height: 2px;
  background: linear-gradient(90deg, transparent, $police-light-blue, #60A5FA, $police-light-blue, transparent);
  animation: scanMove 2.5s infinite linear;
}

@keyframes scanMove {
  0% { transform: translateX(-100%); }
  100% { transform: translateX(100%); }
}

.video-controls {
  position: absolute;
  bottom: 12px;
  right: 12px;
  display: flex;
  gap: 8px;
  opacity: 0;
  transition: opacity 0.2s;
  z-index: 10;
}

.video-cell:hover .video-controls,
.video-cell.playing .video-controls {
  opacity: 1;
}

.ctrl-btn {
  padding: 6px;
  border-radius: 8px;
  background: rgba(255, 255, 255, 0.2);
  border: none;
  color: white;
  cursor: pointer;
  transition: background 0.2s;

  &:hover { background: rgba(255, 255, 255, 0.4); }
}

@media (max-width: 1100px) {
  .content-grid { grid-template-columns: 1fr; height: auto; }
  .resource-panel { max-height: 300px; }
}
</style>