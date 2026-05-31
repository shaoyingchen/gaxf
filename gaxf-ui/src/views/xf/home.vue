<template>
  <div class="home-app-container">
    <!-- 侧边栏 - 公安风格 -->
    <aside class="sidebar">
      <!-- Logo 区域 -->
      <div class="logo-area">
        <div class="logo-icon">
          <svg width="28" height="28" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" class="icon">
            <path d="M12 22s8-4 8-10V5l-8-3-8 3v7c0 6 8 10 8 10z"/>
          </svg>
          <div class="logo-glow"></div>
        </div>
        <div class="logo-text">
          <h2>智慧·信访</h2>
        </div>
      </div>

      <!-- 导航菜单 -->
      <nav class="nav-menu">
        <template v-for="menu in dynamicMenus" :key="menu.id">
          <!-- 叶子菜单项 -->
          <div
            v-if="!menu.children || menu.children.length === 0"
            class="nav-item leaf"
            :class="{ active: activeTab === menu.id }"
            @click="handleNavClick(menu)"
          >
            <div class="menu-row">
              <div class="menu-row-left">
                <component :is="menu.icon" :size="22" class="icon" />
                <span>{{ menu.label }}</span>
              </div>
            </div>
          </div>

          <!-- 可展开菜单项 -->
          <div v-else class="nav-item parent">
            <div class="menu-row" @click="toggleSubmenu(menu.id)">
              <div class="menu-row-left">
                <component :is="menu.icon" :size="22" class="icon" />
                <span>{{ menu.label }}</span>
              </div>
              <ChevronRight :size="12" class="arrow-icon" :class="{ rotated: expandedMenus[menu.id] }" />
            </div>
            <div class="submenu" :class="{ show: expandedMenus[menu.id] }">
              <div
                v-for="child in menu.children"
                :key="child.id"
                class="sub-item"
                :class="{ active: activeTab === child.id }"
                @click="handleNavClick(child)"
              >{{ child.label }}</div>
            </div>
          </div>
        </template>
      </nav>

      <!-- 版本与版权信息 -->
      <div class="version-info">
        <div class="version-text">智慧·信访 v1.0.0</div>
        <div class="copyright">&copy; 盐城南方软件</div>
      </div>
    </aside>

    <!-- 主界面 -->
    <main class="main-content">
      <!-- 顶部 Header -->
      <header class="top-bar">
        <div class="header-inner">
          <!-- 标题区 -->
          <div class="title-area">
            <div class="title-badge">
              <svg width="28" height="28" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                <path d="M12 22s8-4 8-10V5l-8-3-8 3v7c0 6 8 10 8 10z"/>
              </svg>
            </div>
            <div class="title-content">
              <h2 class="main-title">信访事项交办反馈平台</h2>
              <div class="title-tags">
                <span class="tag-item active">
                  <span class="tag-dot"></span>信访事项交办
                </span>
                <span class="tag-divider"></span>
                <span class="tag-item">
                  <span class="tag-dot blue"></span>高效协同处置
                </span>
                <span class="tag-divider"></span>
                <span class="tag-item warning">
                  <span class="tag-dot red"></span>红色预警响应
                </span>
              </div>
            </div>
          </div>

          <!-- 右侧信息与图标区 -->
          <div class="right-area">
            <!-- 时间胶囊 -->
            <div class="time-capsule">
              <span class="time-text">{{ currentDateTime }}</span>
            </div>

            <!-- 图标组 -->
            <div class="icon-group">
              <button
                @click="cycleTheme"
                title="切换显示主题"
                class="icon-btn"
              >
                <Palette :size="20" />
              </button>

              <button class="icon-btn" @click="handleNavClick({ id: 'xf_message', label: '站内消息', icon: Bell, component: 'xf/message/index', path: '/xf/message' })">
                <Bell :size="20" />
                <span v-if="unreadMsgCount > 0" class="msg-badge">{{ unreadMsgCount }}</span>
              </button>

              <el-dropdown trigger="click" @command="handleUserCommand">
                <button class="icon-btn">
                  <UserIcon :size="20" />
                </button>
                <template #dropdown>
                  <el-dropdown-menu>
                    <el-dropdown-item disabled>
                      <UserIcon :size="14" class="mr-2" /> {{ userName }}
                    </el-dropdown-item>
                    <el-dropdown-item command="profile">
                      <UserIcon :size="14" class="mr-2" /> 个人中心
                    </el-dropdown-item>
                    <el-dropdown-item command="logout" divided>
                      <LogOut :size="14" class="mr-2" /> 退出登录
                    </el-dropdown-item>
                  </el-dropdown-menu>
                </template>
              </el-dropdown>
            </div>
          </div>
        </div>
      </header>

      <!-- 标签页导航栏 -->
      <div class="tabs-nav">
        <div class="tabs-container">
          <div
            v-for="tab in openedTabs"
            :key="tab.id"
            class="tab-item"
            :class="{ active: activeTab === tab.id }"
            @click="switchTab(tab)"
          >
            <component :is="tab.icon" :size="14" class="tab-icon" v-if="tab.icon" />
            <span class="tab-label">{{ tab.label }}</span>
            <button
              v-if="openedTabs.length > 1"
              class="tab-close"
              @click.stop="closeTab(tab)"
            >
              <LucideX :size="12" />
            </button>
          </div>
        </div>
        <div class="tabs-actions">
          <el-dropdown trigger="click" @command="handleTabsCommand">
            <button class="tabs-action-btn">
              <LucideChevronDown :size="14" />
            </button>
            <template #dropdown>
              <el-dropdown-menu>
                <el-dropdown-item command="closeOther">
                  <LucideXCircle :size="14" class="mr-2" /> 关闭其他
                </el-dropdown-item>
                <el-dropdown-item command="closeAll">
                  <LucideFolderX :size="14" class="mr-2" /> 关闭全部
                </el-dropdown-item>
              </el-dropdown-menu>
            </template>
          </el-dropdown>
        </div>
      </div>

      <!-- 内容展示区 -->
      <div class="content-wrapper custom-scrollbar">
        <component :is="currentComponent" :key="activeTab" />
      </div>
    </main>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, onUnmounted, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessageBox } from 'element-plus'
