package com.hp.triclops.repository;

import com.hp.triclops.entity.TBox;
import com.hp.triclops.entity.Vehicle;
import com.hp.triclops.utils.EscapeStringUtil;
import com.hp.triclops.utils.Page;
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
    public Page findTboxByKeys(int id, String t_sn, String vin, int isActivated, String imei, String mobile, int fuzzy, int pageSize,int currentPage){
        TypedQuery query = em.createQuery("", TBox.class);
        TypedQuery queryCount = em.createQuery("", TBox.class);
        query.setFirstResult((currentPage - 1)* pageSize);
        query.setMaxResults(pageSize);
        List items=query.getResultList();
        Long count= (long) queryCount.getResultList().size();
        return new Page(currentPage,pageSize,count,items);
    }


}
