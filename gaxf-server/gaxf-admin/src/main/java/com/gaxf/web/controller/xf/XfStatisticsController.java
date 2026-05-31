package com.gaxf.web.controller.xf;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.gaxf.common.core.controller.BaseController;
import com.gaxf.common.core.domain.AjaxResult;
import com.gaxf.system.domain.XfAssignRecord;
import com.gaxf.system.domain.XfWorkOrder;
import com.gaxf.system.service.IXfAssignRecordService;
import com.gaxf.system.service.IXfWorkOrderService;

/**
 * 信访统计 信息操作处理
 *
 * @author gaxf
 */
@RestController
@RequestMapping("/xf/statistics")
public class XfStatisticsController extends BaseController
{
    @Autowired
    private IXfWorkOrderService xfWorkOrderService;

    @Autowired
    private IXfAssignRecordService xfAssignRecordService;

    /**
     * 总览统计
     */
    @PreAuthorize("@ss.hasPermi('xf:statistics:list')")
    @GetMapping("/overview")
    public AjaxResult overview()
    {
        Map<String, Object> data = xfWorkOrderService.selectStatistics();
        return success(data);
    }

    /**
     * 超期统计
     */
    @PreAuthorize("@ss.hasPermi('xf:statistics:list')")
    @GetMapping("/overdue")
    public AjaxResult overdue()
    {
        Map<String, Object> data = xfWorkOrderService.selectStatistics();
        Map<String, Object> result = new HashMap<>();
        int overdueCount = data.get("overdue") != null ? ((Number) data.get("overdue")).intValue() : 0;
        int total = data.get("total") != null ? ((Number) data.get("total")).intValue() : 0;
        result.put("overdueCount", overdueCount);
        result.put("total", total);
        result.put("overdueRate", total > 0 ? String.format("%.2f", (double) overdueCount / total * 100) : "0.00");
        return success(result);
    }

    /**
     * 办理率统计
     */
    @PreAuthorize("@ss.hasPermi('xf:statistics:list')")
    @GetMapping("/completionRate")
    public AjaxResult completionRate()
    {
        Map<String, Object> data = xfWorkOrderService.selectStatistics();
        Map<String, Object> result = new HashMap<>();
        int total = data.get("total") != null ? ((Number) data.get("total")).intValue() : 0;
        int finished = data.get("finished") != null ? ((Number) data.get("finished")).intValue() : 0;
        result.put("total", total);
        result.put("finished", finished);
        result.put("completionRate", total > 0 ? String.format("%.2f", (double) finished / total * 100) : "0.00");
        return success(result);
    }

    /**
     * 退回率统计
     */
    @PreAuthorize("@ss.hasPermi('xf:statistics:list')")
    @GetMapping("/returnRate")
    public AjaxResult returnRate()
    {
        Map<String, Object> data = xfWorkOrderService.selectStatistics();
        Map<String, Object> result = new HashMap<>();
        int total = data.get("total") != null ? ((Number) data.get("total")).intValue() : 0;
        int returned = data.get("returned") != null ? ((Number) data.get("returned")).intValue() : 0;
        result.put("total", total);
        result.put("returned", returned);
        result.put("returnRate", total > 0 ? String.format("%.2f", (double) returned / total * 100) : "0.00");
        return success(result);
    }
}
