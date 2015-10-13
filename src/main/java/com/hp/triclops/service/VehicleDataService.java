package com.hp.triclops.service;

/**
 * Created by luj on 2015/10/12.
 */

import com.hp.triclops.acquire.AcquirePort;
import com.hp.triclops.acquire.DataTool;
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
    @Autowired
    DataTool dataTool;

    private Logger _logger = LoggerFactory.getLogger(VehicleDataService.class);


    public RemoteControl handleRemoteControl(int uid,String vin,short cType,short acTmp){
         //先检测是否有连接，如果没有连接。需要先执行唤醒，通知TBOX发起连接
        if(!hasConnection(vin)){
            _logger.info("vin:"+vin+" have not connection,do wake up...");
            remoteWakeUp(vin);
        }
        //唤醒可能成功也可能失败，只有连接建立才可以发送指令
        if(hasConnection(vin)){
            _logger.info("vin:"+vin+" have connection,sending command...");
            int eventId=dataTool.getCurrentSeconds();
            RemoteControl rc=new RemoteControl();
            rc.setUid(uid);
            rc.setSessionId(49+"-"+eventId);//根据application和eventid生成的session_id
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
            String byteStr=outputHexService.getRemoteControlHex(rc,eventId);
            _logger.info("command hex:"+byteStr);
            return rc;
        }
        return null;
        //命令下发成功，返回保存后的rc  否则返回null
    }

    public void remoteWakeUp(String vin){
        //远程唤醒动作
        _logger.info("doing wake up......");
        int count=0;
        while (count<3){
            //最多执行唤醒三次
            wakeup(vin);
            count++;
            try{
                Thread.sleep(1*1000);//唤醒后
            }catch (InterruptedException e){e.printStackTrace(); }
            //检测连接是否已经建立
            if(hasConnection(vin)){
                return;
            }
        }
      }

    private void wakeup(String vin){
        //本部分代码为调用外部唤醒接口
        _logger.info(" wake up tbox"+vin);
    }

    private boolean hasConnection(String vin){
        //检测对应vin是否有连接可用
        boolean re=false;
        Channel ch=AcquirePort.channels.get(vin);
        if(ch!=null){
            re=true;
        }
        return re;
    }

}
