package com.gaxf.system.service;

import java.util.List;
import com.gaxf.system.domain.XfDocument;

/**
 * 文书 服务层
 *
 * @author gaxf
 */
public interface IXfDocumentService
{
    /**
     * 查询文书信息
     *
     * @param id 文书ID
     * @return 文书信息
     */
    public XfDocument selectXfDocumentById(Long id);

    /**
     * 查询文书列表
     *
     * @param xfDocument 文书信息
     * @return 文书集合
     */
    public List<XfDocument> selectXfDocumentList(XfDocument xfDocument);

    /**
     * 根据工单ID查询文书
     *
     * @param orderId 工单ID
     * @return 文书集合
     */
    public List<XfDocument> selectXfDocumentByOrderId(Long orderId);

    /**
     * 新增文书
     *
     * @param xfDocument 文书信息
     * @return 结果
     */
    public int insertXfDocument(XfDocument xfDocument);

    /**
     * 修改文书
     *
     * @param xfDocument 文书信息
     * @return 结果
     */
    public int updateXfDocument(XfDocument xfDocument);

    /**
     * 批量删除文书
     *
     * @param ids 需要删除的数据ID
     * @return 结果
     */
    public int deleteXfDocumentByIds(Long[] ids);

    /**
     * 生成文书
     *
     * @param orderId 工单ID
     * @param docType 文书类型
     * @return 文书信息
     */
    public XfDocument generateDocument(Long orderId, String docType);

    /**
     * 签发文书
     *
     * @param id 文书ID
     */
    public void signDocument(Long id);
}
