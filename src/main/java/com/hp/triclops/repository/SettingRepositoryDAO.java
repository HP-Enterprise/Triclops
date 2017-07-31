package com.hp.triclops.repository;

import com.hp.triclops.entity.Setting;
import com.hp.triclops.entity.TBox;
import com.hp.triclops.utils.Page2;
import io.netty.util.internal.StringUtil;
import org.springframework.stereotype.Component;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.persistence.TypedQuery;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by sunjun on 2017/7/31.
 */
@Component
public class SettingRepositoryDAO {

    @PersistenceContext
    private EntityManager em;

    /**
     * 根据条件查询TBox列表
     * @param id ID
     * @param type 参数类型
     * @param code 参数编码
     * @param name 参数名称
     * @param orderByProperty 排序条件 TBox类的某一个属性,默认id
     * @param ascOrDesc 排序顺序接受字符串 "ASC"或"DESC"  大小写均可,默认ASC
     * @param pageSize 每页大小
     * @param currentPage 页码
     * @return 分页对象
     */
    public Page2<Setting> findSettingByParam(int id, String type, String code, String name, String orderByProperty, String ascOrDesc, int pageSize,int currentPage){
        String jpql = "select s from Setting s  where 1 = 1";
        String jpqlSum = "select count(*) as count from t_setting s where 1 = 1";
        if(id != 0){
            jpql += " and s.id = :id";
            jpqlSum += " and s.id = :id";
        }
        if(!StringUtil.isNullOrEmpty(code)){
            jpql += " and s.code = :code";
            jpqlSum += " and s.code = :code";
        }
        if(!StringUtil.isNullOrEmpty(type)){
            jpql += " and s.type = :type";
            jpqlSum += " and s.type = :type";
        }
        if(!StringUtil.isNullOrEmpty(name)){
            jpql += " and s.name like :name";
            jpqlSum += " and s.name like :name";
        }

        jpql=jpql+" Order by s." + orderByProperty + " "+ascOrDesc;
        TypedQuery query = em.createQuery(jpql, Setting.class);
        Query queryCount = em.createNativeQuery(jpqlSum);//EntityManager id closed
        if(id != 0){
            query.setParameter("id",id);
            queryCount.setParameter("id",id);
        }
        if(!StringUtil.isNullOrEmpty(code)){
            query.setParameter("code",code);
            queryCount.setParameter("code", code);
        }
        if(!StringUtil.isNullOrEmpty(type)){
            query.setParameter("type",type);
            queryCount.setParameter("type", type);
        }
        if(!StringUtil.isNullOrEmpty(name)){
            query.setParameter("name", "%" + name + "%");
            queryCount.setParameter("name", "%" + name + "%");
        }

        List items_sum =  queryCount.getResultList();
        String count = items_sum.get(0).toString();

        List items = query.getResultList();
        List<Setting> settingList = new ArrayList<Setting>();
        for(Object obj : items){
            Setting setting = (Setting)obj;
            settingList.add(setting);
        }

        Page2<Setting> page2 = new Page2<Setting>();
        page2.setCurrentPage(currentPage);
        page2.setPageSize(pageSize);
        page2.setRecordCount(Long.parseLong(count));
        page2.setItems(settingList);

        return page2;
    }

}
