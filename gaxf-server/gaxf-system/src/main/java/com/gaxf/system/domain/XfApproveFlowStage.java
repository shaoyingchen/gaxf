package com.gaxf.system.domain;

import java.io.Serializable;
import java.util.List;

/**
 * 审批流阶段对象 xf_approve_flow_stage
 *
 * @author gaxf
 */
public class XfApproveFlowStage implements Serializable
{
    private static final long serialVersionUID = 1L;

    /** 主键ID */
    private Long id;

    /** 审批流配置ID */
    private Long flowConfigId;

    /** 阶段序号 */
    private Integer stageNo;

    /** 阶段名称 */
    private String stageName;

    /** 阶段类型: 1=汇总阶段 */
    private String stageType;

    /** 并行模式: 1=并行 */
    private String parallelMode;

    /** 审批部门列表 */
    private List<XfApproveFlowStageDept> deptList;

    public Long getId()
    {
        return id;
    }

    public void setId(Long id)
    {
        this.id = id;
    }

    public Long getFlowConfigId()
    {
        return flowConfigId;
    }

    public void setFlowConfigId(Long flowConfigId)
    {
        this.flowConfigId = flowConfigId;
    }

    public Integer getStageNo()
    {
        return stageNo;
    }

    public void setStageNo(Integer stageNo)
    {
        this.stageNo = stageNo;
    }

    public String getStageName()
    {
        return stageName;
    }

    public void setStageName(String stageName)
    {
        this.stageName = stageName;
    }

    public String getStageType()
    {
        return stageType;
    }

    public void setStageType(String stageType)
    {
        this.stageType = stageType;
    }

    public String getParallelMode()
    {
        return parallelMode;
    }

    public void setParallelMode(String parallelMode)
    {
        this.parallelMode = parallelMode;
    }

    public List<XfApproveFlowStageDept> getDeptList()
    {
        return deptList;
    }

    public void setDeptList(List<XfApproveFlowStageDept> deptList)
    {
        this.deptList = deptList;
    }
}
