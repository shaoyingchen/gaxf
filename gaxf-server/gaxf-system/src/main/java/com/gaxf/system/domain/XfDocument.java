package com.gaxf.system.domain;

import java.util.Date;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.gaxf.common.annotation.Excel;
import com.gaxf.common.annotation.Excel.ColumnType;
import com.gaxf.common.core.domain.BaseEntity;

/**
 * 文书表 xf_document
 *
 * @author gaxf
 */
public class XfDocument extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** 文书ID */
    @Excel(name = "文书ID", cellType = ColumnType.NUMERIC)
    private Long id;

    /** 工单ID */
    @Excel(name = "工单ID", cellType = ColumnType.NUMERIC)
    private Long orderId;

    /** 文书类型: 0=信访工作建议书 1=信访督办建议 */
    @Excel(name = "文书类型", readConverterExp = "0=信访工作建议书,1=信访督办建议")
    private String docType;

    /** 文书编号 */
    @Excel(name = "文书编号")
    private String docNo;

    /** 文书内容 */
    private String docContent;

    /** 签发人 */
    @Excel(name = "签发人")
    private String signBy;

    /** 签发时间 */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Excel(name = "签发时间", width = 30, dateFormat = "yyyy-MM-dd HH:mm:ss")
    private Date signTime;

    /** 是否已签: 0=否 1=是 */
    @Excel(name = "是否已签", readConverterExp = "0=否,1=是")
    @JsonProperty("isSigned")
    private String isSigned;

    /** 生成文件路径 */
    private String filePath;

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

    public String getDocType()
    {
        return docType;
    }

    public void setDocType(String docType)
    {
        this.docType = docType;
    }

    public String getDocNo()
    {
        return docNo;
    }

    public void setDocNo(String docNo)
    {
        this.docNo = docNo;
    }

    public String getDocContent()
    {
        return docContent;
    }

    public void setDocContent(String docContent)
    {
        this.docContent = docContent;
    }

    public String getSignBy()
    {
        return signBy;
    }

    public void setSignBy(String signBy)
    {
        this.signBy = signBy;
    }

    public Date getSignTime()
    {
        return signTime;
    }

    public void setSignTime(Date signTime)
    {
        this.signTime = signTime;
    }

    public String getIsSigned()
    {
        return isSigned;
    }

    public void setIsSigned(String isSigned)
    {
        this.isSigned = isSigned;
    }

    public String getFilePath()
    {
        return filePath;
    }

    public void setFilePath(String filePath)
    {
        this.filePath = filePath;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this,ToStringStyle.MULTI_LINE_STYLE)
            .append("id", getId())
            .append("orderId", getOrderId())
            .append("docType", getDocType())
            .append("docNo", getDocNo())
            .append("docContent", getDocContent())
            .append("signBy", getSignBy())
            .append("signTime", getSignTime())
            .append("isSigned", getIsSigned())
            .append("filePath", getFilePath())
            .append("createBy", getCreateBy())
            .append("createTime", getCreateTime())
            .append("updateBy", getUpdateBy())
            .append("updateTime", getUpdateTime())
            .append("remark", getRemark())
            .toString();
    }
}
