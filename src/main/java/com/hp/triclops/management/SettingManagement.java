package com.hp.triclops.management;

import com.hp.triclops.entity.Setting;
import com.hp.triclops.entity.TBox;
import com.hp.triclops.entity.TBoxEx;
import com.hp.triclops.repository.SettingRepository;
import com.hp.triclops.repository.SettingRepositoryDAO;
import com.hp.triclops.repository.TBoxExRepository;
import com.hp.triclops.repository.TBoxRepositoryDAO;
import com.hp.triclops.utils.Page2;
import com.hp.triclops.vo.TBoxExShow;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Created by Incar on 2016/3/23.
 */
@Component
public class SettingManagement {

    @Autowired
    SettingRepository settingRepository;
    @Autowired
    SettingRepositoryDAO settingRepositoryDAO;

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
    public Page2<Setting> findSettingByParam(Integer id, String type, String code, String name, String orderByProperty, String ascOrDesc, Integer pageSize, Integer currentPage){
        currentPage = currentPage == null ? 1 : currentPage;
        pageSize = pageSize == null ? 10 : pageSize;
        id = id == null ? 0 : id;
        orderByProperty=(orderByProperty==null)?"id":orderByProperty;
        ascOrDesc=(ascOrDesc==null)?"ASC":ascOrDesc;
        return settingRepositoryDAO.findSettingByParam(id, type, code, name, orderByProperty, ascOrDesc, pageSize, currentPage);
    }
}
