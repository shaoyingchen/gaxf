<template>
  <div class="theme-switcher">
    <el-dropdown trigger="click" @command="handleCommand">
      <el-button type="primary" plain size="small">
        <el-icon><Brush /></el-icon>
        <span class="theme-name">{{ currentThemeName }}</span>
      </el-button>
      <template #dropdown>
        <el-dropdown-menu>
          <el-dropdown-item
            v-for="theme in themeList"
            :key="theme.key"
            :command="theme.key"
            :class="{ 'is-active': currentTheme === theme.key }"
          >
            <div class="theme-option">
              <span class="theme-preview" :style="theme.previewStyle"></span>
              <span class="theme-label">{{ theme.name }}</span>
              <el-icon v-if="currentTheme === theme.key" class="check-icon"><Check /></el-icon>
            </div>
          </el-dropdown-item>
        </el-dropdown-menu>
      </template>
    </el-dropdown>
  </div>
</template>

<script setup lang="ts">
import { computed } from 'vue'
import { Brush, Check } from '@element-plus/icons-vue'
import { useTheme } from '@/views/spdc/composables/useTheme'

const { currentTheme, setTheme, themes } = useTheme()

interface ThemeOption {
  key: string
  name: string
  previewStyle: string
}

const themeList: ThemeOption[] = [
  {
    key: 'police',
    name: '公安蓝',
    previewStyle: 'background: linear-gradient(135deg, #1E3A8A 50%, #2F54EB 50%);'
  },
  {
    key: 'default',
    name: '经典蓝',
    previewStyle: 'background: linear-gradient(135deg, #020617 50%, #3b82f6 50%);'
  },
  {
    key: 'crimson',
    name: '威严红',
    previewStyle: 'background: linear-gradient(135deg, #1a0505 50%, #dc2626 50%);'
  },
  {
    key: 'midnight',
    name: '极客黑',
    previewStyle: 'background: linear-gradient(135deg, #0a0a0c 50%, #71717a 50%);'
  }
]

const currentThemeName = computed(() =>
  themeList.find(t => t.key === currentTheme.value)?.name || '公安蓝'
)

const handleCommand = (command: string) => {
  setTheme(command)
}
</script>

<style lang="scss" scoped>
.theme-switcher {
  display: inline-flex;

  .theme-name {
    margin-left: 6px;
    font-weight: 700;
  }
}

.theme-option {
  display: flex;
  align-items: center;
  gap: 10px;
  padding: 4px 0;

  .theme-preview {
    width: 24px;
    height: 24px;
    border-radius: 6px;
    border: 1px solid #E5E7EB;
    flex-shrink: 0;
  }

  .theme-label {
    flex: 1;
    font-weight: 500;
  }

  .check-icon {
    color: #2F54EB;
    font-size: 16px;
  }
}

.el-dropdown-item.is-active {
  background-color: #E6F0FF;
}
</style>