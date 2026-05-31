package com.gaxf.system.service;

import java.util.List;
import com.gaxf.system.domain.XfDossier;

/**
 * 电子卷宗 服务层
 *
 * @author gaxf
 */
public interface IXfDossierService
{
    /**
     * 查询电子卷宗信息
     *
     * @param id 卷宗ID
     * @return 电子卷宗信息
     */
    public XfDossier selectXfDossierById(Long id);

    /**
     * 查询电子卷宗列表
     *
     * @param xfDossier 电子卷宗信息
     * @return 电子卷宗集合
     */
    public List<XfDossier> selectXfDossierList(XfDossier xfDossier);

    /**
     * 根据工单ID查询电子卷宗
     *
     * @param orderId 工单ID
     * @return 电子卷宗集合
     */
    public List<XfDossier> selectXfDossierByOrderId(Long orderId);

    /**
     * 新增电子卷宗
     *
     * @param xfDossier 电子卷宗信息
     * @return 结果
     */
    public int insertXfDossier(XfDossier xfDossier);

    /**
     * 修改电子卷宗
     *
     * @param xfDossier 电子卷宗信息
     * @return 结果
     */
    public int updateXfDossier(XfDossier xfDossier);

    /**
     * 批量删除电子卷宗
     *
     * @param ids 需要删除的数据ID
     * @return 结果
     */
    public int deleteXfDossierByIds(Long[] ids);
}
