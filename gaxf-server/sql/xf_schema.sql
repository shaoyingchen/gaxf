-- =============================================
-- 信访事项交办反馈系统 数据库表结构
-- =============================================

-- 注意：本文件为全量初始化脚本，仅适用于全新库初始化。
-- 已有环境如已存在审批流相关表或业务数据，请勿直接重复执行本文件中的 DROP/CREATE 语句。
-- 下列条目仅用于标识既有环境所需同步变更项，增量迁移请使用独立 SQL 单独执行：
--   1) 新增表：xf_approve_flow_branch
--   2) 新增字段：xf_approve_flow_stage.stage_type
--   3) 新增字段：xf_approve_task.task_type、undertake_dept_id、undertake_dept_name

-- 1. 信访工单表
DROP TABLE IF EXISTS xf_work_order;
CREATE TABLE xf_work_order (
  id                   BIGINT       NOT NULL AUTO_INCREMENT COMMENT '工单ID',
  xf_case_no           VARCHAR(50)  NOT NULL                COMMENT '信访件编号',
  title                VARCHAR(200)                         COMMENT '工单标题/任务名称(手动新建)',
  source_type          CHAR(1)      DEFAULT '1'             COMMENT '来源类型: 0=Excel导入 1=手动新建',
  content              TEXT                                 COMMENT '工单内容/任务要求(富文本)',
  petitioner_name      VARCHAR(50)                          COMMENT '姓名',
  petitioner_idcard    VARCHAR(18)                          COMMENT '身份证号',
  petitioner_phone     VARCHAR(20)                          COMMENT '手机号码',
  petitioner_address   VARCHAR(500)                         COMMENT '住址',
  xf_content           TEXT                                 COMMENT '信访内容',
  register_time        DATETIME                             COMMENT '登记时间',
  deadline             DATETIME                             COMMENT '限办时间',
  xf_case_status       VARCHAR(100)                         COMMENT '信访件状态',
  xf_form              VARCHAR(100)                         COMMENT '信访形式',
  xf_date              DATETIME                             COMMENT '信访日期',
  problem_location     VARCHAR(500)                         COMMENT '问题发生地',
  abnormal_dynamic     VARCHAR(500)                         COMMENT '异常动态',
  problem_date         DATETIME                             COMMENT '问题发生日期',
  petitioner_count     INT                                  COMMENT '信访人数',
  xf_demand            TEXT                                 COMMENT '信访诉求',
  business_category    VARCHAR(200)                         COMMENT '公安业务分类',
  register_unit        VARCHAR(200)                         COMMENT '登记单位',
  archive_no           VARCHAR(100)                         COMMENT '档案编号',
  transfer_dest        VARCHAR(200)                         COMMENT '转往处',
  handle_method        VARCHAR(200)                         COMMENT '办理方式',
  specific_handle_unit VARCHAR(200)                         COMMENT '具体承办单位',
  completion_status    VARCHAR(100)                         COMMENT '办结状态',
  responsible_dept     VARCHAR(200)                         COMMENT '责任部门',
  completion_time      DATETIME                             COMMENT '办结时间',
  xf_item_no           VARCHAR(100)                         COMMENT '信访事项编号',
  police_type_name     VARCHAR(200)                         COMMENT '警种名称',
  xf_purpose           VARCHAR(500)                         COMMENT '信访目的(登记单位)',
  status               CHAR(1)      DEFAULT '0'             COMMENT '工单状态: 0=待派单 1=待提交 2=审批中 3=已办结 4=已退回 5=已超期',
  overdue_count        INT          DEFAULT 0               COMMENT '超期次数',
  is_locked            CHAR(1)      DEFAULT '0'             COMMENT '是否锁定: 0=否 1=是',
  is_top               CHAR(1)      DEFAULT '0'             COMMENT '是否置顶: 0=否 1=是',
  create_dept_id       BIGINT                               COMMENT '创建部门ID',
  del_flag             CHAR(1)      DEFAULT '0'             COMMENT '删除标志: 0=存在 2=删除',
  create_by            VARCHAR(64)  DEFAULT ''              COMMENT '创建者',
  create_time          DATETIME                             COMMENT '创建时间',
  update_by            VARCHAR(64)  DEFAULT ''              COMMENT '更新者',
  update_time          DATETIME                             COMMENT '更新时间',
  remark               VARCHAR(500) DEFAULT NULL             COMMENT '备注',
  PRIMARY KEY (id),
  UNIQUE KEY idx_xf_case_no (xf_case_no)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8mb4 COMMENT='信访工单表';

-- 2. 交办记录表
DROP TABLE IF EXISTS xf_assign_record;
CREATE TABLE xf_assign_record (
  id              BIGINT       NOT NULL AUTO_INCREMENT COMMENT '交办记录ID',
  order_id        BIGINT       NOT NULL                COMMENT '工单ID',
  assign_dept_id  BIGINT                               COMMENT '承办单位ID',
  assign_dept_name VARCHAR(100)                         COMMENT '承办单位名称',
  assign_type     CHAR(1)      DEFAULT '0'             COMMENT '交办类型: 0=县局 1=市局警种',
  assign_time     DATETIME                             COMMENT '交办时间',
  assign_by       VARCHAR(64)                          COMMENT '交办人',
  status          CHAR(1)      DEFAULT '0'             COMMENT '状态: 0=待接收 1=办理中 2=已提交 3=审核通过 4=退回',
  receive_time    DATETIME                             COMMENT '接收时间',
  create_by       VARCHAR(64)  DEFAULT ''              COMMENT '创建者',
  create_time     DATETIME                             COMMENT '创建时间',
  update_by       VARCHAR(64)  DEFAULT ''              COMMENT '更新者',
  update_time     DATETIME                             COMMENT '更新时间',
  remark          VARCHAR(500) DEFAULT NULL             COMMENT '备注',
  PRIMARY KEY (id),
  KEY idx_order_id (order_id)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8mb4 COMMENT='交办记录表';

-- 3. 办理报告表
DROP TABLE IF EXISTS xf_handling_report;
CREATE TABLE xf_handling_report (
  id              BIGINT       NOT NULL AUTO_INCREMENT COMMENT '报告ID',
  order_id        BIGINT       NOT NULL                COMMENT '工单ID',
  assign_id       BIGINT                               COMMENT '交办记录ID',
  report_dept_id  BIGINT                               COMMENT '办理单位ID',
  report_dept_name VARCHAR(100)                         COMMENT '办理单位名称',
  report_content  TEXT                                 COMMENT '办理报告内容',
  handling_opinion TEXT                                COMMENT '办理意见',
  report_by       VARCHAR(64)                          COMMENT '报告人',
  report_time     DATETIME                             COMMENT '报告时间',
  status          CHAR(1)      DEFAULT '0'             COMMENT '状态: 0=草稿 1=已提交 2=县局审核通过 3=退回',
  review_opinion  VARCHAR(500)                         COMMENT '审核意见',
  review_by       VARCHAR(64)                          COMMENT '审核人',
  review_time     DATETIME                             COMMENT '审核时间',
  create_by       VARCHAR(64)  DEFAULT ''              COMMENT '创建者',
  create_time     DATETIME                             COMMENT '创建时间',
  update_by       VARCHAR(64)  DEFAULT ''              COMMENT '更新者',
  update_time     DATETIME                             COMMENT '更新时间',
  remark          VARCHAR(500) DEFAULT NULL             COMMENT '备注',
  PRIMARY KEY (id),
  KEY idx_order_id (order_id),
  KEY idx_assign_id (assign_id)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8mb4 COMMENT='办理报告表';

-- 4. 审核记录表
DROP TABLE IF EXISTS xf_review_record;
CREATE TABLE xf_review_record (
  id              BIGINT       NOT NULL AUTO_INCREMENT COMMENT '审核记录ID',
  order_id        BIGINT       NOT NULL                COMMENT '工单ID',
  review_type     CHAR(1)      NOT NULL                COMMENT '审核类型: 0=县局领导审核 1=警种评查 2=专员审核 3=支队领导审核',
  reviewer_id     BIGINT                               COMMENT '审核人ID',
  reviewer_name   VARCHAR(64)                          COMMENT '审核人姓名',
  review_opinion  TEXT                                 COMMENT '审核意见',
  review_result   CHAR(1)      NOT NULL                COMMENT '审核结果: 0=通过 1=退回',
  return_to       CHAR(1)      DEFAULT NULL             COMMENT '退回对象: 0=县局重办 1=专员重审',
  review_time     DATETIME                             COMMENT '审核时间',
  dept_id         BIGINT                               COMMENT '审核人所属单位ID',
  create_by       VARCHAR(64)  DEFAULT ''              COMMENT '创建者',
  create_time     DATETIME                             COMMENT '创建时间',
  PRIMARY KEY (id),
  KEY idx_order_id (order_id)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8mb4 COMMENT='审核记录表';

-- 5. 电子卷宗表
DROP TABLE IF EXISTS xf_dossier;
CREATE TABLE xf_dossier (
  id              BIGINT       NOT NULL AUTO_INCREMENT COMMENT '卷宗ID',
  order_id        BIGINT       NOT NULL                COMMENT '工单ID',
  file_name       VARCHAR(200) NOT NULL                COMMENT '文件名',
  file_path       VARCHAR(500) NOT NULL                COMMENT '文件路径',
  file_type       VARCHAR(20)                         COMMENT '文件类型: PDF/IMG/ZIP',
  file_size       BIGINT                               COMMENT '文件大小(字节)',
  upload_by       VARCHAR(64)                          COMMENT '上传人',
  upload_time     DATETIME                             COMMENT '上传时间',
  del_flag        CHAR(1)      DEFAULT '0'             COMMENT '删除标志: 0=存在 2=删除',
  create_by       VARCHAR(64)  DEFAULT ''              COMMENT '创建者',
  create_time     DATETIME                             COMMENT '创建时间',
  update_by       VARCHAR(64)  DEFAULT ''              COMMENT '更新者',
  update_time     DATETIME                             COMMENT '更新时间',
  remark          VARCHAR(500) DEFAULT NULL             COMMENT '备注',
  PRIMARY KEY (id),
  KEY idx_order_id (order_id)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8mb4 COMMENT='电子卷宗表';

-- 6. 工单附件表
DROP TABLE IF EXISTS xf_work_order_attachment;
CREATE TABLE xf_work_order_attachment (
  id              BIGINT       NOT NULL AUTO_INCREMENT COMMENT '附件ID',
  order_id        BIGINT       NOT NULL                COMMENT '工单ID',
  file_name       VARCHAR(200) NOT NULL                COMMENT '文件名',
  file_path       VARCHAR(500) NOT NULL                COMMENT '文件路径',
  file_type       VARCHAR(20)                         COMMENT '文件类型',
  file_size       BIGINT                               COMMENT '文件大小(字节)',
  upload_by       VARCHAR(64)                          COMMENT '上传人',
  upload_time     DATETIME                             COMMENT '上传时间',
  del_flag        CHAR(1)      DEFAULT '0'             COMMENT '删除标志: 0=存在 2=删除',
  PRIMARY KEY (id),
  KEY idx_order_id (order_id)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8mb4 COMMENT='工单附件表';

-- 7. 文书表
DROP TABLE IF EXISTS xf_document;
CREATE TABLE xf_document (
  id              BIGINT       NOT NULL AUTO_INCREMENT COMMENT '文书ID',
  order_id        BIGINT       NOT NULL                COMMENT '工单ID',
  doc_type        CHAR(1)      NOT NULL                COMMENT '文书类型: 0=信访工作建议书 1=信访督办建议',
  doc_no          VARCHAR(50)                         COMMENT '文书编号',
  doc_content     TEXT                                 COMMENT '文书内容',
  sign_by         VARCHAR(64)                          COMMENT '签发人',
  sign_time       DATETIME                             COMMENT '签发时间',
  is_signed       CHAR(1)      DEFAULT '0'             COMMENT '是否已签: 0=否 1=是',
  file_path       VARCHAR(500)                         COMMENT '生成文件路径',
  create_by       VARCHAR(64)  DEFAULT ''              COMMENT '创建者',
  create_time     DATETIME                             COMMENT '创建时间',
  update_by       VARCHAR(64)  DEFAULT ''              COMMENT '更新者',
  update_time     DATETIME                             COMMENT '更新时间',
  remark          VARCHAR(500) DEFAULT NULL             COMMENT '备注',
  PRIMARY KEY (id),
  KEY idx_order_id (order_id)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8mb4 COMMENT='文书表';

-- 8. 站内消息表
DROP TABLE IF EXISTS xf_message;
CREATE TABLE xf_message (
  id              BIGINT       NOT NULL AUTO_INCREMENT COMMENT '消息ID',
  order_id        BIGINT                               COMMENT '关联工单ID',
  receiver_id     BIGINT       NOT NULL                COMMENT '接收人ID',
  title           VARCHAR(200) NOT NULL                COMMENT '消息标题',
  content         TEXT                                 COMMENT '消息内容',
  msg_type        CHAR(1)      DEFAULT '0'             COMMENT '消息类型: 0=交办通知 1=审核通知 2=超期预警 3=退回通知',
  is_read         CHAR(1)      DEFAULT '0'             COMMENT '是否已读: 0=否 1=是',
  read_time       DATETIME                             COMMENT '阅读时间',
  create_by       VARCHAR(64)  DEFAULT ''              COMMENT '创建者',
  create_time     DATETIME                             COMMENT '创建时间',
  PRIMARY KEY (id),
  KEY idx_receiver_id (receiver_id),
  KEY idx_order_id (order_id)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8mb4 COMMENT='站内消息表';

-- 9. 审批流程承办单位分支映射表
DROP TABLE IF EXISTS xf_approve_flow_branch;
CREATE TABLE xf_approve_flow_branch (
  id                    BIGINT       NOT NULL AUTO_INCREMENT COMMENT '分支映射ID',
  flow_config_id        BIGINT       NOT NULL                COMMENT '流程配置ID',
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
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8mb4 COMMENT='审批流程承办单位分支映射表';
