package com.hp.triclops.mqtt;

import com.hp.triclops.acquire.DataTool;
import com.hp.triclops.service.MsgHandler;
import com.hp.triclops.utils.RedisTool;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * Created by jackl on 2016/11/24.
 */
@Component
public class MqttMain {

    // 日志
    private Logger _logger;
    @Value("${lct.mqtt.host}")
    private String host;
    @Value("${lct.mqtt.port}")
    private String port;

    private  String hostName;
    @Value("${lct.mqtt.subscribeTopic}")
    private  String subscribeTopic;
    @Value("${lct.mqtt.publishTopicPrefix}")
    private  String publishTopicPrefix;
    @Value("${lct.mqtt.clientId}")
    private  String clientId ;
    @Value("${lct.mqtt.userName}")
    private  String username;
    @Value("${lct.mqtt.password}")
    private  String password;
    @Autowired
    private MsgHandler msgHandler;
    @Autowired
    private RedisTool redisTool;
    @Autowired
    private DataTool dataTool;

    ScheduledExecutorService dataHandlerScheduledService = Executors.newSingleThreadScheduledExecutor();


    public void start() throws Exception{
        this._logger = LoggerFactory.getLogger(MqttMain.class);
        //redisTool.deleteHashAllString(dataTool.onlineDeviceHash);//清理redis里面的全部连接记录
        dataHandlerScheduledService.schedule(new MsgServerTask(host,port,subscribeTopic,publishTopicPrefix,clientId,username,password,msgHandler,redisTool,dataTool),10, TimeUnit.MILLISECONDS);
    }
}
