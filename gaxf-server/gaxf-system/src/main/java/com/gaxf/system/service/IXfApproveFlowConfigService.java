package com.gaxf.system.service;

import java.util.List;
import com.gaxf.system.domain.XfApproveFlowConfig;
import com.gaxf.system.domain.vo.XfApproveFlowConfigBody;

/**
 * 审批流配置 Service 接口
 *
 * @author gaxf
 */
public interface IXfApproveFlowConfigService
{
    public XfApproveFlowConfig selectXfApproveFlowConfigById(Long id);

    public List<XfApproveFlowConfig> selectXfApproveFlowConfigList(XfApproveFlowConfig query);

    public int insertXfApproveFlowConfig(XfApproveFlowConfigBody body);

    public int updateXfApproveFlowConfig(XfApproveFlowConfigBody body);

    public int deleteXfApproveFlowConfigByIds(Long[] ids);
}
