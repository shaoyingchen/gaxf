# Department-Based Approval Flow Implementation Plan

> **For agentic workers:** REQUIRED SUB-SKILL: Use superpowers:subagent-driven-development (recommended) or superpowers:executing-plans to implement this plan task-by-task. Steps use checkbox (`- [ ]`) syntax for tracking.

**Goal:** Add configurable department-based approval flows for work orders, including flow configuration, approval instance generation, approval actions, todo entry points, and work-order status integration.

**Architecture:** Keep approval configuration independent from work-order business tables, but implement runtime execution only for the current xf work-order module. Reuse the existing department tree, current admin/controller/service patterns, and current work-order detail/todo/review pages, while replacing fixed review-type logic with instance-driven staged parallel approval.

**Tech Stack:** Spring Boot, MyBatis XML mapper, RuoYi-style admin/system modules, Vue 3 script setup, Element Plus, TypeScript

---

## File Structure

### Backend
- Create: `gaxf-server/sql/xf_approve_flow.sql` — DDL and seed menu/permission SQL for approval configuration and runtime tables
- Create: `gaxf-server/gaxf-system/src/main/java/com/gaxf/system/domain/XfApproveFlowConfig.java` — approval flow config aggregate root
- Create: `gaxf-server/gaxf-system/src/main/java/com/gaxf/system/domain/XfApproveFlowStage.java` — approval stage definition
- Create: `gaxf-server/gaxf-system/src/main/java/com/gaxf/system/domain/XfApproveFlowStageDept.java` — stage-to-department mapping
- Create: `gaxf-server/gaxf-system/src/main/java/com/gaxf/system/domain/XfApproveInstance.java` — approval runtime instance
- Create: `gaxf-server/gaxf-system/src/main/java/com/gaxf/system/domain/XfApproveTask.java` — approval runtime task per stage/department
- Create: `gaxf-server/gaxf-system/src/main/java/com/gaxf/system/domain/vo/XfApproveFlowConfigBody.java` — request body with stages and stage departments
- Create: `gaxf-server/gaxf-system/src/main/java/com/gaxf/system/domain/vo/XfApproveActionBody.java` — pass/reject request body
- Create: `gaxf-server/gaxf-system/src/main/java/com/gaxf/system/domain/vo/XfApproveProgressVO.java` — work-order detail approval timeline view
- Create: `gaxf-server/gaxf-system/src/main/java/com/gaxf/system/mapper/XfApproveFlowConfigMapper.java`
- Create: `gaxf-server/gaxf-system/src/main/java/com/gaxf/system/mapper/XfApproveInstanceMapper.java`
- Create: `gaxf-server/gaxf-system/src/main/java/com/gaxf/system/service/IXfApproveFlowConfigService.java`
- Create: `gaxf-server/gaxf-system/src/main/java/com/gaxf/system/service/IXfApproveRuntimeService.java`
- Create: `gaxf-server/gaxf-system/src/main/java/com/gaxf/system/service/impl/XfApproveFlowConfigServiceImpl.java`
- Create: `gaxf-server/gaxf-system/src/main/java/com/gaxf/system/service/impl/XfApproveRuntimeServiceImpl.java`
- Create: `gaxf-server/gaxf-admin/src/main/java/com/gaxf/web/controller/xf/XfApproveFlowConfigController.java`
- Create: `gaxf-server/gaxf-admin/src/main/java/com/gaxf/web/controller/xf/XfApproveRuntimeController.java`
- Create: `gaxf-server/gaxf-system/src/main/resources/mapper/system/XfApproveFlowConfigMapper.xml`
- Create: `gaxf-server/gaxf-system/src/main/resources/mapper/system/XfApproveInstanceMapper.xml`
- Modify: `gaxf-server/gaxf-system/src/main/java/com/gaxf/system/domain/XfWorkOrder.java` — add approval summary fields
- Modify: `gaxf-server/gaxf-system/src/main/java/com/gaxf/system/service/IXfWorkOrderService.java` — expose approval-aware detail hooks if needed
- Modify: `gaxf-server/gaxf-system/src/main/java/com/gaxf/system/service/impl/XfWorkOrderServiceImpl.java` — load approval progress with work-order detail and update status transitions
- Modify: `gaxf-server/gaxf-admin/src/main/java/com/gaxf/web/controller/xf/XfWorkOrderController.java` — add submit-approval endpoint if kept in work-order controller
- Modify: `gaxf-server/gaxf-system/src/main/resources/mapper/system/XfWorkOrderMapper.xml` — approval-related select fields

### Frontend
- Create: `gaxf-ui/src/api/xf/approve.ts` — approval config/runtime API wrappers
- Create: `gaxf-ui/src/types/api/xf/approve.ts` — approval config, stage, task, and progress types
- Create: `gaxf-ui/src/views/xf/approveConfig/index.vue` — flow config list + dialog editor
- Create: `gaxf-ui/src/views/xf/approveTodo/index.vue` — approval todo page for pending and handled tasks
- Modify: `gaxf-ui/src/views/xf/workOrder/index.vue` — add submit-approval/progress-related actions and statuses
- Modify: `gaxf-ui/src/views/xf/workOrder/detail.vue` — show approval progress and approval action entry points
- Modify: `gaxf-ui/src/views/xf/review/index.vue` — either replace with new approval todo implementation or redirect to the new page
- Modify: `gaxf-ui/src/api/xf/workOrder.ts` — add submit approval / progress endpoints if needed
- Modify: `gaxf-ui/src/types/api/xf/workOrder.ts` — extend status and approval summary fields
- Modify: router/menu seed SQL through `gaxf-server/sql/xf_approve_flow.sql`

### Test/Verification Targets
- Backend manual verification via work-order, config, and approval APIs
- Frontend manual verification through approval config page, work-order detail, and approval todo page

