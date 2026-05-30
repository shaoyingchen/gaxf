package com.gaxf.spdc.domain;

import java.util.Date;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.gaxf.common.annotation.Excel;
import com.gaxf.common.core.domain.BaseEntity;

/**
 * 问题整改对象 dc_pga
 *
 * @author spdc
 * @date 2026-04-30
 */
public class DcPga extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** 主键 */
    private Integer id;

    /** 关联预警ID */
    @Excel(name = "预警ID")
    private Integer dxjgId;

    /** 问题单位名称 */
    @Excel(name = "问题单位")
    private String problemDepart;

    /** 处置方式（字典） */
    @Excel(name = "处置方式")
    private String dealtype;

    /** 问题性质（字典） */
    @Excel(name = "问题性质")
    private String wtxz;

    /** 整改要求(字典) */
    @Excel(name = "整改要求")
    private String adjust;

    /** 处理意见（字典） */
    @Excel(name = "处理意见")
    private String dealPropose;

    /** 问题大分类（字典）一级分类 */
    @Excel(name = "问题大类")
    private String typeLevelone;

    /** 详细问题（字典问题二级分类） */
    @Excel(name = "详细问题")
    private String typeLeveltwo;

    /** 问题图片 */
    private String pic1;

    /** 问题附件 */
    private String fujian;

    /** 设备编码id */
    private Double camareCode;

    /** 问题短信内容 */
    @Excel(name = "问题内容")
    private String problemMsg;

    /** 发现时间 */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Excel(name = "发现时间", width = 30, dateFormat = "yyyy-MM-dd HH:mm:ss")
    private Date findtime;

    /** 所在县局 */
    @Excel(name = "县局")
    private String countyname;

    /** 派出所名字 */
    @Excel(name = "派出所")
    private String policename;

    /** 单位全称 */
    private String danweiallname;

    /** 警种 */
    private String jz;

    /** 单位编码code */
    private String orgcode;

    /** 整改状态 */
    @Excel(name = "整改状态")
    private String state;

    /** 处置时间 */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date dealTime;

    /** 反馈状态 */
    @Excel(name = "反馈状态")
    private String dealState;

    /** 整改图片 */
    private String dealPic;

    /** 反馈附件 */
    private String dealFujian;

    /** 处置时限 */
    private String deadline;

    /** 反馈情况 */
    private String dealConent;

    /** 部门id */
    @Excel(name = "部门ID")
    private Integer departId;

    /** 设备id */
    private Integer camareId;

    /** 问题大分类code */
    private Integer typeLeveloneCode;

    /** 详细问题code */
    private Integer typeLeveltwoCode;

    /** 数据状态 0草稿 1提交 */
    private Integer status;

    /** 审核状态 0未审核 1已审核 */
    private Integer aduitStatus;

    /** 审核备注 */
    private String remark;

    /** 问题处理部门 */
    private Integer receiveDeptId;

    /** 结果 */
    private Integer result;

    /** 删除标志 */
    private String deleted;

    /** 数据范围过滤：用户部门ID */
    private Long userDeptId;

    /** 部门名称（关联查询） */
    private String deptName;

    /** 创建人ID */
    private Integer creator;

    /** 更新人ID */
    private Integer updater;

    public void setId(Integer id)
    {
        this.id = id;
    }

    public Integer getId()
    {
        return id;
    }

    public void setDxjgId(Integer dxjgId)
    {
        this.dxjgId = dxjgId;
    }

    public Integer getDxjgId()
    {
        return dxjgId;
    }

    public void setProblemDepart(String problemDepart)
    {
        this.problemDepart = problemDepart;
    }

    public String getProblemDepart()
    {
        return problemDepart;
    }

    public void setDealtype(String dealtype)
    {
        this.dealtype = dealtype;
    }

    public String getDealtype()
    {
        return dealtype;
    }

    public void setWtxz(String wtxz)
    {
        this.wtxz = wtxz;
    }

    public String getWtxz()
    {
        return wtxz;
    }

    public void setAdjust(String adjust)
    {
        this.adjust = adjust;
    }

    public String getAdjust()
    {
        return adjust;
    }

    public void setDealPropose(String dealPropose)
    {
        this.dealPropose = dealPropose;
    }

    public String getDealPropose()
    {
        return dealPropose;
    }

    public void setTypeLevelone(String typeLevelone)
    {
        this.typeLevelone = typeLevelone;
    }

    public String getTypeLevelone()
    {
        return typeLevelone;
    }

    public void setTypeLeveltwo(String typeLeveltwo)
    {
        this.typeLeveltwo = typeLeveltwo;
    }

    public String getTypeLeveltwo()
    {
        return typeLeveltwo;
    }

    public void setPic1(String pic1)
    {
        this.pic1 = pic1;
    }

    public String getPic1()
    {
        return pic1;
    }

    public void setFujian(String fujian)
    {
        this.fujian = fujian;
    }

    public String getFujian()
    {
        return fujian;
    }

    public void setCamareCode(Double camareCode)
    {
        this.camareCode = camareCode;
    }

    public Double getCamareCode()
    {
        return camareCode;
    }

    public void setProblemMsg(String problemMsg)
    {
        this.problemMsg = problemMsg;
    }

    public String getProblemMsg()
    {
        return problemMsg;
    }

    public void setFindtime(Date findtime)
    {
        this.findtime = findtime;
    }

    public Date getFindtime()
    {
        return findtime;
    }

    public void setCountyname(String countyname)
    {
        this.countyname = countyname;
    }

    public String getCountyname()
    {
        return countyname;
    }

    public void setPolicename(String policename)
    {
        this.policename = policename;
    }

    public String getPolicename()
    {
        return policename;
    }

    public void setDanweiallname(String danweiallname)
    {
        this.danweiallname = danweiallname;
    }

    public String getDanweiallname()
    {
        return danweiallname;
    }

    public void setJz(String jz)
    {
        this.jz = jz;
    }

    public String getJz()
    {
        return jz;
    }

    public void setOrgcode(String orgcode)
    {
        this.orgcode = orgcode;
    }

    public String getOrgcode()
    {
        return orgcode;
    }

    public void setState(String state)
    {
        this.state = state;
    }

    public String getState()
    {
        return state;
    }

    public void setDealTime(Date dealTime)
    {
        this.dealTime = dealTime;
    }

    public Date getDealTime()
    {
        return dealTime;
    }

    public void setDealState(String dealState)
    {
        this.dealState = dealState;
    }

    public String getDealState()
    {
        return dealState;
    }

    public void setDealPic(String dealPic)
    {
        this.dealPic = dealPic;
    }

    public String getDealPic()
    {
        return dealPic;
    }

    public void setDealFujian(String dealFujian)
    {
        this.dealFujian = dealFujian;
    }

    public String getDealFujian()
    {
        return dealFujian;
    }

    public void setDeadline(String deadline)
    {
        this.deadline = deadline;
    }

    public String getDeadline()
    {
        return deadline;
    }

    public void setDealConent(String dealConent)
    {
        this.dealConent = dealConent;
    }

    public String getDealConent()
    {
        return dealConent;
    }

    public void setDepartId(Integer departId)
    {
        this.departId = departId;
    }

    public Integer getDepartId()
    {
        return departId;
    }

    public void setCamareId(Integer camareId)
    {
        this.camareId = camareId;
    }

    public Integer getCamareId()
    {
        return camareId;
    }

    public void setTypeLeveloneCode(Integer typeLeveloneCode)
    {
        this.typeLeveloneCode = typeLeveloneCode;
    }

    public Integer getTypeLeveloneCode()
    {
        return typeLeveloneCode;
    }

    public void setTypeLeveltwoCode(Integer typeLeveltwoCode)
    {
        this.typeLeveltwoCode = typeLeveltwoCode;
    }

    public Integer getTypeLeveltwoCode()
    {
        return typeLeveltwoCode;
    }

    public void setStatus(Integer status)
    {
        this.status = status;
    }

    public Integer getStatus()
    {
        return status;
    }

    public void setAduitStatus(Integer aduitStatus)
    {
        this.aduitStatus = aduitStatus;
    }

    public Integer getAduitStatus()
    {
        return aduitStatus;
    }

    public void setRemark(String remark)
    {
        this.remark = remark;
    }

    public String getRemark()
    {
        return remark;
    }

    public void setReceiveDeptId(Integer receiveDeptId)
    {
        this.receiveDeptId = receiveDeptId;
    }

    public Integer getReceiveDeptId()
    {
        return receiveDeptId;
    }

    public void setResult(Integer result)
    {
        this.result = result;
    }

    public Integer getResult()
    {
        return result;
    }

    public void setDeleted(String deleted)
    {
        this.deleted = deleted;
    }

    public String getDeleted()
    {
        return deleted;
    }

    public void setUserDeptId(Long userDeptId)
    {
        this.userDeptId = userDeptId;
    }

    public Long getUserDeptId()
    {
        return userDeptId;
    }

    public void setDeptName(String deptName)
    {
        this.deptName = deptName;
    }

    public String getDeptName()
    {
        return deptName;
    }

    public void setCreator(Integer creator)
    {
        this.creator = creator;
    }

    public Integer getCreator()
    {
        return creator;
    }

    public void setUpdater(Integer updater)
    {
        this.updater = updater;
    }

    public Integer getUpdater()
    {
        return updater;
    }

    @Override
    public String toString() {
        return "DcPga{" +
            "id=" + id +
            ", dxjgId=" + dxjgId +
            ", problemDepart='" + problemDepart + '\'' +
            ", dealtype='" + dealtype + '\'' +
            ", state='" + state + '\'' +
            ", departId=" + departId +
            '}';
    }
}