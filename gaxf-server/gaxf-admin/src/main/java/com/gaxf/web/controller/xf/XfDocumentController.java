package com.gaxf.web.controller.xf;

import java.util.List;
import java.util.Map;
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
import com.gaxf.system.domain.XfDocument;
import com.gaxf.system.service.IXfDocumentService;

/**
 * 文书 信息操作处理
 *
 * @author gaxf
 */
@RestController
@RequestMapping("/xf/document")
public class XfDocumentController extends BaseController
{
    @Autowired
    private IXfDocumentService xfDocumentService;

    /**
     * 获取文书列表
     */
    @PreAuthorize("@ss.hasPermi('xf:workOrder:list')")
    @GetMapping("/list")
    public TableDataInfo list(XfDocument xfDocument)
    {
        startPage();
        List<XfDocument> list = xfDocumentService.selectXfDocumentList(xfDocument);
        return getDataTable(list);
    }

    /**
     * 生成文书
     */
    @PreAuthorize("@ss.hasPermi('xf:workOrder:list')")
    @Log(title = "生成文书", businessType = BusinessType.INSERT)
    @PostMapping("/generate")
    public AjaxResult generate(@RequestBody Map<String, Object> params)
    {
        Long orderId = Long.valueOf(params.get("orderId").toString());
        String docType = params.get("docType").toString();
        XfDocument document = xfDocumentService.generateDocument(orderId, docType);
        document.setCreateBy(getUsername());
        xfDocumentService.insertXfDocument(document);
        return success(document);
    }

    /**
     * 签发文书
     */
    @PreAuthorize("@ss.hasPermi('xf:workOrder:list')")
    @Log(title = "签发文书", businessType = BusinessType.UPDATE)
    @PutMapping("/sign")
    public AjaxResult sign(@RequestBody XfDocument xfDocument)
    {
        xfDocumentService.signDocument(xfDocument.getId());
        return success();
    }

    /**
     * 下载文书
     */
    @PreAuthorize("@ss.hasPermi('xf:workOrder:list')")
    @GetMapping("/download/{id}")
    public AjaxResult download(@PathVariable("id") Long id)
    {
        XfDocument document = xfDocumentService.selectXfDocumentById(id);
        if (document == null)
        {
            return error("文书不存在");
        }
        return success(document);
    }

    /**
     * 删除文书
     */
    @PreAuthorize("@ss.hasPermi('xf:workOrder:list')")
    @Log(title = "文书", businessType = BusinessType.DELETE)
    @DeleteMapping("/{ids}")
    public AjaxResult remove(@PathVariable Long[] ids)
    {
        return toAjax(xfDocumentService.deleteXfDocumentByIds(ids));
    }
}
