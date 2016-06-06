package com.hp.triclops.acquire;
import com.hp.triclops.redis.SocketRedis;
import com.hp.triclops.service.OutputHexService;
import io.netty.buffer.ByteBuf;

import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.xml.bind.SchemaOutputResolver;
import java.util.Iterator;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;


/**
 * Handles a server-side channel.
 */
public class NettyServerHandler extends ChannelInboundHandlerAdapter { // (1)
    private SocketRedis socketRedis;
    private RequestHandler requestHandler;
    private DataTool dataTool;
    private ConcurrentHashMap<String,Channel> channels;
    private ConcurrentHashMap<String,String> connections;
    private OutputHexService outputHexService;
    private Logger _logger;
    private ScheduledExecutorService scheduledService;
    private int maxDistance;

    public NettyServerHandler(ConcurrentHashMap<String, Channel> cs,ConcurrentHashMap<String,String> connections,int maxDistance,SocketRedis s,DataTool dt,RequestHandler rh,OutputHexService ohs,ScheduledExecutorService scheduledService ){
        this.channels=cs;
        this.connections=connections;
        this.maxDistance=maxDistance;
        this.socketRedis=s;
        this.dataTool=dt;
        this.requestHandler=rh;
        this.outputHexService=ohs;
        this.scheduledService=scheduledService;
        this._logger = LoggerFactory.getLogger(NettyServerHandler.class);
    }

