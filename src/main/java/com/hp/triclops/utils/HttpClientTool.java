package com.hp.triclops.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by sunjun on 2017/8/2.
 */
@Component
public class HttpClientTool {

    @Value("${com.hp.web.server.host}")
    private String urlLink;

    private Logger _logger = LoggerFactory.getLogger(HttpClientTool.class);

    public String doHttp(String api,String param){
        String result = "";
        if(api == null){
            _logger.info("api error:" + api);
            return null;
        }
        DataOutputStream dos = null;
        try{
            StringBuilder sb = new StringBuilder();
            sb.append("http://" + urlLink + api);
            URL url = new URL(sb.toString());

            HttpURLConnection conn = (HttpURLConnection)url.openConnection();
            conn.setDoOutput(true);
            conn.setDoInput(true);
            conn.setRequestMethod("POST");
            conn.setUseCaches(false);
            conn.setInstanceFollowRedirects(true);
            conn.setRequestProperty("Content-Type", "application/json;");
            conn.connect();
            dos = new DataOutputStream(conn.getOutputStream());
            if (param != null){
                dos.writeBytes(param);
            }
            dos.flush();
            dos.close();

            BufferedReader reader = new BufferedReader(new InputStreamReader(
                    conn.getInputStream()));
            result = reader.readLine();//读取请求结果
            reader.close();

            _logger.info("HttpClientTool status code: " + conn.getResponseCode());
            conn.disconnect();

        }catch(Exception e){
            e.printStackTrace();
            result = e.getMessage();
        }finally {
            if(dos!=null)
                try {
                    dos.close();
                } catch (IOException e) {
                    e.printStackTrace();
                    result = e.getMessage();
                }
        }

        return result;
    }

}
