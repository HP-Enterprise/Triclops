package com.hp.triclops;

import com.hp.triclops.acquire.*;
import com.hp.triclops.mqtt.MqttMain;
import org.slf4j.LoggerFactory;
import org.slf4j.Logger;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.beans.factory.annotation.*;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class Application implements CommandLineRunner {
    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
       }

    // 日志
    private Logger _logger;


    @Autowired
    private AcquirePort _acquirePort;

    //设置在执行测试类的时候不执行回声服务器的标示
    @Value("${com.hp.acquire.disabled}")
    private boolean _disabled;


    @Autowired
    private MqttMain mqttMain;

    @Value("${lct.mqtt.disabled}")
    private boolean _mqttDisabled;

    public void run(String... args) throws Exception{
        this._logger = LoggerFactory.getLogger(Application.class);
        this._logger.info("Application is running...");

        if(!_mqttDisabled){
            this._logger.info("启动后视镜服务端程序...");
            mqttMain.start();
        }else{
            this._logger.info("不启动后视镜服务端程序，如果需要启动请修改配置lct.mqtt.disabled=false");
        }
        // 启动数据接收端口
        if(_disabled){
            this._logger.info("不启动TBOX服务端程序，如果需要启动请修改配置com.hp.acquire.disabled=false");
        }else{
             this._acquirePort.main();
        }
    }
}