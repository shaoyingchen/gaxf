package com.gaxf.spdc.controller;

import java.util.List;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.gaxf.common.annotation.Log;
import com.gaxf.common.core.controller.BaseController;
import com.gaxf.common.core.domain.AjaxResult;
import com.gaxf.common.enums.BusinessType;
import com.gaxf.spdc.domain.DcPga;
import com.gaxf.spdc.domain.DcDxjg;
import com.gaxf.spdc.service.IDcPgaService;
import com.gaxf.spdc.service.IDcDxjgService;
import com.gaxf.common.core.page.TableDataInfo;
import com.gaxf.common.utils.SecurityUtils;

/**
 * 问题整改Controller
 *
 * @author spdc
 * @date 2026-04-30
 */
@RestController
@RequestMapping("/spdc/pga")
public class DcPgaController extends BaseController
{
    @Autowired
    private IDcPgaService dcPgaService;

    @Autowired
    private IDcDxjgService dcDxjgService;

    /**
     * 查询问题整改列表（带数据权限）
     */
    @PreAuthorize("@ss.hasPermi('spdc:pga:list')")
    @GetMapping("/list")
    public TableDataInfo list(DcPga dcPga)
    {
        startPage();
        Long userDeptId = null;
        if (!SecurityUtils.isAdmin())
        {
            userDeptId = SecurityUtils.getDeptId();
        }
        List<DcPga> list = dcPgaService.selectDcPgaList(dcPga, userDeptId);
        return getDataTable(list);
    }

    /**
     * 获取问题整改详细信息
     */
    @PreAuthorize("@ss.hasPermi('spdc:pga:query')")
    @GetMapping(value = "/{id}")
    public AjaxResult getInfo(@PathVariable("id") Integer id)
    {
        return success(dcPgaService.selectDcPgaById(id));
    }

    /**
     * 从预警创建问题整改记录（处置预警）
     */
    @PreAuthorize("@ss.hasPermi('spdc:alert:handle')")
    @Log(title = "问题整改", businessType = BusinessType.INSERT)
    @PostMapping("/createFromAlert/{alertId}")
    public AjaxResult createFromAlert(@PathVariable("alertId") Integer alertId, @RequestBody DcPga dcPga)
    {
        DcDxjg dxjg = dcDxjgService.selectDcDxjgById(alertId);
        if (dxjg == null)
        {
            return error("预警不存在");
        }
        dcPga.setCreator(SecurityUtils.getUserId().intValue());
        return toAjax(dcPgaService.createFromDxjg(dxjg, dcPga));
    }

    /**
     * 整改反馈提交
     */
    @PreAuthorize("@ss.hasPermi('spdc:pga:feedback')")
    @Log(title = "问题整改反馈", businessType = BusinessType.UPDATE)
    @PutMapping("/feedback")
    public AjaxResult feedback(@RequestBody DcPga dcPga)
    {
        dcPga.setUpdater(SecurityUtils.getUserId().intValue());
        return toAjax(dcPgaService.submitFeedback(dcPga));
    }

    /**
     * 修改问题整改
     */
    @PreAuthorize("@ss.hasPermi('spdc:pga:edit')")
    @Log(title = "问题整改", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody DcPga dcPga)
    {
        dcPga.setUpdater(SecurityUtils.getUserId().intValue());
        return toAjax(dcPgaService.updateDcPga(dcPga));
    }

    /**
     * 删除问题整改
     */
    @PreAuthorize("@ss.hasPermi('spdc:pga:remove')")
    @Log(title = "问题整改", businessType = BusinessType.DELETE)
    @DeleteMapping("/{ids}")
    public AjaxResult remove(@PathVariable Integer[] ids)
    {
        return toAjax(dcPgaService.deleteDcPgaByIds(ids));
    }
}