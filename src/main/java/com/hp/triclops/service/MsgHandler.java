package com.hp.triclops.service;

import com.alibaba.fastjson.JSON;
import com.hp.triclops.entity.*;
import com.hp.triclops.repository.LctAppVersionRepository;
import com.hp.triclops.repository.LctRemoteControlRepository;
import com.hp.triclops.repository.LctRepository;
import com.hp.triclops.repository.LctStatusDataRepository;
import com.hp.triclops.acquire.DataTool;
import com.hp.triclops.utils.DateUtil;
import com.hp.triclops.utils.RedisTool;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.eclipse.paho.client.mqttv3.MqttTopic;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by jackl on 2016/11/11.
 */

@Component
public class MsgHandler {

    @Value("${lct.mqtt.publishTopicPrefix}")
    private  String publishTopicPrefix;
    @Value("${lct.mqtt.subscribeTopic}")
    private  String subscribeTopic;
    @Autowired
    private RedisTool redisTool;
    @Autowired
    private DataTool dataTool;
    @Autowired
    LctRepository lctRepository;
    @Autowired
    LctStatusDataRepository lctStatusDataRepository;
    @Autowired
    LctAppVersionRepository lctAppVersionRepository;
    @Autowired
    LctRemoteControlRepository lctRemoteControlRepository;



    private Logger _logger = LoggerFactory.getLogger(MsgHandler.class);

