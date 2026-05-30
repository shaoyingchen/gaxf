package com.gaxf.spdc.mapper;

import java.util.List;
import com.gaxf.spdc.domain.DcDxjg;

/**
 * 预警管理Mapper接口
 *
 * @author spdc
 * @date 2026-04-30
 */
public interface DcDxjgMapper
{
    /**
     * 查询预警管理
     *
     * @param id 预警管理主键
     * @return 预警管理
     */
    public DcDxjg selectDcDxjgById(Integer id);

    /**
     * 查询预警管理列表
     *
     * @param dcDxjg 预警管理
     * @return 预警管理集合
     */
    public List<DcDxjg> selectDcDxjgList(DcDxjg dcDxjg);

    /**
     * 新增预警管理
     *
     * @param dcDxjg 预警管理
     * @return 结果
     */
    public int insertDcDxjg(DcDxjg dcDxjg);

    /**
     * 修改预警管理
     *
     * @param dcDxjg 预警管理
     * @return 结果
     */
    public int updateDcDxjg(DcDxjg dcDxjg);

    /**
     * 删除预警管理
     *
     * @param id 预警管理主键
     * @return 结果
     */
    public int deleteDcDxjgById(Integer id);

    /**
     * 批量删除预警管理
     *
     * @param ids 需要删除的数据主键集合
     * @return 结果
     */
    public int deleteDcDxjgByIds(Integer[] ids);

    /**
     * 查询预警统计（今日预警、高危预警、待处理、已确认）
     *
     * @param userDeptId 用户部门ID（非管理员时传入）
     * @return 统计结果
     */
    public DcDxjg selectDcDxjgStatistics(Long userDeptId);
}