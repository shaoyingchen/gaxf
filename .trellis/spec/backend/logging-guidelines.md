# Logging Guidelines

> How logging is done in this project.

---

## Overview

- **Framework**: SLF4J + Logback (Spring Boot default)
- **Base logging**: `protected final Logger logger = LoggerFactory.getLogger(this.getClass())` in `BaseController`
- **Config**: `application.yml` → `logging.level.com.gaxf: debug`, `org.springframework: warn`

---

## Log Levels

| Level | When to Use | Example |
|-------|-------------|---------|
| `debug` | Development tracing, SQL timing, request details | `logger.debug("Processing camera pointId={}", pointId)` |
| `info` | Significant business events (startup, config load) | `logger.info("Application started")` |
| `warn` | Recoverable issues, deprecation warnings | `logger.warn("Deprecated API called: {}", uri)` |
| `error` | Failures requiring attention, exception stack traces | `logger.error("请求地址'{}',权限校验失败'{}'", uri, e.getMessage())` |

---

## Structured Logging

The project uses parameterized SLF4J logging (not String concatenation):

```java
// Good — parameterized
logger.error("请求地址'{}',发生系统异常.", requestURI, e);

// Bad — string concatenation
logger.error("请求地址'" + requestURI + "',发生系统异常.", e);
```

### GlobalExceptionHandler Log Pattern

Every exception handler logs the request URI + exception:

```java
log.error("请求地址'{}',权限校验失败'{}'", requestURI, e.getMessage());
log.error("请求地址'{}',发生未知异常.", requestURI, e);
```

### Operation Log (via @Log annotation)

Business operations are recorded via `@Log` annotation → `LogAspect` → `AsyncFactory`:

```java
@Log(title = "点位管理", businessType = BusinessType.INSERT)
@PostMapping
public AjaxResult add(@RequestBody DcCamera dcCamera) { ... }
```

This writes to `sys_oper_log` table asynchronously via `AsyncManager`.

---

## What to Log

- All controller exception paths (auto via `GlobalExceptionHandler`)
- Business operations with `@Log` annotation (auto via AOP)
- Authentication events (login success/failure, token expiry)
- Data scope filter decisions (which dept access level)
- Dynamic datasource switches

---

## What NOT to Log

- **Passwords** — never log raw or encrypted passwords
- **JWT tokens** — don't log full Authorization headers
- **PII** — ID numbers, phone numbers (use `@Sensitive` annotation for masking)
- **Large payloads** — request/response bodies for file upload/download
- **Health check requests** — they flood the logs

---

## Async Logging

`AsyncManager` + `AsyncFactory` handle async log writes. Use for:

- Operation log records
- Login info records

This avoids blocking the request thread for DB writes.
