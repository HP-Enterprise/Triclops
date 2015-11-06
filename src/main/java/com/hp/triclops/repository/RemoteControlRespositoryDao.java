package com.hp.triclops.repository;

import com.hp.triclops.entity.RemoteControl;
import com.hp.triclops.utils.EscapeStringUtil;
import com.hp.triclops.utils.Page;
import org.springframework.stereotype.Component;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import java.util.List;

/**
 * Created by HYu on 2015/11/6.
 */
@Component
public class RemoteControlRespositoryDao {

    @PersistenceContext
    private EntityManager em;

    /**
     * @param vin vin
     * @param orderByProperty 排序条件
     * @param ascOrDesc 升降序
     * @param pageSize 页面size
     * @param currentPage   当前页码
     * @return  page 封装好的分页远程控制数据
     */
    public Page findRemoteControlByVin(String vin,String orderByProperty,String ascOrDesc,Integer pageSize,Integer currentPage){
        String jpql="select rc FROM RemoteControl rc where 1=1 ";
        String jpql_count="";
        vin=(vin==null)?"": EscapeStringUtil.toEscape(vin);
        pageSize=(pageSize==null)?10:pageSize;
        pageSize=(pageSize<=0)?10:pageSize;
        currentPage=(currentPage==null)?1:currentPage;
        currentPage=(currentPage<=0)?1:currentPage;
        orderByProperty=(orderByProperty==null)?"vin":orderByProperty;
        ascOrDesc=(ascOrDesc==null)?"ASC":ascOrDesc;

        if(!vin.equals("")){
            jpql=jpql+" And rc.vin = :vin";
        }

        jpql=jpql+" Order by rc."+orderByProperty+" "+ascOrDesc;
        jpql_count=jpql;

        TypedQuery query = em.createQuery(jpql, RemoteControl.class);
        TypedQuery queryCount = em.createQuery(jpql_count, RemoteControl.class);

        if(!vin.equals("")){
            query.setParameter("vin",vin);
            queryCount.setParameter("vin",vin);
        }

        query.setFirstResult((currentPage - 1)* pageSize);
        query.setMaxResults(pageSize);
        List items=query.getResultList();
        Long count= (long) queryCount.getResultList().size();
        return new Page(currentPage,pageSize,count,items);
    }

}
