package com.hp.triclops.repository;

import com.hp.triclops.entity.GpsData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.persistence.TypedQuery;
import java.util.List;

/**
 * Created by Teemol on 2015/11/11.
 */
@Component
public class GpsDataRepositoryDAO {
    @PersistenceContext
    EntityManager em;

    /**
     * 查询热力分布图
     * @return gps
     */
    public List findLatest(){
        String sql = "select g.* from (select gd.* from t_data_gps gd \n" +
                "where gd.application_id = 34 and latitude > 0 and longitude > 0 and gd.sending_time >= date_sub(now(), INTERVAL 60 SECOND) order by gd.sending_time desc) g\n" +
                "group by g.vin";
        Query query = em.createNativeQuery(sql, GpsData.class);
        List queryList = query.getResultList();
        return queryList;
    }

    /**
     * 查询轨迹图
     * @return gps
     */
    public List findByVinAndTime(String vin){
        String sql = "select gd.* from t_data_gps gd \n" +
                "where gd.vin = :vin and gd.application_id = 34 and latitude > 0 and longitude > 0 and gd.sending_time >= date_sub(now(), INTERVAL 60 MINUTE) order by gd.sending_time desc";
        Query query = em.createNativeQuery(sql, GpsData.class);
        query.setParameter("vin", vin);
        List queryList = query.getResultList();
        return queryList;
    }

}
