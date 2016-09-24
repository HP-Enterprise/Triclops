package com.hp.triclops.acquire;

import com.hp.triclops.redis.SocketRedis;
import com.hp.triclops.utils.AES128Tool;
import io.netty.buffer.ByteBuf;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.ConcurrentHashMap;

import static io.netty.buffer.Unpooled.buffer;

/**
 * Created by jackl on 2016/9/23.
 */
public class AESUpDataHandler extends ChannelInboundHandlerAdapter {
    private DataTool dataTool;
    private SocketRedis socketRedis;
    private ConcurrentHashMap<String,String> connections;
    private Logger _logger= LoggerFactory.getLogger(AESUpDataHandler.class);
    private RequestHandler requestHandler;

    public AESUpDataHandler(SocketRedis s,ConcurrentHashMap<String,String> connections,RequestHandler requestHandler,DataTool dataTool){
        this.socketRedis=s;
        this.connections=connections;
        this.requestHandler=requestHandler;
        this.dataTool=dataTool;
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        Channel ch=ctx.channel();
        ByteBuf m = (ByteBuf) msg;
        byte[] receiveData=dataTool.getBytesFromByteBuf(m.copy());
        String receiveDataHexString=dataTool.bytes2hex(receiveData);
        _logger.info("Receive date from " + ch.remoteAddress() + ">>>A:" + receiveDataHexString);

        String encodeStr=dataTool.bytes2hex(upDatafilter(receiveData,ch));
        ByteBuf fire=dataTool.getByteBuf(encodeStr);

        ctx.fireChannelRead(fire);
    }
    /**
     * 上行数据解密 Tbox->平台
     * @param content
     * @return
     */
    public  byte[] upDatafilter(byte[]  content,Channel ch) {
        //todo 判断数据类型，检查是否需要解密，解密密文，与明文拼接返回
        String aesKey="";
        byte[] re;//原文
        byte[] data=new byte[content.length-33-1];
        byte[] head=new byte[33];
        byte dataType=dataTool.getApplicationType(content);//业务类型，判断是否需要加密
        String imei=dataTool.getImeiFromReqData(content);
        if(dataType==0x11||dataType==0x12){//OX11无需密钥，0x12激活报文内容使用密钥（IMEI）
            aesKey=imei;
        }else if(dataType==0x13){//注册报文使用密钥（Serial Number+VIN）
            aesKey=requestHandler.getRegAesKeyByImei(imei);//t通过imei找到密钥
            if(aesKey==null){
                _logger.info("aeskey for register [error],please check in db,imei:"+imei);
                aesKey="";
            }
        }else{
            String vin=geVinByAddress(ch.remoteAddress().toString());
            vin=(vin==null)?"":vin;
            aesKey=socketRedis.getHashString(dataTool.tboxkey_hashmap_name,vin);
            if(aesKey==null||aesKey.equals("")||aesKey.length()!=16){
                _logger.info("aeskey for register [error],please check redis,vin:"+vin);
                aesKey="";
            }
            //从redis取出之前生成的密钥
        }
        if(dataType!=0x11){//除了电检业务 其他都需要加解密
            ByteBuf tmp=buffer(1024);
            ByteBuf orginial=dataTool.getByteBuf(dataTool.bytes2hex(content));
            orginial.readBytes(head, 0, 5 + 28);//包头部分 明文
            tmp.writeBytes(head);
            orginial.readBytes(data, 0, data.length);//待加解密data长度=总长度-包头33-checkSum1
            //todo 数据写入完毕 计算报文长度 计算checkSum
            if(dataType==0x13) {
                tmp.writeBytes(AES128Tool.decrypt(data, aesKey));//
            }else{
                tmp.writeBytes(AES128Tool.decrypt(data, aesKey));//如果要生成测试数据只需要把这一段改为加密即可
            }
            tmp.markWriterIndex();
            int newLength=tmp.readableBytes()-5;
            tmp.writerIndex(2);
            tmp.writeShort((short) newLength);
            tmp.resetWriterIndex();
            byte[] withOutCheckSum=dataTool.getBytesFromByteBuf(tmp);//without checkSum
            tmp.writeByte(dataTool.getCheckSum(withOutCheckSum));//checkSum
            re=dataTool.getBytesFromByteBuf(tmp);//
            }else{
            re=content;
        }
        return re;
    }

    //根据SocketChannel得到对应的vin
    public  String geVinByAddress(String remoteAddress)
    {
        String vin=connections.get(remoteAddress);
        return vin;
    }

    /**
     * 下行数据加密  平台->Tbox
     * @param content
     * @param password
     * @return
     */
    public  byte[] downDatafilter(byte[]  content, String password) {
        return AES128Tool.encrypt(content, password);
    }
}
