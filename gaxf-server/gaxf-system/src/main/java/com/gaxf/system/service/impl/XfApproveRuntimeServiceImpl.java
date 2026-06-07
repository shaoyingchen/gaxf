package com.gaxf.system.service.impl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import com.gaxf.common.exception.ServiceException;
import com.gaxf.common.utils.DateUtils;
import com.gaxf.common.utils.StringUtils;
import com.gaxf.system.domain.XfApproveFlowConfig;
import com.gaxf.system.domain.XfApproveFlowStage;
import com.gaxf.system.domain.XfApproveFlowStageDept;
import com.gaxf.system.domain.XfApproveInstance;
import com.gaxf.system.domain.XfApproveTask;
import com.gaxf.system.domain.XfAssignRecord;
import com.gaxf.system.domain.XfWorkOrder;
import com.gaxf.system.domain.vo.XfApproveProgressVO;
import com.gaxf.system.mapper.XfApproveFlowConfigMapper;
import com.gaxf.system.mapper.XfApproveInstanceMapper;
import com.gaxf.system.mapper.XfAssignRecordMapper;
import com.gaxf.system.mapper.XfWorkOrderMapper;
import com.gaxf.system.service.IXfApproveRuntimeService;

/**
 * 审批运行 Service 实现
 *
 * @author gaxf
 */
@Service
public class XfApproveRuntimeServiceImpl implements IXfApproveRuntimeService
{
    @Autowired
    private XfApproveFlowConfigMapper xfApproveFlowConfigMapper;

    @Autowired
    private XfApproveInstanceMapper xfApproveInstanceMapper;

    @Autowired
    private XfAssignRecordMapper xfAssignRecordMapper;

    @Autowired
    private XfWorkOrderMapper xfWorkOrderMapper;

    @Override
    @Transactional
    public void startAssignApprove(Long orderId, Long[] undertakeDeptIds, String[] undertakeDeptNames, String username)
    {
        XfWorkOrder workOrder = xfWorkOrderMapper.selectXfWorkOrderById(orderId);
        if (workOrder == null)
        {
            throw new ServiceException("工单不存在");
        }
        if (!"0".equals(workOrder.getStatus()))
        {
            throw new ServiceException("只有待派单状态的工单才能交办并发起审批");
        }
        if (undertakeDeptIds == null || undertakeDeptIds.length == 0)
        {
            throw new ServiceException("请选择至少一个承办单位");
        }
        List<Long> deptIdList = Arrays.asList(undertakeDeptIds);
        if (new HashSet<>(deptIdList).size() != deptIdList.size())
        {
            throw new ServiceException("承办单位不能重复");
        }
        if (xfApproveInstanceMapper.selectInstanceByOrderId(orderId) != null)
        {
            throw new ServiceException("该工单已存在审批实例，请勿重复发起");
        }

        List<XfApproveFlowConfig> flowList = new ArrayList<>();
        List<String> missingNames = new ArrayList<>();
        for (int i = 0; i < undertakeDeptIds.length; i++)
        {
            Long undertakeDeptId = undertakeDeptIds[i];
            XfApproveFlowConfig flowConfig = xfApproveFlowConfigMapper.selectEnabledFlowByApplyDeptId(undertakeDeptId);
            if (flowConfig == null)
            {
                missingNames.add(resolveDeptName(undertakeDeptIds, undertakeDeptNames, undertakeDeptId, i));
                continue;
            }
            if (CollectionUtils.isEmpty(flowConfig.getStages()))
            {
                throw new ServiceException("承办单位审批流程未配置完整：" + resolveDeptName(undertakeDeptIds, undertakeDeptNames, undertakeDeptId, i));
            }
            flowList.add(flowConfig);
        }
        if (!missingNames.isEmpty())
        {
            throw new ServiceException("以下承办单位未配置审批流程：" + String.join("、", missingNames));
        }

        XfApproveFlowConfig firstFlow = flowList.get(0);
        XfApproveInstance instance = new XfApproveInstance();
        instance.setOrderId(orderId);
        instance.setFlowConfigId(firstFlow.getId());
        instance.setFlowName(flowList.size() > 1 ? "多承办单位交办审批" : firstFlow.getFlowName());
        instance.setApplyDeptId(workOrder.getCreateDeptId() != null ? workOrder.getCreateDeptId() : firstFlow.getApplyDeptId());
        instance.setApplyDeptName(StringUtils.isNotBlank(workOrder.getRegisterUnit()) ? workOrder.getRegisterUnit() : firstFlow.getApplyDeptName());
        instance.setCurrentStageNo(firstFlow.getStages().get(0).getStageNo());
        instance.setStatus("0");
        instance.setStartTime(DateUtils.getNowDate());
        instance.setCreateBy(username);
        xfApproveInstanceMapper.insertXfApproveInstance(instance);

        List<XfApproveTask> firstTasks = new ArrayList<>();
        for (int i = 0; i < undertakeDeptIds.length; i++)
        {
            XfApproveFlowConfig flowConfig = flowList.get(i);
            XfApproveFlowStage firstStage = flowConfig.getStages().get(0);
            if (CollectionUtils.isEmpty(firstStage.getDeptList()))
            {
                throw new ServiceException("承办单位审批流程未配置完整：" + resolveDeptName(undertakeDeptIds, undertakeDeptNames, undertakeDeptIds[i], i));
            }
            firstTasks.addAll(buildStageTasks(instance, firstStage, undertakeDeptIds[i], resolveDeptName(undertakeDeptIds, undertakeDeptNames, undertakeDeptIds[i], i), username));
        }
        xfApproveInstanceMapper.batchInsertApproveTasks(firstTasks);

        XfWorkOrder update = new XfWorkOrder();
        update.setId(orderId);
        update.setStatus("1");
        update.setUpdateBy(username);
        xfWorkOrderMapper.updateXfWorkOrder(update);
    }

