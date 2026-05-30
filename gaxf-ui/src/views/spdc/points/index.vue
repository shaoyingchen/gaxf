<template>
  <div class="police-page">
    <!-- 部门树 + 点位列表 -->
    <div class="content-grid">
      <!-- 左侧：部门树 -->
      <div class="dept-panel">
        <div class="panel-header">
          <h3><LucideFolder class="icon-small" /> 部门目录</h3>
        </div>
        <div class="search-box">
          <LucideSearch class="search-icon" />
          <input
            v-model="deptSearch"
            class="search-input"
            placeholder="搜索部门..."
          />
        </div>
        <div v-if="deptLoading" class="loading-state">
          <LucideLoader2 class="icon-spin" />
          <span>加载中...</span>
        </div>
        <div v-else class="dept-tree">
          <DeptTreeItem
            v-for="dept in filteredDepts"
            :key="dept.id"
            :node="dept"
            :selected-id="selectedDeptId"
            :depth="0"
            :load-children="loadChildren"
            @select="handleSelectDept"
          />
          <div v-if="filteredDepts.length === 0" class="empty-state">
            无匹配部门
          </div>
        </div>
      </div>

      <!-- 右侧：点位列表 -->
      <div class="point-panel">
        <div class="panel-header">
          <h3>
            <LucideMapPin class="icon-small" />
            {{ selectedDeptName ? selectedDeptName + ' — ' : '' }}点位列表
          </h3>
          <span class="total-count">共 {{ total }} 条</span>
        </div>

        <!-- 表格区域 -->
        <div class="table-wrapper">
          <table class="police-table">
            <thead>
              <tr>
                <th>点位名称</th>
                <th>编号</th>
                <th>类型</th>
                <th>状态</th>
                <th>IP地址</th>
                <th>操作</th>
              </tr>
            </thead>
            <tbody>
              <tr v-if="cameraLoading">
                <td colspan="6" class="loading-cell">
                  <LucideLoader2 class="icon-spin" />
                  <span>加载中...</span>
                </td>
              </tr>
              <tr v-else-if="cameraList.length === 0">
                <td colspan="6" class="empty-cell">暂无数据</td>
              </tr>
              <tr v-for="camera in cameraList" :key="camera.pointId" class="hover-row">
                <td class="name-cell">
                  <LucideCamera class="icon-muted" />
                  <span>{{ camera.pointName || camera.name || '-' }}</span>
                </td>
                <td class="code-cell">{{ camera.code || camera.gbCode || '-' }}</td>
                <td>
                  <span class="type-tag">{{ camera.zoneType || camera.roomKind || '其他' }}</span>
                </td>
                <td>
                  <span v-if="camera.pointStatus === 1" class="status-online">
                    <span class="pulse-dot online"></span> 在线
                  </span>
                  <span v-else class="status-offline">
                    <span class="pulse-dot offline"></span> 离线
                  </span>
                </td>
                <td class="ip-cell">{{ camera.ipAddress || '-' }}</td>
                <td class="action-cell">
                  <button class="action-btn primary" @click="handleView(camera)">
                    <LucideEye class="icon-small" /> 详情
                  </button>
                  <button class="action-btn" @click="handleEdit(camera)">
                    <LucideEdit class="icon-small" /> 编辑
                  </button>
                </td>
              </tr>
            </tbody>
          </table>
        </div>

        <!-- 分页区域 -->
        <div class="pagination-bar">
          <span class="page-info">第 {{ queryParams.pageNum }} 页 / 每页 {{ queryParams.pageSize }} 条</span>
          <div class="page-buttons">
            <button
              :disabled="queryParams.pageNum <= 1"
              class="page-btn"
              @click="handlePrevPage"
            >
              <LucideChevronLeft class="icon-small" /> 上一页
            </button>
            <button
              :disabled="cameraList.length < queryParams.pageSize"
              class="page-btn"
              @click="handleNextPage"
            >
              <LucideChevronRight class="icon-small" /> 下一页
            </button>
          </div>
        </div>
      </div>
    </div>

    <!-- 详情对话框 -->
    <div v-if="showDetail" class="modal-overlay" @click.self="closeDetail">
      <div class="modal-content detail-modal">
        <div class="modal-header">
          <h3><LucideCamera class="icon-small" /> 点位详情</h3>
          <button class="close-btn" @click="closeDetail">
            <LucideX class="icon-small" />
          </button>
        </div>
        <div class="modal-body">
          <div class="detail-grid">
            <div class="detail-section">
              <h4 class="section-title">基本信息</h4>
              <div class="detail-row">
                <span class="label">点位名称</span>
                <span class="value">{{ currentCamera.pointName || '-' }}</span>
              </div>
              <div class="detail-row">
                <span class="label">国标编码</span>
                <span class="value mono">{{ currentCamera.gbCode || '-' }}</span>
              </div>
              <div class="detail-row">
                <span class="label">设备编码</span>
                <span class="value mono">{{ currentCamera.code || '-' }}</span>
              </div>
              <div class="detail-row">
                <span class="label">所属部门</span>
                <span class="value">{{ currentCamera.deptName || '-' }}</span>
              </div>
              <div class="detail-row">
                <span class="label">区域类型</span>
                <span class="value">{{ currentCamera.zoneType || currentCamera.roomKind || '-' }}</span>
              </div>
            </div>
            <div class="detail-section">
              <h4 class="section-title">设备信息</h4>
              <div class="detail-row">
                <span class="label">IP地址</span>
                <span class="value mono">{{ currentCamera.ipAddress || '-' }}</span>
              </div>
              <div class="detail-row">
                <span class="label">端口</span>
                <span class="value mono">{{ currentCamera.port || '-' }}</span>
              </div>
              <div class="detail-row">
                <span class="label">通道号</span>
                <span class="value">{{ currentCamera.channelNo || '-' }}</span>
              </div>
              <div class="detail-row">
                <span class="label">摄像机类型</span>
                <span class="value">{{ cameraTypeText }}</span>
              </div>
              <div class="detail-row">
                <span class="label">点位状态</span>
                <span :class="['value', currentCamera.pointStatus === 1 ? 'text-success' : 'text-danger']">
                  {{ currentCamera.pointStatus === 1 ? '在线' : '离线' }}
                </span>
              </div>
            </div>
            <div class="detail-section">
              <h4 class="section-title">视频分析</h4>
              <div class="detail-row">
                <span class="label">分析状态</span>
                <span :class="['value', currentCamera.detect === 1 ? 'text-success' : 'text-muted']">
                  {{ currentCamera.detect === 1 ? '已启用' : '未启用' }}
                </span>
              </div>
              <div class="detail-row">
                <span class="label">检测人数</span>
                <span class="value">{{ currentCamera.personNum || '-' }}</span>
              </div>
              <div class="detail-row">
                <span class="label">视频分析类型</span>
                <span class="value">{{ upAlarmText }}</span>
              </div>
            </div>
            <div class="detail-section">
              <h4 class="section-title">责任信息</h4>
              <div class="detail-row">
                <span class="label">责任人</span>
                <span class="value">{{ currentCamera.dutyUser || '-' }}</span>
              </div>
              <div class="detail-row">
                <span class="label">责任电话</span>
                <span class="value mono">{{ currentCamera.dutyTel || '-' }}</span>
              </div>
              <div class="detail-row">
                <span class="label">备注</span>
                <span class="value">{{ currentCamera.remark || currentCamera.detectTag || '-' }}</span>
              </div>
            </div>
          </div>
          <div class="detail-footer">
            <div class="meta-info">
              <span>创建时间：{{ currentCamera.createTime || '-' }}</span>
              <span>更新时间：{{ currentCamera.updateTime || '-' }}</span>
            </div>
          </div>
        </div>
      </div>
    </div>

    <!-- 编辑对话框 -->
    <div v-if="showEdit" class="modal-overlay" @click.self="closeEdit">
      <div class="modal-content edit-modal">
        <div class="modal-header">
          <h3><LucideEdit class="icon-small" /> 编辑点位</h3>
          <button class="close-btn" @click="closeEdit">
            <LucideX class="icon-small" />
          </button>
        </div>
        <div class="modal-body">
          <form class="edit-form" @submit.prevent="submitEdit">
            <div class="form-section">
              <h4 class="section-title">基本信息</h4>
              <div class="form-row">
                <label class="form-label required">点位名称</label>
                <input v-model="editForm.pointName" class="form-input" placeholder="请输入点位名称" required />
              </div>
              <div class="form-row">
                <label class="form-label">国标编码</label>
                <input v-model="editForm.gbCode" class="form-input mono" placeholder="20位国标编码" maxlength="20" />
              </div>
              <div class="form-row">
                <label class="form-label">所属部门</label>
                <div class="dept-select" @click="showDeptPicker = !showDeptPicker">
                  <span>{{ editForm.deptName || '请选择部门' }}</span>
                  <LucideChevronDown class="icon-small" />
                </div>
                <div v-if="showDeptPicker" class="dept-picker-dropdown">
                  <DeptTreeItem
                    v-for="dept in deptOptions"
                    :key="dept.id"
                    :node="dept"
                    :selected-id="editForm.deptId"
                    :depth="0"
                    @select="selectEditDept"
                  />
                </div>
              </div>
              <div class="form-row">
                <label class="form-label">区域类型</label>
                <input v-model="editForm.zoneType" class="form-input" placeholder="请输入区域类型" />
              </div>
            </div>
            <div class="form-section">
              <h4 class="section-title">设备信息</h4>
              <div class="form-row">
                <label class="form-label">IP地址</label>
                <input v-model="editForm.ipAddress" class="form-input mono" placeholder="例如：192.168.1.100" />
              </div>
              <div class="form-row half">
                <div class="half-col">
                  <label class="form-label">端口</label>
                  <input v-model="editForm.port" class="form-input" placeholder="例如：8000" />
                </div>
                <div class="half-col">
                  <label class="form-label">通道号</label>
                  <input v-model.number="editForm.channelNo" type="number" class="form-input" placeholder="1" />
                </div>
              </div>
              <div class="form-row">
                <label class="form-label">摄像机类型</label>
                <div class="radio-group">
                  <label class="radio-item" :class="{ active: editForm.cameraType === 1 }">
                    <input type="radio" v-model.number="editForm.cameraType" :value="1" />
                    <span>海康</span>
                  </label>
                  <label class="radio-item" :class="{ active: editForm.cameraType === 2 }">
                    <input type="radio" v-model.number="editForm.cameraType" :value="2" />
                    <span>大华</span>
                  </label>
                </div>
              </div>
              <div class="form-row half">
                <div class="half-col">
                  <label class="form-label">用户名</label>
                  <input v-model="editForm.username" class="form-input" placeholder="摄像机登录用户名" />
                </div>
                <div class="half-col">
                  <label class="form-label">密码</label>
                  <input v-model="editForm.password" type="password" class="form-input" placeholder="摄像机登录密码" />
                </div>
              </div>
            </div>
            <div class="form-section">
              <h4 class="section-title">视频分析</h4>
              <div class="form-row">
                <label class="form-label">分析状态</label>
                <div class="switch-wrapper">
                  <span class="switch-label">启用分析</span>
                  <button type="button" class="switch-btn" :class="{ on: editForm.detect === 1 }" @click="editForm.detect = editForm.detect === 1 ? 0 : 1">
                    <span class="switch-slider"></span>
                  </button>
                </div>
              </div>
              <div class="form-row">
                <label class="form-label">责任人</label>
                <input v-model="editForm.dutyUser" class="form-input" placeholder="责任人姓名" />
              </div>
              <div class="form-row">
                <label class="form-label">责任电话</label>
                <input v-model="editForm.dutyTel" class="form-input mono" placeholder="责任人联系电话" />
              </div>
              <div class="form-row">
                <label class="form-label">备注</label>
                <textarea v-model="editForm.remark" class="form-textarea" placeholder="备注信息" rows="3"></textarea>
              </div>
            </div>
            <div class="form-actions">
              <button type="button" class="cancel-btn" @click="closeEdit">取消</button>
              <button type="submit" class="submit-btn" :disabled="editLoading">
                <LucideLoader2 v-if="editLoading" class="icon-spin" />
                <span v-else>保存修改</span>
              </button>
            </div>
          </form>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, onMounted, onActivated, watch } from 'vue'
