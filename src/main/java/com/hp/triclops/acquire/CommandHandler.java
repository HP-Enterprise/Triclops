package com.hp.triclops.acquire;

import com.hp.triclops.redis.SocketRedis;
import com.hp.triclops.service.OutputHexService;
import io.netty.channel.Channel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;


/**
 * Created by luj on 2015/9/25.
 */
public class CommandHandler extends Thread {
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
    private HashMap<String, Object> datas;
    private String serverId;
    private OutputHexService outputHexService;

    public CommandHandler(Channel ch, String vin, SocketRedis socketRedis, DataTool dt, OutputHexService outputHexService, String serverId, String msg) {
        this.channel = ch;
        this.vin = vin;
        this.socketRedis = socketRedis;
        this.dataTool = dt;
        this.outputHexService = outputHexService;
        this.serverId = serverId;
        this.message = msg;
        this._logger = LoggerFactory.getLogger(CommandHandler.class);
    }

    public void run() {
        //发出一条命令可以知道当前的vin、eventID、messageID、applicationId等信息
        datas = dataTool.getApplicationIdAndMessageIdFromDownBytes(message);
        applicationId = String.valueOf(datas.get("applicationId"));
        eventId = String.valueOf(datas.get("eventId"));
        messageId = String.valueOf(datas.get("messageId"));

        int maxSendCount = dataTool.getMaxSendCount(applicationId, messageId);//基于applicationId-messageId和参考文档得出同一event最多发送的次数
        int sendCount = getCurrentSendCount(vin, eventId, messageId);//从redis取出，这一event已经发了的次数
        if (sendCount < maxSendCount) {
            _logger.info("[0x31]vin:" + vin + " 准备发送报文>>:" + message);
            channel.writeAndFlush(dataTool.getByteBuf(message));
        } else {
            _logger.info("[0x31]最大重发次数" + maxSendCount + "达到!");
            outputHexService.handleFailedData(vin, eventId, applicationId, messageId);
            return;
        }

        //发出消息，redis记录已发次数
        setSendCountPlusOne(vin, eventId, messageId);
        String statusKey = DataTool.msgCurrentStatus_preStr + vin + "-" + applicationId + "-" + eventId;
        String statusValue = messageId;
        //已经取到可以标识 、跟踪一条指令的信息

        _logger.info("set status value>statusKey:" + statusKey + "|statusValue:" + statusValue);
        //通过Redis来跟踪消息状态
        socketRedis.saveValueString(statusKey, statusValue, DataTool.msgCurrentStatus_ttl);//此处可以考虑设置一个合适的TTL
        // 记录下这些信息，应答超时时间过后内后如果redis对应的messageID变化了表明已经处理完毕了 本线程自行了断，否则把记录放到redis里面

        int timeOutSeconds = dataTool.getTimeOutSeconds(applicationId, messageId);//通过applicatinoId取到应答超时时间
        try {
            Thread.sleep(timeOutSeconds * 1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        //timeOutSeconds秒后操作

        String currentValue = socketRedis.getValueString(statusKey);
        _logger.info("statusValue:" + statusValue + "==currentStatusValue" + currentValue + "?");
        if (!currentValue.equals(statusValue)) {
            _logger.info("Client has received the command successfully! So I have nothing to do!");
        } else {
            _logger.info("[0x31]发出的报文没有应答，准备重发!");
            //将数据放入redis，作为另外一条命令处理，本次处理结束
            //在此之前需要判断重发次数是否已经达到
            sendCount = getCurrentSendCount(vin, eventId, messageId);//从redis取出，这一event已经发了的次数
            if (sendCount < maxSendCount) {
                _logger.info("[0x31]已重试次数" + sendCount + "<最大重试" + maxSendCount + ",正在重发...");
                socketRedis.saveSetString(serverId + "-" + dataTool.out_cmd_preStr + vin, message, -1);
                //这里是原样将消息再次发送还是重新设置SendTime待考虑，暂时原样重发，无论如何EventId应该是一样的
            } else {
                _logger.info("[0x31]最大重发次数" + maxSendCount + "达到!");
                outputHexService.handleFailedData(vin, eventId, applicationId, messageId);
            }

        }

    }

    private int getCurrentSendCount(String vin, String eventId, String messageId) {
        //取已发送次数
        String redisKey = DataTool.msgSendCount_preStr + vin + "-" + applicationId + "-" + eventId + "-" + messageId;
        int re = 0;
        String currentValue = socketRedis.getValueString(redisKey);
        if (!currentValue.equals("null")) {
            re = Integer.parseInt(currentValue);
        }
        return re;
    }

    private void setSendCountPlusOne(String vin, String eventId, String messageId) {
        //发送次数加1
        String redisKey = DataTool.msgSendCount_preStr + vin + "-" + applicationId + "-" + eventId + "-" + messageId;
        int currentResendCount = getCurrentSendCount(vin, eventId, messageId);
        String newCountValue = String.valueOf(currentResendCount + 1);
        socketRedis.saveValueString(redisKey, newCountValue, DataTool.msgSendCount_ttl);

    }
}