    public String handleReq (MqttClient client,String msg){
        _logger.info("收到的消息:" +msg);
        String replayMsg=null;
        LctMsgBean bean =null;
        try {
            bean = JSON.parseObject(msg, LctMsgBean.class);
        }catch (Exception e){
            //e.printStackTrace();
        }
        if(bean!=null){
            int version=bean.getHead().getVersion();
            long id=bean.getHead().getId();
            String from=bean.getHead().getFrom();
            int code=bean.getHead().getCode();
            int type=bean.getHead().getType();
            String imei="";
            switch (code){
                case 1:
                     imei=bean.getBody().getImei();
                    _logger.info("设备"+imei+"上线");
                    redisTool.saveHashString(dataTool.onlineDeviceHash,imei,imei,-1);
                     replayMsg=buildResp(version,id,subscribeTopic,code,2,"OK",null);
                    break;
                case 2:
                case 3:
                     imei=bean.getBody().getImei();
                    _logger.info("设备"+imei+"下线");
                    redisTool.deleteHashString(dataTool.onlineDeviceHash, imei);
                    replayMsg=buildResp(version,id,subscribeTopic,code,2,"OK",null);
                    break;

                case 6:
                    //设备查询APP升级信息
                    List<LctApp> reqApps=bean.getBody().getApps();
                    List<LctApp> respApps=new ArrayList<LctApp>();
                    for (int i = 0; i <reqApps.size(); i++) {
                        LctApp app=reqApps.get(i);
                        LctAppVersion lctAppVersion = lctAppVersionRepository.findTopByAppIdAndVersionGreaterThanOrderByPublishTimeDesc(app.getAppId(), app.getVersion());
                        if(lctAppVersion !=null) {
                            LctApp _app = new LctApp(lctAppVersion.getAppId(), lctAppVersion.getVersion(), lctAppVersion.getUrl(), lctAppVersion.getAppSize(), lctAppVersion.getMd5(), lctAppVersion.getAppDesc());
                            respApps.add(_app);
                        }
                    }
                    LctBody body=new LctBody();
                    body.setApps(respApps);
                    replayMsg = buildResp(version, id, subscribeTopic, code, 2, "OK", body);
                    break;
                case 100://主动上报
                case 101://获取上报
                    //设备信息上报
                    imei=bean.getBody().getImei();
                    Lct lct = lctRepository.findByImei(imei);
                    if(lct ==null){
                        lct =new Lct();
                    }
                    lct.setImei(bean.getBody().getImei());
                    lct.setImsi(bean.getBody().getImsi());
                    lct.setModel(bean.getBody().getModel());
                    lct.setOdmModel(bean.getBody().getOdmModel());
                    lct.setHwVer(bean.getBody().getHwver());
                    lct.setSwVer(bean.getBody().getSwver());
                    lct.setOdmSwVer(bean.getBody().getOdmSwver());
                    lct.setWifiMac(bean.getBody().getWifimac());
                    lct.setBtMac(bean.getBody().getBtmac());
                    lct.setBrandName(bean.getBody().getBrandName());
                    lct.setVendor(bean.getBody().getVendor());
                    lct.setReceiveTime(new Date());
                    lctRepository.save(lct);
                    if(code==100) {
                        replayMsg = buildResp(version, id, subscribeTopic, code, 2, "OK", null);
                    }else{
                        //获取，更新记录
                        //更新控制记录
                        LctRemoteControl lctRemoteControl = lctRemoteControlRepository.findBySequenceId(String.valueOf(bean.getHead().getId()));
                        if(lctRemoteControl !=null){
                            lctRemoteControl.setStatus((short)1);
                            lctRemoteControl.setRemark("设备执行操作成功");
                            lctRemoteControl.setReceiveTime(new Date());
                            lctRemoteControlRepository.save(lctRemoteControl);
                            _logger.info("更新远程控制状态. sequenceId>"+ lctRemoteControl.getSequenceId());
                        }else{
                            _logger.info("没有在数据库找到控制记录，无法更新状态. sequenceId>"+ lctRemoteControl.getSequenceId());
                        }
                    }
                    break;

                case 102://主动上报
                case 103://获取上报
                    //状态信息上报
                    imei=bean.getBody().getImei();
                    LctStatusData lctStatusData =new LctStatusData();
                    lctStatusData.setSequenceId(String.valueOf(bean.getHead().getId()));
                    lctStatusData.setImei(imei);
                    lctStatusData.setType((short) 1);
                    lctStatusData.setLatitude(bean.getBody().getLatitude());
                    lctStatusData.setLongitude(bean.getBody().getLongitude());
                    lctStatusData.setReceiveTime(new Date());
                    lctStatusDataRepository.save(lctStatusData);
                    if(code==102) {
                        replayMsg = buildResp(version, id, subscribeTopic, code, 2, "OK", null);
                    }else{
                        //获取，更新记录
                        //更新控制记录
                        LctRemoteControl lctRemoteControl = lctRemoteControlRepository.findBySequenceId(String.valueOf(bean.getHead().getId()));
                        if(lctRemoteControl !=null){
                            lctRemoteControl.setStatus((short)1);
                            lctRemoteControl.setRemark("设备执行操作成功");
                            lctRemoteControl.setReceiveTime(new Date());
                            lctRemoteControlRepository.save(lctRemoteControl);
                            _logger.info("更新远程控制状态. sequenceId>"+ lctRemoteControl.getSequenceId());
                        }else{
                            _logger.info("没有在数据库找到控制记录，无法更新状态. sequenceId>"+ bean.getHead().getId());
                        }
                    }
                    break;
                case 106://控制
                    imei=bean.getBody().getImei();
                    String sequenceId=String.valueOf(bean.getHead().getId());
                    String result=bean.getBody().getResult();
                    if(result!=null){
                        if(result.equals("OK")){
                            //更新控制记录
                            LctRemoteControl lctRemoteControl = lctRemoteControlRepository.findBySequenceId(sequenceId);
                            if(lctRemoteControl !=null){
                                lctRemoteControl.setStatus((short)1);
                                lctRemoteControl.setRemark("设备执行操作成功");
                                lctRemoteControl.setReceiveTime(new Date());
                                lctRemoteControlRepository.save(lctRemoteControl);
                                _logger.info("更新远程控制状态. sequenceId>"+sequenceId+",result>"+result);
                            }else{
                                _logger.info("没有在数据库找到控制记录，无法更新状态. sequenceId>"+sequenceId);
                            }

                        }
                    }
                    break;
                case 107://碰撞上报
                case 108://移动上报
                    imei=bean.getBody().getImei();
                    lctStatusData =new LctStatusData();
                    lctStatusData.setSequenceId(String.valueOf(bean.getHead().getId()));
                    lctStatusData.setImei(imei);
                    if(code==107) {
                        lctStatusData.setType((short) 2);
                        lctStatusData.setActionTime(DateUtil.parseStrToDate(bean.getBody().getCollisionTime()));
                    }else{
                        lctStatusData.setType((short) 3);
                        lctStatusData.setActionTime(DateUtil.parseStrToDate(bean.getBody().getMoveTime()));
                    }

                    lctStatusData.setLatitude(bean.getBody().getLatitude());
                    lctStatusData.setLongitude(bean.getBody().getLongitude());
                    lctStatusData.setReceiveTime(new Date());
                    lctStatusDataRepository.save(lctStatusData);
                    replayMsg = buildResp(version, id, subscribeTopic, code, 2, "OK", null);
                    break;
                case 109://休眠请求
                    imei=bean.getBody().getImei();
                    String sleepTime =bean.getBody().getSleepTime();
                    _logger.info("设备"+imei+"休眠,休眠时间"+sleepTime);
                    break;
            }
            //回复消息
            String replayTopic=bean.getHead().getFrom();
            if(replayMsg!=null) {
                replay(client, replayTopic, replayMsg);
            }
        }else{
            _logger.info("不符合协议的消息");
        }

        return "";
    }

    public  void replay(MqttClient client,String topic,String meaasge){
        try {
            MqttTopic _replayTopic = client.getTopic(topic);
            _logger.info("发出的消息:" + meaasge);
            MqttMessage message = new MqttMessage(meaasge.getBytes());
            message.setQos(0);
            MqttDeliveryToken token = _replayTopic.publish(message);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public String buildResp(int version,long id, String from,int code, int type, String msg,LctBody body){
        String replayStr=null;
        LctHead head=new LctHead(version,id,from,code,type,msg);

        LctMsgBean msgBean=new LctMsgBean(head,body);
        try {
            replayStr = JSON.toJSONString(msgBean);
        }catch (Exception e){e.printStackTrace();}
        return replayStr;
    }


}
