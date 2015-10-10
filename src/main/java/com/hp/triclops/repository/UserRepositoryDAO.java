package com.hp.triclops.repository;

import com.hp.triclops.entity.User;
import com.hp.triclops.utils.EscapeStringUtil;
import com.hp.triclops.utils.Page;
import org.hibernate.cfg.NotYetImplementedException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.persistence.TypedQuery;
import java.util.List;


@Component
@SuppressWarnings("unchecked")
public class UserRepositoryDAO<T>  {

    @Autowired
    UserVehicleRelativedRepositoryDAO userVehicleRelativedRepositoryDAO;

    @PersistenceContext
    private EntityManager em;

    /**模糊查询
     *
     * 用户查询  支持条件模糊，条件缺省，分页显示
     * @param id 传入参数为null或""时不作为查询条件
     * @param name 传入参数为null或""时不作为查询条件
     * @param gender 传入参数为null或""时不作为查询条件
     * @param nick 传入参数为null或""时不作为查询条件
     * @param phone 传入参数为null或""时不作为查询条件
     * @param isVerified 0表示未验证 1表示已验证 其它数值不作为查询条件
     * @param orderByProperty 排序条件 User类的某一个属性,默认id
     * @param ascOrDesc 排序顺序接受字符串 "ASC"或"DESC"  大小写均可,默认ASC
     * @param pageSize 每页数据条数 必须大于0
     * @param currentPage 获取指定页码数据 必须大于0
     * @param vin vin码
     * @param isowner 是否为车主
     * @param fuzzy 1:模糊查询
     * @param oid 组织id
     * @return  封装了数据和页码信息的Page对象
     */
    public Page findUserByKeys(Integer id,String name,Integer gender,String nick,String phone,Integer isVerified,String orderByProperty,String ascOrDesc,Integer pageSize,Integer currentPage,String vin,Integer isowner,Integer fuzzy,Integer oid){
        String jpql="select u from User u";
        String jpql_count="";
        id=(id==null)?-1:id;
        name=(name==null)?"": EscapeStringUtil.toEscape(name);
        gender=(gender==null)?-1:gender;
        nick=(nick==null)?"": EscapeStringUtil.toEscape(nick);
        phone=(phone==null)?"":phone;
        isVerified=(isVerified==null)?-1:isVerified;
        orderByProperty=(orderByProperty==null)?"id":orderByProperty;
        ascOrDesc=(ascOrDesc==null)?"ASC":ascOrDesc;
        pageSize=(pageSize==null)?10:pageSize;
        pageSize=(pageSize<=0)?10:pageSize;
        currentPage=(currentPage==null)?1:currentPage;
        currentPage=(currentPage<=0)?1:currentPage;
        isowner=(isowner==null)?-1:isowner;
        oid=(oid==null)?-1:oid;
        vin=(vin==null)?"":vin;
        if(!vin.equals("") || oid >= 0 || isowner >=0 ) {
            if (oid >= 0) {
                jpql = jpql + " join u.organizationSet O";
            }
            if(isowner == 0 || isowner == 1)
            {
                if(!vin.equals(""))
                {
                    jpql=jpql+" join u.userSet u1 join u.vehicleSet v";
                }
                else
                {
                    jpql = jpql + " join u.userSet u1";
                }
            }
            jpql=jpql+" where 1=1";

            if (oid >= 0) {
                jpql = jpql + " And O.id =:oid";
            }
            if(isowner == 0 || isowner == 1)
            {
                jpql = jpql + " And u1.iflag=:isowner";
            }
            if(!vin.equals(""))
            {
                jpql = jpql + " And v.vin like :vin";
            }
        }else{
            jpql=jpql+" where 1=1";
        }

        if(id>=0){
            jpql=jpql+" And u.id =:id";
        }
        if(!name.equals("")){
            jpql=jpql+" And u.name like :name";
        }
        if(gender==0||gender==1){
            jpql=jpql+" And u.gender =:gender";
        }
        if(!nick.equals("")){
            jpql=jpql+" And u.nick like :nick";
        }
        if(!phone.equals("")){
            jpql=jpql+" And u.phone like :phone";
        }
        if(isVerified==0||isVerified==1){
            jpql=jpql+" And u.isVerified =:isVerified";
        }

        jpql=jpql+" Order by u."+orderByProperty+" "+ascOrDesc;
        jpql_count=jpql;
        System.out.println("111                 "+jpql_count);
        TypedQuery query = em.createQuery(jpql, User.class);
        TypedQuery queryCount = em.createQuery(jpql_count, User.class);

        if(oid != null && oid>=0){
            query.setParameter("oid",oid);
            queryCount.setParameter("oid",oid);
        }
        if(id>=0){
            query.setParameter("id",id);
            queryCount.setParameter("id",id);
        }
        if(!name.equals("")){
            query.setParameter("name","%"+name+"%");
            queryCount.setParameter("name","%"+name+"%");
        }
        if(gender==0||gender==1){
            query.setParameter("gender",gender);
            queryCount.setParameter("gender",gender);
        }
        if(!nick.equals("")){
            query.setParameter("nick","%"+nick+"%");
            queryCount.setParameter("nick","%"+nick+"%");
        }
        if(!phone.equals("")){
            query.setParameter("phone","%"+phone+"%");
            queryCount.setParameter("phone","%"+phone+"%");
        }
        if(isVerified==0||isVerified==1){
            query.setParameter("isVerified",isVerified);
            queryCount.setParameter("isVerified",isVerified);
        }
        if (isowner==0||isowner==1){
            query.setParameter("isowner",isowner);
            queryCount.setParameter("isowner",isowner);
        }
        if (!vin.equals("")){
            query.setParameter("vin","%"+vin+"%");
            queryCount.setParameter("vin","%"+vin+"%");
        }
        query.setFirstResult((currentPage - 1)* pageSize);
        query.setMaxResults(pageSize);
        List items=query.getResultList();
        Long count= (long) queryCount.getResultList().size();
        return new Page(currentPage,pageSize,count,items);
    }

