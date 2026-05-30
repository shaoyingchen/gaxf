-- 预警处置相关字典类型
INSERT INTO `sys_dict_type` VALUES (200, '处置方式', 'spdc_deal_type', '0', 'admin', NOW(), '', NULL, '预警处置方式');
INSERT INTO `sys_dict_type` VALUES (201, '问题性质', 'spdc_wtxz', '0', 'admin', NOW(), '', NULL, '预警问题性质');
INSERT INTO `sys_dict_type` VALUES (202, '整改要求', 'spdc_adjust', '0', 'admin', NOW(), '', NULL, '预警整改要求');
INSERT INTO `sys_dict_type` VALUES (203, '处理意见', 'spdc_deal_propose', '0', 'admin', NOW(), '', NULL, '预警处理意见');

-- 处置方式字典数据
INSERT INTO `sys_dict_data` VALUES (200, 1, '发送短信', '1', 'spdc_deal_type', '', 'primary', 'Y', '0', 'admin', NOW(), '', NULL, '发送短信通知');
INSERT INTO `sys_dict_data` VALUES (201, 2, '电话通知', '2', 'spdc_deal_type', '', 'warning', 'N', '0', 'admin', NOW(), '', NULL, '电话通知');

-- 问题性质字典数据
INSERT INTO `sys_dict_data` VALUES (202, 1, '一般问题', '1', 'spdc_wtxz', '', 'info', 'Y', '0', 'admin', NOW(), '', NULL, '一般问题');
INSERT INTO `sys_dict_data` VALUES (203, 2, '重大问题', '2', 'spdc_wtxz', '', 'danger', 'N', '0', 'admin', NOW(), '', NULL, '重大问题');

-- 整改要求字典数据
INSERT INTO `sys_dict_data` VALUES (204, 1, '限期整改', '1', 'spdc_adjust', '', 'warning', 'Y', '0', 'admin', NOW(), '', NULL, '限期整改');
INSERT INTO `sys_dict_data` VALUES (205, 2, '当场整改', '2', 'spdc_adjust', '', 'success', 'N', '0', 'admin', NOW(), '', NULL, '当场整改');

-- 处理意见字典数据
INSERT INTO `sys_dict_data` VALUES (206, 1, '提醒教育', '1', 'spdc_deal_propose', '', 'info', 'Y', '0', 'admin', NOW(), '', NULL, '提醒教育');
INSERT INTO `sys_dict_data` VALUES (207, 2, '带离现场处置', '2', 'spdc_deal_propose', '', 'warning', 'N', '0', 'admin', NOW(), '', NULL, '带离现场处置');
INSERT INTO `sys_dict_data` VALUES (208, 3, '当场纠正', '3', 'spdc_deal_propose', '', 'primary', 'N', '0', 'admin', NOW(), '', NULL, '当场纠正');