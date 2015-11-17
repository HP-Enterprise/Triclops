package com.hp.triclops.service.impl;


import com.hp.triclops.entity.User;
import com.hp.triclops.entity.UserDevice;
import com.hp.triclops.mq.DeviceType;
import com.hp.triclops.repository.DeviceRepository;
import com.hp.triclops.repository.UserRepository;
import com.hp.triclops.service.ApplePushService;
import com.notnoop.apns.APNS;
import com.notnoop.apns.ApnsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;


import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;


/**
 * APNS业务层接口
 */
@Service
public class ApplePushServiceImpl implements ApplePushService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private DeviceRepository deviceRepository;

    @Value("${com.hp.apns.cer.password}")
    private String pwd;
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
        User user=this.userRepository.findById(userId);
        List<UserDevice> udList=this.deviceRepository.findByDeviceId(deviceId);
        for(UserDevice ud:udList){
            if(ud.getUser().getId()==user.getId()){
                return ud;
            }
        }
        UserDevice userDevice=new UserDevice();
        userDevice.setDeviceId(deviceId);
        userDevice.setActive(1);
        userDevice.setUser(user);
        userDevice.setDeviceType(deviceType);
        return this.deviceRepository.save(userDevice);
    }

    /**
     * 向用户对应的设备推送消息
     *
     * @param userId  userId
     * @param content 推送内容
     */
    @Override
    public void pushToUser(String content,int userId) {
        try {
            String p12Path = this.getParallelPath();

            List<UserDevice> list= this.deviceRepository.findByUserId(userId);
            Collection<String> deviceTokens = new ArrayList<String>();

            for(UserDevice ud :list){
                String pushToken = ud.getDeviceId();
                deviceTokens.add(pushToken);
            }
            ApnsService service = APNS.newService().withCert(p12Path,pwd).withSandboxDestination().build();

            String payLoad = APNS.newPayload().alertBody(content).badge(1).sound("default").build();
            service.push(deviceTokens, payLoad);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public  String  getParallelPath()
    {
        String filename = "certforANPS.p12";
        String pre=System.getProperty("user.dir");
        String path=pre;
        for(String arg:new String[]{"src","main","resources","certificates"})
        {

            path+= File.separator+arg;
        }
        path+=File.separator+filename;
        if(path.startsWith("file"))
        {
            path=path.substring(5);
        }
        path.replace("/", File.separator);
        return path;
    }
}
