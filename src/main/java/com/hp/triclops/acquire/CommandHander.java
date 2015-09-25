package com.hp.triclops.acquire;

import com.hp.triclops.redis.SocketRedis;
import io.netty.channel.Channel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;



/**
 * Created by luj on 2015/9/25.
 */
public class CommandHander extends Thread{
    //处理每一条下行指令
    private SocketRedis socketRedis;
    // 日志
    private Logger _logger;
    private DataTool dataTool;
    private Channel channel;
    private String message;
    public CommandHander(Channel ch,DataTool dt,String msg){
        this.channel=ch;
        this.dataTool=dt;
        this.message=msg;
        this._logger = LoggerFactory.getLogger(Sender.class);
    }
    public  void run()
    {
        _logger.info("Send message from CommandHander>>:" +message);
        channel.writeAndFlush(dataTool.getByteBuf(message));
        //发出一条命令可以知道当前的vin、eventID、messageID
        // 记录下这些信息，60s内后如果redis对应的messageID变化了表明已经处理完毕了 本线程自行了断，否则把记录放到redis里面
    }
}
