package com.hp.triclops.acquire;
import com.hp.triclops.redis.SocketRedis;
import io.netty.buffer.ByteBuf;

import io.netty.buffer.ByteBufUtil;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.util.HashMap;
import java.util.Iterator;
import static io.netty.buffer.Unpooled.*;

/**
 * Handles a server-side channel.
 */
public class NettyServerHandler extends ChannelInboundHandlerAdapter { // (1)
    private SocketRedis socketRedis;
    private RequestHandler requestHandler;
    private DataTool dataTool;
    private HashMap<String,Channel> channels;
    private Logger _logger;

    public NettyServerHandler(HashMap<String, Channel> cs,SocketRedis s,DataTool dt,RequestHandler rh){
        this.channels=cs;
        this.socketRedis=s;
        this.dataTool=dt;
        this.requestHandler=rh;
        this._logger = LoggerFactory.getLogger(NettyServerHandler.class);
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) { // (2)
        Channel ch=ctx.channel();
        ByteBuf m = (ByteBuf) msg;
        String chKey;
        String respStr;
        ByteBuf buf;
        //将缓冲区的数据读出到byte[]

        byte[] receiveData=dataTool.getBytesFromByteBuf(m);
        ch.writeAndFlush(msg); // (1)
        String receiveDataHexString=dataTool.bytes2hex(receiveData);
        _logger.info("Receive date from " + ch.remoteAddress() + ">>>:" + receiveDataHexString);

        if(!dataTool.checkByteArray(receiveData)) {
            _logger.info(">>>>>bytes data is invalid,we will not handle them");
        }else{

            byte dataType=dataTool.getApplicationType(receiveData);
            switch(dataType)
            {
                case 0x11://电检
                    _logger.info("Diag start...");
                    chKey=getKeyByValue(ch);
                    if(chKey==null){
                        _logger.info("Connection is not registered,no response");
                        return;
                    }
                    respStr=requestHandler.getDiagResp(receiveDataHexString);
                    buf=dataTool.getByteBuf(respStr);
                    ch.writeAndFlush(buf);//回发数据直接回消息
                    break;

                case 0x12://激活
                    _logger.info("Active start...");
                    respStr=requestHandler.getActiveHandle(receiveDataHexString);
                    if(respStr!=null){
                        //如果是激活请求，会有messageId=2的响应，如果是激活结果 没有响应
                        buf=dataTool.getByteBuf(respStr);
                        ch.writeAndFlush(buf);//回发数据直接回消息
                    }

                    break;
                case 0x13://注册
                    _logger.info("Register start...");
                    HashMap<String,String> vinAndSerialNum=dataTool.getVinDataFromRegBytes(receiveData);
                    String eventId=vinAndSerialNum.get("eventId");
                    String vin=vinAndSerialNum.get("vin");
                    String serialNum=vinAndSerialNum.get("serialNum");
                    boolean checkVinAndSerNum= dataTool.checkVinAndSerialNum(vin, serialNum);
                    //发往客户端的注册结果数据，根据验证结果+收到的数据生成
                    respStr=requestHandler.getRegisterResp(receiveDataHexString, checkVinAndSerNum);
                    buf=dataTool.getByteBuf(respStr);
                    ch.writeAndFlush(buf);//回发数据直接回消息
                    //如果注册成功记录连接，后续可以通过redis主动发消息，不成功不记录连接
                    if(checkVinAndSerNum){
                        channels.put(vin, ch);
                        _logger.info("resister success,Connection" + vin + "Save to HashMap");
                    }else{
                        _logger.info("resister failed,close Connection");
                        ch.close();//关闭连接
                    }
                    break;

                case 0x14://远程唤醒
                    _logger.info("RemoteWakeUp start...");
                    chKey=getKeyByValue(ch);
                    if(chKey==null){
                        _logger.info("Connection is not registered,no response");
                        return;
                    }
                    respStr=requestHandler.getRemoteWakeUpResp(receiveDataHexString);
                    buf=dataTool.getByteBuf(respStr);
                    ch.writeAndFlush(buf);//回发数据直接回消息

                    break;
                case 0x21://固定数据上报
                    _logger.info("Regular Data Report Message");
                    chKey=getKeyByValue(ch);
                    if(chKey==null){
                        _logger.info("Connection is not registered,no response");
                        return;
                    }
                    saveBytesToRedis(getKeyByValue(ch), receiveData);
                    break;
                case 0x22://实时数据上报
                    _logger.info("Real Time Report Message");
                    chKey=getKeyByValue(ch);
                    if(chKey==null){
                        _logger.info("Connection is not registered,no response");
                        return;
                    }
                    saveBytesToRedis(getKeyByValue(ch), receiveData);
                    break;
                case 0x23://补发数据上报
                    _logger.info("Data ReSend Message");
                    chKey=getKeyByValue(ch);
                    if(chKey==null){
                        _logger.info("Connection is not registered,no response");
                        return;
                    }
                    saveBytesToRedis(getKeyByValue(ch), receiveData);
                    break;
                case 0x24://报警数据上报
                    _logger.info("Warning Message");
                    chKey=getKeyByValue(ch);
                    if(chKey==null){
                        _logger.info("Connection is not registered,no response");
                        return;
                    }
                    saveBytesToRedis(getKeyByValue(ch), receiveData);
                    break;

                case 0x26://心跳
                    _logger.info("Heartbeat request");
                    chKey=getKeyByValue(ch);
                    if(chKey==null){
                        _logger.info("Connection is not registered,no response");
                        return;
                    }
                    respStr=requestHandler.getHeartbeatResp(receiveDataHexString);
                    buf=dataTool.getByteBuf(respStr);
                    ch.writeAndFlush(buf);//回发数据直接回消息
                    break;

                case 0x31://远程控制响应(上行)
                    _logger.info("RemoteControl Ack");
                    chKey=getKeyByValue(ch);
                    if(chKey==null){
                        _logger.info("Connection is not registered,no response");
                        return;
                    }
                    String _vin=chKey;
                    requestHandler.getRemoteControlAck(receiveDataHexString,_vin);
                   //只需要记录远程控制结果，无数据下行
                    break;

                default:
                    _logger.info(">>other request dave,data to redis");
                    saveBytesToRedis(getKeyByValue(ch), receiveData);
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
        String chKey=getKeyByValue(ch);
        channels.remove(chKey);
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

    //根据SocketChannel得到对应的sc在HashMap中的key,为{vin}
    public  String getKeyByValue(Channel sc)
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