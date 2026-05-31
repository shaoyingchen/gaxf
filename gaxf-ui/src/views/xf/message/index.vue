<template>
  <div class="message-wrapper">
    <div class="main-card">
      <!-- 头部区域 -->
      <div class="card-header">
        <div class="header-left">
          <div class="title-section">
            <LucideBell class="title-icon" />
            <h2 class="title-text">站内消息</h2>
            <span v-if="unreadTotal > 0" class="unread-badge">{{ unreadTotal }}</span>
          </div>
        </div>
        <div class="header-actions">
          <button class="action-btn" @click="handleReadAll">
            <LucideCheckCheck :size="16" />
            <span>全部已读</span>
          </button>
        </div>
      </div>

      <!-- 筛选 -->
      <div class="filter-section">
        <div class="filter-pills">
          <button
            v-for="(f, i) in filters"
            :key="i"
            :class="['pill-btn', { active: currentFilter === i }]"
            @click="currentFilter = i"
          >
            {{ f }}
          </button>
        </div>
      </div>

      <!-- 消息列表 -->
      <div class="message-list" v-loading="loading">
        <div
          v-for="msg in messageList"
          :key="msg.id"
          class="message-item"
          :class="{ unread: msg.isRead === '0' }"
          @click="handleRead(msg)"
        >
          <div class="msg-icon" :class="getMsgTypeClass(msg.msgType)">
            <LucideBell v-if="msg.msgType === '1'" :size="18" />
            <LucideShieldCheck v-else-if="msg.msgType === '2'" :size="18" />
            <LucideAlertTriangle v-else-if="msg.msgType === '3'" :size="18" />
            <LucideInfo v-else :size="18" />
          </div>
          <div class="msg-content">
            <div class="msg-header">
              <span class="msg-title">{{ msg.title }}</span>
              <span class="msg-type-tag" :class="getMsgTypeClass(msg.msgType)">{{ getMsgTypeText(msg.msgType) }}</span>
            </div>
            <div class="msg-body">{{ msg.content }}</div>
            <div class="msg-footer">
              <span class="msg-time">{{ formatTime(msg.createTime) }}</span>
              <span v-if="msg.isRead === '0'" class="unread-dot"></span>
            </div>
          </div>
        </div>

        <!-- 空状态 -->
        <div v-if="!loading && messageList.length === 0" class="empty-state">
          <LucideInbox :size="48" class="empty-icon" />
          <span class="empty-text">暂无消息</span>
        </div>
      </div>

      <!-- 分页 -->
      <div class="pagination-section" v-if="total > 0">
        <div class="page-stats">共 <strong>{{ total }}</strong> 条消息</div>
        <el-pagination
          v-model:current-page="pageNum"
          v-model:page-size="pageSize"
          :page-sizes="[10, 20, 50]"
          :total="total"
          layout="prev, pager, next, sizes"
          @size-change="loadMessageList"
          @current-change="loadMessageList"
        />
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted, watch } from 'vue'
import { ElMessage } from 'element-plus'
import {
  LucideBell, LucideCheckCheck, LucideShieldCheck,
  LucideAlertTriangle, LucideInfo, LucideInbox
} from 'lucide-vue-next'
import { listMessage, unreadCount, readMessage, readAllMessage } from '@/api/xf/message'
import type { XfMessage } from '@/types/api/xf/message'

const loading = ref(false)
const currentFilter = ref(0)
const filters = ['全部', '未读', '工单通知', '审核通知', '超期提醒', '系统通知']

const messageList = ref<XfMessage[]>([])
const total = ref(0)
const pageNum = ref(1)
const pageSize = ref(10)
const unreadTotal = ref(0)

watch(currentFilter, (val) => {
  pageNum.value = 1
  loadMessageList()
})

async function loadMessageList() {
  loading.value = true
  try {
    const params: any = { pageNum: pageNum.value, pageSize: pageSize.value }
    if (currentFilter.value === 1) params.isRead = '0'
    else if (currentFilter.value === 2) params.msgType = '1'
    else if (currentFilter.value === 3) params.msgType = '2'
    else if (currentFilter.value === 4) params.msgType = '3'
    else if (currentFilter.value === 5) params.msgType = '4'

    const res = await listMessage(params)
    messageList.value = (res.rows || []) as XfMessage[]
    total.value = res.total || 0
  } catch (error) {
    ElMessage.error('加载消息列表失败')
  } finally {
    loading.value = false
  }
}

async function loadUnreadCount() {
  try {
    const res = await unreadCount()
    unreadTotal.value = (res.data as number) || 0
  } catch (error) {
    console.error('加载未读数量失败:', error)
  }
}

async function handleRead(msg: XfMessage) {
  if (msg.isRead === '0') {
    try {
      await readMessage(msg.id!)
      msg.isRead = '1'
      unreadTotal.value = Math.max(0, unreadTotal.value - 1)
    } catch (error) {
      console.error('标记已读失败:', error)
    }
  }
}

async function handleReadAll() {
  try {
    await readAllMessage()
    ElMessage.success('已全部标记为已读')
    unreadTotal.value = 0
    loadMessageList()
  } catch (error) {
    ElMessage.error('操作失败')
  }
}

