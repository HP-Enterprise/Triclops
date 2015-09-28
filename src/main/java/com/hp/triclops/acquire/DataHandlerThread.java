package com.hp.triclops.acquire;

import com.hp.triclops.redis.SocketRedis;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by luj on 2015/9/28.
 */
public class DataHandlerThread  extends Thread{
    private SocketRedis socketRedis;
    private String vin;
    private Logger _logger;
    private DataTool dataTool;
    private String msg;
    private String key;
    public DataHandlerThread(String vin, SocketRedis socketRedis, DataTool dt, String k){
        this.vin=vin;
        this.socketRedis=socketRedis;
        this.dataTool=dt;
        this.key=k;
        this._logger = LoggerFactory.getLogger(DataHandler.class);
    }
    public  void run()
    {
        HandleMessage(vin,key);
    }

    public void HandleMessage(String vin,String k){
        //将input:{vin}对应的十六进制字符串处理入库
        String msg =socketRedis.popSetOneString(k);
        _logger.info("handle msg>>>>>"+k+":"+msg);
    }
}
