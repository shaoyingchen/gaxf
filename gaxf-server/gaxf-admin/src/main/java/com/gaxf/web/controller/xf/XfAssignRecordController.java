package com.gaxf.web.controller.xf;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import com.gaxf.common.annotation.Log;
import com.gaxf.common.core.controller.BaseController;
import com.gaxf.common.core.domain.AjaxResult;
import com.gaxf.common.core.page.TableDataInfo;
import com.gaxf.common.enums.BusinessType;
import com.gaxf.system.domain.XfAssignRecord;
import com.gaxf.system.service.IXfAssignRecordService;

/**
 * 交办记录 信息操作处理
 *
 * @author gaxf
 */
@RestController
@RequestMapping("/xf/assign")
public class XfAssignRecordController extends BaseController
{
    @Autowired
    private IXfAssignRecordService xfAssignRecordService;

    /**
     * 获取交办记录列表
     */
    @PreAuthorize("@ss.hasPermi('xf:workOrder:list')")
    @GetMapping("/list")
    public TableDataInfo list(XfAssignRecord xfAssignRecord)
    {
        startPage();
        List<XfAssignRecord> list = xfAssignRecordService.selectXfAssignRecordList(xfAssignRecord);
        return getDataTable(list);
    }

    /**
     * 获取交办记录详细信息
     */
    @PreAuthorize("@ss.hasPermi('xf:workOrder:list')")
    @GetMapping(value = "/{id}")
    public AjaxResult getInfo(@PathVariable("id") Long id)
    {
        return success(xfAssignRecordService.selectXfAssignRecordById(id));
    }

    /**
     * 接收工单
     */
    @PreAuthorize("@ss.hasPermi('xf:workOrder:list')")
    @Log(title = "接收工单", businessType = BusinessType.UPDATE)
    @PutMapping("/receive")
    public AjaxResult receive(@RequestBody XfAssignRecord xfAssignRecord)
    {
        xfAssignRecordService.receiveWorkOrder(xfAssignRecord.getId());
        return success();
    }

    /**
     * 提交办理报告
     */
    @PreAuthorize("@ss.hasPermi('xf:workOrder:list')")
    @Log(title = "提交办理报告", businessType = BusinessType.UPDATE)
    @PostMapping("/submitReport")
    public AjaxResult submitReport(
            @RequestParam("id") Long id,
            @RequestParam("reportContent") String reportContent,
            @RequestParam(value = "reportFile", required = false) MultipartFile reportFile,
            @RequestParam(value = "dossierFiles", required = false) MultipartFile[] dossierFiles)
    {
        xfAssignRecordService.submitReport(id, reportContent, reportFile, dossierFiles, getUsername());
        return success();
    }

    /**
     * 县局领导审核
     */
    @PreAuthorize("@ss.hasPermi('xf:workOrder:list')")
    @Log(title = "县局领导审核", businessType = BusinessType.UPDATE)
    @PutMapping("/countyReview")
    public AjaxResult countyReview(@RequestBody XfAssignRecord xfAssignRecord)
    {
        xfAssignRecordService.countyReview(xfAssignRecord.getId(), xfAssignRecord.getRemark(), xfAssignRecord.getStatus());
        return success();
    }
}
