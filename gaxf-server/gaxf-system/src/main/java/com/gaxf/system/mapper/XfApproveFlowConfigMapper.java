package com.gaxf.system.mapper;

import java.util.List;
import org.apache.ibatis.annotations.Param;
import com.gaxf.system.domain.XfApproveFlowConfig;
import com.gaxf.system.domain.XfApproveFlowStage;
import com.gaxf.system.domain.XfApproveFlowStageDept;

/**
 * 审批流配置 Mapper 接口
 *
 * @author gaxf
 */
public interface XfApproveFlowConfigMapper
{
    public XfApproveFlowConfig selectXfApproveFlowConfigById(Long id);

    public List<XfApproveFlowConfig> selectXfApproveFlowConfigList(XfApproveFlowConfig query);

    public XfApproveFlowConfig selectEnabledFlowByApplyDeptId(Long applyDeptId);

    public int countEnabledFlowByApplyDeptId(@Param("applyDeptId") Long applyDeptId, @Param("excludeId") Long excludeId);

    public int insertXfApproveFlowConfig(XfApproveFlowConfig config);

    public int updateXfApproveFlowConfig(XfApproveFlowConfig config);

    public int deleteXfApproveFlowConfigByIds(Long[] ids);

    public int deleteFlowStagesByConfigId(Long flowConfigId);

    public int deleteStageDeptsByConfigId(Long flowConfigId);

    public int batchInsertFlowStages(@Param("list") List<XfApproveFlowStage> list);

    public int batchInsertStageDepts(@Param("list") List<XfApproveFlowStageDept> list);
}