    @Override
    @Transactional
    public void passTask(Long taskId, String opinion, Long userId, String username, Long deptId)
    {
        XfApproveTask task = getPendingTask(taskId, deptId);
        List<XfApproveTask> taskGroup = getReadyTaskGroup(task);
        for (XfApproveTask groupTask : taskGroup)
        {
            groupTask.setStatus("1");
            groupTask.setOpinion(opinion);
            groupTask.setReviewerId(userId);
            groupTask.setReviewerName(username);
            groupTask.setActionTime(DateUtils.getNowDate());
            groupTask.setUpdateBy(username);
            xfApproveInstanceMapper.updateApproveTask(groupTask);
        }
        for (XfApproveTask groupTask : taskGroup)
        {
            advanceOrFinish(groupTask.getInstanceId(), groupTask.getOrderId(), groupTask.getUndertakeDeptId(), groupTask.getStageNo(), username);
        }
    }

    @Override
    @Transactional
    public void rejectTask(Long taskId, String opinion, Long userId, String username, Long deptId)
    {
        XfApproveTask task = getPendingTask(taskId, deptId);
        List<XfApproveTask> taskGroup = getReadyTaskGroup(task);
        for (XfApproveTask groupTask : taskGroup)
        {
            groupTask.setStatus("2");
            groupTask.setOpinion(opinion);
            groupTask.setReviewerId(userId);
            groupTask.setReviewerName(username);
            groupTask.setActionTime(DateUtils.getNowDate());
            groupTask.setUpdateBy(username);
            xfApproveInstanceMapper.updateApproveTask(groupTask);
        }

        XfApproveInstance instance = new XfApproveInstance();
        instance.setId(task.getInstanceId());
        instance.setStatus("2");
        instance.setFinishTime(DateUtils.getNowDate());
        instance.setUpdateBy(username);
        xfApproveInstanceMapper.updateApproveInstance(instance);

        XfWorkOrder order = new XfWorkOrder();
        order.setId(task.getOrderId());
        order.setStatus("4");
        order.setUpdateBy(username);
        xfWorkOrderMapper.updateXfWorkOrder(order);
    }

