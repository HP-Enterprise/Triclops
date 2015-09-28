package com.hp.triclops.acquire;

import com.hp.triclops.redis.SocketRedis;
import io.netty.channel.Channel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Iterator;
import java.util.Map;
import java.util.Set;

/**
 * Created by luj on 2015/9/28.
 */
public class DataHandler extends Thread{

    private SocketRedis socketRedis;
    private Logger _logger;
    private DataTool dataTool;

    public DataHandler(SocketRedis s,DataTool dt){
        this.socketRedis=s;
        this.dataTool=dt;
        this._logger = LoggerFactory.getLogger(DataHandler.class);

    }

    public  void run()
    {
        while (true){
            try{
                Thread.sleep(3000);//开发调试用
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
                new DataHandlerThread(vin,socketRedis,dataTool,k).start();
            }
        }
    }



}