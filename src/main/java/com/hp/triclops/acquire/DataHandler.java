package com.hp.triclops.acquire;

import com.hp.triclops.redis.SocketRedis;
import com.hp.triclops.service.DataHandleService;
import com.hp.triclops.utils.DateUtil;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
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
    private String serverId;
    private ScheduledExecutorService scheduledService;
    private int heartbeatInterval;
    private int heartbeatTTL;
    private int heartInterval;
    private int heartTTL;
    private ConcurrentHashMap<String,Channel> channels;
    private ConcurrentHashMap<String,String> hearts;
    private int dataHandlerMaxCount;
    public DataHandler(ConcurrentHashMap<String,Channel> channels,ConcurrentHashMap<String,String> hearts, SocketRedis s,DataHandleService dataHandleService,String keySuffix,int heartbeatInterval,int heartbeatTTL,int heartInterval,int heartTTL,DataTool dt,ScheduledExecutorService scheduledService, String serverId, int dataHandlerMaxCount){
        this.channels = channels;
        this.hearts = hearts;
        this.socketRedis=s;
        this.dataHandleService=dataHandleService;
        this.keySuffix=keySuffix;
        this.heartbeatInterval=heartbeatInterval;
        this.heartbeatTTL=heartbeatTTL;
        this.heartInterval=heartInterval;
        this.heartTTL=heartTTL;
        this.dataTool=dt;
        this.scheduledService=scheduledService;
        this.serverId=serverId;
        this.dataHandlerMaxCount = dataHandlerMaxCount;
        this._logger = LoggerFactory.getLogger(DataHandler.class);
        _logger.info(">>>>>>>>>>start data Handler handle key->:input" + keySuffix);
        if(!keySuffix.equals("")){
            if(keySuffix.equals("HEART")){//心跳监控线程
                //启动handle 心跳
                int heartbeat_delay=heartbeatInterval*1000;//Heartbeat T millSeconds
                scheduledService.scheduleWithFixedDelay(new DataHandleHeartTask(heartTTL),0,heartbeat_delay,TimeUnit.MILLISECONDS);
                _logger.info(">>>>>>>>>>start data Handler heart :" + keySuffix + ">>>>>>>>>>ttl_seconds:" + heartTTL);
            }else{
                //启动handle 心跳
                int heartbeat_delay=heartbeatInterval*1000;//Heartbeat T millSeconds
                long ttl_seconds=heartbeatTTL;//seconds
                scheduledService.scheduleWithFixedDelay(new DataHandleHeartbeat(keySuffix,ttl_seconds),0,heartbeat_delay,TimeUnit.MILLISECONDS);
                _logger.info(">>>>>>>>>>start data Handler heartbeat :" + keySuffix + ">>>>>>>>>>ttl_seconds:" + ttl_seconds);
            }
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
                String key = (String)keys.next();
                handleInputData(key);
            }
        }
    }

    public void handleInputData(String key) {
        // 将input:{vin}对应的十六进制字符串解析保存入db
//        String msg = socketRedis.popListString(key);
//        _logger.info("dataType>>" + dataType + "|receive vin:msg>>" + msg);
        try {
            List<String> msgList = socketRedis.popListString(key, dataHandlerMaxCount);

            String dataTypeString = dataTool.getVinFromkey(key);
            byte dataType = Byte.valueOf(dataTypeString);
            scheduledService.schedule(new DataHandlerTask(dataType, socketRedis, dataHandleService, dataTool, msgList), 1, TimeUnit.MILLISECONDS);
        } catch (Exception ex) {
            _logger.info("这里可能读取到了VIN码");
            ex.printStackTrace();
        }
    }

    class DataHandleHeartbeat implements Runnable{
        private String key;
        private long ttl_seconds;

        public DataHandleHeartbeat(String key,long ttl_seconds){
            this.key=key;
            this.ttl_seconds=ttl_seconds;
        }
        @Override
        public synchronized void run() {
            socketRedis.saveSetString("available-data-handler",key,ttl_seconds);
        }
    }

    class DataHandleHeartTask implements Runnable{
        private long ttl_seconds;

        public DataHandleHeartTask(long ttl_seconds){
            this.ttl_seconds=ttl_seconds;
        }
        @Override
        public synchronized void run() {
            //处理心跳数据
            Set<String> setKey = hearts.keySet();
            Date date = new Date();
            Iterator keys = setKey.iterator();
            while (keys.hasNext()){
                //遍历所有的心跳数据
                String vin = (String)keys.next();
                String conn = socketRedis.getHashString(dataTool.connection_hashmap_name, vin);
                //判断连接是否存在
                if(conn != null && !"".equals(conn)){
                    String _serverId = conn.split("-")[0];
                    //只有当连接处于该机器时才处理
                    if(serverId.equals(_serverId)){
                        //获取上一次心跳时间
                        String dateTime = hearts.get(vin);
                        if(dateTime != null && !"".equals(dateTime)){
                            Date redisDate = DateUtil.parseStrToDate(dateTime, "yyyy-MM-dd HH:mm:ss");
                            long times = (date.getTime() - redisDate.getTime())/1000;//计算当前时间与上次一心跳时间差(单位秒)
                            //当心跳间隔超出时间，平台主动断开连接
                            if(times > ttl_seconds){
                                Channel channel = channels.get(vin);
                                _logger.info("监听心跳:上一次心跳时间[" + dateTime + "] 连接信息:" + conn);
                                if(channel!=null){//需要在旧的addr被移除后才能close
                                    ChannelFuture future = channel.close();
                                    future.addListener(new ChannelFutureListener() {
                                        @Override
                                        public void operationComplete(ChannelFuture future) {
                                            if(future.isDone()){
                                                hearts.remove(vin);
                                            }
                                        }
                                    });
                                }
                            }
                        }
                    }else{
                        //连接不在该服务器时直接移除心跳信息
                        hearts.remove(vin);
                    }
                }else{
                    //连接不存在时直接移除心跳信息
                    hearts.remove(vin);
                }
            }
        }
    }

}
