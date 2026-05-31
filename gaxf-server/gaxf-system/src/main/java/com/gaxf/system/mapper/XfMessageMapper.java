package com.gaxf.system.mapper;

import java.util.List;
import com.gaxf.system.domain.XfMessage;

/**
 * 站内消息 数据层
 *
 * @author gaxf
 */
public interface XfMessageMapper
{
    /**
     * 查询站内消息信息
     *
     * @param id 消息ID
     * @return 站内消息信息
     */
    public XfMessage selectXfMessageById(Long id);

    /**
     * 查询站内消息列表
     *
     * @param xfMessage 站内消息信息
     * @return 站内消息集合
     */
    public List<XfMessage> selectXfMessageList(XfMessage xfMessage);

    /**
     * 查询未读消息数量
     *
     * @param receiverId 接收人ID
     * @return 未读消息数量
     */
    public int selectUnreadCount(Long receiverId);

    /**
     * 新增站内消息
     *
     * @param xfMessage 站内消息信息
     * @return 结果
     */
    public int insertXfMessage(XfMessage xfMessage);

    /**
     * 批量新增站内消息
     *
     * @param list 站内消息列表
     * @return 结果
     */
    public int batchInsertXfMessage(List<XfMessage> list);

    /**
     * 修改站内消息
     *
     * @param xfMessage 站内消息信息
     * @return 结果
     */
    public int updateXfMessage(XfMessage xfMessage);

    /**
     * 删除站内消息
     *
     * @param id 消息ID
     * @return 结果
     */
    public int deleteXfMessageById(Long id);

    /**
     * 批量删除站内消息
     *
     * @param ids 需要删除的数据ID
     * @return 结果
     */
    public int deleteXfMessageByIds(Long[] ids);

    /**
     * 标记全部已读
     *
     * @param receiverId 接收人ID
     * @return 结果
     */
    public int readAllByReceiverId(Long receiverId);
}