import {
  LucideFolder, LucideSearch, LucideLoader2, LucideMapPin,
  LucideCamera, LucideEye, LucideEdit, LucideChevronLeft, LucideChevronRight,
  LucideX, LucideChevronDown
} from 'lucide-vue-next'
import { deptTree } from '@/api/system/dept'
import { listDept } from '@/api/system/dept'
import { listCameraByDept, listCamera, getCamera, updateCamera } from '@/api/spdc/camera'
import { ElMessage } from 'element-plus'
import DeptTreeItem from './DeptTreeItem.vue'
import type { TreeSelect } from '@/types/api/common'
import type { DcCamera, CameraQueryParams } from '@/types'

const deptSearch = ref('')
const deptLoading = ref(false)
const deptOptions = ref<TreeSelect[]>([])
const selectedDeptId = ref<number | undefined>(undefined)

const cameraLoading = ref(false)
const cameraList = ref<DcCamera[]>([])
const total = ref(0)

const queryParams = ref<CameraQueryParams>({
  pageNum: 1,
  pageSize: 10
})

// 详情对话框
const showDetail = ref(false)
const currentCamera = ref<DcCamera>({})

// 编辑对话框
const showEdit = ref(false)
const editForm = ref<DcCamera>({})
const editLoading = ref(false)
const showDeptPicker = ref(false)

