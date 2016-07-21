package com.hp.triclops.vo;

import com.hp.triclops.entity.Store4s;

/**
 * Created by Liujingguo91year on 2016/7/20.
 */
public class Store4sShow {
    private int vehicleType;
    private Integer oid;
    private String province;
    private String city;
    private String name;
    private String address;
    private String hotllineExclusive;
    private String hotline24;

    public Store4sShow(Store4s store4s) {
        this.vehicleType = store4s.getVehicleType();
        this.oid = store4s.getOid();
        this.province = store4s.getProvince();
        this.city = store4s.getCity();
        this.name = store4s.getName();
        this.address = store4s.getAddress();
        this.hotllineExclusive = store4s.getHotllineExclusive();
        this.hotline24 = store4s.getHotline24();


    }

    public int getOid() {
        return oid;
    }

    public void setOid(Integer oid) {
        this.oid = oid;
    }

    public int getVehicleType() {
        return vehicleType;
    }

    public void setVehicleType(int vehicleType) {
        this.vehicleType = vehicleType;
    }

    public String getProvince() {
        return province;
    }

    public void setProvince(String province) {
        this.province = province;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getHotllineExclusive() {
        return hotllineExclusive;
    }

    public void setHotllineExclusive(String hotllineExclusive) {
        this.hotllineExclusive = hotllineExclusive;
    }

    public String getHotline24() {
        return hotline24;
    }

    public void setHotline24(String hotline24) {
        this.hotline24 = hotline24;
    }
}
