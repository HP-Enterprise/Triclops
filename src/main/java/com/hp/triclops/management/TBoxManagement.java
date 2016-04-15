package com.hp.triclops.management;

import com.hp.triclops.entity.TBox;
import com.hp.triclops.entity.TBoxEx;
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
public class TBoxManagement {

    @Autowired
    TBoxExRepository tBoxExRepository;
    @Autowired
    TBoxRepositoryDAO tBoxRepositoryDao;

    /**
     * 保存TBox信息
     * @param tBoxExShow TBox信息
     * @return 保存后的TBox信息
     */
    public TBoxExShow saveTBox(TBoxExShow tBoxExShow)
    {
        TBoxEx tBoxEx = new TBoxEx(tBoxExShow);
        TBoxEx returnTBoxEx = tBoxExRepository.save(tBoxEx);
        return new TBoxExShow(returnTBoxEx);
    }

    /**
     * 删除TBox
     * @param id
     */
    public void deleteTBox(int id)
    {
        tBoxExRepository.deleteById(id);
    }

    /**
     * 修改TBox信息
     * @param tBoxExShow TBox信息
     * @return 修改后的TBox信息
     */
    public TBoxExShow modifyTBox(TBoxExShow tBoxExShow)
    {
        TBoxEx tBoxEx = new TBoxEx(tBoxExShow);
        TBoxEx returnTBoxEx = tBoxExRepository.save(tBoxEx);
        return new TBoxExShow(returnTBoxEx);
    }

    /**
     * 绑定TBox
     * @param tBoxExShow TBox信息
     */
    public TBoxExShow bindTBox(TBoxExShow tBoxExShow)
    {
        return modifyTBox(tBoxExShow);
    }

    /**
     * 解绑TBox
     * @param id TBox ID
     */
    public void unbindTBox(int id)
    {
        TBoxExShow tBoxExShow = findById(id);
        if(tBoxExShow!=null)
        {
            Integer vid = tBoxExShow.getVid();
            if(vid!=null)
            {
                tBoxExShow.setVid(null);
                tBoxExShow.setVin(null);
                saveTBox(tBoxExShow);
            }
        }
    }

    /**
     * 查询TBox
     * @param id TBox ID
     * @return TBox 信息
     */
    public TBoxExShow findById(int id)
    {
        TBoxEx tBoxEx = tBoxExRepository.findById(id);
        if(tBoxEx == null)
        {
            return null;
        }
        return new TBoxExShow(tBoxEx);
    }

    /**
     * 根据TBox码查询TBox信息
     * @param t_sn TBox码
     * @return TBox信息
     */
    public TBoxExShow findByT_sn(String t_sn)
    {
        TBoxEx tBoxEx = tBoxExRepository.findByT_sn(t_sn);
        if(tBoxEx == null)
        {
            return null;
        }
        return new TBoxExShow(tBoxEx);
    }

    /**
     * 根据TBox ID和车辆ID查询TBox信息
     * @param id TBox ID
     * @param vid 车辆ID
     * @return TBox信息
     */
    public TBoxExShow findByIdAndVid(int id, int vid)
    {
        TBoxEx tBoxEx = tBoxExRepository.findByIdAndVid(id,vid);
        if(tBoxEx==null)
        {
            return null;
        }
        return new TBoxExShow(tBoxEx);
    }

    /**
     * 根据条件查询TBox列表
     * @param id ID
     * @param t_sn tobx码
     * @param isbind 0 or null 查找全部 1 查找未绑定的
     * @param vin VIN码
     * @param isActivated 是否被激活
     * @param imei IMEI
     * @param mobile SIM卡
     * @param total 是否查全部，不分页：0分页查询，1为查询全部
     * @param orderByProperty 排序条件 TBox类的某一个属性,默认id
     * @param ascOrDesc 排序顺序接受字符串 "ASC"或"DESC"  大小写均可,默认ASC
     * @param fuzzy 查询类型标志 0 精确查询 1 模糊查询
     * @param pageSize 每页大小
     * @param currentPage 页码
     * @return 分页对象
     */
    public Page2<TBox> findTboxByKeys(Integer id, String t_sn, Integer isbind, String vin, Integer isActivated, String imei, String mobile, Integer total, String orderByProperty, String ascOrDesc, Integer fuzzy, Integer pageSize, Integer currentPage){
        currentPage = currentPage == null ? 1 : currentPage;
        pageSize = pageSize == null ? 10 : pageSize;
        id = id == null ? 0 : id;
        isActivated = isActivated == null ? 0 : isActivated;
        total = total == null ? 0 :total;
        fuzzy = fuzzy == null ? 0 : fuzzy;
        isbind = isbind == null ? 0 : isbind;
        orderByProperty=(orderByProperty==null)?"id":orderByProperty;
        ascOrDesc=(ascOrDesc==null)?"ASC":ascOrDesc;
        return tBoxRepositoryDao.findTboxByKeys(id,t_sn, isbind, vin,isActivated,imei,mobile,total,orderByProperty,ascOrDesc,fuzzy,pageSize,currentPage);
    }
}
