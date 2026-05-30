<template>
  <div class="police-page">
    <!-- 顶部标题栏 -->
    <div class="page-header">
      <div class="header-title">
        <LucideDatabase class="header-icon" />
        <span>缓存管理</span>
      </div>
      <div class="header-actions">
        <button class="action-btn" @click="refreshCacheNames()">
          <LucideRefreshCw class="icon-small" :class="{ spinning: loading }" />
          <span>刷新</span>
        </button>
        <button class="action-btn danger" @click="handleClearCacheAll()">
          <LucideTrash2 class="icon-small" />
          <span>清理全部</span>
        </button>
      </div>
    </div>

    <!-- 三栏布局 -->
    <div class="cache-grid" v-loading="loading">
      <!-- 缓存名称列表 -->
      <div class="cache-panel">
        <div class="panel-header">
          <LucideFolder class="panel-icon" />
          <span>缓存名称</span>
          <span class="panel-count">{{ cacheNames.length }}</span>
        </div>
        <div class="panel-body">
          <div
            v-for="(item, index) in cacheNames"
            :key="item.cacheName"
            :class="['cache-item', { active: nowCacheName === item.cacheName }]"
            @click="getCacheKeys(item)"
          >
            <div class="item-info">
              <span class="item-name">{{ nameFormatter(item) }}</span>
              <span class="item-remark">{{ item.remark || '-' }}</span>
            </div>
            <button class="item-action" @click.stop="handleClearCacheName(item)">
              <LucideTrash2 :size="14" />
            </button>
          </div>
          <div v-if="!loading && cacheNames.length === 0" class="empty-item">
            <LucideInbox class="empty-icon" />
            <span>暂无缓存</span>
          </div>
        </div>
      </div>

      <!-- 键名列表 -->
      <div class="cache-panel">
        <div class="panel-header">
          <LucideKey class="panel-icon" />
          <span>键名列表</span>
          <span class="panel-count">{{ cacheKeys.length }}</span>
        </div>
        <div class="panel-body">
          <div
            v-for="(key, index) in cacheKeys"
            :key="key"
            :class="['cache-item', { active: cacheForm.cacheKey === key }]"
            @click="handleCacheValue(key)"
          >
            <div class="item-info">
              <span class="item-name">{{ keyFormatter(key) }}</span>
            </div>
            <button class="item-action" @click.stop="handleClearCacheKey(key)">
              <LucideTrash2 :size="14" />
            </button>
          </div>
          <div v-if="!subLoading && cacheKeys.length === 0" class="empty-item">
            <LucideInbox class="empty-icon" />
            <span>请选择缓存名称</span>
          </div>
          <div v-if="subLoading" class="loading-item">
            <LucideLoader2 class="loading-icon spinning" />
            <span>加载中...</span>
          </div>
        </div>
      </div>

      <!-- 缓存内容 -->
      <div class="cache-panel content-panel">
        <div class="panel-header">
          <LucideFileText class="panel-icon" />
          <span>缓存内容</span>
        </div>
        <div class="panel-body">
          <div class="content-form" v-if="cacheForm.cacheName">
            <div class="form-row">
              <label class="form-label">缓存名称</label>
              <div class="form-value">{{ cacheForm.cacheName }}</div>
            </div>
            <div class="form-row">
              <label class="form-label">缓存键名</label>
              <div class="form-value">{{ cacheForm.cacheKey }}</div>
            </div>
            <div class="form-row">
              <label class="form-label">缓存内容</label>
              <div class="form-textarea">{{ cacheForm.cacheValue }}</div>
            </div>
          </div>
          <div v-else class="empty-item">
            <LucideFileText class="empty-icon" />
            <span>请选择键名查看内容</span>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import {
  LucideDatabase, LucideFolder, LucideKey, LucideFileText,
  LucideRefreshCw, LucideTrash2, LucideInbox, LucideLoader2
} from 'lucide-vue-next'
import {
  listCacheName, listCacheKey, getCacheValue,
  clearCacheName, clearCacheKey, clearCacheAll
} from '@/api/monitor/cache'
import type { SysCache } from '@/types/api/monitor/cache'

interface CacheName {
  cacheName: string
  remark?: string
}

