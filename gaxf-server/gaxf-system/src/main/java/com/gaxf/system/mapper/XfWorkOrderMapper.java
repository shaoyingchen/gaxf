package com.gaxf.system.mapper;

import java.util.List;
import java.util.Map;
import com.gaxf.system.domain.XfWorkOrder;

/**
 * 信访工单 数据层
 *
 * @author gaxf
 */
public interface XfWorkOrderMapper
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
     * 删除信访工单
     *
     * @param id 工单ID
     * @return 结果
     */
    public int deleteXfWorkOrderById(Long id);

    /**
     * 批量删除信访工单
     *
     * @param ids 需要删除的数据ID
     * @return 结果
     */
    public int deleteXfWorkOrderByIds(Long[] ids);

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
    public List<Map<String, Object>> selectStatistics();

    /**
     * 查询我的待办统计
     *
     * @param deptId 部门ID
     * @return 统计结果
     */
    public List<Map<String, Object>> selectMyTodoStatistics(Long deptId);

    /**
     * 检查超期工单
     *
     * @return 超期工单列表
     */
    public List<XfWorkOrder> selectOverdueList();
}
