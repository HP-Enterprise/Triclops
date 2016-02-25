package com.hp.triclops.utils;

import org.springframework.stereotype.Component;

import java.io.DataOutputStream;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Map;

/**
 * Created by Administrator on 2015/11/20.
 */
@Component
public class HttpRequestTool {
    public  void doHttp(String urlLink,Map dataMap){

        DataOutputStream dos = null;
        try{
            String boundary = "Boundary-b1ed-4060-99b9-fca7ff59c113"; //Could be any string
            String Enter = "\r\n";

            String targetId= String.valueOf(dataMap.get("targetId"));
            String resourceTo=String.valueOf(dataMap.get("resourceTo"));
            String funType=  String.valueOf(dataMap.get("funType"));
            String messageNums=  String.valueOf(dataMap.get("messageNums"));
            String textContent=  String.valueOf(dataMap.get("textContent"));
            String pType=  String.valueOf(dataMap.get("pType"));
            String cleanFlag=String.valueOf(dataMap.get("cleanFlag"));
            StringBuilder sb=new StringBuilder();
            sb.append("?targetId=").append(targetId);
            if(resourceTo!=null&&!resourceTo.equals("null")){
                sb.append("&").append("resourceTo=").append(resourceTo);
            }
            if(funType!=null&&!funType.equals("null")){
                sb.append("&").append("funType=").append(funType);
            }
            if(messageNums!=null&&!messageNums.equals("null")){
                sb.append("&").append("messageNums=").append(messageNums);
            }
            if(textContent!=null&&!textContent.equals("null")){
                String textContentEncode=java.net.URLEncoder.encode(textContent,"UTF-8");
                sb.append("&").append("textContent=").append(textContentEncode);
            }
            if(pType!=null&&!pType.equals("null")){
                sb.append("&").append("pType=").append(pType);
            }
            if(cleanFlag!=null&&!cleanFlag.equals("null")){
                sb.append("&").append("cleanFlag=").append(cleanFlag);
            }
            //String params=java.net.URLEncoder.encode(sb.toString());
            URL url = new URL(urlLink+sb.toString());
            HttpURLConnection conn = (HttpURLConnection)url.openConnection();
            conn.setDoOutput(true);
            conn.setDoInput(true);
            conn.setRequestMethod("POST");
            conn.setUseCaches(false);
            conn.setInstanceFollowRedirects(true);
            conn.setRequestProperty("Content-Type","multipart/form-data;boundary=" + boundary);
            conn.connect();
            dos = new DataOutputStream(conn.getOutputStream());
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
        } finally {
            if(dos!=null)
                try {
                    dos.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
        }
    }
}