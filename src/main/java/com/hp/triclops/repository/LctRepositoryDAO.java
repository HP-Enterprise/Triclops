package com.hp.triclops.repository;

import com.hp.triclops.entity.Lct;
import com.hp.triclops.utils.Page2;
import org.springframework.stereotype.Component;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.persistence.TypedQuery;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by luj on 2015/8/26.
 */
@Component
public class LctRepositoryDAO {

    @PersistenceContext
    private EntityManager em;

    /**
     * 根据条件查询Lct列表
     * @param id ID
     * @param model tobx码
     * @param isbind 0 or null 查找全部 1 查找未绑定的
     * @param imei IMEI
     * @param brandName 品牌名称
     * @param total 是否查全部，不分页：0分页查询，1为查询全部
     * @param orderByProperty 排序条件 Lct类的某一个属性,默认id
     * @param ascOrDesc 排序顺序接受字符串 "ASC"或"DESC"  大小写均可,默认ASC
     * @param fuzzy 查询类型标志 0 精确查询 1 模糊查询
     * @param pageSize 每页大小
     * @param currentPage 页码
     * @return 分页对象
     */
    public Page2<Lct> findLctByKeys(long id, String model,int isbind, String imei, String brandName,int total,String orderByProperty,String ascOrDesc, int fuzzy, int pageSize,int currentPage){
        String jpql = "select b from Lct b where 1=1";
        String jpqlSum = "select count(*) as count from t_lct b where 1=1";
        if(id != 0){
            jpql += " and b.id = :id";
            jpqlSum += " and b.id = :id";
        }
      /*
        if(isbind == 1){
            jpql += " and b.vehicle is null";
            jpqlSum += " and b.vid is null";
        }*/
        if(fuzzy == 0){ //精确查询
            if(model != null){
                jpql += " and b.model = :model";
                jpqlSum += " and b.model = :model";
            }

            if(imei != null){
                jpql += " and b.imei = :imei";
                jpqlSum += " and b.imei = :imei";
            }
            if(brandName != null){
                jpql += " and b.brandName = :brandName";
                jpqlSum += " and b.brandName = :brandName";
            }
        }else{
            if(model != null){
                jpql += " and b.model like :model";
                jpqlSum += " and b.model like :model";
            }
            if(imei != null){
                jpql += " and b.imei like :imei";
                jpqlSum += " and b.imei like :imei";
            }
            if(brandName != null){
                jpql += " and b.brandName like :brandName";
                jpqlSum += " and b.brandName like :brandName";
            }
        }

        jpql=jpql+" Order by b."+orderByProperty+" "+ascOrDesc;
        TypedQuery query = em.createQuery(jpql, Lct.class);
        Query queryCount = em.createNativeQuery(jpqlSum);//EntityManager id closed
        if(id != 0){
            query.setParameter("id",id);
            queryCount.setParameter("id",id);
        }
       /* if(isActivated != 0){
            query.setParameter("is_activated",isActivated);
            queryCount.setParameter("is_activated", isActivated);
        }*/
        if(fuzzy == 0){ //精确查询
            if(model != null){
                query.setParameter("model",model);
                queryCount.setParameter("model",model);
            }
            if(imei != null){
                query.setParameter("imei",imei);
                queryCount.setParameter("imei",imei);
            }
            if(brandName != null){
                query.setParameter("brandName",brandName);
                queryCount.setParameter("brandName",brandName);
            }
        }else{ //模糊查询
            if(model != null){
                query.setParameter("model","%"+model+"%");
                queryCount.setParameter("model","%"+model+"%");
            }
            if(imei != null){
                query.setParameter("imei","%"+imei+"%");
                queryCount.setParameter("imei","%"+imei+"%");
            }
            if(brandName != null){
                query.setParameter("brandName","%"+brandName+"%");
                queryCount.setParameter("brandName","%"+brandName+"%");
            }
        }
        List items_sum =  queryCount.getResultList();
        String count = items_sum.get(0).toString();
        if(total == 0){
            if(pageSize != 0  && currentPage != 0){
                query.setFirstResult((currentPage - 1)* pageSize);
                query.setMaxResults(pageSize);
            }else{
                currentPage = 1;
                pageSize = Integer.valueOf(count);
            }
        }
        List items = query.getResultList();
        List<Lct> lctList = new ArrayList<Lct>();
        for(Object obj : items){
            Lct lct = (Lct)obj;
            lctList.add(lct);
        }

        Page2<Lct> page2 = new Page2<Lct>();
        page2.setCurrentPage(currentPage);
        page2.setPageSize(pageSize);
        page2.setRecordCount(Long.parseLong(count));
        page2.setItems(lctList);

        return page2;
    }

}
