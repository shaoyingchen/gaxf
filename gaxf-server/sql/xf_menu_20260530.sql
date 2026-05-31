-- =============================================
-- 信访事项交办反馈系统 - 菜单数据
-- 菜单 ID 从 4000 开始，避免与现有菜单冲突
-- =============================================

-- 1. 先删除旧的 SPDC 菜单（视频督察相关）
DELETE FROM sys_menu WHERE menu_id IN (2000, 2001, 2002, 2003, 2004, 2005, 2006, 2007);
DELETE FROM sys_menu WHERE menu_id IN (3000, 3001, 3002, 3003, 3004, 3005, 3006);
DELETE FROM sys_role_menu WHERE menu_id IN (2000, 2001, 2002, 2003, 2004, 2005, 2006, 2007);
DELETE FROM sys_role_menu WHERE menu_id IN (3000, 3001, 3002, 3003, 3004, 3005, 3006);

-- 2. 一级目录：信访管理
INSERT INTO sys_menu VALUES (4000, '信访管理', 0, 1, 'xf', NULL, '', '', 1, 0, 'M', '0', '0', '', 'dict', 'admin', sysdate(), '', NULL, '信访事项交办反馈系统');

-- ========================================
-- 工单管理
-- ========================================
INSERT INTO sys_menu VALUES (4100, '工单管理', 4000, 1, 'workOrder', 'xf/workOrder/index', '', '', 1, 0, 'C', '0', '0', 'xf:workOrder:list', 'edit', 'admin', sysdate(), '', NULL, '工单管理菜单');
-- 工单管理按钮权限
INSERT INTO sys_menu VALUES (4101, '工单查询',   4100, 1, '', '', '', '', 1, 0, 'F', '0', '0', 'xf:workOrder:query',    '#', 'admin', sysdate(), '', NULL, '');
INSERT INTO sys_menu VALUES (4102, '工单新增',   4100, 2, '', '', '', '', 1, 0, 'F', '0', '0', 'xf:workOrder:add',      '#', 'admin', sysdate(), '', NULL, '');
INSERT INTO sys_menu VALUES (4103, '工单修改',   4100, 3, '', '', '', '', 1, 0, 'F', '0', '0', 'xf:workOrder:edit',     '#', 'admin', sysdate(), '', NULL, '');
INSERT INTO sys_menu VALUES (4104, '工单删除',   4100, 4, '', '', '', '', 1, 0, 'F', '0', '0', 'xf:workOrder:remove',   '#', 'admin', sysdate(), '', NULL, '');
INSERT INTO sys_menu VALUES (4105, '工单导出',   4100, 5, '', '', '', '', 1, 0, 'F', '0', '0', 'xf:workOrder:export',   '#', 'admin', sysdate(), '', NULL, '');
INSERT INTO sys_menu VALUES (4106, '工单导入',   4100, 6, '', '', '', '', 1, 0, 'F', '0', '0', 'xf:workOrder:import',   '#', 'admin', sysdate(), '', NULL, '');
INSERT INTO sys_menu VALUES (4107, '工单交办',   4100, 7, '', '', '', '', 1, 0, 'F', '0', '0', 'xf:workOrder:assign',   '#', 'admin', sysdate(), '', NULL, '');

-- ========================================
-- 我的待办
-- ========================================
INSERT INTO sys_menu VALUES (4200, '我的待办', 4000, 2, 'myTodo', 'xf/myTodo/index', '', '', 1, 0, 'C', '0', '0', 'xf:assign:list', 'date', 'admin', sysdate(), '', NULL, '我的待办菜单');
-- 待办按钮权限
INSERT INTO sys_menu VALUES (4201, '待办查询',   4200, 1, '', '', '', '', 1, 0, 'F', '0', '0', 'xf:assign:query',       '#', 'admin', sysdate(), '', NULL, '');
INSERT INTO sys_menu VALUES (4202, '接收工单',   4200, 2, '', '', '', '', 1, 0, 'F', '0', '0', 'xf:assign:receive',     '#', 'admin', sysdate(), '', NULL, '');
INSERT INTO sys_menu VALUES (4203, '提交报告',   4200, 3, '', '', '', '', 1, 0, 'F', '0', '0', 'xf:assign:submitReport','#', 'admin', sysdate(), '', NULL, '');
INSERT INTO sys_menu VALUES (4204, '县局审核',   4200, 4, '', '', '', '', 1, 0, 'F', '0', '0', 'xf:assign:countyReview','#', 'admin', sysdate(), '', NULL, '');

-- ========================================
-- 审核管理
-- ========================================
INSERT INTO sys_menu VALUES (4300, '审核管理', 4000, 3, 'review', 'xf/review/index', '', '', 1, 0, 'C', '0', '0', 'xf:review:list', 'checkbox', 'admin', sysdate(), '', NULL, '审核管理菜单');
-- 审核按钮权限
INSERT INTO sys_menu VALUES (4301, '审核查询',     4300, 1, '', '', '', '', 1, 0, 'F', '0', '0', 'xf:review:query',            '#', 'admin', sysdate(), '', NULL, '');
INSERT INTO sys_menu VALUES (4302, '警种评查',     4300, 2, '', '', '', '', 1, 0, 'F', '0', '0', 'xf:review:policeReview',     '#', 'admin', sysdate(), '', NULL, '');
INSERT INTO sys_menu VALUES (4303, '专员审核',     4300, 3, '', '', '', '', 1, 0, 'F', '0', '0', 'xf:review:commissionerReview','#', 'admin', sysdate(), '', NULL, '');
INSERT INTO sys_menu VALUES (4304, '领导审核',     4300, 4, '', '', '', '', 1, 0, 'F', '0', '0', 'xf:review:leaderReview',     '#', 'admin', sysdate(), '', NULL, '');

