package com.gaxf.spdc.mapper;

import java.util.List;
import com.gaxf.spdc.domain.DcCamera;

/**
 * 点位管理Mapper接口
 *
 * @author ruoyi
 * @date 2026-04-15
 */
public interface DcCameraMapper
{
    /**
     * 查询点位管理
     *
     * @param pointId 点位管理主键
     * @return 点位管理
     */
    public DcCamera selectDcCameraByPointId(Integer pointId);

    /**
     * 查询点位管理列表
     *
     * @param dcCamera 点位管理
     * @return 点位管理集合
     */
    public List<DcCamera> selectDcCameraList(DcCamera dcCamera);

    /**
     * 新增点位管理
     *
     * @param dcCamera 点位管理
     * @return 结果
     */
    public int insertDcCamera(DcCamera dcCamera);

    /**
     * 修改点位管理
     *
     * @param dcCamera 点位管理
     * @return 结果
     */
    public int updateDcCamera(DcCamera dcCamera);

    /**
     * 删除点位管理
     *
     * @param pointId 点位管理主键
     * @return 结果
     */
    public int deleteDcCameraByPointId(Integer pointId);

    /**
     * 批量删除点位管理
     *
     * @param pointIds 需要删除的数据主键集合
     * @return 结果
     */
    public int deleteDcCameraByPointIds(Integer[] pointIds);
}
