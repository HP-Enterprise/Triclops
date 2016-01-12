package com.hp.triclops.manager;

import com.hp.triclops.entity.TBox;
import com.hp.triclops.repository.TBoxRepository;
import com.hp.triclops.repository.TBoxRepositoryDAO;
import com.hp.triclops.utils.Page2;
import org.springframework.context.ApplicationContext;

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
     * @return 执行后的TBox
     */
    public TBox addTBox(TBox tbox){
        if(tbox.getT_sn() != null) {
            TBox _box_1 = this.tBoxRepository.findByT_sn(tbox.getT_sn());
            if(_box_1 == null){
               TBox _tbox = this.tBoxRepository.save(tbox);
                return _tbox;
            }
            return _box_1;
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
     * @return tbox对象
     */
    public TBox updateTBox(TBox tbox){
        TBox tboxtemp = this.tBoxRepository.findById(tbox.getId());
//        TBox tboxtemp = new TBox();
        if(tbox.getId() != 0) tboxtemp.setId(tbox.getId());
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
    public Page2<TBox> findTboxByKeys(Integer id, String t_sn, Integer isbind, String vin, Integer isActivated, String imei, String mobile,Integer total,String orderByProperty,String ascOrDesc, Integer fuzzy, Integer pageSize,Integer currentPage){
        currentPage = currentPage == null ? 1 : currentPage;
        pageSize = pageSize == null ? 10 : pageSize;
        id = id == null ? 0 : id;
        isActivated = isActivated == null ? 0 : isActivated;
        total = total == null ? 0 :total;
        fuzzy = fuzzy == null ? 0 : fuzzy;
        isbind = isbind == null ? 0 : isbind;
        orderByProperty=(orderByProperty==null)?"id":orderByProperty;
        ascOrDesc=(ascOrDesc==null)?"ASC":ascOrDesc;
        return this.tBoxRepositoryDao.findTboxByKeys(id,t_sn, isbind, vin,isActivated,imei,mobile,total,orderByProperty,ascOrDesc,fuzzy,pageSize,currentPage);
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

    public TBox fingTboxByVin(String vin){
        return this.tBoxRepository.findByVin(vin);
    }


}
