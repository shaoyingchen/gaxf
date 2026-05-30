# Quality Guidelines

> Code quality standards for backend development.

---

## Overview

The project follows RuoYi framework conventions. No explicit linting rules are configured — code quality relies on convention adherence and code review.

---

## Forbidden Patterns

### Don't: Return AjaxResult from Service layer

```java
// Bad — service returns presentation object
public AjaxResult save(Xxx xxx) {
    return AjaxResult.error("名称已存在");
}

// Good — throw, let controller decide
public void save(Xxx xxx) {
    throw new ServiceException("名称已存在");
}
```

### Don't: Use `System.out.println`

```java
// Bad
System.out.println("debug: " + value);

// Good
logger.debug("debug: {}", value);
```

### Don't: Catch and swallow exceptions

```java
// Bad — error silently lost
try { ... } catch (Exception e) { }

// Good — log + rethrow or handle
try { ... } catch (Exception e) {
    logger.error("Failed to ...", e);
    throw new ServiceException("操作失败");
}
```

### Don't: Put business logic in Controllers

```java
// Bad — logic in controller
@PostMapping
public AjaxResult add(@RequestBody DcCamera c) {
    if (c.getPointName() == null) {
        return error("名称不能为空");
    }
    return toAjax(service.insert(c));
}

// Good — logic in service, controller is thin
@PostMapping
public AjaxResult add(@RequestBody DcCamera c) {
    return toAjax(service.insertDcCamera(c));
}
```

### Don't: Use string concatenation in SQL

```java
// Bad — SQL injection risk
String sql = "SELECT * FROM dc_camera WHERE name = '" + name + "'";

// Good — use MyBatis parameterized queries
// #{param} in XML or @Param in annotations
```

### Don't: Skip `@PreAuthorize` on new endpoints

Every new endpoint must have permission control:

```java
@PreAuthorize("@ss.hasPermi('spdc:camera:list')")
@GetMapping("/list")
public TableDataInfo list(DcCamera dcCamera) { ... }
```

---

## Required Patterns

### Controller: Extend BaseController

All controllers extend `BaseController` to get `startPage()`, `getDataTable()`, `toAjax()`, `success()`, `error()`, etc.

### Permission: `@PreAuthorize` on every endpoint

```java
@PreAuthorize("@ss.hasPermi('{module}:{entity}:{action}')")
```

Permission format: `{module}:{entity}:{action}` where action is one of: `list`, `query`, `add`, `edit`, `remove`, `export`.

### Operation Log: `@Log` for mutations

```java
@Log(title = "模块名称", businessType = BusinessType.INSERT)
@Log(title = "模块名称", businessType = BusinessType.UPDATE)
@Log(title = "模块名称", businessType = BusinessType.DELETE)
@Log(title = "模块名称", businessType = BusinessType.EXPORT)
```

### Entity: Extend BaseEntity

Domain objects should extend `BaseEntity` for standard audit fields.

### REST: Follow RuoYi URL convention

| Action | HTTP Method | URL Pattern |
|--------|-------------|-------------|
| List | GET | `/{module}/{entity}/list` |
| Detail | GET | `/{module}/{entity}/{id}` |
| Add | POST | `/{module}/{entity}` |
| Update | PUT | `/{module}/{entity}` |
| Delete | DELETE | `/{module}/{entity}/{ids}` |
| Export | POST | `/{module}/{entity}/export` |

---

## Testing Requirements

No automated test framework is currently configured. When adding tests:

- Unit tests go in `src/test/java` mirroring the source structure
- Test service logic, not controller HTTP dispatch
- Mock mapper dependencies in service tests
- Integration tests should use test profile (`application-test.yml`)

---

## Code Review Checklist

- [ ] Controller extends `BaseController`
- [ ] Every endpoint has `@PreAuthorize`
- [ ] Mutations have `@Log` annotation
- [ ] Service throws `ServiceException` (not returns AjaxResult)
- [ ] Domain extends `BaseEntity` where applicable
- [ ] Pagination: `startPage()` before query, `getDataTable()` after
- [ ] No SQL injection risk (parameterized queries only)
- [ ] No sensitive data in logs
- [ ] Data scope filtering applied for dept-owned data
- [ ] `del_flag` based soft delete (not physical DELETE)
