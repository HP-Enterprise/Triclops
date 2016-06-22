package com.hp.triclops.acquire;

import com.hp.triclops.entity.Position;
import com.hp.triclops.entity.RemoteControl;
import com.hp.triclops.service.VehicleDataService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 远程诊断只需要下发短信即可，参照协议0625
 * Created by jackl on 2016/6/6.
 */
public class RemoteDiagnosticSender extends Thread{

    private VehicleDataService vehicleDataService;
    private int uid;
    private String vin;
    private Logger _logger;
    public RemoteDiagnosticSender(VehicleDataService vehicleDataService, String vin){
        this.vehicleDataService=vehicleDataService;
        this.vin=vin;
        this._logger = LoggerFactory.getLogger(RemoteDiagnosticSender.class);
    }

    public  void run()
    {

        try{
            vehicleDataService.wakeup(vin);
            _logger.info("Command send success");
        }catch (Exception e){
            _logger.info("Command send failed");
        }
    }

}
