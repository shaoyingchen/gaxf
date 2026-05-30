# State Management

> How state is managed in this project.

---

## Overview

Pinia 3.0 with `defineStore`. No Vuex. Stores are in `src/store/modules/`.

---

## Store Modules

| Store | File | Purpose |
|-------|------|---------|
| `user` | `user.ts` | Token, user info, roles, permissions, login/logout |
| `permission` | `permission.ts` | Dynamic routes, sidebar menus, route generation |
| `app` | `app.ts` | Sidebar collapse, device type, theme |
| `settings` | `settings.ts` | UI settings (theme, tagsView, layout) |
| `dict` | `dict.ts` | Dictionary data cache |
| `tagsView` | `tagsView.ts` | Open/cached tab views |
| `lock` | `lock.ts` | Screen lock state |

---

## Store Pattern

```ts
interface UserState {
  token: string | undefined
  id: string | number
  name: string
  roles: string[]
  permissions: string[]
}

const useUserStore = defineStore('user', {
  state: (): UserState => ({
    token: getToken(),
    id: '',
    name: '',
    roles: [],
    permissions: []
  }),
  actions: {
    async login(userInfo: LoginForm) {
      const res = await login(userInfo.username, userInfo.password, ...)
      setToken(res.token)
      this.token = res.token
    },
    async getInfo() {
      const res = await getInfo()
      this.roles = res.roles
      this.permissions = res.permissions
    },
    async logOut() {
      await logout()
      this.token = ''
      this.roles = []
      this.permissions = []
      removeToken()
    }
  }
})
```

---

## State Categories

### Local State (Component `ref`/`reactive`)

Use for: form data, table data, loading flags, pagination, dialog visibility.

```ts
const loading = ref(false)
const tableData = ref<DcCamera[]>([])
const open = ref(false)
```

### Global State (Pinia Store)

Use for: auth (token, user, roles, permissions), UI config (theme, sidebar), cached data (dict).

Criteria for promoting to global:
- Multiple unrelated components need the data
- Data persists across route changes
- Data affects app-wide behavior (auth, permissions)

### Server State (API Response)

No server-state cache layer. Each view fetches fresh data on mount. Dict data is the only cached server state (via `dict` store).

---

## Permission Flow

```
Login → setToken → getInfo() → store roles/permissions
  → generateRoutes() → dynamic router.addRoute()
  → v-hasPermi directive reads permissions from store
```

---

## Common Mistakes

### Don't: Store component-local state in Pinia

```ts
// Bad — dialog state is only used by one component
const useDialogStore = defineStore('dialog', {
  state: () => ({ open: false })
})

// Good — use local ref
const open = ref(false)
```

### Don't: Access store state before initialization

The `user` store depends on `getInfo()` being called first. The permission guard ensures this, but don't access `useUserStore().roles` in component setup before the guard completes.

### Don't: Forget to clear state on logout

```ts
// Good — user store clears everything
async logOut() {
  await logout()
  this.token = ''
  this.roles = []
  this.permissions = []
  removeToken()
}
```
