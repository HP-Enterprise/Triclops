package com.hp.triclops.repository;

import com.hp.triclops.entity.Sysdict;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Teemol on 2015/10/22.
 */

@Component
public class SysdictRepositoryDAO {

    @Autowired
    private SysdictRepository sysdictRepository;

    @PersistenceContext
    private EntityManager em;

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
        String jpql="select s from Sysdict s";
        String jpql_count="";
        dictid=(dictid==null)?-1:dictid;
        type=(type==null)?-1:type;
        jpql=jpql+" where 1=1";
        if(dictid!=null){
            jpql=jpql+" and s.dictid=:dictid";
        }
        if(type!=null){
            jpql=jpql+" and s.type=:type";
        }
        jpql_count=jpql;
        TypedQuery query=em.createQuery(jpql,Sysdict.class);
        TypedQuery queryCount=em.createQuery(jpql_count,Sysdict.class);
        if (dictid!=null && dictid>0){
            query.setParameter("dictid",dictid);
            queryCount.setParameter("dictid",dictid);
        }
        if (type!=null && type>0){
            query.setParameter("type",type);
            queryCount.setParameter("type",type);
        }
        List items=query.getResultList();
        List<Sysdict> sysdicts=new ArrayList<>();
        for (int i = 0; i < items.size(); i++) {
            sysdicts.add(i,(Sysdict)items.get(i));
        }
        return sysdicts;
    }
}
