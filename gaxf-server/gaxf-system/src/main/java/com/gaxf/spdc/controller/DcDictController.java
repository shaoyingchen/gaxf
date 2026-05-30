package com.gaxf.spdc.controller;

import java.util.List;
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
import com.gaxf.spdc.domain.DcDict;
import com.gaxf.spdc.service.IDcDictService;

/**
 * 业务字典Controller
 *
 * @author gaxf
 */
@RestController
@RequestMapping("/spdc/dict")
public class DcDictController extends BaseController
{
    @Autowired
    private IDcDictService dcDictService;

    /**
     * 查询业务字典树结构列表
     */
    @GetMapping("/tree")
    public AjaxResult tree(DcDict dcDict)
    {
        List<DcDict> list = dcDictService.selectDcDictTree(dcDict);
        return success(list);
    }

    /**
     * 查询业务字典列表
     */
    @GetMapping("/list")
    public AjaxResult list(DcDict dcDict)
    {
        List<DcDict> list = dcDictService.selectDcDictList(dcDict);
        return success(list);
    }

    /**
     * 获取业务字典详细信息
     */
    @GetMapping(value = "/{id}")
    public AjaxResult getInfo(@PathVariable("id") Integer id)
    {
        return success(dcDictService.selectDcDictById(id));
    }

    /**
     * 新增业务字典
     */
    @Log(title = "业务字典", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody DcDict dcDict)
    {
        dcDict.setCreateBy(getUsername());
        return toAjax(dcDictService.insertDcDict(dcDict));
    }

    /**
     * 修改业务字典
     */
    @Log(title = "业务字典", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody DcDict dcDict)
    {
        dcDict.setUpdateBy(getUsername());
        return toAjax(dcDictService.updateDcDict(dcDict));
    }

    /**
     * 删除业务字典
     */
    @Log(title = "业务字典", businessType = BusinessType.DELETE)
    @DeleteMapping("/{ids}")
    public AjaxResult remove(@PathVariable Integer[] ids)
    {
        return toAjax(dcDictService.deleteDcDictByIds(ids));
    }
}