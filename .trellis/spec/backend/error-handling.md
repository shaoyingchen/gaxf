# Error Handling

> How errors are handled in this project.

---

## Overview

Errors flow through three layers: service layer throws typed exceptions → `GlobalExceptionHandler` catches them → unified `AjaxResult` JSON response returned to the client.

---

## Error Types

| Exception | Package | When to Use |
|-----------|---------|-------------|
| `ServiceException` | `com.gaxf.common.exception` | Business logic errors (most common) |
| `GlobalException` | `com.gaxf.common.exception` | Generic system errors |
| `DemoModeException` | `com.gaxf.common.exception` | Demo mode restrictions |
| `UserException` subclasses | `com.gaxf.common.exception.user` | Auth-related errors (login, captcha, password) |
| `FileException` subclasses | `com.gaxf.common.exception.file` | File upload/download errors |
| `TaskException` | `com.gaxf.common.exception.job` | Scheduled task errors |

### ServiceException (Primary Business Exception)

```java
// With message only → code defaults to 500
throw new ServiceException("点位名称已存在");

// With custom code + message
throw new ServiceException("密码错误次数超限", HttpStatus.FORBIDDEN);
```

Fields: `code` (Integer), `message` (String), `detailMessage` (String, for internal debug).

---

## Error Handling Patterns

### Service Layer: Throw, Don't Catch

```java
// Good — throw in service, let GlobalExceptionHandler handle
public void insertDcCamera(DcCamera camera) {
    if (mapper.checkNameExists(camera.getPointName())) {
        throw new ServiceException("点位名称已存在");
    }
    mapper.insert(camera);
}

// Bad — catching and returning null silently
public void insertDcCamera(DcCamera camera) {
    try {
        mapper.insert(camera);
    } catch (Exception e) {
        return; // caller has no idea it failed
    }
}
```

### Controller Layer: Use BaseController helpers

```java
// Good — toAjax converts rows-affected to AjaxResult
return toAjax(dcCameraService.insertDcCamera(dcCamera));

// Good — explicit success/error
return success(data);
return error("操作失败");
return warn("请注意");
```

### GlobalExceptionHandler Mappings

| Exception | Response Code | Example Message |
|-----------|--------------|-----------------|
| `AccessDeniedException` | 403 | "没有权限，请联系管理员授权" |
| `ServiceException` | `code` field or 500 | From exception message |
| `BindException` | 500 | First field error message |
| `MethodArgumentNotValidException` | 500 | First field error message |
| `HttpRequestMethodNotSupportedException` | 500 | HTTP method error |
| `RuntimeException` | 500 | `e.getMessage()` |
| `Exception` | 500 | `e.getMessage()` |
| `DemoModeException` | 500 | "演示模式，不允许操作" |

---

## API Error Responses

All API responses use `AjaxResult` (extends `HashMap<String, Object>`):

### Response Contract

```json
{
  "code": 500,
  "msg": "点位名称已存在"
}
```

```json
{
  "code": 200,
  "msg": "操作成功",
  "data": { ... }
}
```

### Standard Codes (defined in `HttpStatus`)

| Code | Constant | Meaning |
|------|----------|---------|
| 200 | SUCCESS | Operation succeeded |
| 401 | UNAUTHORIZED | Not authenticated |
| 403 | FORBIDDEN | No permission |
| 500 | ERROR | System/business error |
| 601 | WARN | Warning (soft error) |

### Paginated Response (`TableDataInfo`)

```json
{
  "code": 200,
  "msg": "查询成功",
  "total": 150,
  "rows": [ ... ]
}
```

---

## Frontend Error Handling (Mirror)

The frontend `request.ts` response interceptor maps the same codes:

| Code | Frontend Behavior |
|------|------------------|
| 200 | Resolve with `res.data` |
| 401 | Show re-login dialog, redirect |
| 500 | `ElMessage.error(msg)` |
| 601 | `ElMessage.warning(msg)` |
| Other | `ElNotification.error(msg)` |

---

## Common Mistakes

### Don't: Swallow exceptions in service layer

```java
// Bad — error is silently lost
try {
    doSomething();
} catch (Exception e) {
    log.error("error", e);
    // no throw, no return — caller thinks it succeeded
}
```

### Don't: Return AjaxResult.error() from service layer

```java
// Bad — service should throw, not return error responses
public AjaxResult doSomething() {
    return AjaxResult.error("失败");
}

// Good — throw ServiceException, controller converts to AjaxResult
public void doSomething() {
    throw new ServiceException("失败");
}
```

### Don't: Use raw Exception for business logic

```java
// Bad — untyped, can't be distinguished from system errors
throw new RuntimeException("用户不存在");

// Good — use ServiceException
throw new ServiceException("用户不存在");
```
