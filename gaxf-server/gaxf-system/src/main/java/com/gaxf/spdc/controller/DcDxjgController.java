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
import com.gaxf.spdc.domain.DcDxjg;
import com.gaxf.spdc.service.IDcDxjgService;
import com.gaxf.common.core.page.TableDataInfo;
import com.gaxf.common.utils.DateUtils;
import com.gaxf.common.utils.SecurityUtils;

/**
 * 预警管理Controller
 *
 * @author spdc
 * @date 2026-04-30
 */
@RestController
@RequestMapping("/spdc/alert")
public class DcDxjgController extends BaseController
{
    @Autowired
    private IDcDxjgService dcDxjgService;

    /**
     * 查询预警管理列表
     * 管理员可查看所有预警，非管理员只能查看本部门及下级部门的预警
     */
    @PreAuthorize("@ss.hasPermi('spdc:alert:list')")
    @GetMapping("/list")
    public TableDataInfo list(DcDxjg dcDxjg)
    {
        startPage();
        Long userDeptId = null;
        if (!SecurityUtils.isAdmin())
        {
            userDeptId = SecurityUtils.getDeptId();
        }
        List<DcDxjg> list = dcDxjgService.selectDcDxjgListPublic(dcDxjg, userDeptId);
        return getDataTable(list);
    }

    /**
     * 查询预警统计数据
     */
    @PreAuthorize("@ss.hasPermi('spdc:alert:list')")
    @GetMapping("/statistics")
    public AjaxResult statistics()
    {
        Long userDeptId = null;
        if (!SecurityUtils.isAdmin())
        {
            userDeptId = SecurityUtils.getDeptId();
        }
        DcDxjg stats = dcDxjgService.selectDcDxjgStatistics(userDeptId);
        return success(stats);
    }

    /**
     * 获取预警管理详细信息
     */
    @PreAuthorize("@ss.hasPermi('spdc:alert:query')")
    @GetMapping(value = "/{id}")
    public AjaxResult getInfo(@PathVariable("id") Integer id)
    {
        return success(dcDxjgService.selectDcDxjgById(id));
    }

    /**
     * 处置预警（更新处置状态和结果）
     */
    @PreAuthorize("@ss.hasPermi('spdc:alert:handle')")
    @Log(title = "预警管理", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult handle(@RequestBody DcDxjg dcDxjg)
    {
        dcDxjg.setUpdateBy(SecurityUtils.getUsername());
        return toAjax(dcDxjgService.updateDcDxjg(dcDxjg));
    }

    /**
     * 新增预警（公开接口，无需权限）
     */
    @Log(title = "预警管理", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody DcDxjg dcDxjg)
    {
        // 设置默认处置状态为待处理
        if (dcDxjg.getDeal() == null || dcDxjg.getDeal().isEmpty())
        {
            dcDxjg.setDeal("03");
        }
        // 设置默认是否处理为未处理
        if (dcDxjg.getIsResolved() == null)
        {
            dcDxjg.setIsResolved(0);
        }
        // 设置预警时间默认为当前时间
        if (dcDxjg.getTime() == null)
        {
            dcDxjg.setTime(DateUtils.getNowDate());
        }
        // 设置创建人ID
        try {
            dcDxjg.setCreator(SecurityUtils.getUserId().intValue());
        } catch (Exception e) {
            // 未登录时使用系统默认ID
            dcDxjg.setCreator(0);
        }
        return toAjax(dcDxjgService.insertDcDxjg(dcDxjg));
    }

    /**
     * 删除预警管理
     */
    @PreAuthorize("@ss.hasPermi('spdc:alert:remove')")
    @Log(title = "预警管理", businessType = BusinessType.DELETE)
    @DeleteMapping("/{ids}")
    public AjaxResult remove(@PathVariable Integer[] ids)
    {
        return toAjax(dcDxjgService.deleteDcDxjgByIds(ids));
    }
}