<template>
  <div class="sidebar-container" :class="{ 'has-logo': showLogo }">
    <logo v-if="showLogo" :collapse="isCollapse" />
    <el-scrollbar wrap-class="scrollbar-wrapper">
      <el-menu
        :default-active="activeMenu"
        :collapse="isCollapse"
        :unique-opened="true"
        :collapse-transition="false"
        mode="vertical"
      >
        <sidebar-item
          v-for="(route, index) in sidebarRouters"
          :key="route.path + index"
          :item="route"
          :base-path="route.path"
        />
      </el-menu>
    </el-scrollbar>

    <!-- 底部安全等级信息 -->
    <div v-if="!isCollapse" class="sidebar-footer">
      <div class="security-badge">
        <svg-icon icon-class="monitor" />
        安全等级 · 三级等保
      </div>
      <div class="version-info">v2.6.0 | 智慧警务</div>
    </div>
  </div>
</template>

<script setup lang="ts">
import Logo from './Logo.vue'
import SidebarItem from './SidebarItem.vue'
import useAppStore from '@/store/modules/app'
import useSettingsStore from '@/store/modules/settings'
import usePermissionStore from '@/store/modules/permission'

const route = useRoute()
const appStore = useAppStore()
const settingsStore = useSettingsStore()
const permissionStore = usePermissionStore()

const sidebarRouters = computed(() => permissionStore.sidebarRouters)
const showLogo = computed(() => settingsStore.sidebarLogo)
const isCollapse = computed(() => !appStore.sidebar.opened)

const activeMenu = computed(() => {
  const { meta, path } = route
  if (meta.activeMenu) {
    return meta.activeMenu
  }
  return path
})
</script>

<style lang="scss" scoped>
.sidebar-container {
  background: linear-gradient(180deg, #1E3A8A 0%, #172554 100%);

  .el-menu {
    background: transparent;
    border: none;
  }

  .sidebar-footer {
    padding: 20px 20px 30px;
    font-size: 12px;
    border-top: 1px solid rgba(255, 255, 255, 0.12);
    color: #A5C0E0;
    line-height: 1.6;

    .security-badge {
      display: flex;
      align-items: center;
      gap: 6px;
      margin-bottom: 8px;

      .svg-icon {
        width: 14px;
        height: 14px;
      }
    }

    .version-info {
      opacity: 0.8;
    }
  }
}
</style>