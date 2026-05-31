-- 修复 xf_work_order 字段长度不足问题
-- 适用于 Excel 导入数据超长的情况

ALTER TABLE xf_work_order
  MODIFY COLUMN xf_case_status       VARCHAR(100)  COMMENT '信访件状态',
  MODIFY COLUMN xf_form              VARCHAR(100)  COMMENT '信访形式',
  MODIFY COLUMN business_category    VARCHAR(200)  COMMENT '公安业务分类',
  MODIFY COLUMN handle_method        VARCHAR(200)  COMMENT '办理方式',
  MODIFY COLUMN completion_status    VARCHAR(100)  COMMENT '办结状态',
  MODIFY COLUMN xf_purpose           VARCHAR(500)  COMMENT '信访目的(登记单位)',
  MODIFY COLUMN police_type_name     VARCHAR(200)  COMMENT '警种名称',
  MODIFY COLUMN archive_no           VARCHAR(100)  COMMENT '档案编号',
  MODIFY COLUMN xf_item_no           VARCHAR(100)  COMMENT '信访事项编号',
  MODIFY COLUMN transfer_dest        VARCHAR(200)  COMMENT '转往处',
  MODIFY COLUMN specific_handle_unit VARCHAR(200)  COMMENT '具体承办单位',
  MODIFY COLUMN responsible_dept     VARCHAR(200)  COMMENT '责任部门',
  MODIFY COLUMN register_unit        VARCHAR(200)  COMMENT '登记单位',
  MODIFY COLUMN problem_location     VARCHAR(500)  COMMENT '问题发生地',
  MODIFY COLUMN abnormal_dynamic     VARCHAR(500)  COMMENT '异常动态',
  MODIFY COLUMN petitioner_address   VARCHAR(500)  COMMENT '住址';
