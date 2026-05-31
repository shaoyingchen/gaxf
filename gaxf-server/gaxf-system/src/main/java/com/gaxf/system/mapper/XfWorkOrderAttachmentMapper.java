package com.gaxf.system.mapper;

import java.util.List;
import com.gaxf.system.domain.XfWorkOrderAttachment;

/**
 * 工单附件 数据层
 *
 * @author gaxf
 */
public interface XfWorkOrderAttachmentMapper
{
    /**
     * 查询工单附件信息
     *
     * @param id 附件ID
     * @return 工单附件信息
     */
    public XfWorkOrderAttachment selectXfWorkOrderAttachmentById(Long id);

    /**
     * 根据工单ID查询附件列表
     *
     * @param orderId 工单ID
     * @return 工单附件集合
     */
    public List<XfWorkOrderAttachment> selectXfWorkOrderAttachmentByOrderId(Long orderId);

    /**
     * 新增工单附件
     *
     * @param xfWorkOrderAttachment 工单附件信息
     * @return 结果
     */
    public int insertXfWorkOrderAttachment(XfWorkOrderAttachment xfWorkOrderAttachment);

    /**
     * 批量新增工单附件
     *
     * @param list 工单附件列表
     * @return 结果
     */
    public int batchInsertXfWorkOrderAttachment(List<XfWorkOrderAttachment> list);

    /**
     * 删除工单附件
     *
     * @param id 附件ID
     * @return 结果
     */
    public int deleteXfWorkOrderAttachmentById(Long id);

    /**
     * 批量删除工单附件
     *
     * @param ids 需要删除的数据ID
     * @return 结果
     */
    public int deleteXfWorkOrderAttachmentByIds(Long[] ids);

    /**
     * 根据工单ID删除附件
     *
     * @param orderId 工单ID
     * @return 结果
     */
    public int deleteXfWorkOrderAttachmentByOrderId(Long orderId);
}
