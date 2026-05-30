<template>
  <div class="dept-node-wrapper">
    <!-- 部门节点 -->
    <div
      :class="['dept-row', { expanded: isExpanded }]"
      :style="{ paddingLeft: depth * 12 + 6 + 'px' }"
      @click="handleToggleDept"
    >
      <div class="expand-icon">
        <LucideChevronRight
          v-if="hasChildren || !pointsLoaded"
          class="icon-small"
          :class="{ rotated: isExpanded }"
        />
      </div>
      <LucideFolder class="icon-small folder-icon" />
      <span class="dept-name">{{ dept.label }}</span>
      <span v-if="isExpanded && localPoints.length > 0" class="point-count">
        {{ localPoints.length }}
      </span>
    </div>

    <!-- 展开后：子部门 + 点位列表 -->
    <div v-if="isExpanded" class="expanded-content">
      <!-- 点位列表 -->
      <div v-if="displayPoints.length > 0" class="points-list">
        <div
          v-for="point in displayPoints"
          :key="point.pointId"
          :class="['point-row', { selected: selectedPoint === point.pointId }]"
          :style="{ paddingLeft: (depth + 1) * 12 + 6 + 'px' }"
          @click="handleSelectPoint(point)"
        >
          <div class="expand-icon"></div>
          <LucideVideo class="icon-small video-icon" />
          <span class="point-name">{{ point.pointName }}</span>
          <span :class="['point-status', point.pointStatus === 1 ? 'online' : 'offline']">
            <span :class="['status-dot', point.pointStatus === 1 ? 'online' : 'offline']"></span>
            {{ point.pointStatus === 1 ? '在线' : '离线' }}
          </span>
        </div>
      </div>

      <!-- 子部门 -->
      <DeptNode
        v-for="child in dept.children || []"
        :key="child.id"
        :dept="child"
        :depth="depth + 1"
        :expanded-depts="expandedDepts"
        :selected-point="selectedPoint"
        :search-keyword="searchKeyword"
        :dept-points-map="deptPointsMap"
        @toggle-dept="emit('toggleDept', $event)"
        @select-point="emit('selectPoint', $event)"
        @load-points="emit('loadPoints', $event)"
      />
    </div>
  </div>
</template>

<script setup lang="ts">
import { computed, ref, watch } from 'vue'
import { LucideChevronRight, LucideFolder, LucideVideo } from 'lucide-vue-next'
import type { TreeSelect } from '@/types/api/common'
import type { DcCamera } from '@/types'

// Extended TreeSelect with matched points for search filtering
interface TreeSelectWithMatchedPoints extends TreeSelect {
  _matchedPoints?: DcCamera[]
}

const props = defineProps<{
  dept: TreeSelect
  depth: number
  expandedDepts: Set<number>
  selectedPoint: number | undefined
  searchKeyword?: string
  deptPointsMap?: Map<number, DcCamera[]>
}>()

const emit = defineEmits<{
  toggleDept: [deptId: number]
  selectPoint: [camera: DcCamera]
  loadPoints: [deptId: number]
}>()

const localPoints = ref<DcCamera[]>([])

const isExpanded = computed(() => props.expandedDepts.has(props.dept.id!))

const hasChildren = computed(() =>
  props.dept.children && props.dept.children.length > 0
)

// Check if points have been loaded for this department
const pointsLoaded = computed(() => {
  if (!props.deptPointsMap) return false
  return props.deptPointsMap.has(props.dept.id!)
})

// 获取点位数据
const getPoints = (): DcCamera[] => {
  const map = props.deptPointsMap
  if (!map) return []
  return map.get(props.dept.id!) || []
}

// 搜索匹配的点位
const displayPoints = computed(() => {
  const matchedPoints = (props.dept as TreeSelectWithMatchedPoints)._matchedPoints
  const points: DcCamera[] = matchedPoints || localPoints.value || getPoints()
  if (!props.searchKeyword) return points
  const keyword = props.searchKeyword.toLowerCase()
  return points.filter((p: DcCamera) =>
    p.pointName?.toLowerCase().includes(keyword) ||
    p.gbCode?.toLowerCase().includes(keyword)
  )
})

// 监听展开状态，请求加载点位
watch(isExpanded, async (expanded) => {
  if (expanded) {
    const points = getPoints()
    if (points.length > 0) {
      localPoints.value = points
    } else {
      // 通知父组件加载点位
      emit('loadPoints', props.dept.id!)
    }
  }
})

// 监听点位数据变化
watch(() => props.deptPointsMap, () => {
  if (isExpanded.value) {
    localPoints.value = getPoints()
  }
}, { deep: true })

function handleToggleDept() {
  emit('toggleDept', props.dept.id!)
}

function handleSelectPoint(point: DcCamera) {
  emit('selectPoint', point)
}
</script>

<style lang="scss" scoped>
$police-blue: #2563EB;
$police-light-blue: #3B82F6;
$police-text: #1E293B;
$police-text-muted: #5B6E8C;
$police-selected-bg: #E6F4FF;

.dept-node-wrapper {
  user-select: none;
}

.dept-row {
  display: flex;
  align-items: center;
  gap: 6px;
  padding: 8px 10px;
  border-radius: 8px;
  cursor: pointer;
  transition: all 0.2s;
  font-size: 12px;
  color: $police-text-muted;

  &:hover {
    background: rgba(59, 130, 246, 0.05);
    color: $police-text;
  }

  &.expanded {
    color: $police-blue;
    font-weight: 500;

    .folder-icon { color: $police-blue; }
  }
}

.expand-icon {
  width: 14px;
  height: 14px;
  display: flex;
  align-items: center;
  justify-content: center;
  flex-shrink: 0;
}

.icon-small {
  width: 14px;
  height: 14px;
}

.folder-icon, .video-icon {
  flex-shrink: 0;
  color: $police-text-muted;
}

.dept-name, .point-name {
  flex: 1;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.point-count {
  font-size: 10px;
  padding: 2px 6px;
  border-radius: 10px;
  background: rgba(59, 130, 246, 0.1);
  color: $police-blue;
}

.rotated {
  transform: rotate(90deg);
}

.expanded-content {
  border-left: 2px solid rgba(59, 130, 246, 0.2);
  margin-left: 12px;
}

.points-list {
  margin-bottom: 4px;
}

.point-row {
  display: flex;
  align-items: center;
  gap: 6px;
  padding: 6px 10px;
  border-radius: 8px;
  cursor: pointer;
  transition: all 0.2s;
  font-size: 11px;
  color: $police-text-muted;

  &:hover {
    background: rgba(59, 130, 246, 0.08);
    color: $police-text;
  }

  &.selected {
    background: $police-selected-bg;
    color: $police-blue;
    font-weight: 500;

    .video-icon { color: $police-blue; }
  }
}

.point-status {
  font-size: 10px;
  display: flex;
  align-items: center;
  gap: 4px;

  &.online { color: #10B981; }
  &.offline { color: #EF4444; }
}

.status-dot {
  width: 6px;
  height: 6px;
  border-radius: 50%;

  &.online { background: #10B981; }
  &.offline { background: #EF4444; }
}
</style>