const selectedDeptName = computed(() => {
  if (!selectedDeptId.value) return ''
  const findDept = (depts: TreeSelect[]): TreeSelect | null => {
    for (const d of depts) {
      if (d.id === selectedDeptId.value) return d
      const found = findDept(d.children || [])
      if (found) return found
    }
    return null
  }
  return findDept(deptOptions.value)?.label || ''
})

// 详情显示文本
const cameraTypeText = computed(() => {
  if (currentCamera.value.cameraType === 1) return '海康'
  if (currentCamera.value.cameraType === 2) return '大华'
  return '-'
})

const upAlarmText = computed(() => {
  const types: Record<number, string> = {
    1: '人脸识别',
    2: '车牌识别',
    3: '行为分析',
    4: '人数统计'
  }
  return types[currentCamera.value.upAlarm || 0] || '-'
})

const filteredDepts = computed(() => {
  if (!deptSearch.value) return deptOptions.value
  const keyword = deptSearch.value.toLowerCase()
  const filterTree = (nodes: TreeSelect[]): TreeSelect[] => {
    return nodes.reduce<TreeSelect[]>((acc, node) => {
      const match = node.label?.toLowerCase().includes(keyword)
      const filteredChildren = filterTree(node.children || [])
      if (match || filteredChildren.length > 0) {
        acc.push({ ...node, children: filteredChildren })
      }
      return acc
    }, [])
  }
  return filterTree(deptOptions.value)
})

