package com.gaxf.spdc.service;

import java.util.List;
import com.gaxf.spdc.domain.DcCamera;

/**
 * 点位管理Service接口
 *
 * @author ruoyi
 * @date 2026-04-15
 */
public interface IDcCameraService
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
     * 查询点位管理列表（公开查询，用于非权限校验场景）
     * 非管理员只能查询当前部门及下级部门的点位
     *
     * @param dcCamera 点位管理
     * @param userDeptId 用户部门ID（非管理员时传入）
     * @return 点位管理集合
     */
    public List<DcCamera> selectDcCameraListPublic(DcCamera dcCamera, Long userDeptId);

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
     * 批量删除点位管理
     *
     * @param pointIds 需要删除的点位管理主键集合
     * @return 结果
     */
    public int deleteDcCameraByPointIds(Integer[] pointIds);

    /**
     * 删除点位管理信息
     *
     * @param pointId 点位管理主键
     * @return 结果
     */
    public int deleteDcCameraByPointId(Integer pointId);
}
