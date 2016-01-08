package com.hp.triclops.repository;

import com.hp.triclops.entity.TBox;
import com.hp.triclops.utils.Page2;
import org.hibernate.annotations.SourceType;
import org.springframework.stereotype.Component;
import javax.persistence.*;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by luj on 2015/8/26.
 */
@Component
public class TBoxRepositoryDAO{

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
     * @param total 是否查全部，不分页：0分页查询，1为查询全部
     * @param fuzzy 查询类型标志 0 精确查询 1 模糊查询
     * @param pageSize 每页大小
     * @param currentPage 页码
     * @return 分页对象
     */
    public Page2<TBox> findTboxByKeys(int id, String t_sn,int isbind, String vin, int isActivated, String imei, String mobile,int total, int fuzzy, int pageSize,int currentPage){
        String jpql = "select b from TBox b where 1=1";
        String jpqlSum = "select count(*) as count from t_tbox b where 1=1";
        String tempSql = "";
        if(id != 0){
            tempSql += " and b.id = :id";
        }
        if(isActivated != 0){
            tempSql += " and b.is_activated = :is_activated";
        }
        if(isbind == 1){
            tempSql += " and b.vehicle is null";
        }
        if(fuzzy == 0){ //精确查询
            if(t_sn != null){
                tempSql += " and b.t_sn = :t_sn";
            }
            if(vin != null){
                tempSql += " and b.vin = :vin";
            }
            if(imei != null){
                tempSql += " and b.imei = :imei";
            }
            if(mobile != null){
                tempSql += " and b.mobile = :mobile";
            }
        }else{
            if(t_sn != null){
                tempSql += " and b.t_sn like :t_sn";
            }
            if(vin != null){
                tempSql += " and b.vin like :vin";
            }
            if(imei != null){
                tempSql += " and b.imei like :imei";
            }
            if(mobile != null){
                tempSql += " and b.mobile like :mobile";
            }
        }

        TypedQuery query = em.createQuery(jpql.concat(tempSql), TBox.class);
        Query queryCount = em.createNativeQuery(jpqlSum.concat(tempSql));//EntityManager id closed
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
        List<TBox> tBoxList = new ArrayList<TBox>();
        for(Object obj : items){
            TBox tBox = (TBox)obj;
            tBoxList.add(tBox);
        }

        Page2<TBox> page2 = new Page2<TBox>();
        page2.setCurrentPage(currentPage);
        page2.setPageSize(pageSize);
        page2.setRecordCount(Long.parseLong(count));
        page2.setItems(tBoxList);

        return page2;
    }

}
