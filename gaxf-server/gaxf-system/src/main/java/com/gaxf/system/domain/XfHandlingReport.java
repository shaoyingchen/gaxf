package com.gaxf.system.domain;

import java.util.Date;
import com.fasterxml.jackson.annotation.JsonFormat;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.gaxf.common.annotation.Excel;
import com.gaxf.common.annotation.Excel.ColumnType;
import com.gaxf.common.core.domain.BaseEntity;

/**
 * 办理报告表 xf_handling_report
 *
 * @author gaxf
 */
public class XfHandlingReport extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** 报告ID */
    @Excel(name = "报告ID", cellType = ColumnType.NUMERIC)
    private Long id;

    /** 工单ID */
    @Excel(name = "工单ID", cellType = ColumnType.NUMERIC)
    private Long orderId;

    /** 交办记录ID */
    private Long assignId;

    /** 办理单位ID */
    private Long reportDeptId;

    /** 办理单位名称 */
    @Excel(name = "办理单位")
    private String reportDeptName;

    /** 办理报告内容 */
    private String reportContent;

    /** 办理意见 */
    private String handlingOpinion;

    /** 报告人 */
    @Excel(name = "报告人")
    private String reportBy;

    /** 报告时间 */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Excel(name = "报告时间", width = 30, dateFormat = "yyyy-MM-dd HH:mm:ss")
    private Date reportTime;

    /** 状态: 0=草稿 1=已提交 2=县局审核通过 3=退回 */
    @Excel(name = "状态", readConverterExp = "0=草稿,1=已提交,2=县局审核通过,3=退回")
    private String status;

    /** 审核意见 */
    private String reviewOpinion;

    /** 审核人 */
    private String reviewBy;

    /** 审核时间 */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date reviewTime;

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

    public Long getAssignId()
    {
        return assignId;
    }

    public void setAssignId(Long assignId)
    {
        this.assignId = assignId;
    }

    public Long getReportDeptId()
    {
        return reportDeptId;
    }

    public void setReportDeptId(Long reportDeptId)
    {
        this.reportDeptId = reportDeptId;
    }

    public String getReportDeptName()
    {
        return reportDeptName;
    }

    public void setReportDeptName(String reportDeptName)
    {
        this.reportDeptName = reportDeptName;
    }

    public String getReportContent()
    {
        return reportContent;
    }

    public void setReportContent(String reportContent)
    {
        this.reportContent = reportContent;
    }

    public String getHandlingOpinion()
    {
        return handlingOpinion;
    }

    public void setHandlingOpinion(String handlingOpinion)
    {
        this.handlingOpinion = handlingOpinion;
    }

    public String getReportBy()
    {
        return reportBy;
    }

    public void setReportBy(String reportBy)
    {
        this.reportBy = reportBy;
    }

    public Date getReportTime()
    {
        return reportTime;
    }

    public void setReportTime(Date reportTime)
    {
        this.reportTime = reportTime;
    }

    public String getStatus()
    {
        return status;
    }

    public void setStatus(String status)
    {
        this.status = status;
    }

    public String getReviewOpinion()
    {
        return reviewOpinion;
    }

    public void setReviewOpinion(String reviewOpinion)
    {
        this.reviewOpinion = reviewOpinion;
    }

    public String getReviewBy()
    {
        return reviewBy;
    }

    public void setReviewBy(String reviewBy)
    {
        this.reviewBy = reviewBy;
    }

    public Date getReviewTime()
    {
        return reviewTime;
    }

    public void setReviewTime(Date reviewTime)
    {
        this.reviewTime = reviewTime;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this,ToStringStyle.MULTI_LINE_STYLE)
            .append("id", getId())
            .append("orderId", getOrderId())
            .append("assignId", getAssignId())
            .append("reportDeptId", getReportDeptId())
            .append("reportDeptName", getReportDeptName())
            .append("reportContent", getReportContent())
            .append("handlingOpinion", getHandlingOpinion())
            .append("reportBy", getReportBy())
            .append("reportTime", getReportTime())
            .append("status", getStatus())
            .append("reviewOpinion", getReviewOpinion())
            .append("reviewBy", getReviewBy())
            .append("reviewTime", getReviewTime())
            .append("createBy", getCreateBy())
            .append("createTime", getCreateTime())
            .append("updateBy", getUpdateBy())
            .append("updateTime", getUpdateTime())
            .append("remark", getRemark())
            .toString();
    }
}
