package com.gaxf.spdc.mapper;

import java.util.List;
import com.gaxf.spdc.domain.DcPga;

/**
 * 问题整改Mapper接口
 *
 * @author spdc
 * @date 2026-04-30
 */
public interface DcPgaMapper
{
    /**
     * 查询问题整改
     */
    public DcPga selectDcPgaById(Integer id);

    /**
     * 查询问题整改列表
     */
    public List<DcPga> selectDcPgaList(DcPga dcPga);

    /**
     * 新增问题整改
     */
    public int insertDcPga(DcPga dcPga);

    /**
     * 修改问题整改
     */
    public int updateDcPga(DcPga dcPga);

    /**
     * 删除问题整改
     */
    public int deleteDcPgaById(Integer id);

    /**
     * 批量删除问题整改
     */
    public int deleteDcPgaByIds(Integer[] ids);
}