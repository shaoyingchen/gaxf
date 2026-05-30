# Hook Guidelines

> How hooks are used in this project.

---

## Overview

Vue 3.5 Composition API with auto-import via `unplugin-auto-import`. VueUse 14.1 for utility composables.

---

## Auto-Import

`ref`, `reactive`, `computed`, `watch`, `onMounted`, `defineStore`, etc. are auto-imported — no need to import from `vue` or `pinia`:

```vue
<script setup lang="ts">
// No import needed — auto-imported
const count = ref(0)
const doubled = computed(() => count.value * 2)
onMounted(() => { ... })
</script>
```

---

## Data Fetching

No dedicated data-fetching library (no React Query / SWR equivalent). Pattern is direct API call in component:

### Standard List Fetch Pattern

```ts
const loading = ref(false)
const tableData = ref<DcCamera[]>([])
const total = ref(0)
const queryParams = ref<CameraQueryParams>({})

function getList() {
  loading.value = true
  listCamera(queryParams.value).then(res => {
    tableData.value = res.rows
    total.value = res.total
  }).finally(() => {
    loading.value = false
  })
}
```

### CRUD Pattern

```ts
function handleAdd() {
  addCamera(form.value).then(() => {
    proxy.$modal.msgSuccess('新增成功')
    getList()
  })
}

function handleUpdate() {
  updateCamera(form.value).then(() => {
    proxy.$modal.msgSuccess('修改成功')
    getList()
  })
}

function handleDelete(ids: number[]) {
  proxy.$modal.confirm('确认删除?').then(() => {
    delCamera(ids).then(() => {
      proxy.$modal.msgSuccess('删除成功')
      getList()
    })
  })
}
```

---

## VueUse Composables

`@vueuse/core` is available. Commonly used:

- `useClipboard` — clipboard operations
- `useFullscreen` — fullscreen toggle
- `useResizeObserver` — responsive layouts

---

## Custom Hooks

The project doesn't currently define custom composables in a dedicated `composables/` directory. Reusable logic lives in:

- `utils/` — stateless utility functions
- `plugins/` — app-level plugin modules (auth, cache, modal)
- `store/modules/` — Pinia stores for global state

### When to Extract a Composable

If multiple views share the same data-fetching + state logic:

```
src/composables/
└── useCameraList.ts
```

```ts
export function useCameraList() {
  const loading = ref(false)
  const tableData = ref<DcCamera[]>([])
  const total = ref(0)
  const queryParams = ref<CameraQueryParams>({})

  function getList() { ... }

  return { loading, tableData, total, queryParams, getList }
}
```

---

## Naming Conventions

| Item | Convention | Example |
|------|-----------|---------|
| Composable file | `use{Name}.ts` | `useCameraList.ts` |
| Composable function | `use{Name}` | `useCameraList()` |
| Ref variable | camelCase | `loading`, `tableData` |
| API call function | verb + Entity | `listCamera`, `getCamera` |

---

## Common Mistakes

### Don't: Destructure reactive refs

```ts
// Bad — loses reactivity
const { data } = useSomething()

// Good — keep the ref
const result = useSomething()
// Use result.data.value
```

### Don't: Forget .value in script

```ts
// Bad
if (loading) { ... }

// Good
if (loading.value) { ... }
```
