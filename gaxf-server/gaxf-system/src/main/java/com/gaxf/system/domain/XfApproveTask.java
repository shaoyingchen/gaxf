package com.gaxf.system.domain;

import java.util.Date;
import com.gaxf.common.core.domain.BaseEntity;

/**
 * 审批任务对象 xf_approve_task
 *
 * @author gaxf
 */
public class XfApproveTask extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    private Long id;
    private Long instanceId;
    private Long orderId;
    private Integer stageNo;
    private String stageName;
    private String taskType;
    private Long undertakeDeptId;
    private String undertakeDeptName;
    private Long approveDeptId;
    private String approveDeptName;
    private Long reviewerId;
    private String reviewerName;
    private String opinion;
    private String status;
    private Date actionTime;

    /** 展示字段 */
    private String orderTitle;
    private String xfDemand;
    private String xfCaseNo;

    public Long getId()
    {
        return id;
    }

    public void setId(Long id)
    {
        this.id = id;
    }

    public Long getInstanceId()
    {
        return instanceId;
    }

    public void setInstanceId(Long instanceId)
    {
        this.instanceId = instanceId;
    }

    public Long getOrderId()
    {
        return orderId;
    }

    public void setOrderId(Long orderId)
    {
        this.orderId = orderId;
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

    public String getTaskType()
    {
        return taskType;
    }

    public void setTaskType(String taskType)
    {
        this.taskType = taskType;
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

    public Long getReviewerId()
    {
        return reviewerId;
    }

    public void setReviewerId(Long reviewerId)
    {
        this.reviewerId = reviewerId;
    }

    public String getReviewerName()
    {
        return reviewerName;
    }

    public void setReviewerName(String reviewerName)
    {
        this.reviewerName = reviewerName;
    }

    public String getOpinion()
    {
        return opinion;
    }

    public void setOpinion(String opinion)
    {
        this.opinion = opinion;
    }

    public String getStatus()
    {
        return status;
    }

    public void setStatus(String status)
    {
        this.status = status;
    }

    public Date getActionTime()
    {
        return actionTime;
    }

    public void setActionTime(Date actionTime)
    {
        this.actionTime = actionTime;
    }

    public String getOrderTitle()
    {
        return orderTitle;
    }

    public void setOrderTitle(String orderTitle)
    {
        this.orderTitle = orderTitle;
    }

    public String getXfDemand()
    {
        return xfDemand;
    }

    public void setXfDemand(String xfDemand)
    {
        this.xfDemand = xfDemand;
    }

    public String getXfCaseNo()
    {
        return xfCaseNo;
    }

    public void setXfCaseNo(String xfCaseNo)
    {
        this.xfCaseNo = xfCaseNo;
    }
}
