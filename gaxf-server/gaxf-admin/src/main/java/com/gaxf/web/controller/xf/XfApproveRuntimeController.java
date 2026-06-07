package com.gaxf.web.controller.xf;

import java.util.HashMap;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.gaxf.common.annotation.Log;
import com.gaxf.common.core.controller.BaseController;
import com.gaxf.common.core.domain.AjaxResult;
import com.gaxf.common.enums.BusinessType;
import com.gaxf.system.domain.vo.XfApproveActionBody;
import com.gaxf.system.service.IXfApproveRuntimeService;

/**
 * 审批运行 控制器
 *
 * @author gaxf
 */
@RestController
@RequestMapping("/xf/approve/task")
public class XfApproveRuntimeController extends BaseController
{
    @Autowired
    private IXfApproveRuntimeService xfApproveRuntimeService;

    @PreAuthorize("@ss.hasPermi('xf:approveTask:list')")
    @GetMapping("/myTodo")
    public AjaxResult myTodo()
    {
        Map<String, Object> data = new HashMap<>();
        data.put("pending", xfApproveRuntimeService.selectMyPendingTasks(getDeptId()));
        data.put("handled", xfApproveRuntimeService.selectMyHandledTasks(getDeptId()));
        return success(data);
    }

    @PreAuthorize("@ss.hasPermi('xf:approveTask:action')")
    @Log(title = "审批处理", businessType = BusinessType.UPDATE)
    @PostMapping("/pass")
    public AjaxResult pass(@RequestBody XfApproveActionBody body)
    {
        xfApproveRuntimeService.passTask(body.getTaskId(), body.getOpinion(), getUserId(), getUsername(), getDeptId());
        return success();
    }

    @PreAuthorize("@ss.hasPermi('xf:approveTask:action')")
    @Log(title = "审批处理", businessType = BusinessType.UPDATE)
    @PostMapping("/reject")
    public AjaxResult reject(@RequestBody XfApproveActionBody body)
    {
        xfApproveRuntimeService.rejectTask(body.getTaskId(), body.getOpinion(), getUserId(), getUsername(), getDeptId());
        return success();
    }
}
