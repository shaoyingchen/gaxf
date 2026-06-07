package com.gaxf.system.domain;

import java.io.Serializable;

/**
 * 审批流阶段部门对象 xf_approve_flow_stage_dept
 *
 * @author gaxf
 */
public class XfApproveFlowStageDept implements Serializable
{
    private static final long serialVersionUID = 1L;

    /** 主键ID */
    private Long id;

    /** 阶段ID */
    private Long stageId;

    /** 审批部门ID */
    private Long approveDeptId;

    /** 审批部门名称 */
    private String approveDeptName;

    public Long getId()
    {
        return id;
    }

    public void setId(Long id)
    {
        this.id = id;
    }

    public Long getStageId()
    {
        return stageId;
    }

    public void setStageId(Long stageId)
    {
        this.stageId = stageId;
    }

    public Long getApproveDeptId()
    {
        return approveDeptId;
    }

    public void setApproveDeptId(Long approveDeptId)
    {
        this.approveDeptId = approveDeptId;
    }

    public String getApproveDeptName()
    {
        return approveDeptName;
    }

    public void setApproveDeptName(String approveDeptName)
    {
        this.approveDeptName = approveDeptName;
    }
}
