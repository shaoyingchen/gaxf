-- 预警分类字典数据 (kind=3)
-- 预警级别和预警类型

-- 预警级别（父节点）
INSERT INTO `dc_dict` (`id`, `parent_id`, `name`, `kind`, `sort`, `deleted`, `create_by`, `create_time`) VALUES
(301, 0, '高危预警', 3, 1, 0, 'admin', NOW()),
(302, 0, '中危预警', 3, 2, 0, 'admin', NOW()),
(303, 0, '低危预警', 3, 3, 0, 'admin', NOW());

-- 高危预警类型（子节点）
INSERT INTO `dc_dict` (`id`, `parent_id`, `name`, `kind`, `sort`, `deleted`, `create_by`, `create_time`) VALUES
(311, 301, '设备故障', 3, 1, 0, 'admin', NOW()),
(312, 301, '信号丢失', 3, 2, 0, 'admin', NOW()),
(313, 301, '非法入侵', 3, 3, 0, 'admin', NOW()),
(314, 301, '异常聚集', 3, 4, 0, 'admin', NOW());

-- 中危预警类型（子节点）
INSERT INTO `dc_dict` (`id`, `parent_id`, `name`, `kind`, `sort`, `deleted`, `create_by`, `create_time`) VALUES
(321, 302, '画面异常', 3, 1, 0, 'admin', NOW()),
(322, 302, '遮挡预警', 3, 2, 0, 'admin', NOW()),
(323, 302, '离线预警', 3, 3, 0, 'admin', NOW()),
(324, 302, '存储异常', 3, 4, 0, 'admin', NOW());

-- 低危预警类型（子节点）
INSERT INTO `dc_dict` (`id`, `parent_id`, `name`, `kind`, `sort`, `deleted`, `create_by`, `create_time`) VALUES
(331, 303, '常规巡检', 3, 1, 0, 'admin', NOW()),
(332, 303, '维护提醒', 3, 2, 0, 'admin', NOW()),
(333, 303, '系统通知', 3, 3, 0, 'admin', NOW());