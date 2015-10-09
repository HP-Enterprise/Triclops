package com.hp.triclops.service;

import com.hp.triclops.entity.TBox;
import com.hp.triclops.repository.TBoxRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Date;

/**
 * Created by luj on 2015/10/9.
 */
@Component
public class TboxService {
    @Autowired
    TBoxRepository tBoxRepository;
    public boolean activationTBox(String vin,String t_sn){
        TBox tb=tBoxRepository.findByVinAndT_sn(vin,t_sn);
        if(tb!=null){
            tb.setIs_activated(1);
            tb.setActivation_time(new Date());
            tBoxRepository.save(tb);
            return true;
        }else{
            return false;
        }
    }
}
