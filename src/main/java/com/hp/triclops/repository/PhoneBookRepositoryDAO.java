package com.hp.triclops.repository;

import com.hp.triclops.vo.PhoneBookShow;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Created by Teemol on 2015/11/11.
 */
@Component
public class PhoneBookRepositoryDAO {

    /**
     * 新增一条联系人记录
     * @param phoneBookShow 通讯录信息
     * @return 增加的通讯录信息
     */
    public PhoneBookShow add(PhoneBookShow phoneBookShow){
        return null;
    }

    /**
     * 删除一条联系人记录
     * @param phoneBookShow 通讯录信息
     */
    public void delete(PhoneBookShow phoneBookShow){

    }

    /**
     * 修改一条联系人信息
     * @param phoneBookShow 通讯录信息
     */
    public void updata(PhoneBookShow phoneBookShow){

    }

    /**
     *
     * @param id ID
     * @param uid 用户ID
     * @param name 联系人姓名
     * @param phone 联系人电话
     * @param isuser 是否为系统用户
     * @param orderByProperty 排序条件
     * @param ascOrDesc 排序方式
     * @param pageSize 分页大小
     * @param currentPage 当前页
     * @return 联系人集合
     */
    public List<PhoneBookShow> get(Integer id,Integer uid,String name,String phone,Integer isuser,String orderByProperty,String ascOrDesc,Integer pageSize,Integer currentPage){
        return null;
    }

}
