package com.hp.triclops;

import com.hp.triclops.entity.UserDevice;
import com.hp.triclops.repository.DeviceRepository;
import com.hp.triclops.service.ApplePushService;
import com.notnoop.apns.APNS;
import com.notnoop.apns.ApnsService;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocketFactory;
import java.io.*;
import java.net.Socket;
import java.security.KeyStore;
import java.util.List;
import java.util.regex.Pattern;

/**
 * Created by yuh on 2015/11/9.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = Application.class)
public class APNSTest {
    @Before
    public void setUp() {
    }

    @After
    public void tearDown() {
    }

    @Autowired
    private DeviceRepository deviceRepository;

    @Autowired
    private ApplePushService applePushService;

    @Value("${com.hp.apns.cer.password}")
    private String pwd;

    public  String  getParallelPath()
    {
        String filename = "certforANPS.p12";
        String pre=System.getProperty("user.dir");
        String path=pre;
        //new String[]{"build","resources","main","certificates"}
        for(String arg:new String[]{"src","main","resources","certificates"})
        {

            path+=File.separator+arg;
        }
        path+=File.separator+filename;
        if(path.startsWith("file"))
        {
            path=path.substring(5);
        }
        path.replace("/", File.separator);
        return path;
    }


    @Test
    public void test01(){

        /**APNS推送需要的证书、密码、和设备的Token**/
        String  p12Path = "";
        p12Path = this.getParallelPath();

        String pushToken = "";
        String content = "hello test";
        try {
            /**设置参数，发送数据**/
            int userId = 1;
            List<UserDevice> list= this.deviceRepository.findByUserId(userId);

            for(UserDevice ud :list){
                pushToken = ud.getDeviceId();
                ApnsService service = APNS.newService().withCert(p12Path,pwd).withSandboxDestination().build();
                String payLoad = APNS.newPayload().alertBody(content).badge(1).sound("default").build();
                service.push(pushToken, payLoad);
            }
        } catch (Exception e) {
            System.out.println("出错了：" + e.getMessage());
        }
    }

    @Test
    public void test02(){

        this.applePushService.pushToUser("hello test",1);
    }

    @Test
    public void test03(){
        //String keyPath = "/data/tmp/proj.apns.p12";
        String keyPath = "..path";
        String ksType = "PKCS12";
        String ksPassword = pwd;
        String ksAlgorithm = "SunX509";

        String deviceToken = "1404c88dbb0adb92c0a85f4cd09be9707f251ae5bbecdb0a6a3e572aeb337d73";

        String serverHost = "gateway.push.apple.com";
        int serverPort = 2195;

        try {
            InputStream certInput = new FileInputStream(keyPath);
            KeyStore keyStore = KeyStore.getInstance(ksType);
            keyStore.load(certInput, ksPassword.toCharArray());

            KeyManagerFactory kmf = KeyManagerFactory.getInstance(ksAlgorithm);
            kmf.init(keyStore, ksPassword.toCharArray());

            SSLContext sslContext = SSLContext.getInstance("TLS");
            sslContext.init(kmf.getKeyManagers(), null, null);

            SSLSocketFactory socketFactory = sslContext.getSocketFactory();

            Socket socket = socketFactory.createSocket(serverHost, serverPort);

            StringBuilder content = new StringBuilder();

            String text = "this is a test.";

            content.append("{\"aps\":");
            content.append("{\"alert\":\"").append(text)
                    .append("\",\"badge\":1,\"sound\":\"")
                    .append("ping1").append("\"}");

            content.append(",\"cpn\":{\"t0\":")
                    .append(System.currentTimeMillis()).append("}");
            content.append("}");

            byte[] msgByte = makebyte((byte)1, deviceToken, content.toString(), 10000001);

            System.out.println(msgByte);

            socket.getOutputStream().write(msgByte);
            socket.getOutputStream().flush();

            socket.close();

        } catch (Exception e) {
            e.printStackTrace();
        }

    }


    /**
     * 组装apns规定的字节数组  使用增强型
     *
     * @param command
     * @param deviceToken
     * @param payload
     * @return
     * @throws IOException
     */
    private static byte[] makebyte(byte command, String deviceToken, String payload, int identifer) {

        byte[] deviceTokenb = decodeHex(deviceToken);
        byte[] payloadBytes = null;
        ByteArrayOutputStream boas = new ByteArrayOutputStream();
        DataOutputStream dos = new DataOutputStream(boas);
        try {
            payloadBytes = payload.getBytes("UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            return null;
        }

        try {
            dos.writeByte(command);
            dos.writeInt(identifer);//identifer
            dos.writeInt(Integer.MAX_VALUE);
            dos.writeShort(deviceTokenb.length);
            dos.write(deviceTokenb);
            dos.writeShort(payloadBytes.length);
            dos.write(payloadBytes);
            return boas.toByteArray();
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    private static final Pattern pattern = Pattern.compile("[ -]");
    private static byte[] decodeHex(String deviceToken) {
        String hex = pattern.matcher(deviceToken).replaceAll("");

        byte[] bts = new byte[hex.length() / 2];
        for (int i = 0; i < bts.length; i++) {
            bts[i] = (byte) (charval(hex.charAt(2*i)) * 16 + charval(hex.charAt(2*i + 1)));
        }
        return bts;
    }
    private static int charval(char a) {
        if ('0' <= a && a <= '9')
            return (a - '0');
        else if ('a' <= a && a <= 'f')
            return (a - 'a') + 10;
        else if ('A' <= a && a <= 'F')
            return (a - 'A') + 10;
        else{
            throw new RuntimeException("Invalid hex character: " + a);
        }
    }

}
