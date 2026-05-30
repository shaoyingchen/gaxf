package com.gaxf.spdc.service.impl;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.gaxf.common.utils.StringUtils;
import com.gaxf.spdc.domain.DcDict;
import com.gaxf.spdc.mapper.DcDictMapper;
import com.gaxf.spdc.service.IDcDictService;

/**
 * 业务字典Service业务层处理
 *
 * @author gaxf
 */
@Service
public class DcDictServiceImpl implements IDcDictService
{
    @Autowired
    private DcDictMapper dcDictMapper;

    /**
     * 查询业务字典
     *
     * @param id 业务字典主键
     * @return 业务字典
     */
    @Override
    public DcDict selectDcDictById(Integer id)
    {
        return dcDictMapper.selectDcDictById(id);
    }

    /**
     * 查询业务字典列表
     *
     * @param dcDict 业务字典
     * @return 业务字典
     */
    @Override
    public List<DcDict> selectDcDictList(DcDict dcDict)
    {
        return dcDictMapper.selectDcDictList(dcDict);
    }

    /**
     * 构建前端所需要树结构
     *
     * @param dicts 业务字典列表
     * @return 树结构列表
     */
    public List<DcDict> buildDictTree(List<DcDict> dicts)
    {
        List<DcDict> returnList = new ArrayList<DcDict>();
        List<Integer> tempList = dicts.stream().map(DcDict::getId).collect(Collectors.toList());
        for (DcDict dict : dicts)
        {
            // 如果是顶级节点, 遍历该父节点的所有子节点
            if (!tempList.contains(dict.getParentId()))
            {
                recursionFn(dicts, dict);
                returnList.add(dict);
            }
        }
        if (returnList.isEmpty())
        {
            returnList = dicts;
        }
        return returnList;
    }

    /**
     * 递归列表
     */
    private void recursionFn(List<DcDict> list, DcDict t)
    {
        // 得到子节点列表
        List<DcDict> childList = getChildList(list, t);
        t.setChildren(childList);
        for (DcDict tChild : childList)
        {
            if (hasChild(list, tChild))
            {
                recursionFn(list, tChild);
            }
        }
    }

    /**
     * 得到子节点列表
     */
    private List<DcDict> getChildList(List<DcDict> list, DcDict t)
    {
        List<DcDict> tlist = new ArrayList<DcDict>();
        Iterator<DcDict> it = list.iterator();
        while (it.hasNext())
        {
            DcDict n = (DcDict) it.next();
            if (StringUtils.isNotNull(n.getParentId()) && n.getParentId().equals(t.getId()))
            {
                tlist.add(n);
            }
        }
        return tlist;
    }

    /**
     * 判断是否有子节点
     */
    private boolean hasChild(List<DcDict> list, DcDict t)
    {
        return getChildList(list, t).size() > 0;
    }

    /**
     * 查询业务字典树结构
     *
     * @param dcDict 业务字典
     * @return 业务字典树集合
     */
    @Override
    public List<DcDict> selectDcDictTree(DcDict dcDict)
    {
        List<DcDict> dicts = dcDictMapper.selectDcDictTree(dcDict);
        return buildDictTree(dicts);
    }

    /**
     * 新增业务字典
     *
     * @param dcDict 业务字典
     * @return 结果
     */
    @Override
    public int insertDcDict(DcDict dcDict)
    {
        return dcDictMapper.insertDcDict(dcDict);
    }

    /**
     * 修改业务字典
     *
     * @param dcDict 业务字典
     * @return 结果
     */
    @Override
    public int updateDcDict(DcDict dcDict)
    {
        return dcDictMapper.updateDcDict(dcDict);
    }

    /**
     * 批量删除业务字典
     *
     * @param ids 需要删除的业务字典主键
     * @return 结果
     */
    @Override
    public int deleteDcDictByIds(Integer[] ids)
    {
        return dcDictMapper.deleteDcDictByIds(ids);
    }

    /**
     * 删除业务字典信息
     *
     * @param id 业务字典主键
     * @return 结果
     */
    @Override
    public int deleteDcDictById(Integer id)
    {
        return dcDictMapper.deleteDcDictById(id);
    }
}