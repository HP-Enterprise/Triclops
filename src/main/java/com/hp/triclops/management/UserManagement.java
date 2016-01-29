package com.hp.triclops.management;

import com.hp.triclops.entity.UserEx;
import com.hp.triclops.repository.UserExRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Teemol on 2016/1/26.
 */
@Component
public class UserManagement {

    @Autowired
    OrganizationUserManagement organizationUserManagement;

    @Autowired
    UserExRepository userExRepository;

    /**
     * 查询用户有权查看的用户ID集合
     * @param oid 组织ID
     * @param uid 用户ID
     * @return 用户ID集合
     */
    private List<Integer> selectUserByUid(Integer oid,int uid)
    {
        List<Integer> oids = new ArrayList<>();
        List<Integer> orgUids = new ArrayList<>();

        if(oid != null)
        {
            if(oids.contains(oid))
            {
                oids.clear();
                oids.add(oid);
            }
            else
            {
                oids.clear();
            }
        }

        if(oids.size()>0)
        {
            orgUids = organizationUserManagement.findUidByOids(oids);
        }

        if(oid == null)
        {
            if(!orgUids.contains(uid)){
                orgUids.add(uid);
            }
        }

        return orgUids;
    }

    /**
     * 条件查询用户
     * @param uid 用户ID
     * @param name 用户名
     * @param gender 性别
     * @param nick 昵称
     * @param phone 电话号码
     * @param isVerified 是否已验证 0：未验证 1：已验证
     * @param currentPage 当前页
     * @param pageSize 页面大小
     * @return 车辆信息集合
     */
    public Page<UserEx> selectUser(Integer oid,int uid, String name, Integer gender, String nick, String phone, Integer isVerified, Integer currentPage, Integer pageSize)
    {
        if(name!=null) name = "%" + name + "%";
        if(nick!=null) nick = "%" + nick + "%";
        if(phone!=null) phone = "%" + phone + "%";
        currentPage = currentPage==null?1:currentPage;
        currentPage = currentPage<=0?1:currentPage;
        pageSize = pageSize==null?10:pageSize;
        pageSize = pageSize<=0?10:pageSize;
        Pageable p = new PageRequest(currentPage-1,pageSize);

        Page<UserEx> userPage = new PageImpl<>(new ArrayList<>(),p,0);
        List<Integer> uids = selectUserByUid(oid,uid);
        if(uids == null || uids.size()==0)
        {
            return userPage;
        }
        userPage = userExRepository.selectUser(uids,name,gender,nick,phone,isVerified,p);

        return userPage;
    }

    /**
     * 条件查询用户（超级管理员）
     * @param name 用户名
     * @param gender 性别
     * @param nick 昵称
     * @param phone 电话号码
     * @param isVerified 是否已验证 0：未验证 1：已验证
     * @param currentPage 当前页
     * @param pageSize 页面大小
     * @return 车辆信息集合
     */
    public Page<UserEx> selectUser(String name, Integer gender, String nick, String phone, Integer isVerified, Integer currentPage, Integer pageSize)
    {
        if(name!=null) name = "%" + name + "%";
        if(nick!=null) nick = "%" + nick + "%";
        if(phone!=null) phone = "%" + phone + "%";
        currentPage = currentPage==null?1:currentPage;
        currentPage = currentPage<=0?1:currentPage;
        pageSize = pageSize==null?10:pageSize;
        pageSize = pageSize<=0?10:pageSize;
        Pageable p = new PageRequest(currentPage-1,pageSize);

        Page<UserEx> userPage =  userExRepository.selectUser(name,gender,nick,phone,isVerified,p);

        return userPage;
    }
}
