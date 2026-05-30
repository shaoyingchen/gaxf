-- 为 dc_pga 表添加 dxjg_id 字段，用于关联预警记录
-- 执行时间：2026-04-30

ALTER TABLE `dc_pga` ADD COLUMN `dxjg_id` INT(0) NULL DEFAULT NULL COMMENT '关联预警ID' AFTER `id`;

-- 添加索引，方便查询关联
ALTER TABLE `dc_pga` ADD INDEX `idx_dxjg_id` (`dxjg_id`);