package com.gaxf.system.service.impl;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.gaxf.system.domain.XfHandlingReport;
import com.gaxf.system.mapper.XfHandlingReportMapper;
import com.gaxf.system.service.IXfHandlingReportService;

/**
 * 办理报告 服务层实现
 *
 * @author gaxf
 */
@Service
public class XfHandlingReportServiceImpl implements IXfHandlingReportService
{
    @Autowired
    private XfHandlingReportMapper xfHandlingReportMapper;

    /**
     * 查询办理报告信息
     *
     * @param id 报告ID
     * @return 办理报告信息
     */
    @Override
    public XfHandlingReport selectXfHandlingReportById(Long id)
    {
        return xfHandlingReportMapper.selectXfHandlingReportById(id);
    }

    /**
     * 查询办理报告列表
     *
     * @param xfHandlingReport 办理报告信息
     * @return 办理报告集合
     */
    @Override
    public List<XfHandlingReport> selectXfHandlingReportList(XfHandlingReport xfHandlingReport)
    {
        return xfHandlingReportMapper.selectXfHandlingReportList(xfHandlingReport);
    }

    /**
     * 根据工单ID查询办理报告
     *
     * @param orderId 工单ID
     * @return 办理报告集合
     */
    @Override
    public List<XfHandlingReport> selectXfHandlingReportByOrderId(Long orderId)
    {
        return xfHandlingReportMapper.selectXfHandlingReportByOrderId(orderId);
    }

    /**
     * 根据交办记录ID查询办理报告
     *
     * @param assignId 交办记录ID
     * @return 办理报告信息
     */
    @Override
    public XfHandlingReport selectXfHandlingReportByAssignId(Long assignId)
    {
        return xfHandlingReportMapper.selectXfHandlingReportByAssignId(assignId);
    }

    /**
     * 新增办理报告
     *
     * @param xfHandlingReport 办理报告信息
     * @return 结果
     */
    @Override
    public int insertXfHandlingReport(XfHandlingReport xfHandlingReport)
    {
        return xfHandlingReportMapper.insertXfHandlingReport(xfHandlingReport);
    }

    /**
     * 修改办理报告
     *
     * @param xfHandlingReport 办理报告信息
     * @return 结果
     */
    @Override
    public int updateXfHandlingReport(XfHandlingReport xfHandlingReport)
    {
        return xfHandlingReportMapper.updateXfHandlingReport(xfHandlingReport);
    }

    /**
     * 批量删除办理报告
     *
     * @param ids 需要删除的数据ID
     * @return 结果
     */
    @Override
    public int deleteXfHandlingReportByIds(Long[] ids)
    {
        return xfHandlingReportMapper.deleteXfHandlingReportByIds(ids);
    }
}
