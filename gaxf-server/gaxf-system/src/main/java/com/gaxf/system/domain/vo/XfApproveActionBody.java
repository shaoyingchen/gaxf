package com.gaxf.system.domain.vo;

/**
 * 审批动作请求体
 *
 * @author gaxf
 */
public class XfApproveActionBody
{
    private Long taskId;
    private String opinion;

    public Long getTaskId()
    {
        return taskId;
    }

    public void setTaskId(Long taskId)
    {
        this.taskId = taskId;
    }

    public String getOpinion()
    {
        return opinion;
    }

    public void setOpinion(String opinion)
    {
        this.opinion = opinion;
    }
}
