package com.hp.triclops.repository;

import com.hp.triclops.entity.User;
import com.hp.triclops.entity.UserVehicleRelatived;
import com.hp.triclops.utils.Page;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import java.util.ArrayList;
import java.util.List;


@Component
@SuppressWarnings("unchecked")
public class UserRepositiryDAO<T>  {

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
        if(oid != null && oid>=0){
            jpql = jpql+" join u.organizationSet O where O.id =:oid";
        }else{
            jpql = jpql+ " where 1=1";
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
        System.out.println("jpql:"+jpql);
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


        List<Object> items = query.getResultList();  //获取所有查询结果

        //车主车辆关系过滤
        if(vin!=null || isowner!=null)
        {
            List<Object> userVehicleRelativedList = userVehicleRelativedRepositoryDAO.getList(vin,isowner);
            items = userFilter(items,userVehicleRelativedList);
        }

        Long count = (long)items.size();
        items = getPageData(pageSize,currentPage,items);  //数据分页


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

        if(oid != null && oid>=0){
            jpql = jpql+" join u.organizationSet O where O.id =:oid";
        }else{
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

        List<Object> items = query.getResultList();

        if(vin!=null || isowner!=null)
        {
            List<Object> userVehicleRelativedList = userVehicleRelativedRepositoryDAO.getListAccurate(vin, isowner);
            items = userFilter(items,userVehicleRelativedList);
        }

        Long count = (long)items.size();
        items = getPageData(pageSize,currentPage,items);

//        query.setFirstResult((currentPage - 1)* pageSize);
//        query.setMaxResults(pageSize);
//        List items=query.getResultList();
//        Long count= (long) queryCount.getResultList().size();

        return new Page(currentPage,pageSize,count,items);
    }

    /**
     * 过滤查询结果
     * @param userList   用户列表
     * @param filterList 用户车辆关系列表
     * @return 过滤后的用户列表
     */
    public List userFilter(List userList,List filterList)
    {
        List<Object> result = new ArrayList<Object>();

        for (int i=0;i<userList.size();i++)
        {
            User user = (User)userList.get(i);
            int id = user.getId();

            for (int j=0;j<filterList.size();j++)
            {
                UserVehicleRelatived userVehicleRelatived = (UserVehicleRelatived)filterList.get(j);
                User userid = userVehicleRelatived.getUid();
                if(id==userid.getId())
                {
                    result.add(userList.get(i));
                    break;
                }
            }
        }

        return result;
    }

    /**
     * 获取当前页面数据集
     * @param pageSize  页面数据显示条数
     * @param currentPage  当前页号
     * @param items  查询结果集
     * @return  当前页面数据集
     */
    public List<Object> getPageData(int pageSize,int currentPage,List items)
    {
        List<Object> result = new ArrayList<Object>();

        if(items.size()<=pageSize*(currentPage-1))
        {
            return result;
        }
        else if(items.size()< pageSize*currentPage)
        {
            for (int i=pageSize*(currentPage-1);i<items.size();i++)
            {
                result.add(items.get(i));
            }
        }
        else
        {
            for (int i=pageSize*(currentPage-1);i<pageSize*currentPage;i++)
            {
                result.add(items.get(i));
            }
        }

        return result;
    }

}