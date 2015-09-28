package com.hp.triclops.acquire;

import com.hp.triclops.redis.SocketRedis;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;

/**
 * Created by luj on 2015/9/15.
 */
public class Sender extends Thread{


    private SocketRedis socketRedis;
    // 日志
    private Logger _logger;

    private DataTool dataTool;

    private HashMap<String,SocketChannel> socketChannels;
    public Sender(HashMap<String,SocketChannel> cs,SocketRedis s,DataTool dt){
        this.socketChannels=cs;
        this.socketRedis=s;
        this.dataTool=dt;
        this._logger = LoggerFactory.getLogger(Sender.class);
    }

    public synchronized void run()
    {

        while (true){
            try{
                Thread.sleep(1000);
            }catch (InterruptedException e){e.printStackTrace(); }
            _logger.info("connection count>>:" + socketChannels.keySet().size());
            //读取数据库中所有的命令集合
            Set<String> setKey = socketRedis.getKeysSet("output:*");
            if(setKey.size()>0){   _logger.info("size:" + setKey.size()); }
            Iterator keys = setKey.iterator();
            while (keys.hasNext()){
                //遍历待发数据,处理
                String k=(String)keys.next();
                String scKey=k.replace("output:", "");
                SendMesage(scKey,k);
            }
        }
    }
    public void SendMesage(String scKey,String k){
        //将output:{vin}对应的十六进制字符串发送给客户端
        try{
            String msg =socketRedis.popSetOneString(k);
            _logger.info("sckey>>" + scKey + "|send msg:" + msg);
            SocketChannel sc=socketChannels.get(scKey);
            if(sc!=null){
                //此处存在一个逻辑问题，对于已经确定知道客户端当前没有连接的消息如何处理，是依旧取出发送失败还是保留在redis中
                sc.write(dataTool.getByteBuffer(msg));
            }else{
                _logger.info("Connection is Dead");
                socketRedis.saveSetString(k, msg,-1);
            }

        }catch (IOException e){
            _logger.info(e.toString());
        }
    }
}
