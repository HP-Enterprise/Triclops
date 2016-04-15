package com.hp.triclops.service;

import com.hp.triclops.entity.TBox;
import com.hp.triclops.entity.TBoxEx;
import com.hp.triclops.entity.Vehicle;
import com.hp.triclops.repository.TBoxExRepository;
import com.hp.triclops.repository.TBoxRepository;
import com.hp.triclops.repository.VehicleRepository;
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
    @Autowired
    VehicleRepository vehicleRepository;
    @Autowired
    TBoxExRepository tBoxExRepository;
    public boolean activationTBox(String vin,String t_sn){
        TBox tb=tBoxRepository.findByVinAndT_sn(vin,t_sn);
        //先查询有没有记录，有记录则激活没有记录新增记录
        if(tb!=null){
            tb.setIs_activated(1);
            tb.setActivation_time(new Date());
            tBoxRepository.save(tb);
            return true;
        }else{
            Vehicle vehicle=new Vehicle();
            vehicle.setVin(vin);
            vehicle.setProduct_date(new Date(0));
            vehicle.setTboxsn(t_sn);
            vehicle.setSecurity_pwd("123456");
            vehicle.setSecurity_salt("");
            Vehicle sVehicle=vehicleRepository.save(vehicle);
            if(sVehicle!=null){
                TBoxEx tBox=new TBoxEx();
                tBox.setVid(sVehicle.getId());
                tBox.setVin(vin);
                tBox.setT_sn(t_sn);
                tBox.setIs_activated(1);
                tBox.setActivation_time(new Date());
                tBoxExRepository.save(tBox);
            }
            return true;
        }
    }
}
