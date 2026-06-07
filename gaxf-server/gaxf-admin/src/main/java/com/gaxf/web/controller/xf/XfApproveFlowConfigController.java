package com.gaxf.web.controller.xf;

import java.util.List;
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
import com.gaxf.common.annotation.Log;
import com.gaxf.common.core.controller.BaseController;
import com.gaxf.common.core.domain.AjaxResult;
import com.gaxf.common.core.page.TableDataInfo;
import com.gaxf.common.enums.BusinessType;
import com.gaxf.system.domain.XfApproveFlowConfig;
import com.gaxf.system.domain.vo.XfApproveFlowConfigBody;
import com.gaxf.system.service.IXfApproveFlowConfigService;

/**
 * 审批流配置 控制器
 *
 * @author gaxf
 */
@RestController
@RequestMapping("/xf/approve/config")
public class XfApproveFlowConfigController extends BaseController
{
    @Autowired
    private IXfApproveFlowConfigService xfApproveFlowConfigService;

    @PreAuthorize("@ss.hasPermi('xf:approveConfig:list')")
    @GetMapping("/list")
    public TableDataInfo list(XfApproveFlowConfig query)
    {
        startPage();
        List<XfApproveFlowConfig> list = xfApproveFlowConfigService.selectXfApproveFlowConfigList(query);
        return getDataTable(list);
    }

    @PreAuthorize("@ss.hasPermi('xf:approveConfig:query')")
    @GetMapping("/{id}")
    public AjaxResult getInfo(@PathVariable Long id)
    {
        return success(xfApproveFlowConfigService.selectXfApproveFlowConfigById(id));
    }

    @PreAuthorize("@ss.hasPermi('xf:approveConfig:add')")
    @Log(title = "审批流配置", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody XfApproveFlowConfigBody body)
    {
        body.setCreateBy(getUsername());
        return toAjax(xfApproveFlowConfigService.insertXfApproveFlowConfig(body));
    }

    @PreAuthorize("@ss.hasPermi('xf:approveConfig:edit')")
    @Log(title = "审批流配置", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody XfApproveFlowConfigBody body)
    {
        body.setUpdateBy(getUsername());
        return toAjax(xfApproveFlowConfigService.updateXfApproveFlowConfig(body));
    }

    @PreAuthorize("@ss.hasPermi('xf:approveConfig:remove')")
    @Log(title = "审批流配置", businessType = BusinessType.DELETE)
    @DeleteMapping("/{ids}")
    public AjaxResult remove(@PathVariable Long[] ids)
    {
        return toAjax(xfApproveFlowConfigService.deleteXfApproveFlowConfigByIds(ids));
    }
}
