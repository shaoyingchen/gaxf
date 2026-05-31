package com.gaxf.system.service.impl;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.gaxf.system.domain.XfDossier;
import com.gaxf.system.mapper.XfDossierMapper;
import com.gaxf.system.service.IXfDossierService;

/**
 * 电子卷宗 服务层实现
 *
 * @author gaxf
 */
@Service
public class XfDossierServiceImpl implements IXfDossierService
{
    @Autowired
    private XfDossierMapper xfDossierMapper;

    /**
     * 查询电子卷宗信息
     *
     * @param id 卷宗ID
     * @return 电子卷宗信息
     */
    @Override
    public XfDossier selectXfDossierById(Long id)
    {
        return xfDossierMapper.selectXfDossierById(id);
    }

    /**
     * 查询电子卷宗列表
     *
     * @param xfDossier 电子卷宗信息
     * @return 电子卷宗集合
     */
    @Override
    public List<XfDossier> selectXfDossierList(XfDossier xfDossier)
    {
        return xfDossierMapper.selectXfDossierList(xfDossier);
    }

    /**
     * 根据工单ID查询电子卷宗
     *
     * @param orderId 工单ID
     * @return 电子卷宗集合
     */
    @Override
    public List<XfDossier> selectXfDossierByOrderId(Long orderId)
    {
        return xfDossierMapper.selectXfDossierByOrderId(orderId);
    }

    /**
     * 新增电子卷宗
     *
     * @param xfDossier 电子卷宗信息
     * @return 结果
     */
    @Override
    public int insertXfDossier(XfDossier xfDossier)
    {
        return xfDossierMapper.insertXfDossier(xfDossier);
    }

    /**
     * 修改电子卷宗
     *
     * @param xfDossier 电子卷宗信息
     * @return 结果
     */
    @Override
    public int updateXfDossier(XfDossier xfDossier)
    {
        return xfDossierMapper.updateXfDossier(xfDossier);
    }

    /**
     * 批量删除电子卷宗
     *
     * @param ids 需要删除的数据ID
     * @return 结果
     */
    @Override
    public int deleteXfDossierByIds(Long[] ids)
    {
        return xfDossierMapper.deleteXfDossierByIds(ids);
    }
}
