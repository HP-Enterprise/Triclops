package com.hp.triclops.service.impl;

import com.hp.triclops.repository.DeviceRepository;
import com.notnoop.apns.APNS;
import com.notnoop.apns.ApnsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

import java.util.Date;
import java.util.Map;
import java.util.Set;

/**
 * Created by yuh on 2015/11/17.
 */
@Configuration
@EnableScheduling
public class ApplePushSchedule {

    @Autowired
    private DeviceRepository deviceRepository;

    @Value("${com.hp.apns.cer.password}")
    private String pwd;

    @Value("${com.hp.apns.p12}")
    private String p12Path;

    @Scheduled(cron = "0 0 3 * * ?")//秒，分，小时，月，年
    public void feedbackToken() {
        try {
            ApnsService service = APNS.newService().withCert(p12Path, pwd).withSandboxDestination().build();
            Map<String, Date> inValidateTokens = service.getInactiveDevices();
            Set<String> tokenSet = inValidateTokens.keySet();
            for (String token : tokenSet) {
                //Date date = inValidateTokens.get(token);
               // System.out.println(">>>>>" + token + "<<<<<" + date);
                this.deviceRepository.deleteByDeviceId(token);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
