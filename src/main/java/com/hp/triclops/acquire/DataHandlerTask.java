package com.hp.triclops.acquire;

import com.hp.triclops.redis.SocketRedis;
import com.hp.triclops.service.DataHandleService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by luj on 2015/9/28.
 */
public class DataHandlerTask implements Runnable{
    private SocketRedis socketRedis;
    private DataHandleService dataHandleService;
    private String vin;
    private Logger _logger;
    private DataTool dataTool;
    private String msg;
    public DataHandlerTask(String vin, SocketRedis socketRedis, DataHandleService dataHandleService, DataTool dt, String msg){
        this.vin=vin;
        this.socketRedis=socketRedis;
        this.dataHandleService=dataHandleService;
        this.dataTool=dt;
        this.msg=msg;
        this._logger = LoggerFactory.getLogger(DataHandlerTask.class);
    }
    public  synchronized void run()
    {
        long startTime = System.currentTimeMillis();
        HandleMessage(vin,msg);
        long time = System.currentTimeMillis() - startTime;
        if (time > 100)
            _logger.info("Handle msg time:" + time);
    }

    public void HandleMessage(String vin,String message){
        //将input:{vin}对应的十六进制字符串处理入库
        _logger.info("[0x21][0x22][0x23][0x24][0x25][0x28][0x29]处理数据>>>>>:" + message);
        if(msg!=null&&!msg.equalsIgnoreCase("null")){
            dataHandleService.saveMessage(vin,message);
        }
    }
}