// 点击外部关闭部门选择器
watch(showDeptPicker, (val) => {
  if (val) {
    setTimeout(() => {
      document.addEventListener('click', closeDeptPickerOnClickOutside)
    }, 100)
  } else {
    document.removeEventListener('click', closeDeptPickerOnClickOutside)
  }
})

function closeDeptPickerOnClickOutside(e: MouseEvent) {
  const target = e.target as HTMLElement
  if (!target.closest('.dept-select') && !target.closest('.dept-picker-dropdown')) {
    showDeptPicker.value = false
  }
}

function handleSelectDept(deptId: number) {
  selectedDeptId.value = deptId
  queryParams.value.pageNum = 1
  queryParams.value.deptId = deptId
  getCameraList()
}

function selectEditDept(deptId: number) {
  editForm.value.deptId = deptId
  // 查找部门名称
  const findDeptName = (depts: TreeSelect[]): string => {
    for (const d of depts) {
      if (d.id === deptId) return d.label || ''
      if (d.children) {
        const name = findDeptName(d.children)
        if (name) return name
      }
    }
    return ''
  }
  editForm.value.deptName = findDeptName(deptOptions.value)
  showDeptPicker.value = false
}

function handlePrevPage() {
  if (queryParams.value.pageNum > 1) {
    queryParams.value.pageNum--
    getCameraList()
  }
}

