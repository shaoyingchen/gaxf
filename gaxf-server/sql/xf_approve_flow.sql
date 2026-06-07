-- =============================================
-- 部门审批流配置表及菜单脚本
-- =============================================

-- ========================================
-- 审批流配置相关表
-- ========================================

-- 1. 审批流配置表
DROP TABLE IF EXISTS xf_approve_flow_config;
CREATE TABLE xf_approve_flow_config (
  id                   BIGINT       NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  flow_name            VARCHAR(100) NOT NULL                COMMENT '审批流名称',
  apply_dept_id        BIGINT       NOT NULL                COMMENT '申请部门ID',
  apply_dept_name      VARCHAR(100) NOT NULL                COMMENT '申请部门名称',
  status               CHAR(1)      DEFAULT '0'             COMMENT '状态: 0=启用 1=停用',
  remark               VARCHAR(500) DEFAULT NULL            COMMENT '备注',
  create_by            VARCHAR(64)  DEFAULT ''              COMMENT '创建者',
  create_time          DATETIME                             COMMENT '创建时间',
  update_by            VARCHAR(64)  DEFAULT ''              COMMENT '更新者',
  update_time          DATETIME                             COMMENT '更新时间',
  PRIMARY KEY (id),
  UNIQUE KEY uk_apply_dept_id (apply_dept_id),
  KEY idx_status (status)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8mb4 COMMENT='审批流配置表';

-- 2. 审批流承办单位分支映射表
DROP TABLE IF EXISTS xf_approve_flow_branch;
CREATE TABLE xf_approve_flow_branch (
  id                    BIGINT       NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  flow_config_id        BIGINT       NOT NULL                COMMENT '审批流配置ID',
  undertake_dept_id     BIGINT       NOT NULL                COMMENT '承办单位ID',
  undertake_dept_name   VARCHAR(100) NOT NULL                COMMENT '承办单位名称',
  first_approve_dept_id BIGINT       NOT NULL                COMMENT '首审部门ID',
  first_approve_dept_name VARCHAR(100) NOT NULL              COMMENT '首审部门名称',
  create_by             VARCHAR(64)  DEFAULT ''              COMMENT '创建者',
  create_time           DATETIME                             COMMENT '创建时间',
  update_by             VARCHAR(64)  DEFAULT ''              COMMENT '更新者',
  update_time           DATETIME                             COMMENT '更新时间',
  remark                VARCHAR(500) DEFAULT NULL            COMMENT '备注',
  PRIMARY KEY (id),
  UNIQUE KEY uk_flow_config_undertake_dept (flow_config_id, undertake_dept_id),
  KEY idx_flow_config_id (flow_config_id),
  KEY idx_undertake_dept_id (undertake_dept_id),
  KEY idx_first_approve_dept_id (first_approve_dept_id)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8mb4 COMMENT='审批流承办单位分支映射表';

-- 3. 审批流阶段表
DROP TABLE IF EXISTS xf_approve_flow_stage;
CREATE TABLE xf_approve_flow_stage (
  id                   BIGINT       NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  flow_config_id       BIGINT       NOT NULL                COMMENT '审批流配置ID',
  stage_no             INT          NOT NULL                COMMENT '阶段序号',
  stage_name           VARCHAR(100) NOT NULL                COMMENT '阶段名称',
  stage_type           CHAR(1)      DEFAULT '1'             COMMENT '阶段类型: 1=汇总阶段',
  parallel_mode        CHAR(1)      DEFAULT '1'             COMMENT '并行模式: 1=并行',
  PRIMARY KEY (id),
  UNIQUE KEY uk_flow_stage_no (flow_config_id, stage_no),
  KEY idx_flow_config_id (flow_config_id)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8mb4 COMMENT='审批流阶段表';

-- 3. 审批流阶段部门表
DROP TABLE IF EXISTS xf_approve_flow_stage_dept;
CREATE TABLE xf_approve_flow_stage_dept (
  id                   BIGINT       NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  stage_id             BIGINT       NOT NULL                COMMENT '阶段ID',
  approve_dept_id      BIGINT       NOT NULL                COMMENT '审批部门ID',
  approve_dept_name    VARCHAR(100) NOT NULL                COMMENT '审批部门名称',
  PRIMARY KEY (id),
  UNIQUE KEY uk_stage_approve_dept (stage_id, approve_dept_id),
  KEY idx_stage_id (stage_id),
  KEY idx_approve_dept_id (approve_dept_id)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8mb4 COMMENT='审批流阶段部门表';

-- 4. 审批实例表
DROP TABLE IF EXISTS xf_approve_instance;
CREATE TABLE xf_approve_instance (
  id                   BIGINT       NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  order_id             BIGINT       NOT NULL                COMMENT '工单ID',
  flow_config_id       BIGINT       NOT NULL                COMMENT '审批流配置ID',
  flow_name            VARCHAR(100) NOT NULL                COMMENT '审批流名称',
  apply_dept_id        BIGINT       NOT NULL                COMMENT '申请部门ID',
  apply_dept_name      VARCHAR(100) NOT NULL                COMMENT '申请部门名称',
  current_stage_no     INT          DEFAULT 1               COMMENT '当前阶段序号',
  status               CHAR(1)      DEFAULT '0'             COMMENT '实例状态: 0=审批中 1=已通过 2=已驳回',
  start_time           DATETIME                             COMMENT '开始时间',
  finish_time          DATETIME                             COMMENT '完成时间',
  create_by            VARCHAR(64)  DEFAULT ''              COMMENT '创建者',
  create_time          DATETIME                             COMMENT '创建时间',
  update_by            VARCHAR(64)  DEFAULT ''              COMMENT '更新者',
  update_time          DATETIME                             COMMENT '更新时间',
  PRIMARY KEY (id),
  KEY idx_order_id (order_id),
  KEY idx_flow_config_id (flow_config_id),
  KEY idx_status (status)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8mb4 COMMENT='审批实例表';

-- 5. 审批任务表
DROP TABLE IF EXISTS xf_approve_task;
CREATE TABLE xf_approve_task (
  id                   BIGINT       NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  instance_id          BIGINT       NOT NULL                COMMENT '审批实例ID',
  order_id             BIGINT       NOT NULL                COMMENT '工单ID',
  stage_no             INT          NOT NULL                COMMENT '阶段序号',
  stage_name           VARCHAR(100) NOT NULL                COMMENT '阶段名称',
  task_type            CHAR(1)      DEFAULT '0'             COMMENT '任务类型: 0=承办分支首审 1=汇总阶段',
  undertake_dept_id    BIGINT                               COMMENT '承办单位ID快照',
  undertake_dept_name  VARCHAR(100)                         COMMENT '承办单位名称快照',
  approve_dept_id      BIGINT       NOT NULL                COMMENT '审批部门ID',
  approve_dept_name    VARCHAR(100) NOT NULL                COMMENT '审批部门名称',
  reviewer_id          BIGINT                               COMMENT '审批人ID',
  reviewer_name        VARCHAR(64)                          COMMENT '审批人姓名',
  opinion              VARCHAR(500)                         COMMENT '审批意见',
  status               CHAR(1)      DEFAULT '0'             COMMENT '任务状态: 0=待处理 1=已通过 2=已驳回',
  action_time          DATETIME                             COMMENT '处理时间',
  create_by            VARCHAR(64)  DEFAULT ''              COMMENT '创建者',
  create_time          DATETIME                             COMMENT '创建时间',
  update_by            VARCHAR(64)  DEFAULT ''              COMMENT '更新者',
  update_time          DATETIME                             COMMENT '更新时间',
  PRIMARY KEY (id),
  UNIQUE KEY uk_instance_stage_dept (instance_id, stage_no, approve_dept_id),
  KEY idx_instance_id (instance_id),
  KEY idx_order_id (order_id),
  KEY idx_status (status),
  KEY idx_reviewer_id (reviewer_id)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8mb4 COMMENT='审批任务表';

-- ========================================
-- 审批流菜单及权限
-- ========================================

DELETE FROM sys_role_menu WHERE menu_id IN (4800, 4801, 4802, 4803, 4804, 4810, 4811, 4812);
DELETE FROM sys_menu WHERE menu_id IN (4800, 4801, 4802, 4803, 4804, 4810, 4811, 4812);

-- 审批配置
INSERT INTO sys_menu VALUES (4800, '审批配置', 4000, 8, 'approveConfig', 'xf/approveConfig/index', '', '', 1, 0, 'C', '0', '0', 'xf:approveConfig:list', 'tree', 'admin', sysdate(), '', NULL, '审批配置菜单');
INSERT INTO sys_menu VALUES (4801, '审批配置查询', 4800, 1, '', '', '', '', 1, 0, 'F', '0', '0', 'xf:approveConfig:query',  '#', 'admin', sysdate(), '', NULL, '');
INSERT INTO sys_menu VALUES (4802, '审批配置新增', 4800, 2, '', '', '', '', 1, 0, 'F', '0', '0', 'xf:approveConfig:add',    '#', 'admin', sysdate(), '', NULL, '');
INSERT INTO sys_menu VALUES (4803, '审批配置修改', 4800, 3, '', '', '', '', 1, 0, 'F', '0', '0', 'xf:approveConfig:edit',   '#', 'admin', sysdate(), '', NULL, '');
INSERT INTO sys_menu VALUES (4804, '审批配置删除', 4800, 4, '', '', '', '', 1, 0, 'F', '0', '0', 'xf:approveConfig:remove', '#', 'admin', sysdate(), '', NULL, '');

-- 审批待办
INSERT INTO sys_menu VALUES (4810, '审批待办', 4000, 9, 'approveTodo', 'xf/approveTodo/index', '', '', 1, 0, 'C', '0', '0', 'xf:approveTask:list', 'date', 'admin', sysdate(), '', NULL, '审批待办菜单');
INSERT INTO sys_menu VALUES (4811, '审批待办查询', 4810, 1, '', '', '', '', 1, 0, 'F', '0', '0', 'xf:approveTask:query',  '#', 'admin', sysdate(), '', NULL, '');
INSERT INTO sys_menu VALUES (4812, '审批处理',     4810, 2, '', '', '', '', 1, 0, 'F', '0', '0', 'xf:approveTask:action', '#', 'admin', sysdate(), '', NULL, '');

-- 给 admin 角色（role_id=1）授权审批流菜单
INSERT INTO sys_role_menu (role_id, menu_id) VALUES
(1, 4800),(1, 4801),(1, 4802),(1, 4803),(1, 4804),
(1, 4810),(1, 4811),(1, 4812);
