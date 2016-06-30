package com.hp.triclops.acquire;

import com.hp.triclops.entity.Position;
import com.hp.triclops.entity.RemoteControl;
import com.hp.triclops.entity.RemoteControlBody;
import com.hp.triclops.service.VehicleDataService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 处理api下发远程控制指令，验证连接，如果需要唤醒TBox
 * Created by jackl on 2016/6/6.
 */
public class RemoteCommandSender extends Thread{

    private VehicleDataService vehicleDataService;
    private int uid;
    private String vin;
    private short cType;
    private short acTmp;
    private Position position;
    private RemoteControlBody remoteControlBody;
    private Logger _logger;
    public RemoteCommandSender(VehicleDataService vehicleDataService,int uid,String vin,RemoteControlBody remoteControlBody){
        this.vehicleDataService=vehicleDataService;
        this.uid=uid;
        this.vin=vin;
        this.remoteControlBody=remoteControlBody;
        this._logger = LoggerFactory.getLogger(RemoteCommandSender.class);
    }

    public  void run()
    {
        _logger.info("handling remoteCommand...");
        _logger.info("remoteCommand:"+remoteControlBody.getVin()+"| type:"+remoteControlBody.getcType());
        RemoteControl rc=vehicleDataService.handleRemoteControl(uid, vin, remoteControlBody);
        if(rc!=null){
            //远程控制命令下发成功,执行结果会通过mqtt下发 以sessionId识别
            //ObjectResult obj = new ObjectResult("success",rc.getSessionId());
             _logger.info("Command send success  "+uid+"-"+ vin+"-"+cType+"-"+acTmp+"-"+position);
        }else{
            _logger.info("Command send failed"+uid+"-"+ vin+"-"+cType+"-"+acTmp+"-"+position);
        }
    }

}
