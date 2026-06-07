package com.gaxf.system.domain.vo;

import java.util.List;
import com.gaxf.system.domain.XfApproveFlowConfig;
import com.gaxf.system.domain.XfApproveFlowStage;

/**
 * 审批流配置请求体
 *
 * @author gaxf
 */
public class XfApproveFlowConfigBody extends XfApproveFlowConfig
{
    private static final long serialVersionUID = 1L;

    @Override
    public List<XfApproveFlowStage> getStages()
    {
        return super.getStages();
    }
}
