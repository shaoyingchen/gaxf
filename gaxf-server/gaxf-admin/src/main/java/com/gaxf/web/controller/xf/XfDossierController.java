package com.gaxf.web.controller.xf;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.gaxf.common.annotation.Log;
import com.gaxf.common.core.controller.BaseController;
import com.gaxf.common.core.domain.AjaxResult;
import com.gaxf.common.core.page.TableDataInfo;
import com.gaxf.common.enums.BusinessType;
import com.gaxf.system.domain.XfDossier;
import com.gaxf.system.service.IXfDossierService;

/**
 * 电子卷宗 信息操作处理
 *
 * @author gaxf
 */
@RestController
@RequestMapping("/xf/dossier")
public class XfDossierController extends BaseController
{
    @Autowired
    private IXfDossierService xfDossierService;

    /**
     * 获取电子卷宗列表
     */
    @PreAuthorize("@ss.hasPermi('xf:workOrder:list')")
    @GetMapping("/list")
    public TableDataInfo list(XfDossier xfDossier)
    {
        startPage();
        List<XfDossier> list = xfDossierService.selectXfDossierList(xfDossier);
        return getDataTable(list);
    }

    /**
     * 上传电子卷宗
     */
    @PreAuthorize("@ss.hasPermi('xf:workOrder:list')")
    @Log(title = "电子卷宗", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody XfDossier xfDossier)
    {
        xfDossier.setCreateBy(getUsername());
        xfDossier.setUploadBy(getUsername());
        return toAjax(xfDossierService.insertXfDossier(xfDossier));
    }

    /**
     * 删除电子卷宗
     */
    @PreAuthorize("@ss.hasPermi('xf:workOrder:list')")
    @Log(title = "电子卷宗", businessType = BusinessType.DELETE)
    @DeleteMapping("/{ids}")
    public AjaxResult remove(@PathVariable Long[] ids)
    {
        return toAjax(xfDossierService.deleteXfDossierByIds(ids));
    }
}
