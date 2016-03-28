package com.hp.triclops.management;

import com.hp.triclops.entity.TBoxEx;
import com.hp.triclops.repository.TBoxExRepository;
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
            return new TBoxExShow();
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
}