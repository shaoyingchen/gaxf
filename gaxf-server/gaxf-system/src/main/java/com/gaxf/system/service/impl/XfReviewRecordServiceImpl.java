package com.gaxf.system.service.impl;

import java.util.Date;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.gaxf.common.exception.ServiceException;
import com.gaxf.system.domain.XfAssignRecord;
import com.gaxf.system.domain.XfMessage;
import com.gaxf.system.domain.XfReviewRecord;
import com.gaxf.system.domain.XfWorkOrder;
import com.gaxf.system.mapper.XfAssignRecordMapper;
import com.gaxf.system.mapper.XfMessageMapper;
import com.gaxf.system.mapper.XfReviewRecordMapper;
import com.gaxf.system.mapper.XfWorkOrderMapper;
import com.gaxf.system.service.IXfReviewRecordService;

/**
 * 审核记录 服务层实现
 *
 * @author gaxf
 */
@Service
public class XfReviewRecordServiceImpl implements IXfReviewRecordService
{
    @Autowired
    private XfReviewRecordMapper xfReviewRecordMapper;

    @Autowired
    private XfWorkOrderMapper xfWorkOrderMapper;

    @Autowired
    private XfAssignRecordMapper xfAssignRecordMapper;

    @Autowired
    private XfMessageMapper xfMessageMapper;

    /**
     * 查询审核记录信息
     *
     * @param id 审核记录ID
     * @return 审核记录信息
     */
    @Override
    public XfReviewRecord selectXfReviewRecordById(Long id)
    {
        return xfReviewRecordMapper.selectXfReviewRecordById(id);
    }

    /**
     * 查询审核记录列表
     *
     * @param xfReviewRecord 审核记录信息
     * @return 审核记录集合
     */
    @Override
    public List<XfReviewRecord> selectXfReviewRecordList(XfReviewRecord xfReviewRecord)
    {
        return xfReviewRecordMapper.selectXfReviewRecordList(xfReviewRecord);
    }

    /**
     * 根据工单ID查询审核记录
     *
     * @param orderId 工单ID
     * @return 审核记录集合
     */
    @Override
    public List<XfReviewRecord> selectXfReviewRecordByOrderId(Long orderId)
    {
        return xfReviewRecordMapper.selectXfReviewRecordByOrderId(orderId);
    }

    /**
     * 新增审核记录
     *
     * @param xfReviewRecord 审核记录信息
     * @return 结果
     */
    @Override
    public int insertXfReviewRecord(XfReviewRecord xfReviewRecord)
    {
        return xfReviewRecordMapper.insertXfReviewRecord(xfReviewRecord);
    }

    /**
     * 警种评查复核
     *
     * @param xfReviewRecord 审核记录信息
     */
    @Override
    @Transactional
    public void policeReview(XfReviewRecord xfReviewRecord)
    {
        xfReviewRecord.setReviewType("1");
        xfReviewRecord.setReviewTime(new Date());
        xfReviewRecordMapper.insertXfReviewRecord(xfReviewRecord);
        updateWorkOrderAfterReview(xfReviewRecord);
    }

    /**
     * 专员审核
     *
     * @param xfReviewRecord 审核记录信息
     */
    @Override
    @Transactional
    public void commissionerReview(XfReviewRecord xfReviewRecord)
    {
        xfReviewRecord.setReviewType("2");
        xfReviewRecord.setReviewTime(new Date());
        xfReviewRecordMapper.insertXfReviewRecord(xfReviewRecord);
        updateWorkOrderAfterReview(xfReviewRecord);
    }

    /**
     * 支队领导审核
     *
     * @param xfReviewRecord 审核记录信息
     */
    @Override
    @Transactional
    public void leaderReview(XfReviewRecord xfReviewRecord)
    {
        xfReviewRecord.setReviewType("3");
        xfReviewRecord.setReviewTime(new Date());
        xfReviewRecordMapper.insertXfReviewRecord(xfReviewRecord);
        updateWorkOrderAfterReview(xfReviewRecord);
    }

    /**
     * 审核后更新工单状态
     */
    private void updateWorkOrderAfterReview(XfReviewRecord xfReviewRecord)
    {
        XfWorkOrder workOrder = xfWorkOrderMapper.selectXfWorkOrderById(xfReviewRecord.getOrderId());
        if (workOrder == null)
        {
            throw new ServiceException("工单不存在");
        }
        if ("0".equals(xfReviewRecord.getReviewResult()))
        {
            // 审核通过，更新工单状态为已办结
            XfWorkOrder updateOrder = new XfWorkOrder();
            updateOrder.setId(xfReviewRecord.getOrderId());
            updateOrder.setStatus("3");
            xfWorkOrderMapper.updateXfWorkOrder(updateOrder);
        }
        else if ("1".equals(xfReviewRecord.getReviewResult()))
        {
            // 退回
            XfWorkOrder updateOrder = new XfWorkOrder();
            updateOrder.setId(xfReviewRecord.getOrderId());
            updateOrder.setStatus("4");
            xfWorkOrderMapper.updateXfWorkOrder(updateOrder);

            // 推送退回通知
            List<XfAssignRecord> assignRecords = xfAssignRecordMapper.selectXfAssignRecordByOrderId(xfReviewRecord.getOrderId());
            for (XfAssignRecord record : assignRecords)
            {
                XfMessage message = new XfMessage();
                message.setOrderId(xfReviewRecord.getOrderId());
                message.setReceiverId(record.getAssignDeptId());
                message.setTitle("退回通知：" + workOrder.getTitle());
                message.setContent("审核未通过，工单已退回。退回原因：" + xfReviewRecord.getReviewOpinion());
                message.setMsgType("3");
                xfMessageMapper.insertXfMessage(message);
            }
        }
    }
}
