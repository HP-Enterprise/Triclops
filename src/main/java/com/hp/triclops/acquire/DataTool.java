package com.hp.triclops.acquire;

import com.hp.data.bean.tbox.*;
import com.hp.triclops.entity.DiagnosticData;
import com.hp.triclops.entity.Vehicle;
import com.hp.triclops.entity.WarningMessageConversion;
import com.hp.triclops.redis.SocketRedis;
import com.hp.triclops.utils.DateUtil;
import io.netty.buffer.ByteBuf;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.nio.ByteBuffer;
import java.text.DecimalFormat;
import java.util.*;

import com.hp.triclops.repository.VehicleRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import static io.netty.buffer.Unpooled.*;
/**
 * Created by luj on 2015/9/17.
 */
@Component
public class DataTool {


    public static final String msgSendCount_preStr="msgSendCount:";
    public static final String msgCurrentStatus_preStr="msgCurrentStatus:";
    public static final String remoteControlRef_preStr="remoteControlRef:";//远程控制event引用的远程控制db id
    public static final String remote_cmd_value_preStr="remoteCommand";
    public static final String out_cmd_preStr="output:";
    public static final String connection_hashmap_name="tbox-connections";//Tbox连接标志 vin:remoteAddress
    public static final String connection_online_imei_hashmap_name="tbox-connections-imei";//Tbox在线连接标志 remoteAddress:imei 用于提取imei下行报文加密时使用
    public static final String remoteControl_hashmap_name="remoteControl-results";//远程控制结果 vin-eventId:RecordId
    public static final String remoteControlSet_hashmap_name="remoteControlSet-results";//远程控制参数设置结果 vin-eventId:Result
    public static final String tboxkey_hashmap_name="tbox-aeskeys";//存储AES加密的key vin:AesKeyValue

    public static final long msgSendCount_ttl=600l;//数据存储redis中的ttl 10*60s
    public static final long msgCurrentStatus_ttl=600l;//消息发送标志ttl
    public static final long remoteControlResult_ttl=600l;//控制结果ttl
    public static final long remote_cmd_value_ttl=600l;



    private Logger _logger = LoggerFactory.getLogger(DataTool.class);
    @Autowired
    VehicleRepository vehicleRepository;
    @Autowired
    SocketRedis socketRedis;
    public  boolean checkReg(byte[] bytes){
        //校验注册数据
        return true;
    }

    public int getHitSpeed(Integer[] speeds) {
        //todo 根据速度数组得到碰撞前速度
        //todo 传入一个速度数组，计算前后两个速度的差值，返回差值正值最大（即发送最大减速）前的速度
        if(speeds==null){
            return -1;
        }
        if(speeds.length < 0){
            return -1;
        }
        float max = 0f;
        int index = 0;
        for(int i = 0 ; i< speeds.length-1 ; i++){
            float temp = speeds[i] - speeds[i+1];
            if(temp >= max){
                max = temp;
                index = i;
            }
        }
        return speeds[index];
    }

    public float getHitSpeedFromSpeeds(float[] speeds) {
        //todo 根据速度数组得到碰撞前速度
        //todo 传入一个速度数组，计算前后两个速度的差值，返回差值正值最大（即发送最大减速）前的速度
        if(speeds.length < 0){
            return -1f;
        }
        float max = 0f;
        int index = 0;
        for(int i = 0 ; i< speeds.length-1 ; i++){
            float temp = speeds[i] - speeds[i+1];
            if(temp >= max){
                max = temp;
                index = i;
            }
        }
        return speeds[index];
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
      /*  //经纬度除以1000000得到真实值
        String  num = a/1000000+"."+a%1000000;
        return Double.valueOf(num);*/
        //按照0.619协议变更经纬度取值方式
        //double  num = a * 0.00390625*3600;
        // 2016.5.24变化
        double num=a * 0.00390625 /3600;
        BigDecimal bd  =   new  BigDecimal(num);
        bd   =  bd.setScale(6,BigDecimal.ROUND_HALF_DOWN);//四舍五入保留6位小数
        return bd.doubleValue();
    }
    public float getTrueSpeed(int a){
        //得到真实速度值
        float  speed =Float.parseFloat( a/10+"."+a%10);
        return speed;
    }
    public float getTrueAvgOil(int a){
        //得到真实油耗值 0x1ff=511无效值
        if(a==0x1ff){
            return -200f;
        }
        String  avgOil=a/10+"."+a%10;
        return Float.valueOf(avgOil);
    }
    public float getTrueTirePressure(int a){
        //得到真实胎压耗值 0xff=无效值
        if(a==0xff){
            return -200f;
        }
       return a * 2.8f;
    }
    public int getTrueAvgSpeed(int a){
        //得到真实平均车速 0x1ff=无效值
        if(a==0x1ff){
            return -200;
        }
        return a;
    }
    public float getInternTrueTmp(short a){
        //得到车内真实温度
        //分辨率 0.5A，偏移量40，
        //显示范围： -40°C ~+80°C
        //上报数据范围： 0~240
        if(a==0xff){
            return -200f;
        }
        a=a>240?240:a;
        a=a<0?0:a;
        short t=(short)(a-(short)80);
        return t*0.5f;
    }
    public float getOuterTrueTmp(short a){
        //分辨率 0.5A，偏移量40，
        //显示范围： -40°C ~+80°C
        //上报数据范围： 0~240
        if(a==0xff){
            return -200f;
        }
        a=a>252?252:a;
        a=a<0?0:a;
        short t=(short)(a-(short)80);
        return t*0.5f;
    }
    public String getWindowStatus(String bita_b){
        //得到车窗状态 传入两个bit的字符表示,
        // 数据库 车窗信息 0开1半开2关3信号异常
        //参考0.613
        //0x0： 00 Open--0
        //0x1： 01 Intermediate--1
        //0x2： 10 Close--2
        //0x3： 11 Signal invalid--3
        String re="2";
        if(bita_b!=null){
            if(bita_b.equals("00")){
                re="0";
            }else if(bita_b.equals("01")){
                re="1";
            }else if(bita_b.equals("10")){
                re="2";
            }else if(bita_b.equals("11")){
                re="3";
            }
        }
        return re;
    }

