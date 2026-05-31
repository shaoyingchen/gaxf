package com.gaxf.system.service.impl;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.gaxf.common.exception.ServiceException;
import com.gaxf.common.utils.DateUtils;
import com.gaxf.common.utils.StringUtils;
import com.gaxf.system.domain.XfAssignRecord;
import com.gaxf.system.domain.XfMessage;
import com.gaxf.system.domain.XfWorkOrder;
import com.gaxf.system.mapper.XfAssignRecordMapper;
import com.gaxf.system.mapper.XfMessageMapper;
import com.gaxf.system.mapper.XfWorkOrderMapper;
import com.gaxf.system.service.IXfWorkOrderService;

/**
 * 信访工单 服务层实现
 *
 * @author gaxf
 */
@Service
public class XfWorkOrderServiceImpl implements IXfWorkOrderService
{
    private static final Logger log = LoggerFactory.getLogger(XfWorkOrderServiceImpl.class);

    /** 导入任务进度缓存: taskId -> progress map */
    private final ConcurrentHashMap<String, Map<String, Object>> importProgressMap = new ConcurrentHashMap<>();

    @Autowired
    private XfWorkOrderMapper xfWorkOrderMapper;

    @Autowired
    private XfAssignRecordMapper xfAssignRecordMapper;

    @Autowired
    private XfMessageMapper xfMessageMapper;

    /**
     * 查询信访工单信息
     */
    @Override
    public XfWorkOrder selectXfWorkOrderById(Long id)
    {
        XfWorkOrder workOrder = xfWorkOrderMapper.selectXfWorkOrderById(id);
        if (workOrder != null)
        {
            List<XfAssignRecord> assignRecords = xfAssignRecordMapper.selectXfAssignRecordByOrderId(id);
            workOrder.setAssignRecords(assignRecords);
        }
        return workOrder;
    }

    /**
     * 查询信访工单列表
     */
    @Override
    public List<XfWorkOrder> selectXfWorkOrderList(XfWorkOrder xfWorkOrder)
    {
        return xfWorkOrderMapper.selectXfWorkOrderList(xfWorkOrder);
    }

    /**
     * 新增信访工单
     */
    @Override
    public int insertXfWorkOrder(XfWorkOrder xfWorkOrder)
    {
        if (StringUtils.isEmpty(xfWorkOrder.getXfCaseNo()))
        {
            xfWorkOrder.setXfCaseNo(generateOrderNo());
        }
        if (StringUtils.isEmpty(xfWorkOrder.getSourceType()))
        {
            xfWorkOrder.setSourceType("1");
        }
        if (StringUtils.isEmpty(xfWorkOrder.getStatus()))
        {
            xfWorkOrder.setStatus("0");
        }
        xfWorkOrder.setCreateTime(DateUtils.getNowDate());
        return xfWorkOrderMapper.insertXfWorkOrder(xfWorkOrder);
    }

    /**
     * 修改信访工单
     */
    @Override
    public int updateXfWorkOrder(XfWorkOrder xfWorkOrder)
    {
        return xfWorkOrderMapper.updateXfWorkOrder(xfWorkOrder);
    }

    /**
     * 批量删除信访工单
     */
    @Override
    public int deleteXfWorkOrderByIds(Long[] ids)
    {
        return xfWorkOrderMapper.deleteXfWorkOrderByIds(ids);
    }

    /**
     * 交办派单
     */
    @Override
    @Transactional
    public void assignWorkOrder(Long orderId, Long[] deptIds, String deadline, String[] deptNames)
    {
        XfWorkOrder workOrder = xfWorkOrderMapper.selectXfWorkOrderById(orderId);
        if (workOrder == null)
        {
            throw new ServiceException("工单不存在");
        }
        if (!"0".equals(workOrder.getStatus()))
        {
            throw new ServiceException("只有待派单状态的工单才能交办");
        }
        XfWorkOrder updateOrder = new XfWorkOrder();
        updateOrder.setId(orderId);
        updateOrder.setStatus("1");
        if (StringUtils.isNotEmpty(deadline))
        {
            updateOrder.setDeadline(DateUtils.parseDate(deadline));
        }
        xfWorkOrderMapper.updateXfWorkOrder(updateOrder);

        for (int i = 0; i < deptIds.length; i++)
        {
            XfAssignRecord record = new XfAssignRecord();
            record.setOrderId(orderId);
            record.setAssignDeptId(deptIds[i]);
            if (deptNames != null && i < deptNames.length)
            {
                record.setAssignDeptName(deptNames[i]);
            }
            record.setAssignType("0");
            record.setAssignTime(new Date());
            record.setStatus("0");
            xfAssignRecordMapper.insertXfAssignRecord(record);

            XfMessage message = new XfMessage();
            message.setOrderId(orderId);
            message.setReceiverId(deptIds[i]);
            message.setTitle("交办通知：" + workOrder.getTitle());
            message.setContent("您有一条新的交办工单，请及时接收处理。工单编号：" + workOrder.getXfCaseNo());
            message.setMsgType("0");
            xfMessageMapper.insertXfMessage(message);
        }
    }

