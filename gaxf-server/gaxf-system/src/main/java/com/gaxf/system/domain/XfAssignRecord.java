package com.gaxf.system.domain;

import java.util.Date;
import com.fasterxml.jackson.annotation.JsonFormat;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.gaxf.common.annotation.Excel;
import com.gaxf.common.annotation.Excel.ColumnType;
import com.gaxf.common.core.domain.BaseEntity;

/**
 * 交办记录表 xf_assign_record
 *
 * @author gaxf
 */
public class XfAssignRecord extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** 交办记录ID */
    @Excel(name = "交办记录ID", cellType = ColumnType.NUMERIC)
    private Long id;

    /** 工单ID */
    @Excel(name = "工单ID", cellType = ColumnType.NUMERIC)
    private Long orderId;

    /** 承办单位ID */
    private Long assignDeptId;

    /** 承办单位名称 */
    @Excel(name = "承办单位")
    private String assignDeptName;

    /** 交办类型: 0=县局 1=市局警种 */
    @Excel(name = "交办类型", readConverterExp = "0=县局,1=市局警种")
    private String assignType;

    /** 交办时间 */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Excel(name = "交办时间", width = 30, dateFormat = "yyyy-MM-dd HH:mm:ss")
    private Date assignTime;

    /** 交办人 */
    @Excel(name = "交办人")
    private String assignBy;

    /** 状态: 0=待接收 1=办理中 2=已提交 3=审核通过 4=退回 */
    @Excel(name = "状态", readConverterExp = "0=待接收,1=办理中,2=已提交,3=审核通过,4=退回")
    private String status;

    /** 接收时间 */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Excel(name = "接收时间", width = 30, dateFormat = "yyyy-MM-dd HH:mm:ss")
    private Date receiveTime;

    /** 限期报送截止时间（从工单继承） */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date deadline;

    /** 办理意见 */
    private String reportContent;

    /** 办理报告附件路径 */
    private String reportAttachment;

    /** 上报时间 */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date reportTime;

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

    public Long getAssignDeptId()
    {
        return assignDeptId;
    }

    public void setAssignDeptId(Long assignDeptId)
    {
        this.assignDeptId = assignDeptId;
    }

    public String getAssignDeptName()
    {
        return assignDeptName;
    }

    public void setAssignDeptName(String assignDeptName)
    {
        this.assignDeptName = assignDeptName;
    }

    public String getAssignType()
    {
        return assignType;
    }

    public void setAssignType(String assignType)
    {
        this.assignType = assignType;
    }

    public Date getAssignTime()
    {
        return assignTime;
    }

    public void setAssignTime(Date assignTime)
    {
        this.assignTime = assignTime;
    }

    public String getAssignBy()
    {
        return assignBy;
    }

    public void setAssignBy(String assignBy)
    {
        this.assignBy = assignBy;
    }

    public String getStatus()
    {
        return status;
    }

    public void setStatus(String status)
    {
        this.status = status;
    }

    public Date getReceiveTime()
    {
        return receiveTime;
    }

    public void setReceiveTime(Date receiveTime)
    {
        this.receiveTime = receiveTime;
    }

    public Date getDeadline()
    {
        return deadline;
    }

    public void setDeadline(Date deadline)
    {
        this.deadline = deadline;
    }

    public String getReportContent()
    {
        return reportContent;
    }

    public void setReportContent(String reportContent)
    {
        this.reportContent = reportContent;
    }

    public String getReportAttachment()
    {
        return reportAttachment;
    }

    public void setReportAttachment(String reportAttachment)
    {
        this.reportAttachment = reportAttachment;
    }

    public Date getReportTime()
    {
        return reportTime;
    }

    public void setReportTime(Date reportTime)
    {
        this.reportTime = reportTime;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this,ToStringStyle.MULTI_LINE_STYLE)
            .append("id", getId())
            .append("orderId", getOrderId())
            .append("assignDeptId", getAssignDeptId())
            .append("assignDeptName", getAssignDeptName())
            .append("assignType", getAssignType())
            .append("assignTime", getAssignTime())
            .append("assignBy", getAssignBy())
            .append("status", getStatus())
            .append("receiveTime", getReceiveTime())
            .append("createBy", getCreateBy())
            .append("createTime", getCreateTime())
            .append("updateBy", getUpdateBy())
            .append("updateTime", getUpdateTime())
            .append("remark", getRemark())
            .toString();
    }
}
