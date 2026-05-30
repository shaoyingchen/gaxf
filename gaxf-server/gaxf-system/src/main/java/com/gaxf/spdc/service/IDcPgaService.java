package com.gaxf.spdc.service;

import java.util.List;
import com.gaxf.spdc.domain.DcPga;
import com.gaxf.spdc.domain.DcDxjg;

/**
 * 问题整改Service接口
 *
 * @author spdc
 * @date 2026-04-30
 */
public interface IDcPgaService
{
    /**
     * 查询问题整改
     */
    public DcPga selectDcPgaById(Integer id);

    /**
     * 查询问题整改列表（带数据权限）
     */
    public List<DcPga> selectDcPgaList(DcPga dcPga, Long userDeptId);

    /**
     * 从预警创建问题整改记录
     */
    public int createFromDxjg(DcDxjg dxjg, DcPga dcPga);

    /**
     * 整改反馈提交
     */
    public int submitFeedback(DcPga dcPga);

    /**
     * 修改问题整改
     */
    public int updateDcPga(DcPga dcPga);

    /**
     * 批量删除问题整改
     */
    public int deleteDcPgaByIds(Integer[] ids);

    /**
     * 删除问题整改信息
     */
    public int deleteDcPgaById(Integer id);
}