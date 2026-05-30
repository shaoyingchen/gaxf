package com.gaxf.spdc.service.impl;

import java.util.List;
import com.gaxf.common.utils.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.gaxf.spdc.mapper.DcDxjgMapper;
import com.gaxf.spdc.domain.DcDxjg;
import com.gaxf.spdc.service.IDcDxjgService;

/**
 * 预警管理Service业务层处理
 *
 * @author spdc
 * @date 2026-04-30
 */
@Service
public class DcDxjgServiceImpl implements IDcDxjgService
{
    @Autowired
    private DcDxjgMapper dcDxjgMapper;

    /**
     * 查询预警管理
     *
     * @param id 预警管理主键
     * @return 预警管理
     */
    @Override
    public DcDxjg selectDcDxjgById(Integer id)
    {
        return dcDxjgMapper.selectDcDxjgById(id);
    }

    /**
     * 查询预警管理列表
     *
     * @param dcDxjg 预警管理
     * @return 预警管理
     */
    @Override
    public List<DcDxjg> selectDcDxjgList(DcDxjg dcDxjg)
    {
        return dcDxjgMapper.selectDcDxjgList(dcDxjg);
    }

    /**
     * 查询预警管理列表（公开查询，用于非权限校验场景）
     * 非管理员只能查询当前部门及下级部门的预警
     *
     * @param dcDxjg 预警管理
     * @param userDeptId 用户部门ID（非管理员时传入）
     * @return 预警管理
     */
    @Override
    public List<DcDxjg> selectDcDxjgListPublic(DcDxjg dcDxjg, Long userDeptId)
    {
        if (userDeptId != null && userDeptId != 0)
        {
            dcDxjg.setUserDeptId(userDeptId);
        }
        return dcDxjgMapper.selectDcDxjgList(dcDxjg);
    }

    /**
     * 新增预警管理
     *
     * @param dcDxjg 预警管理
     * @return 结果
     */
    @Override
    public int insertDcDxjg(DcDxjg dcDxjg)
    {
        dcDxjg.setCreateTime(DateUtils.getNowDate());
        return dcDxjgMapper.insertDcDxjg(dcDxjg);
    }

    /**
     * 修改预警管理（处置预警）
     *
     * @param dcDxjg 预警管理
     * @return 结果
     */
    @Override
    public int updateDcDxjg(DcDxjg dcDxjg)
    {
        dcDxjg.setUpdateTime(DateUtils.getNowDate());
        return dcDxjgMapper.updateDcDxjg(dcDxjg);
    }

    /**
     * 批量删除预警管理
     *
     * @param ids 需要删除的预警管理主键
     * @return 结果
     */
    @Override
    public int deleteDcDxjgByIds(Integer[] ids)
    {
        return dcDxjgMapper.deleteDcDxjgByIds(ids);
    }

    /**
     * 删除预警管理信息
     *
     * @param id 预警管理主键
     * @return 结果
     */
    @Override
    public int deleteDcDxjgById(Integer id)
    {
        return dcDxjgMapper.deleteDcDxjgById(id);
    }

    /**
     * 查询预警统计
     *
     * @param userDeptId 用户部门ID（非管理员时传入）
     * @return 统计结果
     */
    @Override
    public DcDxjg selectDcDxjgStatistics(Long userDeptId)
    {
        return dcDxjgMapper.selectDcDxjgStatistics(userDeptId);
    }
}