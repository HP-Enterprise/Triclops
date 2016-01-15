package com.hp.triclops.acquire;


import com.hp.triclops.redis.SocketRedis;
import com.hp.triclops.service.DataHandleService;
import com.hp.triclops.service.OutputHexService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.*;
import org.springframework.stereotype.*;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.*;
import java.util.*;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

/**
 * 数据接收端口.<br>
 * 监听由com.hp.acquire.port属性指定的TCP端口,把接收到的数据快速转存到内部缓冲池中,其它后续数据入理组件会从池中取出数据
 * 进行处理.<br>
 * 数据接收端口也会对数据来源和完整性进行基本校验工作,来源不明和不完整的数据会被抛弃.
 */
@Component
public class AcquirePort {
    // 用于检测所有Channel状态的Selector
    @Value("${com.hp.acquire.port}")
    private int _acquirePort;

    @Value("${com.hp.acquire.nettyServerThreadPoolSize}")
    private int _nettyServerThreadPoolSize;
    //主IO线程池，封装request Msg为Task

    @Value("${com.hp.acquire.dataHandlerThreadPoolSize}")
    private int _dataHandlerThreadPoolSize;
    //消息处理线程池，封装receive Msg为Task,处理入库

    @Autowired
    SocketRedis socketRedis;
    @Autowired
    DataTool dataTool;
    @Autowired
    RequestHandler requestHandler;
    @Autowired
    DataHandleService dataHandleService;
    @Autowired
    OutputHexService outputHexService;

    private Logger _logger;

    private Selector selector = null;

    public static HashMap<String,io.netty.channel.Channel> channels=new HashMap<String,io.netty.channel.Channel>();
    //用于保存连接的哈希表<remoteAddress,Channel>
    public static HashMap<String,String> connectionAddress=new HashMap<String,String>();
    //用于保存连接的哈希表<remoteAddress,vin>

    public   void main(){

        //生成数据
        ScheduledExecutorService  nettyServerScheduledService = Executors.newScheduledThreadPool(_nettyServerThreadPoolSize);
        ScheduledExecutorService  dataHandlerScheduledService = Executors.newScheduledThreadPool(_dataHandlerThreadPoolSize);

        new NettySender(channels,socketRedis,dataTool).start();    //netty发数据线程，根据需要 可以新建多个
        //new DataHandler(socketRedis,dataHandleService,dataTool,dataHandlerScheduledService).start();    //netty数据处理入库线程，内部采用线程池处理数据入库
        new NettyServer(channels,connectionAddress,socketRedis,dataTool,requestHandler,outputHexService,_acquirePort,nettyServerScheduledService).run();    //netty收数据程序，收到消息后可能导致阻塞的业务全部交由线程池处理

    }

}
