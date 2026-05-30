# Directory Structure

> How backend code is organized in this project.

---

## Overview

The backend is a Spring Boot 4.0.3 multi-module Maven project based on the RuoYi v3.9.1 framework. Java 17, package root `com.gaxf`.

---

## Directory Layout

```
gaxf-server/
├── pom.xml                    # Parent POM with dependency management
├── gaxf-admin/                # Web layer (controllers + application entry)
│   ├── src/main/java/com/gaxf/
│   │   ├── RuoYiApplication.java
│   │   └── web/controller/
│   │       ├── common/        # Captcha, file upload
│   │       ├── monitor/       # Cache, server, login log, online users
│   │       ├── system/        # User, role, menu, dept, dict, config, notice
│   │       └── tool/          # Code gen test endpoint
│   └── src/main/resources/
│       ├── application.yml
│       ├── application-druid.yml
│       └── mybatis/mybatis-config.xml
├── gaxf-common/               # Shared utilities and domain objects
│   └── src/main/java/com/gaxf/common/
│       ├── annotation/         # @Log, @DataScope, @DataSource, @Excel, @RateLimiter, @RepeatSubmit, @Sensitive, @Anonymous
│       ├── config/             # RuoYiConfig
│       ├── constant/           # HttpStatus, Constants, CacheConstants, UserConstants
│       ├── core/
│       │   ├── controller/     # BaseController
│       │   ├── domain/         # AjaxResult, R, BaseEntity, TreeEntity, TreeSelect
│       │   ├── page/           # PageDomain, TableDataInfo, TableSupport
│       │   └── redis/          # RedisCache
│       ├── enums/              # BusinessType, BusinessStatus, DataSourceType
│       ├── exception/          # ServiceException, GlobalException, file/user/job exceptions
│       ├── filter/             # XssFilter, XssHttpServletRequestWrapper
│       ├── utils/              # StringUtils, DateUtils, SecurityUtils, DictUtils, ExcelUtil, etc.
│       └── xss/                # XssValidator
├── gaxf-framework/            # Infrastructure layer (Spring config, security, AOP)
│   └── src/main/java/com/gaxf/framework/
│       ├── aspectj/            # DataScopeAspect, DataSourceAspect, LogAspect, RateLimiterAspect
│       ├── config/             # SecurityConfig, DruidConfig, RedisConfig, MyBatisConfig, etc.
│       ├── datasource/         # DynamicDataSource, DynamicDataSourceContextHolder
│       ├── interceptor/        # RepeatSubmitInterceptor, SameUrlDataInterceptor
│       ├── manager/            # AsyncManager, AsyncFactory, ShutdownManager
│       ├── security/           # JwtAuthenticationTokenFilter, AuthenticationEntryPointImpl
│       └── web/exception/      # GlobalExceptionHandler
├── gaxf-system/               # Business module (spdc = 视频督查)
│   └── src/main/java/com/gaxf/spdc/
│       ├── controller/         # DcCameraController, DcDictController, DcDxjgController, DcPgaController
│       ├── domain/             # DcCamera, DcDict, DcDxjg, DcPga
│       ├── mapper/             # DcCameraMapper, DcDictMapper, ...
│       └── service/            # IDcCameraService + impl/
│   └── src/main/resources/mapper/spdc/  # MyBatis XML mappers
├── gaxf-quartz/               # Scheduled tasks
├── sql/                       # Database init scripts
├── logs/                      # Runtime log output
└── bin/                       # Startup scripts (ry.sh, ry.bat)
```

---

## Module Organization

New business modules should follow the `gaxf-system` pattern:

```
gaxf-system/src/main/java/com/gaxf/{module}/
├── controller/     XxxController.java
├── domain/         Xxx.java
├── mapper/         XxxMapper.java
└── service/
    ├── IXxxService.java
    └── impl/       XxxServiceImpl.java
```

With corresponding mapper XML in `src/main/resources/mapper/{module}/`.

---

## Naming Conventions

| Item | Convention | Example |
|------|-----------|---------|
| Module package | lowercase | `spdc` |
| Controller | `{Entity}Controller` | `DcCameraController` |
| Domain entity | PascalCase, prefixed by module abbreviation | `DcCamera` |
| Service interface | `I{Entity}Service` | `IDcCameraService` |
| Service impl | `{Entity}ServiceImpl` | `DcCameraServiceImpl` |
| Mapper interface | `{Entity}Mapper` | `DcCameraMapper` |
| Mapper XML | `{Entity}Mapper.xml` | `DcCameraMapper.xml` |
| REST path | `/{module}/{entity}` | `/spdc/camera` |

---

## Examples

The `spdc` module (`gaxf-system/src/main/java/com/gaxf/spdc/`) is the reference implementation for business modules.
