// src/permission.js
import router from './router'
import { ElMessage } from 'element-plus'
import NProgress from 'nprogress'
import 'nprogress/nprogress.css'
import { getToken } from '@/utils/auth'
import { isHttp, isPathMatch } from '@/utils/validate'
import { isRelogin } from '@/utils/request'
import useUserStore from '@/store/modules/user'
import useLockStore from '@/store/modules/lock'
import useSettingsStore from '@/store/modules/settings'
import usePermissionStore from '@/store/modules/permission'

NProgress.configure({ showSpinner: false })

const whiteList = ['/login', '/register']

const isWhiteList = (path: string): boolean => {
  return whiteList.some((pattern: string) => isPathMatch(pattern, path))
}

router.beforeEach((to, from, next) => {
  NProgress.start()
  if (getToken()) {
    to.meta.title && useSettingsStore().setTitle(to.meta.title as string)
    const isLock = useLockStore().isLock
    /* 已登录状态 */
    if (to.path === '/login') {
      next({ path: '/home' })  // 已登录时访问登录页，重定向至首页
      NProgress.done()
    } else if (isWhiteList(to.path)) {
      next()
    } else if (isLock && to.path !== '/lock') {
      next({ path: '/lock' })
      NProgress.done()
    } else if (!isLock && to.path === '/lock') {
      next({ path: '/home' })
      NProgress.done()
    } else {
      const userStore = useUserStore()
      if (userStore.roles.length === 0) {
        isRelogin.show = true
        // 拉取用户信息并生成动态路由
        userStore.getInfo().then(() => {
          isRelogin.show = false
          usePermissionStore().generateRoutes().then((accessRoutes: any[]) => {
            accessRoutes.forEach((route: any) => {
              if (!isHttp(route.path)) {
                router.addRoute(route)
              }
            })
            // 处理根路径重定向到首页
            if (to.path === '/') {
              next({ path: '/home', replace: true })
            } else {
              next({ ...to, replace: true })
            }
          })
        }).catch((err: any) => {
          userStore.logOut().then(() => {
            ElMessage.error(err as string)
            next({ path: '/login' })
          })
        })
      } else {
        // 已加载用户角色，处理根路径
        if (to.path === '/') {
          next({ path: '/home', replace: true })
        } else {
          next()
        }
      }
    }
  } else {
    // 未登录状态
    if (isWhiteList(to.path)) {
      next()
    } else {
      // 记录原本要去的页面，登录后跳转回去
      next(`/login?redirect=${to.fullPath}`)
      NProgress.done()
    }
  }
})

router.afterEach(() => {
  NProgress.done()
})