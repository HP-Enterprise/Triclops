package com.hp.triclops.acquire;

import com.hp.triclops.redis.SocketRedis;
import io.netty.channel.Channel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;


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
    private String vin;
    private String eventId;
    private String messageId;
    private String applicationId;
    private HashMap<String,Object> datas;

    private String msgSendCount_preStr="msgSendCount:";
    private String msgCurrentStatusValue="msgCurrentStatusValue:";

    public CommandHander(Channel ch,String vin,SocketRedis socketRedis,DataTool dt,String msg){
        this.channel=ch;
        this.vin=vin;
        this.socketRedis=socketRedis;
        this.dataTool=dt;
        this.message=msg;
        this._logger = LoggerFactory.getLogger(Sender.class);
    }

    public  void run()
    {

        _logger.info("Send message from CommandHander>>:" + message);
        _logger.info("vin:>>>>" + vin);

        //发出一条命令可以知道当前的vin、eventID、messageID、applicationId等信息
        datas=dataTool.getApplicationIdAndMessageIdFromDownBytes(message);
        applicationId=String.valueOf(datas.get("applicationId"));
        eventId=String.valueOf(datas.get("eventId"));
        messageId=String.valueOf(datas.get("messageId"));
        channel.writeAndFlush(dataTool.getByteBuf(message));
        //发出消息，redis记录已发次数
        setSendCountPlusOne(vin,eventId,messageId);
        String statusKey=msgCurrentStatusValue+vin+"-"+applicationId+"-"+eventId;
        String statusValue=messageId;
        //已经取到可以标识 跟踪一条指令的信息

        _logger.info("statusKey:" + statusKey + "|statusValue:" + statusValue);
        //通过Redis来跟踪消息状态
        socketRedis.saveValueString(statusKey, statusValue,-1);//此处可以考虑设置一个合适的TTL
        // 记录下这些信息，应答超时时间过后内后如果redis对应的messageID变化了表明已经处理完毕了 本线程自行了断，否则把记录放到redis里面

        int timeOutSeconds=60;//通过applicatinoId取到应答超时时间
        try{
            Thread.sleep(timeOutSeconds*1000);
        }catch (InterruptedException e){e.printStackTrace(); }
        //timeOutSeconds秒后操作

        String currentValue=socketRedis.getValueString(statusKey);
        _logger.info("currentStatusValue:" + currentValue);
        if(!currentValue.equals(statusValue)){
            _logger.info("Client has received the command successfully! So I have nothing to do!");
        }else{
            _logger.info("maybe I need Resend the Command!");
            //将数据放入redis，作为另外一条命令处理，本次处理结束
            //在此之前需要判断重发次数是否已经达到
            int maxSendCount=3;//基于applicationId-messageId和参考文档得出同一event最多发送的次数
            int sendCount=getCurrentSendCount(vin, eventId, messageId);//从redis取出，这一event已经发了的次数
            if(sendCount<maxSendCount){
                _logger.info("sendCount"+sendCount+"<maxSendCount"+maxSendCount+",ReTry>>");
                socketRedis.saveSetString("output:" + vin,message,-1);
                //这里是原样将消息再次发送还是重新设置SendTime待考虑，暂时原样重发，无论如何EventId应该是一样的
            }else{
                _logger.info("max send count reached!");
            }

        }

    }

    private int getCurrentSendCount(String vin,String eventId,String messageId){
        String  redisKey=msgSendCount_preStr+vin+"-"+eventId+"-"+messageId;
        int re=0;
        String currentValue=socketRedis.getValueString(redisKey);
        if(!currentValue.equals("null")){
            re=Integer.parseInt(currentValue);
        }
        return re;
    }
    private void setSendCountPlusOne(String vin,String eventId,String messageId){
        String  redisKey=msgSendCount_preStr+vin+"-"+eventId+"-"+messageId;
        int currentResendCount=getCurrentSendCount(vin, eventId, messageId);
        String newCountValue=String.valueOf(currentResendCount+1);
        socketRedis.saveValueString(redisKey,newCountValue,-1);

    }
}
