package com.hp.triclops.acquire;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.*;
import org.springframework.stereotype.*;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.SocketAddress;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;

/**
 * 数据接收端口.<br>
 * 监听由com.hp.acquire.port属性指定的TCP端口,把接收到的数据快速转存到内部缓冲池中,其它后续数据入理组件会从池中取出数据
 * 进行处理.<br>
 * 数据接收端口也会对数据来源和完整性进行基本校验工作,来源不明和不完整的数据会被抛弃.
 */
@Component
public class AcquirePort {
    // 设备数据上报端口号
    @Value("${com.hp.acquire.port}")
    private int _acquirePort;

    // 日志
    private Logger _logger;

    // 套接字信道
    protected ServerSocketChannel _channelListen;

    // accept选择器
    protected Selector _selectorAccept;

    public AcquirePort(){
        this._logger = LoggerFactory.getLogger(AcquirePort.class);
    }

    /**
     * 启动数据接收端口.<br>
     * 如果com.hp.acquire.port为空或0或负数,则表示禁用数据接收端口.<br>
     */
    public void start() throws IOException {
        // 已经有一个活动的端口了
        if(this._channelListen != null) return;

        if(this._acquirePort <= 0){
            this._logger.info("The AcquirePort is disabled.");
        }
        else{
            this._channelListen = ServerSocketChannel.open();
            this._channelListen.socket().bind(new InetSocketAddress(this._acquirePort));
            this._channelListen.configureBlocking(false);

            this._selectorAccept = Selector.open();
            this._channelListen.register(this._selectorAccept, SelectionKey.OP_ACCEPT);
        }
    }
}
