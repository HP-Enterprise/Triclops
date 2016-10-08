package com.hp.triclops.utils;

import javax.crypto.*;
import javax.crypto.spec.SecretKeySpec;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;


/**
 * Created by jackl on 2016/9/23.
 */
public class AES128Tool {

    /**
     *
     * @param byteContent 需要加密的内容
     * @param password 加密密码 不足16补0xFF
     * @return
     */
    public static byte[] encrypt(byte[] byteContent, String password) {
        byte[] pwdBytes=password.getBytes();
        if(password.length()<16) {
            pwdBytes= Arrays.copyOf(pwdBytes, 16);
            Arrays.fill(pwdBytes, password.length(),16,(byte) 0xff);
        }
        //System.out.println("AA:"+parseByte2HexStr(pwdBytes));
        return encrypt(byteContent,pwdBytes);
    }


    public static byte[] encrypt(byte[] byteContent, byte[] password) {
        try {
            SecretKeySpec key = new SecretKeySpec(password, "AES");
            Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");// 创建密码器
            cipher.init(Cipher.ENCRYPT_MODE, key);// 初始化
            byte[] result = cipher.doFinal(byteContent);
            return result; // 加密
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (NoSuchPaddingException e) {
            e.printStackTrace();
        } catch (InvalidKeyException e) {
            e.printStackTrace();
        }  catch (IllegalBlockSizeException e) {
            e.printStackTrace();
        } catch (BadPaddingException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**解密
     * @param content  待解密内容
     * @param password 解密密钥 不足16补0xFF
     * @return
     */
    public static byte[] decrypt(byte[] content, String password) {
        byte[] pwdBytes=password.getBytes();
        if(password.length()<16) {
            pwdBytes=Arrays.copyOf(pwdBytes, 16);
            Arrays.fill(pwdBytes, password.length(),16,(byte) 0xff);
        }
        return decrypt(content, pwdBytes);
    }


    public static byte[] decrypt(byte[] content, byte[] password) {
        try {

            SecretKeySpec key = new SecretKeySpec(password, "AES");
            Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");// 创建密码器
            cipher.init(Cipher.DECRYPT_MODE, key);// 初始化
            byte[] result = cipher.doFinal(content);
            return result; // 加密
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (NoSuchPaddingException e) {
            e.printStackTrace();
        } catch (InvalidKeyException e) {
            e.printStackTrace();
        } catch (IllegalBlockSizeException e) {
            e.printStackTrace();
        } catch (BadPaddingException e) {
            e.printStackTrace();
        }
        return null;
    }


  /*  public static void main(String[] args){
        String content = "testvcasljdhcejwfdhsljcxbouwaehdasol";
        String password = "12345678919991234";
        //加密
        System.out.println("加密前：" + content);
        byte[] encryptResult = encrypt(content, password);
        System.out.println(parseByte2HexStr(encryptResult));
        byte[] encryptResult1 = encrypt(content, password+"1");
        System.out.println(parseByte2HexStr(encryptResult1));

        //解密

        byte[] decryptResult = decrypt(encryptResult1,password+"1");
        System.out.println("解密后：" + new String(decryptResult));
    }
*/
    /**将二进制转换成16进制
     * @param buf
     * @return
     */
    public static String parseByte2HexStr(byte buf[]) {
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < buf.length; i++) {
            String hex = Integer.toHexString(buf[i] & 0xFF);
            if (hex.length() == 1) {
                hex = '0' + hex;
            }
            sb.append(hex.toUpperCase());
        }
        return sb.toString();
    }

    /**将16进制转换为二进制
     * @param hexStr
     * @return
     */
    public static byte[] parseHexStr2Byte(String hexStr) {
        if (hexStr.length() < 1)
            return null;
        byte[] result = new byte[hexStr.length()/2];
        for (int i = 0;i< hexStr.length()/2; i++) {
            int high = Integer.parseInt(hexStr.substring(i*2, i*2+1), 16);
            int low = Integer.parseInt(hexStr.substring(i * 2 + 1, i * 2 + 2), 16);
            result[i] = (byte) (high * 16 + low);
        }
        return result;
    }

}
