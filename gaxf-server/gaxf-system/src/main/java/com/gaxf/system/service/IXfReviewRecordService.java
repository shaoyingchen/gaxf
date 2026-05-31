package com.gaxf.system.service;

import java.util.List;
import com.gaxf.system.domain.XfReviewRecord;

/**
 * 审核记录 服务层
 *
 * @author gaxf
 */
public interface IXfReviewRecordService
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
     * 警种评查复核
     *
     * @param xfReviewRecord 审核记录信息
     */
    public void policeReview(XfReviewRecord xfReviewRecord);

    /**
     * 专员审核
     *
     * @param xfReviewRecord 审核记录信息
     */
    public void commissionerReview(XfReviewRecord xfReviewRecord);

    /**
     * 支队领导审核
     *
     * @param xfReviewRecord 审核记录信息
     */
    public void leaderReview(XfReviewRecord xfReviewRecord);
}
