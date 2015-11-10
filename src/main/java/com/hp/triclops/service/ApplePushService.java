package com.hp.triclops.service;

import com.hp.triclops.entity.UserDevice;
import com.hp.triclops.mq.DeviceType;
import org.springframework.stereotype.Service;

/**
 * MQTT业务层接口
 */
@Service
public interface ApplePushService {

    /**
     * 注册设备
     * @param userId    用户Id
     * @param deviceId  设备编号
     * @param deviceType 设备类型
     * @return  注册结果
     */
    public UserDevice deviceRegister(int userId, String deviceId, DeviceType deviceType);

    /**
     * 向用户对应的设备推送消息
     * @param content   推送内容
     * @param userId    用户Id
     */
    public void pushToUser(String content, int userId);


}