interface CacheForm {
  cacheName?: string
  cacheKey?: string
  cacheValue?: string
}

const cacheNames = ref<CacheName[]>([])
const cacheKeys = ref<string[]>([])
const cacheForm = ref<CacheForm>({})
const loading = ref(true)
const subLoading = ref(false)
const nowCacheName = ref('')

/** 查询缓存名称列表 */
async function getCacheNames() {
  loading.value = true
  try {
    const res = await listCacheName()
    cacheNames.value = (res.data || []).map(item => ({
      cacheName: item.cacheName || '',
      remark: item.remark || ''
    }))
  } catch (error) {
    ElMessage.error('加载缓存列表失败')
  } finally {
    loading.value = false
  }
}

/** 刷新缓存名称列表 */
function refreshCacheNames() {
  getCacheNames()
  cacheKeys.value = []
  cacheForm.value = {}
  nowCacheName.value = ''
  ElMessage.success('刷新成功')
}

/** 清理指定名称缓存 */
async function handleClearCacheName(row: CacheName) {
  try {
    await ElMessageBox.confirm(`确认清理缓存[${nameFormatter(row)}]吗？`, '提示', { type: 'warning' })
    await clearCacheName(row.cacheName)
    ElMessage.success('清理成功')
    if (nowCacheName.value === row.cacheName) {
      cacheKeys.value = []
      cacheForm.value = {}
    }
    getCacheNames()
  } catch {}
}

/** 查询缓存键名列表 */
async function getCacheKeys(row?: CacheName) {
  const cacheName = row ? row.cacheName : nowCacheName.value
  if (!cacheName) return

  subLoading.value = true
  nowCacheName.value = cacheName
  cacheForm.value = {}

  try {
    const res = await listCacheKey(cacheName)
    cacheKeys.value = res.data || []
  } catch (error) {
    ElMessage.error('加载键名列表失败')
  } finally {
    subLoading.value = false
  }
}

/** 清理指定键名缓存 */
async function handleClearCacheKey(cacheKey: string) {
  try {
    await ElMessageBox.confirm(`确认清理键名[${keyFormatter(cacheKey)}]吗？`, '提示', { type: 'warning' })
    await clearCacheKey(cacheKey)
    ElMessage.success('清理成功')
    if (cacheForm.value.cacheKey === cacheKey) {
      cacheForm.value = {}
    }
    getCacheKeys()
  } catch {}
}

/** 列表前缀去除 */
function nameFormatter(row: CacheName): string {
  if (!row?.cacheName) return ''
  return row.cacheName.replace(':', '')
}

/** 键名前缀去除 */
function keyFormatter(cacheKey: string): string {
  return cacheKey.replace(nowCacheName.value, '')
}

/** 查询缓存内容详细 */
async function handleCacheValue(cacheKey: string) {
  try {
    const res = await getCacheValue(nowCacheName.value, cacheKey)
    cacheForm.value = {
      cacheName: nameFormatter({ cacheName: nowCacheName.value }),
      cacheKey: keyFormatter(cacheKey),
      cacheValue: res.data?.cacheValue || ''
    }
  } catch (error) {
    ElMessage.error('获取缓存内容失败')
  }
}

/** 清理全部缓存 */
async function handleClearCacheAll() {
  try {
    await ElMessageBox.confirm('确认清理全部缓存吗？', '提示', { type: 'warning' })
    await clearCacheAll()
    ElMessage.success('清理成功')
    cacheKeys.value = []
    cacheForm.value = {}
    nowCacheName.value = ''
    getCacheNames()
  } catch {}
}

onMounted(() => {
  getCacheNames()
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

.spinning {
  animation: spin 1s linear infinite;
}

@keyframes spin {
  from { transform: rotate(0deg); }
  to { transform: rotate(360deg); }
}

.page-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 24px;
}

.header-title {
  display: flex;
  align-items: center;
  gap: 12px;
  font-size: 20px;
  font-weight: 700;
  color: $police-primary;

  .header-icon {
    width: 28px;
    height: 28px;
    color: $police-blue;
  }
}

.header-actions {
  display: flex;
  gap: 12px;
}

.action-btn {
  padding: 10px 20px;
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
    transform: translateY(-2px);
  }

  &.danger {
    background: #EF4444;
    color: white;
    border-color: #EF4444;

    &:hover {
      background: #DC2626;
    }
  }
}

