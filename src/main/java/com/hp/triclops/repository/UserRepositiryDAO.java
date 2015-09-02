package com.hp.triclops.repository;

import com.hp.triclops.entity.User;
import com.hp.triclops.utils.Page;
import org.springframework.stereotype.Component;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import java.util.List;


@Component
public class UserRepositiryDAO<T>  {
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
     * @param fuzzy 是否模糊查询
     * @return  封装了数据和页码信息的Page对象
     */
    public Page findUserByKeys(Integer id,String name,Integer gender,String nick,String phone,Integer isVerified,String orderByProperty,String ascOrDesc,Integer pageSize,Integer currentPage,Integer fuzzy){
        String jpql="FROM User u where 1=1";
        String jpql_count="";
        id=(id==null)?-1:id;
        name=(name==null)?"":name;
        gender=(gender==null)?-1:gender;
        nick=(nick==null)?"":nick;
        phone=(phone==null)?"":phone;
        isVerified=(isVerified==null)?-1:isVerified;
        orderByProperty=(orderByProperty==null)?"id":orderByProperty;
        ascOrDesc=(ascOrDesc==null)?"ASC":ascOrDesc;
        pageSize=(pageSize==null)?10:pageSize;
        pageSize=(pageSize<=0)?10:pageSize;
        currentPage=(currentPage==null)?1:currentPage;
        currentPage=(currentPage<=0)?1:currentPage;
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
        System.out.println("jpql:"+jpql);
        TypedQuery query = em.createQuery(jpql, User.class);
        TypedQuery queryCount = em.createQuery(jpql_count, User.class);
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
     * @return  封装了数据和页码信息的Page对象
     */
    public Page findUserByKeys(Integer id,String name,Integer gender,String nick,String phone,Integer isVerified,String orderByProperty,String ascOrDesc,Integer pageSize,Integer currentPage){
        String jpql="FROM User u where 1=1";
        String jpql_count="";
        id=(id==null)?-1:id;
        name=(name==null)?"":name;
        gender=(gender==null)?-1:gender;
        nick=(nick==null)?"":nick;
        phone=(phone==null)?"":phone;
        isVerified=(isVerified==null)?-1:isVerified;
        orderByProperty=(orderByProperty==null)?"id":orderByProperty;
        ascOrDesc=(ascOrDesc==null)?"ASC":ascOrDesc;
        pageSize=(pageSize==null)?10:pageSize;
        pageSize=(pageSize<=0)?10:pageSize;
        currentPage=(currentPage==null)?1:currentPage;
        currentPage=(currentPage<=0)?1:currentPage;
        if (id>=0){
            jpql=jpql+" And u.id = :id";
        }
        if (!name.equals("")){
            jpql=jpql+" And u.name = :name";
        }
        if (gender==0||gender==1){
            jpql=jpql+" And u.gender = :gender";
        }
        if (!nick.equals("")){
            jpql=jpql+" And  u.nick= :nick";
        }
        if (!phone.equals("")){
            jpql=jpql+" And u.phone = :phone";
        }
        if (isVerified==0||isVerified==1){
            jpql=jpql+" And u.isVerified = :isVerified";
        }
        jpql=jpql+" Order by u."+orderByProperty+" "+ascOrDesc;
        jpql_count=jpql;
        TypedQuery query=em.createQuery(jpql,User.class);
        TypedQuery queryCount=em.createQuery(jpql_count,User.class);
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
        query.setFirstResult((currentPage - 1)* pageSize);
        query.setMaxResults(pageSize);
        List items=query.getResultList();
        Long count= (long) queryCount.getResultList().size();
        return new Page(currentPage,pageSize,count,items);
    }
}