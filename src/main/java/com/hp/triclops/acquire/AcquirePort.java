package com.hp.triclops.acquire;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.*;
import org.springframework.stereotype.*;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.StandardSocketOptions;
import java.nio.ByteBuffer;
import java.nio.channels.*;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

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

    final String IP = "127.0.0.1";

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

        try (AsynchronousServerSocketChannel asynchronousServerSocketChannel = AsynchronousServerSocketChannel
                .open()) {
            if (asynchronousServerSocketChannel.isOpen()) {
                // set some options
                asynchronousServerSocketChannel.setOption(
                        StandardSocketOptions.SO_RCVBUF, 4 * 1024);
                asynchronousServerSocketChannel.setOption(
                        StandardSocketOptions.SO_REUSEADDR, true);
                // bind the asynchronous server socket channel to local address
                asynchronousServerSocketChannel.bind(new InetSocketAddress(IP,
                        _acquirePort));// display a waiting message while ...
                // waiting clients
                System.out.println("Waiting for connections ...");
                while (true) {
                    Future<AsynchronousSocketChannel> asynchronousSocketChannelFuture = asynchronousServerSocketChannel
                            .accept();
                    try (AsynchronousSocketChannel asynchronousSocketChannel = asynchronousSocketChannelFuture.get()) {
                        System.out.println("Incoming connection from: "
                                + asynchronousSocketChannel.getRemoteAddress());
                        final ByteBuffer buffer = ByteBuffer
                                .allocateDirect(1024);
                        // transmitting data
                        while (asynchronousSocketChannel.read(buffer).get() != -1) {
                            System.out.println(asynchronousSocketChannel.read(buffer).get());
                            buffer.flip();
                            asynchronousSocketChannel.write(buffer).get();
                            if (buffer.hasRemaining()) {
                                buffer.compact();
                            } else {
                                buffer.clear();
                            }
                        }
                        System.out.println(asynchronousSocketChannel
                                .getRemoteAddress()
                                + " was successfully served!");
                    } catch (IOException | InterruptedException | ExecutionException ex) {
                        System.err.println(ex);
                    }
                }
            } else {
                System.out.println("The asynchronous server-socket channel cannot be opened!");
            }
        } catch (IOException ex) {
            System.err.println(ex);
        }
    }
}
