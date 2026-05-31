package com.gaxf.system.service.impl;

import java.io.IOException;
import java.util.Date;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import com.gaxf.common.exception.ServiceException;
import com.gaxf.common.utils.DateUtils;
import com.gaxf.common.utils.file.FileUploadUtils;
import com.gaxf.system.domain.XfAssignRecord;
import com.gaxf.system.domain.XfDossier;
import com.gaxf.system.domain.XfHandlingReport;
import com.gaxf.system.domain.XfMessage;
import com.gaxf.system.domain.XfWorkOrder;
import com.gaxf.system.mapper.XfAssignRecordMapper;
import com.gaxf.system.mapper.XfDossierMapper;
import com.gaxf.system.mapper.XfHandlingReportMapper;
import com.gaxf.system.mapper.XfMessageMapper;
import com.gaxf.system.mapper.XfWorkOrderMapper;
import com.gaxf.system.service.IXfAssignRecordService;

/**
 * 交办记录 服务层实现
 *
 * @author gaxf
 */
@Service
public class XfAssignRecordServiceImpl implements IXfAssignRecordService
{
    @Autowired
    private XfAssignRecordMapper xfAssignRecordMapper;

    @Autowired
    private XfWorkOrderMapper xfWorkOrderMapper;

    @Autowired
    private XfHandlingReportMapper xfHandlingReportMapper;

    @Autowired
    private XfDossierMapper xfDossierMapper;

    @Autowired
    private XfMessageMapper xfMessageMapper;

    /**
     * 查询交办记录信息
     *
     * @param id 交办记录ID
     * @return 交办记录信息
     */
    @Override
    public XfAssignRecord selectXfAssignRecordById(Long id)
    {
        return xfAssignRecordMapper.selectXfAssignRecordById(id);
    }

    /**
     * 查询交办记录列表
     *
     * @param xfAssignRecord 交办记录信息
     * @return 交办记录集合
     */
    @Override
    public List<XfAssignRecord> selectXfAssignRecordList(XfAssignRecord xfAssignRecord)
    {
        return xfAssignRecordMapper.selectXfAssignRecordList(xfAssignRecord);
    }

    /**
     * 根据工单ID查询交办记录
     *
     * @param orderId 工单ID
     * @return 交办记录集合
     */
    @Override
    public List<XfAssignRecord> selectXfAssignRecordByOrderId(Long orderId)
    {
        return xfAssignRecordMapper.selectXfAssignRecordByOrderId(orderId);
    }

    /**
     * 新增交办记录
     *
     * @param xfAssignRecord 交办记录信息
     * @return 结果
     */
    @Override
    public int insertXfAssignRecord(XfAssignRecord xfAssignRecord)
    {
        return xfAssignRecordMapper.insertXfAssignRecord(xfAssignRecord);
    }

    /**
     * 修改交办记录
     *
     * @param xfAssignRecord 交办记录信息
     * @return 结果
     */
    @Override
    public int updateXfAssignRecord(XfAssignRecord xfAssignRecord)
    {
        return xfAssignRecordMapper.updateXfAssignRecord(xfAssignRecord);
    }

    /**
     * 批量删除交办记录
     *
     * @param ids 需要删除的数据ID
     * @return 结果
     */
    @Override
    public int deleteXfAssignRecordByIds(Long[] ids)
    {
        return xfAssignRecordMapper.deleteXfAssignRecordByIds(ids);
    }

    /**
     * 接收工单
     *
     * @param id 交办记录ID
     */
    @Override
    @Transactional
    public void receiveWorkOrder(Long id)
    {
        XfAssignRecord record = xfAssignRecordMapper.selectXfAssignRecordById(id);
        if (record == null)
        {
            throw new ServiceException("交办记录不存在");
        }
        if (!"0".equals(record.getStatus()))
        {
            throw new ServiceException("只有待接收状态的工单才能接收");
        }
        // 更新交办记录状态为办理中
        record.setStatus("1");
        record.setReceiveTime(new Date());
        xfAssignRecordMapper.updateXfAssignRecord(record);
    }

    /**
     * 提交办理报告
     *
     * @param xfAssignRecord 交办记录信息（含报告信息）
     */
    @Override
    @Transactional
    public void submitReport(XfAssignRecord xfAssignRecord)
    {
        XfAssignRecord record = xfAssignRecordMapper.selectXfAssignRecordById(xfAssignRecord.getId());
        if (record == null)
        {
            throw new ServiceException("交办记录不存在");
        }
        if (!"1".equals(record.getStatus()))
        {
            throw new ServiceException("只有办理中状态的工单才能提交报告");
        }
        // 更新交办记录状态为已提交
        record.setStatus("2");
        xfAssignRecordMapper.updateXfAssignRecord(record);

        // 更新工单状态为已上报
        XfWorkOrder workOrder = new XfWorkOrder();
        workOrder.setId(record.getOrderId());
        workOrder.setStatus("2");
        xfWorkOrderMapper.updateXfWorkOrder(workOrder);

        // 推送审核通知消息
        XfMessage message = new XfMessage();
        message.setOrderId(record.getOrderId());
        message.setReceiverId(record.getAssignDeptId());
        message.setTitle("审核通知：办理报告已提交");
        message.setContent("工单交办记录已提交办理报告，请及时审核。");
        message.setMsgType("1");
        xfMessageMapper.insertXfMessage(message);
    }