-- ========================================
-- 卷宗管理
-- ========================================
INSERT INTO sys_menu VALUES (4400, '卷宗管理', 4000, 4, 'dossier', 'xf/dossier/index', '', '', 1, 0, 'C', '0', '0', 'xf:dossier:list', 'documentation', 'admin', sysdate(), '', NULL, '卷宗管理菜单');
-- 卷宗按钮权限
INSERT INTO sys_menu VALUES (4401, '卷宗查询', 4400, 1, '', '', '', '', 1, 0, 'F', '0', '0', 'xf:dossier:query',  '#', 'admin', sysdate(), '', NULL, '');
INSERT INTO sys_menu VALUES (4402, '卷宗上传', 4400, 2, '', '', '', '', 1, 0, 'F', '0', '0', 'xf:dossier:add',    '#', 'admin', sysdate(), '', NULL, '');
INSERT INTO sys_menu VALUES (4403, '卷宗删除', 4400, 3, '', '', '', '', 1, 0, 'F', '0', '0', 'xf:dossier:remove', '#', 'admin', sysdate(), '', NULL, '');

-- ========================================
-- 文书管理
-- ========================================
INSERT INTO sys_menu VALUES (4500, '文书管理', 4000, 5, 'document', 'xf/document/index', '', '', 1, 0, 'C', '0', '0', 'xf:document:list', 'form', 'admin', sysdate(), '', NULL, '文书管理菜单');
-- 文书按钮权限
INSERT INTO sys_menu VALUES (4501, '文书查询', 4500, 1, '', '', '', '', 1, 0, 'F', '0', '0', 'xf:document:query',    '#', 'admin', sysdate(), '', NULL, '');
INSERT INTO sys_menu VALUES (4502, '文书生成', 4500, 2, '', '', '', '', 1, 0, 'F', '0', '0', 'xf:document:generate', '#', 'admin', sysdate(), '', NULL, '');
INSERT INTO sys_menu VALUES (4503, '文书签发', 4500, 3, '', '', '', '', 1, 0, 'F', '0', '0', 'xf:document:sign',     '#', 'admin', sysdate(), '', NULL, '');

-- ========================================
-- 查询统计
-- ========================================
INSERT INTO sys_menu VALUES (4600, '查询统计', 4000, 6, 'statistics', 'xf/statistics/index', '', '', 1, 0, 'C', '0', '0', 'xf:statistics:list', 'chart', 'admin', sysdate(), '', NULL, '查询统计菜单');
-- 统计按钮权限
INSERT INTO sys_menu VALUES (4601, '统计查询', 4600, 1, '', '', '', '', 1, 0, 'F', '0', '0', 'xf:statistics:query', '#', 'admin', sysdate(), '', NULL, '');
INSERT INTO sys_menu VALUES (4602, '统计导出', 4600, 2, '', '', '', '', 1, 0, 'F', '0', '0', 'xf:statistics:export', '#', 'admin', sysdate(), '', NULL, '');

-- ========================================
-- 站内消息
-- ========================================
INSERT INTO sys_menu VALUES (4700, '站内消息', 4000, 7, 'message', 'xf/message/index', '', '', 1, 0, 'C', '0', '0', 'xf:message:list', 'message', 'admin', sysdate(), '', NULL, '站内消息菜单');
-- 消息按钮权限
INSERT INTO sys_menu VALUES (4701, '消息查询', 4700, 1, '', '', '', '', 1, 0, 'F', '0', '0', 'xf:message:query', '#', 'admin', sysdate(), '', NULL, '');
INSERT INTO sys_menu VALUES (4702, '消息删除', 4700, 2, '', '', '', '', 1, 0, 'F', '0', '0', 'xf:message:remove','#', 'admin', sysdate(), '', NULL, '');

-- ========================================
-- 给 admin 角色（role_id=1）授权所有信访菜单
-- ========================================
INSERT INTO sys_role_menu (role_id, menu_id) VALUES
(1, 4000),
(1, 4100),(1, 4101),(1, 4102),(1, 4103),(1, 4104),(1, 4105),(1, 4106),(1, 4107),
(1, 4200),(1, 4201),(1, 4202),(1, 4203),(1, 4204),
(1, 4300),(1, 4301),(1, 4302),(1, 4303),(1, 4304),
(1, 4400),(1, 4401),(1, 4402),(1, 4403),
(1, 4500),(1, 4501),(1, 4502),(1, 4503),
(1, 4600),(1, 4601),(1, 4602),
(1, 4700),(1, 4701),(1, 4702);
