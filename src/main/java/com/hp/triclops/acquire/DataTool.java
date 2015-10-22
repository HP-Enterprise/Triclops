package com.hp.triclops.acquire;

import com.hp.triclops.entity.Vehicle;
import com.hp.triclops.entity.WarningMessageConversion;
import io.netty.buffer.ByteBuf;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.math.BigInteger;
import java.nio.ByteBuffer;
import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import com.hp.triclops.repository.VehicleRepository;
import org.springframework.stereotype.Component;
import static io.netty.buffer.Unpooled.*;
/**
 * Created by luj on 2015/9/17.
 */
@Component
public class DataTool {


    public static final String msgSendCount_preStr="msgSendCount:";
    public static final String msgCurrentStatus_preStr="msgCurrentStatus:";
    public static final String remote_cmd_value_preStr="remoteCommand:";
    public static final String out_cmd_preStr="output:";

    private Logger _logger = LoggerFactory.getLogger(DataTool.class);
    @Autowired
    VehicleRepository vehicleRepository;
    public  boolean checkReg(byte[] bytes){
        //校验注册数据
        return true;
    }

    public  String getIp(byte[] bytes){
        //IP地址转换 00 00 C0 A8 01 01 读出192.168.1.1
        String re="";
        StringBuilder sb=new StringBuilder();
        if (bytes.length==6){
           if(bytes[0]==0x00&&bytes[1]==0x00){
            //ipv4
            sb.append(bytes[2]&0xFF);
            sb.append(".");
            sb.append(bytes[3]&0xFF);
            sb.append(".");
            sb.append(bytes[4]&0xFF);
            sb.append(".");
            sb.append(bytes[5]&0xFF);
        }else{//ipv6存储为6个byte并还原的方法暂无相关资料，暂未实现
            sb.append("ipv6");
           }
           re=sb.toString();
        }
        return re;
    }

    public byte[] getIpBytes(String ip){
        //IP地址转换  192.168.1.1读出 00 00 C0 A8 01 01
        String[] ips=ip.split("\\.");
        byte[] bytes = new byte[]{(byte)0,(byte)0,(byte)Integer.parseInt(ips[0]),(byte)Integer.parseInt(ips[1]),(byte)Integer.parseInt(ips[2]),(byte)Integer.parseInt(ips[3])};
        return bytes;
    }

    public  String getBinaryStrFromByte(byte b)
    {
        //将byte转换层二进制字符串 (byte)170  ->> 10101010
        String result ="";
        byte a = b;
        for (int i = 0; i < 8; i++)
        {
            byte c=a;
            a=(byte)(a>>1);
            a=(byte)(a<<1);
            if(a==c){
                result="0"+result;
            }else{
                result="1"+result;
            }
            a=(byte)(a>>1);
        }
        return result;
    }

    public  byte getApplicationType(byte[] bytes){
        //返回数据包操作类型对应的byte
        byte data=0;
        if(bytes!=null){
            if(bytes.length>9) {
                data=bytes[9];
            }
        }
        return data;
    }
    public  byte getMessageId(byte[] bytes){
        //返回数据包操作类型对应的byte
        byte data=0;
        if(bytes!=null){
            if(bytes.length>10) {
                data=bytes[10];
            }
        }
        return data;
    }


    public  String bytes2hex(byte[] bArray) {
        //字节数据转16进制字符串
        StringBuffer sb = new StringBuffer(bArray.length);
        String sTemp;
        for (int i = 0; i < bArray.length; i++) {
            sTemp = Integer.toHexString(0xFF & bArray[i]);
            if (sTemp.length() < 2)
                sb.append(0);
            sb.append(sTemp.toUpperCase());
        }
        return getSpaceHex(sb.toString());
    }


    public int getCurrentSeconds(){
        //返回当前时间的秒数
        int currentSeconds=Integer.valueOf(String.valueOf(new Date().getTime()/1000));
        return currentSeconds;
    }

    public Date seconds2Date(long seconds){
        //时间的秒数转换成Date
        Date d=new Date(seconds*1000L);
        return d;
    }

