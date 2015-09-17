package com.hp.triclops.acquire;

/**
 * Created by luj on 2015/9/17.
 */
public class DataTool {


    public static boolean checkReg(byte[] bytes){
        //校验注册数据
        return true;
    }

    public static boolean isRegData(byte[] bytes){
        //是否是注册数据 bytes[21] == 0x13
        boolean result=false;
        if(bytes!=null){
            if(bytes.length>2) {
                byte data=bytes[21];
                result=(data==0x13);
            }
        }
        return result;
    }



    public static byte getApplicationType(byte[] bytes){
        //返回数据包操作类型对应的byte
        byte data=0;
        boolean result=false;
        if(bytes!=null){
            if(bytes.length>2) {
                data=bytes[21];
            }
        }
        return data;
    }

    public static byte[] getWriteData(byte[] bytes){
        //返回处理后发回客户端的字节数组
        return null;
    }




    public  static boolean checkByteArray(byte[] data)
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
        System.out.println("checkByteArray:"+result);
        return result;
    }
    public static boolean checkSum(byte[] bytes){
        //将字节数组除了最后一位的部分进行异或操作，与最后一位比较
        //校验数据包尾
        //将编码后的报文（ Message Header -- Application Data）进行异或操作， 1 个字节长度
        byte sum=0;
        for(int i=0;i<bytes.length-1;i++){
            sum^=bytes[i];
        }
        System.out.println(">>checkSum:"+Integer.toHexString(sum)+"<>"+Integer.toHexString(bytes[bytes.length-1]));
        return bytes[bytes.length-1]==sum;
    }
}
