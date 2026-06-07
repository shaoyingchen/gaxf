package com.gaxf.system.service.impl;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import com.gaxf.common.exception.ServiceException;
import com.gaxf.common.utils.StringUtils;
import com.gaxf.system.domain.XfApproveFlowConfig;
import com.gaxf.system.domain.XfApproveFlowStage;
import com.gaxf.system.domain.XfApproveFlowStageDept;
import com.gaxf.system.domain.vo.XfApproveFlowConfigBody;
import com.gaxf.system.mapper.XfApproveFlowConfigMapper;
import com.gaxf.system.service.IXfApproveFlowConfigService;

/**
 * 审批流配置 Service 实现
 *
 * @author gaxf
 */
@Service
public class XfApproveFlowConfigServiceImpl implements IXfApproveFlowConfigService
{
    @Autowired
    private XfApproveFlowConfigMapper xfApproveFlowConfigMapper;

    @Override
    public XfApproveFlowConfig selectXfApproveFlowConfigById(Long id)
    {
        return xfApproveFlowConfigMapper.selectXfApproveFlowConfigById(id);
    }

    @Override
    public List<XfApproveFlowConfig> selectXfApproveFlowConfigList(XfApproveFlowConfig query)
    {
        return xfApproveFlowConfigMapper.selectXfApproveFlowConfigList(query);
    }

    @Override
    @Transactional
    public int insertXfApproveFlowConfig(XfApproveFlowConfigBody body)
    {
        validateFlowConfig(body);
        if ("0".equals(body.getStatus()) && xfApproveFlowConfigMapper.countEnabledFlowByApplyDeptId(body.getApplyDeptId(), null) > 0)
        {
            throw new ServiceException("该发起部门已存在启用中的审批流程");
        }
        int rows = xfApproveFlowConfigMapper.insertXfApproveFlowConfig(body);
        saveChildren(body);
        return rows;
    }

    @Override
    @Transactional
    public int updateXfApproveFlowConfig(XfApproveFlowConfigBody body)
    {
        if (body.getId() == null)
        {
            throw new ServiceException("流程配置ID不能为空");
        }
        validateFlowConfig(body);
        if ("0".equals(body.getStatus()) && xfApproveFlowConfigMapper.countEnabledFlowByApplyDeptId(body.getApplyDeptId(), body.getId()) > 0)
        {
            throw new ServiceException("该发起部门已存在启用中的审批流程");
        }
        int rows = xfApproveFlowConfigMapper.updateXfApproveFlowConfig(body);
        xfApproveFlowConfigMapper.deleteStageDeptsByConfigId(body.getId());
        xfApproveFlowConfigMapper.deleteFlowStagesByConfigId(body.getId());
        saveChildren(body);
        return rows;
    }

    @Override
    @Transactional
    public int deleteXfApproveFlowConfigByIds(Long[] ids)
    {
        for (Long id : ids)
        {
            xfApproveFlowConfigMapper.deleteStageDeptsByConfigId(id);
            xfApproveFlowConfigMapper.deleteFlowStagesByConfigId(id);
        }
        return xfApproveFlowConfigMapper.deleteXfApproveFlowConfigByIds(ids);
    }

    private void validateFlowConfig(XfApproveFlowConfigBody body)
    {
        if (body.getApplyDeptId() == null)
        {
            throw new ServiceException("请选择发起部门");
        }
        if (StringUtils.isBlank(body.getApplyDeptName()))
        {
            throw new ServiceException("发起部门名称不能为空");
        }
        if (StringUtils.isBlank(body.getFlowName()))
        {
            throw new ServiceException("请输入流程名称");
        }
        if (StringUtils.isBlank(body.getStatus()))
        {
            body.setStatus("0");
        }
        if (CollectionUtils.isEmpty(body.getStages()))
        {
            throw new ServiceException("至少配置一个审批阶段");
        }
        Set<Integer> stageNos = new HashSet<>();
        for (XfApproveFlowStage stage : body.getStages())
        {
            if (stage.getStageNo() == null || !stageNos.add(stage.getStageNo()))
            {
                throw new ServiceException("阶段序号不能为空且不能重复");
            }
            if (StringUtils.isBlank(stage.getStageName()))
            {
                throw new ServiceException("阶段名称不能为空");
            }
            if (StringUtils.isBlank(stage.getStageType()))
            {
                stage.setStageType("1");
            }
            if (StringUtils.isBlank(stage.getParallelMode()))
            {
                stage.setParallelMode("1");
            }
            if (CollectionUtils.isEmpty(stage.getDeptList()))
            {
                throw new ServiceException("每个阶段至少选择一个审批部门");
            }
            Set<Long> deptIds = new HashSet<>();
            for (XfApproveFlowStageDept dept : stage.getDeptList())
            {
                if (dept.getApproveDeptId() == null || !deptIds.add(dept.getApproveDeptId()))
                {
                    throw new ServiceException("同一阶段审批部门不能为空且不能重复");
                }
                if (StringUtils.isBlank(dept.getApproveDeptName()))
                {
                    throw new ServiceException("审批部门名称不能为空");
                }
            }
        }
    }

    private void saveChildren(XfApproveFlowConfigBody body)
    {
        List<XfApproveFlowStage> stages = body.getStages();
        for (XfApproveFlowStage stage : stages)
        {
            stage.setFlowConfigId(body.getId());
        }
        xfApproveFlowConfigMapper.batchInsertFlowStages(stages);

        List<XfApproveFlowStageDept> allStageDepts = new ArrayList<>();
        for (XfApproveFlowStage stage : stages)
        {
            if (!CollectionUtils.isEmpty(stage.getDeptList()))
            {
                for (XfApproveFlowStageDept dept : stage.getDeptList())
                {
                    dept.setStageId(stage.getId());
                    allStageDepts.add(dept);
                }
            }
        }
        if (!allStageDepts.isEmpty())
        {
            xfApproveFlowConfigMapper.batchInsertStageDepts(allStageDepts);
        }
    }
}