    @Override
    public XfApproveProgressVO selectProgressByOrderId(Long orderId)
    {
        XfApproveInstance instance = xfApproveInstanceMapper.selectInstanceByOrderId(orderId);
        if (instance == null)
        {
            return null;
        }
        List<XfApproveTask> taskList = xfApproveInstanceMapper.selectTasksByInstanceId(instance.getId());
        XfApproveProgressVO progressVO = new XfApproveProgressVO();
        progressVO.setInstanceId(instance.getId());
        progressVO.setFlowName(instance.getFlowName());
        progressVO.setStatus(instance.getStatus());
        progressVO.setCurrentStageNo(instance.getCurrentStageNo());
        progressVO.setTaskList(taskList);
        progressVO.setBranchTaskList(taskList.stream()
            .filter(task -> task.getUndertakeDeptId() != null)
            .collect(Collectors.toList()));
        return progressVO;
    }

    @Override
    public List<XfApproveTask> selectMyPendingTasks(Long deptId)
    {
        return xfApproveInstanceMapper.selectMyPendingTasks(deptId);
    }

    @Override
    public List<XfApproveTask> selectMyHandledTasks(Long deptId)
    {
        return xfApproveInstanceMapper.selectMyHandledTasks(deptId);
    }

    private XfApproveTask getPendingTask(Long taskId, Long deptId)
    {
        XfApproveTask task = xfApproveInstanceMapper.selectTaskById(taskId);
        if (task == null)
        {
            throw new ServiceException("审批任务不存在");
        }
        if (!deptId.equals(task.getApproveDeptId()))
        {
            throw new ServiceException("当前部门无权处理该审批任务");
        }
        if (!"0".equals(task.getStatus()))
        {
            throw new ServiceException("审批任务已处理，请勿重复提交");
        }
        validateReportSubmitted(task);
        return task;
    }

    private void validateReportSubmitted(XfApproveTask task)
    {
        List<XfAssignRecord> assignRecords = xfAssignRecordMapper.selectXfAssignRecordByOrderId(task.getOrderId());
        for (XfAssignRecord record : assignRecords)
        {
            if (task.getUndertakeDeptId().equals(record.getAssignDeptId())
                && "2".equals(record.getStatus())
                && record.getReportTime() != null
                && StringUtils.isNotBlank(record.getReportContent()))
            {
                return;
            }
        }
        throw new ServiceException("承办单位尚未提交办理报告，不能进行审批");
    }

    private List<XfApproveTask> getReadyTaskGroup(XfApproveTask task)
    {
        int totalUndertakeCount = xfApproveInstanceMapper.countInstanceUndertakeDept(task.getInstanceId());
        int readyUndertakeCount = xfApproveInstanceMapper.countPendingUndertakeDeptByApproveDept(task.getInstanceId(), task.getApproveDeptId());
        if (readyUndertakeCount < totalUndertakeCount)
        {
            throw new ServiceException("该工单仍有承办单位未流转到当前审核部门，请等待全部承办单位到达后再统一审核");
        }
        List<XfApproveTask> taskGroup = xfApproveInstanceMapper.selectPendingTasksByInstanceAndApproveDept(task.getInstanceId(), task.getApproveDeptId());
        if (CollectionUtils.isEmpty(taskGroup))
        {
            throw new ServiceException("当前审核部门暂无可处理任务");
        }
        return taskGroup;
    }

    private List<XfApproveTask> buildStageTasks(XfApproveInstance instance, XfApproveFlowStage stage, Long undertakeDeptId, String undertakeDeptName, String username)
    {
        List<XfApproveTask> tasks = new ArrayList<>();
        for (XfApproveFlowStageDept dept : stage.getDeptList())
        {
            XfApproveTask task = new XfApproveTask();
            task.setInstanceId(instance.getId());
            task.setOrderId(instance.getOrderId());
            task.setStageNo(stage.getStageNo());
            task.setStageName(stage.getStageName());
            task.setTaskType("1");
            task.setUndertakeDeptId(undertakeDeptId);
            task.setUndertakeDeptName(undertakeDeptName);
            task.setApproveDeptId(dept.getApproveDeptId());
            task.setApproveDeptName(dept.getApproveDeptName());
            task.setStatus("0");
            task.setCreateBy(username);
            task.setCreateTime(new Date());
            tasks.add(task);
        }
        return tasks;
    }

