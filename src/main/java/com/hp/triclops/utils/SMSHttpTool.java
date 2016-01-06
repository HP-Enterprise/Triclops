package com.hp.triclops.utils;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * Created by yuh on 2015/12/10.
 */
@Component
public class SMSHttpTool {

    @Value("${com.hp.web.server.host}")
    private String urlLink;

    public  void doHttp(String phone,String message){

        DataOutputStream dos = null;
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
        }finally {
            if(dos!=null)
                try {
                    dos.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
        }
    }

    public String getShortUrl(String u){
        //u=  "http://127.0.0.1:8080/baiduMap.html?lon=114.13320540355&lat=30.257868000746";
        String re="";
        String requestUrl="http://dwz.cn/create.php";
        Map<String, Object> requestParamsMap = new HashMap<String, Object>();
        requestParamsMap.put("url", u);
        PrintWriter printWriter = null;
        BufferedReader bufferedReader = null;
        // BufferedReader bufferedReader = null;
        StringBuffer responseResult = new StringBuffer();
        StringBuffer params = new StringBuffer();
        HttpURLConnection httpURLConnection = null;
        // add param
        Iterator it = requestParamsMap.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry element = (Map.Entry) it.next();
            params.append(element.getKey());
            params.append("=");
            params.append(element.getValue());
            params.append("&");
        }
        if (params.length() > 0) {
            params.deleteCharAt(params.length() - 1);
        }
        try {
            URL realUrl = new URL(requestUrl);
            // open conn
            httpURLConnection = (HttpURLConnection) realUrl.openConnection();
            // set attribute
            httpURLConnection.setRequestProperty("accept", "*/*");
            httpURLConnection.setRequestProperty("connection", "Keep-Alive");
            httpURLConnection.setRequestProperty("Content-Length", String
                    .valueOf(params.length()));
            // set post request
            httpURLConnection.setDoOutput(true);
            httpURLConnection.setDoInput(true);
            // URLConnection outputStream
            printWriter = new PrintWriter(httpURLConnection.getOutputStream());
            // post param
            printWriter.write(params.toString());
            // flush
            printWriter.flush();
            // response code
            int responseCode = httpURLConnection.getResponseCode();
            if (responseCode != 200) {
                System.out.println(" Error===" + responseCode);
            } else {
                System.out.println("Post Success!");
            }
            // BufferedReader URL ResponseData
            bufferedReader = new BufferedReader(new InputStreamReader(
                    httpURLConnection.getInputStream()));
            String line;
            while ((line = bufferedReader.readLine()) != null) {
                responseResult.append(line);
            }

            String reResult = responseResult.toString();
            JSONObject jbResult = JSON.parseObject(reResult);
            re =  jbResult.get("tinyurl").toString();
            re = re.replace("\\","");
        } catch (Exception e) {
            System.out.println("send post request error!" + e);
        } finally {
            httpURLConnection.disconnect();
            try {
                if (printWriter != null) {
                    printWriter.close();
                }
                if (bufferedReader != null) {
                    bufferedReader.close();
                }
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
        return re;
    }


}
