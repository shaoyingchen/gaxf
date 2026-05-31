package com.gaxf.system.service.impl;

import java.io.StringWriter;
import java.util.Date;
import java.util.List;
import java.util.Properties;
import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.Velocity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.gaxf.common.exception.ServiceException;
import com.gaxf.common.utils.DateUtils;
import com.gaxf.common.utils.StringUtils;
import com.gaxf.system.domain.XfDocument;
import com.gaxf.system.domain.XfWorkOrder;
import com.gaxf.system.mapper.XfDocumentMapper;
import com.gaxf.system.mapper.XfWorkOrderMapper;
import com.gaxf.system.service.IXfDocumentService;

/**
 * 文书 服务层实现
 *
 * @author gaxf
 */
@Service
public class XfDocumentServiceImpl implements IXfDocumentService
{
    private static final Logger log = LoggerFactory.getLogger(XfDocumentServiceImpl.class);

    @Autowired
    private XfDocumentMapper xfDocumentMapper;

    @Autowired
    private XfWorkOrderMapper xfWorkOrderMapper;

    /**
     * 查询文书信息
     *
     * @param id 文书ID
     * @return 文书信息
     */
    @Override
    public XfDocument selectXfDocumentById(Long id)
    {
        return xfDocumentMapper.selectXfDocumentById(id);
    }

    /**
     * 查询文书列表
     *
     * @param xfDocument 文书信息
     * @return 文书集合
     */
    @Override
    public List<XfDocument> selectXfDocumentList(XfDocument xfDocument)
    {
        return xfDocumentMapper.selectXfDocumentList(xfDocument);
    }

    /**
     * 根据工单ID查询文书
     *
     * @param orderId 工单ID
     * @return 文书集合
     */
    @Override
    public List<XfDocument> selectXfDocumentByOrderId(Long orderId)
    {
        return xfDocumentMapper.selectXfDocumentByOrderId(orderId);
    }

    /**
     * 新增文书
     *
     * @param xfDocument 文书信息
     * @return 结果
     */
    @Override
    public int insertXfDocument(XfDocument xfDocument)
    {
        return xfDocumentMapper.insertXfDocument(xfDocument);
    }

    /**
     * 修改文书
     *
     * @param xfDocument 文书信息
     * @return 结果
     */
    @Override
    public int updateXfDocument(XfDocument xfDocument)
    {
        return xfDocumentMapper.updateXfDocument(xfDocument);
    }

    /**
     * 批量删除文书
     *
     * @param ids 需要删除的数据ID
     * @return 结果
     */
    @Override
    public int deleteXfDocumentByIds(Long[] ids)
    {
        return xfDocumentMapper.deleteXfDocumentByIds(ids);
    }

    /**
     * 生成文书
     *
     * @param orderId 工单ID
     * @param docType 文书类型
     * @return 文书信息
     */
    @Override
    @Transactional
    public XfDocument generateDocument(Long orderId, String docType)
    {
        XfWorkOrder workOrder = xfWorkOrderMapper.selectXfWorkOrderById(orderId);
        if (workOrder == null)
        {
            throw new ServiceException("工单不存在");
        }
        String docTypeName = "0".equals(docType) ? "信访工作建议书" : "信访督办建议";

        // 使用Velocity模板生成文书内容
        VelocityContext context = new VelocityContext();
        context.put("xfCaseNo", workOrder.getXfCaseNo() != null ? workOrder.getXfCaseNo() : "");
        context.put("title", workOrder.getTitle() != null ? workOrder.getTitle() : "");
        context.put("petitionerName", workOrder.getPetitionerName() != null ? workOrder.getPetitionerName() : "");
        context.put("petitionerIdcard", workOrder.getPetitionerIdcard() != null ? workOrder.getPetitionerIdcard() : "");
        context.put("petitionerPhone", workOrder.getPetitionerPhone() != null ? workOrder.getPetitionerPhone() : "");
        context.put("petitionerAddress", workOrder.getPetitionerAddress() != null ? workOrder.getPetitionerAddress() : "");
        context.put("xfContent", workOrder.getXfContent() != null ? workOrder.getXfContent() : "");
        context.put("xfForm", workOrder.getXfForm() != null ? workOrder.getXfForm() : "");
        context.put("content", workOrder.getContent() != null ? workOrder.getContent() : "");
        context.put("registerTime", workOrder.getRegisterTime() != null ? DateUtils.parseDateToStr("yyyy-MM-dd", workOrder.getRegisterTime()) : "");
        context.put("deadline", workOrder.getDeadline() != null ? DateUtils.parseDateToStr("yyyy-MM-dd", workOrder.getDeadline()) : "");
        context.put("currentDate", DateUtils.getDate());

        String docContent = "";
        try
        {
            // 初始化Velocity
            Properties props = new Properties();
            props.put("resource.loader", "class");
            props.put("class.resource.loader.class", "org.apache.velocity.runtime.resource.loader.ClasspathResourceLoader");
            Velocity.init(props);

            String templateName = "0".equals(docType) ? "templates/suggestion.doc.vm" : "templates/supervision.doc.vm";
            StringWriter writer = new StringWriter();
            Template template = Velocity.getTemplate(templateName, "UTF-8");
            template.merge(context, writer);
            docContent = writer.toString();
        }
        catch (Exception e)
        {
            log.error("生成文书内容失败", e);
            docContent = workOrder.getContent() != null ? workOrder.getContent() : "";
        }

        // 生成文书编号
        String docNo = ("0".equals(docType) ? "JY" : "DB") + DateUtils.dateTimeNow("yyyyMMdd") + String.format("%03d", (int) (Math.random() * 1000));

        XfDocument document = new XfDocument();
        document.setOrderId(orderId);
        document.setDocType(docType);
        document.setDocNo(docNo);
        document.setDocContent(docContent);
        document.setIsSigned("0");
        return document;
    }

    /**
     * 签发文书
     *
     * @param id 文书ID
     */
    @Override
    @Transactional
    public void signDocument(Long id)
    {
        XfDocument document = xfDocumentMapper.selectXfDocumentById(id);
        if (document == null)
        {
            throw new ServiceException("文书不存在");
        }
        if ("1".equals(document.getIsSigned()))
        {
            throw new ServiceException("文书已签发，不能重复签发");
        }
        XfDocument updateDoc = new XfDocument();
        updateDoc.setId(id);
        updateDoc.setIsSigned("1");
        updateDoc.setSignTime(new Date());
        xfDocumentMapper.updateXfDocument(updateDoc);
    }
}
