package com.gaxf.system.service;

import java.util.List;
import com.gaxf.system.domain.XfHandlingReport;

/**
 * 办理报告 服务层
 *
 * @author gaxf
 */
public interface IXfHandlingReportService
{
    /**
     * 查询办理报告信息
     *
     * @param id 报告ID
     * @return 办理报告信息
     */
    public XfHandlingReport selectXfHandlingReportById(Long id);

    /**
     * 查询办理报告列表
     *
     * @param xfHandlingReport 办理报告信息
     * @return 办理报告集合
     */
    public List<XfHandlingReport> selectXfHandlingReportList(XfHandlingReport xfHandlingReport);

    /**
     * 根据工单ID查询办理报告
     *
     * @param orderId 工单ID
     * @return 办理报告集合
     */
    public List<XfHandlingReport> selectXfHandlingReportByOrderId(Long orderId);

    /**
     * 根据交办记录ID查询办理报告
     *
     * @param assignId 交办记录ID
     * @return 办理报告信息
     */
    public XfHandlingReport selectXfHandlingReportByAssignId(Long assignId);

    /**
     * 新增办理报告
     *
     * @param xfHandlingReport 办理报告信息
     * @return 结果
     */
    public int insertXfHandlingReport(XfHandlingReport xfHandlingReport);

    /**
     * 修改办理报告
     *
     * @param xfHandlingReport 办理报告信息
     * @return 结果
     */
    public int updateXfHandlingReport(XfHandlingReport xfHandlingReport);

    /**
     * 批量删除办理报告
     *
     * @param ids 需要删除的数据ID
     * @return 结果
     */
    public int deleteXfHandlingReportByIds(Long[] ids);
}