    /**
     * 查询我的待办工单
     */
    @Override
    public List<XfWorkOrder> selectMyTodoList(XfWorkOrder xfWorkOrder)
    {
        return xfWorkOrderMapper.selectMyTodoList(xfWorkOrder);
    }

    /**
     * 查询统计数据
     */
    @Override
    public Map<String, Object> selectStatistics()
    {
        Map<String, Object> result = new HashMap<>();
        List<Map<String, Object>> stats = xfWorkOrderMapper.selectStatistics();
        int total = 0;
        for (Map<String, Object> stat : stats)
        {
            String status = String.valueOf(stat.get("status"));
            int count = ((Number) stat.get("count")).intValue();
            total += count;
            switch (status)
            {
                case "0": result.put("waitAssign", count); break;
                case "1": result.put("handling", count); break;
                case "2": result.put("reported", count); break;
                case "3": result.put("finished", count); break;
                case "4": result.put("returned", count); break;
                case "5": result.put("overdue", count); break;
                default: break;
            }
        }
        result.put("total", total);
        return result;
    }

    @Override
    public Map<String, Object> selectMyTodoStatistics(Long deptId)
    {
        Map<String, Object> result = new HashMap<>();
        result.put("pendingCount", 0);
        result.put("inProgressCount", 0);
        result.put("reportedCount", 0);
        List<Map<String, Object>> stats = xfWorkOrderMapper.selectMyTodoStatistics(deptId);
        for (Map<String, Object> stat : stats)
        {
            String status = String.valueOf(stat.get("status"));
            int count = ((Number) stat.get("count")).intValue();
            switch (status)
            {
                case "0": result.put("pendingCount", count); break;
                case "1": result.put("inProgressCount", count); break;
                case "2": result.put("reportedCount", count); break;
                default: break;
            }
        }
        return result;
    }

    /**
     * 检查超期工单
     */
    @Override
    @Transactional
    public void checkOverdue()
    {
        List<XfWorkOrder> overdueList = xfWorkOrderMapper.selectOverdueList();
        for (XfWorkOrder workOrder : overdueList)
        {
            XfWorkOrder updateOrder = new XfWorkOrder();
            updateOrder.setId(workOrder.getId());
            updateOrder.setStatus("5");
            updateOrder.setOverdueCount(workOrder.getOverdueCount() != null ? workOrder.getOverdueCount() + 1 : 1);
            xfWorkOrderMapper.updateXfWorkOrder(updateOrder);

            List<XfAssignRecord> assignRecords = xfAssignRecordMapper.selectXfAssignRecordByOrderId(workOrder.getId());
            for (XfAssignRecord record : assignRecords)
            {
                XfMessage message = new XfMessage();
                message.setOrderId(workOrder.getId());
                message.setReceiverId(record.getAssignDeptId());
                message.setTitle("超期预警：" + workOrder.getTitle());
                message.setContent("工单编号" + workOrder.getXfCaseNo() + "已超过限期报送截止时间，请尽快处理。");
                message.setMsgType("2");
                xfMessageMapper.insertXfMessage(message);
            }
        }
    }

