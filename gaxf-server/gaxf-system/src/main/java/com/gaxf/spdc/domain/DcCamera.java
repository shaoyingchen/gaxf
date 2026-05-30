package com.gaxf.spdc.domain;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.gaxf.common.annotation.Excel;
import com.gaxf.common.core.domain.BaseEntity;

/**
 * 点位管理对象 dc_cameratable
 *
 * @author ruoyi
 * @date 2026-04-15
 */
public class DcCamera extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** 点位主键 */
    private Integer pointId;

    /** 国标编码（20位） */
    @Excel(name = "国标编码")
    private String gbCode;

    /** 部门ID（所属单位，关联 system_dept） */
    @Excel(name = "部门ID")
    private Integer deptId;

    /** 部门名称（冗余字段） */
    @Excel(name = "部门名称")
    private String deptName;

    /** 点位名称 */
    @Excel(name = "点位名称")
    private String pointName;

    /** 房间类型 */
    @Excel(name = "房间类型")
    private String roomKind;

    /** 设备类型ID */
    @Excel(name = "设备类型ID")
    private Integer zoneTypeId;

    /** 所属区域 */
    @Excel(name = "所属区域")
    private String zoneType;

    /** 视频分析类型 */
    @Excel(name = "视频分析类型")
    private Integer upAlarm;

    /** IP地址 */
    @Excel(name = "IP地址")
    private String ipAddress;

    /** 端口号 */
    @Excel(name = "端口号")
    private String port;

    /** 摄像机用户名 */
    @Excel(name = "用户名")
    private String username;

    /** 摄像机密码 */
    @Excel(name = "密码")
    private String password;

    /** 摄像机通道 */
    @Excel(name = "通道号")
    private Integer channelNo;

    /** 摄像机类型(1表示海康 2表示大华) */
    @Excel(name = "摄像机类型", readConverterExp = "1=海康,2=大华")
    private Integer cameraType;

    /** 县级公安局 */
    @Excel(name = "县级公安局")
    private String county;

    /** 市局名称 */
    @Excel(name = "市局名称")
    private String city;

    /** 警种 */
    @Excel(name = "警种")
    private String jz;

    /** 摄像机状态（1表示在线 0表示不在线） */
    @Excel(name = "点位状态", readConverterExp = "1=在线,0=离线")
    private Integer pointStatus;

    /** 摄像机状态原因备注 */
    @Excel(name = "状态备注")
    private String connState;

    /** 检测人数（视频分析） */
    @Excel(name = "检测人数")
    private Integer personNum;

    /** 设备的编码（类似id） */
    @Excel(name = "设备编码")
    private String code;

    /** 配置国标的状态 */
    @Excel(name = "国标状态")
    private Integer gbState;

    /** 单位编码 */
    @Excel(name = "单位编码")
    private String parentCode;

    /** 分析状态 为0 不分析 */
    @Excel(name = "分析状态")
    private Integer detect;

    /** 必须接入的标志 */
    @Excel(name = "必须接入")
    private Integer mustAcc;

    /** 视频分析备注 */
    @Excel(name = "分析备注")
    private String detectTag;

    /** 计数 */
    @Excel(name = "计数")
    private Integer cCount;

    /** 责任人 */
    @Excel(name = "责任人")
    private String dutyUser;

    /** 责任电话 */
    @Excel(name = "责任电话")
    private String dutyTel;

    /** 删除标志（0代表存在 1代表删除） */
    private String delFlag;

    /** 设备禁用标志（0禁用 1启用） */
    @Excel(name = "启用状态", readConverterExp = "0=禁用,1=启用")
    private Integer enable;

    /** 数据范围过滤：用户部门ID（非管理员查询时使用） */
    private Long userDeptId;

    public void setPointId(Integer pointId)
    {
        this.pointId = pointId;
    }

    public Integer getPointId()
    {
        return pointId;
    }

    public void setGbCode(String gbCode)
    {
        this.gbCode = gbCode;
    }

    public String getGbCode()
    {
        return gbCode;
    }

    public void setDeptId(Integer deptId)
    {
        this.deptId = deptId;
    }

    public Integer getDeptId()
    {
        return deptId;
    }

    public void setDeptName(String deptName)
    {
        this.deptName = deptName;
    }

    public String getDeptName()
    {
        return deptName;
    }

    public void setPointName(String pointName)
    {
        this.pointName = pointName;
    }

    public String getPointName()
    {
        return pointName;
    }

    public void setRoomKind(String roomKind)
    {
        this.roomKind = roomKind;
    }

    public String getRoomKind()
    {
        return roomKind;
    }

    public void setZoneTypeId(Integer zoneTypeId)
    {
        this.zoneTypeId = zoneTypeId;
    }

    public Integer getZoneTypeId()
    {
        return zoneTypeId;
    }

    public void setZoneType(String zoneType)
    {
        this.zoneType = zoneType;
    }

    public String getZoneType()
    {
        return zoneType;
    }

    public void setUpAlarm(Integer upAlarm)
    {
        this.upAlarm = upAlarm;
    }

    public Integer getUpAlarm()
    {
        return upAlarm;
    }

    public void setIpAddress(String ipAddress)
    {
        this.ipAddress = ipAddress;
    }

    public String getIpAddress()
    {
        return ipAddress;
    }

    public void setPort(String port)
    {
        this.port = port;
    }

    public String getPort()
    {
        return port;
    }

    public void setUsername(String username)
    {
        this.username = username;
    }

    public String getUsername()
    {
        return username;
    }

    public void setPassword(String password)
    {
        this.password = password;
    }

    public String getPassword()
    {
        return password;
    }

    public void setChannelNo(Integer channelNo)
    {
        this.channelNo = channelNo;
    }

    public Integer getChannelNo()
    {
        return channelNo;
    }

    public void setCameraType(Integer cameraType)
    {
        this.cameraType = cameraType;
    }

    public Integer getCameraType()
    {
        return cameraType;
    }

    public void setCounty(String county)
    {
        this.county = county;
    }

    public String getCounty()
    {
        return county;
    }

    public void setCity(String city)
    {
        this.city = city;
    }

    public String getCity()
    {
        return city;
    }

    public void setJz(String jz)
    {
        this.jz = jz;
    }

    public String getJz()
    {
        return jz;
    }

    public void setPointStatus(Integer pointStatus)
    {
        this.pointStatus = pointStatus;
    }

    public Integer getPointStatus()
    {
        return pointStatus;
    }

    public void setConnState(String connState)
    {
        this.connState = connState;
    }

    public String getConnState()
    {
        return connState;
    }

    public void setPersonNum(Integer personNum)
    {
        this.personNum = personNum;
    }

    public Integer getPersonNum()
    {
        return personNum;
    }

    public void setCode(String code)
    {
        this.code = code;
    }

    public String getCode()
    {
        return code;
    }

    public void setGbState(Integer gbState)
    {
        this.gbState = gbState;
    }

    public Integer getGbState()
    {
        return gbState;
    }

    public void setParentCode(String parentCode)
    {
        this.parentCode = parentCode;
    }

    public String getParentCode()
    {
        return parentCode;
    }

    public void setDetect(Integer detect)
    {
        this.detect = detect;
    }

    public Integer getDetect()
    {
        return detect;
    }

    public void setMustAcc(Integer mustAcc)
    {
        this.mustAcc = mustAcc;
    }

    public Integer getMustAcc()
    {
        return mustAcc;
    }

    public void setDetectTag(String detectTag)
    {
        this.detectTag = detectTag;
    }

    public String getDetectTag()
    {
        return detectTag;
    }

    public void setcCount(Integer cCount)
    {
        this.cCount = cCount;
    }

    public Integer getcCount()
    {
        return cCount;
    }

    public void setDutyUser(String dutyUser)
    {
        this.dutyUser = dutyUser;
    }

    public String getDutyUser()
    {
        return dutyUser;
    }

    public void setDutyTel(String dutyTel)
    {
        this.dutyTel = dutyTel;
    }

    public String getDutyTel()
    {
        return dutyTel;
    }

    public void setDelFlag(String delFlag)
    {
        this.delFlag = delFlag;
    }

    public String getDelFlag()
    {
        return delFlag;
    }

    public void setEnable(Integer enable)
    {
        this.enable = enable;
    }

    public Integer getEnable()
    {
        return enable;
    }

    public void setUserDeptId(Long userDeptId)
    {
        this.userDeptId = userDeptId;
    }

    public Long getUserDeptId()
    {
        return userDeptId;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this,ToStringStyle.MULTI_LINE_STYLE)
            .append("pointId", getPointId())
            .append("gbCode", getGbCode())
            .append("deptId", getDeptId())
            .append("deptName", getDeptName())
            .append("pointName", getPointName())
            .append("roomKind", getRoomKind())
            .append("zoneTypeId", getZoneTypeId())
            .append("zoneType", getZoneType())
            .append("upAlarm", getUpAlarm())
            .append("ipAddress", getIpAddress())
            .append("port", getPort())
            .append("username", getUsername())
            .append("password", getPassword())
            .append("channelNo", getChannelNo())
            .append("cameraType", getCameraType())
            .append("county", getCounty())
            .append("city", getCity())
            .append("jz", getJz())
            .append("pointStatus", getPointStatus())
            .append("connState", getConnState())
            .append("personNum", getPersonNum())
            .append("code", getCode())
            .append("gbState", getGbState())
            .append("parentCode", getParentCode())
            .append("detect", getDetect())
            .append("mustAcc", getMustAcc())
            .append("detectTag", getDetectTag())
            .append("cCount", getcCount())
            .append("dutyUser", getDutyUser())
            .append("dutyTel", getDutyTel())
            .append("remark", getRemark())
            .append("createBy", getCreateBy())
            .append("createTime", getCreateTime())
            .append("updateBy", getUpdateBy())
            .append("updateTime", getUpdateTime())
            .append("delFlag", getDelFlag())
            .append("enable", getEnable())
            .toString();
    }
}
