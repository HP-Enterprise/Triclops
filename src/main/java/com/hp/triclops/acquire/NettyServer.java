package com.hp.triclops.acquire;

/**
 * Created by luj on 2015/9/21.
 */

import com.hp.triclops.redis.SocketRedis;
import com.hp.triclops.service.OutputHexService;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import io.netty.util.ResourceLeakDetector;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ScheduledExecutorService;

/**
 * Discards any incoming data.
 */
public class NettyServer {

    private int port;
    private SocketRedis socketRedis;
    private DataTool dataTool;
    private ConcurrentHashMap<String, Channel> channels;
    private ConcurrentHashMap<String, String> connections;
    private ConcurrentHashMap<String, String> hearts;
    private RequestHandler requestHandler;
    private OutputHexService outputHexService;
    private Logger _logger;
    private ScheduledExecutorService scheduledService;
    private int backlog;
    private int maxDistance;
    private String serverId;

    public NettyServer(ConcurrentHashMap<String, Channel> cs, ConcurrentHashMap<String, String> connections, ConcurrentHashMap<String, String> hearts, int maxDistance, int _backlog, SocketRedis s, DataTool dt, RequestHandler rh, OutputHexService ohs, int port, String serverId, ScheduledExecutorService scheduledService) {
        this.channels = cs;
        this.connections = connections;
        this.hearts = hearts;
        this.maxDistance = maxDistance;
        this.backlog = _backlog;
        this.socketRedis = s;
        this.dataTool = dt;
        this.requestHandler = rh;
        this.outputHexService = ohs;
        this.port = port;
        this.serverId = serverId;
        this.scheduledService = scheduledService;
        this._logger = LoggerFactory.getLogger(NettyServer.class);
    }

    static int connectionCount = 0;

    public void run() {
        try {

            EventLoopGroup bossGroup = new NioEventLoopGroup(); // (1)
            EventLoopGroup workerGroup = new NioEventLoopGroup();

            try {
                ServerBootstrap b = new ServerBootstrap(); // (2)
                b.group(bossGroup, workerGroup)
                        .channel(NioServerSocketChannel.class)
//                        .handler(new LoggingHandler(LogLevel.INFO)) // (3)
                        .childHandler(new ChannelInitializer<SocketChannel>() { // (4)
                            @Override
                            public void initChannel(SocketChannel ch) throws Exception {
                                ch.pipeline().addLast(new LengthFieldBasedFrameDecoder(1024, 2, 2, 2, 0));
                                ch.pipeline().addLast(new AESUpDataHandler(socketRedis, connections, requestHandler, dataTool));
                                ch.pipeline().addLast(new NettyServerHandler(channels, connections, hearts, maxDistance, socketRedis, dataTool, requestHandler, outputHexService, serverId, scheduledService));
                                ch.pipeline().addLast(new AESDownDataHandler(socketRedis, connections, requestHandler, dataTool));
                                connectionCount++;
                                // _logger.info("real connectionCount>>>>>>>>>>>>>>>>:"+connectionCount);
                            }
                        })
                        .option(ChannelOption.SO_BACKLOG, backlog)          // (5)
                        .childOption(ChannelOption.SO_KEEPALIVE, true); // (6)
                //ResourceLeakDetector.setLevel(ResourceLeakDetector.Level.PARANOID);
                // Bind and start to accept incoming connections.
                ChannelFuture f = b.bind(port).sync(); // (7)

                // Wait until the server socket is closed.
                // In this example, this does not happen, but you can do that to gracefully
                // shut down your server.
                f.channel().closeFuture().sync();
            } finally {
                workerGroup.shutdownGracefully();
                bossGroup.shutdownGracefully();
            }

        } catch (Exception e) {
            e.printStackTrace();
            _logger.info("exception:" + e);
        }
    }


}