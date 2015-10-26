package com.hp.triclops.repository;

import com.hp.triclops.entity.TBox;
import com.hp.triclops.entity.Vehicle;
import com.hp.triclops.utils.EscapeStringUtil;
import com.hp.triclops.utils.Page;
import com.hp.triclops.utils.Page2;
import org.springframework.stereotype.Component;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.persistence.TypedQuery;
import java.util.Date;
import java.util.List;

/**
 * Created by luj on 2015/8/26.
 */
@Component
public class TBoxRepositoryDAO {
    @PersistenceContext
    private EntityManager em;

    /**
     * 根据条件查询TBox列表
     * @param id ID
     * @param t_sn tobx码
     * @param vin VIN码
     * @param isActivated 是否被激活
     * @param imei IMEI
     * @param mobile SIM卡
     * @param fuzzy 查询类型标志 0 精确查询 1 模糊查询
     * @return 分页对象
     */
    public Page2<TBox> findTboxByKeys(int id, String t_sn, String vin, int isActivated, String imei, String mobile, int fuzzy, int pageSize,int currentPage){
        String jpql = "select b from TBox b where 1=1";
        if(id != 0){
            jpql += " and b.id = :id";
        }
        if(isActivated != 0){
            jpql += " and b.is_activated = :is_activated";
        }
        if(fuzzy != 0){ //模糊查询
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
        if(id != 0){
            query.setParameter("id",id);
        }
        if(isActivated != 0){
            query.setParameter("is_activated",isActivated);
        }
        if(fuzzy != 0){ //模糊查询
            if(t_sn != null){
                query.setParameter("it_sn",t_sn);
            }
            if(vin != null){
                query.setParameter("vin",vin);
            }
            if(imei != null){
                query.setParameter("imei",imei);
            }
            if(mobile != null){
                query.setParameter("mobile",mobile);
            }
        }else{
            if(t_sn != null){
                query.setParameter("it_sn","%"+t_sn+"%");
            }
            if(vin != null){
                query.setParameter("vin","%"+vin+"%");
            }
            if(imei != null){
                query.setParameter("imei","%"+imei+"%");
            }
            if(mobile != null){
                query.setParameter("mobile","%"+mobile+"%");
            }
        }
        Long count= (long) query.getResultList().size();
        if(pageSize != 0  && currentPage != 0){
           query.setFirstResult((currentPage - 1)* pageSize);
           query.setMaxResults(pageSize);
        }else{
            currentPage = 1;
            pageSize = count.intValue();
        }
        List items=query.getResultList();
        return new Page2(currentPage,pageSize,count,items);
    }

}
