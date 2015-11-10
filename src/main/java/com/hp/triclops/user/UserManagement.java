package com.hp.triclops.user;

import com.hp.triclops.entity.User;
import com.hp.triclops.repository.UserRepository;
import com.hp.triclops.repository.UserRepositoryDAO;
import com.hp.triclops.user.vo.UserShow;
import com.hp.triclops.utils.Page;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Teemol on 2015/11/3.
 */
public class UserManagement {

    @Autowired
    UserRepository userRepository;

    @Autowired
    UserRepositoryDAO userRepositoryDAO;

    /**
     * 获取用户信息
     * @param uid  用户ID
     * @return 用户信息
     */
    public User getUser(int uid){
       return userRepository.findById(uid);
    }

    /**
     * 修改用户信息
     * @param user 用户信息
     * @return 修改后的用户信息
     */
    public User modifyUser(User user){
//        int id = user.getId();
//        String name = user.getName();
//        String nick = user.getNick();
//        String phone = user.getPhone();
//        Integer gender = user.getGender();
//        String contacts = user.getContacts();
//        String contactsPhone = user.getContactsPhone();
//        int isVerified = user.getIsVerified();
        return userRepository.save(user);
    }

    /**
     * 条件查询用户信息
     * @param id 用户id
     * @param name 姓名
     * @param gender 性别
     * @param nick 昵称
     * @param phone 电弧
     * @param isVerified 是否验证，0表示未验证，1表示已验证
     * @param contacts 紧急联系人姓名
     * @param contactsPhone 紧急联系人电话
     * @param orderByProperty 排序条件 User类的某一个属性,默认id
     * @param ascOrDesc 排序顺序接受字符串 "ASC"或"DESC"  大小写均可,默认ASC
     * @param currentPage 获取指定页码数据 必须大于0
     * @param pageSize 每页数据条数 必须大于0
     * @param vin 车架号
     * @param isowner 是否为车主
     * @param fuzzy 1:模糊查询 0:精确查询
     * @param oid 组织id
     * @return 当前页面的用户记录
     */
    public List<UserShow> getUserList(Integer id,String name,Integer gender,String nick,String phone,Integer isVerified,String contacts, String contactsPhone,
                                  String orderByProperty,String ascOrDesc,Integer currentPage,Integer pageSize, String vin, Integer isowner, Integer fuzzy, Integer oid){
        List<UserShow> userList = new ArrayList<UserShow>();
        Page page= userRepositoryDAO.findUserByKeys(id,name,gender,nick,phone,isVerified,contacts,contactsPhone,orderByProperty,ascOrDesc,currentPage,pageSize,vin,isowner,fuzzy,oid);
        if (page.getItems() != null && page.getItems().size() > 0) {
            for (Object o : page.getItems()) {
                User user = (User) o;
                userList.add(new UserShow(user));
            }
        }
        return userList;
    }
}