    /** 精确查询
     *
     * 用户查询  支持条件模糊，条件缺省，分页显示
     * @param id 传入参数为null或""时不作为查询条件
     * @param name 传入参数为null或""时不作为查询条件
     * @param gender 传入参数为null或""时不作为查询条件
     * @param nick 传入参数为null或""时不作为查询条件
     * @param phone 传入参数为null或""时不作为查询条件
     * @param isVerified 0表示未验证 1表示已验证 其它数值不作为查询条件
     * @param orderByProperty 排序条件 User类的某一个属性,默认id
     * @param ascOrDesc 排序顺序接受字符串 "ASC"或"DESC"  大小写均可,默认ASC
     * @param pageSize 每页数据条数 必须大于0
     * @param currentPage 获取指定页码数据 必须大于0
     * @param vin vin码
     * @param isowner 是否为车主
     * @param oid 组织id
     * @return  封装了数据和页码信息的Page对象
     */
    public Page findUserByKeys(Integer id,String name,Integer gender,String nick,String phone,Integer isVerified,String orderByProperty,String ascOrDesc,Integer pageSize,Integer currentPage,String vin,Integer isowner,Integer oid){
        String jpql="select u from User u";
        String jpql_count="";
        id=(id==null)?-1:id;
        name=(name==null)?"": EscapeStringUtil.toEscape(name);
        gender=(gender==null)?-1:gender;
        nick=(nick==null)?"": EscapeStringUtil.toEscape(nick);
        phone=(phone==null)?"":phone;
        isVerified=(isVerified==null)?-1:isVerified;
        orderByProperty=(orderByProperty==null)?"id":orderByProperty;
        ascOrDesc=(ascOrDesc==null)?"ASC":ascOrDesc;
        pageSize=(pageSize==null)?10:pageSize;
        pageSize=(pageSize<=0)?10:pageSize;
        currentPage=(currentPage==null)?1:currentPage;
        currentPage=(currentPage<=0)?1:currentPage;
        isowner=(isowner==null)?-1:isowner;
        oid=(oid==null)?-1:oid;
        vin=(vin==null)?"":vin;
        if (!vin.equals("") || oid >= 0 || isowner >= 0){
            if (oid >= 0) {
                jpql = jpql + " join u.organizationSet O";
            }
            if(isowner == 0 || isowner == 1)
            {
                if(!vin.equals(""))
                {
                    jpql=jpql+" join u.userSet u1 join u.vehicleSet v";
                }
                else
                {
                    jpql = jpql + " join u.userSet u1";
                }
            }
            jpql=jpql+" where 1=1";

            if (oid >= 0) {
                jpql = jpql + " And O.id =:oid";
            }
            if(isowner == 0 || isowner == 1)
            {
                jpql = jpql + " And u1.iflag=:isowner";
            }
            if(!vin.equals(""))
            {
                jpql = jpql + " And v.vin =:vin";
            }
        }else {
                jpql = jpql+ " where 1=1";
              }
        if (id>=0){
            jpql=jpql+" And u.id =:id";
        }
        if (!name.equals("")){
            jpql=jpql+" And u.name =:name";
        }
        if (gender==0||gender==1){
            jpql=jpql+" And u.gender =:gender";
        }
        if (!nick.equals("")){
            jpql=jpql+" And  u.nick=:nick";
        }
        if (!phone.equals("")){
            jpql=jpql+" And u.phone =:phone";
        }
        if (isVerified==0||isVerified==1){
            jpql=jpql+" And u.isVerified =:isVerified";
        }

        jpql=jpql+" Order by u."+orderByProperty+" "+ascOrDesc;
        jpql_count=jpql;
        TypedQuery query=em.createQuery(jpql,User.class);
        TypedQuery queryCount=em.createQuery(jpql_count,User.class);

        if(oid != null && oid>=0){
            query.setParameter("oid",oid);
            queryCount.setParameter("oid",oid);
        }
        if (id>=0){
            query.setParameter("id",id);
            queryCount.setParameter("id",id);
        }
        if (!name.equals("")){
            query.setParameter("name",name);
            queryCount.setParameter("name",name);
        }
        if (gender==0||gender==1){
            query.setParameter("gender",gender);
            queryCount.setParameter("gender",gender);
        }
        if (!nick.equals("")){
            query.setParameter("nick",nick);
            queryCount.setParameter("nick",nick);
        }
        if (!phone.equals("")){
            query.setParameter("phone",phone);
            queryCount.setParameter("phone",phone);
        }
        if (isVerified==0||isVerified==1){
            query.setParameter("isVerified",isVerified);
            queryCount.setParameter("isVerified",isVerified);
        }
        if (isowner==0||isowner==1){
            query.setParameter("isowner",isowner);
            queryCount.setParameter("isowner",isowner);
        }
        if (!vin.equals("")){
            query.setParameter("vin",vin);
            queryCount.setParameter("vin",vin);
        }
        query.setFirstResult((currentPage - 1)* pageSize);
        query.setMaxResults(pageSize);
        List items=query.getResultList();
        Long count= (long) queryCount.getResultList().size();

        return new Page(currentPage,pageSize,count,items);
    }

    /** 调用存储过程查询多个组织用户
     *
     * 用户查询  支持条件模糊，条件缺省，分页显示
     * @param gender 传入参数为null或""时不作为查询条件
     * @param nick 传入参数为null或""时不作为查询条件
     * @param isVerified 0表示未验证 1表示已验证 其它数值不作为查询条件
     * @param orderByProperty 排序条件 User类的某一个属性,默认id
     * @param ascOrDesc 排序顺序接受字符串 "ASC"或"DESC"  大小写均可,默认ASC
     * @param pageSize 每页数据条数 必须大于0
     * @param currentPage 获取指定页码数据 必须大于0
     * @param vid 车辆id
     * @param isowner 是否为车主
     * @param oid 组织id
     * @return  封装了数据和页码信息的Page对象
     */
    public Page findUserList(Integer uid,Integer gender,String nick,Integer isVerified,String orderByProperty,String ascOrDesc,Integer pageSize,Integer currentPage,Integer vid,Integer isowner,Integer oid,Integer fuzzy){
        throw new NotYetImplementedException("There is a SQL INJECT problem in e3f02c885d8548e99c669fffcbd7462e3aaa0fe4");
    }

}