    /**
     * 提交办理报告（含文件上传）
     */
    @Override
    @Transactional
    public void submitReport(Long id, String reportContent, MultipartFile reportFile, MultipartFile[] dossierFiles, String username)
    {
        XfAssignRecord record = xfAssignRecordMapper.selectXfAssignRecordById(id);
        if (record == null)
        {
            throw new ServiceException("交办记录不存在");
        }
        if (!"1".equals(record.getStatus()))
        {
            throw new ServiceException("只有办理中状态的工单才能提交报告");
        }

        // 上传办理报告附件
        String attachmentPath = null;
        if (reportFile != null && !reportFile.isEmpty())
        {
            try
            {
                attachmentPath = FileUploadUtils.upload(reportFile);
            }
            catch (IOException e)
            {
                throw new ServiceException("办理报告文件上传失败");
            }
        }

        // 更新交办记录
        record.setReportContent(reportContent);
        record.setReportAttachment(attachmentPath);
        record.setReportTime(new Date());
        record.setStatus("2");
        record.setUpdateBy(username);
        xfAssignRecordMapper.updateXfAssignRecord(record);

        // 上传案件电子卷宗
        if (dossierFiles != null)
        {
            for (MultipartFile dossierFile : dossierFiles)
            {
                if (dossierFile != null && !dossierFile.isEmpty())
                {
                    try
                    {
                        String filePath = FileUploadUtils.upload(dossierFile);
                        XfDossier dossier = new XfDossier();
                        dossier.setOrderId(record.getOrderId());
                        dossier.setFileName(dossierFile.getOriginalFilename());
                        dossier.setFilePath(filePath);
                        dossier.setFileSize(dossierFile.getSize());
                        dossier.setUploadBy(username);
                        dossier.setCreateBy(username);
                        xfDossierMapper.insertXfDossier(dossier);
                    }
                    catch (IOException e)
                    {
                        throw new ServiceException("卷宗文件上传失败：" + dossierFile.getOriginalFilename());
                    }
                }
            }
        }

        // 更新工单状态为已上报
        XfWorkOrder workOrder = new XfWorkOrder();
        workOrder.setId(record.getOrderId());
        workOrder.setStatus("2");
        xfWorkOrderMapper.updateXfWorkOrder(workOrder);

        // 推送审核通知消息
        XfMessage message = new XfMessage();
        message.setOrderId(record.getOrderId());
        message.setReceiverId(record.getAssignDeptId());
        message.setTitle("审核通知：办理报告已提交");
        message.setContent("工单交办记录已提交办理报告，请及时审核。");
        message.setMsgType("1");
        xfMessageMapper.insertXfMessage(message);
    }

    /**
     * 县局领导审核
     *
     * @param id 交办记录ID
     * @param reviewOpinion 审核意见
     * @param reviewResult 审核结果
     */
    @Override
    @Transactional
    public void countyReview(Long id, String reviewOpinion, String reviewResult)
    {
        XfAssignRecord record = xfAssignRecordMapper.selectXfAssignRecordById(id);
        if (record == null)
        {
            throw new ServiceException("交办记录不存在");
        }
        if (!"2".equals(record.getStatus()))
        {
            throw new ServiceException("只有已提交状态的工单才能审核");
        }
        if ("0".equals(reviewResult))
        {
            // 审核通过
            record.setStatus("3");
            xfAssignRecordMapper.updateXfAssignRecord(record);

            // 更新工单状态为已办结
            XfWorkOrder workOrder = new XfWorkOrder();
            workOrder.setId(record.getOrderId());
            workOrder.setStatus("3");
            xfWorkOrderMapper.updateXfWorkOrder(workOrder);
        }
        else if ("1".equals(reviewResult))
        {
            // 退回
            record.setStatus("4");
            xfAssignRecordMapper.updateXfAssignRecord(record);

            // 更新工单状态为已退回
            XfWorkOrder workOrder = new XfWorkOrder();
            workOrder.setId(record.getOrderId());
            workOrder.setStatus("4");
            xfWorkOrderMapper.updateXfWorkOrder(workOrder);

            // 推送退回通知
            XfMessage message = new XfMessage();
            message.setOrderId(record.getOrderId());
            message.setReceiverId(record.getAssignDeptId());
            message.setTitle("退回通知：" + reviewOpinion);
            message.setContent("您提交的办理报告已被退回，请重新办理。退回原因：" + reviewOpinion);
            message.setMsgType("3");
            xfMessageMapper.insertXfMessage(message);
        }
    }
}
