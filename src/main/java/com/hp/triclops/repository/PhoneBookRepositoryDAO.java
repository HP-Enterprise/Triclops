package com.hp.triclops.repository;

import com.hp.triclops.entity.PhoneBook;
import com.hp.triclops.utils.Page;
import com.hp.triclops.vo.PhoneBookShow;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Created by Teemol on 2015/11/11.
 */
@Component
public class PhoneBookRepositoryDAO {
    @PersistenceContext
    EntityManager em;
    @Autowired
    PhoneBookRepository phoneBookRepository;

    /**
     * 新增一条联系人记录
     * @param phoneBookShow 通讯录信息
     * @return 增加的通讯录信息
     */
    public PhoneBookShow add(PhoneBookShow phoneBookShow){
        PhoneBook phoneBook=new PhoneBook(phoneBookShow.getId(),phoneBookShow.getUid(),phoneBookShow.getName(),phoneBookShow.getPhone(),phoneBookShow.getUserid(),phoneBookShow.getIsuser());
        phoneBookRepository.save(phoneBook);
        phoneBookShow=new PhoneBookShow(phoneBook);
        return phoneBookShow;

    }

    /**
     * 删除一条联系人记录
     * @param id ID
     */
    public void delete(int id){
        phoneBookRepository.delete(id);
    }

    /**
     * 修改一条联系人信息
     * @param phoneBookShow 通讯录信息
     */
    public void updata(PhoneBookShow phoneBookShow){
        PhoneBook phoneBook=new PhoneBook(phoneBookShow.getId(),phoneBookShow.getUid(),phoneBookShow.getName(),phoneBookShow.getPhone(),phoneBookShow.getUserid(),phoneBookShow.getIsuser());
        phoneBookRepository.save(phoneBook);
    }

    /**
     * 条件查询联系人信息，并分页
     * @param id ID
     * @param uid 用户ID
     * @param name 联系人姓名
     * @param phone 联系人电话
     * @param isuser 是否为系统用户
     * @param orderByProperty 排序条件
     * @param ascOrDesc 排序方式:"ASC"或"DESC"  大小写均可,默认ASC
     * @param pageSize 分页大小
     * @param currentPage 当前页
     * @return 联系人集合
     */
    public Page getByPage(Integer id,Integer uid,String name,String phone,Integer isuser,String orderByProperty,String ascOrDesc,Integer pageSize,Integer currentPage){
        String jpql="select p from PhoneBook p";
        String jpql_count = "";
        id=(id==null)?-1:id;
        uid=(uid==null)?-1:uid;
        name=(name==null)?"":name;
        phone=(phone==null)?"":phone;
        isuser=(isuser==null)?-1:isuser;
        orderByProperty=(orderByProperty==null)?"id":orderByProperty;
        ascOrDesc=(ascOrDesc==null)?"Desc":ascOrDesc;
        pageSize=(pageSize==null)?10:pageSize;
        pageSize=(pageSize<=0)?10:pageSize;
        currentPage=(currentPage==null)?1:currentPage;
        currentPage=(currentPage<=0)?1:currentPage;
        jpql=jpql+" where 1=1";
        if (id>=0){
            jpql=jpql+" and p.id=:id";
        }
        if (uid>=0){
            jpql=jpql+" and p.uid=:uid";
        }
        if (!name.equals("")){
            jpql=jpql+" and p.name=:name";
        }
        if (!phone.equals("")){
            jpql=jpql+" and p.phone=:phone";
        }
        if (isuser>=0){
            jpql=jpql+" and p.isuser=:isuser";
        }
        jpql=jpql+" Order by p."+orderByProperty+" "+ascOrDesc;
        jpql_count = jpql;
        TypedQuery query=em.createQuery(jpql,PhoneBook.class);
        TypedQuery queryCount = em.createQuery(jpql_count,PhoneBook.class);
        if (id>=0){
            query.setParameter("id",id);
            queryCount.setParameter("id",id);
        }
        if (uid>=0){
            query.setParameter("uid",uid);
            queryCount.setParameter("uid",uid);
        }
        if (!name.equals("")){
            query.setParameter("name",name);
            queryCount.setParameter("name",name);
        }
        if (!phone.equals("")){
            query.setParameter("phone",phone);
            queryCount.setParameter("phone",phone);
        }
        if (isuser>=0){
            query.setParameter("isuser",isuser);
            queryCount.setParameter("isuser",isuser);
        }
        query.setFirstResult((currentPage - 1) * pageSize);
        query.setMaxResults(pageSize);
        List queryList = query.getResultList();
        Long count = (long)queryCount.getResultList().size();

        return new Page(currentPage,pageSize,count,queryList);
    }

}
