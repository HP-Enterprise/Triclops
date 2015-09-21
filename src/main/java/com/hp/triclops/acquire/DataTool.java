package com.hp.triclops.acquire;

import com.hp.triclops.entity.Vehicle;
import org.springframework.beans.factory.annotation.Autowired;

import java.math.BigInteger;
import java.nio.ByteBuffer;
import java.util.HashMap;
import java.util.Date;
import com.hp.triclops.repository.VehicleRepository;
import org.springframework.stereotype.Component;

/**
 * Created by luj on 2015/9/17.
 */
@Component
public class DataTool {

    @Autowired
    VehicleRepository vehicleRepository;
    public  boolean checkReg(byte[] bytes){
        //校验注册数据
        return true;
    }

     public  byte getApplicationType(byte[] bytes){
        //返回数据包操作类型对应的byte
        byte data=0;
        boolean result=false;
        if(bytes!=null){
            if(bytes.length>9) {
                data=bytes[9];
            }
        }
        return data;
    }


    public  String bytes2hex(byte[] bytes)
    {
        /**
         * 第一个参数的解释，记得一定要设置为1
         *  signum of the number (-1 for negative, 0 for zero, 1 for positive).
         */
        BigInteger bigInteger = new BigInteger(1, bytes);
        return getSpaceHex(bigInteger.toString(16));
    }

    public  byte[] decodeHexToBytes(char[] data) {
        int len = data.length;
        if ((len & 0x01) != 0) {
            throw new RuntimeException("Odd number of characters.");
        }
        byte[] out = new byte[len >> 1];
        // two characters form the hex value.
        for (int i = 0, j = 0; j < len; i++) {
            int f = toDigit(data[j], j) << 4;
            j++;
            f = f | toDigit(data[j], j);
            j++;
            out[i] = (byte) (f & 0xFF);
        }
        return out;
    }
    protected  int toDigit(char ch, int index) {
        int digit = Character.digit(ch, 16);
        if (digit == -1) {
            throw new RuntimeException("Illegal hexadecimal character " + ch
                    + " at index " + index);
        }
        return digit;
    }
    public  boolean checkVinAndSerialNum(String vin,String serialNum){
        //调用平台接口,校验vin和SerialNumber
        return true;
     /*   boolean checkResult=false;
        Vehicle v=vehicleRepository.findByVinAndTbox(vin, serialNum);
        if(v!=null){
            checkResult=true;
        }
        return checkResult;*/
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

    public  ByteBuffer getRegResultByteBuffer(byte[] data,int eventId,boolean check){
        String byteString="23 23 00 0B 01 ";//包头和size
        //根据注册校验结果，形成返回数据包
        ByteBuffer bb= ByteBuffer.allocate(1024);
        String[] command=byteString.split(" ");
        byte[] abc=new byte[command.length];
        for(int i=0;i<command.length;i++){
            abc[i]=Integer.valueOf(command[i],16).byteValue();
        }
        bb.put(abc);
        int currentSeconds=Integer.valueOf(String.valueOf(new Date().getTime()/1000));
        bb.putInt(currentSeconds);
        bb.put(Integer.valueOf("13", 16).byteValue());
        bb.put(Integer.valueOf("02", 16).byteValue());
        bb.putInt(eventId);
        int checkInt=check?1:0;
        bb.put((byte) checkInt);
        //校验码
        bb.flip();
        byte[] bodyData=getBytesFromByteBuffer(bb);//不包含checkSum的字节数组
        ByteBuffer re= ByteBuffer.allocate(1024);
        re.put(bodyData);
        re.put(getCheckSum(bodyData));
        re.flip();
        return re;
    }

    public  byte[] getBytesFromByteBuffer(ByteBuffer buff){
        byte[] result = new byte[buff.remaining()];
        if (buff.remaining() > 0) {
            buff.get(result, 0, buff.remaining());
        }
        return result;
    }
    public   HashMap<String,String> getVinDataFromRegBytes(byte[] data)
    {
        //解析注册数据包,提取vin和SerialNumber
        //serialNumber:36,14
        //vin         :50,17
        String serialNum="";
        String vin="";
        int eventId=0;
        HashMap<String,String> re=new HashMap<String ,String>();
        if(data!=null){
            if(data.length>66) {
                serialNum=new String(data, 36, 14);//serialNum在字节数组中的位置
                vin=new String(data, 50, 17);//vin在字节数组中的位置
                ByteBuffer bb= ByteBuffer.allocate(1024);
                bb.put(data);
                bb.flip();
                eventId=  bb.getInt(32);
            }
        }
        re.put("eventId",String.valueOf(eventId));
        re.put("vin",vin);
        re.put("serialNum",serialNum);
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
        System.out.print(">>checkSum:" + Integer.toHexString(sum) + "<>" + Integer.toHexString(bytes[bytes.length - 1]));
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
}
