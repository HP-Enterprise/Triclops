package com.hp.triclops.repository;

import com.hp.triclops.entity.Sysdict;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

/**
 * Created by Teemol on 2015/10/22.
 */
public class SysdictRepositoryDAO {

    @Autowired
    private SysdictRepository sysdictRepository;

    /**
     * 根据dictid查询字典信息
     * @param dictid  字典ID
     * @return 字典信息
     */
    public Sysdict findBuyId(Integer dictid)
    {
        return sysdictRepository.findByDictid(dictid);
    }


    /**
     * 查询字典
     * @param dictid 字典ID   传入参数为null或""时不作为查询条件
     * @param type 字典类型  1：组织
     * @return 查询到的字典列表
     */
    public List<Sysdict> findTypes(Integer dictid,Integer type)
    {
        return null;
    }
}
