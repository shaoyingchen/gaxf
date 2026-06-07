# Work Order Handover Branch Approval Implementation Plan

> **For agentic workers:** REQUIRED SUB-SKILL: Use superpowers:subagent-driven-development (recommended) or superpowers:executing-plans to implement this plan task-by-task. Steps use checkbox (`- [ ]`) syntax for tracking.

**Goal:** Make work-order handover automatically start a configurable approval flow where each selected undertaking department enters its mapped first-review branch, then all branches converge into shared follow-up review stages.

**Architecture:** Extend the existing approval-flow model from "one apply department -> linear stages" to "handover-selected undertaking departments -> dynamic branch first-review tasks -> shared configured summary stages". Keep one approval instance per work order, validate all selected undertaking departments before any writes, and drive both backend/runtime and frontend detail/progress views from the same instance/task data.

**Tech Stack:** Spring Boot, MyBatis XML mapper, RuoYi-style admin/system modules, Vue 3 script setup, Element Plus, TypeScript

---

## File Structure

### Backend
- Modify: `gaxf-server/sql/xf_approve_flow.sql` — add branch mapping table/columns and document migration for existing approval tables.
- Create: `gaxf-server/gaxf-system/src/main/java/com/gaxf/system/domain/XfApproveFlowBranch.java` — branch mapping from undertaking department to first-review department.
- Modify: `gaxf-server/gaxf-system/src/main/java/com/gaxf/system/domain/XfApproveFlowConfig.java` — carry branch mapping list alongside shared stages.
- Modify: `gaxf-server/gaxf-system/src/main/java/com/gaxf/system/domain/XfApproveFlowStage.java` — add stage type so runtime can distinguish summary stages.
- Modify: `gaxf-server/gaxf-system/src/main/java/com/gaxf/system/domain/XfApproveTask.java` — store undertaking department snapshot and branch markers on generated tasks.
- Modify: `gaxf-server/gaxf-system/src/main/java/com/gaxf/system/domain/vo/XfApproveFlowConfigBody.java` — accept branch mappings in save/update payloads.
- Modify: `gaxf-server/gaxf-system/src/main/java/com/gaxf/system/domain/vo/XfApproveProgressVO.java` — expose branch groups for detail display.
- Modify: `gaxf-server/gaxf-system/src/main/java/com/gaxf/system/mapper/XfApproveFlowConfigMapper.java` — persist branch mappings and fetch enabled handover flow.
- Modify: `gaxf-server/gaxf-system/src/main/resources/mapper/system/XfApproveFlowConfigMapper.xml` — map branch rows, stage type, and validation queries.
- Modify: `gaxf-server/gaxf-system/src/main/java/com/gaxf/system/mapper/XfApproveInstanceMapper.java` — support branch task queries and missing-config validation.
- Modify: `gaxf-server/gaxf-system/src/main/resources/mapper/system/XfApproveInstanceMapper.xml` — persist task branch fields and grouped selects.
- Modify: `gaxf-server/gaxf-system/src/main/java/com/gaxf/system/service/IXfApproveFlowConfigService.java` — validate branch mapping completeness during config saves.
- Modify: `gaxf-server/gaxf-system/src/main/java/com/gaxf/system/service/impl/XfApproveFlowConfigServiceImpl.java` — save branch mappings and enforce one enabled handover flow.
- Modify: `gaxf-server/gaxf-system/src/main/java/com/gaxf/system/service/IXfApproveRuntimeService.java` — add start method for multi-undertaking handover approval.
- Modify: `gaxf-server/gaxf-system/src/main/java/com/gaxf/system/service/impl/XfApproveRuntimeServiceImpl.java` — validate undertaking mappings, create branch tasks, and converge into shared stages.
- Modify: `gaxf-server/gaxf-system/src/main/java/com/gaxf/system/service/IXfWorkOrderService.java` — keep handover API contract aligned if signature changes.
- Modify: `gaxf-server/gaxf-system/src/main/java/com/gaxf/system/service/impl/XfWorkOrderServiceImpl.java` — call branch approval runtime inside the handover transaction and surface exact missing departments.
- Modify: `gaxf-server/gaxf-system/src/main/java/com/gaxf/system/domain/XfWorkOrder.java` — expose richer branch approval progress fields if needed by detail UI.
- Modify: `gaxf-server/gaxf-admin/src/main/java/com/gaxf/web/controller/xf/XfWorkOrderController.java` — return handover validation errors cleanly.
- Modify: `gaxf-server/gaxf-admin/src/main/java/com/gaxf/web/controller/xf/XfApproveFlowConfigController.java` — accept branch mapping form payload.

