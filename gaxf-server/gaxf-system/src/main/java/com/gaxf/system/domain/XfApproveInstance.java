package com.gaxf.system.domain;

import java.util.Date;
import java.util.List;
import com.gaxf.common.core.domain.BaseEntity;

/**
 * 审批实例对象 xf_approve_instance
 *
 * @author gaxf
 */
public class XfApproveInstance extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    private Long id;
    private Long orderId;
    private Long flowConfigId;
    private String flowName;
    private Long applyDeptId;
    private String applyDeptName;
    private Integer currentStageNo;
    private String status;
    private Date startTime;
    private Date finishTime;
    private List<XfApproveTask> taskList;

    public Long getId()
    {
        return id;
    }

    public void setId(Long id)
    {
        this.id = id;
    }

    public Long getOrderId()
    {
        return orderId;
    }

    public void setOrderId(Long orderId)
    {
        this.orderId = orderId;
    }

    public Long getFlowConfigId()
    {
        return flowConfigId;
    }

    public void setFlowConfigId(Long flowConfigId)
    {
        this.flowConfigId = flowConfigId;
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

    public Integer getCurrentStageNo()
    {
        return currentStageNo;
    }

    public void setCurrentStageNo(Integer currentStageNo)
    {
        this.currentStageNo = currentStageNo;
    }

    public String getStatus()
    {
        return status;
    }

    public void setStatus(String status)
    {
        this.status = status;
    }

    public Date getStartTime()
    {
        return startTime;
    }

    public void setStartTime(Date startTime)
    {
        this.startTime = startTime;
    }

    public Date getFinishTime()
    {
        return finishTime;
    }

    public void setFinishTime(Date finishTime)
    {
        this.finishTime = finishTime;
    }

    public List<XfApproveTask> getTaskList()
    {
        return taskList;
    }

    public void setTaskList(List<XfApproveTask> taskList)
    {
        this.taskList = taskList;
    }
}
