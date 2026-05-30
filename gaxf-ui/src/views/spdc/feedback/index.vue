<template>
  <div class="police-page">
    <div class="content-grid">
      <!-- 左侧：待整改任务列表 -->
      <div class="task-panel">
        <div class="panel-header">
          <h3><LucideSend class="icon-small" /> 待处理任务</h3>
          <span class="task-count">3 项</span>
        </div>

        <div class="task-list">
          <div
            v-for="(task, i) in pendingTasks"
            :key="i"
            :class="['task-item', { active: selectedTask === i }]"
            @click="selectedTask = i"
          >
            <div class="task-header">
              <span class="task-id">#ZG-202400{{ i + 1 }}</span>
              <span class="task-status" :class="task.statusClass">{{ task.status }}</span>
            </div>
            <div class="task-title">{{ task.title }}</div>
            <div class="task-desc">{{ task.description }}</div>
            <div class="task-footer">
              <span class="task-assignee">
                <LucideUser class="icon-small" />
                {{ task.assignee }}
              </span>
              <span class="task-time">{{ task.time }}</span>
            </div>
          </div>
        </div>
      </div>

      <!-- 右侧：任务详情与反馈编辑器 -->
      <div class="detail-panel">
        <!-- 任务详情头部 -->
        <div class="detail-header">
          <div class="header-info">
            <h2 class="detail-title">任务详情：#ZG-202400{{ selectedTask + 1 }}</h2>
            <p class="detail-subtitle">
              <LucideMapPin class="icon-small" />
              关联点位：{{ pendingTasks[selectedTask]?.location || '盐城市局一楼大厅东侧枪机' }}
            </p>
          </div>
          <button class="more-btn">
            <LucideMoreVertical class="icon-small" />
          </button>
        </div>

        <!-- 督察意见 -->
        <div class="opinion-box">
          <div class="opinion-label">
            <LucideMessageSquare class="icon-small" />
            督察意见
          </div>
          <div class="opinion-content">
            "{{ pendingTasks[selectedTask]?.opinion || '经视频回溯分析，该员在10:20-10:45期间未出现在工位，疑似离岗。请分局督察部门核实是否履行请假手续或执行其他紧急任务。' }}"
          </div>
        </div>

        <!-- 反馈编辑器 -->
        <div class="feedback-section">
          <label class="feedback-label">
            <LucideFileText class="icon-small" />
            提交反馈说明
          </label>
          <textarea
            v-model="feedbackContent"
            class="feedback-input"
            placeholder="在此输入整改核实结果..."
          ></textarea>
        </div>

        <!-- 附件上传 -->
        <div class="attachment-section">
          <label class="attachment-label">
            <LucidePaperclip class="icon-small" />
            上传附件材料
          </label>
          <div class="attachment-grid">
            <div class="upload-box">
              <LucideUpload class="upload-icon" />
              <span class="upload-text">点击上传</span>
            </div>
            <div class="upload-box uploaded">
              <LucideFile class="upload-icon" />
              <span class="upload-text">请假条.pdf</span>
            </div>
          </div>
        </div>

        <!-- 提交按钮 -->
        <div class="submit-bar">
          <button class="submit-btn secondary">
            <LucideSave class="icon-small" />
            暂存草稿
          </button>
          <button class="submit-btn primary">
            <LucideSend class="icon-small" />
            正式提交反馈
          </button>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref } from 'vue'
import {
  LucideSend, LucideUser, LucideMapPin, LucideMoreVertical,
  LucideMessageSquare, LucideFileText, LucidePaperclip, LucideUpload,
  LucideFile, LucideSave
} from 'lucide-vue-next'

const selectedTask = ref(0)
const feedbackContent = ref('')

const pendingTasks = [
  {
    title: '点位 Cam-001 离岗异常整改',
    description: '督察中心下发：请相关责任人核实离岗原因并提交文字说明。',
    assignee: '督察员 A',
    time: '2024-04-20 10:00',
    status: '待反馈',
    statusClass: 'status-pending',
    location: '盐城市局一楼大厅东侧枪机',
    opinion: '经视频回溯分析，该员在10:20-10:45期间未出现在工位，疑似离岗。请分局督察部门核实是否履行请假手续或执行其他紧急任务。'
  },
  {
    title: '点位 Cam-002 设备故障整改',
    description: '设备长时间离线，请排查网络连接或设备状态。',
    assignee: '督察员 B',
    time: '2024-04-19 14:30',
    status: '待核实',
    statusClass: 'status-verify',
    location: '盐城市局二楼办案区',
    opinion: '该点位设备连续离线超过24小时，请技术部门排查故障原因并尽快恢复。'
  },
  {
    title: '点位 Cam-003 画面遮挡整改',
    description: '监控画面存在遮挡物，影响正常监控。',
    assignee: '督察员 C',
    time: '2024-04-18 09:15',
    status: '进行中',
    statusClass: 'status-progress',
    location: '盐城市局西侧入口',
    opinion: '监控画面被临时堆放物资遮挡，请相关部门清理遮挡物并确保监控视野完整。'
  }
]
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
  grid-template-columns: 340px 1fr;
  gap: 24px;
  height: calc(100vh - 140px);
}

.icon-small { width: 16px; height: 16px; }

.task-panel {
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
  display: flex;
  justify-content: space-between;
  align-items: center;

  h3 {
    font-size: 14px;
    font-weight: 600;
    color: $police-primary;
    display: flex;
    align-items: center;
    gap: 8px;
  }
}

.task-count {
  font-size: 12px;
  color: $police-text-muted;
  padding: 4px 12px;
  border-radius: 20px;
  background: #F5F5F5;
}