### Frontend
- Modify: `gaxf-ui/src/types/api/xf/approve.ts` — add branch mapping, stage type, and grouped progress types.
- Modify: `gaxf-ui/src/api/xf/approve.ts` — carry branch mapping fields to config endpoints.
- Modify: `gaxf-ui/src/views/xf/approveConfig/index.vue` — let users configure undertaking-department -> first-review-department mappings and shared summary stages.
- Modify: `gaxf-ui/src/types/api/xf/workOrder.ts` — type handover failure messages and branch approval progress.
- Modify: `gaxf-ui/src/api/xf/workOrder.ts` — keep handover request/response typing aligned.
- Modify: `gaxf-ui/src/views/xf/workOrder/index.vue` — show exact config-missing error when handover fails.
- Modify: `gaxf-ui/src/views/xf/workOrder/detail.vue` — display branch first-review tasks and shared summary stages distinctly.
- Modify: `gaxf-ui/src/views/xf/approveTodo/index.vue` — show undertaking department context on pending/handled approval tasks.

### Verification Targets
- Handover with multiple undertaking departments starts one approval instance and multiple branch tasks.
- Handover fails atomically when any selected undertaking department lacks a branch mapping.
- Branch approvals converge into the first shared summary stage only after all branches pass.
- Any branch rejection marks the instance/work order rejected and prevents summary-stage generation.

---

### Task 1: Extend approval schema for branch mappings and branch task snapshots

**Files:**
- Modify: `gaxf-server/sql/xf_approve_flow.sql`

- [ ] **Step 1: Add the branch mapping table definition to the SQL file**

