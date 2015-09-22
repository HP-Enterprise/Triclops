package com.hp.triclops.repository;

import com.hp.triclops.entity.Vehicle;
import com.hp.triclops.utils.EscapeStringUtil;
import com.hp.triclops.utils.Page;
import org.springframework.stereotype.Component;

import javax.persistence.*;
import java.util.Date;
import java.util.List;

/**
 * Created by luj on 2015/8/26.
 */
@Component
public class VehicleRepositoryDAO {
    @PersistenceContext
    private EntityManager em;


    /**
     * 模糊查询
     * @param id 传入参数为null或""时不作为查询条件
     * @param vin 传入参数为null或""时不作为查询条件
     * @param vendor 传入参数为null或""时不作为查询条件
     * @param model 传入参数为null或""时不作为查询条件
     * @param t_flag 0 自然吸气 1 涡轮增压 其它数值不作为查询条件
     * @param displacement 传入参数为null或""时不作为查询条件
     * @param license_plate 传入参数为null或""时不作为查询条件
     * @param start_date 出厂日期开始范围，传入参数为null不作为查询条件
     * @param end_date 出厂日期结束范围，传入参数为null不作为查询条件
     * @param orderByProperty 排序条件 Vehicle类的某一个属性,默认vin
     * @param ascOrDesc 排序顺序接受字符串 "ASC"或"DESC"  大小写均可,默认ASC
     * @param pageSize 每页数据条数 必须大于0
     * @param currentPage 获取指定页码数据 必须大于0
     * @param fuzzy 是否模糊查询
     * @return  封装了数据和页码信息的Page对象
     */
    public Page findVehiclesByKeys(Integer id,String vin,String vendor,String model,Integer t_flag,String displacement,String license_plate,Date start_date,Date end_date,String orderByProperty,String ascOrDesc,Integer pageSize,Integer currentPage,Integer fuzzy,Integer oid){
        String jpql="FROM Vehicle v";
        String jpql_count="";
        id=(id==null)?-1:id;
        vin=(vin==null)?"": EscapeStringUtil.toEscape(vin);
        vendor=(vendor==null)?"":vendor;
        model=(model==null)?"":model;
        displacement=(displacement==null)?"":displacement;
        license_plate=(license_plate==null)?"":EscapeStringUtil.toEscape(license_plate);
        orderByProperty=(orderByProperty==null)?"vin":orderByProperty;
        ascOrDesc=(ascOrDesc==null)?"ASC":ascOrDesc;
        t_flag=(t_flag==null)?-1:t_flag;
        pageSize=(pageSize==null)?10:pageSize;
        pageSize=(pageSize<=0)?10:pageSize;
        currentPage=(currentPage==null)?1:currentPage;
        currentPage=(currentPage<=0)?1:currentPage;

        if(oid != null && oid>=0){
            jpql = jpql+" join v.organizationSet O where O.id =:oid";
        }else{
            jpql = jpql+ " where 1=1";
        }
        if(id>=0){
            jpql=jpql+" And v.id =:id";
        }
        if(!vin.equals("")){
         jpql=jpql+" And v.vin like :vin";
        }
        if(!vendor.equals("")){
            jpql=jpql+" And v.vendor like :vendor";
        }
        if(!model.equals("")){
            jpql=jpql+" And v.model like :model";
        }
        if(!displacement.equals("")){
            jpql=jpql+" And v.displacement like :displacement";
        }
        if(!license_plate.equals("")){
            jpql=jpql+" And v.license_plate like :license_plate";
        }
        if(t_flag==0||t_flag==1){
            jpql=jpql+" And v.t_flag =:t_flag";
        }
        if(start_date!=null){
            jpql=jpql+" And v.product_date >= :start_date";
        }
        if(end_date!=null){
            jpql=jpql+" And v.product_date <= :end_date";
        }
        jpql=jpql+" Order by v."+orderByProperty+" "+ascOrDesc;
        jpql_count=jpql;

        TypedQuery query = em.createQuery(jpql, Vehicle.class);
        TypedQuery queryCount = em.createQuery(jpql_count, Vehicle.class);
        if(oid != null && oid>=0){
            query.setParameter("oid",oid);
            queryCount.setParameter("oid",oid);
        }
        if(id>=0){
            query.setParameter("id",id);
            queryCount.setParameter("id",id);
        }
        if(!vin.equals("")){
            query.setParameter("vin","%"+vin+"%");
            queryCount.setParameter("vin","%"+vin+"%");
        }
        if(!vendor.equals("")){
            query.setParameter("vendor","%"+vendor+"%");
            queryCount.setParameter("vendor","%"+vendor+"%");
        }
        if(!model.equals("")){
            query.setParameter("model","%"+model+"%");
            queryCount.setParameter("model","%"+model+"%");
        }
        if(!displacement.equals("")){
            query.setParameter("displacement","%"+displacement+"%");
            queryCount.setParameter("displacement","%"+displacement+"%");
        }
        if(!license_plate.equals("")){
            query.setParameter("license_plate","%"+license_plate+"%");
            queryCount.setParameter("license_plate","%"+license_plate+"%");
        }
        if(t_flag==0||t_flag==1){
            query.setParameter("t_flag",t_flag);
            queryCount.setParameter("t_flag",t_flag);
        }
        if(start_date!=null){
            query.setParameter("start_date",start_date);
            queryCount.setParameter("start_date",start_date);
        }
        if(end_date!=null){
            query.setParameter("end_date",end_date);
            queryCount.setParameter("end_date",end_date);
        }
        query.setFirstResult((currentPage - 1)* pageSize);
        query.setMaxResults(pageSize);
        List items=query.getResultList();
        Long count= (long) queryCount.getResultList().size();
        return new Page(currentPage,pageSize,count,items);
    }
    /**
     * 精确查询
     * @param id 传入参数为null或""时不作为查询条件
     * @param vin 传入参数为null或""时不作为查询条件
     * @param vendor 传入参数为null或""时不作为查询条件
     * @param model 传入参数为null或""时不作为查询条件
     * @param t_flag 0 自然吸气 1 涡轮增压 其它数值不作为查询条件
     * @param displacement 传入参数为null或""时不作为查询条件
     * @param license_plate 传入参数为null或""时不作为查询条件
     * @param start_date 出厂日期开始范围，传入参数为null不作为查询条件
     * @param end_date 出厂日期结束范围，传入参数为null不作为查询条件
     * @param orderByProperty 排序条件 Vehicle类的某一个属性,默认vin
     * @param ascOrDesc 排序顺序接受字符串 "ASC"或"DESC"  大小写均可,默认ASC
     * @param pageSize 每页数据条数 必须大于0
     * @param currentPage 获取指定页码数据 必须大于0
     * @return  封装了数据和页码信息的Page对象
     */
    public Page findVehiclesByKeys(Integer id,String vin,String vendor,String model,Integer t_flag,String displacement,String license_plate,Date start_date,Date end_date,String orderByProperty,String ascOrDesc,Integer pageSize,Integer currentPage,Integer oid){
        String jpql="FROM Vehicle v";
        String jpql_count="";
        id=(id==null)?-1:id;
        vin=(vin==null)?"":EscapeStringUtil.toEscape(vin);
        vendor=(vendor==null)?"":vendor;
        model=(model==null)?"":model;
        displacement=(displacement==null)?"":displacement;
        license_plate=(license_plate==null)?"":EscapeStringUtil.toEscape(license_plate);
        orderByProperty=(orderByProperty==null)?"vin":orderByProperty;
        ascOrDesc=(ascOrDesc==null)?"ASC":ascOrDesc;
        t_flag=(t_flag==null)?-1:t_flag;
        pageSize=(pageSize==null)?10:pageSize;
        pageSize=(pageSize<=0)?10:pageSize;
        currentPage=(currentPage==null)?1:currentPage;
        currentPage=(currentPage<=0)?1:currentPage;
        if(oid != null && oid>=0){
            jpql = jpql+" join v.organizationSet O where O.id =:oid";
        }else{
            jpql = jpql+ " where 1=1";
        }
        if(id>=0){
            jpql=jpql+" And v.id =:id";
        }
        if(!vin.equals("")){
            jpql=jpql+" And v.vin = :vin";
        }
        if(!vendor.equals("")){
            jpql=jpql+" And v.vendor = :vendor";
        }
        if(!model.equals("")){
            jpql=jpql+" And v.model = :model";
        }
        if(!displacement.equals("")){
            jpql=jpql+" And v.displacement = :displacement";
        }
        if(!license_plate.equals("")){
            jpql=jpql+" And v.license_plate = :license_plate";
        }
        if(t_flag==0||t_flag==1){
            jpql=jpql+" And v.t_flag =:t_flag";
        }
        if(start_date!=null){
            jpql=jpql+" And v.product_date >= :start_date";
        }
        if(end_date!=null){
            jpql=jpql+" And v.product_date <= :end_date";
        }
        jpql=jpql+" Order by v."+orderByProperty+" "+ascOrDesc;
        jpql_count=jpql;

        TypedQuery query = em.createQuery(jpql, Vehicle.class);
        TypedQuery queryCount = em.createQuery(jpql_count, Vehicle.class);

        if(oid != null && oid>=0){
            query.setParameter("oid",oid);
            queryCount.setParameter("oid",oid);
        }
        if(id>=0){
            query.setParameter("id",id);
            queryCount.setParameter("id",id);
        }
        if(!vin.equals("")){
            query.setParameter("vin",vin);
            queryCount.setParameter("vin",vin);
        }
        if(!vendor.equals("")){
            query.setParameter("vendor",vendor);
            queryCount.setParameter("vendor",vendor);
        }
        if(!model.equals("")){
            query.setParameter("model",model);
            queryCount.setParameter("model",model);
        }
        if(!displacement.equals("")){
            query.setParameter("displacement",displacement);
            queryCount.setParameter("displacement",displacement);
        }
        if(!license_plate.equals("")){
            query.setParameter("license_plate",license_plate);
            queryCount.setParameter("license_plate",license_plate);
        }
        if(t_flag==0||t_flag==1){
            query.setParameter("t_flag",t_flag);
            queryCount.setParameter("t_flag",t_flag);
        }
        if(start_date!=null){
            query.setParameter("start_date",start_date);
            queryCount.setParameter("start_date",start_date);
        }
        if(end_date!=null){
            query.setParameter("end_date",end_date);
            queryCount.setParameter("end_date",end_date);
        }
        query.setFirstResult((currentPage - 1)* pageSize);
        query.setMaxResults(pageSize);
        List items=query.getResultList();
        Long count= (long) queryCount.getResultList().size();
        return new Page(currentPage,pageSize,count,items);
    }

}
