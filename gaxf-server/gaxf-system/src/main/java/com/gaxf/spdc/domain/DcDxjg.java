package com.gaxf.spdc.domain;

import java.util.Date;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.gaxf.common.annotation.Excel;
import com.gaxf.common.core.domain.BaseEntity;

/**
 * 预警管理对象 dc_dxjg
 *
 * @author spdc
 * @date 2026-04-30
 */
public class DcDxjg extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** 主键 */
    private Integer id;

    /** 问题单位编码 */
    @Excel(name = "问题单位编码")
    private String orgid;

    /** 预警具体类型（2级） */
    @Excel(name = "预警类型")
    private String warningtype;

    /** 单位名称 */
    @Excel(name = "单位名称")
    private String policename;

    /** 县局名 */
    @Excel(name = "县局名")
    private String countyname;

    /** 摄像机的编码 */
    @Excel(name = "摄像机编码")
    private String orgtableid;

    /** 预警时间 */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Excel(name = "预警时间", width = 30, dateFormat = "yyyy-MM-dd HH:mm:ss")
    private Date time;

    /** 处置状态（旧） */
    private String state;

    /** 预警内容 */
    @Excel(name = "预警内容")
    private String msgtitle;

    /** 问题图片 */
    private String pic1;

    /** 第二个摄像头预警的图片 */
    private String pic2;

    /** 预警大分类（1级） */
    @Excel(name = "预警大类")
    private String type;

    /** 预警接收人 */
    private Integer acceptUserid;

    /** 发送状态 */
    private Integer sendsate;

    /** 部门ID（数据权限关键） */
    @Excel(name = "部门ID")
    private Integer departId;

    /** 摄像机的ID */
    @Excel(name = "摄像机ID")
    private Integer cameraId;

    /** 事件编号 */
    private String eventCode;

    /** 处置结果 */
    @Excel(name = "处置结果")
    private String result;

    /** 处置状态（新） */
    @Excel(name = "处置状态", readConverterExp = "01=已核实,02=已处置,03=待处理")
    private String deal;

    /** 区域类型 */
    private String zoneType;

    /** 房间类型 */
    private String roomType;

    /** 县局名字 */
    private String county;

    /** 预警大类 */
    private String mainType;

    /** 是否处理（0未，1处） */
    private Integer isResolved;

    /** 删除标志 */
    private String deleted;

    /** 数据范围过滤：用户部门ID（非管理员查询时使用） */
    private Long userDeptId;

    /** 部门名称（关联查询） */
    private String deptName;

    /** 摄像机名称（关联查询） */
    private String cameraName;

    /** 今日预警数（统计字段） */
    private Integer todayCount;

    /** 待核实数（统计字段） */
    private Integer verifyCount;

    /** 待处理数（统计字段） */
    private Integer pendingCount;

    /** 已处置数（统计字段） */
    private Integer resolvedCount;

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

    public void setOrgid(String orgid)
    {
        this.orgid = orgid;
    }

    public String getOrgid()
    {
        return orgid;
    }

    public void setWarningtype(String warningtype)
    {
        this.warningtype = warningtype;
    }

    public String getWarningtype()
    {
        return warningtype;
    }

    public void setPolicename(String policename)
    {
        this.policename = policename;
    }

    public String getPolicename()
    {
        return policename;
    }

    public void setCountyname(String countyname)
    {
        this.countyname = countyname;
    }

    public String getCountyname()
    {
        return countyname;
    }

    public void setOrgtableid(String orgtableid)
    {
        this.orgtableid = orgtableid;
    }

    public String getOrgtableid()
    {
        return orgtableid;
    }

    public void setTime(Date time)
    {
        this.time = time;
    }

    public Date getTime()
    {
        return time;
    }

    public void setState(String state)
    {
        this.state = state;
    }

    public String getState()
    {
        return state;
    }

    public void setMsgtitle(String msgtitle)
    {
        this.msgtitle = msgtitle;
    }

    public String getMsgtitle()
    {
        return msgtitle;
    }

    public void setPic1(String pic1)
    {
        this.pic1 = pic1;
    }

    public String getPic1()
    {
        return pic1;
    }

    public void setPic2(String pic2)
    {
        this.pic2 = pic2;
    }

    public String getPic2()
    {
        return pic2;
    }

    public void setType(String type)
    {
        this.type = type;
    }

    public String getType()
    {
        return type;
    }

    public void setAcceptUserid(Integer acceptUserid)
    {
        this.acceptUserid = acceptUserid;
    }

    public Integer getAcceptUserid()
    {
        return acceptUserid;
    }

    public void setSendsate(Integer sendsate)
    {
        this.sendsate = sendsate;
    }

    public Integer getSendsate()
    {
        return sendsate;
    }

    public void setDepartId(Integer departId)
    {
        this.departId = departId;
    }

    public Integer getDepartId()
    {
        return departId;
    }

    public void setCameraId(Integer cameraId)
    {
        this.cameraId = cameraId;
    }

    public Integer getCameraId()
    {
        return cameraId;
    }

    public void setEventCode(String eventCode)
    {
        this.eventCode = eventCode;
    }

    public String getEventCode()
    {
        return eventCode;
    }

    public void setResult(String result)
    {
        this.result = result;
    }

    public String getResult()
    {
        return result;
    }

    public void setDeal(String deal)
    {
        this.deal = deal;
    }

    public String getDeal()
    {
        return deal;
    }

    public void setZoneType(String zoneType)
    {
        this.zoneType = zoneType;
    }

    public String getZoneType()
    {
        return zoneType;
    }

    public void setRoomType(String roomType)
    {
        this.roomType = roomType;
    }

    public String getRoomType()
    {
        return roomType;
    }

    public void setCounty(String county)
    {
        this.county = county;
    }

    public String getCounty()
    {
        return county;
    }

    public void setMainType(String mainType)
    {
        this.mainType = mainType;
    }

    public String getMainType()
    {
        return mainType;
    }

    public void setIsResolved(Integer isResolved)
    {
        this.isResolved = isResolved;
    }

    public Integer getIsResolved()
    {
        return isResolved;
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

    public void setCameraName(String cameraName)
    {
        this.cameraName = cameraName;
    }

    public String getCameraName()
    {
        return cameraName;
    }

    public void setTodayCount(Integer todayCount)
    {
        this.todayCount = todayCount;
    }

    public Integer getTodayCount()
    {
        return todayCount;
    }

    public void setVerifyCount(Integer verifyCount)
    {
        this.verifyCount = verifyCount;
    }

    public Integer getVerifyCount()
    {
        return verifyCount;
    }

    public void setPendingCount(Integer pendingCount)
    {
        this.pendingCount = pendingCount;
    }

    public Integer getPendingCount()
    {
        return pendingCount;
    }

    public void setResolvedCount(Integer resolvedCount)
    {
        this.resolvedCount = resolvedCount;
    }

    public Integer getResolvedCount()
    {
        return resolvedCount;
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
        return "DcDxjg{" +
            "id=" + id +
            ", orgid='" + orgid + '\'' +
            ", warningtype='" + warningtype + '\'' +
            ", policename='" + policename + '\'' +
            ", time=" + time +
            ", msgtitle='" + msgtitle + '\'' +
            ", deal='" + deal + '\'' +
            ", departId=" + departId +
            '}';
    }
}