    public double getTrueLatAndLon(long a){
        //经纬度除以1000000得到真实值
        String  num = a/1000000+"."+a%1000000;
        return Double.valueOf(num);
    }
    public float getTrueSpeed(int a){
        //得到真实速度值
        float  speed =Float.parseFloat( a/10+"."+a%10);
        return speed;
    }
    public float getTrueAvgOil(int a){
        //得到真实油耗值
        String  avgOil=a/10+"."+a%10;
        return Float.valueOf(avgOil);
    }
    public Short getTrueTmp(short a){
        //得到真实温度
        return (short)(a-(short)40);
    }
    public String  getEngineConditionInfo(short s){
         /*
        得到发动机状态信息
        0:engine stop
        1:engine start
        2:idle speed
        3:part load
        4:trailling throttle
        5:full load
        6:Fuel Cut Off
        7:undefined
        数据超出范围时按7(undefined)处理
         */
        byte b=(byte)s;
        String re="";
        switch(b)
        {
            case 0x00://
               re="0";
                break;
            case 0x01://
                re="1";
                break;
            case 0x02://
                re="2";
                break;
            case 0x03://
                re="3";
                break;
            case 0x04://
                re="4";
                break;
            case 0x05://
                re="5";
                break;
            case 0x06://
                re="6";
                break;
            case 0x07://
                re="7";
                break;
            default:
                re="7";
                break;
        }
      return re;
    }


    public double getTrueBatteryVoltage(int a){
        //得到真实蓄电池电压
        String  v=a/1000+"."+a%1000;
        return Double.valueOf(v);
    }

    public char[] getBitsFromShort(short a){
        //取包含8个数字的数组
        String binStr=getBinaryStrFromByte((byte)a);
        return binStr.toCharArray();
    }
    public char[] getBitsFromByte(Byte a){
        //取包含8个数字的数组
        String binStr=getBinaryStrFromByte(a);
        return binStr.toCharArray();
    }

    public  boolean checkVinAndSerialNum(String vin,String serialNum){
        //调用平台db接口,校验vin和SerialNumber 性能测试时改为始终返回true
        // return true;
        boolean checkResult=false;
        Vehicle v=vehicleRepository.findByVinAndTbox(vin, serialNum);
        if(v!=null){
            checkResult=true;
        }
        return checkResult;
    }

    public  String getSpaceHex(String str){
        //将不带空格的16进制字符串加上空格
        String re="";
        String regex = "(.{2})";
        re = str.replaceAll (regex, "$1 ");
        return re;
    }
    public  ByteBuffer getByteBuffer(String str){
        //根据16进制字符串得到buffer
        ByteBuffer bb= ByteBuffer.allocate(1024);
        String[] command=str.split(" ");
        byte[] abc=new byte[command.length];
        for(int i=0;i<command.length;i++){
            abc[i]=Integer.valueOf(command[i],16).byteValue();
        }
        bb.put(abc);
        bb.flip();
        return bb;
    }
    public  ByteBuf getByteBuf(String str){
        //根据16进制字符串得到ByteBuf对象(netty)
        ByteBuf bb=buffer(1024);

        String[] command=str.split(" ");
        byte[] abc=new byte[command.length];
        for(int i=0;i<command.length;i++){
            abc[i]=Integer.valueOf(command[i],16).byteValue();
        }
        bb.writeBytes(abc);
        return bb;
    }


    public  byte[] getBytesFromByteBuffer(ByteBuffer buff){
        byte[] result = new byte[buff.remaining()];
        if (buff.remaining() > 0) {
            buff.get(result, 0, buff.remaining());
        }
        return result;
    }

    public  static byte[] getBytesFromByteBuf(ByteBuf buf){
        //基于netty
        byte[] result = new byte[buf.readableBytes()];
        buf.readBytes(result, 0, buf.readableBytes());
        return result;
    }
    public   HashMap<String,String> getVinDataFromRegBytes(byte[] data)
    {
        //解析注册数据包,提取vin和SerialNumber
        //serialNumber:37,12
        //vin         :51,17
        String serialNum="";
        String vin="";
        int eventId=0;
        HashMap<String,String> re=new HashMap<String ,String>();
        if(data!=null){
            if(data.length>67) {
                serialNum=new String(data, 37, 12);//serialNum在字节数组中的位置
                vin=new String(data, 49, 17);//vin在字节数组中的位置
                ByteBuffer bb= ByteBuffer.allocate(1024);
                bb.put(data);
                bb.flip();
                eventId=  bb.getInt(33);
            }
        }
        re.put("eventId",String.valueOf(eventId));
        re.put("vin", vin);
        re.put("serialNum", serialNum);
        return re;
    }

