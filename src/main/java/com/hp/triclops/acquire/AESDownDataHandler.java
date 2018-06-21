package com.hp.triclops.acquire;

import com.hp.triclops.redis.SocketRedis;
import com.hp.triclops.utils.AES128Tool;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufUtil;
import io.netty.channel.*;
import io.netty.util.ReferenceCountUtil;
import io.netty.util.ResourceLeakDetector;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.ConcurrentHashMap;

import static io.netty.buffer.Unpooled.buffer;

/**
 * Created by jackl on 2016/9/23.
 */
public class AESDownDataHandler extends ChannelOutboundHandlerAdapter {
    private DataTool dataTool;
    private SocketRedis socketRedis;
    private ConcurrentHashMap<String, String> connections;
    private Logger _logger = LoggerFactory.getLogger(AESDownDataHandler.class);
    private RequestHandler requestHandler;

    public AESDownDataHandler(SocketRedis s, ConcurrentHashMap<String, String> connections, RequestHandler requestHandler, DataTool dataTool) {
        this.socketRedis = s;
        this.connections = connections;
        this.requestHandler = requestHandler;
        this.dataTool = dataTool;
    }

    @Override
    public void write(ChannelHandlerContext ctx, Object msg, ChannelPromise promise) throws Exception {
        Channel ch = ctx.channel();
        ByteBuf m = (ByteBuf) msg;
//        byte[] sendData=dataTool.getBytesFromByteBuf(m.copy());
        try {
//            ResourceLeakDetector.setLevel(ResourceLeakDetector.Level.PARANOID);
           // byte[] sendData = dataTool.getBytesFromByteBuf(m);
            byte[] sendData = new byte[m.readableBytes()];
            m.readBytes(sendData);
            String sendDataHexString = dataTool.bytes2hex(sendData);
            _logger.info("发送报文 " + ch.remoteAddress() + ">>>原始:" + sendDataHexString);
            String encodeStr = dataTool.bytes2hex(downDataFilter(sendData, ch));
            ByteBuf fire = dataTool.getByteBuf(encodeStr);
            _logger.info("发送报文 " + ch.remoteAddress() + ">>>处理:" + encodeStr);
            promise.addListener(new ChannelFutureListener() {
                @Override
                public void operationComplete(ChannelFuture future) throws Exception {
                    _logger.info("加密报文发送完成 " + ch.remoteAddress() + encodeStr);
                }
            });
            super.write(ctx, fire, promise);
        } finally {
            //todo 释放buffer
            ReferenceCountUtil.release(m);
        }
    }

