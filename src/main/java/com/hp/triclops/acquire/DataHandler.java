package com.hp.triclops.acquire;

import com.hp.triclops.redis.SocketRedis;
import com.hp.triclops.service.DataHandleService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * Created by luj on 2015/9/28.
 */
public class DataHandler extends Thread{

    private SocketRedis socketRedis;
    private Logger _logger;
    private DataTool dataTool;
    private DataHandleService dataHandleService;
    private ScheduledExecutorService scheduledService;

    public DataHandler(SocketRedis s,DataHandleService dataHandleService,DataTool dt,ScheduledExecutorService scheduledService){
        this.socketRedis=s;
        this.dataHandleService=dataHandleService;
        this.dataTool=dt;
        this.scheduledService=scheduledService;
        this._logger = LoggerFactory.getLogger(DataHandler.class);

    }

    public  synchronized void run()
    {
        while (true){
            try{
                Thread.sleep(10);//开发调试用
            }catch (InterruptedException e){e.printStackTrace(); }
            Map<Thread, StackTraceElement[]> maps = Thread.getAllStackTraces();
            //读取数据库中所有的数据集合
            Set<String> setKey = socketRedis.getKeysSet("input:*");
            if(setKey.size()>0){   _logger.info("size:" + setKey.size()); }
            Iterator keys = setKey.iterator();
            while (keys.hasNext()){
                //遍历待发数据,处理
                String k=(String)keys.next();
                String vin=k.replace("input:", "");
                handleInputData(vin, k);
            }
        }
    }

    public void handleInputData(String vin,String k){
        //将input:{vin}对应的十六进制字符串解析保存入db
        String msg =socketRedis.popSetOneString(k);
        _logger.info("vin>>" + vin + "|receive msg:" + msg);
        scheduledService.schedule(new DataHandlerTask(vin,socketRedis,dataHandleService,dataTool,msg), 1, TimeUnit.MILLISECONDS);
    }



}
