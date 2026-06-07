package com.gaxf.system.domain.vo;

import java.io.Serializable;
import java.util.List;
import com.gaxf.system.domain.XfApproveTask;

/**
 * 审批进度视图对象
 *
 * @author gaxf
 */
public class XfApproveProgressVO implements Serializable
{
    private static final long serialVersionUID = 1L;

    private Long instanceId;
    private String flowName;
    private String status;
    private Integer currentStageNo;
    private List<XfApproveTask> taskList;
    private List<XfApproveTask> branchTaskList;

    public Long getInstanceId()
    {
        return instanceId;
    }

    public void setInstanceId(Long instanceId)
    {
        this.instanceId = instanceId;
    }

    public String getFlowName()
    {
        return flowName;
    }

    public void setFlowName(String flowName)
    {
        this.flowName = flowName;
    }

    public String getStatus()
    {
        return status;
    }

    public void setStatus(String status)
    {
        this.status = status;
    }

    public Integer getCurrentStageNo()
    {
        return currentStageNo;
    }

    public void setCurrentStageNo(Integer currentStageNo)
    {
        this.currentStageNo = currentStageNo;
    }

    public List<XfApproveTask> getTaskList()
    {
        return taskList;
    }

    public void setTaskList(List<XfApproveTask> taskList)
    {
        this.taskList = taskList;
    }

    public List<XfApproveTask> getBranchTaskList()
    {
        return branchTaskList;
    }

    public void setBranchTaskList(List<XfApproveTask> branchTaskList)
    {
        this.branchTaskList = branchTaskList;
    }
}
