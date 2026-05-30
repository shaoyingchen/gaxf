package com.gaxf.spdc.service.impl;

import java.util.List;
import com.gaxf.common.utils.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.gaxf.spdc.mapper.DcCameraMapper;
import com.gaxf.spdc.domain.DcCamera;
import com.gaxf.spdc.service.IDcCameraService;

/**
 * 点位管理Service业务层处理
 *
 * @author ruoyi
 * @date 2026-04-15
 */
@Service
public class DcCameraServiceImpl implements IDcCameraService
{
    @Autowired
    private DcCameraMapper dcCameraMapper;

    /**
     * 查询点位管理
     *
     * @param pointId 点位管理主键
     * @return 点位管理
     */
    @Override
    public DcCamera selectDcCameraByPointId(Integer pointId)
    {
        return dcCameraMapper.selectDcCameraByPointId(pointId);
    }

    /**
     * 查询点位管理列表
     *
     * @param dcCamera 点位管理
     * @return 点位管理
     */
    @Override
    public List<DcCamera> selectDcCameraList(DcCamera dcCamera)
    {
        return dcCameraMapper.selectDcCameraList(dcCamera);
    }

    /**
     * 查询点位管理列表（公开查询，用于非权限校验场景）
     * 非管理员只能查询当前部门及下级部门的点位
     *
     * @param dcCamera 点位管理
     * @param userDeptId 用户部门ID（非管理员时传入）
     * @return 点位管理
     */
    @Override
    public List<DcCamera> selectDcCameraListPublic(DcCamera dcCamera, Long userDeptId)
    {
        if (userDeptId != null && userDeptId != 0)
        {
            dcCamera.setUserDeptId(userDeptId);
        }
        return dcCameraMapper.selectDcCameraList(dcCamera);
    }

    /**
     * 新增点位管理
     *
     * @param dcCamera 点位管理
     * @return 结果
     */
    @Override
    public int insertDcCamera(DcCamera dcCamera)
    {
        dcCamera.setCreateTime(DateUtils.getNowDate());
        return dcCameraMapper.insertDcCamera(dcCamera);
    }

    /**
     * 修改点位管理
     *
     * @param dcCamera 点位管理
     * @return 结果
     */
    @Override
    public int updateDcCamera(DcCamera dcCamera)
    {
        dcCamera.setUpdateTime(DateUtils.getNowDate());
        return dcCameraMapper.updateDcCamera(dcCamera);
    }

    /**
     * 批量删除点位管理
     *
     * @param pointIds 需要删除的点位管理主键
     * @return 结果
     */
    @Override
    public int deleteDcCameraByPointIds(Integer[] pointIds)
    {
        return dcCameraMapper.deleteDcCameraByPointIds(pointIds);
    }

    /**
     * 删除点位管理信息
     *
     * @param pointId 点位管理主键
     * @return 结果
     */
    @Override
    public int deleteDcCameraByPointId(Integer pointId)
    {
        return dcCameraMapper.deleteDcCameraByPointId(pointId);
    }
}
