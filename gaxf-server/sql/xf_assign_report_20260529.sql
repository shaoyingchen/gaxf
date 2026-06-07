-- ========================================
-- 交办记录表增加办理报告相关字段
-- ========================================
ALTER TABLE xf_assign_record
    ADD COLUMN deadline DATETIME DEFAULT NULL COMMENT '限期报送截止时间' AFTER receive_time,
  ADD COLUMN report_content TEXT DEFAULT NULL COMMENT '办理意见' AFTER deadline,
  ADD COLUMN report_attachment VARCHAR(500) DEFAULT NULL COMMENT '办理报告附件路径' AFTER report_content,
  ADD COLUMN report_time DATETIME DEFAULT NULL COMMENT '上报时间' AFTER report_attachment;
