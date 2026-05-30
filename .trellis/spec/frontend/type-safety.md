# Type Safety

> Type safety patterns in this project.

---

## Overview

TypeScript 5.6 with strict mode. Type definitions in `src/types/`. API types mirror the backend response structure exactly.

---

## Type Organization

```
src/types/
├── index.ts                   # Re-exports everything
├── api/
│   ├── index.ts               # Re-exports API types
│   ├── common.ts              # AjaxResult, TableDataInfo, PageDomain, BaseEntity, TreeSelect
│   ├── login.ts               # LoginInfoResult, UserInfoResult, CaptchaInfoResult, LoginForm
│   ├── menu.ts                # Menu types
│   ├── spdc/
│   │   ├── camera.ts          # DcCamera, CameraQueryParams
│   │   ├── dict.ts
│   │   ├── pga.ts
│   │   └── alert.ts
│   ├── system/
│   │   ├── user.ts            # SysUser
│   │   ├── role.ts
│   │   └── ...
│   ├── monitor/               # Cache, job, log types
│   └── tool/                  # Code gen types
├── components.d.ts            # Auto-generated component types
└── global.d.ts                # Global type augmentations
```

---

## Core Shared Types (common.ts)

These mirror the backend Java classes exactly:

### AjaxResult ↔ `com.gaxf.common.core.domain.AjaxResult`

```ts
export interface AjaxResult<T = any> {
  code: number    // 200=success, 500=error, 601=warn, 401=unauthorized
  msg: string
  data?: T
}
```

### TableDataInfo ↔ `com.gaxf.common.core.page.TableDataInfo`

```ts
export interface TableDataInfo<T = any> {
  code: number
  msg: string
  total: number
  rows: T[]
}
```

### PageDomain ↔ `com.gaxf.common.core.page.PageDomain`

```ts
export interface PageDomain {
  pageNum?: number
  pageSize?: number
  orderByColumn?: string
  isAsc?: string
  reasonable?: boolean
}
```

### BaseEntity ↔ `com.gaxf.common.core.domain.BaseEntity`

```ts
export interface BaseEntity {
  searchValue?: string
  createBy?: string
  createTime?: string
  updateBy?: string
  updateTime?: string
  remark?: string
  params?: Record<string, any>
}
```

---

## Entity Type Pattern

Every business entity follows this pattern:

```ts
import type { PageDomain, BaseEntity } from "../common";

/** Query parameters — extends PageDomain for pagination */
export interface CameraQueryParams extends PageDomain {
  pointName?: string
  deptId?: number
  // ... filterable fields
}

/** Entity — extends BaseEntity for audit fields */
export interface DcCamera extends BaseEntity {
  pointId?: number       // Primary key
  pointName?: string
  deptId?: number
  delFlag?: string       // Soft delete
  enable?: number        // Status
  // ... business fields
}
```

---

## Validation

No runtime validation library (Zod, Yup, etc.). Validation is handled by:

1. **Element Plus form rules** — for UI form validation
2. **Backend validation** — `@Valid` + `BindException` on server
3. **TypeScript** — compile-time type checking

---

## Common Patterns

### Generic API Response Typing

```ts
// Typed list query
export function listCamera(query: CameraQueryParams): Promise<TableDataInfo<DcCamera>> {
  return request({ url: '/spdc/camera/list', method: 'get', params: query })
}

// Typed detail query
export function getCamera(id: number): Promise<AjaxResult<DcCamera>> {
  return request({ url: '/spdc/camera/' + id, method: 'get' })
}

// Untyped mutation (no data returned)
export function addCamera(data: DcCamera): Promise<AjaxResult> {
  return request({ url: '/spdc/camera', method: 'post', data })
}
```

### Backend-Frontend Type Correspondence

| Backend Java | Frontend TypeScript |
|-------------|-------------------|
| `AjaxResult` | `AjaxResult<T>` |
| `TableDataInfo` | `TableDataInfo<T>` |
| `PageDomain` | `PageDomain` |
| `BaseEntity` | `BaseEntity` |
| `TreeSelect` | `TreeSelect` |
| `DcCamera` (domain) | `DcCamera` (interface) |

---

## Forbidden Patterns

### Don't: Use `any` when a proper type exists

```ts
// Bad
const res: any = await listCamera(query)

// Good
const res: TableDataInfo<DcCamera> = await listCamera(query)
```

### Don't: Duplicate types across files

If a type is used by multiple API modules, put it in `common.ts`. Module-specific types go in their own file.

### Don't: Define types inline in API files

```ts
// Bad — type defined in API file
export function getCamera(id: number): Promise<{ code: number; data: DcCamera }> { ... }

// Good — type defined in types/api/spdc/camera.ts, imported in API file
export function getCamera(id: number): Promise<AjaxResult<DcCamera>> { ... }
```

### Don't: Forget optional `?` on entity fields

Backend Java entities have nullable fields. Frontend types must reflect this:

```ts
// Good — all fields optional (matching backend behavior)
export interface DcCamera extends BaseEntity {
  pointId?: number
  pointName?: string
}
```
