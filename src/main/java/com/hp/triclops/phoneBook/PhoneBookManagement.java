package com.hp.triclops.phoneBook;

import com.hp.triclops.vo.PhoneBookShow;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Created by Teemol on 2015/11/11.
 */
@Component
public class PhoneBookManagement {

    /**
     * 新增联系人信息
     * @param phoneBookShow
     * @return true：添加成功  false：添加失败
     */
    public boolean addContacter(PhoneBookShow phoneBookShow){

        //Todo:  1.查询联系人电话是否在user表中
        //Todo:  2.若存在，则将isuser属性设置为"1"，否则设置为“0”
        //Todo:  3.调用PhoneBookRepositoryDAO中的方法对数据进行保存

        return true;
    }

    /**
     * 删除一条联系人信息
     * @param phoneBookShow 联系人信息
     * @return  0：不存在此联系人记录   1：删除成功
     */
    public int deleteContacter(PhoneBookShow phoneBookShow){
        return 1;
    }

    /**
     * 修改一条联系人信息
     * @param phoneBookShow 联系人信息
     * @return 0：不存在此联系人记录  1：删除成功
     */
    public int updataContacter(PhoneBookShow phoneBookShow){
        return 1;
    }


    /**
     * 获取用户通讯录
     * @param uid
     * @param orderByProperty
     * @param ascOrDesc
     * @param pageSize
     * @param currentPage
     * @return
     */
    public List<PhoneBookShow> getPhoneBook(int uid,String orderByProperty,String ascOrDesc,Integer pageSize,Integer currentPage){
        return null;
    }
}
