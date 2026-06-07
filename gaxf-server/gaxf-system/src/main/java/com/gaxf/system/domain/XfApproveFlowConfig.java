package com.gaxf.system.domain;

import java.util.List;
import com.gaxf.common.core.domain.BaseEntity;

/**
 * 审批流配置对象 xf_approve_flow_config
 *
 * @author gaxf
 */
public class XfApproveFlowConfig extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** 主键ID */
    private Long id;

    /** 审批流名称 */
    private String flowName;

    /** 申请部门ID */
    private Long applyDeptId;

    /** 申请部门名称 */
    private String applyDeptName;

    /** 状态: 0=启用 1=停用 */
    private String status;

    /** 阶段列表 */
    private List<XfApproveFlowStage> stages;

    public Long getId()
    {
        return id;
    }

    public void setId(Long id)
    {
        this.id = id;
    }

    public String getFlowName()
    {
        return flowName;
    }

    public void setFlowName(String flowName)
    {
        this.flowName = flowName;
    }

    public Long getApplyDeptId()
    {
        return applyDeptId;
    }

    public void setApplyDeptId(Long applyDeptId)
    {
        this.applyDeptId = applyDeptId;
    }

    public String getApplyDeptName()
    {
        return applyDeptName;
    }

    public void setApplyDeptName(String applyDeptName)
    {
        this.applyDeptName = applyDeptName;
    }

    public String getStatus()
    {
        return status;
    }

    public void setStatus(String status)
    {
        this.status = status;
    }

    public List<XfApproveFlowStage> getStages()
    {
        return stages;
    }

    public void setStages(List<XfApproveFlowStage> stages)
    {
        this.stages = stages;
    }
}
