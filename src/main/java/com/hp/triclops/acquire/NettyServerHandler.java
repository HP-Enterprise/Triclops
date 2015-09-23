package com.hp.triclops.acquire;
import com.hp.triclops.redis.SocketRedis;
import io.netty.buffer.ByteBuf;

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
    private DataTool dataTool;
    private HashMap<String,Channel> channels;
    private Logger _logger;

    public NettyServerHandler(HashMap<String, Channel> cs,SocketRedis s,DataTool dt){
        this.channels=cs;
        this.socketRedis=s;
        this.dataTool=dt;
        this._logger = LoggerFactory.getLogger(NettyServerHandler.class);
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) { // (2)
        Channel ch=ctx.channel();
        ByteBuf m = (ByteBuf) msg;

        //将缓冲区的数据读出到byte[]

        byte[] receiveData=dataTool.getBytesFromByteBuf(m);
        ch.writeAndFlush(msg); // (1)
        String receiveDataHexString=dataTool.bytes2hex(receiveData);
        _logger.info("Receive date from " + ch.remoteAddress() + ">>>:" + receiveDataHexString);

        if(!dataTool.checkByteArray(receiveData)) {
            _logger.info(">>>>>bytes data is invalid,we will not save them");
        }else{
            byte dataType=dataTool.getApplicationType(receiveData);
            switch(dataType)
            {
                case 0x13:
                    _logger.info("Register start...");
                    HashMap<String,String> vinAndSerialNum=dataTool.getVinDataFromRegBytes(receiveData);
                    String eventId=vinAndSerialNum.get("eventId");
                    String vin=vinAndSerialNum.get("vin");
                    String serialNum=vinAndSerialNum.get("serialNum");
                    boolean checkVinAndSerNum= dataTool.checkVinAndSerialNum(vin, serialNum);
                    //如果注册成功记录连接，后续可以通过redis主动发消息，不成功不记录连接
                    ByteBuf send=dataTool.getRegResultByteBuf(receiveData, Integer.valueOf(eventId), checkVinAndSerNum);
                    //发往客户端的数据，根据验证结果+收到的数据生成
                    ch.writeAndFlush(send);
                    if(checkVinAndSerNum){
                        channels.put(vin, ch);
                        _logger.info("resister success,contection" + vin + "Save to HashMap");
                    }else{
                        _logger.info("resister faild,close contection");
                        ch.close();//暂时不关闭连接
                    }
                    break;
                case 0x11:
                    _logger.info("check check!");
                    ch.write(dataTool.getByteBuf("test passed!"));//回发数据直接回消息
                    //不记录连接，只能通过请求-应答方式回消息，无法通过redis主动发消息
                    break;
                case 0x26:
                    _logger.info("Heartbeat request");
                    String chKey=getKeyByValue(ch);
                    if(chKey==null){
                        _logger.info("conntection is not registered,no response");
                    }else{
                        System.out.println(dataTool.bytes2hex("we receive your Heartbeat!".getBytes()));
                        ch.writeAndFlush(dataTool.getByteBuf(dataTool.bytes2hex("we receive your Heartbeat!".getBytes())));//回发数据直接回消息
                    }
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
                socketRedis.saveString(inputKey, receiveDataHexString,-1);
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