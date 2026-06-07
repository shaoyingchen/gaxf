package com.gaxf.system.service;

import java.util.List;
import java.util.Map;
import com.gaxf.system.domain.XfWorkOrder;

/**
 * 信访工单 服务层
 *
 * @author gaxf
 */
public interface IXfWorkOrderService
{
    /**
     * 查询信访工单信息
     *
     * @param id 工单ID
     * @return 信访工单信息
     */
    public XfWorkOrder selectXfWorkOrderById(Long id);

    /**
     * 查询信访工单列表
     *
     * @param xfWorkOrder 信访工单信息
     * @return 信访工单集合
     */
    public List<XfWorkOrder> selectXfWorkOrderList(XfWorkOrder xfWorkOrder);

    /**
     * 新增信访工单
     *
     * @param xfWorkOrder 信访工单信息
     * @return 结果
     */
    public int insertXfWorkOrder(XfWorkOrder xfWorkOrder);

    /**
     * 修改信访工单
     *
     * @param xfWorkOrder 信访工单信息
     * @return 结果
     */
    public int updateXfWorkOrder(XfWorkOrder xfWorkOrder);

    /**
     * 批量删除信访工单
     *
     * @param ids 需要删除的数据ID
     * @return 结果
     */
    public int deleteXfWorkOrderByIds(Long[] ids);

    /**
     * 交办派单
     *
     * @param orderId 工单ID
     * @param deptIds 承办单位ID数组
     * @param deadline 截止时间
     * @param deptNames 承办单位名称数组
     */
    public void assignWorkOrder(Long orderId, Long[] deptIds, String deadline, String[] deptNames);

    /**
     * 查询我的待办工单
     *
     * @param xfWorkOrder 查询条件
     * @return 信访工单集合
     */
    public List<XfWorkOrder> selectMyTodoList(XfWorkOrder xfWorkOrder);

    /**
     * 查询统计数据
     *
     * @return 统计结果
     */
    public Map<String, Object> selectStatistics();

    /**
     * 查询我的待办统计
     *
     * @param deptId 部门ID
     * @return 统计结果
     */
    public Map<String, Object> selectMyTodoStatistics(Long deptId);

    /**
     * 检查超期工单
     */
    public void checkOverdue();

    /**
     * 导入信访工单（同步，内部调用）
     *
     * @param workOrderList 工单列表
     * @param createBy 创建者
     * @return 结果
     */
    public String importWorkOrder(List<XfWorkOrder> workOrderList, String createBy);

    /**
     * 异步导入信访工单，立即返回任务ID
     *
     * @param workOrderList 工单列表
     * @param createBy 创建者
     * @return 任务ID
     */
    public String asyncImportWorkOrder(List<XfWorkOrder> workOrderList, String createBy);

    /**
     * 查询导入进度
     *
     * @param taskId 任务ID
     * @return 进度信息
     */
    public Map<String, Object> getImportProgress(String taskId);

    /**
     * 查询月度趋势统计数据
     *
     * @return 月度趋势列表
     */
    public List<Map<String, Object>> selectMonthlyTrend();
}
