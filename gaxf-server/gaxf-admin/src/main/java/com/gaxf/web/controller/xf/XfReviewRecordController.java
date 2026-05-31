package com.gaxf.web.controller.xf;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.gaxf.common.annotation.Log;
import com.gaxf.common.core.controller.BaseController;
import com.gaxf.common.core.domain.AjaxResult;
import com.gaxf.common.core.page.TableDataInfo;
import com.gaxf.common.enums.BusinessType;
import com.gaxf.system.domain.XfReviewRecord;
import com.gaxf.system.service.IXfReviewRecordService;

/**
 * 审核记录 信息操作处理
 *
 * @author gaxf
 */
@RestController
@RequestMapping("/xf/review")
public class XfReviewRecordController extends BaseController
{
    @Autowired
    private IXfReviewRecordService xfReviewRecordService;

    /**
     * 获取审核记录列表
     */
    @PreAuthorize("@ss.hasPermi('xf:workOrder:list')")
    @GetMapping("/list")
    public TableDataInfo list(XfReviewRecord xfReviewRecord)
    {
        startPage();
        List<XfReviewRecord> list = xfReviewRecordService.selectXfReviewRecordList(xfReviewRecord);
        return getDataTable(list);
    }

    /**
     * 警种评查复核
     */
    @PreAuthorize("@ss.hasPermi('xf:workOrder:list')")
    @Log(title = "警种评查复核", businessType = BusinessType.UPDATE)
    @PostMapping("/policeReview")
    public AjaxResult policeReview(@RequestBody XfReviewRecord xfReviewRecord)
    {
        xfReviewRecord.setReviewerId(getUserId());
        xfReviewRecord.setReviewerName(getUsername());
        xfReviewRecord.setDeptId(getDeptId());
        xfReviewRecord.setCreateBy(getUsername());
        xfReviewRecordService.policeReview(xfReviewRecord);
        return success();
    }

    /**
     * 专员审核
     */
    @PreAuthorize("@ss.hasPermi('xf:workOrder:list')")
    @Log(title = "专员审核", businessType = BusinessType.UPDATE)
    @PostMapping("/commissionerReview")
    public AjaxResult commissionerReview(@RequestBody XfReviewRecord xfReviewRecord)
    {
        xfReviewRecord.setReviewerId(getUserId());
        xfReviewRecord.setReviewerName(getUsername());
        xfReviewRecord.setDeptId(getDeptId());
        xfReviewRecord.setCreateBy(getUsername());
        xfReviewRecordService.commissionerReview(xfReviewRecord);
        return success();
    }

    /**
     * 支队领导审核
     */
    @PreAuthorize("@ss.hasPermi('xf:workOrder:list')")
    @Log(title = "支队领导审核", businessType = BusinessType.UPDATE)
    @PostMapping("/leaderReview")
    public AjaxResult leaderReview(@RequestBody XfReviewRecord xfReviewRecord)
    {
        xfReviewRecord.setReviewerId(getUserId());
        xfReviewRecord.setReviewerName(getUsername());
        xfReviewRecord.setDeptId(getDeptId());
        xfReviewRecord.setCreateBy(getUsername());
        xfReviewRecordService.leaderReview(xfReviewRecord);
        return success();
    }
}
