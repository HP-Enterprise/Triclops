package com.hp.triclops.acquire;


import com.hp.triclops.redis.SocketRedis;
import com.hp.triclops.service.DataHandleService;
import com.hp.triclops.service.OutputHexService;
import io.netty.channel.Channel;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.*;
import org.springframework.stereotype.*;
import java.nio.channels.*;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
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

    @Value("${com.hp.acquire.nettyServerTcpBacklog}")
    private int _nettyServerTcpBacklog;
    //TCP backlog

    @Value("${com.hp.acquire.dataHandlerThreadPoolSize}")
    private int _dataHandlerThreadPoolSize;
    //消息处理线程池，封装receive Msg为Task,处理入库

    //设置在执行测试类的时候不执行数据接收服务器的标示
    @Value("${com.hp.acquire.dataserver-disabled:}")
    private boolean _dataserverDisabled;

    //设置在执行测试类的时候不执行回声服务器的标示
    @Value("${com.hp.acquire.datahandler-disabled}")
    private boolean _datahandlerDisabled;

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

    public static ConcurrentHashMap<String,Channel> channels=new ConcurrentHashMap<String,io.netty.channel.Channel>();
    //用于保存连接的哈希表<remoteAddress,Channel>
    public static ConcurrentHashMap<String,String> connectionAddress=new ConcurrentHashMap<String,String>();
    //用于保存连接的哈希表<remoteAddress,vin>

    public   void main(){

        //生成数据
        ScheduledExecutorService  nettyServerScheduledService = Executors.newScheduledThreadPool(_nettyServerThreadPoolSize);
        ScheduledExecutorService  dataHandlerScheduledService = Executors.newScheduledThreadPool(_dataHandlerThreadPoolSize);
        new NettySender(channels,socketRedis,dataTool).start();    //netty发数据线程，根据需要 可以新建多个

        //多层开关，通过配置文件控制，支持部署专门的数据解析服务器
        if(!_datahandlerDisabled){
            List<String> HandleSuffixes=dataTool.getHandleSuffix();
            if(HandleSuffixes.size()==0){
                new DataHandler(socketRedis,dataHandleService,"",dataTool,dataHandlerScheduledService).start();    //netty数据处理入库线程，内部采用线程池处理数据入库
            }else if(HandleSuffixes.size()==1&&HandleSuffixes.get(0).equalsIgnoreCase("ALL")){
                new DataHandler(socketRedis,dataHandleService,"",dataTool,dataHandlerScheduledService).start();    //netty数据处理入库线程，内部采用线程池处理数据入库
            }else{
                for(String k:HandleSuffixes)  {
                    new DataHandler(socketRedis,dataHandleService,k,dataTool,dataHandlerScheduledService).start();
                }
                 //netty数据处理入库线程，内部采用线程池处理数据入库
            }
        }
        if(!_dataserverDisabled) {
            new NettyServer(channels, connectionAddress, _nettyServerTcpBacklog, socketRedis, dataTool, requestHandler, outputHexService, _acquirePort, nettyServerScheduledService).run();    //netty收数据程序，收到消息后可能导致阻塞的业务全部交由线程池处理
        }
    }

}