    /**
     * 同步导入（内部调用）
     */
    @Override
    public String importWorkOrder(List<XfWorkOrder> workOrderList, String createBy)
    {
        if (StringUtils.isNull(workOrderList) || workOrderList.size() == 0)
        {
            throw new ServiceException("导入工单数据不能为空！");
        }
        int successNum = 0;
        int failureNum = 0;
        StringBuilder failureMsg = new StringBuilder();
        for (XfWorkOrder workOrder : workOrderList)
        {
            try
            {
                workOrder.setSourceType("0");
                workOrder.setStatus("0");
                if (StringUtils.isEmpty(workOrder.getXfCaseNo()))
                {
                    workOrder.setXfCaseNo(generateOrderNo());
                }
                workOrder.setCreateBy(createBy);
                this.insertXfWorkOrder(workOrder);
                successNum++;
            }
            catch (Exception e)
            {
                failureNum++;
                String msg = "<br/>" + failureNum + "、工单 " + workOrder.getXfCaseNo() + " 导入失败：";
                failureMsg.append(msg).append(e.getMessage());
                log.error(msg, e);
            }
        }
        if (failureNum > 0)
        {
            failureMsg.insert(0, "导入完成，成功 " + successNum + " 条，失败 " + failureNum + " 条。错误如下：");
        }
        else
        {
            failureMsg.insert(0, "数据已全部导入成功！共 " + successNum + " 条。");
        }
        return failureMsg.toString();
    }

    /**
     * 异步导入信访工单，立即返回任务ID
     */
    @Override
    public String asyncImportWorkOrder(List<XfWorkOrder> workOrderList, String createBy)
    {
        if (StringUtils.isNull(workOrderList) || workOrderList.size() == 0)
        {
            throw new ServiceException("导入工单数据不能为空！");
        }
        String taskId = UUID.randomUUID().toString().replace("-", "");
        int total = workOrderList.size();

        // 初始化进度
        Map<String, Object> progress = new HashMap<>();
        progress.put("taskId", taskId);
        progress.put("total", total);
        progress.put("success", 0);
        progress.put("fail", 0);
        progress.put("status", "processing");
        progress.put("message", "");
        importProgressMap.put(taskId, progress);

        // 异步执行导入
        doAsyncImport(taskId, workOrderList, createBy);

        return taskId;
    }

    /**
     * 异步执行导入逻辑
     */
    @Async
    protected void doAsyncImport(String taskId, List<XfWorkOrder> workOrderList, String createBy)
    {
        Map<String, Object> progress = importProgressMap.get(taskId);
        StringBuilder errorMsg = new StringBuilder();

        for (int i = 0; i < workOrderList.size(); i++)
        {
            XfWorkOrder workOrder = workOrderList.get(i);
            try
            {
                workOrder.setSourceType("0");
                workOrder.setStatus("0");
                if (StringUtils.isEmpty(workOrder.getXfCaseNo()))
                {
                    workOrder.setXfCaseNo(generateOrderNo());
                }
                workOrder.setCreateBy(createBy);
                xfWorkOrderMapper.insertXfWorkOrder(workOrder);
                progress.put("success", (int) progress.get("success") + 1);
            }
            catch (Exception e)
            {
                progress.put("fail", (int) progress.get("fail") + 1);
                String msg = "第" + (i + 1) + "条(编号" + workOrder.getXfCaseNo() + "): " + e.getMessage() + "; ";
                errorMsg.append(msg);
                log.error("导入工单失败, xfCaseNo={}", workOrder.getXfCaseNo(), e);
            }
            // 更新进度
            progress.put("current", i + 1);
        }

        progress.put("status", "done");
        if ((int) progress.get("fail") > 0)
        {
            progress.put("message", "导入完成，成功" + progress.get("success") + "条，失败" + progress.get("fail") + "条。" + errorMsg.toString());
        }
        else
        {
            progress.put("message", "全部导入成功！共" + progress.get("success") + "条。");
        }

        // 10分钟后自动清理进度缓存
        new Thread(() -> {
            try { Thread.sleep(600000); } catch (InterruptedException ignored) {}
            importProgressMap.remove(taskId);
        }).start();
    }

    /**
     * 查询导入进度
     */
    @Override
    public Map<String, Object> getImportProgress(String taskId)
    {
        Map<String, Object> progress = importProgressMap.get(taskId);
        if (progress == null)
        {
            Map<String, Object> result = new HashMap<>();
            result.put("status", "not_found");
            result.put("message", "任务不存在或已过期");
            return result;
        }
        return progress;
    }

    /**
     * 生成工单编号
     */
    private String generateOrderNo()
    {
        return "XF" + DateUtils.dateTimeNow("yyyyMMddHHmmss") + String.format("%03d", (int) (Math.random() * 1000));
    }
}
