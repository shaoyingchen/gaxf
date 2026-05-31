package com.gaxf.system.mapper;

import java.util.List;
import com.gaxf.system.domain.XfReviewRecord;

/**
 * 审核记录 数据层
 *
 * @author gaxf
 */
public interface XfReviewRecordMapper
{
    /**
     * 查询审核记录信息
     *
     * @param id 审核记录ID
     * @return 审核记录信息
     */
    public XfReviewRecord selectXfReviewRecordById(Long id);

    /**
     * 查询审核记录列表
     *
     * @param xfReviewRecord 审核记录信息
     * @return 审核记录集合
     */
    public List<XfReviewRecord> selectXfReviewRecordList(XfReviewRecord xfReviewRecord);

    /**
     * 根据工单ID查询审核记录
     *
     * @param orderId 工单ID
     * @return 审核记录集合
     */
    public List<XfReviewRecord> selectXfReviewRecordByOrderId(Long orderId);

    /**
     * 新增审核记录
     *
     * @param xfReviewRecord 审核记录信息
     * @return 结果
     */
    public int insertXfReviewRecord(XfReviewRecord xfReviewRecord);

    /**
     * 修改审核记录
     *
     * @param xfReviewRecord 审核记录信息
     * @return 结果
     */
    public int updateXfReviewRecord(XfReviewRecord xfReviewRecord);

    /**
     * 删除审核记录
     *
     * @param id 审核记录ID
     * @return 结果
     */
    public int deleteXfReviewRecordById(Long id);

    /**
     * 批量删除审核记录
     *
     * @param ids 需要删除的数据ID
     * @return 结果
     */
    public int deleteXfReviewRecordByIds(Long[] ids);
}
