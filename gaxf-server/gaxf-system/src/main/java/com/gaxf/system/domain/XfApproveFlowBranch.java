package com.gaxf.system.domain;

import java.io.Serializable;

/**
 * 审批流承办单位分支映射对象 xf_approve_flow_branch
 *
 * @author gaxf
 */
public class XfApproveFlowBranch implements Serializable
{
    private static final long serialVersionUID = 1L;

    /** 主键ID */
    private Long id;

    /** 审批流配置ID */
    private Long flowConfigId;

    /** 承办单位ID */
    private Long undertakeDeptId;

    /** 承办单位名称 */
    private String undertakeDeptName;

    /** 首审部门ID */
    private Long firstApproveDeptId;

    /** 首审部门名称 */
    private String firstApproveDeptName;

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

    public Long getUndertakeDeptId()
    {
        return undertakeDeptId;
    }

    public void setUndertakeDeptId(Long undertakeDeptId)
    {
        this.undertakeDeptId = undertakeDeptId;
    }

    public String getUndertakeDeptName()
    {
        return undertakeDeptName;
    }

    public void setUndertakeDeptName(String undertakeDeptName)
    {
        this.undertakeDeptName = undertakeDeptName;
    }

    public Long getFirstApproveDeptId()
    {
        return firstApproveDeptId;
    }

    public void setFirstApproveDeptId(Long firstApproveDeptId)
    {
        this.firstApproveDeptId = firstApproveDeptId;
    }

    public String getFirstApproveDeptName()
    {
        return firstApproveDeptName;
    }

    public void setFirstApproveDeptName(String firstApproveDeptName)
    {
        this.firstApproveDeptName = firstApproveDeptName;
    }
}
