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
    private String serverId;

    public NettyServerHandler(ConcurrentHashMap<String, Channel> cs,ConcurrentHashMap<String,String> connections,int maxDistance,SocketRedis s,DataTool dt,RequestHandler rh,OutputHexService ohs,String serverId,ScheduledExecutorService scheduledService ){
        this.channels=cs;
        this.connections=connections;
        this.maxDistance=maxDistance;
        this.socketRedis=s;
        this.dataTool=dt;
        this.requestHandler=rh;
        this.outputHexService=ohs;
        this.serverId=serverId;
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
        _logger.info("收到报文 " + ch.remoteAddress() + ">>>处理:" + receiveDataHexString);

        if(!dataTool.checkByteArray(receiveData)) {
            _logger.info(">>>>>报文非法，不处理");
        }else{
            byte dataType=dataTool.getApplicationType(receiveData);
            switch(dataType)
            {
                case 0x11://电检
                case 0x12://激活
                case 0x13://注册
                case 0x14://远程唤醒
                case 0x15://流量查询请求
                case 0x21://固定数据上报
                case 0x22://实时数据上报
                case 0x23://补发实时数据上报
                case 0x24://报警数据上报
                case 0x25://补发报警数据上报
                    scheduledService.schedule(new RequestTask(channels, connections,maxDistance,ch, socketRedis, dataTool, requestHandler, outputHexService, serverId,receiveDataHexString), 1, TimeUnit.MILLISECONDS);
                    break;

                case 0x26://心跳
                    _logger.info("[0x26]收到心跳请求");
                    chKey=geVinByAddress(ch.remoteAddress().toString());
                    if(chKey==null){
                        _logger.info("报文对应的连接没有注册，不处理报文");
                        return;
                    }
                    respStr=requestHandler.getHeartbeatResp(receiveDataHexString);
                    buf=dataTool.getByteBuf(respStr);
                    ch.writeAndFlush(buf);//心跳流程直接回消息
                    break;
                case 0x27://休眠请求
                case 0x28://故障数据上报
                case 0x29://补发故障数据上报
                case 0x2A://驾驶行为上报
                case 0x31://远程控制响应(上行)包含mid 2 4 5
                case 0x32://远程控制设置响应(上行)包含mid 2
                    scheduledService.schedule(new RequestTask(channels, connections,maxDistance,ch, socketRedis, dataTool, requestHandler, outputHexService,serverId, receiveDataHexString), 1, TimeUnit.MILLISECONDS);
                    break;
                case 0x41://参数查询响应(上行)
                    _logger.info("ParamStatus Ack");
                    saveBytesToRedis(geVinByAddress(ch.remoteAddress().toString()), receiveData);
                    break;
                case 0x42://远程车辆诊断响应(上行)
                    scheduledService.schedule(new RequestTask(channels, connections, maxDistance, ch, socketRedis, dataTool, requestHandler, outputHexService, serverId,receiveDataHexString), 1, TimeUnit.MILLISECONDS);
                    break;
                 case 0x51://上报数据设置响应(上行)
                    _logger.info("SignalSetting Ack");
                    saveBytesToRedis(geVinByAddress(ch.remoteAddress().toString()), receiveData);
                    break;
                case 0x52://参数设置响应(上行)
                case 0x61://解密失败报告
                    scheduledService.schedule(new RequestTask(channels, connections,maxDistance,ch, socketRedis, dataTool, requestHandler, outputHexService, serverId,receiveDataHexString), 1, TimeUnit.MILLISECONDS);
                    break;
                default:
                    _logger.info("未知类型的数据，记录到日志：" + receiveDataHexString);
                    //一般数据，判断是否已注册，注册的数据保存
                    break;
            }
        }
    }
    @Override
    public void channelRegistered(ChannelHandlerContext ctx){
        Channel ch=ctx.channel();
        _logger.info("Socket连接:" + ch.remoteAddress());
    }
    public void channelUnregistered(ChannelHandlerContext ctx){
        Channel ch=ctx.channel();
        _logger.info("Socket断连:" + ch.remoteAddress());
        //连接断开 从map移除连接
        String vin = connections.get(ch.remoteAddress().toString());
        connections.remove(ch.remoteAddress().toString());
        socketRedis.deleteHashString(dataTool.connection_online_imei_hashmap_name, ch.remoteAddress().toString());
        if(vin!=null){
            //todo 这里我们加上一个逻辑，断开连接的时候，再清除redis的连接之前，需要判断redis现在保存的和我们当前要处理的是不是同一个 ok?
            //存储的代码//socketRedis.saveHashString(dataTool.connection_hashmap_name, vin, serverId + "-" + ch.remoteAddress().toString(), -1);//连接名称保存到redis
            String _val=socketRedis.getHashString(dataTool.connection_hashmap_name, vin);
            String _addr=_val.split("-")[1];
            if(_addr.equals(ch.remoteAddress().toString())) {
                _logger.info("Socket断连处理，通过"+ ch.remoteAddress().toString()+"找到的vin:"+vin+",将会移除该vin在Redis和map中的存储");
                socketRedis.deleteHashString(dataTool.connection_hashmap_name, vin);//连接从redis中清除
            }else{
                _logger.info("Socket断连处理，con@redis:"+_addr+"<>"+ch.remoteAddress().toString()+",将不会清除redis的连接信息，vin "+vin+"已经有了新的连接信息");
            }
            channels.remove(vin);//chanels是一个本机的概念，必须清除。
        }
        _logger.info("连接信息Redis:"+socketRedis.listHashKeys(dataTool.connection_hashmap_name));
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
                _logger.info("保存数据到Redis:" + inputKey);
            }else{
                _logger.info("未能找到对应的vin，无法保存数据!");
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