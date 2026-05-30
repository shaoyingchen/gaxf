package com.gaxf.spdc.service.impl;

import java.util.List;
import com.gaxf.common.utils.DateUtils;
import com.gaxf.common.utils.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.gaxf.spdc.mapper.DcPgaMapper;
import com.gaxf.spdc.mapper.DcDxjgMapper;
import com.gaxf.spdc.domain.DcPga;
import com.gaxf.spdc.domain.DcDxjg;
import com.gaxf.spdc.service.IDcPgaService;
import com.gaxf.system.service.ISysDeptService;
import com.gaxf.common.core.domain.entity.SysDept;

/**
 * 问题整改Service业务层处理
 *
 * @author spdc
 * @date 2026-04-30
 */
@Service
public class DcPgaServiceImpl implements IDcPgaService
{
    @Autowired
    private DcPgaMapper dcPgaMapper;

    @Autowired
    private DcDxjgMapper dcDxjgMapper;

    @Autowired
    private ISysDeptService sysDeptService;

    /**
     * 查询问题整改
     */
    @Override
    public DcPga selectDcPgaById(Integer id)
    {
        return dcPgaMapper.selectDcPgaById(id);
    }

    /**
     * 查询问题整改列表（带数据权限）
     */
    @Override
    public List<DcPga> selectDcPgaList(DcPga dcPga, Long userDeptId)
    {
        if (userDeptId != null && userDeptId != 0)
        {
            dcPga.setUserDeptId(userDeptId);
        }
        return dcPgaMapper.selectDcPgaList(dcPga);
    }

    /**
     * 从预警创建问题整改记录
     */
    @Override
    @Transactional
    public int createFromDxjg(DcDxjg dxjg, DcPga dcPga)
    {
        // 从预警复制信息到整改记录
        dcPga.setDxjgId(dxjg.getId());
        dcPga.setProblemDepart(dxjg.getPolicename());

        // 获取县局名称：优先 countyname -> county -> 通过部门查询
        String countyValue = dxjg.getCountyname();
        if (StringUtils.isEmpty(countyValue)) {
            countyValue = dxjg.getCounty();
        }
        // 如果都为空，尝试通过部门ID查询县局名称
        if (StringUtils.isEmpty(countyValue) && dxjg.getDepartId() != null) {
            countyValue = getCountyNameByDeptId(dxjg.getDepartId().longValue());
        }
        dcPga.setCountyname(countyValue);

        dcPga.setPolicename(dxjg.getPolicename());
        dcPga.setOrgcode(dxjg.getOrgid());
        dcPga.setDepartId(dxjg.getDepartId());
        dcPga.setCamareId(dxjg.getCameraId());
        if (dxjg.getOrgtableid() != null) {
            try {
                dcPga.setCamareCode(Double.parseDouble(dxjg.getOrgtableid()));
            } catch (NumberFormatException e) {
                // 忽略转换失败
            }
        }
        dcPga.setPic1(dxjg.getPic1());
        dcPga.setProblemMsg(dxjg.getMsgtitle());
        dcPga.setFindtime(dxjg.getTime());
        dcPga.setState("01"); // 默认整改状态：待整改
        dcPga.setStatus(1); // 默认状态：已提交
        dcPga.setCreateTime(DateUtils.getNowDate());

        int result = dcPgaMapper.insertDcPga(dcPga);

        // 更新预警状态为已处置
        if (result > 0)
        {
            DcDxjg updateDxjg = new DcDxjg();
            updateDxjg.setId(dxjg.getId());
            updateDxjg.setDeal("02"); // 已处置
            updateDxjg.setUpdateTime(DateUtils.getNowDate());
            dcDxjgMapper.updateDcDxjg(updateDxjg);
        }

        return result;
    }

    /**
     * 通过部门ID获取县局名称
     * 根据部门的 ancestors 查找上级县局部门
     */
    private String getCountyNameByDeptId(Long deptId) {
        if (deptId == null || deptId == 0) {
            return null;
        }
        SysDept dept = sysDeptService.selectDeptById(deptId);
        if (dept == null) {
            return null;
        }

        // 如果部门本身就是县局级别（ancestors 只有两级：0,xxx），返回当前部门名称
        String ancestors = dept.getAncestors();
        if (StringUtils.isEmpty(ancestors)) {
            return dept.getDeptName();
        }

        // ancestors 格式: "0,100,101,102"，解析获取上级县局ID
        String[] ancestorIds = ancestors.split(",");
        // 如果有至少3级（0, 市局, 县局, 派出所），取倒数第二个作为县局
        // 如果只有2级（0, 市局/县局），取当前部门的上级（第一个非0的）
        if (ancestorIds.length >= 3) {
            // 倒数第二个是县局（例如: 0,100,101 -> 100是县局）
            String countyDeptIdStr = ancestorIds[ancestorIds.length - 2];
            try {
                Long countyDeptId = Long.parseLong(countyDeptIdStr);
                SysDept countyDept = sysDeptService.selectDeptById(countyDeptId);
                if (countyDept != null) {
                    return countyDept.getDeptName();
                }
            } catch (NumberFormatException e) {
                // 忽略解析失败
            }
        } else if (ancestorIds.length == 2) {
            // 只有两级，取第一级非0的作为县局
            String parentIdStr = ancestorIds[1];
            try {
                Long parentId = Long.parseLong(parentIdStr);
                SysDept parentDept = sysDeptService.selectDeptById(parentId);
                if (parentDept != null) {
                    return parentDept.getDeptName();
                }
            } catch (NumberFormatException e) {
                // 忽略解析失败
            }
        }

        // 无法解析，返回当前部门名称作为备用
        return dept.getDeptName();
    }

    /**
     * 整改反馈提交
     */
    @Override
    @Transactional
    public int submitFeedback(DcPga dcPga)
    {
        dcPga.setUpdateTime(DateUtils.getNowDate());
        dcPga.setDealState("01"); // 已反馈
        int result = dcPgaMapper.updateDcPga(dcPga);

        // 更新预警的 is_resolved = 1
        if (result > 0 && dcPga.getDxjgId() != null)
        {
            DcDxjg updateDxjg = new DcDxjg();
            updateDxjg.setId(dcPga.getDxjgId());
            updateDxjg.setIsResolved(1); // 已处理
            updateDxjg.setUpdateTime(DateUtils.getNowDate());
            dcDxjgMapper.updateDcDxjg(updateDxjg);
        }

        return result;
    }

    /**
     * 修改问题整改
     */
    @Override
    public int updateDcPga(DcPga dcPga)
    {
        dcPga.setUpdateTime(DateUtils.getNowDate());
        return dcPgaMapper.updateDcPga(dcPga);
    }

    /**
     * 批量删除问题整改
     */
    @Override
    public int deleteDcPgaByIds(Integer[] ids)
    {
        return dcPgaMapper.deleteDcPgaByIds(ids);
    }

    /**
     * 删除问题整改信息
     */
    @Override
    public int deleteDcPgaById(Integer id)
    {
        return dcPgaMapper.deleteDcPgaById(id);
    }
}