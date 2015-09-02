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

    // 数据接收端口
    @Autowired
    private AcquirePort _acquirePort;

    public void run(String... args) throws Exception{
        this._logger = LoggerFactory.getLogger(Application.class);
        this._logger.info("Application is running...");

        // 启动数据接收端口
//        this._acquirePort.start();
    }
}