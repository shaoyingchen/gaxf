package com.gaxf.system.domain;

import java.util.Date;
import com.fasterxml.jackson.annotation.JsonFormat;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.gaxf.common.annotation.Excel;
import com.gaxf.common.annotation.Excel.ColumnType;
import com.gaxf.common.core.domain.BaseEntity;

/**
 * 审核记录表 xf_review_record
 *
 * @author gaxf
 */
public class XfReviewRecord extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** 审核记录ID */
    @Excel(name = "审核记录ID", cellType = ColumnType.NUMERIC)
    private Long id;

    /** 工单ID */
    @Excel(name = "工单ID", cellType = ColumnType.NUMERIC)
    private Long orderId;

    /** 审核类型: 0=县局领导审核 1=警种评查 2=专员审核 3=支队领导审核 */
    @Excel(name = "审核类型", readConverterExp = "0=县局领导审核,1=警种评查,2=专员审核,3=支队领导审核")
    private String reviewType;

    /** 审核人ID */
    private Long reviewerId;

    /** 审核人姓名 */
    @Excel(name = "审核人")
    private String reviewerName;

    /** 审核意见 */
    private String reviewOpinion;

    /** 审核结果: 0=通过 1=退回 */
    @Excel(name = "审核结果", readConverterExp = "0=通过,1=退回")
    private String reviewResult;

    /** 退回对象: 0=县局重办 1=专员重审 */
    private String returnTo;

    /** 审核时间 */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Excel(name = "审核时间", width = 30, dateFormat = "yyyy-MM-dd HH:mm:ss")
    private Date reviewTime;

    /** 审核人所属单位ID */
    private Long deptId;

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

    public String getReviewType()
    {
        return reviewType;
    }

    public void setReviewType(String reviewType)
    {
        this.reviewType = reviewType;
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

    public String getReviewOpinion()
    {
        return reviewOpinion;
    }

    public void setReviewOpinion(String reviewOpinion)
    {
        this.reviewOpinion = reviewOpinion;
    }

    public String getReviewResult()
    {
        return reviewResult;
    }

    public void setReviewResult(String reviewResult)
    {
        this.reviewResult = reviewResult;
    }

    public String getReturnTo()
    {
        return returnTo;
    }

    public void setReturnTo(String returnTo)
    {
        this.returnTo = returnTo;
    }

    public Date getReviewTime()
    {
        return reviewTime;
    }

    public void setReviewTime(Date reviewTime)
    {
        this.reviewTime = reviewTime;
    }

    public Long getDeptId()
    {
        return deptId;
    }

    public void setDeptId(Long deptId)
    {
        this.deptId = deptId;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this,ToStringStyle.MULTI_LINE_STYLE)
            .append("id", getId())
            .append("orderId", getOrderId())
            .append("reviewType", getReviewType())
            .append("reviewerId", getReviewerId())
            .append("reviewerName", getReviewerName())
            .append("reviewOpinion", getReviewOpinion())
            .append("reviewResult", getReviewResult())
            .append("returnTo", getReturnTo())
            .append("reviewTime", getReviewTime())
            .append("deptId", getDeptId())
            .append("createBy", getCreateBy())
            .append("createTime", getCreateTime())
            .toString();
    }
}