.task-list {
  flex: 1;
  overflow-y: auto;
  padding: 16px;
}

.task-item {
  padding: 16px;
  border-radius: 16px;
  border: 1px solid $police-border;
  margin-bottom: 12px;
  cursor: pointer;
  transition: all 0.2s;
  border-left: 4px solid transparent;

  &:hover {
    background: #F5F5F5;
    border-left-color: $police-light-blue;
  }

  &.active {
    background: #E6F4FF;
    border-color: $police-light-blue;
    border-left-color: $police-blue;
  }
}

.task-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 8px;
}

.task-id {
  font-size: 11px;
  color: $police-text-muted;
  padding: 2px 8px;
  border-radius: 4px;
  background: #F5F5F5;
}

.task-status {
  font-size: 11px;
  font-weight: 600;
  padding: 4px 10px;
  border-radius: 20px;

  &.status-pending { background: rgba(245, 158, 11, 0.1); color: #F59E0B; }
  &.status-verify { background: rgba(59, 130, 246, 0.1); color: #3B82F6; }
  &.status-progress { background: rgba(16, 185, 129, 0.1); color: #10B981; }
}

.task-title {
  font-size: 14px;
  font-weight: 600;
  color: $police-text;
  margin-bottom: 6px;
}

.task-desc {
  font-size: 12px;
  color: $police-text-muted;
  margin-bottom: 12px;
}

.task-footer {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.task-assignee {
  font-size: 11px;
  color: $police-text-muted;
  display: flex;
  align-items: center;
  gap: 4px;
}

.task-time {
  font-size: 11px;
  color: $police-text-muted;
}

.detail-panel {
  background: white;
  border-radius: 20px;
  border: 1px solid $police-border;
  display: flex;
  flex-direction: column;
  overflow: hidden;
}

.detail-header {
  padding: 20px 24px;
  border-bottom: 1px solid $police-border;
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
}

.header-info {
  flex: 1;
}

.detail-title {
  font-size: 18px;
  font-weight: 600;
  color: $police-text;
}

.detail-subtitle {
  font-size: 13px;
  color: $police-text-muted;
  margin-top: 8px;
  display: flex;
  align-items: center;
  gap: 4px;
}

.more-btn {
  padding: 8px;
  border-radius: 12px;
  border: 1px solid $police-border;
  background: white;
  cursor: pointer;
  transition: background 0.2s;

  &:hover { background: #F5F5F5; }
}

.opinion-box {
  padding: 20px 24px;
  border-bottom: 1px solid $police-border;
}

.opinion-label {
  font-size: 13px;
  font-weight: 500;
  color: $police-text-muted;
  margin-bottom: 12px;
  display: flex;
  align-items: center;
  gap: 6px;
}

.opinion-content {
  padding: 16px 20px;
  background: #F5F5F5;
  border-radius: 12px;
  font-size: 14px;
  color: $police-text;
  line-height: 1.6;
  font-style: italic;
}

.feedback-section {
  padding: 20px 24px;
}

.feedback-label {
  font-size: 13px;
  font-weight: 500;
  color: $police-text-muted;
  margin-bottom: 12px;
  display: flex;
  align-items: center;
  gap: 6px;
}

.feedback-input {
  width: 100%;
  min-height: 160px;
  padding: 16px 20px;
  border: 1px solid $police-border;
  border-radius: 16px;
  font-size: 14px;
  color: $police-text;
  background: white;
  resize: vertical;
  transition: border-color 0.2s;

  &:focus {
    outline: none;
    border-color: $police-blue;
  }

  &::placeholder { color: $police-text-muted; }
}

.attachment-section {
  padding: 20px 24px;
}

.attachment-label {
  font-size: 13px;
  font-weight: 500;
  color: $police-text-muted;
  margin-bottom: 12px;
  display: flex;
  align-items: center;
  gap: 6px;
}

.attachment-grid {
  display: flex;
  gap: 16px;
}

.upload-box {
  width: 120px;
  height: 120px;
  border: 2px dashed $police-border;
  border-radius: 16px;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  gap: 8px;
  cursor: pointer;
  transition: all 0.2s;

  &:hover {
    border-color: $police-blue;
    background: #E6F4FF;
  }

  &.uploaded {
    border-style: solid;
    border-color: #10B981;
    background: rgba(16, 185, 129, 0.05);
  }
}

.upload-icon {
  width: 32px;
  height: 32px;
  color: $police-text-muted;
}

.upload-text {
  font-size: 12px;
  color: $police-text-muted;
}

.submit-bar {
  padding: 20px 24px;
  border-top: 1px solid $police-border;
  display: flex;
  justify-content: flex-end;
  gap: 16px;
  background: #F5F5F5;
}

.submit-btn {
  padding: 12px 24px;
  border-radius: 20px;
  font-size: 14px;
  font-weight: 500;
  cursor: pointer;
  transition: all 0.2s;
  display: flex;
  align-items: center;
  gap: 6px;

  &.secondary {
    border: 1px solid $police-border;
    background: white;
    color: $police-text;

    &:hover { background: #F5F5F5; }
  }

  &.primary {
    border: none;
    background: $police-blue;
    color: white;
    box-shadow: 0 4px 12px rgba(37, 99, 235, 0.3);

    &:hover {
      background: $police-light-blue;
      transform: translateY(-1px);
    }
  }
}

@media (max-width: 1100px) {
  .content-grid { grid-template-columns: 1fr; height: auto; }
  .task-panel { max-height: 400px; }
}
</style>