import useUserStore from '@/store/modules/user'
import usePermissionStore from '@/store/modules/permission'
import {
  LayoutDashboard,
  ClipboardList,
  ListTodo,
  ShieldCheck,
  FolderOpen,
  FileText,
  BarChart3,
  Bell,
  User as UserIcon,
  LogOut,
  Settings,
  ChevronRight,
  ChevronDown as LucideChevronDown,
  Folder,
  Users,
  Shield,
  List,
  Building,
  Briefcase,
  BookOpen,
  Database as LucideDatabase,
  X as LucideX,
  XCircle as LucideXCircle,
  FolderX as LucideFolderX,
  Palette
} from 'lucide-vue-next'

import { useTheme } from './composables/useTheme'
import Dashboard from './dashboard/index.vue'
import WorkOrderManage from './workOrder/index.vue'
import MyTodo from './myTodo/index.vue'
import ReviewManage from './review/index.vue'
import DossierManage from './dossier/index.vue'
import DocumentManage from './document/index.vue'
import StatisticsPage from './statistics/index.vue'
import MessagePage from './message/index.vue'

// 系统管理组件
import UserManage from '@/views/system/user/index.vue'
import RoleManage from '@/views/system/role/index.vue'
import MenuManage from '@/views/system/menu/index.vue'
import DeptManage from '@/views/system/dept/index.vue'
import PostManage from '@/views/system/post/index.vue'
import DictManage from '@/views/system/dict/index.vue'

// 个人中心组件
import ProfileCenter from '@/views/system/user/profile/index.vue'

import { unreadCount } from '@/api/xf/message'

const router = useRouter()
const userStore = useUserStore()
const permissionStore = usePermissionStore()
const { cycleTheme } = useTheme()

const userName = computed(() => userStore.user?.nickName || userStore.user?.userName || '管理员')
const unreadMsgCount = ref(0)

// 图标映射表
const iconMap: Record<string, any> = {
  'dashboard': LayoutDashboard,
  'workOrder': ClipboardList,
  'work-order': ClipboardList,
  'myTodo': ListTodo,
  'my-todo': ListTodo,
  'review': ShieldCheck,
  'dossier': FolderOpen,
  'document': FileText,
  'statistics': BarChart3,
  'message': Bell,
  'system': Settings,
  'settings': Settings,
  'tool': Settings,
  'user': Users,
  'users': Users,
  'peoples': Users,
  'role': Shield,
  'shield': Shield,
  'menu': List,
  'tree': List,
  'list': List,
  'dept': Building,
  'building': Building,
  'post': Briefcase,
  'briefcase': Briefcase,
  'dict': BookOpen,
  'book': BookOpen,
  'database': LucideDatabase,
  'folder': Folder,
  '': Folder
}