    private void advanceOrFinish(Long instanceId, Long orderId, Long undertakeDeptId, Integer currentStageNo, String username)
    {
        XfApproveInstance currentInstance = xfApproveInstanceMapper.selectInstanceById(instanceId);
        if (currentInstance == null)
        {
            throw new ServiceException("审批实例不存在");
        }
        XfApproveFlowConfig config = xfApproveFlowConfigMapper.selectEnabledFlowByApplyDeptId(undertakeDeptId);
        if (config == null || CollectionUtils.isEmpty(config.getStages()))
        {
            throw new ServiceException("承办单位审批流程配置不存在");
        }

        XfApproveFlowStage nextStage = null;
        for (XfApproveFlowStage stage : config.getStages())
        {
            if (stage.getStageNo() > currentStageNo)
            {
                if (nextStage == null || stage.getStageNo() < nextStage.getStageNo())
                {
                    nextStage = stage;
                }
            }
        }

        if (nextStage != null)
        {
            if (CollectionUtils.isEmpty(nextStage.getDeptList()))
            {
                throw new ServiceException("下一阶段未配置审批部门");
            }
            xfApproveInstanceMapper.batchInsertApproveTasks(buildStageTasks(currentInstance, nextStage, undertakeDeptId, findUndertakeDeptName(instanceId, undertakeDeptId), username));
            updateInstanceStage(instanceId, nextStage.getStageNo(), username);
            return;
        }

        if (hasPendingTasks(instanceId))
        {
            return;
        }

        XfApproveInstance instance = new XfApproveInstance();
        instance.setId(instanceId);
        instance.setStatus("1");
        instance.setFinishTime(DateUtils.getNowDate());
        instance.setUpdateBy(username);
        xfApproveInstanceMapper.updateApproveInstance(instance);

        XfWorkOrder order = new XfWorkOrder();
        order.setId(orderId);
        order.setStatus("3");
        order.setUpdateBy(username);
        xfWorkOrderMapper.updateXfWorkOrder(order);
    }

    private boolean hasPendingTasks(Long instanceId)
    {
        List<XfApproveTask> tasks = xfApproveInstanceMapper.selectTasksByInstanceId(instanceId);
        for (XfApproveTask task : tasks)
        {
            if ("0".equals(task.getStatus()))
            {
                return true;
            }
        }
        return false;
    }

    private void updateInstanceStage(Long instanceId, Integer stageNo, String username)
    {
        XfApproveInstance instance = new XfApproveInstance();
        instance.setId(instanceId);
        instance.setCurrentStageNo(stageNo);
        instance.setUpdateBy(username);
        xfApproveInstanceMapper.updateApproveInstance(instance);
    }

    private String findUndertakeDeptName(Long instanceId, Long undertakeDeptId)
    {
        List<XfApproveTask> tasks = xfApproveInstanceMapper.selectTasksByInstanceId(instanceId);
        for (XfApproveTask task : tasks)
        {
            if (undertakeDeptId.equals(task.getUndertakeDeptId()) && StringUtils.isNotBlank(task.getUndertakeDeptName()))
            {
                return task.getUndertakeDeptName();
            }
        }
        return String.valueOf(undertakeDeptId);
    }

    private String resolveDeptName(Long[] deptIds, String[] deptNames, Long deptId, int index)
    {
        if (deptNames != null && index < deptNames.length && StringUtils.isNotBlank(deptNames[index]))
        {
            return deptNames[index];
        }
        return String.valueOf(deptId);
    }
}
