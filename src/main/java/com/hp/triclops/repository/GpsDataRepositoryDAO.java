package com.hp.triclops.repository;

import com.hp.triclops.entity.GpsData;
import org.springframework.stereotype.Component;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
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
        String sql = "SELECT A.*, v.* FROM t_data_gps A JOIN" +
                " (SELECT vin, max(sending_time) lastTime FROM t_data_gps g" +
                " where g.application_id = 34 and g.latitude > 0 and g.longitude > 0 and g.sending_time >= date_sub(now(), INTERVAL 60 SECOND) GROUP BY vin) B ON A.vin = B.vin and A.sending_time = B.lastTime" +
                " left join t_vehicle v on v.vin = A.vin";
        Query query = em.createNativeQuery(sql, GpsData.class);
        List queryList = query.getResultList();
        return queryList;
    }

    /**
     * 查询轨迹图
     * @return gps
     */
    public List findByVinAndTime(String vin){
        String sql = "select gd.*, v.* from t_data_gps gd \n" +
                "left join t_vehicle v on v.vin = gd.vin \n" +
                "where gd.vin = :vin and gd.application_id = 34 and latitude > 0 and longitude > 0 and gd.sending_time >= date_sub(now(), INTERVAL 60 MINUTE) " +
                "order by gd.sending_time desc";
        Query query = em.createNativeQuery(sql, GpsData.class);
        query.setParameter("vin", vin);
        List queryList = query.getResultList();
        return queryList;
    }

}
