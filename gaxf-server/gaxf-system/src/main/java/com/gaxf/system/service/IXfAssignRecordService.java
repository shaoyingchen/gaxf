package com.gaxf.system.service;

import java.util.List;
import org.springframework.web.multipart.MultipartFile;
import com.gaxf.system.domain.XfAssignRecord;

/**
 * 交办记录 服务层
 *
 * @author gaxf
 */
public interface IXfAssignRecordService
{
    /**
     * 查询交办记录信息
     *
     * @param id 交办记录ID
     * @return 交办记录信息
     */
    public XfAssignRecord selectXfAssignRecordById(Long id);

    /**
     * 查询交办记录列表
     *
     * @param xfAssignRecord 交办记录信息
     * @return 交办记录集合
     */
    public List<XfAssignRecord> selectXfAssignRecordList(XfAssignRecord xfAssignRecord);

    /**
     * 根据工单ID查询交办记录
     *
     * @param orderId 工单ID
     * @return 交办记录集合
     */
    public List<XfAssignRecord> selectXfAssignRecordByOrderId(Long orderId);

    /**
     * 新增交办记录
     *
     * @param xfAssignRecord 交办记录信息
     * @return 结果
     */
    public int insertXfAssignRecord(XfAssignRecord xfAssignRecord);

    /**
     * 修改交办记录
     *
     * @param xfAssignRecord 交办记录信息
     * @return 结果
     */
    public int updateXfAssignRecord(XfAssignRecord xfAssignRecord);

    /**
     * 批量删除交办记录
     *
     * @param ids 需要删除的数据ID
     * @return 结果
     */
    public int deleteXfAssignRecordByIds(Long[] ids);

    /**
     * 接收工单
     *
     * @param id 交办记录ID
     */
    public void receiveWorkOrder(Long id);

    /**
     * 提交办理报告
     *
     * @param xfAssignRecord 交办记录信息（含报告信息）
     */
    public void submitReport(XfAssignRecord xfAssignRecord);

    /**
     * 提交办理报告（含文件上传）
     */
    public void submitReport(Long id, String reportContent, MultipartFile reportFile, MultipartFile[] dossierFiles, String username);

    /**
     * 县局领导审核
     *
     * @param xfAssignRecord 交办记录信息
     * @param reviewOpinion 审核意见
     * @param reviewResult 审核结果
     */
    public void countyReview(Long id, String reviewOpinion, String reviewResult);
}