```sql
CREATE TABLE `xf_approve_flow_branch` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '分支映射ID',
  `flow_config_id` BIGINT NOT NULL COMMENT '流程配置ID',
  `undertake_dept_id` BIGINT NOT NULL COMMENT '承办单位ID',
  `undertake_dept_name` VARCHAR(100) NOT NULL COMMENT '承办单位名称',
  `first_approve_dept_id` BIGINT NOT NULL COMMENT '首审部门ID',
  `first_approve_dept_name` VARCHAR(100) NOT NULL COMMENT '首审部门名称',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_xf_approve_flow_branch` (`flow_config_id`, `undertake_dept_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='审批流程承办单位分支映射';
```

- [ ] **Step 2: Add stage/task columns needed for branch-vs-summary runtime handling**

```sql
ALTER TABLE `xf_approve_flow_stage`
  ADD COLUMN `stage_type` CHAR(1) NOT NULL DEFAULT '1' COMMENT '阶段类型 1汇总阶段' AFTER `stage_name`;

ALTER TABLE `xf_approve_task`
  ADD COLUMN `task_type` CHAR(1) NOT NULL DEFAULT '0' COMMENT '任务类型 0承办分支首审 1汇总阶段' AFTER `stage_name`,
  ADD COLUMN `undertake_dept_id` BIGINT DEFAULT NULL COMMENT '承办单位ID快照' AFTER `task_type`,
  ADD COLUMN `undertake_dept_name` VARCHAR(100) DEFAULT NULL COMMENT '承办单位名称快照' AFTER `undertake_dept_id`;
```

- [ ] **Step 3: Append migration notes for environments where the tables already exist**

```sql
-- 如果库中已存在 2026-06-03 版本审批表，请先执行以下迁移：
-- 1) 创建 xf_approve_flow_branch
-- 2) 为 xf_approve_flow_stage 新增 stage_type 字段并将已有阶段默认置为 '1'
-- 3) 为 xf_approve_task 新增 task_type / undertake_dept_id / undertake_dept_name 字段
-- 4) 重新导入菜单前先确认 sys_menu 中对应 menu_id 未被占用
```

- [ ] **Step 4: Apply the SQL in the target database**

Run: `mysql -u root -p < gaxf-server/sql/xf_approve_flow.sql`
Expected: the branch table is created, alter statements succeed on a fresh schema, and no half-applied approval tables remain.

- [ ] **Step 5: Commit**

```bash
git add gaxf-server/sql/xf_approve_flow.sql
git commit -m "feat: extend approval schema for branch handover"
```

### Task 2: Extend approval config domain/model objects for branch mappings

**Files:**
- Create: `gaxf-server/gaxf-system/src/main/java/com/gaxf/system/domain/XfApproveFlowBranch.java`
- Modify: `gaxf-server/gaxf-system/src/main/java/com/gaxf/system/domain/XfApproveFlowConfig.java`
- Modify: `gaxf-server/gaxf-system/src/main/java/com/gaxf/system/domain/XfApproveFlowStage.java`
- Modify: `gaxf-server/gaxf-system/src/main/java/com/gaxf/system/domain/vo/XfApproveFlowConfigBody.java`
- Modify: `gaxf-server/gaxf-system/src/main/java/com/gaxf/system/domain/XfApproveTask.java`
- Modify: `gaxf-server/gaxf-system/src/main/java/com/gaxf/system/domain/vo/XfApproveProgressVO.java`

- [ ] **Step 1: Write the new branch mapping domain object**

```java
package com.gaxf.system.domain;

import java.io.Serializable;

public class XfApproveFlowBranch implements Serializable
{
    private static final long serialVersionUID = 1L;

    private Long id;
    private Long flowConfigId;
    private Long undertakeDeptId;
    private String undertakeDeptName;
    private Long firstApproveDeptId;
    private String firstApproveDeptName;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Long getFlowConfigId() { return flowConfigId; }
    public void setFlowConfigId(Long flowConfigId) { this.flowConfigId = flowConfigId; }
    public Long getUndertakeDeptId() { return undertakeDeptId; }
    public void setUndertakeDeptId(Long undertakeDeptId) { this.undertakeDeptId = undertakeDeptId; }
    public String getUndertakeDeptName() { return undertakeDeptName; }
    public void setUndertakeDeptName(String undertakeDeptName) { this.undertakeDeptName = undertakeDeptName; }
    public Long getFirstApproveDeptId() { return firstApproveDeptId; }
    public void setFirstApproveDeptId(Long firstApproveDeptId) { this.firstApproveDeptId = firstApproveDeptId; }
    public String getFirstApproveDeptName() { return firstApproveDeptName; }
    public void setFirstApproveDeptName(String firstApproveDeptName) { this.firstApproveDeptName = firstApproveDeptName; }
}
```

- [ ] **Step 2: Add branch mappings to the flow config aggregate**

```java
private List<XfApproveFlowBranch> branchList;

public List<XfApproveFlowBranch> getBranchList()
{
    return branchList;
}

public void setBranchList(List<XfApproveFlowBranch> branchList)
{
    this.branchList = branchList;
}
```

- [ ] **Step 3: Add stage type to summary stages**

```java
/** 阶段类型: 1=汇总阶段 */
private String stageType;

public String getStageType()
{
    return stageType;
}

public void setStageType(String stageType)
{
    this.stageType = stageType;
}
```

- [ ] **Step 4: Extend the config body and runtime task/progress objects**

```java
// XfApproveFlowConfigBody keeps inheritance, but payload now carries branchList through the parent.

/** 任务类型: 0=承办分支首审 1=汇总阶段 */
private String taskType;

/** 承办单位ID快照 */
private Long undertakeDeptId;

/** 承办单位名称快照 */
private String undertakeDeptName;

/** 按承办单位分组后的任务列表 */
private List<XfApproveTask> branchTaskList;
```

- [ ] **Step 5: Add getters/setters for the new task/progress fields**

```java
public String getTaskType() { return taskType; }
public void setTaskType(String taskType) { this.taskType = taskType; }
public Long getUndertakeDeptId() { return undertakeDeptId; }
public void setUndertakeDeptId(Long undertakeDeptId) { this.undertakeDeptId = undertakeDeptId; }
public String getUndertakeDeptName() { return undertakeDeptName; }
public void setUndertakeDeptName(String undertakeDeptName) { this.undertakeDeptName = undertakeDeptName; }
public List<XfApproveTask> getBranchTaskList() { return branchTaskList; }
public void setBranchTaskList(List<XfApproveTask> branchTaskList) { this.branchTaskList = branchTaskList; }
```

- [ ] **Step 6: Run a compile-focused smoke check**

Run: `cd gaxf-server && mvn -pl gaxf-system -am -DskipTests compile`
Expected: domain object compilation passes with the new branch/stage/task fields.

- [ ] **Step 7: Commit**

```bash
git add gaxf-server/gaxf-system/src/main/java/com/gaxf/system/domain/XfApproveFlowBranch.java \
  gaxf-server/gaxf-system/src/main/java/com/gaxf/system/domain/XfApproveFlowConfig.java \
  gaxf-server/gaxf-system/src/main/java/com/gaxf/system/domain/XfApproveFlowStage.java \
  gaxf-server/gaxf-system/src/main/java/com/gaxf/system/domain/XfApproveTask.java \
  gaxf-server/gaxf-system/src/main/java/com/gaxf/system/domain/vo/XfApproveFlowConfigBody.java \
  gaxf-server/gaxf-system/src/main/java/com/gaxf/system/domain/vo/XfApproveProgressVO.java
git commit -m "feat: extend approval models for branch handover"
```

### Task 3: Persist branch mappings in approval flow config CRUD

**Files:**
- Modify: `gaxf-server/gaxf-system/src/main/java/com/gaxf/system/mapper/XfApproveFlowConfigMapper.java`
- Modify: `gaxf-server/gaxf-system/src/main/resources/mapper/system/XfApproveFlowConfigMapper.xml`
- Modify: `gaxf-server/gaxf-system/src/main/java/com/gaxf/system/service/impl/XfApproveFlowConfigServiceImpl.java`
- Modify: `gaxf-server/gaxf-system/src/main/java/com/gaxf/system/service/IXfApproveFlowConfigService.java`

- [ ] **Step 1: Extend the mapper interface with branch CRUD helpers**

```java
int deleteFlowBranchesByConfigId(Long flowConfigId);
int batchInsertFlowBranches(@Param("list") List<XfApproveFlowBranch> list);
List<XfApproveFlowBranch> selectFlowBranchesByConfigId(Long flowConfigId);
```

- [ ] **Step 2: Add branch result mapping and nested select wiring in XML**

```xml
<resultMap id="XfApproveFlowConfigResult" type="com.gaxf.system.domain.XfApproveFlowConfig">
    <id property="id" column="id"/>
    <result property="flowName" column="flow_name"/>
    <result property="applyDeptId" column="apply_dept_id"/>
    <result property="applyDeptName" column="apply_dept_name"/>
    <result property="status" column="status"/>
    <collection property="branchList" column="id" select="selectFlowBranchesByConfigId"/>
    <collection property="stages" column="id" select="selectFlowStagesByConfigId"/>
</resultMap>

<resultMap id="XfApproveFlowBranchResult" type="com.gaxf.system.domain.XfApproveFlowBranch">
    <id property="id" column="id"/>
    <result property="flowConfigId" column="flow_config_id"/>
    <result property="undertakeDeptId" column="undertake_dept_id"/>
    <result property="undertakeDeptName" column="undertake_dept_name"/>
    <result property="firstApproveDeptId" column="first_approve_dept_id"/>
    <result property="firstApproveDeptName" column="first_approve_dept_name"/>
</resultMap>
```

- [ ] **Step 3: Persist stage type and batch-insert branch mappings**

```xml
<insert id="batchInsertFlowBranches">
    insert into xf_approve_flow_branch(
        flow_config_id, undertake_dept_id, undertake_dept_name, first_approve_dept_id, first_approve_dept_name
    ) values
    <foreach collection="list" item="item" separator=",">
        (#{item.flowConfigId}, #{item.undertakeDeptId}, #{item.undertakeDeptName}, #{item.firstApproveDeptId}, #{item.firstApproveDeptName})
    </foreach>
</insert>

<insert id="batchInsertFlowStages">
    insert into xf_approve_flow_stage(flow_config_id, stage_no, stage_name, stage_type, parallel_mode)
```

- [ ] **Step 4: Save branch mappings during config insert/update service flows**

```java
private void saveChildren(XfApproveFlowConfigBody body)
{
    if (!CollectionUtils.isEmpty(body.getBranchList()))
    {
        for (XfApproveFlowBranch branch : body.getBranchList())
        {
            branch.setFlowConfigId(body.getId());
        }
        xfApproveFlowConfigMapper.batchInsertFlowBranches(body.getBranchList());
    }
    if (!CollectionUtils.isEmpty(body.getStages()))
    {
        // existing stage save logic continues here
    }
}
```

- [ ] **Step 5: Validate required config structure before save**

```java
if (CollectionUtils.isEmpty(body.getBranchList()))
{
    throw new ServiceException("请至少配置一个承办单位首审映射");
}
if (CollectionUtils.isEmpty(body.getStages()))
{
    throw new ServiceException("请至少配置一个汇总审核阶段");
}
```

- [ ] **Step 6: Verify config CRUD compiles and mapper XML parses**

Run: `cd gaxf-server && mvn -pl gaxf-system -am -DskipTests compile`
Expected: mapper XML loads successfully and service compilation passes.

- [ ] **Step 7: Commit**

```bash
git add gaxf-server/gaxf-system/src/main/java/com/gaxf/system/mapper/XfApproveFlowConfigMapper.java \
  gaxf-server/gaxf-system/src/main/resources/mapper/system/XfApproveFlowConfigMapper.xml \
  gaxf-server/gaxf-system/src/main/java/com/gaxf/system/service/IXfApproveFlowConfigService.java \
  gaxf-server/gaxf-system/src/main/java/com/gaxf/system/service/impl/XfApproveFlowConfigServiceImpl.java
git commit -m "feat: persist branch approval mappings"
```

### Task 4: Add atomic handover validation and branch approval instance creation

**Files:**
- Modify: `gaxf-server/gaxf-system/src/main/java/com/gaxf/system/service/IXfApproveRuntimeService.java`
- Modify: `gaxf-server/gaxf-system/src/main/java/com/gaxf/system/service/impl/XfApproveRuntimeServiceImpl.java`
- Modify: `gaxf-server/gaxf-system/src/main/java/com/gaxf/system/mapper/XfApproveInstanceMapper.java`
- Modify: `gaxf-server/gaxf-system/src/main/resources/mapper/system/XfApproveInstanceMapper.xml`

- [ ] **Step 1: Replace the old start signature with a multi-undertaking handover entrypoint**

```java
void startAssignApprove(Long orderId, Long[] undertakeDeptIds, String[] undertakeDeptNames, String username);
```

- [ ] **Step 2: Add mapper queries for branch config lookup and grouped task persistence**

```java
List<XfApproveFlowBranch> selectFlowBranchesByDeptIds(@Param("flowConfigId") Long flowConfigId,
    @Param("deptIds") List<Long> deptIds);
```

```xml
<select id="selectFlowBranchesByDeptIds" resultMap="com.gaxf.system.mapper.XfApproveFlowConfigMapper.XfApproveFlowBranchResult">
    select id, flow_config_id, undertake_dept_id, undertake_dept_name, first_approve_dept_id, first_approve_dept_name
    from xf_approve_flow_branch
    where flow_config_id = #{flowConfigId}
      and undertake_dept_id in
      <foreach collection="deptIds" item="deptId" open="(" separator="," close=")">
        #{deptId}
      </foreach>
</select>
```

- [ ] **Step 3: Write the failing runtime logic mentally into code as explicit validation**

```java
if (undertakeDeptIds == null || undertakeDeptIds.length == 0)
{
    throw new ServiceException("请选择至少一个承办单位");
}
XfApproveFlowConfig config = xfApproveFlowConfigMapper.selectEnabledFlowByApplyDeptId(workOrder.getCreateDeptId());
if (config == null)
{
    throw new ServiceException("当前发起部门未配置审批流程");
}
List<XfApproveFlowBranch> branchList = xfApproveInstanceMapper.selectFlowBranchesByDeptIds(config.getId(), Arrays.asList(undertakeDeptIds));
if (branchList.size() != undertakeDeptIds.length)
{
    throw new ServiceException(buildMissingDeptMessage(undertakeDeptIds, undertakeDeptNames, branchList));
}
```

- [ ] **Step 4: Generate one approval instance and one branch task per selected undertaking department**

```java
XfApproveInstance instance = new XfApproveInstance();
instance.setOrderId(orderId);
instance.setFlowConfigId(config.getId());
instance.setFlowName(config.getFlowName());
instance.setApplyDeptId(workOrder.getCreateDeptId());
instance.setApplyDeptName(workOrder.getCreateDeptName());
instance.setCurrentStageNo(0);
instance.setStatus("0");
instance.setStartTime(DateUtils.getNowDate());
instance.setCreateBy(username);
xfApproveInstanceMapper.insertXfApproveInstance(instance);

List<XfApproveTask> tasks = new ArrayList<>();
for (XfApproveFlowBranch branch : branchList)
{
    XfApproveTask task = new XfApproveTask();
    task.setInstanceId(instance.getId());
    task.setOrderId(orderId);
    task.setStageNo(0);
    task.setStageName("承办单位首审");
    task.setTaskType("0");
    task.setUndertakeDeptId(branch.getUndertakeDeptId());
    task.setUndertakeDeptName(branch.getUndertakeDeptName());
    task.setApproveDeptId(branch.getFirstApproveDeptId());
    task.setApproveDeptName(branch.getFirstApproveDeptName());
    task.setStatus("0");
    task.setCreateBy(username);
    tasks.add(task);
}
xfApproveInstanceMapper.batchInsertApproveTasks(tasks);
```

- [ ] **Step 5: Converge into configured summary stages only after all branch tasks pass**

```java
if (task.getStageNo() == 0)
{
    if (xfApproveInstanceMapper.countStagePendingTasks(task.getInstanceId(), 0) == 0)
    {
        createNextSummaryStage(task.getInstanceId(), task.getOrderId(), username);
    }
    return;
}
```

- [ ] **Step 6: Make rejection stop the whole approval and leave no summary tasks behind**

```java
instance.setStatus("2");
instance.setFinishTime(DateUtils.getNowDate());
xfApproveInstanceMapper.updateApproveInstance(instance);
order.setStatus("4");
xfWorkOrderMapper.updateXfWorkOrder(order);
```

- [ ] **Step 7: Compile the backend approval runtime changes**

Run: `cd gaxf-server && mvn -pl gaxf-system -am -DskipTests compile`
Expected: approval runtime, mapper interfaces, and XML all compile with the new branch start entrypoint.

- [ ] **Step 8: Commit**

```bash
git add gaxf-server/gaxf-system/src/main/java/com/gaxf/system/service/IXfApproveRuntimeService.java \
  gaxf-server/gaxf-system/src/main/java/com/gaxf/system/service/impl/XfApproveRuntimeServiceImpl.java \
  gaxf-server/gaxf-system/src/main/java/com/gaxf/system/mapper/XfApproveInstanceMapper.java \
  gaxf-server/gaxf-system/src/main/resources/mapper/system/XfApproveInstanceMapper.xml
git commit -m "feat: start branch approval on work order handover"
```

### Task 5: Wire handover transaction to approval startup and exact error reporting

**Files:**
- Modify: `gaxf-server/gaxf-system/src/main/java/com/gaxf/system/service/impl/XfWorkOrderServiceImpl.java`
- Modify: `gaxf-server/gaxf-system/src/main/java/com/gaxf/system/service/IXfWorkOrderService.java`
- Modify: `gaxf-server/gaxf-admin/src/main/java/com/gaxf/web/controller/xf/XfWorkOrderController.java`

- [ ] **Step 1: Preserve the existing handover input contract and call the new approval runtime before final success**

```java
xfApproveRuntimeService.startAssignApprove(orderId, deptIds, deptNames, SecurityUtils.getUsername());
```

- [ ] **Step 2: Keep handover writes inside one transaction and update status only after approval instance creation succeeds**

```java
XfWorkOrder updateOrder = new XfWorkOrder();
updateOrder.setId(orderId);
updateOrder.setStatus("6");
if (StringUtils.isNotEmpty(deadline))
{
    updateOrder.setDeadline(DateUtils.parseDate(deadline));
}
xfWorkOrderMapper.updateXfWorkOrder(updateOrder);
```

- [ ] **Step 3: Continue inserting assign records and messages only after config validation passes**

```java
for (int i = 0; i < deptIds.length; i++)
{
    XfAssignRecord record = new XfAssignRecord();
    record.setOrderId(orderId);
    record.setAssignDeptId(deptIds[i]);
    record.setAssignDeptName(deptNames != null && i < deptNames.length ? deptNames[i] : null);
    record.setAssignType("0");
    record.setAssignTime(new Date());
    record.setStatus("0");
    xfAssignRecordMapper.insertXfAssignRecord(record);
}
```

- [ ] **Step 4: Let the controller surface exact missing-config errors unchanged**

```java
@PostMapping("/assign")
public AjaxResult assign(@RequestBody AssignBody body)
{
    xfWorkOrderService.assignWorkOrder(body.getOrderId(), body.getDeptIds(), body.getDeadline(), body.getDeptNames());
    return AjaxResult.success("交办并发起审核成功");
}
```

- [ ] **Step 5: Verify atomic failure behavior manually with one unmapped undertaking department**

Run: `cd gaxf-server && mvn -pl gaxf-admin -am -DskipTests package`
Expected: backend packages successfully; when tested manually later, a missing mapping should return one ServiceException message and no assign record / no approval instance / no status update.

- [ ] **Step 6: Commit**

```bash
git add gaxf-server/gaxf-system/src/main/java/com/gaxf/system/service/IXfWorkOrderService.java \
  gaxf-server/gaxf-system/src/main/java/com/gaxf/system/service/impl/XfWorkOrderServiceImpl.java \
  gaxf-server/gaxf-admin/src/main/java/com/gaxf/web/controller/xf/XfWorkOrderController.java
git commit -m "feat: make handover approval startup atomic"
```

### Task 6: Return grouped branch progress in work-order detail and approval todo APIs

**Files:**
- Modify: `gaxf-server/gaxf-system/src/main/java/com/gaxf/system/domain/XfWorkOrder.java`
- Modify: `gaxf-server/gaxf-system/src/main/java/com/gaxf/system/domain/vo/XfApproveProgressVO.java`
- Modify: `gaxf-server/gaxf-system/src/main/resources/mapper/system/XfApproveInstanceMapper.xml`
- Modify: `gaxf-server/gaxf-system/src/main/java/com/gaxf/system/service/impl/XfApproveRuntimeServiceImpl.java`
- Modify: `gaxf-server/gaxf-system/src/main/java/com/gaxf/system/service/impl/XfWorkOrderServiceImpl.java`

- [ ] **Step 1: Add branch group fields for detail rendering**

```java
private List<XfApproveTask> branchTaskList;
private List<XfApproveTask> summaryTaskList;
```

- [ ] **Step 2: Split approval tasks into branch and summary groups in progress assembly**

```java
List<XfApproveTask> taskList = xfApproveInstanceMapper.selectTasksByInstanceId(instance.getId());
progressVO.setTaskList(taskList);
progressVO.setBranchTaskList(taskList.stream()
    .filter(task -> "0".equals(task.getTaskType()))
    .collect(Collectors.toList()));
progressVO.setSummaryTaskList(taskList.stream()
    .filter(task -> "1".equals(task.getTaskType()))
    .collect(Collectors.toList()));
```

- [ ] **Step 3: Include undertaking department snapshot columns in task selects**

```xml
<result property="taskType" column="task_type"/>
<result property="undertakeDeptId" column="undertake_dept_id"/>
<result property="undertakeDeptName" column="undertake_dept_name"/>
```

- [ ] **Step 4: Surface grouped progress on work-order detail objects**

```java
workOrder.setApproveInstanceId(progressVO.getInstanceId());
workOrder.setApproveStatus(progressVO.getStatus());
workOrder.setCurrentApproveStage(progressVO.getCurrentStageNo());
workOrder.setApproveTaskList(progressVO.getTaskList());
workOrder.setBranchTaskList(progressVO.getBranchTaskList());
```

- [ ] **Step 5: Verify backend serialization compiles**

Run: `cd gaxf-server && mvn -pl gaxf-system -am -DskipTests compile`
Expected: progress VO and work-order serialization compile with the new branch/summary lists.

- [ ] **Step 6: Commit**

```bash
git add gaxf-server/gaxf-system/src/main/java/com/gaxf/system/domain/XfWorkOrder.java \
  gaxf-server/gaxf-system/src/main/java/com/gaxf/system/domain/vo/XfApproveProgressVO.java \
  gaxf-server/gaxf-system/src/main/resources/mapper/system/XfApproveInstanceMapper.xml \
  gaxf-server/gaxf-system/src/main/java/com/gaxf/system/service/impl/XfApproveRuntimeServiceImpl.java \
  gaxf-server/gaxf-system/src/main/java/com/gaxf/system/service/impl/XfWorkOrderServiceImpl.java
git commit -m "feat: expose branch approval progress"
```

### Task 7: Update approval-config frontend for branch mappings and summary stages

**Files:**
- Modify: `gaxf-ui/src/types/api/xf/approve.ts`
- Modify: `gaxf-ui/src/api/xf/approve.ts`
- Modify: `gaxf-ui/src/views/xf/approveConfig/index.vue`

- [ ] **Step 1: Add frontend types for branch mappings and stage type**

```ts
export interface ApproveFlowBranch {
  id?: number
  flowConfigId?: number
  undertakeDeptId?: number
  undertakeDeptName?: string
  firstApproveDeptId?: number
  firstApproveDeptName?: string
}

export interface ApproveFlowStage {
  id?: number
  flowConfigId?: number
  stageNo?: number
  stageName?: string
  stageType?: string
  parallelMode?: string
  deptList?: ApproveFlowStageDept[]
}
```

- [ ] **Step 2: Extend the config form model with branchList**

```ts
const form = ref<ApproveFlowConfig>({
  id: undefined,
  flowName: '',
  applyDeptId: undefined,
  applyDeptName: '',
  status: '0',
  branchList: [],
  stages: []
})
```

- [ ] **Step 3: Add a dedicated UI block for undertaking -> first-review mappings**

```vue
<el-card shadow="never" class="stage-card">
  <template #header>
    <div class="stage-card-header">
      <span>承办单位首审映射</span>
      <el-button type="primary" link @click="handleAddBranch">新增映射</el-button>
    </div>
  </template>
  <div v-for="(branch, index) in form.branchList" :key="index" class="branch-row">
    <!-- 承办单位选择 + 首审部门选择 + 删除按钮 -->
  </div>
</el-card>
```

- [ ] **Step 4: Label the existing stages as shared summary stages in the editor**

```ts
function handleAddStage() {
  form.value.stages!.push({
    stageNo: form.value.stages!.length + 1,
    stageName: '',
    stageType: '1',
    parallelMode: '1',
    deptList: []
  })
}
```

- [ ] **Step 5: Verify the frontend builds with the new config form types**

Run: `cd gaxf-ui && npm run build`
Expected: approval config page compiles with branch mapping form fields and shared-stage labels.

- [ ] **Step 6: Commit**

```bash
git add gaxf-ui/src/types/api/xf/approve.ts \
  gaxf-ui/src/api/xf/approve.ts \
  gaxf-ui/src/views/xf/approveConfig/index.vue
git commit -m "feat: configure branch handover approval mappings"
```

### Task 8: Update work-order and approval UI for branch progress and atomic handover errors

**Files:**
- Modify: `gaxf-ui/src/types/api/xf/workOrder.ts`
- Modify: `gaxf-ui/src/api/xf/workOrder.ts`
- Modify: `gaxf-ui/src/views/xf/workOrder/index.vue`
- Modify: `gaxf-ui/src/views/xf/workOrder/detail.vue`
- Modify: `gaxf-ui/src/views/xf/approveTodo/index.vue`

- [ ] **Step 1: Extend work-order/approve task types with branch fields**

```ts
export interface ApproveTask {
  id?: number
  taskType?: string
  undertakeDeptId?: number
  undertakeDeptName?: string
  approveDeptId?: number
  approveDeptName?: string
  status?: string
}

export interface XfWorkOrder {
  branchTaskList?: ApproveTask[]
}
```

- [ ] **Step 2: Show exact backend error text when handover fails**

```ts
try {
  await assignWorkOrder(assignForm.value)
  ElMessage.success('交办并发起审核成功')
} catch (error: any) {
  ElMessage.error(error?.msg || error?.message || '交办失败')
}
```

- [ ] **Step 3: Render branch first-review progress separately from summary stages on detail page**

```vue
<el-card v-if="workOrder.branchTaskList?.length" shadow="never" class="detail-card">
  <template #header><span>承办单位首审</span></template>
  <div v-for="task in workOrder.branchTaskList" :key="task.id" class="approve-node">
    <span>{{ task.undertakeDeptName }} → {{ task.approveDeptName }}</span>
    <el-tag>{{ getApproveStatusText(task.status) }}</el-tag>
  </div>
</el-card>
```

- [ ] **Step 4: Show undertaking department context on the approval todo page**

```vue
<el-table-column prop="undertakeDeptName" label="承办单位" width="160" />
<el-table-column prop="approveDeptName" label="当前审核部门" width="160" />
```

- [ ] **Step 5: Build the frontend and manually inspect the changed pages**

Run: `cd gaxf-ui && npm run build`
Expected: the work-order list/detail and approval todo pages compile and render branch-aware fields with no TypeScript errors.

- [ ] **Step 6: Commit**

```bash
git add gaxf-ui/src/types/api/xf/workOrder.ts \
  gaxf-ui/src/api/xf/workOrder.ts \
  gaxf-ui/src/views/xf/workOrder/index.vue \
  gaxf-ui/src/views/xf/workOrder/detail.vue \
  gaxf-ui/src/views/xf/approveTodo/index.vue
git commit -m "feat: show branch approval progress in work order ui"
```

### Task 9: End-to-end verification of branch handover approval

**Files:**
- Modify: `docs/superpowers/plans/2026-06-06-workorder-handover-branch-approval.md`

- [ ] **Step 1: Verify backend package build**

Run: `cd gaxf-server && mvn -pl gaxf-admin -am -DskipTests package`
Expected: BUILD SUCCESS and generated mapper resources include branch approval SQL mappings.

- [ ] **Step 2: Verify frontend production build**

Run: `cd gaxf-ui && npm run build`
Expected: Vite build succeeds and no branch-approval TypeScript errors remain.

- [ ] **Step 3: Run manual happy-path test**

Run: `# Start backend and frontend using the project's normal dev commands, then in the UI: create or choose a 待派单工单 -> select two configured 承办单位 -> click 交办 -> verify one approval instance with two 首审任务 is created -> approve both -> verify进入第一个汇总阶段`
Expected: handover succeeds once, the work order enters 审批中, and detail/todo pages show branch tasks then summary tasks.

- [ ] **Step 4: Run manual failure-path test**

Run: `# In the UI, select one configured 承办单位 and one未配置承办单位 -> click 交办`
Expected: the UI shows the exact missing-config department names; the work-order status, assign records, and approval instance/task tables remain unchanged.

- [ ] **Step 5: Run manual rejection-path test**

Run: `# Start a new approval instance, reject any one 承办单位首审任务`
Expected: the instance becomes 已驳回, no汇总阶段任务 is generated, and the work order status becomes 已退回.

- [ ] **Step 6: Commit the verified plan notes if you updated them during execution**

```bash
git add docs/superpowers/plans/2026-06-06-workorder-handover-branch-approval.md
git commit -m "docs: record branch handover approval verification"
```

---

## Self-Review

- Spec coverage: covered multi-undertaking handover, config-driven undertaking->first-review mapping, atomic failure on missing mapping, branch convergence into shared summary stages, rejection behavior, and UI progress visibility.
- Placeholder scan: removed TODO/TBD language from task steps; each code step includes concrete snippets/commands.
- Type consistency: `branchList`, `stageType`, `taskType`, `undertakeDeptId`, and `undertakeDeptName` are used consistently across schema/domain/mapper/service/UI tasks.