.cache-grid {
  display: grid;
  grid-template-columns: repeat(3, 1fr);
  gap: 20px;
  min-height: calc(100vh - 200px);
}

.cache-panel {
  background: white;
  border-radius: 20px;
  border: 1px solid $police-border;
  overflow: hidden;
  display: flex;
  flex-direction: column;
  transition: all 0.2s;

  &:hover {
    box-shadow: 0 4px 12px rgba(0, 0, 0, 0.08);
  }
}

.panel-header {
  background: linear-gradient(135deg, $police-primary 0%, $police-blue 100%);
  color: white;
  padding: 16px 20px;
  display: flex;
  align-items: center;
  gap: 10px;
  font-size: 14px;
  font-weight: 600;

  .panel-icon {
    width: 18px;
    height: 18px;
  }

  .panel-count {
    background: rgba(255, 255, 255, 0.2);
    padding: 2px 8px;
    border-radius: 12px;
    font-size: 12px;
    margin-left: auto;
  }
}

.panel-body {
  flex: 1;
  overflow-y: auto;
  padding: 12px;

  &::-webkit-scrollbar {
    width: 4px;
  }
  &::-webkit-scrollbar-thumb {
    background: #CBD5E1;
    border-radius: 2px;
  }
}

.cache-item {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 12px 16px;
  border-radius: 12px;
  border: 1px solid transparent;
  background: #F8FAFE;
  margin-bottom: 8px;
  cursor: pointer;
  transition: all 0.2s;

  &:hover {
    background: #EFF6FF;
    border-color: $police-light-blue;
  }

  &.active {
    background: $police-blue;
    color: white;

    .item-name, .item-remark {
      color: white;
    }

    .item-action {
      background: rgba(255, 255, 255, 0.2);
      color: white;

      &:hover {
        background: rgba(255, 255, 255, 0.3);
      }
    }
  }
}

.item-info {
  display: flex;
  flex-direction: column;
  gap: 4px;
  flex: 1;
  overflow: hidden;
}

.item-name {
  font-size: 13px;
  font-weight: 600;
  color: $police-text;
  overflow: hidden;
  text-overflow: ellipsis;
}

.item-remark {
  font-size: 11px;
  color: $police-text-muted;
  overflow: hidden;
  text-overflow: ellipsis;
}

.item-action {
  width: 28px;
  height: 28px;
  border-radius: 8px;
  background: #FEE2E2;
  color: #EF4444;
  border: none;
  cursor: pointer;
  display: flex;
  align-items: center;
  justify-content: center;
  transition: all 0.2s;
  flex-shrink: 0;

  &:hover {
    background: #EF4444;
    color: white;
  }
}

.empty-item, .loading-item {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: 40px 20px;
  color: $police-text-muted;
  gap: 12px;

  .empty-icon, .loading-icon {
    width: 32px;
    height: 32px;
    opacity: 0.5;
  }
}

.content-panel .panel-body {
  padding: 20px;
}

.content-form {
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.form-row {
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.form-label {
  font-size: 12px;
  color: $police-text-muted;
  font-weight: 500;
}

.form-value {
  font-size: 14px;
  color: $police-text;
  background: #F8FAFE;
  padding: 10px 14px;
  border-radius: 10px;
  border: 1px solid $police-border;
}

.form-textarea {
  font-size: 13px;
  color: $police-text;
  background: #F8FAFE;
  padding: 14px;
  border-radius: 10px;
  border: 1px solid $police-border;
  min-height: 200px;
  max-height: 400px;
  overflow-y: auto;
  white-space: pre-wrap;
  word-break: break-all;
  line-height: 1.5;

  &::-webkit-scrollbar {
    width: 4px;
  }
  &::-webkit-scrollbar-thumb {
    background: #CBD5E1;
    border-radius: 2px;
  }
}

@media (max-width: 1100px) {
  .cache-grid {
    grid-template-columns: 1fr;
    min-height: auto;
  }
}
</style>