    @Override
    public synchronized void channelRead(ChannelHandlerContext ctx, Object msg) { // (2)
        Channel ch=ctx.channel();
        ByteBuf m = (ByteBuf) msg;
        String chKey;
        String respStr;
        ByteBuf buf;
        //将缓冲区的数据读出到byte[]

        byte[] receiveData=dataTool.getBytesFromByteBuf(m);

        String receiveDataHexString=dataTool.bytes2hex(receiveData);
        _logger.info("Receive date from " + ch.remoteAddress() + ">>>:" + receiveDataHexString);


        if(!dataTool.checkByteArray(receiveData)) {
            _logger.info(">>>>>bytes data is invalid,we will not handle them");
        }else{
            byte dataType=dataTool.getApplicationType(receiveData);
            switch(dataType)
            {
                case 0x11://电检
                    scheduledService.schedule(new RequestTask(channels, connections,maxDistance,ch, socketRedis, dataTool, requestHandler, outputHexService, receiveDataHexString), 1, TimeUnit.MILLISECONDS);
                    break;
                case 0x12://激活
                    scheduledService.schedule(  new RequestTask(channels,connections,maxDistance,ch,socketRedis,dataTool,requestHandler,outputHexService,receiveDataHexString), 1, TimeUnit.MILLISECONDS);
                    break;
                case 0x13://注册
                    scheduledService.schedule(  new RequestTask(channels,connections,maxDistance,ch,socketRedis,dataTool,requestHandler,outputHexService,receiveDataHexString), 1, TimeUnit.MILLISECONDS);
                    break;
                case 0x14://远程唤醒
                    scheduledService.schedule(  new RequestTask(channels,connections,maxDistance,ch,socketRedis,dataTool,requestHandler,outputHexService,receiveDataHexString), 1, TimeUnit.MILLISECONDS);
                    break;
                case 0x21://固定数据上报
                    scheduledService.schedule(new RequestTask(channels, connections,maxDistance,ch, socketRedis, dataTool, requestHandler, outputHexService, receiveDataHexString), 1, TimeUnit.MILLISECONDS);
                    break;
                case 0x22://实时数据上报
                    scheduledService.schedule(new RequestTask(channels,connections, maxDistance,ch, socketRedis, dataTool, requestHandler, outputHexService, receiveDataHexString), 1, TimeUnit.MILLISECONDS);
                    break;
                case 0x23://补发实时数据上报
                    scheduledService.schedule(new RequestTask(channels, connections,maxDistance,ch, socketRedis, dataTool, requestHandler, outputHexService, receiveDataHexString), 1, TimeUnit.MILLISECONDS);
                    break;
                case 0x24://报警数据上报
                    scheduledService.schedule(new RequestTask(channels, connections,maxDistance,ch, socketRedis, dataTool, requestHandler, outputHexService, receiveDataHexString), 1, TimeUnit.MILLISECONDS);
                    break;
                case 0x25://补发报警数据上报
                    scheduledService.schedule(new RequestTask(channels,connections, maxDistance,ch, socketRedis, dataTool, requestHandler, outputHexService, receiveDataHexString), 1, TimeUnit.MILLISECONDS);
                    break;
                case 0x26://心跳
                    _logger.info("Heartbeat request");
                    chKey=geVinByAddress(ch.remoteAddress().toString());
                    if(chKey==null){
                        _logger.info("Connection is not registered,no response");
                        return;
                    }
                    respStr=requestHandler.getHeartbeatResp(receiveDataHexString);
                    buf=dataTool.getByteBuf(respStr);
                    ch.writeAndFlush(buf);//心跳流程直接回消息
                    break;
                case 0x27://休眠请求
                    scheduledService.schedule(new RequestTask(channels,connections, maxDistance,ch, socketRedis, dataTool, requestHandler, outputHexService, receiveDataHexString), 10, TimeUnit.MILLISECONDS);
                    break;
                case 0x28://故障数据上报
                    scheduledService.schedule(new RequestTask(channels,connections, maxDistance,ch, socketRedis, dataTool, requestHandler, outputHexService, receiveDataHexString), 10, TimeUnit.MILLISECONDS);
                    break;
                case 0x29://补发故障数据上报
                    scheduledService.schedule(new RequestTask(channels, connections,maxDistance,ch, socketRedis, dataTool, requestHandler, outputHexService, receiveDataHexString), 10, TimeUnit.MILLISECONDS);
                    break;
                case 0x31://远程控制响应(上行)包含mid 2 4 5
                    scheduledService.schedule(new RequestTask(channels, connections,maxDistance,ch, socketRedis, dataTool, requestHandler, outputHexService, receiveDataHexString), 10, TimeUnit.MILLISECONDS);
                    break;
                case 0x41://参数查询响应(上行)
                    _logger.info("ParamStatus Ack");
                    saveBytesToRedis(geVinByAddress(ch.remoteAddress().toString()), receiveData);
                    break;
                case 0x42://远程车辆诊断响应(上行)
                    scheduledService.schedule(new RequestTask(channels,connections, maxDistance,ch, socketRedis, dataTool, requestHandler, outputHexService, receiveDataHexString), 10, TimeUnit.MILLISECONDS);
                    break;
                 case 0x51://上报数据设置响应(上行)
                    _logger.info("SignalSetting Ack");
                    saveBytesToRedis(geVinByAddress(ch.remoteAddress().toString()), receiveData);
                    break;
                case 0x52://参数设置响应(上行)
                    scheduledService.schedule(new RequestTask(channels, connections,maxDistance,ch, socketRedis, dataTool, requestHandler, outputHexService, receiveDataHexString), 10, TimeUnit.MILLISECONDS);
                    break;
                default:
                    _logger.info(">>unknown request ,log to log" + receiveDataHexString);
                    //一般数据，判断是否已注册，注册的数据保存
                    break;
            }
        }
    }
    @Override
    public void channelRegistered(ChannelHandlerContext ctx){
        Channel ch=ctx.channel();
        _logger.info("Register" + ch.remoteAddress());
    }
    public void channelUnregistered(ChannelHandlerContext ctx){
        Channel ch=ctx.channel();
        _logger.info("UnRegister" + ch.remoteAddress());
        //连接断开 从map移除连接
        String vin=connections.get(ch.remoteAddress().toString());
        connections.remove(ch.remoteAddress().toString());
        if(vin!=null){
            channels.remove(vin);
        }
       }
    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) { // (4)
        // Close the connection when an exception is raised.
        Channel ch=ctx.channel();
        _logger.info("exceptionCaught" + ch.remoteAddress());
        cause.printStackTrace();
        ctx.close();
    }

    public void saveBytesToRedis(String scKey,byte[] bytes){
        //存储接收数据到redis 采用redis Set结构，一个key对应一个Set<String>
        if(dataTool.checkByteArray(bytes)){
            if(scKey!=null){
                String inputKey="input:"+scKey;//保存数据包到redis里面的key，格式input:{vin}
                String receiveDataHexString=dataTool.bytes2hex(bytes);
                socketRedis.saveSetString(inputKey, receiveDataHexString,-1);
                _logger.info("Save data to Redis:" + inputKey);
            }else{
                _logger.info("can not find the scKey,data is invalid，do not save!");
            }
        }
    }

    //根据SocketChannel得到对应的vin
    public  String geVinByAddress(String remoteAddress)
    {
        String vin=connections.get(remoteAddress);
        return vin;
    }
    //根据SocketChannel得到对应的sc在HashMap中的key,为{vin}
    public  String getKeyByValue_(Channel sc)
    {
        Iterator<String> it= channels.keySet().iterator();
        while(it.hasNext())
        {
            String keyString=it.next();
            if(channels.get(keyString).equals(sc))
                return keyString;
        }
        return null;
    }

}