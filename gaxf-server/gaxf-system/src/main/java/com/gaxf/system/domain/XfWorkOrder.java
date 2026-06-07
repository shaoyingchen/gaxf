package com.gaxf.system.domain;

import java.util.Date;
import java.util.List;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.gaxf.common.annotation.Excel;
import com.gaxf.common.annotation.Excel.ColumnType;
import com.gaxf.common.core.domain.BaseEntity;

/**
 * 信访工单表 xf_work_order
 *
 * @author gaxf
 */
public class XfWorkOrder extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** 工单ID */
    @Excel(name = "工单ID", cellType = ColumnType.NUMERIC)
    private Long id;

    /** 信访件编号 */
    @Excel(name = "信访件编号")
    private String xfCaseNo;

    /** 工单标题/任务名称(手动新建) */
    @Excel(name = "工单标题")
    private String title;

    /** 来源类型: 0=Excel导入 1=手动新建 */
    @Excel(name = "来源类型", readConverterExp = "0=Excel导入,1=手动新建")
    private String sourceType;

    /** 工单内容/任务要求(富文本) */
    private String content;

    /** 姓名 */
    @Excel(name = "姓名")
    private String petitionerName;

    /** 身份证号 */
    @Excel(name = "身份证号")
    private String petitionerIdcard;

    /** 手机号码 */
    @Excel(name = "手机号码")
    private String petitionerPhone;

    /** 住址 */
    @Excel(name = "住址")
    private String petitionerAddress;

    /** 信访内容 */
    @Excel(name = "信访内容")
    private String xfContent;

    /** 登记时间 */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Excel(name = "登记时间", width = 30, dateFormat = "yyyy-MM-dd HH:mm:ss")
    private Date registerTime;

    /** 限办时间 */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Excel(name = "限办时间", width = 30, dateFormat = "yyyy-MM-dd HH:mm:ss")
    private Date deadline;

    /** 信访件状态 */
    @Excel(name = "信访件状态")
    private String xfCaseStatus;

    /** 信访形式 */
    @Excel(name = "信访形式")
    private String xfForm;

    /** 信访日期 */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Excel(name = "信访日期", width = 30, dateFormat = "yyyy-MM-dd HH:mm:ss")
    private Date xfDate;

    /** 问题发生地 */
    @Excel(name = "问题发生地")
    private String problemLocation;

    /** 异常动态 */
    @Excel(name = "异常动态")
    private String abnormalDynamic;

    /** 问题发生日期 */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Excel(name = "问题发生日期", width = 30, dateFormat = "yyyy-MM-dd HH:mm:ss")
    private Date problemDate;

    /** 信访人数 */
    @Excel(name = "信访人数", cellType = ColumnType.NUMERIC)
    private Integer petitionerCount;

    /** 信访诉求 */
    @Excel(name = "信访诉求")
    private String xfDemand;

    /** 公安业务分类 */
    @Excel(name = "公安业务分类")
    private String businessCategory;

    /** 登记单位 */
    @Excel(name = "登记单位")
    private String registerUnit;

    /** 档案编号 */
    @Excel(name = "档案编号")
    private String archiveNo;

    /** 转往处 */
    @Excel(name = "转往处")
    private String transferDest;

    /** 办理方式 */
    @Excel(name = "办理方式")
    private String handleMethod;

    /** 具体承办单位 */
    @Excel(name = "具体承办单位")
    private String specificHandleUnit;

    /** 办结状态 */
    @Excel(name = "办结状态")
    private String completionStatus;

    /** 责任部门 */
    @Excel(name = "责任部门")
    private String responsibleDept;

    /** 办结时间 */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Excel(name = "办结时间", width = 30, dateFormat = "yyyy-MM-dd HH:mm:ss")
    private Date completionTime;

    /** 信访事项编号 */
    @Excel(name = "信访事项编号")
    private String xfItemNo;

    /** 警种名称 */
    @Excel(name = "警种名称")
    private String policeTypeName;

    /** 信访目的(登记单位) */
    @Excel(name = "信访目的(登记单位)")
    private String xfPurpose;

    /** 工单状态: 0=待派单 1=待提交 2=审批中 3=已办结 4=已退回 5=已超期 */
    @Excel(name = "工单状态", readConverterExp = "0=待派单,1=待提交,2=审批中,3=已办结,4=已退回,5=已超期")
    private String status;

    /** 超期次数 */
    @Excel(name = "超期次数", cellType = ColumnType.NUMERIC)
    private Integer overdueCount;

    /** 是否锁定: 0=否 1=是 */
    @JsonProperty("isLocked")
    private String isLocked;

    /** 是否置顶: 0=否 1=是 */
    @JsonProperty("isTop")
    private String isTop;

    /** 创建部门ID */
    private Long createDeptId;

    /** 删除标志: 0=存在 2=删除 */
    private String delFlag;

    /** 交办记录列表（非数据库字段） */
    private List<XfAssignRecord> assignRecords;

    /** 当前用户的交办记录ID（非数据库字段，myTodo查询使用） */
    private Long assignId;

    /** 最新审批实例ID（非数据库字段） */
    private Long approveInstanceId;

    /** 审批状态（非数据库字段） */
    private String approveStatus;

    /** 当前审批阶段（非数据库字段） */
    private Integer currentApproveStage;

    /** 审批任务列表（非数据库字段） */
    private List<XfApproveTask> approveTaskList;

    /** 承办单位首审任务列表（非数据库字段） */
    private List<XfApproveTask> branchTaskList;

    public Long getId()
    {
        return id;
    }

    public void setId(Long id)
    {
        this.id = id;
    }

    public String getXfCaseNo()
    {
        return xfCaseNo;
    }

    public void setXfCaseNo(String xfCaseNo)
    {
        this.xfCaseNo = xfCaseNo;
    }

    public String getTitle()
    {
        return title;
    }

    public void setTitle(String title)
    {
        this.title = title;
    }

    public String getSourceType()
    {
        return sourceType;
    }

    public void setSourceType(String sourceType)
    {
        this.sourceType = sourceType;
    }

    public String getContent()
    {
        return content;
    }

    public void setContent(String content)
    {
        this.content = content;
    }

    public String getPetitionerName()
    {
        return petitionerName;
    }

    public void setPetitionerName(String petitionerName)
    {
        this.petitionerName = petitionerName;
    }

    public String getPetitionerIdcard()
    {
        return petitionerIdcard;
    }

    public void setPetitionerIdcard(String petitionerIdcard)
    {
        this.petitionerIdcard = petitionerIdcard;
    }

    public String getPetitionerPhone()
    {
        return petitionerPhone;
    }

    public void setPetitionerPhone(String petitionerPhone)
    {
        this.petitionerPhone = petitionerPhone;
    }

    public String getPetitionerAddress()
    {
        return petitionerAddress;
    }

    public void setPetitionerAddress(String petitionerAddress)
    {
        this.petitionerAddress = petitionerAddress;
    }

    public String getXfContent()
    {
        return xfContent;
    }

    public void setXfContent(String xfContent)
    {
        this.xfContent = xfContent;
    }

    public String getXfCaseStatus()
    {
        return xfCaseStatus;
    }

    public void setXfCaseStatus(String xfCaseStatus)
    {
        this.xfCaseStatus = xfCaseStatus;
    }

    public String getXfForm()
    {
        return xfForm;
    }

    public void setXfForm(String xfForm)
    {
        this.xfForm = xfForm;
    }

    public Date getXfDate()
    {
        return xfDate;
    }

    public void setXfDate(Date xfDate)
    {
        this.xfDate = xfDate;
    }

    public String getProblemLocation()
    {
        return problemLocation;
    }

    public void setProblemLocation(String problemLocation)
    {
        this.problemLocation = problemLocation;
    }

    public String getAbnormalDynamic()
    {
        return abnormalDynamic;
    }

    public void setAbnormalDynamic(String abnormalDynamic)
    {
        this.abnormalDynamic = abnormalDynamic;
    }

    public Date getProblemDate()
    {
        return problemDate;
    }

    public void setProblemDate(Date problemDate)
    {
        this.problemDate = problemDate;
    }

    public Integer getPetitionerCount()
    {
        return petitionerCount;
    }

    public void setPetitionerCount(Integer petitionerCount)
    {
        this.petitionerCount = petitionerCount;
    }

    public String getXfDemand()
    {
        return xfDemand;
    }

    public void setXfDemand(String xfDemand)
    {
        this.xfDemand = xfDemand;
    }

    public String getBusinessCategory()
    {
        return businessCategory;
    }

    public void setBusinessCategory(String businessCategory)
    {
        this.businessCategory = businessCategory;
    }

    public String getRegisterUnit()
    {
        return registerUnit;
    }

    public void setRegisterUnit(String registerUnit)
    {
        this.registerUnit = registerUnit;
    }

    public String getArchiveNo()
    {
        return archiveNo;
    }

    public void setArchiveNo(String archiveNo)
    {
        this.archiveNo = archiveNo;
    }

    public String getTransferDest()
    {
        return transferDest;
    }

    public void setTransferDest(String transferDest)
    {
        this.transferDest = transferDest;
    }

    public String getHandleMethod()
    {
        return handleMethod;
    }

    public void setHandleMethod(String handleMethod)
    {
        this.handleMethod = handleMethod;
    }

    public String getSpecificHandleUnit()
    {
        return specificHandleUnit;
    }

    public void setSpecificHandleUnit(String specificHandleUnit)
    {
        this.specificHandleUnit = specificHandleUnit;
    }

    public String getCompletionStatus()
    {
        return completionStatus;
    }

    public void setCompletionStatus(String completionStatus)
    {
        this.completionStatus = completionStatus;
    }

    public String getResponsibleDept()
    {
        return responsibleDept;
    }

    public void setResponsibleDept(String responsibleDept)
    {
        this.responsibleDept = responsibleDept;
    }

    public Date getCompletionTime()
    {
        return completionTime;
    }

    public void setCompletionTime(Date completionTime)
    {
        this.completionTime = completionTime;
    }

    public String getXfItemNo()
    {
        return xfItemNo;
    }

    public void setXfItemNo(String xfItemNo)
    {
        this.xfItemNo = xfItemNo;
    }

    public String getPoliceTypeName()
    {
        return policeTypeName;
    }

    public void setPoliceTypeName(String policeTypeName)
    {
        this.policeTypeName = policeTypeName;
    }

    public String getXfPurpose()
    {
        return xfPurpose;
    }

    public void setXfPurpose(String xfPurpose)
    {
        this.xfPurpose = xfPurpose;
    }

    public Date getRegisterTime()
    {
        return registerTime;
    }

    public void setRegisterTime(Date registerTime)
    {
        this.registerTime = registerTime;
    }

    public Date getDeadline()
    {
        return deadline;
    }

    public void setDeadline(Date deadline)
    {
        this.deadline = deadline;
    }

    public String getStatus()
    {
        return status;
    }

    public void setStatus(String status)
    {
        this.status = status;
    }

    public Integer getOverdueCount()
    {
        return overdueCount;
    }

    public void setOverdueCount(Integer overdueCount)
    {
        this.overdueCount = overdueCount;
    }

    public String getIsLocked()
    {
        return isLocked;
    }

    public void setIsLocked(String isLocked)
    {
        this.isLocked = isLocked;
    }

    public String getIsTop()
    {
        return isTop;
    }

    public void setIsTop(String isTop)
    {
        this.isTop = isTop;
    }

    public Long getCreateDeptId()
    {
        return createDeptId;
    }

    public void setCreateDeptId(Long createDeptId)
    {
        this.createDeptId = createDeptId;
    }

    public String getDelFlag()
    {
        return delFlag;
    }

    public void setDelFlag(String delFlag)
    {
        this.delFlag = delFlag;
    }

    public List<XfAssignRecord> getAssignRecords()
    {
        return assignRecords;
    }

    public void setAssignRecords(List<XfAssignRecord> assignRecords)
    {
        this.assignRecords = assignRecords;
    }

    public Long getAssignId()
    {
        return assignId;
    }

    public void setAssignId(Long assignId)
    {
        this.assignId = assignId;
    }

    public Long getApproveInstanceId()
    {
        return approveInstanceId;
    }

    public void setApproveInstanceId(Long approveInstanceId)
    {
        this.approveInstanceId = approveInstanceId;
    }

    public String getApproveStatus()
    {
        return approveStatus;
    }

    public void setApproveStatus(String approveStatus)
    {
        this.approveStatus = approveStatus;
    }

    public Integer getCurrentApproveStage()
    {
        return currentApproveStage;
    }

    public void setCurrentApproveStage(Integer currentApproveStage)
    {
        this.currentApproveStage = currentApproveStage;
    }

    public List<XfApproveTask> getApproveTaskList()
    {
        return approveTaskList;
    }

    public void setApproveTaskList(List<XfApproveTask> approveTaskList)
    {
        this.approveTaskList = approveTaskList;
    }

    public List<XfApproveTask> getBranchTaskList()
    {
        return branchTaskList;
    }

    public void setBranchTaskList(List<XfApproveTask> branchTaskList)
    {
        this.branchTaskList = branchTaskList;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this,ToStringStyle.MULTI_LINE_STYLE)
            .append("id", getId())
            .append("xfCaseNo", getXfCaseNo())
            .append("title", getTitle())
            .append("sourceType", getSourceType())
            .append("content", getContent())
            .append("petitionerName", getPetitionerName())
            .append("petitionerIdcard", getPetitionerIdcard())
            .append("petitionerPhone", getPetitionerPhone())
            .append("petitionerAddress", getPetitionerAddress())
            .append("xfContent", getXfContent())
            .append("registerTime", getRegisterTime())
            .append("deadline", getDeadline())
            .append("xfCaseStatus", getXfCaseStatus())
            .append("xfForm", getXfForm())
            .append("xfDate", getXfDate())
            .append("problemLocation", getProblemLocation())
            .append("abnormalDynamic", getAbnormalDynamic())
            .append("problemDate", getProblemDate())
            .append("petitionerCount", getPetitionerCount())
            .append("xfDemand", getXfDemand())
            .append("businessCategory", getBusinessCategory())
            .append("registerUnit", getRegisterUnit())
            .append("archiveNo", getArchiveNo())
            .append("transferDest", getTransferDest())
            .append("handleMethod", getHandleMethod())
            .append("specificHandleUnit", getSpecificHandleUnit())
            .append("completionStatus", getCompletionStatus())
            .append("responsibleDept", getResponsibleDept())
            .append("completionTime", getCompletionTime())
            .append("xfItemNo", getXfItemNo())
            .append("policeTypeName", getPoliceTypeName())
            .append("xfPurpose", getXfPurpose())
            .append("status", getStatus())
            .append("overdueCount", getOverdueCount())
            .append("isLocked", getIsLocked())
            .append("isTop", getIsTop())
            .append("createDeptId", getCreateDeptId())
            .append("delFlag", getDelFlag())
            .append("assignId", getAssignId())
            .append("approveInstanceId", getApproveInstanceId())
            .append("approveStatus", getApproveStatus())
            .append("currentApproveStage", getCurrentApproveStage())
            .append("createBy", getCreateBy())
            .append("createTime", getCreateTime())
            .append("updateBy", getUpdateBy())
            .append("updateTime", getUpdateTime())
            .append("remark", getRemark())
            .toString();
    }
}
