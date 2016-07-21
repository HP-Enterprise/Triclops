package com.hp.triclops.entity;

import javax.persistence.*;

/**
 * Created by Liujingguo91year on 2016/7/20.
 */
@Entity
@Table(name = "t_fs_store")
public class Store4s {
    private int id;
    private int vehicleType;
    private int oid;
    private String province;
    private String city;
    private String name;
    private String address;
    private String hotllineExclusive;
    private String hotline24;

    public Store4s() {
    }

    public Store4s(int id, int vehicleType, int oid,String province, String city, String name, String address, String hotllineExclusive, String hotline24) {
        this.id = id;
        this.vehicleType = vehicleType;
        this.oid = oid;
        this.province = province;
        this.city = city;
        this.name = name;
        this.address = address;
        this.hotllineExclusive = hotllineExclusive;
        this.hotline24 = hotline24;
    }

    public Store4s(int vehicleType,int oid, String province, String city, String name, String address, String hotllineExclusive, String hotline24) {
        this.vehicleType = vehicleType;
        this.oid = oid;
        this.province = province;
        this.city = city;
        this.name = name;
        this.address = address;
        this.hotllineExclusive = hotllineExclusive;
        this.hotline24 = hotline24;
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false, insertable = true, updatable = true)
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    @Basic
    @Column(name = "vehicle_Type", nullable = false, insertable = true, updatable = true, length = 15)
    public int getVehicleType() {
        return vehicleType;
    }

    public void setVehicleType(int vehicleType) {
        this.vehicleType = vehicleType;
    }


    @Basic
    @Column(name = "oid", nullable = true, insertable = true, updatable = true, length = 15)

    public int getOid() {
        return oid;
    }

    public void setOid(int oid) {
        this.oid = oid;
    }

    @Basic
    @Column(name = "province", nullable = false, insertable = true, updatable = true, length = 50)
    public String getProvince() {
        return province;
    }

    public void setProvince(String province) {
        this.province = province;
    }

    @Basic
    @Column(name = "city", nullable = false, insertable = true, updatable = true, length = 50)
    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    @Basic
    @Column(name = "name", nullable = false, insertable = true, updatable = true, length = 50)
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Basic
    @Column(name = "address", nullable = false, insertable = true, updatable = true, length = 50)
    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    @Basic
    @Column(name = "hotlline_Exclusive", nullable = false, insertable = true, updatable = true, length = 50)
    public String getHotllineExclusive() {
        return hotllineExclusive;
    }

    public void setHotllineExclusive(String hotllineExclusive) {
        this.hotllineExclusive = hotllineExclusive;
    }

    @Basic
    @Column(name = "hotline24", nullable = false, insertable = true, updatable = true, length = 50)
    public String getHotline24() {
        return hotline24;
    }

    public void setHotline24(String hotline24) {
        this.hotline24 = hotline24;
    }
}
