# Quality Guidelines

> Code quality standards for frontend development.

---

## Overview

TypeScript strict mode, Vite build, no ESLint/Prettier config detected. Code quality relies on convention and review.

---

## Forbidden Patterns

### Don't: Use `any` type

```ts
// Bad
const data: any = res.data
function process(item: any) { ... }

// Good
const data: DcCamera = res.data
function process(item: DcCamera) { ... }
```

### Don't: Import from `vue` or `pinia` (use auto-import)

```ts
// Bad — auto-import handles these
import { ref, computed, onMounted } from 'vue'
import { defineStore } from 'pinia'

// Good — just use directly
const count = ref(0)
```

### Don't: Use Options API

```vue
<!-- Bad -->
<script>
export default { data() { ... }, methods: { ... } }
</script>

<!-- Good -->
<script setup lang="ts">
</script>
```

### Don't: Directly manipulate DOM

```ts
// Bad
document.getElementById('form').reset()

// Good — use template refs
const formRef = ref<FormInstance>()
formRef.value?.resetFields()
```

### Don't: Use `console.log` in production code

```ts
// Bad
console.log('debug:', data)

// Good — remove before commit, or use conditional logging
if (import.meta.env.DEV) {
  console.log('debug:', data)
}
```

---

## Required Patterns

### API Module Pattern

Every API call must:
1. Import `request` from `@/utils/request`
2. Specify return type from `@/types`
3. Follow RESTful verb naming

```ts
import request from '@/utils/request'
import type { AjaxResult, TableDataInfo, DcCamera } from '@/types'

export function listCamera(query: CameraQueryParams): Promise<TableDataInfo<DcCamera>> {
  return request({ url: '/spdc/camera/list', method: 'get', params: query })
}
```

### Type Definition Pattern

Every API module should have matching type definitions:

```
src/types/api/{module}/{entity}.ts
```

With query params and entity interfaces:

```ts
export interface CameraQueryParams extends PageDomain {
  pointName?: string
  deptId?: number
  // ...
}

export interface DcCamera extends BaseEntity {
  pointId?: number
  pointName?: string
  // ...
}
```

### Permission Check Pattern

Mutations (add/edit/delete buttons) must use `v-hasPermi`:

```vue
<el-button v-hasPermi="['spdc:camera:add']" type="primary">新增</el-button>
```

Permission string must match backend `@PreAuthorize` exactly: `{module}:{entity}:{action}`.

---

## Testing Requirements

No test framework configured. When adding:

- Vitest for unit tests (aligns with Vite)
- @vue/test-utils for component tests
- Test critical business logic and permission flows first

---

## Code Review Checklist

- [ ] Uses `<script setup lang="ts">` (no Options API)
- [ ] API calls have proper TypeScript return types
- [ ] Type definitions in `types/api/{module}/` match API module
- [ ] Permission buttons use `v-hasPermi`
- [ ] Permission strings match backend `@PreAuthorize`
- [ ] No `any` types (use proper interfaces)
- [ ] Scoped styles (`<style scoped>`)
- [ ] No `console.log` left in code
- [ ] Loading state for all API calls
- [ ] Error handling via `proxy.$modal.msgError` or `ElMessage`
