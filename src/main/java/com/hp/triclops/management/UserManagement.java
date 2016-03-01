package com.hp.triclops.management;

import com.hp.triclops.entity.UserEx;
import com.hp.triclops.repository.UserExRepository;
import com.hp.triclops.vo.UserExShow;
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
     * 新增用户
     * @param userExShow 用户信息
     * @return 新增后的用户信息
     */
    public UserExShow save(UserExShow userExShow)
    {
        UserEx userEx = new UserEx(userExShow);
        UserEx returnUser = userExRepository.save(userEx);
        return new UserExShow(returnUser);
    }

    /**
     * 根据ID查询用户信息
     * @param id 用户ID
     * @return 用户信息
     */
    public UserExShow findById(int id)
    {
        UserEx userEx = userExRepository.findById(id);
        if(userEx == null)
        {
            return null;
        }

        return new UserExShow(userEx);
    }

    /**
     * 根据用户名查询用户信息
     * @param name 用户名
     * @return 用户信息
     */
    public UserExShow findByName(String name)
    {
        UserEx userEx = userExRepository.findByName(name);
        if(userEx == null)
        {
            return null;
        }

        return new UserExShow(userEx);
    }

    /**
     * 修改用户信息
     * @param user 用户信息类
     * @return 修改后的的用户信息
     */
    public UserExShow modifyUser(UserExShow user)
    {
        UserEx userEx = new UserEx(user);
        UserEx returnUser = userExRepository.save(userEx);
        return new UserExShow(returnUser);
    }

    /**
     * 条件查询用户(具有Read权限的组织用户)
     * @param name 用户名
     * @param gender 性别
     * @param nick 昵称
     * @param phone 电话号码
     * @param isVerified 是否已验证 0：未验证 1：已验证
     * @param currentPage 当前页
     * @param pageSize 页面大小
     * @return 车辆信息集合
     */
    public Page<UserExShow> readSelect(int oid,String name, Integer gender, String nick, String phone, Integer isVerified, Integer currentPage, Integer pageSize)
    {
        if(name!=null) name = "%" + name + "%";
        if(nick!=null) nick = "%" + nick + "%";
        if(phone!=null) phone = "%" + phone + "%";
        currentPage = currentPage==null?1:currentPage;
        currentPage = currentPage<=0?1:currentPage;
        pageSize = pageSize==null?10:pageSize;
        pageSize = pageSize<=0?10:pageSize;
        Pageable p = new PageRequest(currentPage-1,pageSize);

        List<Integer> oids = new ArrayList<>();
        oids.add(oid);
        List<Integer> uids = organizationUserManagement.findUidByOids(oids);
        if(uids == null || uids.size()==0)
        {
            return new PageImpl<>(new ArrayList<>(),p,0);
        }
        Page<UserEx> userPage = userExRepository.selectUser(uids,name,gender,nick,phone,isVerified,p);

        List<UserEx> list = userPage.getContent();
        List<UserExShow> returnList = new ArrayList<>();
        for(UserEx user:list)
        {
            UserExShow userExShow = new UserExShow(user);
            returnList.add(userExShow);
        }

        return new PageImpl<>(returnList,p,userPage.getTotalElements());
    }

    /**
     * 条件查询用户(不具有Read权限的组织成员)
     * @param name 用户名
     * @param gender 性别
     * @param nick 昵称
     * @param phone 电话号码
     * @param isVerified 是否已验证 0：未验证 1：已验证
     * @param currentPage 当前页
     * @param pageSize 页面大小
     * @return 车辆信息集合
     */
    public Page<UserExShow> select(int oid, String name, Integer gender, String nick, String phone, Integer isVerified, Integer currentPage, Integer pageSize)
    {
        currentPage = currentPage==null?1:currentPage;
        currentPage = currentPage<=0?1:currentPage;
        pageSize = pageSize==null?10:pageSize;
        pageSize = pageSize<=0?10:pageSize;
        Pageable p = new PageRequest(currentPage-1,pageSize);

        Page<UserExShow> userPage =  readSelect(oid,name,gender,nick,phone,isVerified,currentPage,pageSize);

        List<UserExShow> list = userPage.getContent();
        List<UserExShow> returnList = new ArrayList<>();
        for(UserExShow user:list)
        {
            user.blur();   // 信息过滤
            returnList.add(user);
        }

        return  new PageImpl<>(returnList,p,userPage.getTotalElements());
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
    public Page<UserExShow> adminSelect(String name, Integer gender, String nick, String phone, Integer isVerified, Integer currentPage, Integer pageSize)
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

        List<UserEx> list = userPage.getContent();
        List<UserExShow> returnList = new ArrayList<>();
        for(UserEx user:list)
        {
            UserExShow userExShow = new UserExShow(user);
            returnList.add(userExShow);
        }

        return new PageImpl<>(returnList,p,userPage.getTotalElements());
    }

    /**
     * 条件查询用户（用户注册查询）
     * @param name 用户名
     * @param gender 性别
     * @param nick 昵称
     * @param phone 电话号码
     * @param isVerified 是否已验证 0：未验证 1：已验证
     * @param currentPage 当前页
     * @param pageSize 页面大小
     * @return 车辆信息集合
     */
    public Page<UserExShow> registSelect(String name, Integer gender, String nick, String phone, Integer isVerified, Integer currentPage, Integer pageSize)
    {
        currentPage = currentPage==null?1:currentPage;
        currentPage = currentPage<=0?1:currentPage;
        pageSize = pageSize==null?10:pageSize;
        pageSize = pageSize<=0?10:pageSize;
        Pageable p = new PageRequest(currentPage-1,pageSize);

        Page<UserExShow> userPage =  adminSelect(name,gender,nick,phone,isVerified,currentPage,pageSize);

        List<UserExShow> list = userPage.getContent();
        List<UserExShow> returnList = new ArrayList<>();
        for(UserExShow user:list)
        {
            user.blur();   // 信息过滤
            returnList.add(user);
        }

        return  new PageImpl<>(returnList,p,userPage.getTotalElements());
    }

}
