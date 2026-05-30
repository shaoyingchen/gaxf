# Database Guidelines

> Database patterns and conventions for this project.

---

## Overview

- **ORM**: MyBatis (mybatis-spring-boot-starter 4.0.1)
- **Connection Pool**: Alibaba Druid 1.2.28 (with dynamic datasource support)
- **Database**: MySQL (PageHelper dialect = mysql)
- **Cache**: Redis (Lettuce client, via `RedisCache` wrapper)
- **Pagination**: PageHelper 2.1.1

---

## Query Patterns

### Standard CRUD Flow

Every entity follows the same MyBatis pattern:

```java
// Mapper interface — plain MyBatis, no generic BaseMapper
public interface DcCameraMapper {
    List<DcCamera> selectDcCameraList(DcCamera dcCamera);
    DcCamera selectDcCameraByPointId(Integer pointId);
    int insertDcCamera(DcCamera dcCamera);
    int updateDcCamera(DcCamera dcCamera);
    int deleteDcCameraByPointIds(Integer[] pointIds);
}
```

### Pagination in Controllers

```java
@GetMapping("/list")
public TableDataInfo list(DcCamera dcCamera) {
    startPage();  // Sets PageHelper thread-local from request params
    List<DcCamera> list = dcCameraService.selectDcCameraList(dcCamera);
    return getDataTable(list);  // Wraps list + total into TableDataInfo
}
```

**Convention**: Always call `startPage()` **before** the query, then `getDataTable()` after. PageHelper uses ThreadLocal — calling `startPage()` after the query returns unpaginated results.

### Data Scope (Row-Level Security)

Use `@DataScope` annotation for department-based data filtering:

```java
@DataScope(deptAlias = "d")  // Adds WHERE clause based on user's dept
public List<DcCamera> selectDcCameraList(DcCamera dcCamera) { ... }
```

For manual dept filtering in controllers:

```java
Long userDeptId = null;
if (!SecurityUtils.isAdmin()) {
    userDeptId = SecurityUtils.getDeptId();
}
List<DcCamera> list = service.selectDcCameraListPublic(dcCamera, userDeptId);
```

### Dynamic DataSource

Switch database with `@DataSource` annotation:

```java
@DataSource(DataSourceType.SLAVE)
public List<Xxx> readFromSlave() { ... }
```

---

## MyBatis Configuration

From `application.yml`:

```yaml
mybatis:
  typeAliasesPackage: com.gaxf.**.domain
  mapperLocations: classpath*:mapper/**/*Mapper.xml
  configLocation: classpath:mybatis/mybatis-config.xml
```

### Mapper XML Location

Mapper XMLs are in each module's `src/main/resources/mapper/{module}/`. The glob `classpath*:mapper/**/*Mapper.xml` picks them all up.

### Column Mapping Convention

MyBatis XML uses `resultMap` with explicit column-to-property mapping:

```xml
<resultMap type="DcCamera" id="DcCameraResult">
    <result property="pointId"    column="point_id" />
    <result property="pointName"  column="point_name" />
    <result property="delFlag"    column="del_flag" />
</resultMap>
```

---

## Naming Conventions

| Item | Convention | Example |
|------|-----------|---------|
| Table name | snake_case, prefixed by module | `dc_camera`, `dc_dict` |
| Column name | snake_case | `point_id`, `point_name`, `dept_id` |
| Primary key | `{entity}_id` or `id` | `point_id` |
| Foreign key | `{referenced_entity}_id` | `dept_id`, `zone_type_id` |
| Soft delete column | `del_flag` | `del_flag` (0=normal, 1=deleted) |
| Status column | `{entity}_status` or `enable` | `point_status`, `enable` |
| Timestamps | `create_time`, `update_time` | via `BaseEntity` |
| Creator/Updater | `create_by`, `update_by` | via `BaseEntity` |

---

## BaseEntity Convention

Most domain objects extend `BaseEntity` which provides:

- `searchValue` — search parameter passthrough
- `createBy` / `createTime` — auto-filled on insert
- `updateBy` / `updateTime` — auto-filled on update
- `remark` — optional note
- `params` — flexible key-value map for query conditions

---

## Redis Cache Patterns

Use `RedisCache` wrapper (not direct RedisTemplate):

```java
@Autowired
private RedisCache redisCache;

// Set
redisCache.setCacheObject("key", value);
redisCache.setCacheObject("key", value, 30, TimeUnit.MINUTES);

// Get
Object val = redisCache.getCacheObject("key");

// Delete
redisCache.deleteObject("key");
```

Cache key constants are in `CacheConstants`.

---

## Common Mistakes

### Don't: Forget `startPage()` before list queries

```java
// Bad — returns ALL rows, no pagination
List<DcCamera> list = service.selectList(query);
startPage();  // too late!
return getDataTable(list);

// Good — startPage FIRST, then query
startPage();
List<DcCamera> list = service.selectList(query);
return getDataTable(list);
```

### Don't: Skip data scope filtering

For business tables with department ownership, always apply data scope. Admin sees all; non-admin sees own dept + children.

### Don't: Use physical delete on soft-delete tables

Tables with `del_flag` should use logical delete:

```xml
<!-- Good -->
UPDATE dc_camera SET del_flag = '1' WHERE point_id = #{pointId}

<!-- Bad -->
DELETE FROM dc_camera WHERE point_id = #{pointId}
```
