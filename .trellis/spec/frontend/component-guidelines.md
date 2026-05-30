# Component Guidelines

> How components are built in this project.

---

## Overview

Vue 3.5 Composition API with `<script setup lang="ts">`. UI framework: Element Plus 2.13. Styling: SCSS + Tailwind CSS 3.4.

---

## Component Structure

### Standard Single-File Component

```vue
<script setup lang="ts" name="CameraManage">
import { ref, reactive } from 'vue'
import { listCamera, addCamera, updateCamera, delCamera } from '@/api/spdc/camera'
import type { DcCamera, CameraQueryParams } from '@/types'

const queryForm = ref<CameraQueryParams>({})
const tableData = ref<DcCamera[]>([])
const loading = ref(false)

function handleQuery() {
  loading.value = true
  listCamera(queryForm.value).then(res => {
    tableData.value = res.rows
    total.value = res.total
  }).finally(() => {
    loading.value = false
  })
}
</script>

<template>
  <div class="app-container">
    <!-- Element Plus components -->
  </div>
</template>

<style scoped>
/* Scoped styles only */
</style>
```

### Key Conventions

- Always use `<script setup lang="ts">` — no Options API
- Use `name` attribute via `unplugin-vue-setup-extend-plus` for keep-alive: `<script setup lang="ts" name="CameraManage">`
- Use `<style scoped>` to prevent style leakage
- Import types with `import type` (type-only imports)

---

## Props Conventions

Define props with `defineProps<T>()` using TypeScript interface:

```vue
<script setup lang="ts">
interface Props {
  modelValue: boolean
  data?: DcCamera
  readonly?: boolean
}

const props = defineProps<Props>()
const emit = defineEmits<{
  'update:modelValue': [value: boolean]
  'success': []
}>()
</script>
```

---

## Styling Patterns

- **Element Plus** for all UI components (tables, forms, dialogs, messages)
- **Tailwind CSS** for utility classes (spacing, layout, responsive)
- **SCSS** for complex component styles via `<style lang="scss" scoped>`
- Global CSS variables defined in Element Plus theme

```vue
<template>
  <!-- Tailwind for layout, Element Plus for components -->
  <div class="flex items-center gap-4 mb-4">
    <el-input v-model="query" placeholder="搜索" />
    <el-button type="primary" @click="handleQuery">搜索</el-button>
  </div>
</template>
```

---

## Shared Components

Reusable components live in `src/components/`. Key ones:

| Component | Purpose |
|-----------|---------|
| `Pagination` | Table pagination (wraps el-pagination) |
| `RightToolbar` | Table toolbar (columns, refresh) |
| `DictTag` | Display dict values with colored tags |
| `FileUpload` | File upload with preview |
| `ImageUpload` | Image upload with crop |
| `Editor` | Rich text editor (Quill) |
| `SvgIcon` | SVG icon component |
| `Breadcrumb` | Navigation breadcrumb |

---

## Permission Directives

Use `v-hasPermi` and `v-hasRole` to conditionally render elements:

```vue
<el-button v-hasPermi="['spdc:camera:add']" @click="handleAdd">新增</el-button>
<el-button v-hasPermi="['spdc:camera:edit']" @click="handleEdit">修改</el-button>
<el-button v-hasPermi="['spdc:camera:remove']" @click="handleDelete">删除</el-button>
```

For programmatic checks, use the `auth` plugin:

```ts
import auth from '@/plugins/auth'
if (auth.hasPermi('spdc:camera:add')) { ... }
```

---

## Common Mistakes

### Don't: Use Options API

```vue
<!-- Bad -->
<script>
export default {
  data() { return { ... } },
  methods: { ... }
}
</script>

<!-- Good -->
<script setup lang="ts">
const data = ref(...)
</script>
```

### Don't: Use global styles without scoped

```vue
<!-- Bad — leaks to all components -->
<style>
.my-class { ... }
</style>

<!-- Good -->
<style scoped>
.my-class { ... }
</style>
```

### Don't: Hard-code permission strings

Always use the `{module}:{entity}:{action}` format matching the backend `@PreAuthorize` values.
