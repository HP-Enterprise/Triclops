package com.hp.triclops.mqtt;

import com.alibaba.fastjson.JSON;
import com.hp.triclops.entity.Body;
import com.hp.triclops.entity.CommandParam;
import com.hp.triclops.entity.Head;
import com.hp.triclops.entity.MsgBean;
import com.hp.triclops.service.MsgHandler;
import com.hp.triclops.acquire.DataTool;
import com.hp.triclops.utils.RedisTool;
import org.eclipse.paho.client.mqttv3.*;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Iterator;
import java.util.Set;

/**
 * Created by jackl on 2016/11/11.
 */
public class MsgServerTask implements Runnable{

    private String host;
    private String port;
    private  String subscribeTopic;
    private  String publishTopicPrefix;
    private  String clientId ;
    private  String username;
    private  String password;
    private MsgHandler msgHandler;
    private RedisTool redisTool;
    private DataTool dataTool;

    private  String hostName;
    private static MqttClient client ;
    private static MemoryPersistence persistence;
    private Logger _logger = LoggerFactory.getLogger(MsgServerTask.class);

    public MsgServerTask() {
    }

    public MsgServerTask(String host, String port, String subscribeTopic, String publishTopicPrefix, String clientId, String username, String password, MsgHandler msgHandler, RedisTool redisTool,DataTool dataTool) {
        this.host = host;
        this.port = port;
        this.subscribeTopic = subscribeTopic;
        this.publishTopicPrefix = publishTopicPrefix;
        this.clientId = clientId;
        this.username = username;
        this.password = password;
        this.msgHandler = msgHandler;
        this.redisTool = redisTool;
        this.dataTool=dataTool;
    }

    @Override
    public void run() {
        subscribe();
        loadCommand();
    }
    public  String subscribe() {
        try {
            hostName="tcp://"+host+":"+port;
            //创建MqttClient
            client=new MqttClient(hostName,clientId);
            persistence = new MemoryPersistence();
            client.setCallback(new MqttCallback() {
                @Override
                public void connectionLost(Throwable throwable) {
                    _logger.info("MQTT连接断开");
                }

                @Override
                public void messageArrived(String s, MqttMessage message) throws Exception {
                    try {
                        String msg= message.toString();
                     //   replay("testtest","receive msg:"+msg);
                        msgHandler.handleReq(client,msg);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void deliveryComplete(IMqttDeliveryToken iMqttDeliveryToken) {
                }
            });
            MqttConnectOptions conOptions = new MqttConnectOptions();
            conOptions.setUserName(username);
            conOptions.setPassword(password.toCharArray());
            conOptions.setCleanSession(true);
            conOptions.setWill(subscribeTopic, "will msg".getBytes(), 1, true);
            client.connect(conOptions);
            client.subscribe(subscribeTopic, 1);
            boolean isSuccess =client.isConnected();
            if(isSuccess){
                _logger.info("已经成功连接到MQTT服务器.");
            }
            //client.disconnect();
        } catch (Exception e) {
            e.printStackTrace();
            return "failed";
        }
        return "success";
    }

    public void loadCommand(){
        while (true){
            try{
                Thread.sleep(10);//开发调试用
            }catch (InterruptedException e){e.printStackTrace(); }
            //读取数据库中所有的命令集合
            Set<String> setKey = redisTool.getKeysSet(dataTool.outCmdPreStr+"*");//read output:*
            if(setKey.size()>0){
                //_logger.info( setKey.size()+" message wait to be handle ");
            }
            Iterator keys = setKey.iterator();
            while (keys.hasNext()){
                //遍历待发数据,处理
                String k=(String)keys.next();
                handleCommand(k);
            }
        }
    }

    public void handleCommand(String key){
        //key>output:tricheer:868516020035370:106:1479264439091|msg>NAVIGATE,114.360734, 30.541093
        String msg =redisTool.popSetOneString(key);
        _logger.info("读取到控制指令key>" + key + "|msg>" + msg);
        String[] ss=key.split(":");
        String imei=ss[2];
        String command=ss[3];
        String sequenceId=ss[4];

        Body body=null;
        String replayMsg=null;
        if(command.equals("101")){
            replayMsg=buildResp(1,Long.parseLong(sequenceId),subscribeTopic,101,1,"",null);
        }else  if(command.equals("103")){
            replayMsg=buildResp(1,Long.parseLong(sequenceId),subscribeTopic,103,1,"",null);
        }else  if(command.equals("106")){
            String[] vals=msg.split(",");
            body=new Body();
            body.setOperate(vals[0]);
            CommandParam commandParam=new CommandParam();
            if(vals[0].equals("VIDEO")){
                commandParam.setTime(vals[1]);
            }else  if(vals[0].equals("NAVIGATE")){
                commandParam.setDest(vals[1]+","+vals[2]);
            }
            body.setParam(commandParam);
            replayMsg=buildResp(1,Long.parseLong(sequenceId),subscribeTopic,106,1,"",body);
        }
        String replayTopic=publishTopicPrefix+"/"+imei;
        if(replayMsg!=null) {
            replay(client, replayTopic, replayMsg);
        }
    }

    public String buildResp(int version,long id, String from,int code, int type, String msg,Body body){
        String replayStr=null;
        Head head=new Head(version,id,from,code,type,msg);

        MsgBean msgBean=new MsgBean(head,body);
        try {
            replayStr = JSON.toJSONString(msgBean);
        }catch (Exception e){e.printStackTrace();}
        return replayStr;
    }

    public  void replay(MqttClient client,String topic,String msg){
        try {
            MqttTopic _replayTopic = client.getTopic(topic);
            _logger.info("发往"+topic+"的消息:" + msg);
            MqttMessage message = new MqttMessage(msg.getBytes());
            message.setQos(0);
            MqttDeliveryToken token = _replayTopic.publish(message);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
