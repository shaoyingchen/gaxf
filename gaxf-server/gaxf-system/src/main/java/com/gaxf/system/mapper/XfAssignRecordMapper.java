package com.gaxf.system.mapper;

import java.util.List;
import com.gaxf.system.domain.XfAssignRecord;

/**
 * 交办记录 数据层
 *
 * @author gaxf
 */
public interface XfAssignRecordMapper
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
     * 删除交办记录
     *
     * @param id 交办记录ID
     * @return 结果
     */
    public int deleteXfAssignRecordById(Long id);

    /**
     * 批量删除交办记录
     *
     * @param ids 需要删除的数据ID
     * @return 结果
     */
    public int deleteXfAssignRecordByIds(Long[] ids);
}
