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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;

/**
 * Discards any incoming data.
 */
public class NettyServer {

    private int port;
    private SocketRedis socketRedis;
    private DataTool dataTool;
    private HashMap<String,Channel> channels;
    private RequestHandler requestHandler;
    private OutputHexService outputHexService;
    private Logger _logger;

    public NettyServer(HashMap<String, Channel> cs,SocketRedis s,DataTool dt,RequestHandler rh,OutputHexService ohs,int port) {
        this.channels=cs;
        this.socketRedis=s;
        this.dataTool=dt;
        this.requestHandler=rh;
        this.outputHexService=ohs;
        this.port = port;
        this._logger = LoggerFactory.getLogger(NettyServer.class);
    }
    static int connectionCount=0;
    public  void run()  {
        try{

            EventLoopGroup bossGroup = new NioEventLoopGroup(); // (1)
            EventLoopGroup workerGroup = new NioEventLoopGroup();
            try {
                ServerBootstrap b = new ServerBootstrap(); // (2)
                b.group(bossGroup, workerGroup)
                        .channel(NioServerSocketChannel.class) // (3)
                        .childHandler(new ChannelInitializer<SocketChannel>() { // (4)
                            @Override
                            public void initChannel(SocketChannel ch) throws Exception {
                                //ch.pipeline().addLast(new MultiLengthFieldBasedFrameDecoder());
                                ch.pipeline().addLast(new LengthFieldBasedFrameDecoder(1024,2,2,2,0));
                                ch.pipeline().addLast(new NettyServerHandler(channels,socketRedis,dataTool,requestHandler,outputHexService));
                                connectionCount++;
                               // _logger.info("real connectionCount>>>>>>>>>>>>>>>>:"+connectionCount);
                            }
                        })
                        .option(ChannelOption.SO_BACKLOG, 1024)          // (5)
                        .childOption(ChannelOption.SO_KEEPALIVE, true); // (6)

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

        }catch (Exception e){e.printStackTrace();}
    }


}