function handleNextPage() {
  queryParams.value.pageNum++
  getCameraList()
}

function getCameraList() {
  cameraLoading.value = true
  const params = { ...queryParams.value }
  const promise = selectedDeptId.value
    ? listCameraByDept(selectedDeptId.value, params)
    : listCamera(params)
  promise.then(response => {
    cameraList.value = response.rows || []
    total.value = response.total || 0
    cameraLoading.value = false
  }).catch(() => {
    cameraList.value = []
    total.value = 0
    cameraLoading.value = false
  })
}

function loadChildren(parentId: number) {
  return listDept({ parentId: String(parentId) }).then(response => {
    return (response.data || []).map(d => ({
      id: d.deptId!,
      label: d.deptName!,
      disabled: false,
      children: undefined as TreeSelect[] | undefined,
      hasChild: true
    }))
  })
}

function loadInitialTree() {
  deptLoading.value = true
  deptTree().then(response => {
    const tree = response.data || []
    const truncateToLevel2 = (nodes: TreeSelect[]): TreeSelect[] => {
      return nodes.map(node => {
        if (node.children && node.children.length > 0) {
          const truncated = node.children.map(child => {
            if (child.children && child.children.length > 0) {
              return { ...child, children: undefined, hasChild: true }
            }
            return { ...child, children: undefined }
          })
          return { ...node, children: truncated }
        }
        return node
      })
    }
    deptOptions.value = truncateToLevel2(tree)
    deptLoading.value = false
  }).catch(() => {
    deptOptions.value = []
    deptLoading.value = false
  })
}

// 详情功能
function handleView(camera: DcCamera) {
  getCamera(camera.pointId!).then(response => {
    currentCamera.value = response.data || camera
    showDetail.value = true
  }).catch(() => {
    currentCamera.value = camera
    showDetail.value = true
  })
}

function closeDetail() {
  showDetail.value = false
  currentCamera.value = {}
}

// 编辑功能
function handleEdit(camera: DcCamera) {
  getCamera(camera.pointId!).then(response => {
    editForm.value = { ...response.data } || { ...camera }
    showEdit.value = true
  }).catch(() => {
    editForm.value = { ...camera }
    showEdit.value = true
  })
}

function closeEdit() {
  showEdit.value = false
  editForm.value = {}
  showDeptPicker.value = false
}

async function submitEdit() {
  if (!editForm.value.pointName) {
    ElMessage.warning('请输入点位名称')
    return
  }
  editLoading.value = true
  try {
    await updateCamera(editForm.value)
    ElMessage.success('修改成功')
    closeEdit()
    getCameraList()
  } catch (error) {
    ElMessage.error('修改失败')
  } finally {
    editLoading.value = false
  }
}

onMounted(() => {
  refreshData()
})

onActivated(() => {
  refreshData()
})

function refreshData() {
  loadInitialTree()
  getCameraList()
}
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

.dept-panel {
  background: white;
  border-radius: 20px;
  border: 1px solid $police-border;
  display: flex;
  flex-direction: column;
  overflow: hidden;
}

.panel-header {
  padding: 14px 16px;
  border-bottom: 1px solid $police-border;
  display: flex;
  justify-content: space-between;
  align-items: center;

  h3 {
    font-size: 13px;
    font-weight: 600;
    color: $police-primary;
    display: flex;
    align-items: center;
    gap: 6px;
  }
}

