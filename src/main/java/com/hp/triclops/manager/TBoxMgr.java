package com.hp.triclops.manager;

import com.hp.triclops.entity.TBox;
import com.hp.triclops.repository.TBoxRepository;
import com.hp.triclops.repository.TBoxRepositoryDAO;
import com.hp.triclops.utils.Page;
import com.hp.triclops.utils.Page2;
import org.springframework.context.ApplicationContext;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by liz on 2015/10/23.
 */
public class TBoxMgr {

    private TBox tbox;
    private TBoxRepository tBoxRepository;
    private TBoxRepositoryDAO tBoxRepositoryDao;
    private ApplicationContext appContext;
    private int id = 0;

    public TBoxMgr() {
        this.setId(id);
    }

    public TBoxMgr(int id) {
       this.setId(id);
    }

    /**
     * 手动注入Repository
     * @param appContext ApplicationContext
     */
    public void setAppCtxAndInit(ApplicationContext appContext){
        this.appContext = appContext;
        this.tBoxRepository = this.appContext.getBean(TBoxRepository.class);
        this.tBoxRepositoryDao = this.appContext.getBean(TBoxRepositoryDAO.class);
        if(this.getId() != 0){
            this.setTbox(this.findTboxById(this.getId()));
        }
    }

    /**
     * 添加Tbox
     * @param tbox tbox对象
     */
    public TBox addTBox(TBox tbox){
        if(tbox.getT_sn() != null && tbox.getVin() != null) {
            TBox _box_1 = this.tBoxRepository.findByT_sn(tbox.getT_sn());
            TBox _box_2 = this.tBoxRepository.findByVin(tbox.getVin());
            if(_box_1 == null &&  _box_2 == null){
               TBox _tbox = this.tBoxRepository.save(tbox);
                return _tbox;
            }
        }
        return null;
    }

    /**
     * 删除Tbox
     * @param tbox tbox对象
     */
    public void deleteTbox(TBox tbox){
        if(this.findTboxById(tbox.getId()) != null){
            this.tBoxRepository.delete(tbox);
        }
    }

    /**
     * 修改TBox
     * @param tbox tobx对象
     * @return
     */
    public TBox updateTBox(TBox tbox){
        TBox tboxtemp = new TBox();
        if(tbox.getVin() != null) tboxtemp.setVin(tbox.getVin());
        if(tbox.getT_sn() != null) tboxtemp.setT_sn(tbox.getT_sn());
        if(tbox.getIs_activated() != 0) tboxtemp.setIs_activated(tbox.getIs_activated());
        if(tbox.getActivation_time() != null) tboxtemp.setActivation_time(tbox.getActivation_time());
        if(tbox.getImei() != null) tboxtemp.setImei(tbox.getImei());
        if(tbox.getMobile() != null) tboxtemp.setMobile(tbox.getMobile());
        if(tbox.getRemark() != null) tboxtemp.setRemark(tbox.getRemark());
        TBox _tbox = this.tBoxRepository.save(tboxtemp);
        return _tbox;
    }

    /**
     * 根据条件查询TBox列表
     * @param id ID
     * @param t_sn tobx码
     * @param vin VIN码
     * @param isActivated 是否被激活
     * @param imei IMEI
     * @param mobile SIM卡
     * @param fuzzy 查询类型标志 0 精确查询 1 模糊查询
     * @return 分页对象
     */
    public Page2<TBox> findTboxByKeys(int id, String t_sn, String vin, int isActivated, String imei, String mobile, int fuzzy, int pageSize,int currentPage){
        return this.tBoxRepositoryDao.findTboxByKeys(id,t_sn,vin,isActivated,imei,mobile,fuzzy,pageSize,currentPage);
    }

    /**
     * 根据ID查询TBOX
     * @param tid TBoxID
     * @return  查找到的组织
     */
    public TBox findTboxById(int tid){
        return this.tBoxRepository.findById(tid);
    }

    public TBox getTbox() {
        return tbox;
    }

    public void setTbox(TBox tbox) {
        this.tbox = tbox;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }


}
