package com.hp.triclops.repository;

import com.hp.triclops.entity.UserVehicleRelatived;
import org.springframework.stereotype.Component;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import java.util.List;

/**
 * Created by Teemol on 2015/9/5.
 */

@Component
public class UserVehicleRelativedRepositoryDAO<T>  {
    @PersistenceContext
    private EntityManager em;

    /**
     * 模糊查询车主车辆关系集
     * @param vin 车辆vin码
     * @param isowner 是否为车主 1：车主
     * @return 车主车辆关系集
     */
    public List getList(String vin,Integer isowner)
    {
        String jpql="FROM UserVehicleRelatived where 1=1";

        vin=(vin==null)?"":vin;
        Integer iflag=(isowner==null)?0:isowner;
        if(!vin.equals("")){
            jpql=jpql+" And vin like :vin";
        }
        if(iflag==1) {
            jpql=jpql+" And iflag =:iflag";
        }

        TypedQuery query = em.createQuery(jpql, UserVehicleRelatived.class);

        if(!vin.equals("")){
            query.setParameter("vin","%"+vin+"%");
        }
        if(iflag==1) {
            query.setParameter("iflag",iflag);
        }

        List items=query.getResultList();

        return items;
    }

    /**
     * 精确查询车主车辆关系集
     * @param vin 车辆vin码
     * @param isowner 是否为车主   1：车主
     * @return 车主车辆关系集
     */
    public List getListAccurate(String vin,Integer isowner)
    {
        String jpql="FROM UserVehicleRelatived where 1=1";

        vin=(vin==null)?"":vin;
        Integer iflag=(isowner==null)?0:isowner;
        if(!vin.equals("")){
            jpql=jpql+" And vin =:vin";
        }
        if(iflag==1) {
            jpql=jpql+" And iflag =:iflag";
        }

        TypedQuery query = em.createQuery(jpql, UserVehicleRelatived.class);

        if(!vin.equals("")){
            query.setParameter("vin",vin);
        }
        if(iflag==1) {
            query.setParameter("iflag",iflag);
        }

        List items=query.getResultList();

        return items;
    }

}


