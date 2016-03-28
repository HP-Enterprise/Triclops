package com.hp.triclops.phoneBook;

import com.hp.triclops.repository.PhoneBookRepository;
import com.hp.triclops.repository.PhoneBookRepositoryDAO;
import com.hp.triclops.utils.Page;
import com.hp.triclops.vo.PhoneBookShow;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Created by Teemol on 2015/11/11.
 */
@Component
public class PhoneBookManagement {

    @Autowired
    PhoneBookRepositoryDAO phoneBookRepositoryDAO;

    @Autowired
    PhoneBookRepository phoneBookRepository;


    /**
     * 条件查询用户通讯录，并分页
     * @param uid 用户ID
     * @param orderByProperty 排序条件
     * @param ascOrDesc 排序方式："ASC"或"DESC"
     * @param pageSize 分页大小
     * @param currentPage 当前页
     * @return 联系人集合
     */
    public Page getContactersByPage(int uid, String orderByProperty, String ascOrDesc, Integer pageSize, Integer currentPage) {
        return phoneBookRepositoryDAO.getByPage(null, uid, null, null, null, orderByProperty, ascOrDesc, pageSize, currentPage);
    }

    /**
     * 新增联系人信息
     * @param phoneBookShow 联系人信息
     * @return true：添加成功  false：添加失败
     */
    public boolean addContacter(PhoneBookShow phoneBookShow){

        PhoneBookShow phoneBookShowReturn =  phoneBookRepositoryDAO.add(phoneBookShow);
        if (phoneBookShowReturn != null) {
            return true;
        }
        return false;
    }

    /**
     * 删除一条联系人信息
     * @param id 联系人信息
     * @return  0：不存在此联系人记录   1：删除成功
     */
    public int deleteContacter(int id){

        List phoneBookShowList = phoneBookRepositoryDAO.getByPage(id, null, null, null, null, null, null, null, null).getItems();
        if (phoneBookShowList.size() == 0) {
            return 0;
        }
        phoneBookRepositoryDAO.delete(id);
        return 1;
    }

    /**
     * 修改一条联系人信息
     * @param phoneBookShow 联系人信息
     * @return 0：不存在此联系人记录  1：修改成功
     */
    public int updataContacter(PhoneBookShow phoneBookShow){

        List phoneBookShowList = phoneBookRepositoryDAO.getByPage(phoneBookShow.getId(),null,null,null,null,null,null,null,null).getItems();
        if (phoneBookShowList.size() == 0) {
            return 0;
        }
        phoneBookRepositoryDAO.updata(phoneBookShow);
        return 1;
    }

    /**
     * 删除用户通讯录
     * @param uid 用户ID
     * @return 0：删除失败 1：删除成功
     */
    public int deletePhoneBook(int uid){
        int flag = phoneBookRepository.deleteByUid(uid);
        if (flag > 0) {
            return 1;
        }
        return 0;
    }

}
