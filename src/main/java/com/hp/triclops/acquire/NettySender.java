package com.hp.triclops.acquire;

import com.hp.triclops.redis.SocketRedis;
import io.netty.channel.Channel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;

/**
 * Created by luj on 2015/9/22.
 */
public class NettySender extends Thread{
    private SocketRedis socketRedis;
    // 日志
    private Logger _logger;

    private DataTool dataTool;

    private HashMap<String,Channel> channels;
    public NettySender(HashMap<String,Channel> cs,SocketRedis s,DataTool dt){
        this.channels=cs;
        this.socketRedis=s;
        this.dataTool=dt;
        this._logger = LoggerFactory.getLogger(Sender.class);
    }
    public  void run()
    {

        while (true){
            try{
                Thread.sleep(3000);
            }catch (InterruptedException e){e.printStackTrace(); }
            _logger.info("connection count>>:" + channels.keySet().size());
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
        String msg =socketRedis.popOneString(k);
        _logger.info("sckey>>" + scKey + "|send msg:" + msg);
        Channel ch=channels.get(scKey);
        if(ch!=null){
            //此处存在一个逻辑问题，对于已经确定知道客户端当前没有连接的消息如何处理，是依旧取出发送失败还是保留在redis中
            ch.writeAndFlush(dataTool.getByteBuf(msg));
        }else{
            _logger.info("Connection is Dead");
            socketRedis.saveString(k, msg);
        }
    }
}
