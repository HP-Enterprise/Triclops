package com.hp.triclops.utils;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.hp.triclops.entity.GpsData;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.Map;


@Component
public class GpsTool {
    /**
     * WGS84转GCJ-02坐标系 算法来源于互联网，与官方转换接口相比较精度还不错
     * @param gpsData
     * @return 转换后的数据实体
     */
    double pi = 3.14159265358979324;
    double a = 6378245.0;
    double ee = 0.00669342162296594323;
    @Value("${com.hp.web.server.baiduKey}")
    private String baiduKey;

    private Logger _logger= LoggerFactory.getLogger(GpsTool.class);

    public  GpsData convertToGCJ02(GpsData gpsData)
    {
        DecimalFormat df = new DecimalFormat("#.######");
        double wgLat=gpsData.getLatitude();
        double wgLon=gpsData.getLongitude();
        double mgLat;
        double mgLon;
        if (outOfChina(wgLat, wgLon))
        {
            mgLat = wgLat;
            mgLon = wgLon;

        }else{
        double dLat = transformLat(wgLon - 105.0, wgLat - 35.0);
        double dLon = transformLon(wgLon - 105.0, wgLat - 35.0);
        double radLat = wgLat / 180.0 * pi;
        double magic = Math.sin(radLat);
        magic = 1 - ee * magic * magic;
        double sqrtMagic = Math.sqrt(magic);
        dLat = (dLat * 180.0) / ((a * (1 - ee)) / (magic * sqrtMagic) * pi);
        dLon = (dLon * 180.0) / (a / sqrtMagic * Math.cos(radLat) * pi);
        mgLat = wgLat + dLat;
        mgLon = wgLon + dLon;
        }
        mgLat=Double.parseDouble(df.format(mgLat));
        mgLon=Double.parseDouble(df.format(mgLon));

        gpsData.setLatitude(mgLat);
        gpsData.setLongitude(mgLon);
        return gpsData;
      }

    /**
     * WGS84转百度坐标系 调用百度转换api
     * @param gpsData
     * @return 转换后的数据实体
     */
    public  GpsData convertToBaiDuGps(GpsData gpsData)
    {

        return getDataFromBaidu(gpsData);
    }


    private boolean  outOfChina(double lat, double lon)
    {
        if (lon < 72.004 || lon > 137.8347)
            return true;
        if (lat < 0.8293 || lat > 55.8271)
            return true;
        return false;
    }
    private double transformLat(double x, double y)
    {
        double ret = -100.0 + 2.0 * x + 3.0 * y + 0.2 * y * y + 0.1 * x * y + 0.2 * Math.sqrt(Math.abs(x));
        ret += (20.0 * Math.sin(6.0 * x * pi) + 20.0 * Math.sin(2.0 * x * pi)) * 2.0 / 3.0;
        ret += (20.0 * Math.sin(y * pi) + 40.0 * Math.sin(y / 3.0 * pi)) * 2.0 / 3.0;
        ret += (160.0 * Math.sin(y / 12.0 * pi) + 320 * Math.sin(y * pi / 30.0)) * 2.0 / 3.0;
        return ret;
    }
    private double transformLon(double x,double y){
        double ret = 300.0 + x + 2.0 * y + 0.1 * x * x + 0.1 * x * y + 0.1 * Math.sqrt(Math.abs(x));
        ret += (20.0 * Math.sin(6.0 * x * pi) + 20.0 * Math.sin(2.0 * x * pi)) * 2.0 / 3.0;
        ret += (20.0 * Math.sin(x * pi) + 40.0 * Math.sin(x / 3.0 * pi)) * 2.0 / 3.0;
        ret += (150.0 * Math.sin(x / 12.0 * pi) + 300.0 * Math.sin(x / 30.0 * pi)) * 2.0 / 3.0;
        return ret;
    }
    public GpsData getDataFromBaidu(GpsData gpsData){
        StringBuilder sb=new StringBuilder();
        sb.append("http://api.map.baidu.com/geoconv/v1/?");
        sb.append("&coords=").append(gpsData.getLongitude()).append(",").append(gpsData.getLatitude());
        sb.append("&from=1&to=5");
        sb.append("&ak=").append(baiduKey);
        String ADD_URL=sb.toString();

        try {
            //建立连接
            URL url = new URL(ADD_URL);
            HttpURLConnection connection = (HttpURLConnection) url
                    .openConnection();
            connection.setDoOutput(true);
            connection.setDoInput(true);
            connection.setRequestMethod("GET");
            connection.setUseCaches(false);
            connection.setInstanceFollowRedirects(true);
            connection.setRequestProperty("Content-Type", "application/json;charset=utf-8");
            connection.connect();
            //POST请求
            OutputStreamWriter out = new  OutputStreamWriter(connection.getOutputStream(), "UTF-8");

                     //读取响应
            BufferedReader reader = new BufferedReader(new InputStreamReader(
                    connection.getInputStream()));
            String lines;
            StringBuffer re = new StringBuffer();
            while ((lines = reader.readLine()) != null) {
                lines = new String(lines.getBytes(), "utf-8");
                re.append(lines);
            }
            String result=re.toString();
            System.out.println(result);
            Gson gs=new Gson();
            Map<String,String> resultMap = gs.fromJson(sb.toString(), new TypeToken<Map<String, String>>(){}.getType());
            String status=resultMap.get("status");
            this._logger.info("status:" + status);
            reader.close();
            // 断开连接
            connection.disconnect();

        } catch (MalformedURLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return gpsData;//success返回1 faild返回0
    }
}
