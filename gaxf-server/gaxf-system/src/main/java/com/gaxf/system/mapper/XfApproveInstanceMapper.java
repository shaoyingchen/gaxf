package com.gaxf.system.mapper;

import java.util.List;
import org.apache.ibatis.annotations.Param;
import com.gaxf.system.domain.XfApproveInstance;
import com.gaxf.system.domain.XfApproveTask;

/**
 * 审批运行 Mapper 接口
 *
 * @author gaxf
 */
public interface XfApproveInstanceMapper
{
    public XfApproveInstance selectInstanceByOrderId(Long orderId);

    public XfApproveInstance selectInstanceById(Long id);

    public List<XfApproveTask> selectTasksByInstanceId(Long instanceId);

    public List<XfApproveTask> selectMyPendingTasks(@Param("deptId") Long deptId);

    public List<XfApproveTask> selectMyHandledTasks(@Param("deptId") Long deptId);

    public XfApproveTask selectTaskById(Long id);

    public List<XfApproveTask> selectPendingTasksByInstanceAndApproveDept(@Param("instanceId") Long instanceId,
        @Param("approveDeptId") Long approveDeptId);

    public int insertXfApproveInstance(XfApproveInstance instance);

    public int batchInsertApproveTasks(@Param("list") List<XfApproveTask> list);

    public int updateApproveTask(XfApproveTask task);

    public int updateApproveInstance(XfApproveInstance instance);

    public int countStagePendingTasks(@Param("instanceId") Long instanceId, @Param("stageNo") Integer stageNo);

    public int countStageRejectedTasks(@Param("instanceId") Long instanceId, @Param("stageNo") Integer stageNo);

    public int countInstanceUndertakeDept(@Param("instanceId") Long instanceId);

    public int countPendingUndertakeDeptByApproveDept(@Param("instanceId") Long instanceId, @Param("approveDeptId") Long approveDeptId);
}
