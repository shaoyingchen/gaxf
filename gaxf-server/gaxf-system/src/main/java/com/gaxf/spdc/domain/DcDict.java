package com.gaxf.spdc.domain;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.gaxf.common.annotation.Excel;
import com.gaxf.common.annotation.Excel.ColumnType;
import com.gaxf.common.core.domain.BaseEntity;

/**
 * 业务字典对象 dc_dict
 *
 * @author gaxf
 */
public class DcDict extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** 主键 */
    private Integer id;

    /** 父级id */
    @Excel(name = "父级ID")
    private Integer parentId;

    /** 节点名称 */
    @Excel(name = "节点名称")
    private String name;

    /** 字典种类：1问题分类 2设备分类 3预警分类 */
    @Excel(name = "字典种类", readConverterExp = "1=问题分类,2=设备分类,3=预警分类")
    private Integer kind;

    /** 排序 */
    @Excel(name = "排序", cellType = ColumnType.NUMERIC)
    private Integer sort;

    /** 删除标志 */
    private Integer deleted;

    /** 父级名称 */
    private String parentName;

    /** 子节点列表 */
    private java.util.List<DcDict> children;

    public Integer getId()
    {
        return id;
    }

    public void setId(Integer id)
    {
        this.id = id;
    }

    public Integer getParentId()
    {
        return parentId;
    }

    public void setParentId(Integer parentId)
    {
        this.parentId = parentId;
    }

    public String getName()
    {
        return name;
    }

    public void setName(String name)
    {
        this.name = name;
    }

    public Integer getKind()
    {
        return kind;
    }

    public void setKind(Integer kind)
    {
        this.kind = kind;
    }

    public Integer getSort()
    {
        return sort;
    }

    public void setSort(Integer sort)
    {
        this.sort = sort;
    }

    public Integer getDeleted()
    {
        return deleted;
    }

    public void setDeleted(Integer deleted)
    {
        this.deleted = deleted;
    }

    public String getParentName()
    {
        return parentName;
    }

    public void setParentName(String parentName)
    {
        this.parentName = parentName;
    }

    public java.util.List<DcDict> getChildren()
    {
        return children;
    }

    public void setChildren(java.util.List<DcDict> children)
    {
        this.children = children;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this, ToStringStyle.MULTI_LINE_STYLE)
            .append("id", getId())
            .append("parentId", getParentId())
            .append("name", getName())
            .append("kind", getKind())
            .append("sort", getSort())
            .append("deleted", getDeleted())
            .append("createBy", getCreateBy())
            .append("createTime", getCreateTime())
            .append("updateBy", getUpdateBy())
            .append("updateTime", getUpdateTime())
            .toString();
    }
}