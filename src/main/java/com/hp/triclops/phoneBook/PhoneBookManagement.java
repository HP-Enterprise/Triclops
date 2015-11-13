package com.hp.triclops.phoneBook;

import com.hp.triclops.entity.User;
import com.hp.triclops.repository.PhoneBookRepositoryDAO;
import com.hp.triclops.repository.UserRepository;
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
    private PhoneBookRepositoryDAO phoneBookRepositoryDAO;

    @Autowired
    private UserRepository userRepository;

    /**
     * 新增联系人信息
     * @param phoneBookShow
     * @return true：添加成功  false：添加失败
     */
    public boolean addContacter(PhoneBookShow phoneBookShow){

        //Todo:  1.查询联系人电话是否在user表中
        //Todo:  2.若存在，则将isuser属性设置为"1"，否则设置为“0”
        //Todo:  3.调用PhoneBookRepositoryDAO中的方法对数据进行保存
        User user = userRepository.findByPhone(phoneBookShow.getPhone());
        if (user != null) {
            phoneBookShow.setIsuser(0);
        } else {
            phoneBookShow.setIsuser(1);
        }
        PhoneBookShow phoneBookShowReturn =  phoneBookRepositoryDAO.add(phoneBookShow);
        if (phoneBookShowReturn != null) {
            return true;
        }
        return false;
    }

    /**
     * 删除一条联系人信息
     * @param phoneBookShow 联系人信息
     * @return  0：不存在此联系人记录   1：删除成功
     */
    public int deleteContacter(PhoneBookShow phoneBookShow){

        List<PhoneBookShow> phoneBookShowList = phoneBookRepositoryDAO.get(phoneBookShow.getId(),null,null,null,null,null,null,null,null);
        if (phoneBookShowList.size() == 0) {
            return 0;
        }
        phoneBookRepositoryDAO.delete(phoneBookShow);
        return 1;
    }

    /**
     * 修改一条联系人信息
     * @param phoneBookShow 联系人信息
     * @return 0：不存在此联系人记录  1：修改成功
     */
    public int updataContacter(PhoneBookShow phoneBookShow){

        List<PhoneBookShow> phoneBookShowList = phoneBookRepositoryDAO.get(phoneBookShow.getId(),null,null,null,null,null,null,null,null);
        if (phoneBookShowList.size() == 0) {
            return 0;
        }
        phoneBookRepositoryDAO.updata(phoneBookShow);
        return 1;
    }


    /**
     * 获取用户通讯录
     * @param uid 用户ID
     * @param orderByProperty 排序条件
     * @param ascOrDesc 排序方式
     * @param pageSize 分页大小
     * @param currentPage 当前页
     * @return 联系人集合
     */
    public List<PhoneBookShow> getPhoneBook(int uid,String orderByProperty,String ascOrDesc,Integer pageSize,Integer currentPage){
        return phoneBookRepositoryDAO.get(null,uid,null,null,null,orderByProperty,ascOrDesc,pageSize,currentPage);
    }
}
