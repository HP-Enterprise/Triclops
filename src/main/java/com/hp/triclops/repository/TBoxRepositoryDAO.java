package com.hp.triclops.repository;

import com.hp.triclops.entity.TBox;
import com.hp.triclops.utils.Page2;
import org.springframework.stereotype.Component;
import javax.persistence.*;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import java.util.List;

/**
 * Created by luj on 2015/8/26.
 */
@Component
public class TBoxRepositoryDAO<T> {

    @PersistenceContext
    private EntityManager em;

    /**
     * 根据条件查询TBox列表
     * @param id ID
     * @param t_sn tobx码
     * @param isbind 0 or null 查找全部 1 查找未绑定的
     * @param vin VIN码
     * @param isActivated 是否被激活
     * @param imei IMEI
     * @param mobile SIM卡
     * @param fuzzy 查询类型标志 0 精确查询 1 模糊查询
     * @param pageSize 每页大小
     * @param currentPage 页码
     * @return 分页对象
     */
    public Page2<TBox> findTboxByKeys(int id, String t_sn,int isbind, String vin, int isActivated, String imei, String mobile, int fuzzy, int pageSize,int currentPage){
        String jpql = "select b from TBox b where 1=1";
        if(id != 0){
            jpql += " and b.id = :id";
        }
        if(isActivated != 0){
            jpql += " and b.is_activated = :is_activated";
        }
        if(isbind == 1){
            jpql += " and b.vehicle is null";
        }
        if(fuzzy == 0){ //精确查询
            if(t_sn != null){
                jpql += " and b.t_sn = :t_sn";
            }
            if(vin != null){
                jpql += " and b.vin = :vin";
            }
            if(imei != null){
                jpql += " and b.imei = :imei";
            }
            if(mobile != null){
                jpql += " and b.mobile = :mobile";
            }
        }else{
            if(t_sn != null){
                jpql += " and b.t_sn like :t_sn";
            }
            if(vin != null){
                jpql += " and b.vin like :vin";
            }
            if(imei != null){
                jpql += " and b.imei like :imei";
            }
            if(mobile != null){
                jpql += " and b.mobile like :mobile";
            }
        }

        TypedQuery query = em.createQuery(jpql, TBox.class);
        TypedQuery queryCount = em.createQuery(jpql, TBox.class);//EntityManager id closed
        if(id != 0){
            query.setParameter("id",id);
            queryCount.setParameter("id",id);
        }
        if(isActivated != 0){
            query.setParameter("is_activated",isActivated);
            queryCount.setParameter("is_activated", isActivated);
        }
        if(fuzzy == 0){ //精确查询
            if(t_sn != null){
                query.setParameter("t_sn",t_sn);
                queryCount.setParameter("t_sn",t_sn);
            }
            if(vin != null){
                query.setParameter("vin",vin);
                queryCount.setParameter("vin",vin);
            }
            if(imei != null){
                query.setParameter("imei",imei);
                queryCount.setParameter("imei",imei);
            }
            if(mobile != null){
                query.setParameter("mobile",mobile);
                queryCount.setParameter("mobile",mobile);
            }
        }else{ //模糊查询
            if(t_sn != null){
                query.setParameter("t_sn","%"+t_sn+"%");
                queryCount.setParameter("t_sn","%"+t_sn+"%");
            }
            if(vin != null){
                query.setParameter("vin","%"+vin+"%");
                queryCount.setParameter("vin","%"+vin+"%");
            }
            if(imei != null){
                query.setParameter("imei","%"+imei+"%");
                queryCount.setParameter("imei","%"+imei+"%");
            }
            if(mobile != null){
                query.setParameter("mobile","%"+mobile+"%");
                queryCount.setParameter("mobile","%"+mobile+"%");
            }
        }
        Long count= (long) queryCount.getResultList().size();
        if(pageSize != 0  && currentPage != 0){
           query.setFirstResult((currentPage - 1)* pageSize);
           query.setMaxResults(pageSize);
        }else{
            currentPage = 1;
            pageSize = count.intValue();
        }
        List items = query.getResultList();
        return new Page2(currentPage,pageSize,count,items);
    }

}