    /**
     * 下行数据加密  平台->Tbox
     *
     * @param content
     * @return
     */
    public byte[] downDataFilter(byte[] content, Channel ch) {
        //todo 判断数据类型，检查是否需要解密，解密密文，与明文拼接返回
        String aesKey = "";
        byte[] re;//原文
        byte[] data = new byte[content.length - 11 - 1];
        byte[] head = new byte[11];
        byte dataType = dataTool.getApplicationType(content);//业务类型，判断是否需要加密
        byte messageId = dataTool.getMessageId(content);
        String imei = "";
        if (dataType == 0x11 || dataType == 0x12 || dataType == 0x13 || dataType == 0x14) {//这些业务的key依赖于从imei获取
            imei = getImeiFromRedis(ch);
        }
//        if(dataType==0x11||dataType==0x12){//OX11无需密钥，0x12激活报文内容使用密钥（IMEI）
        if (dataType == 0x12) {//0x12激活报文内容使用密钥（IMEI）
            aesKey = imei;
//        }else if(dataType==0x13||dataType==0x14){//注册报文使用密钥（Serial Number+VIN）
        } else if (dataType == 0x11) {//0x11电检
            if (messageId == 0x02) {//0x02不需要加密
                aesKey = imei;
            }
            if (messageId == 0x04) {//0x04需要加密
                aesKey = requestHandler.getRegAesKeyByImei(imei);//t通过imei找到密钥
                if (aesKey == null) {
                    _logger.info("aeskey for register [error],please check in db,imei:" + imei);
                    aesKey = "";
                } else {
                    _logger.info("aeskey for register imei:" + imei + ",aesKey:" + aesKey);
                }
            }
//        }else if(dataType==0x13||dataType==0x14){//注册报文使用密钥（Serial Number+VIN）
        } else if (dataType == 0x13 || dataType == 0x14) {//注册报文使用密钥（Serial Number+VIN）
            aesKey = requestHandler.getRegAesKeyByImei(imei);//t通过imei找到密钥
            if (aesKey == null) {
                _logger.info("aeskey for register [error],please check in db,imei:" + imei);
                aesKey = "";
            } else {
                _logger.info("aeskey for register imei:" + imei + ",aesKey:" + aesKey);
            }
        } else {
            String vin = geVinByAddress(ch.remoteAddress().toString());
            vin = (vin == null) ? "" : vin;
            aesKey = socketRedis.getHashString(dataTool.tboxkey_hashmap_name, vin);
            if (aesKey == null || aesKey.equals("") || aesKey.length() != 16) {
                _logger.info("aeskey for register [error],please check redis,vin:" + vin);
                aesKey = "";
            }
            //从redis取出之前生成的密钥
        }
//        if(dataType!=0x11 && dataType!=0x26 && dataType!=0x61){//除了电检/心跳/失败报告 业务 其他都需要加解密
        if (dataType != 0x26 && dataType != 0x61) {//除了心跳/失败报告 业务 其他都需要加解密
            if (dataType == 0x11 && messageId == 0x02) {
                return content;
            }
            ByteBuf tmp = buffer(1024);
            ByteBuf orginial = dataTool.getByteBuf(dataTool.bytes2hex(content));
            orginial.readBytes(head, 0, 5 + 6);//包头部分 明文
            tmp.writeBytes(head);
            orginial.readBytes(data, 0, data.length);//待加解密data长度=总长度-包头33-checkSum1
            //todo 数据写入完毕 计算报文长度 计算checkSum
//            if(dataType==0x13) {
            if (dataType == 0x11) {
                if (messageId == 0x04) {//0x04才需要加密
                    tmp.writeBytes(AES128Tool.encrypt(data, aesKey));//
                }
            } else if (dataType == 0x13) {
                tmp.writeBytes(AES128Tool.encrypt(data, aesKey));//
            } else {
                tmp.writeBytes(AES128Tool.encrypt(data, aesKey));//如果要生成测试数据只需要把这一段改为加密即可
            }
            tmp.markWriterIndex();
            int newLength = tmp.readableBytes() - 5;//包头不计
            tmp.writerIndex(2);
            tmp.writeShort((short) newLength);
            tmp.resetWriterIndex();
            byte[] withOutCheckSum = dataTool.getBytesFromByteBuf(tmp);//without checkSum
            tmp.writeByte(dataTool.getCheckSum(withOutCheckSum));//checkSum
            re = dataTool.getBytesFromByteBuf(tmp);
            //todo 释放buffer
            ReferenceCountUtil.release(orginial);
            ReferenceCountUtil.release(tmp);

        } else {
            re = content;
        }
        return re;
    }

    //根据SocketChannel得到对应的vin
    public String geVinByAddress(String remoteAddress) {
        String vin = connections.get(remoteAddress);
        return vin;
    }

    public String getImeiFromRedis(Channel ch) {
        String imei = socketRedis.getHashString(dataTool.connection_online_imei_hashmap_name, ch.remoteAddress().toString());
        if (imei == null || imei.equals("") || imei.length() != 15) {
            _logger.info("get imei from redis [error],please check redis,connection:" + ch.remoteAddress());
            imei = "";
        }
        return imei;
    }
}
