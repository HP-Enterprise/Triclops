package com.hp.triclops.service;

/**
 * Created by luj on 2015/10/12.
 */

import com.hp.triclops.acquire.AcquirePort;
import com.hp.triclops.entity.RemoteControl;
import com.hp.triclops.repository.RemoteControlRepository;
import io.netty.channel.Channel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
    @Autowired
    OutputHexService outputHexService;

    private Logger _logger = LoggerFactory.getLogger(VehicleDataService.class);

    public boolean handleRemoteControl(int uid,String vin,short cType,short acTmp){
        _logger.info(">>>>>>>连接数:" + AcquirePort.channels.size());

        Channel ch=AcquirePort.channels.get(vin);
        if(ch!=null){
            //检测对应vin是否存在长连接，存在下发数据，不存在执行唤醒
            _logger.info("vin:"+vin+" have not connection,do wake up...");
        }else{
            _logger.info("vin:"+vin+" have connection,sending command...");
            RemoteControl rc=new RemoteControl();
            rc.setUid(uid);
            rc.setVin(vin);
            rc.setSendingTime(new Date());
            rc.setControlType(cType);
            rc.setAcTemperature(acTmp);
            rc.setStatus((short) 0);
            rc.setRemark("");
            remoteControlRepository.save(rc);
            _logger.info("save RemoteControl to db");
            //保存到数据库
            //产生数据包hex并入redis
            String byteStr=outputHexService.getRemoteControlHex(rc);
            _logger.info("command hex:"+byteStr);
        }
        return true;
    }

    public void remoteWakeUp(){



    }


}