    public   HashMap<String,Object> getApplicationIdAndMessageIdFromDownBytes(String msg)
    {
        //解析下行数据包,提取ApplicationId和MessageId
        //eventId      :33
        //ApplicationId:9
        //MessageId    :10

        //String ApplicationId="";
        byte[] data=getBytesFromByteBuf(getByteBuf(msg));
        byte applicationId=0;
        byte messageId=0;
        int eventId=0;
        HashMap<String,Object> re=new HashMap<String ,Object>();
        if(data!=null){
            if(data.length>18) {
                ByteBuffer bb= ByteBuffer.allocate(1024);
                bb.put(data);
                bb.flip();
                applicationId=bb.get(9);
                messageId=bb.get(10);
                eventId= bb.getInt(11);
            }
        }
        re.put("applicationId",applicationId);
        re.put("messageId",messageId);
        re.put("eventId",eventId);
        return re;
    }





    public   boolean checkByteArray(byte[] data)
    {
        //校验数据包是否合法
        // 包头 0X23 0X23  2个字节长度
        //包尾 将编码后的报文（ Message Header -- Application Data）进行异或操作，1个字节长度
        boolean result=false;
        if(data!=null){
            if(data.length>2) {
                if (data[0] == 0x23 && data[1] == 0x23 && checkSum(data)) {
                    result = true;
                }
            }
        }
        return result;
    }
    public  boolean checkSum(byte[] bytes){
        //将字节数组除了最后一位的部分进行异或操作，与最后一位比较
        //校验数据包尾
        //将编码后的报文（ Message Header -- Application Data）进行异或操作， 1 个字节长度
        byte sum=bytes[0];
        for(int i=1;i<bytes.length-1;i++){
            sum^=bytes[i];
        }
        _logger.info(">>checkSum:" + Integer.toHexString(sum) + "<>" + Integer.toHexString(bytes[bytes.length - 1]));
        return bytes[bytes.length-1]==sum;
    }

    public  byte getCheckSum(byte[] bytes){
        //将字节数组进行异或操作求和
        byte sum=bytes[0];
        for(int i=1;i<bytes.length;i++){
            sum^=bytes[i];
        }
        return sum;
    }


    public int getMaxSendCount(String applicationId,String messageId){
        //某一消息的下发最大发送次数 参考文档
        //为什么写这么多的if elseif ?这样更像参数配置 易懂易改。
        int re=3;
        if(applicationId.equals("49")&&messageId.equals("1")){//远程控制预指令
            re=3;
        }else if(applicationId.equals("49")&&messageId.equals("3")) {//远程控制指令
            re=3;
        }else if(applicationId.equals("65")&&messageId.equals("1")){//参数查询
            re=3;
        }else if(applicationId.equals("66")&&messageId.equals("1")){//远程诊断
            re=3;
        }else if(applicationId.equals("81")&&messageId.equals("1")){//上报数据设置
            re=3;
        }else if(applicationId.equals("82")&&messageId.equals("1")){//参数设置
            re=3;
        }
        //参数升级比较复杂
        //...
        return re;
    }
    public int getTimeOutSeconds(String applicationId,String messageId){
        //某一消息的下发超时时间（秒） 参考文档
        int re=60;
        if(applicationId.equals("49")&&messageId.equals("1")){//远程控制
            re=60;
        }else if(applicationId.equals("49")&&messageId.equals("3")) {//远程控制指令
            re=60;
        }else if(applicationId.equals("65")&&messageId.equals("1")){//参数查询
            re=60;
        }else if(applicationId.equals("66")&&messageId.equals("1")){//远程诊断
            re=60;
        }else if(applicationId.equals("81")&&messageId.equals("1")){//上报数据设置
            re=60;
        }else if(applicationId.equals("82")&&messageId.equals("1")){//参数设置
            re=60;
        }
        //参数升级比较复杂
        //...
        return re;
    }

    /**
     * 报警消息转换
     * @param iterator 报警消息集合
     * @return 报警消息hashmap
     */
    public HashMap<Short,String> messageIteratorToMap(Iterator<WarningMessageConversion> iterator){
        HashMap<Short,String> messages=new HashMap<Short,String>();
        while (iterator.hasNext()){
            WarningMessageConversion warningMessageConversion=iterator.next();
            messages.put(warningMessageConversion.getMessageId(),warningMessageConversion.getMessageZh());
        }
        return messages;
    }
}