// 组件映射表
const componentMap: Record<string, any> = {
  // 个人中心
  'profile': ProfileCenter,
  'user/profile': ProfileCenter,
  // XF 业务组件
  'xf/dashboard/index': Dashboard,
  'xf/dashboard': Dashboard,
  'dashboard': Dashboard,
  'Xf/dashboard': Dashboard,
  'xf/workOrder/index': WorkOrderManage,
  'xf/workOrder': WorkOrderManage,
  'workOrder': WorkOrderManage,
  'Xf/workOrder': WorkOrderManage,
  'xf/myTodo/index': MyTodo,
  'xf/myTodo': MyTodo,
  'myTodo': MyTodo,
  'Xf/myTodo': MyTodo,
  'xf/review/index': ReviewManage,
  'xf/review': ReviewManage,
  'review': ReviewManage,
  'Xf/review': ReviewManage,
  'xf/dossier/index': DossierManage,
  'xf/dossier': DossierManage,
  'dossier': DossierManage,
  'Xf/dossier': DossierManage,
  'xf/document/index': DocumentManage,
  'xf/document': DocumentManage,
  'document': DocumentManage,
  'Xf/document': DocumentManage,
  'xf/statistics/index': StatisticsPage,
  'xf/statistics': StatisticsPage,
  'statistics': StatisticsPage,
  'Xf/statistics': StatisticsPage,
  'xf/message/index': MessagePage,
  'xf/message': MessagePage,
  'message': MessagePage,
  'Xf/message': MessagePage,
  // 系统管理组件
  'system/user/index': UserManage,
  'system/user': UserManage,
  'user': UserManage,
  'System/user': UserManage,
  'system/role/index': RoleManage,
  'system/role': RoleManage,
  'role': RoleManage,
  'System/role': RoleManage,
  'system/menu/index': MenuManage,
  'system/menu': MenuManage,
  'menu': MenuManage,
  'System/menu': MenuManage,
  'system/dept/index': DeptManage,
  'system/dept': DeptManage,
  'dept': DeptManage,
  'System/dept': DeptManage,
  'system/post/index': PostManage,
  'system/post': PostManage,
  'post': PostManage,
  'System/post': PostManage,
  'system/dict/index': DictManage,
  'system/dict': DictManage,
  'dict': DictManage,
  'System/dict': DictManage,
}

// 需要排除的菜单名称
const excludeMenuTitles = ['首页', '个人中心', 'Profile', 'Index', '未命名', '']

// 从 sidebarRouters 获取动态菜单
const dynamicMenus = computed(() => {
  const routes = permissionStore.sidebarRouters || []
  const result: any[] = []

  routes.forEach(route => {
    const meta = route.meta || {}
    const title = meta.title || ''
    const hasValidTitle = title && !excludeMenuTitles.includes(title)

    if (!hasValidTitle && route.children && route.children.length > 0) {
      route.children.forEach(child => {
        const menu = convertRouteToMenu(child)
        if (menu && !excludeMenuTitles.includes(menu.label)) result.push(menu)
      })
    } else if (hasValidTitle) {
      const menu = convertRouteToMenu(route)
      if (menu && !excludeMenuTitles.includes(menu.label)) result.push(menu)
    }
  })

  return result
})

