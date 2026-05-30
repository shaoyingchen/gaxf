<template>
  <div class="resource-tree-wrapper">
    <!-- 搜索框 -->
    <div class="search-box">
      <LucideSearch class="search-icon" />
      <input
        v-model="searchKeyword"
        class="search-input"
        placeholder="搜索点位..."
      />
    </div>

    <!-- 加载状态 -->
    <div v-if="loading" class="loading-state">
      <LucideLoader2 class="icon-spin" />
      <span>加载中...</span>
    </div>

    <!-- 树内容 -->
    <div v-else class="tree-content">
      <DeptNode
        v-for="dept in filteredTree"
        :key="dept.id"
        :dept="dept"
        :depth="0"
        :expanded-depts="expandedDepts"
        :selected-point="selectedPoint"
        :search-keyword="searchKeyword"
        :dept-points-map="deptPointsMap"
        @toggle-dept="toggleDept"
        @select-point="handleSelectPoint"
        @load-points="loadDeptPoints"
      />
      <div v-if="filteredTree.length === 0" class="empty-state">
        无匹配点位
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, onMounted } from 'vue'
import { LucideSearch, LucideLoader2 } from 'lucide-vue-next'
import { deptTree } from '@/api/system/dept'
import { listCamera } from '@/api/spdc/camera'
import DeptNode from './DeptNode.vue'
import type { TreeSelect } from '@/types/api/common'
import type { DcCamera } from '@/types'

// Extended TreeSelect with matched points for search filtering
interface TreeSelectWithMatchedPoints extends TreeSelect {
  _matchedPoints?: DcCamera[]
}

const emit = defineEmits<{
  selectPoint: [camera: DcCamera]
}>()

const searchKeyword = ref('')
const loading = ref(false)
const deptData = ref<TreeSelect[]>([])
const expandedDepts = ref<Set<number>>(new Set())
const selectedPoint = ref<number | undefined>()
const deptPointsMap = ref<Map<number, DcCamera[]>>(new Map())

// 过滤后的树
const filteredTree = computed(() => {
  if (!searchKeyword.value) return deptData.value

  const keyword = searchKeyword.value.toLowerCase()
  const filterDeptTree = (depts: TreeSelect[]): TreeSelect[] => {
    return depts.reduce<TreeSelect[]>((acc, dept) => {
      const points = deptPointsMap.value.get(dept.id!) || []
      const matchedPoints = points.filter((p: DcCamera) =>
        p.pointName?.toLowerCase().includes(keyword) ||
        p.gbCode?.toLowerCase().includes(keyword)
      )

      if (matchedPoints.length > 0) {
        // 部门匹配，保留并标记展开
        expandedDepts.value.add(dept.id!)
        acc.push({
          ...dept,
          children: [],
          _matchedPoints: matchedPoints
        } as TreeSelectWithMatchedPoints)
      } else if (dept.children && dept.children.length > 0) {
        const filteredChildren = filterDeptTree(dept.children)
        if (filteredChildren.length > 0) {
          expandedDepts.value.add(dept.id!)
          acc.push({ ...dept, children: filteredChildren })
        }
      }

      return acc
    }, [])
  }

  return filterDeptTree(deptData.value)
})

// 切换部门展开
async function toggleDept(deptId: number) {
  if (expandedDepts.value.has(deptId)) {
    expandedDepts.value.delete(deptId)
  } else {
    expandedDepts.value.add(deptId)
    // 加载该部门的点位
    if (!deptPointsMap.value.has(deptId)) {
      await loadDeptPoints(deptId)
    }
  }
}

// 加载部门点位
async function loadDeptPoints(deptId: number) {
  try {
    const response = await listCamera({ deptId, pageNum: 1, pageSize: 100 })
    deptPointsMap.value.set(deptId, response.rows || [])
  } catch {
    deptPointsMap.value.set(deptId, [])
  }
}

// 选择点位
function handleSelectPoint(camera: DcCamera) {
  selectedPoint.value = camera.pointId
  emit('selectPoint', camera)
}

// 初始化
onMounted(async () => {
  loading.value = true
  try {
    const response = await deptTree()
    deptData.value = response.data || []
    // 默认展开第一个部门
    if (deptData.value.length > 0) {
      const firstDeptId = deptData.value[0].id!
      expandedDepts.value.add(firstDeptId)
      await loadDeptPoints(firstDeptId)
    }
  } catch {
    deptData.value = []
  } finally {
    loading.value = false
  }
})

// 暴露方法供外部获取点位列表
function getDeptPoints(deptId: number): DcCamera[] {
  return deptPointsMap.value.get(deptId) || []
}
defineExpose({ getDeptPoints, deptPointsMap, loading })
</script>

<style lang="scss" scoped>
$police-blue: #2563EB;
$police-text-muted: #5B6E8C;
$police-border: #E2E8F0;

.resource-tree-wrapper {
  display: flex;
  flex-direction: column;
  height: 100%;
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

.tree-content {
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
</style>