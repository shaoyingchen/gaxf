package com.gaxf.web.controller.xf;

import java.util.List;
import java.util.Map;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import com.gaxf.common.annotation.Log;
import com.gaxf.common.core.controller.BaseController;
import com.gaxf.common.core.domain.AjaxResult;
import com.gaxf.common.core.page.TableDataInfo;
import com.gaxf.common.enums.BusinessType;
import com.gaxf.common.utils.poi.ExcelUtil;
import com.gaxf.system.domain.XfWorkOrder;
import com.gaxf.system.service.IXfApproveRuntimeService;
import com.gaxf.system.service.IXfWorkOrderService;

/**
 * 信访工单 信息操作处理
 *
 * @author gaxf
 */
@RestController
@RequestMapping("/xf/workOrder")
public class XfWorkOrderController extends BaseController
{
    @Autowired
    private IXfWorkOrderService xfWorkOrderService;

    @Autowired
    private IXfApproveRuntimeService xfApproveRuntimeService;

    /**
     * 获取信访工单列表
     */
    @PreAuthorize("@ss.hasPermi('xf:workOrder:list')")
    @GetMapping("/list")
    public TableDataInfo list(XfWorkOrder xfWorkOrder)
    {
        startPage();
        List<XfWorkOrder> list = xfWorkOrderService.selectXfWorkOrderList(xfWorkOrder);
        return getDataTable(list);
    }

    /**
     * 导出信访工单列表
     */
    @PreAuthorize("@ss.hasPermi('xf:workOrder:list')")
    @Log(title = "信访工单", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, XfWorkOrder xfWorkOrder)
    {
        List<XfWorkOrder> list = xfWorkOrderService.selectXfWorkOrderList(xfWorkOrder);
        ExcelUtil<XfWorkOrder> util = new ExcelUtil<XfWorkOrder>(XfWorkOrder.class);
        util.exportExcel(response, list, "信访工单数据");
    }

    /**
     * 获取信访工单详细信息
     */
    @PreAuthorize("@ss.hasPermi('xf:workOrder:list')")
    @GetMapping(value = "/{id}")
    public AjaxResult getInfo(@PathVariable("id") Long id)
    {
        return success(xfWorkOrderService.selectXfWorkOrderById(id));
    }

    /**
     * 新增信访工单
     */
    @PreAuthorize("@ss.hasPermi('xf:workOrder:list')")
    @Log(title = "信访工单", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody XfWorkOrder xfWorkOrder)
    {
        xfWorkOrder.setCreateBy(getUsername());
        return toAjax(xfWorkOrderService.insertXfWorkOrder(xfWorkOrder));
    }

    /**
     * 修改信访工单
     */
    @PreAuthorize("@ss.hasPermi('xf:workOrder:list')")
    @Log(title = "信访工单", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody XfWorkOrder xfWorkOrder)
    {
        xfWorkOrder.setUpdateBy(getUsername());
        return toAjax(xfWorkOrderService.updateXfWorkOrder(xfWorkOrder));
    }

    /**
     * 删除信访工单
     */
    @PreAuthorize("@ss.hasPermi('xf:workOrder:list')")
    @Log(title = "信访工单", businessType = BusinessType.DELETE)
    @DeleteMapping("/{ids}")
    public AjaxResult remove(@PathVariable Long[] ids)
    {
        return toAjax(xfWorkOrderService.deleteXfWorkOrderByIds(ids));
    }

    /**
     * Excel异步导入生成工单（立即返回taskId）
     */
    @PreAuthorize("@ss.hasPermi('xf:workOrder:list')")
    @Log(title = "信访工单", businessType = BusinessType.IMPORT)
    @PostMapping("/import")
    public AjaxResult importData(MultipartFile file, boolean updateSupport) throws Exception
    {
        ExcelUtil<XfWorkOrder> util = new ExcelUtil<XfWorkOrder>(XfWorkOrder.class);
        List<XfWorkOrder> workOrderList = util.importExcel(file.getInputStream());
        String taskId = xfWorkOrderService.asyncImportWorkOrder(workOrderList, getUsername());
        AjaxResult ajax = AjaxResult.success("导入任务已提交");
        ajax.put("taskId", taskId);
        return ajax;
    }

    /**
     * 查询导入进度
     */
    @PreAuthorize("@ss.hasPermi('xf:workOrder:list')")
    @GetMapping("/importProgress/{taskId}")
    public AjaxResult importProgress(@PathVariable String taskId)
    {
        Map<String, Object> data = xfWorkOrderService.getImportProgress(taskId);
        return success(data);
    }

    /**
     * 交办派单
     */
    @PreAuthorize("@ss.hasPermi('xf:workOrder:list')")
    @Log(title = "信访工单交办", businessType = BusinessType.UPDATE)
    @PostMapping("/assign")
    @SuppressWarnings("unchecked")
    public AjaxResult assign(@RequestBody Map<String, Object> params)
    {
        Long orderId = Long.valueOf(params.get("orderId").toString());
        List<?> rawDeptIds = (List<?>) params.get("deptIds");
        Long[] deptIds = rawDeptIds.stream().map(item -> Long.valueOf(item.toString())).toArray(Long[]::new);
        String deadline = params.get("deadline") != null ? params.get("deadline").toString() : null;
        String[] deptNames = null;
        if (params.get("deptNames") != null)
        {
            List<String> deptNameList = (List<String>) params.get("deptNames");
            deptNames = deptNameList.toArray(new String[0]);
        }
        xfWorkOrderService.assignWorkOrder(orderId, deptIds, deadline, deptNames);
        return success();
    }

    /**
     * 查询审批进度
     */
    @PreAuthorize("@ss.hasPermi('xf:workOrder:list')")
    @GetMapping("/approveProgress/{orderId}")
    public AjaxResult approveProgress(@PathVariable Long orderId)
    {
        return success(xfApproveRuntimeService.selectProgressByOrderId(orderId));
    }

    /**
     * 获取我的待办工单
     */
    @PreAuthorize("@ss.hasPermi('xf:workOrder:list')")
    @GetMapping("/myTodo")
    public TableDataInfo myTodo(XfWorkOrder xfWorkOrder)
    {
        xfWorkOrder.getParams().put("assignDeptId", getDeptId());
        startPage();
        List<XfWorkOrder> list = xfWorkOrderService.selectMyTodoList(xfWorkOrder);
        return getDataTable(list);
    }

    /**
     * 获取我的待办统计
     */
    @PreAuthorize("@ss.hasPermi('xf:workOrder:list')")
    @GetMapping("/myTodoStatistics")
    public AjaxResult myTodoStatistics()
    {
        Map<String, Object> data = xfWorkOrderService.selectMyTodoStatistics(getDeptId());
        return success(data);
    }

    /**
     * 获取统计数据
     */
    @PreAuthorize("@ss.hasPermi('xf:workOrder:list')")
    @GetMapping("/statistics")
    public AjaxResult statistics()
    {
        Map<String, Object> data = xfWorkOrderService.selectStatistics();
        return success(data);
    }
}