function convertRouteToMenu(route: any): any {
  if (route.hidden) return null

  const meta = route.meta || {}
  const title = meta.title || ''
  if (excludeMenuTitles.includes(title)) return null

  const menuId = route.name || route.path?.replace(/\//g, '_') || 'unknown'
  const iconName = meta.icon || ''
  const iconComponent = iconMap[iconName] || Folder

  const menu: any = {
    id: menuId,
    label: title || route.name || '未命名',
    icon: iconComponent,
    path: route.path,
    component: route.component
  }

  if (route.children && route.children.length > 0) {
    const visibleChildren = route.children
      .map(child => convertRouteToMenu(child))
      .filter(m => m && !excludeMenuTitles.includes(m.label))
    if (visibleChildren.length > 0) menu.children = visibleChildren
  }

  return menu
}

function getComponentByMenu(menu: any): any {
  const possibleKeys: string[] = []

  if (menu.component && typeof menu.component === 'string') {
    possibleKeys.push(menu.component)
    possibleKeys.push(menu.component.replace('/index', '').replace(/^\/+/, '').replace(/\/+$/, ''))
  }
  if (menu.path && typeof menu.path === 'string') {
    possibleKeys.push(menu.path)
    possibleKeys.push(menu.path.replace(/^\/+/, '').replace(/\/+$/, ''))
    possibleKeys.push(menu.path.replace('/index', '').replace(/^\/+/, '').replace(/\/+$/, ''))
  }
  if (menu.id) {
    possibleKeys.push(menu.id)
    possibleKeys.push(menu.id.toLowerCase())
    possibleKeys.push(menu.id.replace(/_/g, '/'))
  }
  if (menu.label) {
    const labelKey = menu.label.toLowerCase().replace('管理', '').trim()
    possibleKeys.push(labelKey)
  }

  for (const key of possibleKeys) {
    if (key && componentMap[key]) return componentMap[key]
  }

  if (menu.children && menu.children.length > 0) {
    return getComponentByMenu(menu.children[0])
  }

  return Dashboard
}

// 日期时间
const currentDateTime = ref('')
let dateTimer: number
const updateDateTime = () => {
  const now = new Date()
  const dateStr = now.toLocaleDateString('zh-CN', {
    year: 'numeric', month: '2-digit', day: '2-digit', weekday: 'long'
  }).replace(/\//g, '-')
  const timeStr = now.toLocaleTimeString('zh-CN', { hour12: false })
  currentDateTime.value = dateStr + ' ' + timeStr
}
updateDateTime()
dateTimer = window.setInterval(updateDateTime, 1000)

// 导航与路由
const activeTab = ref('')
const activeMenu = ref<any>(null)
const openedTabs = ref<any[]>([])
const expandedMenus = ref<Record<string, boolean>>({})

const toggleSubmenu = (key: string) => {
  expandedMenus.value[key] = !expandedMenus.value[key]
}

const currentComponent = computed(() => {
  if (!activeMenu.value) {
    if (dynamicMenus.value.length > 0) {
      const firstMenu = dynamicMenus.value[0]
      return getComponentByMenu(firstMenu)
    }
    return Dashboard
  }
  return getComponentByMenu(activeMenu.value)
})

const handleNavClick = (menu: any) => {
  activeTab.value = menu.id
  activeMenu.value = menu

  const exists = openedTabs.value.find(t => t.id === menu.id)
  if (!exists) {
    openedTabs.value.push({
      id: menu.id,
      label: menu.label,
      icon: menu.icon,
      component: menu.component,
      path: menu.path
    })
  }
}

function switchTab(tab: any) {
  activeTab.value = tab.id
  activeMenu.value = tab
}

function closeTab(tab: any) {
  const index = openedTabs.value.findIndex(t => t.id === tab.id)
  if (index > -1) {
    openedTabs.value.splice(index, 1)
    if (activeTab.value === tab.id) {
      if (openedTabs.value.length > 0) {
        const newTab = openedTabs.value[Math.max(0, index - 1)]
        switchTab(newTab)
      }
    }
  }
}

function handleTabsCommand(cmd: string) {
  if (cmd === 'closeOther') {
    openedTabs.value = openedTabs.value.filter(t => t.id === activeTab.value)
  } else if (cmd === 'closeAll') {
    if (dynamicMenus.value.length > 0) {
      const firstMenu = dynamicMenus.value[0]
      const tab = {
        id: firstMenu.id, label: firstMenu.label, icon: firstMenu.icon,
        component: firstMenu.component, path: firstMenu.path
      }
      if (firstMenu.children && firstMenu.children.length > 0) {
        const child = firstMenu.children[0]
        tab.id = child.id; tab.label = child.label; tab.icon = child.icon; tab.component = child.component; tab.path = child.path
      }
      openedTabs.value = [tab]
      switchTab(tab)
    }
  }
}

const handleUserCommand = (cmd: string) => {
  if (cmd === 'logout') {
    ElMessageBox.confirm('确定要退出登录吗？', '提示', { type: 'warning' }).then(async () => {
      await userStore.logOut()
      router.push('/login')
    }).catch(() => {})
  } else if (cmd === 'profile') {
    const profileTab = { id: 'profile', label: '个人中心', icon: UserIcon, component: 'profile' }
    activeTab.value = 'profile'
    activeMenu.value = profileTab
    const exists = openedTabs.value.find(t => t.id === 'profile')
    if (!exists) openedTabs.value.push(profileTab)
  }
}

async function loadUnreadCount() {
  try {
    const res = await unreadCount()
    unreadMsgCount.value = (res.data as number) || 0
  } catch (error) {
    // ignore
  }
}

onMounted(() => {
  if (dynamicMenus.value.length > 0) {
    const firstMenu = dynamicMenus.value[0]
    let initTab: any
    if (firstMenu.children && firstMenu.children.length > 0) {
      expandedMenus.value[firstMenu.id] = true
      initTab = firstMenu.children[0]
    } else {
      initTab = firstMenu
    }
    activeTab.value = initTab.id
    activeMenu.value = initTab
    openedTabs.value = [{
      id: initTab.id, label: initTab.label, icon: initTab.icon,
      component: initTab.component, path: initTab.path
    }]
  }
  loadUnreadCount()
})

onUnmounted(() => {
  clearInterval(dateTimer)
})
</script>

<style lang="scss">
// 全局重置
html, body, #app {
  margin: 0 !important;
  padding: 0 !important;
  width: 100% !important;
  height: 100% !important;
  overflow: hidden;
}

// ===== 公安主题变量 =====
$police-primary: #1E3A8A;
$police-dark: #172554;
$police-blue: #2563EB;
$police-light-blue: #3B82F6;
$police-bg: #F8FAFE;
$police-text: #1E293B;
$police-text-muted: #5B6E8C;
$police-border: #E2E8F0;

// ===== 主容器 =====
.home-app-container {
  position: fixed !important;
  top: 0 !important;
  left: 0 !important;
  right: 0 !important;
  bottom: 0 !important;
  width: 100vw !important;
  height: 100vh !important;
  display: flex !important;
  overflow: hidden !important;
  background: $police-bg !important;
  box-sizing: border-box !important;
  margin: 0 !important;
  padding: 0 !important;
}

// ===== 侧边栏 =====
.sidebar {
  width: 260px;
  height: 100vh !important;
  min-height: 100vh !important;
  background: linear-gradient(180deg, $police-primary 0%, $police-dark 100%);
  color: #F0F4FA;
  display: flex;
  flex-direction: column;
  box-shadow: 4px 0 20px rgba(0, 0, 0, 0.08);
  z-index: 20;
  overflow-y: auto;
  flex-shrink: 0;

  &::-webkit-scrollbar { width: 6px; }
  &::-webkit-scrollbar-track { background: rgba(255, 255, 255, 0.05); }
  &::-webkit-scrollbar-thumb { background: rgba(255, 255, 255, 0.2); border-radius: 4px; }
}

.logo-area {
  padding: 28px 24px;
  border-bottom: 1px solid rgba(255, 255, 255, 0.08);
  margin-bottom: 24px;
  display: flex;
  align-items: center;
  gap: 16px;
  background: linear-gradient(135deg, rgba(59, 130, 246, 0.15) 0%, rgba(30, 58, 138, 0.08) 100%);
  position: relative;

  &::before {
    content: '';
    position: absolute;
    top: 0; left: 24px; right: 24px; height: 1px;
    background: linear-gradient(90deg, transparent, rgba(59, 130, 246, 0.5), transparent);
  }

  .logo-icon {
    background: linear-gradient(135deg, $police-light-blue 0%, $police-blue 100%);
    width: 52px; height: 52px; border-radius: 14px;
    display: flex; align-items: center; justify-content: center; color: white;
    box-shadow: 0 8px 20px -8px rgba(37, 99, 235, 0.5), 0 0 0 1px rgba(255, 255, 255, 0.1) inset;
    position: relative; flex-shrink: 0;

    .icon { width: 28px; height: 28px; stroke-width: 2; filter: drop-shadow(0 2px 4px rgba(0, 0, 0, 0.2)); }
    .logo-glow {
      position: absolute; inset: 0; border-radius: 14px;
      background: radial-gradient(circle at 30% 30%, rgba(255, 255, 255, 0.3) 0%, transparent 50%);
    }
  }

  .logo-text {
    flex: 1;
    h2 { font-size: 22px; font-weight: 700; letter-spacing: 2px; color: white; margin: 0; line-height: 1.2; text-shadow: 0 2px 8px rgba(0, 0, 0, 0.3); white-space: nowrap; }
  }
}

.nav-menu { flex: 1; padding: 0 12px 20px 12px; }

.nav-item {
  margin-bottom: 6px; border-radius: 12px; transition: all 0.2s;

  &.leaf .menu-row { justify-content: flex-start; }
  &.leaf .menu-row .menu-row-left { flex: 1; }
  &.leaf.active .menu-row {
    background: $police-blue; color: white;
    box-shadow: 0 4px 8px rgba(37, 99, 235, 0.3);
  }
}

.menu-row {
  display: flex; align-items: center; justify-content: space-between;
  padding: 12px 16px; border-radius: 12px; color: #CBD5E1;
  font-weight: 500; cursor: pointer; transition: all 0.2s;

  &:hover { background: #2E4A8A; color: #F1F5F9; }
  .icon { width: 22px; height: 22px; }
}

.menu-row-left { display: flex; align-items: center; gap: 14px; }

.arrow-icon {
  width: 12px; height: 12px; transition: transform 0.2s; color: #9BB5E0;
  &.rotated { transform: rotate(90deg); }
}

.submenu {
  margin-left: 24px; padding-left: 16px;
  border-left: 1px solid rgba(59, 130, 246, 0.4);
  display: none; flex-direction: column; gap: 4px; margin-top: 8px; margin-bottom: 8px;
  &.show { display: flex; }
}

.sub-item {
  padding: 10px 12px 10px 28px; border-radius: 10px; font-size: 13px; font-weight: 500;
  color: #CBD5E1; cursor: pointer; transition: all 0.2s;

  &:hover { background: #2D4A7C; color: white; }
  &.active { background: $police-blue; color: white; box-shadow: 0 2px 6px rgba(37, 99, 235, 0.3); }
}

.version-info {
  padding: 20px 24px 30px; border-top: 1px solid rgba(255, 255, 255, 0.08);
  margin-top: 20px; text-align: center;
  .version-text { font-size: 13px; color: #CBD5E1; font-weight: 500; margin-bottom: 6px; }
  .copyright { font-size: 11px; color: #94A3B8; }
}

// ===== 主内容区滚动条 =====
.custom-scrollbar::-webkit-scrollbar { width: 5px; }
.custom-scrollbar::-webkit-scrollbar-track { background: transparent; }
.custom-scrollbar::-webkit-scrollbar-thumb { background: rgba(255, 255, 255, 0.05); border-radius: 10px; }
.custom-scrollbar::-webkit-scrollbar-thumb:hover { background: rgba(255, 255, 255, 0.1); }

// ===== 主内容区 =====
.main-content { flex: 1; height: 100vh !important; display: flex; flex-direction: column; overflow: hidden; background: $police-bg; }

.top-bar {
  background: linear-gradient(135deg, #FFFFFF 0%, #F8FAFE 100%);
  padding: 20px 32px;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.06);
  position: sticky; top: 0; z-index: 10;
  border-bottom: 1px solid rgba(37, 99, 235, 0.1);
  .header-inner { display: flex; justify-content: space-between; align-items: center; }
}

.title-area { display: flex; align-items: center; gap: 16px; }

.title-badge {
  width: 56px; height: 56px;
  background: linear-gradient(135deg, $police-light-blue 0%, $police-blue 100%);
  border-radius: 14px; display: flex; align-items: center; justify-content: center; color: white;
  box-shadow: 0 4px 12px rgba(37, 99, 235, 0.3);
  svg { width: 28px; height: 28px; }
}

.title-content { display: flex; flex-direction: column; gap: 6px; }

.main-title { font-size: 22px; font-weight: 700; color: $police-primary; letter-spacing: 1px; margin: 0; line-height: 1.3; }

.title-tags {
  display: flex; align-items: center; gap: 0;

  .tag-item {
    display: flex; align-items: center; gap: 6px; font-size: 12px; color: $police-text-muted;
    padding: 4px 12px; border-radius: 20px; background: rgba(148, 163, 184, 0.1);
    &.active { background: rgba(59, 130, 246, 0.1); color: $police-blue; }
    &.warning { background: rgba(239, 68, 68, 0.1); color: #EF4444; }

    .tag-dot {
      width: 6px; height: 6px; border-radius: 50%; background: #94A3B8;
      &.blue { background: $police-blue; }
      &.red { background: #EF4444; animation: pulse-red-dot 1.5s infinite; }
    }
  }
  .tag-divider { width: 1px; height: 12px; background: #CBD5E1; margin: 0 8px; }
}

@keyframes pulse-red-dot {
  0%, 100% { opacity: 1; }
  50% { opacity: 0.4; }
}

.right-area { display: flex; align-items: center; gap: 20px; }

.time-capsule {
  background: linear-gradient(135deg, #F1F5F9 0%, #E2E8F0 100%);
  padding: 10px 20px; border-radius: 24px; border: 1px solid rgba(148, 163, 184, 0.3);
  position: relative;

  &::before {
    content: ''; position: absolute; top: 0; left: 0; right: 0; height: 1px;
    background: linear-gradient(90deg, transparent, rgba(59, 130, 246, 0.3), transparent);
  }

  .time-text { font-size: 14px; font-weight: 600; color: $police-blue; font-family: 'SF Mono', ui-monospace, monospace; }
}

.icon-group { display: flex; align-items: center; gap: 12px; }

.icon-btn {
  width: 44px; height: 44px; border-radius: 12px;
  background: linear-gradient(135deg, #F8FAFE 0%, #F1F5F9 100%);
  border: 1px solid rgba(148, 163, 184, 0.2); color: $police-text-muted;
  display: flex; align-items: center; justify-content: center;
  cursor: pointer; transition: all 0.3s ease; position: relative;

  &:hover {
    background: linear-gradient(135deg, #EFF6FF 0%, #DBEAFE 100%);
    border-color: rgba(59, 130, 246, 0.3); color: $police-blue;
    transform: translateY(-2px); box-shadow: 0 4px 12px rgba(37, 99, 235, 0.15);
  }
}

.msg-badge {
  position: absolute; top: 4px; right: 4px;
  background: #EF4444; color: white; font-size: 10px; font-weight: 700;
  padding: 1px 5px; border-radius: 8px; min-width: 16px; text-align: center;
  line-height: 1.2;
}

.content-wrapper { flex: 1; overflow-y: auto; padding: 16px 28px; background: $police-bg; }

// ===== 标签页导航栏 =====
.tabs-nav {
  display: flex; align-items: center; justify-content: space-between;
  padding: 8px 16px; background: #FFFFFF;
  border-bottom: 1px solid $police-border; box-shadow: 0 1px 4px rgba(0, 0, 0, 0.04);
}

.tabs-container {
  display: flex; align-items: center; gap: 6px; flex: 1; overflow-x: auto;
  &::-webkit-scrollbar { height: 4px; }
  &::-webkit-scrollbar-thumb { background: #CBD5E1; border-radius: 2px; }
}

.tab-item {
  display: flex; align-items: center; gap: 6px; padding: 6px 12px;
  background: #F5F5F5; border-radius: 8px; font-size: 13px; font-weight: 500;
  color: $police-text-muted; cursor: pointer; transition: all 0.2s;
  white-space: nowrap; border: 1px solid transparent;

  &:hover { background: #EFF6FF; color: $police-text; }
  &.active { background: $police-blue; color: white; border-color: $police-blue; }

  .tab-icon { flex-shrink: 0; }
  .tab-label { max-width: 120px; overflow: hidden; text-overflow: ellipsis; }

  .tab-close {
    width: 16px; height: 16px; border-radius: 4px;
    display: flex; align-items: center; justify-content: center;
    opacity: 0.6; transition: all 0.2s; background: transparent;
    border: none; cursor: pointer;
    &:hover { opacity: 1; background: rgba(255, 255, 255, 0.2); }
  }
  &.active .tab-close:hover { background: rgba(255, 255, 255, 0.3); }
}

.tabs-actions { display: flex; align-items: center; margin-left: 12px; }

.tabs-action-btn {
  width: 28px; height: 28px; border-radius: 8px; background: #F5F5F5;
  border: 1px solid $police-border; color: $police-text-muted;
  display: flex; align-items: center; justify-content: center;
  cursor: pointer; transition: all 0.2s;

  &:hover { background: $police-blue; color: white; border-color: $police-blue; }
}

.mr-2 { margin-right: 8px; }
</style>
