-- 问题整改模块菜单脚本
-- 执行前请确认 SPDC 父菜单 ID（假设为 2001，若不存在请先创建）
-- 菜单 ID 从 3000 开始，避免与现有菜单冲突

-- 一级目录：SPDC 业务管理（若已存在则跳过）
-- INSERT INTO sys_menu VALUES(2001, '业务管理', '0', '5', 'spdc', null, '', '', 1, 0, 'M', '0', '0', '', 'monitor', 'admin', sysdate(), '', null, 'SPDC业务管理目录');

-- 二级菜单：问题整改
INSERT INTO sys_menu VALUES(3000, '整改反馈', 2001, '3', 'pga', 'spdc/pga/index', '', '', 1, 0, 'C', '0', '0', 'spdc:pga:list', 'edit', 'admin', sysdate(), '', null, '问题整改菜单');

-- 三级按钮：问题整改操作权限
INSERT INTO sys_menu VALUES(3001, '问题整改查询', 3000, '1', '', '', '', '', 1, 0, 'F', '0', '0', 'spdc:pga:query', '#', 'admin', sysdate(), '', null, '');
INSERT INTO sys_menu VALUES(3002, '问题整改新增', 3000, '2', '', '', '', '', 1, 0, 'F', '0', '0', 'spdc:pga:add', '#', 'admin', sysdate(), '', null, '');
INSERT INTO sys_menu VALUES(3003, '问题整改修改', 3000, '3', '', '', '', '', 1, 0, 'F', '0', '0', 'spdc:pga:edit', '#', 'admin', sysdate(), '', null, '');
INSERT INTO sys_menu VALUES(3004, '问题整改删除', 3000, '4', '', '', '', '', 1, 0, 'F', '0', '0', 'spdc:pga:remove', '#', 'admin', sysdate(), '', null, '');
INSERT INTO sys_menu VALUES(3005, '整改反馈提交', 3000, '5', '', '', '', '', 1, 0, 'F', '0', '0', 'spdc:pga:feedback', '#', 'admin', sysdate(), '', null, '');
INSERT INTO sys_menu VALUES(3006, '问题整改导出', 3000, '6', '', '', '', '', 1, 0, 'F', '0', '0', 'spdc:pga:export', '#', 'admin', sysdate(), '', null, '');

-- 预警管理处置权限（若预警管理菜单已存在，只需添加按钮权限）
-- 假设预警管理菜单 ID 为 2002
-- INSERT INTO sys_menu VALUES(3007, '预警处置', 2002, '5', '', '', '', '', 1, 0, 'F', '0', '0', 'spdc:alert:handle', '#', 'admin', sysdate(), '', null, '预警处置按钮');

-- 注意事项：
-- 1. 请先查询现有 SPDC 父菜单 ID: SELECT menu_id FROM sys_menu WHERE menu_name = '业务管理' OR path = 'spdc';
-- 2. 若父菜单不存在，请先执行第一条 INSERT 创建父目录
-- 3. 若预警管理菜单已存在，请查询其 menu_id 并调整 3007 的 parent_id