function getMsgTypeClass(type: string | undefined) {
  if (!type) return 'type-system'
  const map: Record<string, string> = { '1': 'type-order', '2': 'type-review', '3': 'type-overdue', '4': 'type-system' }
  return map[type] || 'type-system'
}

function getMsgTypeText(type: string | undefined) {
  if (!type) return '系统通知'
  const map: Record<string, string> = { '1': '工单通知', '2': '审核通知', '3': '超期提醒', '4': '系统通知' }
  return map[type] || '系统通知'
}

function formatTime(time: string | undefined) {
  if (!time) return ''
  return time.replace('T', ' ').substring(0, 19)
}

onMounted(() => {
  loadMessageList()
  loadUnreadCount()
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

.message-wrapper { padding: 24px; background: $police-bg; min-height: calc(100vh - 48px); }

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

.header-left { display: flex; align-items: center; gap: 12px; }

.title-section {
  display: flex; align-items: center; gap: 12px;
  .title-icon { width: 24px; height: 24px; color: white; }
  .title-text { font-size: 18px; font-weight: 700; margin: 0; letter-spacing: 1px; }
}

.unread-badge {
  background: #EF4444; color: white; font-size: 12px; font-weight: 700;
  padding: 2px 8px; border-radius: 10px; min-width: 20px; text-align: center;
}

.header-actions {
  .action-btn {
    padding: 8px 14px; border-radius: 10px; font-size: 13px; font-weight: 500;
    border: 1px solid rgba(255, 255, 255, 0.3); background: rgba(255, 255, 255, 0.1);
    color: white; cursor: pointer; transition: all 0.2s;
    display: flex; align-items: center; gap: 6px;
    &:hover { background: rgba(255, 255, 255, 0.2); }
  }
}

.filter-section {
  padding: 12px 24px; border-bottom: 1px solid $police-border;
  background: #F8FAFE;
}

.filter-pills { display: flex; gap: 8px; }

.pill-btn {
  padding: 6px 16px; border-radius: 20px; font-size: 13px; font-weight: 500;
  border: 1px solid $police-border; background: white;
  color: $police-text-muted; cursor: pointer; transition: all 0.2s;

  &:hover { border-color: $police-text-muted; }
  &.active { background: $police-blue; color: white; border-color: $police-blue; }
}

.message-list {
  padding: 16px 24px; flex: 1; overflow-y: auto;
}

.message-item {
  display: flex; gap: 16px; padding: 16px; border-radius: 12px;
  border: 1px solid $police-border; margin-bottom: 12px;
  cursor: pointer; transition: all 0.2s;

  &:hover { background: #F8FAFE; border-color: $police-light-blue; }
  &.unread { background: #F0F9FF; border-color: #BAE6FD; }
}

.msg-icon {
  width: 40px; height: 40px; border-radius: 12px;
  display: flex; align-items: center; justify-content: center;
  flex-shrink: 0;

  &.type-order { background: #DBEAFE; color: #2563EB; }
  &.type-review { background: #E0E7FF; color: #4F46E5; }
  &.type-overdue { background: #FEE2E2; color: #EF4444; }
  &.type-system { background: #F3F4F6; color: #6B7280; }
}

.msg-content { flex: 1; min-width: 0; }

.msg-header {
  display: flex; justify-content: space-between; align-items: center;
  margin-bottom: 6px;
}

.msg-title { font-size: 14px; font-weight: 600; color: $police-text; }

.msg-type-tag {
  padding: 2px 10px; border-radius: 10px; font-size: 11px; font-weight: 500;

  &.type-order { background: #DBEAFE; color: #2563EB; }
  &.type-review { background: #E0E7FF; color: #4F46E5; }
  &.type-overdue { background: #FEE2E2; color: #EF4444; }
  &.type-system { background: #F3F4F6; color: #6B7280; }
}

.msg-body {
  font-size: 13px; color: $police-text-muted; line-height: 1.5;
  margin-bottom: 8px;
  overflow: hidden; text-overflow: ellipsis; white-space: nowrap;
}

.msg-footer {
  display: flex; justify-content: space-between; align-items: center;
}

.msg-time { font-size: 12px; color: #94A3B8; }

.unread-dot {
  width: 8px; height: 8px; border-radius: 50%;
  background: #EF4444; display: inline-block;
  animation: pulse-dot 1.5s infinite;
}

@keyframes pulse-dot {
  0%, 100% { opacity: 1; }
  50% { opacity: 0.4; }
}

.empty-state {
  display: flex; flex-direction: column; align-items: center;
  padding: 60px 20px; color: $police-text-muted;
  .empty-icon { margin-bottom: 16px; opacity: 0.5; }
  .empty-text { font-size: 14px; }
}

.pagination-section {
  display: flex; justify-content: space-between; align-items: center;
  padding: 16px 24px; background: #F8FAFE; border-top: 1px solid $police-border; flex-shrink: 0;
  .page-stats { font-size: 13px; color: $police-text-muted; strong { color: $police-text; } }
}
</style>
