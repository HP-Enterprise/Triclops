package com.hp.triclops.utils;

import com.hp.triclops.entity.GpsData;
import org.springframework.stereotype.Component;

import java.text.DecimalFormat;


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
}
