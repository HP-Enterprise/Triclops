package com.hp.triclops;

import com.hp.triclops.acquire.*;
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
    private HansServer _hansServer;

    //设置在执行测试类的时候不执行回声服务器的标示
    @Value("${com.hp.acquire.disabled}")
    private boolean _disabled;

    public void run(String... args) throws Exception{
        this._logger = LoggerFactory.getLogger(Application.class);
        this._logger.info("Application is running...");

        // 启动数据接收端口
//        this._acquirePort.start();
        if(_disabled){
            return;
        }else{
           this._hansServer.init();
        }
    }
}