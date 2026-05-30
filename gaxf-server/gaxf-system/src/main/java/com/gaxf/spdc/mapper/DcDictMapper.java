package com.gaxf.spdc.mapper;

import java.util.List;
import com.gaxf.spdc.domain.DcDict;

/**
 * 业务字典Mapper接口
 *
 * @author gaxf
 */
public interface DcDictMapper
{
    /**
     * 查询业务字典
     *
     * @param id 业务字典主键
     * @return 业务字典
     */
    public DcDict selectDcDictById(Integer id);

    /**
     * 查询业务字典列表
     *
     * @param dcDict 业务字典
     * @return 业务字典集合
     */
    public List<DcDict> selectDcDictList(DcDict dcDict);

    /**
     * 查询业务字典树结构
     *
     * @param dcDict 业务字典
     * @return 业务字典树集合
     */
    public List<DcDict> selectDcDictTree(DcDict dcDict);

    /**
     * 新增业务字典
     *
     * @param dcDict 业务字典
     * @return 结果
     */
    public int insertDcDict(DcDict dcDict);

    /**
     * 修改业务字典
     *
     * @param dcDict 业务字典
     * @return 结果
     */
    public int updateDcDict(DcDict dcDict);

    /**
     * 删除业务字典
     *
     * @param id 业务字典主键
     * @return 结果
     */
    public int deleteDcDictById(Integer id);

    /**
     * 批量删除业务字典
     *
     * @param ids 需要删除的数据主键集合
     * @return 结果
     */
    public int deleteDcDictByIds(Integer[] ids);

    /**
     * 查询是否存在子节点
     *
     * @param id 业务字典主键
     * @return 结果
     */
    public int hasChildById(Integer id);
}