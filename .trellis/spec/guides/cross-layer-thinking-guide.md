# Cross-Layer Thinking Guide

> **Purpose**: Think through data flow across layers before implementing.

---

## The Problem

**Most bugs happen at layer boundaries**, not within layers.

Common cross-layer bugs:
- API returns format A, frontend expects format B
- Database stores X, service transforms to Y, but loses data
- Multiple layers implement the same logic differently

---

## Before Implementing Cross-Layer Features

### Step 1: Map the Data Flow

Draw out how data moves:

```
Source → Transform → Store → Retrieve → Transform → Display
```

For each arrow, ask:
- What format is the data in?
- What could go wrong?
- Who is responsible for validation?

### Step 2: Identify Boundaries

| Boundary | Common Issues |
|----------|---------------|
| API ↔ Service | Type mismatches, missing fields |
| Service ↔ Database | Format conversions, null handling |
| Backend ↔ Frontend | Serialization, date formats |
| Component ↔ Component | Props shape changes |

### Step 3: Define Contracts

For each boundary:
- What is the exact input format?
- What is the exact output format?
- What errors can occur?

---

## Common Cross-Layer Mistakes

### Mistake 1: Implicit Format Assumptions

**Bad**: Assuming date format without checking

**Good**: Explicit format conversion at boundaries

### Mistake 2: Scattered Validation

**Bad**: Validating the same thing in multiple layers

**Good**: Validate once at the entry point

### Mistake 3: Leaky Abstractions

**Bad**: Component knows about database schema

**Good**: Each layer only knows its neighbors

---

## Checklist for Cross-Layer Features

Before implementation:
- [ ] Mapped the complete data flow
- [ ] Identified all layer boundaries
- [ ] Defined format at each boundary
- [ ] Decided where validation happens

After implementation:
- [ ] Tested with edge cases (null, empty, invalid)
- [ ] Verified error handling at each boundary
- [ ] Checked data survives round-trip

---

## Project-Specific Cross-Layer Contracts (gaxf)

### Backend → Frontend Response Contract

| Backend Java Type | Frontend TypeScript Type | Key Fields |
|-------------------|--------------------------|------------|
| `AjaxResult` | `AjaxResult<T>` | `code`, `msg`, `data` |
| `TableDataInfo` | `TableDataInfo<T>` | `code`, `msg`, `total`, `rows` |
| `BaseEntity` | `BaseEntity` | `createBy`, `createTime`, `updateBy`, `updateTime`, `remark` |
| `HttpStatus.SUCCESS (200)` | code 200 → resolve | |
| `HttpStatus.ERROR (500)` | code 500 → `ElMessage.error` | |
| `HttpStatus.WARN (601)` | code 601 → `ElMessage.warning` | |
| `HttpStatus.UNAUTHORIZED (401)` | code 401 → re-login dialog | |

### Permission String Contract

Backend `@PreAuthorize("@ss.hasPermi('spdc:camera:list')")` must exactly match frontend `v-hasPermi="['spdc:camera:list']"`.

Format: `{module}:{entity}:{action}` where action ∈ {list, query, add, edit, remove, export}.

### Date Format Contract

Backend serializes dates as `yyyy-MM-dd HH:mm:ss` (Jackson config in `application.yml`). Frontend receives ISO-style strings; display formatting uses Element Plus date components.

### Pagination Contract

Frontend sends `pageNum` + `pageSize` as query params → Backend `startPage()` reads from `PageHelper` ThreadLocal → Response includes `total` + `rows`.

---

## When to Create Flow Documentation

Create detailed flow docs when:
- Feature spans 3+ layers
- Multiple teams are involved
- Data format is complex
- Feature has caused bugs before
