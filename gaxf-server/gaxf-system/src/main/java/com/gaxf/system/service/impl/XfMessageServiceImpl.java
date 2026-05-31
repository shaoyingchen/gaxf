package com.gaxf.system.service.impl;

import java.util.Date;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.gaxf.system.domain.XfMessage;
import com.gaxf.system.mapper.XfMessageMapper;
import com.gaxf.system.service.IXfMessageService;

/**
 * 站内消息 服务层实现
 *
 * @author gaxf
 */
@Service
public class XfMessageServiceImpl implements IXfMessageService
{
    @Autowired
    private XfMessageMapper xfMessageMapper;

    /**
     * 查询站内消息信息
     *
     * @param id 消息ID
     * @return 站内消息信息
     */
    @Override
    public XfMessage selectXfMessageById(Long id)
    {
        return xfMessageMapper.selectXfMessageById(id);
    }

    /**
     * 查询站内消息列表
     *
     * @param xfMessage 站内消息信息
     * @return 站内消息集合
     */
    @Override
    public List<XfMessage> selectXfMessageList(XfMessage xfMessage)
    {
        return xfMessageMapper.selectXfMessageList(xfMessage);
    }

    /**
     * 查询未读消息数量
     *
     * @param receiverId 接收人ID
     * @return 未读消息数量
     */
    @Override
    public int selectUnreadCount(Long receiverId)
    {
        return xfMessageMapper.selectUnreadCount(receiverId);
    }

    /**
     * 新增站内消息
     *
     * @param xfMessage 站内消息信息
     * @return 结果
     */
    @Override
    public int insertXfMessage(XfMessage xfMessage)
    {
        return xfMessageMapper.insertXfMessage(xfMessage);
    }

    /**
     * 批量新增站内消息
     *
     * @param list 站内消息列表
     * @return 结果
     */
    @Override
    public int batchInsertXfMessage(List<XfMessage> list)
    {
        return xfMessageMapper.batchInsertXfMessage(list);
    }

    /**
     * 修改站内消息
     *
     * @param xfMessage 站内消息信息
     * @return 结果
     */
    @Override
    public int updateXfMessage(XfMessage xfMessage)
    {
        return xfMessageMapper.updateXfMessage(xfMessage);
    }

    /**
     * 批量删除站内消息
     *
     * @param ids 需要删除的数据ID
     * @return 结果
     */
    @Override
    public int deleteXfMessageByIds(Long[] ids)
    {
        return xfMessageMapper.deleteXfMessageByIds(ids);
    }

    /**
     * 标记已读
     *
     * @param id 消息ID
     */
    @Override
    public void readMessage(Long id)
    {
        XfMessage message = new XfMessage();
        message.setId(id);
        message.setIsRead("1");
        message.setReadTime(new Date());
        xfMessageMapper.updateXfMessage(message);
    }

    /**
     * 全部已读
     *
     * @param receiverId 接收人ID
     */
    @Override
    public void readAllMessage(Long receiverId)
    {
        xfMessageMapper.readAllByReceiverId(receiverId);
    }
}