.search-box {
  padding: 10px 14px;
  position: relative;

  .search-icon {
    position: absolute;
    left: 22px;
    top: 50%;
    transform: translateY(-50%);
    width: 14px;
    height: 14px;
    color: $police-text-muted;
  }

  .search-input {
    width: 100%;
    padding: 8px 10px 8px 32px;
    border: 1px solid $police-border;
    border-radius: 10px;
    font-size: 12px;
    color: $police-text;
    background: #F5F5F5;
    transition: all 0.2s;

    &:focus {
      outline: none;
      border-color: $police-blue;
      background: white;
    }

    &::placeholder { color: $police-text-muted; }
  }
}

.dept-tree {
  flex: 1;
  overflow-y: auto;
  padding: 6px 10px;
}

.loading-state, .empty-state {
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 8px;
  padding: 40px 20px;
  color: $police-text-muted;
  font-size: 13px;
}

.icon-spin {
  animation: spin 1s linear infinite;
}

@keyframes spin {
  from { transform: rotate(0deg); }
  to { transform: rotate(360deg); }
}

.icon-small { width: 14px; height: 14px; }
.icon-muted { width: 14px; height: 14px; color: $police-text-muted; }

.point-panel {
  background: white;
  border-radius: 20px;
  border: 1px solid $police-border;
  display: flex;
  flex-direction: column;
  overflow: hidden;
}

.total-count {
  font-size: 12px;
  color: $police-text-muted;
}

.table-wrapper {
  flex: 1;
  overflow-y: auto;
}

