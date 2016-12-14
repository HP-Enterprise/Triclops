package com.hp.triclops.repository;

import com.hp.triclops.entity.RemoteControl;
import com.hp.triclops.utils.EscapeStringUtil;
import com.hp.triclops.utils.Page;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.hibernate.Session;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Created by HYu on 2015/11/6.
 */
@Component
public class RemoteControlRespositoryDao {

    @PersistenceContext
    private EntityManager em;

    @Autowired
    RemoteControlRepository remoteControlRepository;

    /**
     * @param vin vin
     * @param orderByProperty 排序条件
     * @param ascOrDesc 升降序
     * @param pageSize 页面size
     * @param currentPage   当前页码
     * @return  page 封装好的分页远程控制数据
     */
    public Page findRemoteControlByVin(String vin,String orderByProperty,String ascOrDesc,Integer pageSize,Integer currentPage){
        String jpql="select rc FROM RemoteControl rc where 1=1 And rc.available = 1 ";
        String jpql_count="";
        vin=(vin==null)?"": EscapeStringUtil.toEscape(vin);
        pageSize=(pageSize==null)?10:pageSize;
        pageSize=(pageSize<=0)?10:pageSize;
        currentPage=(currentPage==null)?1:currentPage;
        currentPage=(currentPage<=0)?1:currentPage;
        orderByProperty=(orderByProperty==null)?"vin":orderByProperty;
        ascOrDesc=(ascOrDesc==null)?"ASC":ascOrDesc;

        if(!vin.equals("")){
            jpql=jpql+" And rc.vin = :vin";
        }

        jpql=jpql+" Order by rc."+orderByProperty+" "+ascOrDesc;
        jpql_count=jpql;

        TypedQuery query = em.createQuery(jpql, RemoteControl.class);
        TypedQuery queryCount = em.createQuery(jpql_count, RemoteControl.class);

        if(!vin.equals("")){
            query.setParameter("vin",vin);
            queryCount.setParameter("vin",vin);
        }

        query.setFirstResult((currentPage - 1)* pageSize);
        query.setMaxResults(pageSize);
        List items=query.getResultList();
        Long count= (long) queryCount.getResultList().size();
        return new Page(currentPage,pageSize,count,items);
    }


    /**
     * remotecontrol修改
     * ids 消息ID字符串<br>
     * @param ids 远程控制ID
     * @return satae -1远程控制删除失败，1删除成功
     */
    public int modifyRemoteControl(String ids){
        int state = 0;
        List<String> list = new ArrayList<String>();
        String jpql = "select rc from RemoteControl rc where rc.id in ("+ids+") and rc.available = 1";
        System.out.println(jpql);
        TypedQuery query=em.createQuery(jpql,RemoteControl.class);
        List queryList = query.getResultList();
        List<RemoteControl> remoteList = new ArrayList<RemoteControl>();
        Iterator iterator = queryList.iterator();
        while (iterator.hasNext()) {
            remoteList.add((RemoteControl) iterator.next());
            System.out.println(remoteList);
        }
        if(remoteList.size()==0){
            state = -1;
        }else{
            try{
                remoteList.forEach(rc -> {
                    rc.setAvailable((short)0);
                    remoteControlRepository.save(rc);
                });
                state = 1;
            }catch (Exception e){
                state = -1;
            }
        }
        return state;
    }

    public RemoteControl findById(Long id){
        Session session = em.unwrap(org.hibernate.Session.class);
        RemoteControl remoteControl= em.find(RemoteControl.class, id);
        session.evict(remoteControl);
        return  remoteControl;
    }

}
