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
     * @param oid 组织机构id
     * @return  封装了数据和页码信息的Page对象
     */
    public Page findVehiclesByKeys(Integer id,String vin,String vendor,String model,Integer t_flag,String displacement,String license_plate,Date start_date,Date end_date,String orderByProperty,String ascOrDesc,Integer pageSize,Integer currentPage,Integer fuzzy,Integer oid,String tboxsn){
        String jpql="select v FROM Vehicle v";
        String jpql_count="";
        id=(id==null)?-1:id;
        vin=(vin==null)?"": EscapeStringUtil.toEscape(vin);
        tboxsn=(tboxsn==null)?"": EscapeStringUtil.toEscape(tboxsn);
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
        if(!tboxsn.equals("")){
            jpql=jpql+" And v.tboxsn like :tboxsn";
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
        if(!tboxsn.equals("")){
            query.setParameter("tboxsn","%"+tboxsn+"%");
            queryCount.setParameter("tboxsn","%"+tboxsn+"%");
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
     * @param oid 组织机构id
     * @return  封装了数据和页码信息的Page对象
     */
    public Page findVehiclesByKeys(Integer id,String vin,String vendor,String model,Integer t_flag,String displacement,String license_plate,Date start_date,Date end_date,String orderByProperty,String ascOrDesc,Integer pageSize,Integer currentPage,Integer oid,String tboxsn){
        String jpql="select v FROM Vehicle v";
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
        if(!tboxsn.equals("")){
            jpql=jpql+" And v.tboxsn = :tboxsn";
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
        if(!tboxsn.equals("")){
            query.setParameter("tboxsn",tboxsn);
            queryCount.setParameter("tboxsn",tboxsn);
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

    /** 调用存储过程查询多个组织车辆
     *
     * 用户查询  支持条件模糊，条件缺省，分页显示
     * @param tboxsn 传入参数为null或""时不作为查询条件
     * @param vendor 传入参数为null或""时不作为查询条件
     * @param t_flag 0表示未验证 1表示已验证 其它数值不作为查询条件
     * @param orderByProperty 排序条件 User类的某一个属性,默认id
     * @param ascOrDesc 排序顺序接受字符串 "ASC"或"DESC"  大小写均可,默认ASC
     * @param pageSize 每页数据条数 必须大于0
     * @param currentPage 获取指定页码数据 必须大于0
     * @param fuzzy 是否模糊查询 1模糊查询 0精确查询
     * @return  封装了数据和页码信息的Page对象
     */
    public Page findVehicleList(Integer uid,String tboxsn,String vendor,Integer fuzzy,String model,Integer t_flag,String displacement,String license_plate,String orderByProperty,String ascOrDesc,Integer pageSize,Integer currentPage,Date start_date,Date end_date){
        tboxsn=(tboxsn==null)?null: EscapeStringUtil.toEscape(tboxsn);
        vendor=(vendor==null)?null: EscapeStringUtil.toEscape(vendor);
            fuzzy = (fuzzy == null) ? 0 : fuzzy;
        model=(model==null)?null: EscapeStringUtil.toEscape(model);
        t_flag = (t_flag == null) ? 0 : t_flag;
        displacement=(displacement==null)?null: EscapeStringUtil.toEscape(displacement);
        license_plate=(license_plate==null)?null: EscapeStringUtil.toEscape(license_plate);
        orderByProperty=(orderByProperty==null)?"id":orderByProperty;
        ascOrDesc=(ascOrDesc==null)?"ASC":ascOrDesc;
        pageSize=(pageSize==null)?10:pageSize;
        pageSize=(pageSize<=0)?10:pageSize;
        currentPage=(currentPage==null)?1:currentPage;
        currentPage=(currentPage<=0)?1:currentPage;
        Query queryCount = em.createNativeQuery("{call pro_findvehicles(?,?,?,?,?,?,?,?,?,?,?,?,?,?)}", Vehicle.class);
        queryCount.setParameter(1,uid);
        queryCount.setParameter(2,tboxsn);
        queryCount.setParameter(3,vendor);
        queryCount.setParameter(4,fuzzy);
        queryCount.setParameter(5,model);
        queryCount.setParameter(6,t_flag);
        queryCount.setParameter(7,displacement);
        queryCount.setParameter(8,license_plate);
        if(fuzzy == 1){
            if(vendor!=null) queryCount.setParameter(3,"%"+vendor+"%");
            if(model!=null)queryCount.setParameter(5,"%"+model+"%");
            if(license_plate!=null)queryCount.setParameter(8,"%"+license_plate+"%");
        }
        queryCount.setParameter(9,-1);
        queryCount.setParameter(10,-1);
        queryCount.setParameter(11,orderByProperty);
        queryCount.setParameter(12,ascOrDesc);
        queryCount.setParameter(13,start_date);
        queryCount.setParameter(14,end_date);
        Long count = (long)queryCount.getResultList().size();
        Integer firstRcord = (currentPage - 1) * pageSize;
        queryCount.setParameter(9,firstRcord);
        queryCount.setParameter(10,pageSize);
        List items=queryCount.getResultList();
        return new Page(currentPage,pageSize,count,items);
    }




}
