package com.hp.triclops.utils;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.DataOutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Map;

/**
 * Created by yuh on 2015/12/10.
 */
@Component
public class SMSHttpTool {

    @Value("${com.hp.web.server.host}")
    private String urlLink;///api/sms/message?phone=123123123&message=这是一条测试短信哦

    public  void doHttp(String phone,String message){
        try{
            String boundary = "Boundary-b1ed-4060-99b9-fca7ff59c113"; //Could be any string
            String Enter = "\r\n";

            StringBuilder sb = new StringBuilder();
            sb.append("http://");
            sb.append(urlLink);
            sb.append("/api/sms/message");
            sb.append("?phone=");
            sb.append(phone);
            sb.append("&message=");
            sb.append(message);
            URL url = new URL(sb.toString());

            HttpURLConnection conn = (HttpURLConnection)url.openConnection();
            conn.setDoOutput(true);
            conn.setDoInput(true);
            conn.setRequestMethod("POST");
            conn.setUseCaches(false);
            conn.setInstanceFollowRedirects(true);
            conn.setRequestProperty("Content-Type","multipart/form-data;boundary=" + boundary);
            conn.connect();
            DataOutputStream dos = new DataOutputStream(conn.getOutputStream());
            //part 1
            String part1 =  "--" + boundary + Enter
                    + "Content-Type: application/octet-stream" + Enter
                    + "Content-Disposition: form-data; filename=\""+""+"\"; name=\"file\"" + Enter + Enter;
            //part 2
            String part2 = Enter
                    + "--" + boundary + Enter
                    + "Content-Type: text/plain" + Enter
                    + "Content-Disposition: form-data; name=\"dataFormat\"" + Enter + Enter
                    + "hk" + Enter
                    + "--" + boundary + "--";
            dos.writeBytes(part1);
            dos.writeBytes(part2);
            dos.flush();
            dos.close();
            System.out.println("status code: "+conn.getResponseCode());
            conn.disconnect();

        }catch(Exception e){
            e.printStackTrace();
        }

    }
}
