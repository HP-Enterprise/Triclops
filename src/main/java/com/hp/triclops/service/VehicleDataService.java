package com.hp.triclops.service;

/**
 * Created by luj on 2015/10/12.
 */

import com.hp.triclops.entity.RemoteControl;
import com.hp.triclops.repository.RemoteControlRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;


/**
 * 车辆控制相关业务代码
 */
@Service
public class VehicleDataService {
    @Autowired
    RemoteControlRepository remoteControlRepository;
    public boolean handleRemoteControl(int uid,String vin,short cType,short acTmp){
        RemoteControl rc=new RemoteControl();
        rc.setUid(uid);
        rc.setVin(vin);
        rc.setSendingTime(new Date());
        rc.setControlType(cType);
        rc.setAcTemperature(acTmp);
        rc.setStatus((short) 0);
        rc.setRemark("");
        remoteControlRepository.save(rc);
        System.out.println("save RemoteControl to db");
        //保存到数据库
        //产生数据包hex入redis
        return true;
    }
}