    public String getF60WindowStatus(String bita_b){
        //得到车窗状态 传入3个bit的字符表示,
        // 数据库 车窗信息 0开1半开2关3信号异常
        // 车窗信息 0,1,2,3 开 4关
        //参考0.613
        //0x0： 000
        //0x1： 001
        //0x2： 010
        //0x3： 011
        //0x4： 100
        String re="3";
        if(bita_b!=null){
            if(bita_b.equals("000")||bita_b.equals("001")||bita_b.equals("010")||bita_b.equals("011")){
                re="0";
            }else if(bita_b.equals("100")){
                re="2";
            }
        }
        return re;
    }

    public String getSkyWindowStatus(String bita_b){
        //得到天窗状态 传入两个bit的字符表示,
        // 数据库 天窗信息 0开 1半开 2关 3信号异常
        //参考0.613
        //0x0： Stopped--1
        //0x1： closed--2
        //0x2： opened--0
        //0x3： reserved--3

        String re="1";
        if(bita_b!=null){
            if(bita_b.equals("00")){
                re="1";
            }else if(bita_b.equals("01")){
                re="2";
            }else if(bita_b.equals("10")){
                re="0";
            }else if(bita_b.equals("11")){
                re="3";
            }
        }
        return re;
    }
    public String getDoorStatus(String bita_b){
        //得到车门状态 传入两个bit的字符表示,
        // 数据库 车门信息 0开1关2保留3信号异常
        //参考0.613
        //0x0： 00 Close--1
        //0x1： 01 Open--0
        //0x2： 10 Reserved-2
        //0x3： 11 Signal invalid--3
        String re="1";
        if(bita_b!=null){
            if(bita_b.equals("00")){
                re="1";
            }else if(bita_b.equals("01")){
                re="0";
            }else if(bita_b.equals("10")){
                re="2";
            }else if(bita_b.equals("11")){
                re="3";
            }
        }
        return re;
    }


    public String getSeatBeltStatus4M8X(String bita_b){
        //得到M8X安全带状态 传入两个bit的字符表示,
        // 数据库 0没系 1系了 2保留 3信号异常
        //参考0.630
        //0x0： 00 没系
        //0x1： 01 系
        //0x2： 10 保留
        //0x3： 11 Signal invalid--3
        String re="1";
        if(bita_b!=null){
            if(bita_b.equals("00")){
                re="0";
            }else if(bita_b.equals("01")){
                re="1";
            }else if(bita_b.equals("10")){
                re="2";
            }else if(bita_b.equals("11")){
                re="3";
            }
        }
        return re;
    }

    public String getSeatBeltStatus4F60(String bita_b){
        //得到F60安全带状态 传入两个bit的字符表示,
        // 数据库 0没系 1系了 2保留 3信号异常
        //参考0.630
        //0x0： 00 系
        //0x1： 01 FAULT
        //0x2： 10 没系
        //0x3： 11 保留
        String re="1";
        if(bita_b!=null){
            if(bita_b.equals("00")){
                re="1";
            }else if(bita_b.equals("01")){
                re="3";
            }else if(bita_b.equals("10")){
                re="0";
            }else if(bita_b.equals("11")){
                re="2";
            }
        }
        return re;
    }

