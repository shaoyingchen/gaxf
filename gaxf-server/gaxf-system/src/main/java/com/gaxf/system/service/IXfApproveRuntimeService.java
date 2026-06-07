package com.gaxf.system.service;

import java.util.List;
import com.gaxf.system.domain.XfApproveTask;
import com.gaxf.system.domain.vo.XfApproveProgressVO;

/**
 * 审批运行 Service 接口
 *
 * @author gaxf
 */
public interface IXfApproveRuntimeService
{
    public void startAssignApprove(Long orderId, Long[] undertakeDeptIds, String[] undertakeDeptNames, String username);

    public void passTask(Long taskId, String opinion, Long userId, String username, Long deptId);

    public void rejectTask(Long taskId, String opinion, Long userId, String username, Long deptId);

    public XfApproveProgressVO selectProgressByOrderId(Long orderId);

    public List<XfApproveTask> selectMyPendingTasks(Long deptId);

    public List<XfApproveTask> selectMyHandledTasks(Long deptId);
}
