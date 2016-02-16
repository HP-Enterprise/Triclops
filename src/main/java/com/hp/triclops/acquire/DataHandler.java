package com.hp.triclops.acquire;

import com.hp.triclops.redis.SocketRedis;
import com.hp.triclops.service.DataHandleService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Date;
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
    private String keySuffix;
    private ScheduledExecutorService scheduledService;
    private int heartbeatInterval;
    private int heartbeatTTL;
    public DataHandler(SocketRedis s,DataHandleService dataHandleService,String keySuffix,int heartbeatInterval,int heartbeatTTL,DataTool dt,ScheduledExecutorService scheduledService){
        this.socketRedis=s;
        this.dataHandleService=dataHandleService;
        this.keySuffix=keySuffix;
        this.heartbeatInterval=heartbeatInterval;
        this.heartbeatTTL=heartbeatTTL;
        this.dataTool=dt;
        this.scheduledService=scheduledService;
        this._logger = LoggerFactory.getLogger(DataHandler.class);
        _logger.info(">>>>>>>>>>start data Handler handle key->:input" + keySuffix);
        if(!keySuffix.equals("")){
            //启动handle 心跳
            int heartbeat_delay=heartbeatInterval*1000;//Heartbeat T millSeconds
            long ttl_seconds=heartbeatTTL;//seconds
            scheduledService.scheduleWithFixedDelay(new DataHandleHeartbeat(keySuffix,ttl_seconds),0,heartbeat_delay,TimeUnit.MILLISECONDS);
            _logger.info(">>>>>>>>>>start data Handler heartbeat :" + keySuffix);
        }
      }

    public  synchronized void run()
    {
        while (true){

            //读取数据库中所有的数据集合
            String redisKeyFilter="input"+keySuffix+":*";
            if(keySuffix.equals("")){ //handle all input data
                redisKeyFilter="input*";
            }
            Set<String> setKey = socketRedis.getKeysSet(redisKeyFilter);
            if(setKey.size()>0){   _logger.info(redisKeyFilter+" size:" + setKey.size()); }
            Iterator keys = setKey.iterator();
            while (keys.hasNext()){
                //遍历待发数据,处理
                String k=(String)keys.next();
                String vin=dataTool.getVinFromkey(k);
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

    class DataHandleHeartbeat implements   Runnable{
        private String key;
        private long ttl_seconds;
        public DataHandleHeartbeat(String key,long ttl_seconds){
            this.key=key;
            this.ttl_seconds=ttl_seconds;
        }
    @Override
    public void run() {
        socketRedis.saveSetString("available-data-handler",key,ttl_seconds);
        //System.out.println(new Date().toLocaleString()+" DataHandleHeartbeat:"+key);
    }
}

}