---

### Task 1: Add approval flow schema and menu seeds

**Files:**
- Create: `gaxf-server/sql/xf_approve_flow.sql`

- [ ] **Step 1: Write the SQL file with approval config tables**

```sql
CREATE TABLE `xf_approve_flow_config` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '流程配置ID',
  `flow_name` VARCHAR(100) NOT NULL COMMENT '流程名称',
  `apply_dept_id` BIGINT NOT NULL COMMENT '发起部门ID',
  `apply_dept_name` VARCHAR(100) NOT NULL COMMENT '发起部门名称',
  `status` CHAR(1) NOT NULL DEFAULT '0' COMMENT '状态 0启用 1停用',
  `remark` VARCHAR(500) DEFAULT NULL COMMENT '备注',
  `create_by` VARCHAR(64) DEFAULT '' COMMENT '创建者',
  `create_time` DATETIME DEFAULT NULL COMMENT '创建时间',
  `update_by` VARCHAR(64) DEFAULT '' COMMENT '更新者',
  `update_time` DATETIME DEFAULT NULL COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_xf_approve_flow_apply_dept` (`apply_dept_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='审批流程配置';

CREATE TABLE `xf_approve_flow_stage` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '流程阶段ID',
  `flow_config_id` BIGINT NOT NULL COMMENT '流程配置ID',
  `stage_no` INT NOT NULL COMMENT '阶段序号',
  `stage_name` VARCHAR(100) NOT NULL COMMENT '阶段名称',
  `parallel_mode` CHAR(1) NOT NULL DEFAULT '1' COMMENT '1并行会签',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_xf_approve_stage_no` (`flow_config_id`, `stage_no`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='审批流程阶段';

CREATE TABLE `xf_approve_flow_stage_dept` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '阶段审批部门ID',
  `stage_id` BIGINT NOT NULL COMMENT '阶段ID',
  `approve_dept_id` BIGINT NOT NULL COMMENT '审批部门ID',
  `approve_dept_name` VARCHAR(100) NOT NULL COMMENT '审批部门名称',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_xf_approve_stage_dept` (`stage_id`, `approve_dept_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='阶段审批部门';
```

- [ ] **Step 2: Append runtime tables to the same SQL file**

```sql
CREATE TABLE `xf_approve_instance` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '审批实例ID',
  `order_id` BIGINT NOT NULL COMMENT '工单ID',
  `flow_config_id` BIGINT NOT NULL COMMENT '流程配置ID',
  `flow_name` VARCHAR(100) NOT NULL COMMENT '流程名称快照',
  `apply_dept_id` BIGINT NOT NULL COMMENT '发起部门ID',
  `apply_dept_name` VARCHAR(100) NOT NULL COMMENT '发起部门名称',
  `current_stage_no` INT NOT NULL DEFAULT 1 COMMENT '当前阶段',
  `status` CHAR(1) NOT NULL DEFAULT '0' COMMENT '0审批中 1已通过 2已驳回',
  `start_time` DATETIME DEFAULT NULL COMMENT '发起时间',
  `finish_time` DATETIME DEFAULT NULL COMMENT '完成时间',
  `create_by` VARCHAR(64) DEFAULT '' COMMENT '创建者',
  `create_time` DATETIME DEFAULT NULL COMMENT '创建时间',
  `update_by` VARCHAR(64) DEFAULT '' COMMENT '更新者',
  `update_time` DATETIME DEFAULT NULL COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_xf_approve_instance_order` (`order_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='审批实例';

CREATE TABLE `xf_approve_task` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '审批任务ID',
  `instance_id` BIGINT NOT NULL COMMENT '审批实例ID',
  `order_id` BIGINT NOT NULL COMMENT '工单ID',
  `stage_no` INT NOT NULL COMMENT '阶段序号',
  `stage_name` VARCHAR(100) NOT NULL COMMENT '阶段名称',
  `approve_dept_id` BIGINT NOT NULL COMMENT '审批部门ID',
  `approve_dept_name` VARCHAR(100) NOT NULL COMMENT '审批部门名称',
  `reviewer_id` BIGINT DEFAULT NULL COMMENT '审批人ID',
  `reviewer_name` VARCHAR(64) DEFAULT NULL COMMENT '审批人姓名',
  `opinion` VARCHAR(500) DEFAULT NULL COMMENT '审批意见',
  `status` CHAR(1) NOT NULL DEFAULT '0' COMMENT '0待审批 1通过 2驳回',
  `action_time` DATETIME DEFAULT NULL COMMENT '审批时间',
  `create_by` VARCHAR(64) DEFAULT '' COMMENT '创建者',
  `create_time` DATETIME DEFAULT NULL COMMENT '创建时间',
  `update_by` VARCHAR(64) DEFAULT '' COMMENT '更新者',
  `update_time` DATETIME DEFAULT NULL COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_xf_approve_task_node` (`instance_id`, `stage_no`, `approve_dept_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='审批任务';
```

- [ ] **Step 3: Add menu and permission seed statements**

```sql
INSERT INTO `sys_menu` (`menu_id`, `menu_name`, `parent_id`, `order_num`, `path`, `component`, `query`, `route_name`, `is_frame`, `is_cache`, `menu_type`, `visible`, `status`, `perms`, `icon`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`) VALUES
(2200, '审批流程配置', 2000, 5, 'approveConfig', 'xf/approveConfig/index', '', '', 1, 0, 'C', '0', '0', 'xf:approveConfig:list', 'guide', 'admin', NOW(), '', NULL, '审批流程配置菜单'),
(2201, '审批待办', 2000, 6, 'approveTodo', 'xf/approveTodo/index', '', '', 1, 0, 'C', '0', '0', 'xf:approveTask:list', 'form', 'admin', NOW(), '', NULL, '审批待办菜单');

INSERT INTO `sys_menu` (`menu_id`, `menu_name`, `parent_id`, `order_num`, `path`, `component`, `query`, `route_name`, `is_frame`, `is_cache`, `menu_type`, `visible`, `status`, `perms`, `icon`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`) VALUES
(2202, '审批流程查询', 2200, 1, '', '', '', '', 1, 0, 'F', '0', '0', 'xf:approveConfig:query', '#', 'admin', NOW(), '', NULL, ''),
(2203, '审批流程新增', 2200, 2, '', '', '', '', 1, 0, 'F', '0', '0', 'xf:approveConfig:add', '#', 'admin', NOW(), '', NULL, ''),
(2204, '审批流程修改', 2200, 3, '', '', '', '', 1, 0, 'F', '0', '0', 'xf:approveConfig:edit', '#', 'admin', NOW(), '', NULL, ''),
(2205, '审批流程删除', 2200, 4, '', '', '', '', 1, 0, 'F', '0', '0', 'xf:approveConfig:remove', '#', 'admin', NOW(), '', NULL, ''),
(2206, '审批待办查询', 2201, 1, '', '', '', '', 1, 0, 'F', '0', '0', 'xf:approveTask:query', '#', 'admin', NOW(), '', NULL, ''),
(2207, '审批处理', 2201, 2, '', '', '', '', 1, 0, 'F', '0', '0', 'xf:approveTask:action', '#', 'admin', NOW(), '', NULL, '');
```

- [ ] **Step 4: Apply the SQL in the target database**

Run: `mysql -u root -p < gaxf-server/sql/xf_approve_flow.sql`
Expected: table creation and insert statements succeed with no duplicate-key errors on a fresh database

- [ ] **Step 5: Commit**

```bash
git add gaxf-server/sql/xf_approve_flow.sql
git commit -m "feat: add approval flow schema"
```

### Task 2: Create backend approval config domain and mapper skeleton

**Files:**
- Create: `gaxf-server/gaxf-system/src/main/java/com/gaxf/system/domain/XfApproveFlowConfig.java`
- Create: `gaxf-server/gaxf-system/src/main/java/com/gaxf/system/domain/XfApproveFlowStage.java`
- Create: `gaxf-server/gaxf-system/src/main/java/com/gaxf/system/domain/XfApproveFlowStageDept.java`
- Create: `gaxf-server/gaxf-system/src/main/java/com/gaxf/system/domain/vo/XfApproveFlowConfigBody.java`
- Create: `gaxf-server/gaxf-system/src/main/java/com/gaxf/system/mapper/XfApproveFlowConfigMapper.java`
- Create: `gaxf-server/gaxf-system/src/main/resources/mapper/system/XfApproveFlowConfigMapper.xml`

- [ ] **Step 1: Write the flow config aggregate root**

```java
public class XfApproveFlowConfig extends BaseEntity {
    private Long id;
    private String flowName;
    private Long applyDeptId;
    private String applyDeptName;
    private String status;
    private List<XfApproveFlowStage> stages;

    // standard getters and setters
}
```

- [ ] **Step 2: Write stage and stage-department child models**

```java
public class XfApproveFlowStage {
    private Long id;
    private Long flowConfigId;
    private Integer stageNo;
    private String stageName;
    private String parallelMode;
    private List<XfApproveFlowStageDept> deptList;

    // standard getters and setters
}

public class XfApproveFlowStageDept {
    private Long id;
    private Long stageId;
    private Long approveDeptId;
    private String approveDeptName;

    // standard getters and setters
}
```

- [ ] **Step 3: Write the request body object used by save/update**

```java
public class XfApproveFlowConfigBody extends XfApproveFlowConfig {
    private static final long serialVersionUID = 1L;

    @Override
    public List<XfApproveFlowStage> getStages() {
        return super.getStages();
    }
}
```

- [ ] **Step 4: Create the mapper interface**

```java
public interface XfApproveFlowConfigMapper {
    XfApproveFlowConfig selectXfApproveFlowConfigById(Long id);
    List<XfApproveFlowConfig> selectXfApproveFlowConfigList(XfApproveFlowConfig query);
    XfApproveFlowConfig selectEnabledFlowByApplyDeptId(Long applyDeptId);
    int countEnabledFlowByApplyDeptId(@Param("applyDeptId") Long applyDeptId, @Param("excludeId") Long excludeId);
    int insertXfApproveFlowConfig(XfApproveFlowConfig config);
    int updateXfApproveFlowConfig(XfApproveFlowConfig config);
    int deleteXfApproveFlowConfigByIds(Long[] ids);
    int deleteFlowStagesByConfigId(Long flowConfigId);
    int deleteStageDeptsByConfigId(Long flowConfigId);
    int batchInsertFlowStages(@Param("list") List<XfApproveFlowStage> list);
    int batchInsertStageDepts(@Param("list") List<XfApproveFlowStageDept> list);
}
```

- [ ] **Step 5: Write the result map and CRUD SQL**

```xml
<resultMap id="XfApproveFlowConfigResult" type="com.gaxf.system.domain.XfApproveFlowConfig">
    <id property="id" column="id"/>
    <result property="flowName" column="flow_name"/>
    <result property="applyDeptId" column="apply_dept_id"/>
    <result property="applyDeptName" column="apply_dept_name"/>
    <result property="status" column="status"/>
    <collection property="stages" ofType="com.gaxf.system.domain.XfApproveFlowStage" select="selectStagesByFlowConfigId" column="id"/>
</resultMap>

<select id="selectEnabledFlowByApplyDeptId" resultMap="XfApproveFlowConfigResult">
    select id, flow_name, apply_dept_id, apply_dept_name, status
    from xf_approve_flow_config
    where apply_dept_id = #{applyDeptId} and status = '0'
</select>
```

- [ ] **Step 6: Run compile to verify the mapper wiring**

Run: `mvn -pl gaxf-server/gaxf-system -am -DskipTests compile`
Expected: BUILD SUCCESS

- [ ] **Step 7: Commit**

```bash
git add gaxf-server/gaxf-system/src/main/java/com/gaxf/system/domain/XfApproveFlowConfig.java gaxf-server/gaxf-system/src/main/java/com/gaxf/system/domain/XfApproveFlowStage.java gaxf-server/gaxf-system/src/main/java/com/gaxf/system/domain/XfApproveFlowStageDept.java gaxf-server/gaxf-system/src/main/java/com/gaxf/system/domain/vo/XfApproveFlowConfigBody.java gaxf-server/gaxf-system/src/main/java/com/gaxf/system/mapper/XfApproveFlowConfigMapper.java gaxf-server/gaxf-system/src/main/resources/mapper/system/XfApproveFlowConfigMapper.xml
git commit -m "feat: add approval flow config models"
```

### Task 3: Implement backend approval config service and controller

**Files:**
- Create: `gaxf-server/gaxf-system/src/main/java/com/gaxf/system/service/IXfApproveFlowConfigService.java`
- Create: `gaxf-server/gaxf-system/src/main/java/com/gaxf/system/service/impl/XfApproveFlowConfigServiceImpl.java`
- Create: `gaxf-server/gaxf-admin/src/main/java/com/gaxf/web/controller/xf/XfApproveFlowConfigController.java`

- [ ] **Step 1: Define the service contract**

```java
public interface IXfApproveFlowConfigService {
    XfApproveFlowConfig selectXfApproveFlowConfigById(Long id);
    List<XfApproveFlowConfig> selectXfApproveFlowConfigList(XfApproveFlowConfig query);
    int insertXfApproveFlowConfig(XfApproveFlowConfigBody body);
    int updateXfApproveFlowConfig(XfApproveFlowConfigBody body);
    int deleteXfApproveFlowConfigByIds(Long[] ids);
}
```

- [ ] **Step 2: Implement validation and child-table persistence**

```java
private void validateFlowConfig(XfApproveFlowConfigBody body) {
    if (body.getApplyDeptId() == null) {
        throw new ServiceException("请选择发起部门");
    }
    if (StringUtils.isBlank(body.getFlowName())) {
        throw new ServiceException("请输入流程名称");
    }
    if (CollectionUtils.isEmpty(body.getStages())) {
        throw new ServiceException("至少配置一个审批阶段");
    }
    for (XfApproveFlowStage stage : body.getStages()) {
        if (CollectionUtils.isEmpty(stage.getDeptList())) {
            throw new ServiceException("每个阶段至少选择一个审批部门");
        }
    }
}
```

- [ ] **Step 3: Implement insert/update with uniqueness enforcement**

```java
@Transactional
public int insertXfApproveFlowConfig(XfApproveFlowConfigBody body) {
    validateFlowConfig(body);
    if (xfApproveFlowConfigMapper.countEnabledFlowByApplyDeptId(body.getApplyDeptId(), null) > 0 && "0".equals(body.getStatus())) {
        throw new ServiceException("该发起部门已存在启用中的审批流程");
    }
    xfApproveFlowConfigMapper.insertXfApproveFlowConfig(body);
    saveChildren(body);
    return 1;
}
```

- [ ] **Step 4: Implement the controller endpoints**

```java
@RestController
@RequestMapping("/xf/approve/config")
public class XfApproveFlowConfigController extends BaseController {
    @Autowired
    private IXfApproveFlowConfigService xfApproveFlowConfigService;

    @PreAuthorize("@ss.hasPermi('xf:approveConfig:list')")
    @GetMapping("/list")
    public TableDataInfo list(XfApproveFlowConfig query) {
        startPage();
        return getDataTable(xfApproveFlowConfigService.selectXfApproveFlowConfigList(query));
    }
}
```

- [ ] **Step 5: Run compile to verify service/controller wiring**

Run: `mvn -pl gaxf-server/gaxf-admin -am -DskipTests compile`
Expected: BUILD SUCCESS

- [ ] **Step 6: Commit**

```bash
git add gaxf-server/gaxf-system/src/main/java/com/gaxf/system/service/IXfApproveFlowConfigService.java gaxf-server/gaxf-system/src/main/java/com/gaxf/system/service/impl/XfApproveFlowConfigServiceImpl.java gaxf-server/gaxf-admin/src/main/java/com/gaxf/web/controller/xf/XfApproveFlowConfigController.java
git commit -m "feat: add approval flow config management"
```

### Task 4: Create runtime approval domain and mapper

**Files:**
- Create: `gaxf-server/gaxf-system/src/main/java/com/gaxf/system/domain/XfApproveInstance.java`
- Create: `gaxf-server/gaxf-system/src/main/java/com/gaxf/system/domain/XfApproveTask.java`
- Create: `gaxf-server/gaxf-system/src/main/java/com/gaxf/system/domain/vo/XfApproveActionBody.java`
- Create: `gaxf-server/gaxf-system/src/main/java/com/gaxf/system/domain/vo/XfApproveProgressVO.java`
- Create: `gaxf-server/gaxf-system/src/main/java/com/gaxf/system/mapper/XfApproveInstanceMapper.java`
- Create: `gaxf-server/gaxf-system/src/main/resources/mapper/system/XfApproveInstanceMapper.xml`

- [ ] **Step 1: Write runtime models**

```java
public class XfApproveInstance extends BaseEntity {
    private Long id;
    private Long orderId;
    private Long flowConfigId;
    private String flowName;
    private Long applyDeptId;
    private String applyDeptName;
    private Integer currentStageNo;
    private String status;
    private Date startTime;
    private Date finishTime;
    private List<XfApproveTask> taskList;

    // standard getters and setters
}

public class XfApproveTask extends BaseEntity {
    private Long id;
    private Long instanceId;
    private Long orderId;
    private Integer stageNo;
    private String stageName;
    private Long approveDeptId;
    private String approveDeptName;
    private Long reviewerId;
    private String reviewerName;
    private String opinion;
    private String status;
    private Date actionTime;

    // standard getters and setters
}
```

- [ ] **Step 2: Write request and progress VO objects**

```java
public class XfApproveActionBody {
    private Long taskId;
    private String opinion;

    // standard getters and setters
}

public class XfApproveProgressVO {
    private Long instanceId;
    private String flowName;
    private String status;
    private Integer currentStageNo;
    private List<XfApproveTask> taskList;

    // standard getters and setters
}
```

- [ ] **Step 3: Define the runtime mapper contract**

```java
public interface XfApproveInstanceMapper {
    XfApproveInstance selectInstanceByOrderId(Long orderId);
    XfApproveInstance selectInstanceById(Long id);
    List<XfApproveTask> selectMyPendingTasks(@Param("deptId") Long deptId);
    List<XfApproveTask> selectMyHandledTasks(@Param("deptId") Long deptId);
    int insertXfApproveInstance(XfApproveInstance instance);
    int batchInsertApproveTasks(@Param("list") List<XfApproveTask> list);
    int updateApproveTask(XfApproveTask task);
    int updateApproveInstance(XfApproveInstance instance);
    int countStagePendingTasks(@Param("instanceId") Long instanceId, @Param("stageNo") Integer stageNo);
    int countStageRejectedTasks(@Param("instanceId") Long instanceId, @Param("stageNo") Integer stageNo);
}
```

- [ ] **Step 4: Implement task list SQL for todo pages**

```xml
<select id="selectMyPendingTasks" resultType="com.gaxf.system.domain.XfApproveTask">
    select t.id, t.instance_id, t.order_id, t.stage_no, t.stage_name,
           t.approve_dept_id, t.approve_dept_name, t.status, t.create_time,
           w.title as order_title, w.xf_case_no
    from xf_approve_task t
    left join xf_work_order w on w.id = t.order_id
    where t.approve_dept_id = #{deptId} and t.status = '0'
    order by t.create_time desc
</select>
```

- [ ] **Step 5: Run compile to verify runtime mapper wiring**

Run: `mvn -pl gaxf-server/gaxf-system -am -DskipTests compile`
Expected: BUILD SUCCESS

- [ ] **Step 6: Commit**

```bash
git add gaxf-server/gaxf-system/src/main/java/com/gaxf/system/domain/XfApproveInstance.java gaxf-server/gaxf-system/src/main/java/com/gaxf/system/domain/XfApproveTask.java gaxf-server/gaxf-system/src/main/java/com/gaxf/system/domain/vo/XfApproveActionBody.java gaxf-server/gaxf-system/src/main/java/com/gaxf/system/domain/vo/XfApproveProgressVO.java gaxf-server/gaxf-system/src/main/java/com/gaxf/system/mapper/XfApproveInstanceMapper.java gaxf-server/gaxf-system/src/main/resources/mapper/system/XfApproveInstanceMapper.xml
git commit -m "feat: add approval runtime models"
```

### Task 5: Implement runtime service for submit/pass/reject and staged parallel flow

**Files:**
- Create: `gaxf-server/gaxf-system/src/main/java/com/gaxf/system/service/IXfApproveRuntimeService.java`
- Create: `gaxf-server/gaxf-system/src/main/java/com/gaxf/system/service/impl/XfApproveRuntimeServiceImpl.java`
- Modify: `gaxf-server/gaxf-system/src/main/java/com/gaxf/system/service/impl/XfWorkOrderServiceImpl.java`
- Modify: `gaxf-server/gaxf-system/src/main/java/com/gaxf/system/domain/XfWorkOrder.java`

- [ ] **Step 1: Define the runtime service contract**

```java
public interface IXfApproveRuntimeService {
    void startApprove(Long orderId, Long currentDeptId, String currentDeptName, String username);
    void passTask(Long taskId, String opinion, Long userId, String username, Long deptId);
    void rejectTask(Long taskId, String opinion, Long userId, String username, Long deptId);
    XfApproveProgressVO selectProgressByOrderId(Long orderId);
    List<XfApproveTask> selectMyPendingTasks(Long deptId);
    List<XfApproveTask> selectMyHandledTasks(Long deptId);
}
```

- [ ] **Step 2: Implement start approval with first-stage task generation**

```java
@Transactional
public void startApprove(Long orderId, Long currentDeptId, String currentDeptName, String username) {
    XfWorkOrder workOrder = xfWorkOrderMapper.selectXfWorkOrderById(orderId);
    if (workOrder == null) {
        throw new ServiceException("工单不存在");
    }
    if (!"0".equals(workOrder.getStatus()) && !"4".equals(workOrder.getStatus())) {
        throw new ServiceException("当前工单状态不允许提交审批");
    }
    XfApproveFlowConfig config = xfApproveFlowConfigMapper.selectEnabledFlowByApplyDeptId(currentDeptId);
    if (config == null) {
        throw new ServiceException("当前部门未配置审批流程");
    }
    XfApproveInstance instance = buildInstance(orderId, config, currentDeptId, currentDeptName, username);
    xfApproveInstanceMapper.insertXfApproveInstance(instance);
    xfApproveInstanceMapper.batchInsertApproveTasks(buildStageTasks(instance, config.getStages().get(0)));
    XfWorkOrder update = new XfWorkOrder();
    update.setId(orderId);
    update.setStatus("6");
    xfWorkOrderMapper.updateXfWorkOrder(update);
}
```

- [ ] **Step 3: Implement pass logic for parallel stage completion**

```java
@Transactional
public void passTask(Long taskId, String opinion, Long userId, String username, Long deptId) {
    XfApproveTask task = getPendingTask(taskId, deptId);
    task.setStatus("1");
    task.setOpinion(opinion);
    task.setReviewerId(userId);
    task.setReviewerName(username);
    task.setActionTime(DateUtils.getNowDate());
    xfApproveInstanceMapper.updateApproveTask(task);

    if (xfApproveInstanceMapper.countStagePendingTasks(task.getInstanceId(), task.getStageNo()) == 0) {
        advanceOrFinish(task.getInstanceId(), task.getOrderId(), task.getStageNo(), username);
    }
}
```

- [ ] **Step 4: Implement reject logic with work-order rollback status**

```java
@Transactional
public void rejectTask(Long taskId, String opinion, Long userId, String username, Long deptId) {
    XfApproveTask task = getPendingTask(taskId, deptId);
    task.setStatus("2");
    task.setOpinion(opinion);
    task.setReviewerId(userId);
    task.setReviewerName(username);
    task.setActionTime(DateUtils.getNowDate());
    xfApproveInstanceMapper.updateApproveTask(task);

    XfApproveInstance instance = new XfApproveInstance();
    instance.setId(task.getInstanceId());
    instance.setStatus("2");
    instance.setFinishTime(DateUtils.getNowDate());
    xfApproveInstanceMapper.updateApproveInstance(instance);

    XfWorkOrder order = new XfWorkOrder();
    order.setId(task.getOrderId());
    order.setStatus("4");
    xfWorkOrderMapper.updateXfWorkOrder(order);
}
```

- [ ] **Step 5: Extend work-order domain status mapping and detail summary fields**

```java
private Long approveInstanceId;
private String approveStatus;
private Integer currentApproveStage;
private List<XfApproveTask> approveTaskList;
```

- [ ] **Step 6: Run compile to verify service integration**

Run: `mvn -pl gaxf-server/gaxf-admin -am -DskipTests compile`
Expected: BUILD SUCCESS

- [ ] **Step 7: Commit**

```bash
git add gaxf-server/gaxf-system/src/main/java/com/gaxf/system/service/IXfApproveRuntimeService.java gaxf-server/gaxf-system/src/main/java/com/gaxf/system/service/impl/XfApproveRuntimeServiceImpl.java gaxf-server/gaxf-system/src/main/java/com/gaxf/system/service/impl/XfWorkOrderServiceImpl.java gaxf-server/gaxf-system/src/main/java/com/gaxf/system/domain/XfWorkOrder.java
git commit -m "feat: implement approval runtime flow"
```

### Task 6: Expose approval runtime controllers and work-order integration endpoints

**Files:**
- Create: `gaxf-server/gaxf-admin/src/main/java/com/gaxf/web/controller/xf/XfApproveRuntimeController.java`
- Modify: `gaxf-server/gaxf-admin/src/main/java/com/gaxf/web/controller/xf/XfWorkOrderController.java`

- [ ] **Step 1: Add runtime controller for todo and actions**

```java
@RestController
@RequestMapping("/xf/approve/task")
public class XfApproveRuntimeController extends BaseController {
    @Autowired
    private IXfApproveRuntimeService xfApproveRuntimeService;

    @PreAuthorize("@ss.hasPermi('xf:approveTask:list')")
    @GetMapping("/myTodo")
    public AjaxResult myTodo() {
        Map<String, Object> data = new HashMap<>();
        data.put("pending", xfApproveRuntimeService.selectMyPendingTasks(getDeptId()));
        data.put("handled", xfApproveRuntimeService.selectMyHandledTasks(getDeptId()));
        return success(data);
    }
}
```

- [ ] **Step 2: Add pass and reject endpoints**

```java
@PreAuthorize("@ss.hasPermi('xf:approveTask:action')")
@PostMapping("/pass")
public AjaxResult pass(@RequestBody XfApproveActionBody body) {
    xfApproveRuntimeService.passTask(body.getTaskId(), body.getOpinion(), getUserId(), getUsername(), getDeptId());
    return success();
}

@PreAuthorize("@ss.hasPermi('xf:approveTask:action')")
@PostMapping("/reject")
public AjaxResult reject(@RequestBody XfApproveActionBody body) {
    xfApproveRuntimeService.rejectTask(body.getTaskId(), body.getOpinion(), getUserId(), getUsername(), getDeptId());
    return success();
}
```

- [ ] **Step 3: Add work-order submit-approval and progress endpoints**

```java
@PreAuthorize("@ss.hasPermi('xf:workOrder:list')")
@PostMapping("/startApprove/{orderId}")
public AjaxResult startApprove(@PathVariable Long orderId) {
    xfApproveRuntimeService.startApprove(orderId, getDeptId(), getDeptName(), getUsername());
    return success();
}

@PreAuthorize("@ss.hasPermi('xf:workOrder:list')")
@GetMapping("/approveProgress/{orderId}")
public AjaxResult approveProgress(@PathVariable Long orderId) {
    return success(xfApproveRuntimeService.selectProgressByOrderId(orderId));
}
```

- [ ] **Step 4: Run compile to verify controller endpoints**

Run: `mvn -pl gaxf-server/gaxf-admin -am -DskipTests compile`
Expected: BUILD SUCCESS

- [ ] **Step 5: Commit**

```bash
git add gaxf-server/gaxf-admin/src/main/java/com/gaxf/web/controller/xf/XfApproveRuntimeController.java gaxf-server/gaxf-admin/src/main/java/com/gaxf/web/controller/xf/XfWorkOrderController.java
git commit -m "feat: expose approval runtime endpoints"
```

### Task 7: Add frontend approval API and types

**Files:**
- Create: `gaxf-ui/src/api/xf/approve.ts`
- Create: `gaxf-ui/src/types/api/xf/approve.ts`
- Modify: `gaxf-ui/src/api/xf/workOrder.ts`
- Modify: `gaxf-ui/src/types/api/xf/workOrder.ts`

- [ ] **Step 1: Write approval config/runtime types**

```ts
export interface ApproveFlowStageDept {
  id?: number
  approveDeptId: number
  approveDeptName: string
}

export interface ApproveFlowStage {
  id?: number
  stageNo: number
  stageName: string
  parallelMode: '1'
  deptList: ApproveFlowStageDept[]
}

export interface ApproveFlowConfig {
  id?: number
  flowName: string
  applyDeptId: number
  applyDeptName: string
  status: '0' | '1'
  stages: ApproveFlowStage[]
}
```

- [ ] **Step 2: Write runtime task and progress types**

```ts
export interface ApproveTask {
  id: number
  orderId: number
  stageNo: number
  stageName: string
  approveDeptId: number
  approveDeptName: string
  reviewerName?: string
  opinion?: string
  status: '0' | '1' | '2'
  actionTime?: string
  orderTitle?: string
  xfCaseNo?: string
}

export interface ApproveProgress {
  instanceId: number
  flowName: string
  status: '0' | '1' | '2'
  currentStageNo: number
  taskList: ApproveTask[]
}
```

- [ ] **Step 3: Write the API wrapper file**

```ts
export function listApproveFlowConfig(query: any) {
  return request({ url: '/xf/approve/config/list', method: 'get', params: query })
}

export function addApproveFlowConfig(data: ApproveFlowConfig) {
  return request({ url: '/xf/approve/config', method: 'post', data })
}

export function updateApproveFlowConfig(data: ApproveFlowConfig) {
  return request({ url: '/xf/approve/config', method: 'put', data })
}

export function getApproveTodo() {
  return request({ url: '/xf/approve/task/myTodo', method: 'get' })
}
```

- [ ] **Step 4: Extend work-order API/types for approval start and progress**

```ts
export function startApprove(orderId: number) {
  return request({ url: '/xf/workOrder/startApprove/' + orderId, method: 'post' })
}

export function getApproveProgress(orderId: number) {
  return request({ url: '/xf/workOrder/approveProgress/' + orderId, method: 'get' })
}
```

- [ ] **Step 5: Run frontend type check or build**

Run: `npm --prefix gaxf-ui run build`
Expected: build completes without TypeScript errors from the new api/type files

- [ ] **Step 6: Commit**

```bash
git add gaxf-ui/src/api/xf/approve.ts gaxf-ui/src/types/api/xf/approve.ts gaxf-ui/src/api/xf/workOrder.ts gaxf-ui/src/types/api/xf/workOrder.ts
git commit -m "feat: add frontend approval api"
```

### Task 8: Build approval flow config page

**Files:**
- Create: `gaxf-ui/src/views/xf/approveConfig/index.vue`

- [ ] **Step 1: Write the flow config list page shell**

```vue
<template>
  <div class="approve-config-wrapper">
    <div class="main-card">
      <div class="card-header">
        <div class="title-section"><h2 class="title-text">审批流程配置</h2></div>
        <div class="header-actions">
          <el-button type="primary" @click="handleAdd">新增流程</el-button>
        </div>
      </div>
      <el-table :data="configList" v-loading="loading">
        <el-table-column prop="flowName" label="流程名称" />
        <el-table-column prop="applyDeptName" label="发起部门" />
        <el-table-column label="阶段数">
          <template #default="{ row }">{{ row.stages?.length || 0 }}</template>
        </el-table-column>
      </el-table>
    </div>
  </div>
</template>
```

- [ ] **Step 2: Add editor dialog state and stage helpers**

```ts
const form = ref<ApproveFlowConfig>({
  flowName: '',
  applyDeptId: 0,
  applyDeptName: '',
  status: '0',
  stages: [{ stageNo: 1, stageName: '第1阶段', parallelMode: '1', deptList: [] }]
})

function addStage() {
  form.value.stages.push({
    stageNo: form.value.stages.length + 1,
    stageName: `第${form.value.stages.length + 1}阶段`,
    parallelMode: '1',
    deptList: []
  })
}
```

- [ ] **Step 3: Render per-stage department selectors with dept tree data**

```vue
<el-form-item label="发起部门">
  <el-tree-select
    v-model="form.applyDeptId"
    :data="deptOptions"
    check-strictly
    node-key="id"
    :props="{ label: 'label', children: 'children' }"
    @change="handleApplyDeptChange"
  />
</el-form-item>

<div v-for="(stage, index) in form.stages" :key="index" class="stage-card">
  <el-input v-model="stage.stageName" />
  <el-select v-model="stage.deptList" multiple value-key="approveDeptId">
    <el-option v-for="dept in flatDeptOptions" :key="dept.id" :label="dept.label" :value="{ approveDeptId: dept.id, approveDeptName: dept.label }" />
  </el-select>
</div>
```

- [ ] **Step 4: Add validation and save logic**

```ts
function validateForm() {
  if (!form.value.applyDeptId) return '请选择发起部门'
  if (!form.value.stages.length) return '至少保留一个阶段'
  if (form.value.stages.some(stage => !stage.deptList.length)) return '每个阶段至少选择一个审批部门'
  return ''
}
```

- [ ] **Step 5: Run frontend build to verify the page**

Run: `npm --prefix gaxf-ui run build`
Expected: build completes and includes `xf/approveConfig/index.vue`

- [ ] **Step 6: Commit**

```bash
git add gaxf-ui/src/views/xf/approveConfig/index.vue
git commit -m "feat: add approval flow config page"
```

### Task 9: Build approval todo page and approval actions

**Files:**
- Create: `gaxf-ui/src/views/xf/approveTodo/index.vue`
- Modify: `gaxf-ui/src/views/xf/review/index.vue`

- [ ] **Step 1: Write pending/handled tab structure**

```vue
<el-tabs v-model="activeTab">
  <el-tab-pane label="待审批" name="pending">
    <el-table :data="pendingList">
      <el-table-column prop="xfCaseNo" label="信访件编号" />
      <el-table-column prop="orderTitle" label="工单标题" />
      <el-table-column prop="stageName" label="当前阶段" />
    </el-table>
  </el-tab-pane>
  <el-tab-pane label="我已审批" name="handled">
    <el-table :data="handledList" />
  </el-tab-pane>
</el-tabs>
```

- [ ] **Step 2: Add action dialog for pass/reject**

```ts
const actionForm = ref({ taskId: 0, opinion: '', action: 'pass' as 'pass' | 'reject' })

async function submitAction() {
  if (!actionForm.value.opinion) {
    ElMessage.warning('请输入审批意见')
    return
  }
  if (actionForm.value.action === 'pass') {
    await passApproveTask({ taskId: actionForm.value.taskId, opinion: actionForm.value.opinion })
  } else {
    await rejectApproveTask({ taskId: actionForm.value.taskId, opinion: actionForm.value.opinion })
  }
}
```

- [ ] **Step 3: Replace old review page with redirect or wrapper**

```ts
onMounted(() => {
  router.replace({ path: '/xf/approveTodo' })
})
```

- [ ] **Step 4: Run frontend build to verify todo page**

Run: `npm --prefix gaxf-ui run build`
Expected: build completes and approval todo page imports resolve correctly

- [ ] **Step 5: Commit**

```bash
git add gaxf-ui/src/views/xf/approveTodo/index.vue gaxf-ui/src/views/xf/review/index.vue
git commit -m "feat: add approval todo page"
```

### Task 10: Integrate approval submission and timeline into work-order pages

**Files:**
- Modify: `gaxf-ui/src/views/xf/workOrder/index.vue`
- Modify: `gaxf-ui/src/views/xf/workOrder/detail.vue`

- [ ] **Step 1: Extend work-order list action area with submit-approval button**

```vue
<el-tooltip v-if="row.status === '0' || row.status === '4'" content="提交审批" placement="top">
  <button class="action-icon-btn success" @click.stop="handleStartApprove(row)">
    <LucideShieldCheck :size="14" />
  </button>
</el-tooltip>
```

- [ ] **Step 2: Add submit-approval handler in the list page**

```ts
async function handleStartApprove(row: XfWorkOrder) {
  try {
    await startApprove(row.id)
    ElMessage.success('提交审批成功')
    loadWorkOrderList()
  } catch (error) {
    ElMessage.error('提交审批失败')
  }
}
```

- [ ] **Step 3: Add approval timeline tab to the detail page**

```vue
<el-tab-pane label="审批进度" name="approve">
  <div v-if="!approveProgress?.taskList?.length" class="empty-tab">暂无审批记录</div>
  <el-timeline v-else>
    <el-timeline-item
      v-for="task in approveProgress.taskList"
      :key="task.id"
      :timestamp="formatTime(task.actionTime || task.createTime)"
      placement="top"
    >
      <div class="timeline-card">
        <div class="timeline-header">
          <span>{{ task.stageName }}</span>
          <span>{{ task.approveDeptName }}</span>
          <span>{{ getApproveTaskStatusText(task.status) }}</span>
        </div>
        <div v-if="task.opinion">{{ task.opinion }}</div>
      </div>
    </el-timeline-item>
  </el-timeline>
</el-tab-pane>
```

- [ ] **Step 4: Load approval progress in detail page setup**

```ts
const approveProgress = ref<ApproveProgress | null>(null)

async function loadApproveProgress(id: number) {
  const res = await getApproveProgress(id)
  approveProgress.value = (res.data || null) as ApproveProgress | null
}
```

- [ ] **Step 5: Run frontend build to verify work-order integration**

Run: `npm --prefix gaxf-ui run build`
Expected: build completes and work-order pages compile with new approval imports

- [ ] **Step 6: Commit**

```bash
git add gaxf-ui/src/views/xf/workOrder/index.vue gaxf-ui/src/views/xf/workOrder/detail.vue
git commit -m "feat: integrate work orders with approval flow"
```

### Task 11: Verify backend and frontend end-to-end behavior

**Files:**
- Modify: any files needed to fix verification issues discovered in this task

- [ ] **Step 1: Start the backend and frontend locally**

Run: `mvn -pl gaxf-server/gaxf-admin -am spring-boot:run`
Expected: backend starts successfully and exposes `/xf/approve/config/list`

Run: `npm --prefix gaxf-ui run dev`
Expected: frontend dev server starts successfully

- [ ] **Step 2: Create one approval flow config through the UI**

```text
Flow name: 部门并行审批测试
Apply dept: one real department from sys_dept
Stage 1: 县局审批 -> 2 departments
Stage 2: 领导审批 -> 1 department
Status: 启用
```

- [ ] **Step 3: Submit one work order into approval**

```text
Open 工单管理 -> choose a row in status 待派单 or 已退回 -> click 提交审批 -> expect status changes to 审批中 and detail page shows first-stage tasks
```

- [ ] **Step 4: Verify staged parallel behavior**

```text
Use a user from first approval department to pass task A; expect instance remains in current stage.
Use a user from second approval department to pass task B; expect next stage task is generated.
Use a user from final stage department to reject; expect instance becomes 驳回 and work order becomes 已退回.
```

- [ ] **Step 5: Run a production frontend build and backend compile**

Run: `npm --prefix gaxf-ui run build && mvn -pl gaxf-server/gaxf-admin -am -DskipTests compile`
Expected: both commands succeed

- [ ] **Step 6: Commit**

```bash
git add .
git commit -m "test: verify approval flow end to end"
```
