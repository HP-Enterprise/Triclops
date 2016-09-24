package com.hp.triclops.acquire;

import com.hp.triclops.redis.SocketRedis;
import io.netty.channel.Channel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by luj on 2015/9/22.
 */
public class NettySender extends Thread{
    private SocketRedis socketRedis;
    // 日志
    private Logger _logger;

    private DataTool dataTool;

    private ConcurrentHashMap<String,Channel> channels;
    public NettySender(ConcurrentHashMap<String,Channel> cs,SocketRedis s,DataTool dt){
        this.channels=cs;
        this.socketRedis=s;
        this.dataTool=dt;
        this._logger = LoggerFactory.getLogger(NettySender.class);
    }
    public  synchronized void run()
    {

        while (true){
            try{
                Thread.sleep(10);//开发调试用
            }catch (InterruptedException e){e.printStackTrace(); }
            Map<Thread, StackTraceElement[]> maps = Thread.getAllStackTraces();
            //_logger.info("Connection count>>:" + channels.keySet().size()+"|Thread count>>:" + maps.size());
            //读取数据库中所有的命令集合
            Set<String> setKey = socketRedis.getKeysSet("output:*");
            if(setKey.size()>0){   _logger.info( setKey.size()+" message wait to be handle "); }
            Iterator keys = setKey.iterator();
            while (keys.hasNext()){
                //遍历待发数据,处理
                String k=(String)keys.next();
                String scKey=k.replace("output:", "");
                SendMessage(scKey,k);
            }
        }
    }
    public void SendMessage(String scKey,String k){
        //将output:{vin}对应的十六进制字符串发送给客户端
        String msg =socketRedis.popSetOneString(k);
        _logger.info("sckey>>" + scKey + "|send msg:" + msg);
        Channel ch=channels.get(scKey);
        if(ch!=null){
            //此处存在一个逻辑问题，对于已经确定知道客户端当前没有连接的消息如何处理，是依旧取出发送失败还是保留在redis中
            //ch.writeAndFlush(dataTool.getByteBuf(msg));
            new CommandHandler(ch,scKey,socketRedis,dataTool,msg).start();
        }else{
            //一般情况下，不会出现此种情况，出现此情况是由于连接判断和连接实际情况不一致导致，比如异常的连接断开单服务端没有收到任何信息，依然认为连接可用。
            _logger.info("Connection is Dead"+scKey);
            socketRedis.deleteHashString(dataTool.connection_hashmap_name,scKey);//从redis里面清除连接记录
            //socketRedis.saveSetString(k, msg,-1);发送时异常不返回redis，执行结果依靠响应数据来判断
        }
    }
}
