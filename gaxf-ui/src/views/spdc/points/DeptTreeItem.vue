<template>
  <div class="tree-item-wrapper">
    <div
      :class="['tree-item-row', { selected: isSelected, expandable: canExpand }]"
      :style="{ paddingLeft: depth * 12 + 6 + 'px' }"
      @click="handleClick"
    >
      <div class="expand-icon" v-if="canExpand" @click.stop="toggle">
        <LucideChevronRight
          class="icon-small"
          :class="{ rotated: expanded }"
        />
      </div>
      <div class="expand-icon" v-else></div>
      <LucideFolder class="icon-small folder-icon" />
      <span class="node-label">{{ node.label }}</span>
    </div>
    <template v-if="hasChildren && expanded">
      <DeptTreeItem
        v-for="child in node.children || []"
        :key="child.id"
        :node="child"
        :selected-id="selectedId"
        :depth="depth + 1"
        :load-children="loadChildren"
        @select="emit('select', $event)"
      />
    </template>
  </div>
</template>

<script setup lang="ts">
import { ref, computed } from 'vue'
import { LucideChevronRight, LucideFolder } from 'lucide-vue-next'
import type { TreeSelect } from '@/types/api/common'

const props = defineProps<{
  node: TreeSelect
  selectedId: number | undefined
  depth: number
  loadChildren?: (parentId: number) => Promise<TreeSelect[]>
}>()

const emit = defineEmits<{
  select: [id: number]
}>()

const expanded = ref(props.depth === 0)
const loading = ref(false)

const canExpand = computed(() => {
  if (props.node.children === undefined && (props.node as any).hasChild) {
    return !loading.value
  }
  return props.node.children && props.node.children.length > 0
})

const hasChildren = computed(() => {
  return props.node.children && props.node.children.length > 0
})

const isSelected = computed(() => props.selectedId === props.node.id)

function handleClick() {
  emit('select', props.node.id!)
}

async function toggle() {
  if (props.node.children === undefined && props.loadChildren && (props.node as any).hasChild) {
    loading.value = true
    try {
      const children = await props.loadChildren(props.node.id!)
      props.node.children = children
      // 加载完成后自动展开
      expanded.value = true
    } catch {
      console.error('Failed to load department children')
    } finally {
      loading.value = false
    }
  } else if (props.node.children && props.node.children.length > 0) {
    expanded.value = !expanded.value
  }
}
</script>

<style lang="scss" scoped>
$police-blue: #2563EB;
$police-light-blue: #3B82F6;
$police-text: #1E293B;
$police-text-muted: #5B6E8C;
$police-selected-bg: #E6F4FF;

.tree-item-wrapper {
  user-select: none;
}

.tree-item-row {
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

  &.selected {
    background: $police-selected-bg;
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

.folder-icon {
  flex-shrink: 0;
  color: $police-text-muted;
}

.node-label {
  flex: 1;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
  line-height: 1.4;
}

.rotated {
  transform: rotate(90deg);
}
</style>