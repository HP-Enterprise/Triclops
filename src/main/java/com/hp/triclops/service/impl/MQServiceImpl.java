package com.hp.triclops.service.impl;


import com.hp.triclops.entity.Message;
import com.hp.triclops.entity.User;
import com.hp.triclops.entity.UserDevice;
import com.hp.triclops.mq.DeviceType;
import com.hp.triclops.repository.DeviceRepository;
import com.hp.triclops.repository.UserRepository;
import com.hp.triclops.service.MQService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.TimeoutException;

/**
 * MQTT业务层接口
 */
@Service
public class MQServiceImpl implements MQService{
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private DeviceRepository deviceRepository;
    @Autowired
    private MQTTHome mqttHome;
    /**
     * 注册设备
     *
     * @param userId   用户Id
     * @param deviceId 设备编号
     * @param deviceType 设备类型
     * @return 注册结果
     */
    @Override
    public UserDevice deviceRegister(int userId, String deviceId, DeviceType deviceType) {
        List<UserDevice> userDevices = deviceRepository.findByUserId(userId);
        if(userDevices.size()>0&&(deviceType.equals(DeviceType.IOS)||deviceType.equals(DeviceType.ANDROID))){
            for(UserDevice u:userDevices){
                if(u.getDeviceType().equals(DeviceType.IOS)||u.getDeviceType().equals(DeviceType.ANDROID)){
                    u.setActive(0);
                    deviceRepository.save(u);
                }
            }
        }
        User user=this.userRepository.findById(userId);
        List<UserDevice> udList=this.deviceRepository.findByDeviceId(deviceId);
        for(UserDevice ud:udList){
            if(ud.getUser().getId()==user.getId()){
                ud.setActive(1);
                userRepository.updateDeviceid(deviceId, user.getId());
                return deviceRepository.save(ud);
            }
        }
        UserDevice userDevice=new UserDevice();
        userDevice.setDeviceId(deviceId);
        userDevice.setActive(1);
        userDevice.setUser(user);
        userDevice.setDeviceType(deviceType);
        userRepository.updateDeviceid(deviceId, user.getId());
        return this.deviceRepository.save(userDevice);
    }

    /**
     * 向用户对应的设备推送消息
     *
     * @param userId  用户Id
     * @param content 推送内容
     */
    @Override
    public void pushToUser(int userId, String content) {
        List<UserDevice> list= this.deviceRepository.findByUserId(userId);
        StringBuilder sb=new StringBuilder("{devices:[");
        for(UserDevice ud:list){
            sb.append("{token:'")
                    .append(ud.getUser().getId())
                    .append(ud.getDeviceId())
                    .append("',os:'")
                    .append(ud.getDeviceType().name().toLowerCase())
                    .append("'},");
        }
        sb.delete(sb.length()-1,sb.length())
                .append("],alert:'PDFDemo',content:'")
                .append(content)
                .append("',badge:'0'}");
        try {
            this.mqttHome.sendMessage(sb.toString());
            //System.out.println(sb.toString());
        } catch (TimeoutException | IOException e) {
            e.printStackTrace();
            throw new RuntimeException("发送失败:无法建立与消息服务器的连接");
        }
    }


    public void pushToUserAtt(int userId, Message message) {
        List<UserDevice> list= this.deviceRepository.findByUserId(userId);
        StringBuilder sb=new StringBuilder("{devices:[");
//        Message message = new Message();
        for(UserDevice ud:list){
            sb.append("{token:'")
                    .append(ud.getUser().getId())
                    .append(ud.getDeviceId())
                    .append("',os:'")
                    .append(ud.getDeviceType().name().toLowerCase())
                    .append("'},");
        }
        sb.delete(sb.length()-1,sb.length())
                .append("],alert:'PDFDemo',content:'")
                .append(message.toString())
                .append("',badge:'0'}");
        try {
            this.mqttHome.sendMessage(sb.toString());
            //System.out.println(sb.toString());
        } catch (TimeoutException | IOException e) {
            e.printStackTrace();
            throw new RuntimeException("发送失败:无法建立与消息服务器的连接");
        }
    }


}