.police-table {
  width: 100%;
  border-collapse: collapse;

  thead {
    background: #FAFAFA;
    position: sticky;
    top: 0;
    z-index: 1;

    th {
      padding: 14px 16px;
      text-align: left;
      font-size: 12px;
      font-weight: 600;
      color: $police-text-muted;
      border-bottom: 1px solid $police-border;
      text-transform: uppercase;
      letter-spacing: 0.5px;
    }
  }

  tbody tr {
    border-bottom: 1px solid $police-border;
    transition: background 0.2s;

    &.hover-row:hover { background: #F5F5F5; }
  }

  td {
    padding: 14px 16px;
    font-size: 13px;
    color: $police-text;

    &.name-cell {
      display: flex;
      align-items: center;
      gap: 8px;
      font-weight: 500;
    }

    &.code-cell, &.ip-cell {
      font-family: ui-monospace, monospace;
      font-size: 12px;
      color: $police-text-muted;
    }

    &.loading-cell, &.empty-cell {
      text-align: center;
      padding: 40px;
      color: $police-text-muted;
    }

    &.loading-cell {
      display: flex;
      align-items: center;
      justify-content: center;
      gap: 8px;
    }
  }
}

.type-tag {
  padding: 4px 10px;
  border-radius: 20px;
  font-size: 11px;
  font-weight: 500;
  background: rgba(59, 130, 246, 0.1);
  color: $police-blue;
}

.status-online, .status-offline {
  display: flex;
  align-items: center;
  gap: 6px;
  font-size: 12px;
}

.pulse-dot {
  width: 8px;
  height: 8px;
  border-radius: 50%;

  &.online {
    background: #10B981;
    animation: pulse-green 1.5s infinite;
  }
  &.offline {
    background: #EF4444;
    animation: pulse-red 1.5s infinite;
  }
}

@keyframes pulse-green {
  0%, 100% { opacity: 1; transform: scale(1); }
  50% { opacity: 0.5; transform: scale(0.8); }
}

@keyframes pulse-red {
  0%, 100% { opacity: 1; transform: scale(1); }
  50% { opacity: 0.5; transform: scale(0.8); }
}

.status-online { color: #10B981; }
.status-offline { color: #EF4444; }

.action-cell {
  display: flex;
  gap: 8px;
}

.action-btn {
  padding: 6px 12px;
  border-radius: 20px;
  font-size: 12px;
  font-weight: 500;
  border: 1px solid $police-border;
  background: white;
  color: $police-text;
  cursor: pointer;
  transition: all 0.2s;
  display: flex;
  align-items: center;
  gap: 4px;

  &:hover {
    background: #F5F5F5;
    border-color: $police-text-muted;
  }

  &.primary {
    background: $police-blue;
    color: white;
    border-color: $police-blue;

    &:hover { background: $police-light-blue; }
  }
}

.pagination-bar {
  padding: 16px 20px;
  border-top: 1px solid $police-border;
  display: flex;
  justify-content: space-between;
  align-items: center;

  .page-info {
    font-size: 12px;
    color: $police-text-muted;
  }
}

.page-buttons {
  display: flex;
  gap: 12px;
}

.page-btn {
  padding: 8px 16px;
  border-radius: 20px;
  font-size: 12px;
  font-weight: 500;
  border: 1px solid $police-border;
  background: white;
  color: $police-text;
  cursor: pointer;
  transition: all 0.2s;
  display: flex;
  align-items: center;
  gap: 4px;

  &:hover:not(:disabled) {
    background: $police-blue;
    color: white;
    border-color: $police-blue;
  }

  &:disabled {
    opacity: 0.5;
    cursor: not-allowed;
  }
}

@media (max-width: 1100px) {
  .content-grid { grid-template-columns: 1fr; height: auto; }
  .dept-panel { max-height: 300px; }
}

// 模态框样式
.modal-overlay {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: rgba(0, 0, 0, 0.5);
  display: flex;
  align-items: center;
  justify-content: center;
  z-index: 1000;
}

.modal-content {
  background: white;
  border-radius: 16px;
  max-height: 90vh;
  overflow: hidden;
  display: flex;
  flex-direction: column;
}

.detail-modal {
  width: 700px;
}

.edit-modal {
  width: 600px;
}

.modal-header {
  padding: 16px 20px;
  border-bottom: 1px solid $police-border;
  display: flex;
  justify-content: space-between;
  align-items: center;

  h3 {
    font-size: 16px;
    font-weight: 600;
    color: $police-primary;
    display: flex;
    align-items: center;
    gap: 8px;
  }

  .close-btn {
    width: 32px;
    height: 32px;
    border-radius: 8px;
    border: 1px solid $police-border;
    background: white;
    cursor: pointer;
    display: flex;
    align-items: center;
    justify-content: center;
    transition: all 0.2s;

    &:hover {
      background: #F5F5F5;
      border-color: $police-text-muted;
    }
  }
}

.modal-body {
  padding: 20px;
  overflow-y: auto;
  flex: 1;
}

// 详情样式
.detail-grid {
  display: grid;
  grid-template-columns: repeat(2, 1fr);
  gap: 20px;
}

.detail-section {
  .section-title {
    font-size: 13px;
    font-weight: 600;
    color: $police-primary;
    margin-bottom: 12px;
    padding-bottom: 8px;
    border-bottom: 1px solid $police-border;
  }
}

.detail-row {
  display: flex;
  margin-bottom: 10px;

  .label {
    width: 80px;
    font-size: 12px;
    color: $police-text-muted;
    flex-shrink: 0;
  }

  .value {
    font-size: 13px;
    color: $police-text;
    flex: 1;

    &.mono {
      font-family: ui-monospace, monospace;
      font-size: 12px;
    }

    &.text-success {
      color: #10B981;
    }

    &.text-danger {
      color: #EF4444;
    }

    &.text-muted {
      color: $police-text-muted;
    }
  }
}

.detail-footer {
  margin-top: 20px;
  padding-top: 16px;
  border-top: 1px solid $police-border;

  .meta-info {
    display: flex;
    gap: 24px;
    font-size: 12px;
    color: $police-text-muted;
  }
}

// 编辑表单样式
.edit-form {
  .form-section {
    margin-bottom: 20px;

    .section-title {
      font-size: 13px;
      font-weight: 600;
      color: $police-primary;
      margin-bottom: 12px;
      padding-bottom: 8px;
      border-bottom: 1px solid $police-border;
    }
  }

  .form-row {
    margin-bottom: 12px;

    &.half {
      display: grid;
      grid-template-columns: repeat(2, 1fr);
      gap: 16px;
    }
  }

  .form-label {
    display: block;
    font-size: 12px;
    color: $police-text-muted;
    margin-bottom: 6px;

    &.required::after {
      content: '*';
      color: #EF4444;
      margin-left: 4px;
    }
  }

  .form-input {
    width: 100%;
    padding: 10px 12px;
    border: 1px solid $police-border;
    border-radius: 8px;
    font-size: 13px;
    color: $police-text;
    background: #FAFAFA;
    transition: all 0.2s;

    &:focus {
      outline: none;
      border-color: $police-blue;
      background: white;
    }

    &.mono {
      font-family: ui-monospace, monospace;
      font-size: 12px;
    }
  }

  .form-textarea {
    width: 100%;
    padding: 10px 12px;
    border: 1px solid $police-border;
    border-radius: 8px;
    font-size: 13px;
    color: $police-text;
    background: #FAFAFA;
    resize: vertical;
    min-height: 80px;

    &:focus {
      outline: none;
      border-color: $police-blue;
      background: white;
    }
  }

  .dept-select {
    width: 100%;
    padding: 10px 12px;
    border: 1px solid $police-border;
    border-radius: 8px;
    font-size: 13px;
    color: $police-text;
    background: #FAFAFA;
    cursor: pointer;
    display: flex;
    justify-content: space-between;
    align-items: center;
    transition: all 0.2s;

    &:hover {
      border-color: $police-blue;
      background: white;
    }
  }

  .dept-picker-dropdown {
    position: absolute;
    top: 100%;
    left: 0;
    right: 0;
    margin-top: 4px;
    background: white;
    border: 1px solid $police-border;
    border-radius: 8px;
    max-height: 200px;
    overflow-y: auto;
    z-index: 10;
    padding: 8px;
  }

  .radio-group {
    display: flex;
    gap: 12px;
  }

  .radio-item {
    padding: 8px 16px;
    border-radius: 20px;
    border: 1px solid $police-border;
    font-size: 13px;
    cursor: pointer;
    display: flex;
    align-items: center;
    gap: 6px;
    transition: all 0.2s;

    input { display: none; }

    &:hover {
      border-color: $police-blue;
    }

    &.active {
      background: $police-blue;
      color: white;
      border-color: $police-blue;
    }
  }

  .switch-wrapper {
    display: flex;
    align-items: center;
    gap: 12px;

    .switch-label {
      font-size: 13px;
      color: $police-text;
    }

    .switch-btn {
      width: 48px;
      height: 26px;
      border-radius: 13px;
      background: #E5E7EB;
      border: none;
      cursor: pointer;
      position: relative;
      transition: all 0.3s;

      .switch-slider {
        position: absolute;
        top: 3px;
        left: 3px;
        width: 20px;
        height: 20px;
        border-radius: 50%;
        background: white;
        transition: all 0.3s;
      }

      &.on {
        background: $police-blue;

        .switch-slider {
          left: 25px;
        }
      }
    }
  }

  .form-actions {
    display: flex;
    justify-content: flex-end;
    gap: 12px;
    padding-top: 16px;
    border-top: 1px solid $police-border;
    margin-top: 20px;

    .cancel-btn {
      padding: 10px 20px;
      border-radius: 20px;
      border: 1px solid $police-border;
      background: white;
      font-size: 13px;
      font-weight: 500;
      color: $police-text;
      cursor: pointer;
      transition: all 0.2s;

      &:hover {
        background: #F5F5F5;
        border-color: $police-text-muted;
      }
    }

    .submit-btn {
      padding: 10px 24px;
      border-radius: 20px;
      border: none;
      background: $police-blue;
      font-size: 13px;
      font-weight: 500;
      color: white;
      cursor: pointer;
      display: flex;
      align-items: center;
      gap: 6px;
      transition: all 0.2s;

      &:hover:not(:disabled) {
        background: $police-light-blue;
      }

      &:disabled {
        opacity: 0.6;
        cursor: not-allowed;
      }
    }
  }
}
</style>