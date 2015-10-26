package com.hp.triclops.acquire;

import com.hp.triclops.redis.SocketRedis;
import com.hp.triclops.service.DataHandleService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by luj on 2015/9/28.
 */
public class DataHandlerThread  extends Thread{
    private SocketRedis socketRedis;
    private DataHandleService dataHandleService;
    private String vin;
    private Logger _logger;
    private DataTool dataTool;
    private String msg;
    public DataHandlerThread(String vin, SocketRedis socketRedis, DataHandleService dataHandleService,DataTool dt, String msg){
        this.vin=vin;
        this.socketRedis=socketRedis;
        this.dataHandleService=dataHandleService;
        this.dataTool=dt;
        this.msg=msg;
        this._logger = LoggerFactory.getLogger(DataHandlerThread.class);
    }
    public  synchronized void run()
    {
        HandleMessage(vin,msg);
    }

    public void HandleMessage(String vin,String message){
        //将input:{vin}对应的十六进制字符串处理入库
        _logger.info("handle msg>>>>>:" + message);
        if(msg!=null&&!msg.equalsIgnoreCase("null")){
            dataHandleService.saveMessage(vin,message);
        }
    }
}
