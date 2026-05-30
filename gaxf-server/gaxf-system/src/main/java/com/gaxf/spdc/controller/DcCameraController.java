package com.gaxf.spdc.controller;

import java.util.List;
import jakarta.servlet.http.HttpServletResponse;
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
import com.gaxf.spdc.domain.DcCamera;
import com.gaxf.spdc.service.IDcCameraService;
import com.gaxf.common.utils.poi.ExcelUtil;
import com.gaxf.common.core.page.TableDataInfo;
import com.gaxf.common.utils.SecurityUtils;

/**
 * 点位管理Controller
 *
 * @author ruoyi
 * @date 2026-04-15
 */
@RestController
@RequestMapping("/spdc/camera")
public class DcCameraController extends BaseController
{
    @Autowired
    private IDcCameraService dcCameraService;

    /**
     * 查询点位管理列表
     * 管理员可查看所有点位，非管理员只能查看本部门及下级部门的点位
     */
    @PreAuthorize("@ss.hasPermi('spdc:camera:list')")
    @GetMapping("/list")
    public TableDataInfo list(DcCamera dcCamera)
    {
        startPage();
        // 数据范围过滤
        Long userDeptId = null;
        if (!SecurityUtils.isAdmin())
        {
            userDeptId = SecurityUtils.getDeptId();
        }
        List<DcCamera> list = dcCameraService.selectDcCameraListPublic(dcCamera, userDeptId);
        return getDataTable(list);
    }

    /**
     * 导出点位管理列表
     */
    @PreAuthorize("@ss.hasPermi('spdc:camera:export')")
    @Log(title = "点位管理", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, DcCamera dcCamera)
    {
        // 数据范围过滤
        Long userDeptId = null;
        if (!SecurityUtils.isAdmin())
        {
            userDeptId = SecurityUtils.getDeptId();
        }
        List<DcCamera> list = dcCameraService.selectDcCameraListPublic(dcCamera, userDeptId);
        ExcelUtil<DcCamera> util = new ExcelUtil<DcCamera>(DcCamera.class);
        util.exportExcel(response, list, "点位管理数据");
    }

    /**
     * 获取点位管理详细信息
     */
    @PreAuthorize("@ss.hasPermi('spdc:camera:query')")
    @GetMapping(value = "/{pointId}")
    public AjaxResult getInfo(@PathVariable("pointId") Integer pointId)
    {
        return success(dcCameraService.selectDcCameraByPointId(pointId));
    }

    /**
     * 新增点位管理
     */
    @PreAuthorize("@ss.hasPermi('spdc:camera:add')")
    @Log(title = "点位管理", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody DcCamera dcCamera)
    {
        return toAjax(dcCameraService.insertDcCamera(dcCamera));
    }

    /**
     * 修改点位管理
     */
    @PreAuthorize("@ss.hasPermi('spdc:camera:edit')")
    @Log(title = "点位管理", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody DcCamera dcCamera)
    {
        return toAjax(dcCameraService.updateDcCamera(dcCamera));
    }

    /**
     * 删除点位管理
     */
    @PreAuthorize("@ss.hasPermi('spdc:camera:remove')")
    @Log(title = "点位管理", businessType = BusinessType.DELETE)
	@DeleteMapping("/{pointIds}")
    public AjaxResult remove(@PathVariable Integer[] pointIds)
    {
        return toAjax(dcCameraService.deleteDcCameraByPointIds(pointIds));
    }
}