    public int getDriveRangeFrom3Bytes(byte[] bytes){
        //从3个字节读出数字 无效值0xffffff
        int km=0;
        ByteBuf buf=getByteBuf(bytes2hex(bytes));
        km=buf.readUnsignedMedium();
        if(km==0xffffff){
            km=-200;
        }
        return km;
    }
    public String getLengthString(String str,int length){
        //将给定字符串右补空格为定长字符串
        if(str==null){
            return str;
        }
        if(str.length()>=length){
            return str;
        }
        while (str.length()<length){
            str=str+" ";
        }
        return str;
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

    public  char[] getBitsFrom2Byte(byte[]  bytes){
        String a=new String(getBitsFromByte(bytes[0]))+new String(getBitsFromByte(bytes[1]));
        return a.toCharArray();
    }
    public  char[] getBitsFromInteger(int value){
        //双字节转二进制
        char[] array=new char[16];
        for (int j = 15; j >= 0; j--)
        {
            if (((1 << j) & value) != 0)
            {
                array[15-j]='1';
            }
            else
            {
                array[15-j]='0';
            }
        }
        return array;
    }
    public  char[] getBitsFromLong(long value){
        //4字节转二进制
        char[] array=new char[32];
        for (int j = 31; j >= 0; j--)
        {
            if (((1 << j) & value) != 0)
            {
                array[31-j]='1';
            }
            else
            {
                array[31-j]='0';
            }
        }
        return array;
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

    public Short getWarningInfoFromByte(Byte a){
        //取包含8个数字的数组
        Short re=0;
        String binStr=getBinaryStrFromByte(a);
        char[] array= binStr.toCharArray();//bit 0~1读取char[6] char[7]
        String bita_b=String.valueOf(array[6]) + String.valueOf(array[7]);
        if(bita_b.equals("00")){
            re=0;
        }else if(bita_b.equals("01")){
            re=1;
        }
        return re;
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
        buf.readerIndex(0);
        return result;
    }

    public  String getImeiFromReqData(byte[] data)
    {
        //解析TBOX上行数据包,提取imei
        //imei:37,15
        //vin         :51,17
        String imei="";
        if(data!=null){
            if(data.length>33) {
                imei=new String(data, 11, 15);//serialNum在字节数组中的位置
                ByteBuffer bb= ByteBuffer.allocate(1024);
                bb.put(data);
                bb.flip();
            }
        }
        return imei;
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

    public String getCurrentDate(){
        return DateUtil.format(new Date(),"yyyy-MM-dd HH:mm:ss");
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
            if(data.length>15) {//下行数据包最小长度16
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




    private int getFieldLength(int id){
        //参考文档关于诊断的部分
        int re=0;
        switch(id) {
            case 1://
                re=6;
                break;
            case 2://
                re=1;
                break;
            case 3://
                re=5;
                break;
            case 4://
                re=3;
                break;
            case 5://
                re=7;
                break;
            case 6://
                re=7;
                break;
            case 7://
                re=30;
                break;
            case 8://
                re=2;
                break;
            case 9://
                re=3;
                break;
            case 10://
                re=4;
                break;
            case 11://
                re=3;
                break;
            case 12://
                re=3;
                break;
            case 13://
                re=27;
                break;
            case 14://
                re=4;
                break;
            case 15://
                re=2;
                break;
            case 16://
                re=1;
                break;
            case 17://
                re=1;
                break;
            default:
                break;
        }
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
        boolean result=false;
        byte sum=bytes[0];
        for(int i=1;i<bytes.length-1;i++){
            sum^=bytes[i];
        }
        result = bytes[bytes.length-1]==sum;
        if(!result) {
            _logger.info(">>checkSum:" + Integer.toHexString(sum) + "<>" + Integer.toHexString(bytes[bytes.length - 1]));
        }
        return result;
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
        }else if(applicationId.equals("50")&&messageId.equals("1")) {//远程控制设置指令
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
            re=30;
        } if(applicationId.equals("50")&&messageId.equals("1")){//远程控制参数设置
            re=30;
        }else if(applicationId.equals("49")&&messageId.equals("3")) {//远程控制指令
            re=30;
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
     *//*
    public HashMap<String,String> messageIteratorToMap(Iterator<WarningMessageConversion> iterator){
        HashMap<String,String> messages=new HashMap<String,String>();
        while (iterator.hasNext()){
            WarningMessageConversion warningMessageConversion=iterator.next();
            messages.put(warningMessageConversion.getMessageId(),warningMessageConversion.getGroupMessage());
        }
        return messages;
    }
*/
    /**
     * 从bean得到包含的故障ID
     * @param bean
     * @return 故障ID字符串 ""  "1"  "1,2"
     */
    public String getFailureMesId(FailureMessage bean){
        StringBuilder sb=new StringBuilder();

        char[] _acm=getBitsFromShort(bean.getWarnMsg_acm());
        if(_acm[7]== '0'){//bit0 0 warning MID=1
            sb.append(",1");
        }
        if(_acm[6]== '0'){//bit1 0 warning MID=200
            sb.append(",200");
        }
        if(_acm[5]== '0'){//bit2 0 warning MID=201
            sb.append(",201");
        }
        if(_acm[4]== '0'){//bit3 0 warning MID=202
            sb.append(",202");
        }
        if(_acm[3]== '0'){//bit4 0 warning MID=203
            sb.append(",203");
        }
        if(_acm[2]== '0'){//bit5 0 warning MID=204
            sb.append(",204");
        }
        if(_acm[1]== '0'){//bit6 0 warning MID=205
            sb.append(",205");
        }


        char[] _ic=getBitsFromInteger(bean.getWarnMsg_ic());
        if(_ic[15]== '0'){//bit0 0 warning MID=2
            sb.append(",2");
        }
        if(_ic[14]== '0'){//bit1 0 warning MID=5
            sb.append(",5");
        }
        if(_ic[13]== '0'){//bit2 0 warning MID=10
            sb.append(",10");
        }
        if(_ic[12]== '0'){//bit3 0 warning MID=13
            sb.append(",13");
        }

     /*   if(_ic[11]== '0'){//bit4 0 warning MID=14
            sb.append(",14");
        }
        if(_ic[10]== '0'){//bit5 0 warning MID=15
            sb.append(",15");
        }*/
        if(_ic[9]== '0'){//bit6 0 warning MID=16
            sb.append(",16");
        }
     /*   if(_ic[8]== '0'){//bit7 0 warning MID=17
            sb.append(",17");
        }*/
        if(_ic[7]== '0'){//bit8 0 warning MID=18
            sb.append(",18");
        }
        if(_ic[6]== '0'){//bit9 0 warning MID=19
            sb.append(",19");
        }
      /*  if(_ic[5]== '0'){//bit10 0 warning MID=99
            sb.append(",99");
        }
        if(_ic[4]== '0'){//bit11 0 warning MID=100
            sb.append(",100");
        }*/
        if(_ic[3]== '0'){//bit12 0 warning MID=145
            sb.append(",145");
        }
       /* if(_ic[2]== '0'){//bit13 0 warning MID=147
            sb.append(",147");
        }
        if(_ic[1]== '0'){//bit14 0 warning MID=149
            sb.append(",149");
        }*/


        char[] _escl=getBitsFromShort(bean.getWarnMsg_escl());
        if(_escl[7]== '0'){//bit0 0 warning MID=3
            sb.append(",3");
        }
       /* if(_escl[6]== '0'){//bit1 0 warning MID=60
            sb.append(",60");
        }*/

        char[] _bcm=getBitsFromLong(bean.getWarnMsg_bcm());
        if(_bcm[31]== '0'){//bit0 0 warning MID=4
            sb.append(",4");
        }
      /*  if(_bcm[30]== '0'){//bit1 0 warning MID=73
            sb.append(",73");
        }
        if(_bcm[29]== '0'){//bit2 0 warning MID=76
            sb.append(",76");
        }
        if(_bcm[28]== '0'){//bit3 0 warning MID=124
            sb.append(",124");
        }
       if(_bcm[27]== '0'){//bit4 0 warning MID=125
            sb.append(",125");
        }
        if(_bcm[26]== '0'){//bit5 0 warning MID=126
            sb.append(",126");
        }
        if(_bcm[25]== '0'){//bit6 0 warning MID=127
            sb.append(",127");
        }
        if(_bcm[24]== '0'){//bit7 0 warning MID=128
            sb.append(",128");
        }
        if(_bcm[23]== '0'){//bit8 0 warning MID=129
            sb.append(",129");
        }
        if(_bcm[22]== '0'){//bit9 0 warning MID=130
            sb.append(",130");
        }
        if(_bcm[21]== '0'){//bit10 0 warning MID=131
            sb.append(",131");
        }
        if(_bcm[20]== '0'){//bit11 0 warning MID=132
            sb.append(",132");
        }
        if(_bcm[19]== '0'){//bit12 0 warning MID=133
            sb.append(",133");
        }
        if(_bcm[18]== '0'){//bit13 0 warning MID=134
            sb.append(",134");
        }
        if(_bcm[17]== '0'){//bit14 0 warning MID=135
            sb.append(",135");
        }
        if(_bcm[16]== '0'){//bit15 0 warning MID=136
            sb.append(",136");
        }
        if(_bcm[15]== '0'){//bit16 0 warning MID=137
            sb.append(",137");
        }
        if(_bcm[14]== '0'){//bit17 0 warning MID=138
            sb.append(",138");
        }
        if(_bcm[13]== '0'){//bit18 0 warning MID=139
            sb.append(",139");
        }*/
        if(_bcm[12]== '0'){//bit19 0 warning MID=59
            sb.append(",59");
        }
      /*  if(_bcm[11]== '0'){//bit20 0 warning MID=121
            sb.append(",121");
        }*/


        char[] _esc=getBitsFromInteger(bean.getWarnMsg_esc());
        if(_esc[15]== '0'){//bit0 0 warning MID=6
            sb.append(",6");
        }
        if(_esc[14]== '0'){//bit1 0 warning MID=9
            sb.append(",9");
        }
        if(_esc[13]== '0'){//bit2 0 warning MID=11
            sb.append(",11");
        }
        if(_esc[12]== '0'){//bit3 0 warning MID=12
            sb.append(",12");
        }
        if(_esc[11]== '0'){//bit4 0 warning MID=83
            sb.append(",83");
        }
        if(_esc[10]== '0'){//bit5 0 warning MID=87
            sb.append(",87");
        }
        if(_esc[9]== '0'){//bit6 0 warning MID=105
            sb.append(",105");
        }
        if(_esc[8]== '0'){//bit7 0 warning MID=120
            sb.append(",120");
        }
        if(_esc[7]== '0'){//bit8 0 warning MID=118
            sb.append(",118");
        }
        if(_esc[6]== '0'){//bit9 0 warning MID=122
            sb.append(",122");
        }
        if(_esc[5]== '0'){//bit10 0 warning MID=110
            sb.append(",110");
        }
        if(_esc[4]== '0'){//bit11 0 warning MID=121
            sb.append(",121");
        }

       /* char[] _tpms=getBitsFromShort(bean.getWarnMsg_tpms());
        if(_tpms[7]== '0'){//bit0 0 warning MID=14
            sb.append(",14");
        }
        if(_tpms[6]== '0'){//bit0 0 warning MID=15
            sb.append(",15");
        }
        if(_tpms[5]== '0'){//bit0 0 warning MID=16
            sb.append(",16");
        }
        if(_tpms[4]== '0'){//bit0 0 warning MID=17
            sb.append(",17");
        }
        if(_tpms[3]== '0'){//bit0 0 warning MID=78
            sb.append(",78");
        }
        if(_tpms[2]== '0'){//bit0 0 warning MID=79
            sb.append(",79");
        }
        if(_tpms[1]== '0'){//bit0 0 warning MID=140
            sb.append(",140");
        }*/

        char[] _dme=getBitsFromInteger(bean.getWarnMsg_dme());
        if(_dme[15]== '0'){//bit0 0 warning MID=20
            sb.append(",20");
        }
        if(_dme[14]== '0'){//bit1 0 warning MID=21
            sb.append(",21");
        }
        if(_dme[13]== '0'){//bit2 0 warning MID=23
            sb.append(",23");
        }
      /*  if(_dme[12]== '0'){//bit3 0 warning MID=89
            sb.append(",89");
        }*/
        if(_dme[11]== '0'){//bit4 0 warning MID=95
            sb.append(",95");
        }
         /*  if(_dme[10]== '0'){//bit5 0 warning MID=213
            sb.append(",213");
        }*/
        if(_dme[9]== '0'){//bit6 0 warning MID=97
            sb.append(",97");
        }
       /* if(_dme[8]== '0'){//bit7 0 warning MID=98
            sb.append(",98");
        }
        if(_dme[7]== '0'){//bit8 0 warning MID=141
            sb.append(",141");
        }*/
        if(_dme[6]== '0'){//bit9 0 warning MID=90
            sb.append(",90");
        }

        char[] _tcu=getBitsFromShort(bean.getWarnMsg_tcu());
        if(_tcu[7]== '0'){//bit0 0 warning MID=22
            sb.append(",22");
        }
        if(_tcu[6]== '0'){//bit1 0 warning MID=96
            sb.append(",96");
        }
        if(_tcu[5]== '0'){//bit2 0 warning MID=148
            sb.append(",148");
        }
       /* if(_tcu[4]== '0'){//bit3 0 warning MID=88
            sb.append(",88");
        }*/
        if(_tcu[3]== '0'){//bit4 0 warning MID=146
            sb.append(",146");
        }


        char[] _pdc_bsw=getBitsFromShort(bean.getWarnMsg_pdc_bsw());
        if(_pdc_bsw[7]== '0'){//bit0 0 warning MID=50
            sb.append(",50");
        }
        if(_pdc_bsw[6]== '0'){//bit1 0 warning MID=51
            sb.append(",51");
        }

        char[] _sesam=getBitsFromInteger(bean.getWarnMsg_sesam());
      /*  if(_sesam[15]== '0'){//bit0 0 warning MID=57
            sb.append(",57");
        }
        if(_sesam[14]== '0'){//bit1 0 warning MID=58
            sb.append(",58");
        }*/
        if(_sesam[13]== '0'){//bit2 0 warning MID=61
            sb.append(",61");
        }
       /* if(_sesam[12]== '0'){//bit3 0 warning MID=62
            sb.append(",62");
        }
        if(_sesam[11]== '0'){//bit4 0 warning MID=63
            sb.append(",63");
        }
        if(_sesam[10]== '0'){//bit5 0 warning MID=64
            sb.append(",64");
        }
        if(_sesam[9]== '0'){//bit6 0 warning MID=65
            sb.append(",65");
        }*/

        if(_sesam[8]== '0'){//bit7 0 warning MID=91
            sb.append(",91");
        }
        if(_sesam[7]== '0'){//bit8 0 warning MID=92
            sb.append(",92");
        }
        if(_sesam[6]== '0'){//bit9 0 warning MID=93
            sb.append(",93");
        }
      /*  if(_sesam[5]== '0'){//bit10 0 warning MID=94
            sb.append(",94");
        }
        if(_sesam[4]== '0'){//bit11 0 warning MID=121
            sb.append(",121");
        }
         if(_sesam[3]== '0'){//bit12 0 warning MID=142
            sb.append(",142");
        }
        if(_sesam[2]== '0'){//bit13 0 warning MID=56
            sb.append(",56");
        }*/

        char[] _tbox=getBitsFromShort(bean.getWarnMsg_tbox());
       /* if(_tbox[7]== '0'){//bit0 0 warning MID=143
            sb.append(",143");
        }
        if(_tbox[6]== '0'){//bit1 0 warning MID=144
            sb.append(",144");
        }*/

        String str=sb.toString();
        if(str.startsWith(",")){
            str=str.substring(1);
        }
        return str;
    }

    /**
     * 从bean得到包含的故障ID
     * @param bean
     * @return 故障ID字符串 ""  "1"  "1,2"
     */
     public String getDataResendFailureMesId(DataResendFailureData bean){

         FailureMessage failureMessage=new FailureMessage();
         failureMessage.setWarnMsg_acm(bean.getWarnMsg_acm());
         failureMessage.setWarnMsg_ic(bean.getWarnMsg_ic());
         failureMessage.setWarnMsg_escl(bean.getWarnMsg_escl());
         failureMessage.setWarnMsg_bcm(bean.getWarnMsg_bcm());
         failureMessage.setWarnMsg_esc(bean.getWarnMsg_esc());
         failureMessage.setWarnMsg_tpms(bean.getWarnMsg_tpms());
         failureMessage.setWarnMsg_dme(bean.getWarnMsg_dme());
         failureMessage.setWarnMsg_tcu(bean.getWarnMsg_tcu());
         failureMessage.setWarnMsg_pdc_bsw(bean.getWarnMsg_pdc_bsw());
         failureMessage.setWarnMsg_sesam(bean.getWarnMsg_sesam());
         failureMessage.setWarnMsg_tbox(bean.getWarnMsg_tbox());
         failureMessage.setWarnMsg_scu(bean.getWarnMsg_scu());
         return getFailureMesId(failureMessage);
        }

    @Value("${com.hp.acquire.dataserver-warningDataSuffix}")
    private String warningDataSuffix;

    @Value("${com.hp.acquire.datahandler-handleSuffix}")
    private String handleSuffix;



    /**
     * 报警数据存储后缀
     * @return
     */
    public String getWarningDataSuffix(){
           return warningDataSuffix;
    }

    /**
     * 处理的目标数据后缀
     * @return
     */
    public List<String> getHandleSuffix(){
        List<String> re=new ArrayList<String>();
        if(handleSuffix!=null){
            String[] array=handleSuffix.split(",");
            for(int i=0;i<array.length;i++){
                re.add(array[i]);
            }
        }
        return re;
    }
    /**
     * 从redis获取可用data-handler节点
     * @return
     */
    public  Set<String> getRealTimeDataSuffixesFromRedis(){
        return socketRedis.getSmembers("available-data-handler");
    }

    /**
     * 从rediskey 拿到vin  key=input1:123456  vin=123456
     * @param key
     * @return
     */
    public String getVinFromkey(String key){
      String vin=null;
        try{
        if(key!=null){
            String[] arry=key.split(":");
            if(arry.length==2){
              vin=arry[1];
          }
        }
        }catch (Exception e){e.printStackTrace();}
    return vin;
    }



    /**
     * 基于redis的可用节点返回数据写group
     * @return random group suffix（from redis available cluster node）
     */
    public  String getRandomRealTimeDataSuffix(){
        String suffix="";
        Set<String> suffixes=getRealTimeDataSuffixesFromRedis();
        String _warningDataSuffix=getWarningDataSuffix();
        //排除warningDataHandle节点
        if(suffixes.contains(_warningDataSuffix)){
            suffixes.remove(_warningDataSuffix);
        }
        int _suffixes_size=suffixes.size();
        if(_suffixes_size>0){
            String[] suffixesArray = suffixes.toArray(new String[_suffixes_size-1]);
            int max=_suffixes_size;
            int min=0;
            Random random = new Random();
            int index = random.nextInt(max)%(max-min+1) + min;
            suffix=suffixesArray[index];
        }
        return suffix;
    }

    /**
     * 解码报警数据包
     * @param msg
     * @return
     */
    public WarningMessage decodeWarningMessage(String msg){
        WarningMessage warningMessage=new WarningMessage();
        byte[] data=getBytesFromByteBuf(getByteBuf(msg));
        ByteBuf buf=getByteBuf(bytes2hex(data));
        warningMessage.setHead((int) buf.readShort());
        warningMessage.setLength((int) buf.readShort());
        warningMessage.setTestFlag((short) buf.readByte());
        warningMessage.setSendingTime((long) buf.readInt());
        warningMessage.setApplicationID((short) buf.readByte());
        warningMessage.setMessageID((short) buf.readByte());
        byte[] imeiBytes=new byte[15];
        buf.readBytes(imeiBytes);
        warningMessage.setImei(new String(imeiBytes));
        warningMessage.setProtocolVersionNumber((short) buf.readByte());
        warningMessage.setVehicleID((short) buf.readByte());
        warningMessage.setVehicleModel((short) buf.readByte());
        warningMessage.setTripID((int) buf.readShort());
        warningMessage.setReserved((int) buf.readShort());
        warningMessage.setEventID((long) buf.readInt());
        warningMessage.setIsLocation((short) buf.readByte());
        warningMessage.setLatitude((long) buf.readInt());
        warningMessage.setLongitude((long) buf.readInt());
        warningMessage.setSpeed((int) buf.readShort());
        warningMessage.setHeading((int) buf.readShort());
        warningMessage.setSrsWarning(buf.readByte());
        warningMessage.setCrashWarning(buf.readByte());
        warningMessage.setAtaWarning(buf.readByte());
       // if(warningMessage.getSrsWarning()==(byte)1) { 协议0627改为始终发送车速报文
            warningMessage.setSafetyBeltCount((short) buf.readByte());
            byte[] speedLastBytes = new byte[300];
            Integer[] speeds = new Integer[150];
            buf.readBytes(speedLastBytes);
            ByteBuf bu=getByteBuf(bytes2hex(speedLastBytes));
            for(int i=0;i<150;i++){
                speeds[i]=(int)bu.readShort();
            }
            warningMessage.setVehicleSpeedLast(speeds);
       // }
        return warningMessage;
    }

    /**
     * 解码补发报警数据包
     * @param msg
     * @return
     */
    public DataResendWarningMes decodeResendWarningMessage(String msg){
        DataResendWarningMes warningMessage=new DataResendWarningMes();
        byte[] data=getBytesFromByteBuf(getByteBuf(msg));
        ByteBuf buf=getByteBuf(bytes2hex(data));
        warningMessage.setHead((int) buf.readShort());
        warningMessage.setLength((int) buf.readShort());
        warningMessage.setTestFlag((short) buf.readByte());
        warningMessage.setSendingTime((long) buf.readInt());
        warningMessage.setApplicationID((short) buf.readByte());
        warningMessage.setMessageID((short) buf.readByte());
        byte[] imeiBytes=new byte[15];
        buf.readBytes(imeiBytes);
        warningMessage.setImei(new String(imeiBytes));
        warningMessage.setProtocolVersionNumber((short) buf.readByte());
        warningMessage.setVehicleID((short) buf.readByte());
        warningMessage.setVehicleModel((short) buf.readByte());
        warningMessage.setTripID((int) buf.readShort());
        warningMessage.setReserved((int) buf.readShort());
        warningMessage.setEventID((long) buf.readInt());
        warningMessage.setIsLocation((short) buf.readByte());
        warningMessage.setLatitude((long) buf.readInt());
        warningMessage.setLongitude((long) buf.readInt());
        warningMessage.setSpeed((int) buf.readShort());
        warningMessage.setHeading((int) buf.readShort());
        warningMessage.setSrsWarning(buf.readByte());
        warningMessage.setCrashWarning(buf.readByte());
        warningMessage.setAtaWarning(buf.readByte());
       // if(warningMessage.getSrsWarning()==(byte)1) {协议0627改为始终发送车速报文
            warningMessage.setSafetyBeltCount((short) buf.readByte());
            byte[] speedLastBytes = new byte[300];
            Integer[] speeds = new Integer[150];
            buf.readBytes(speedLastBytes);
            ByteBuf bu=getByteBuf(bytes2hex(speedLastBytes));
            for(int i=0;i<150;i++){
                speeds[i]=(int)bu.readShort();
            }
            warningMessage.setVehicleSpeedLast(speeds);
     //   }
        return warningMessage;
    }


    /**
     * 解码驾驶行为数据包
     * @param msg
     * @return
     */
    public DrivingBehaviorMes decodeDrivingBehaviorMes(String msg){
        DrivingBehaviorMes drivingBehaviorMes=new DrivingBehaviorMes();
        byte[] data=getBytesFromByteBuf(getByteBuf(msg));
        ByteBuf buf=getByteBuf(bytes2hex(data));
        drivingBehaviorMes.setHead((int) buf.readShort());
        drivingBehaviorMes.setLength((int) buf.readShort());
        drivingBehaviorMes.setTestFlag((short) buf.readByte());
        drivingBehaviorMes.setSendingTime((long) buf.readInt());
        drivingBehaviorMes.setApplicationID((short) buf.readByte());
        drivingBehaviorMes.setMessageID((short) buf.readByte());
        byte[] imeiBytes=new byte[15];
        buf.readBytes(imeiBytes);
        drivingBehaviorMes.setImei(new String(imeiBytes));
        drivingBehaviorMes.setProtocolVersionNumber((short) buf.readByte());
        drivingBehaviorMes.setVehicleID((short) buf.readByte());
        drivingBehaviorMes.setVehicleModel((short) buf.readByte());
        drivingBehaviorMes.setTripID((int) buf.readShort());
        drivingBehaviorMes.setReserved((int) buf.readShort());
        drivingBehaviorMes.setEventID((long) buf.readInt());

        Integer[] lateralAcceleration = new Integer[40];
        for(int i=0;i<lateralAcceleration.length;i++){
            lateralAcceleration[i]=(int)buf.readUnsignedShort();//16bit
        }
        drivingBehaviorMes.setLateralAcceleration(lateralAcceleration);

        Integer[] driveAcceleration = new Integer[40];
        for(int i=0;i<driveAcceleration.length;i++){
            driveAcceleration[i]=(int)buf.readUnsignedShort();//16bit
        }
        drivingBehaviorMes.setDriveAcceleration(driveAcceleration);

        Short[] brake = new Short[40];
        for(int i=0;i<brake.length;i++){
            brake[i]=(short)buf.readByte();//8bit
        }
        drivingBehaviorMes.setBrake(brake);

        Integer[] speed = new Integer[40];
        for(int i=0;i<speed.length;i++){
            speed[i] = (int)buf.readUnsignedShort();//16bit
        }
        drivingBehaviorMes.setSpeed(speed);

        Integer[] lws = new Integer[40];
        for(int i=0;i<lws.length;i++){
            lws[i]=buf.readUnsignedMedium();//24bit
        }
        drivingBehaviorMes.setLws(lws);

        Integer[] bcvol = new Integer[40];
        for(int i=0;i<bcvol.length;i++){
            bcvol[i] =buf.readUnsignedMedium();//24bit
        }
        drivingBehaviorMes.setBcvol(bcvol);

        Short[] cruise = new Short[40];
        for(int i=0;i<cruise.length;i++){
            cruise[i]=(short)buf.readByte();//8bit
        }
        drivingBehaviorMes.setCruise(cruise);



        return drivingBehaviorMes;
    }



    /**
     *四舍五入保留指定小数位数
     * @param value
     * @param num
     * @return
     */
    public float getRoundHalfDown(float value,int num){
        BigDecimal bd  =   new  BigDecimal((double)value);
        bd   =  bd.setScale(num,BigDecimal.ROUND_HALF_DOWN);//四舍五入保留 num 位小数
       float re=bd.floatValue();
        return re;
    }

    public  String getRandomString(int length) { //length表示生成字符串的长度
        String base = "abcdefghijklmnopqrstuvwxyz0123456789";
        Random random = new Random();
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < length; i++) {
            int number = random.nextInt(base.length());
            sb.append(base.charAt(number));
        }
        return sb.toString();
    }



    /**
     * 从给定的数组中去length位的值，从低位到高位
     * @param datas
     * @param length
     * @return
     */
    public int getValueFromBytes(char[] datas,int length){
        if(datas.length<length){
            _logger.info("getValueFromBytes参数错误"+datas+">"+length);
            return 0;
        }
        int re=0;
        for(int i=0;i<length;i++){
            int a=(int)(Math.pow((double)2,(double)i));
            re+=a*Integer.parseInt(String.valueOf((datas[datas.length-1-i])));
        }
        return re;
    }

    /**
     * 计算急加速 急减速 急转弯值
     * @param vals
     * @param mode 1判断急加速（+）  2判断急减速（-）  3判断急转弯（+ -）
     * @return
     */
    public int calcSpeed(Integer[] vals,int mode){
        int count=0;
        double temp=0;
        double baseValA=9.8*3;
        double baseValB=-9.8*3;
        if(vals!=null) {
            for (int i = 0; i <vals.length; i++) {
                if(vals[i]==0xffff){//无效值0xffff
                    continue;
                }
                temp=vals[i]*0.002-65;
                if(mode==1){
                    if(temp>=baseValA){
                        count++;
                    }
                }else  if(mode==2){
                    if(temp<=baseValB){
                        count++;
                    }
                }else  if(mode==3){
                    if(temp>=baseValA||temp<=baseValB){
                        count++;
                    }
                }

            }
        }
        return count;
    }

}
