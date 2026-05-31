package com.gaxf.web.controller.xf;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.gaxf.common.annotation.Log;
import com.gaxf.common.core.controller.BaseController;
import com.gaxf.common.core.domain.AjaxResult;
import com.gaxf.common.core.page.TableDataInfo;
import com.gaxf.common.enums.BusinessType;
import com.gaxf.system.domain.XfMessage;
import com.gaxf.system.service.IXfMessageService;

/**
 * 站内消息 信息操作处理
 *
 * @author gaxf
 */
@RestController
@RequestMapping("/xf/message")
public class XfMessageController extends BaseController
{
    @Autowired
    private IXfMessageService xfMessageService;

    /**
     * 获取站内消息列表
     */
    @PreAuthorize("@ss.hasPermi('xf:message:list')")
    @GetMapping("/list")
    public TableDataInfo list(XfMessage xfMessage)
    {
        xfMessage.setReceiverId(getUserId());
        startPage();
        List<XfMessage> list = xfMessageService.selectXfMessageList(xfMessage);
        return getDataTable(list);
    }

    /**
     * 获取未读消息数量
     */
    @GetMapping("/unreadCount")
    public AjaxResult unreadCount()
    {
        int count = xfMessageService.selectUnreadCount(getUserId());
        AjaxResult result = AjaxResult.success();
        result.put("unreadCount", count);
        return result;
    }

    /**
     * 标记已读
     */
    @PutMapping("/read/{id}")
    public AjaxResult read(@PathVariable("id") Long id)
    {
        xfMessageService.readMessage(id);
        return success();
    }

    /**
     * 全部已读
     */
    @PutMapping("/readAll")
    public AjaxResult readAll()
    {
        xfMessageService.readAllMessage(getUserId());
        return success();
    }

    /**
     * 删除消息
     */
    @PreAuthorize("@ss.hasPermi('xf:message:list')")
    @Log(title = "站内消息", businessType = BusinessType.DELETE)
    @DeleteMapping("/{ids}")
    public AjaxResult remove(@PathVariable Long[] ids)
    {
        return toAjax(xfMessageService.deleteXfMessageByIds(ids));
    }
}
