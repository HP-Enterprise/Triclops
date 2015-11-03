package com.hp.triclops.user;

import java.util.List;

/**
 * Created by Teemol on 2015/11/3.
 */
public class UserManagement {

    /**
     * 获取用户信息
     * @param uid  用户ID
     * @return 用户信息
     */
    public User getUser(int uid){
        return null;
    }

    /**
     * 修改用户信息
     * @param user 用户信息
     * @return 修改后的用户信息
     */
    public User modifyUser(User user){
        return null;
    }

    /**
     * 条件查询用户信息
     * @param id
     * @param name
     * @param gender
     * @param nick
     * @param phone
     * @param isVerified
     * @param contacts
     * @param contactsPhone
     * @param orderByProperty
     * @param ascOrDesc
     * @param currentPage
     * @param pageSize
     * @param vin
     * @param vid
     * @param isowner
     * @param fuzzy
     * @param oid
     * @return
     */
    public List<User> getUserList(Integer id,String name,Integer gender,String nick,String phone,Integer isVerified,String contacts, String contactsPhone,
                                  String orderByProperty,String ascOrDesc,Integer currentPage,Integer pageSize, String vin,Integer vid, Integer isowner, Integer fuzzy, Integer oid){

        return null;
    }
}
