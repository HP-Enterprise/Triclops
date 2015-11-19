package com.hp.triclops.entity;

import javax.persistence.*;

/**
 * Created by Liujingguo91Year on 2015/11/18.
 */
@Entity
@Table(name = "t_fs_shop")
public class Shop4s {
    private int id;
    private int oid;
    private String fSname;
    private String fSphone;
    private String fSaddress;
    private String city;
    private int cityid;
    public Shop4s(){}

    public Shop4s(Integer oid,String fSname,String fSphone,String fSaddress,String city,Integer cityid){
        this.oid = oid;
        this.fSname = fSname;
        this.fSphone = fSphone;
        this.fSaddress = fSaddress;
        this.city = city;
        this.cityid = cityid;
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
    @Column(name = "oid", nullable = false, insertable = true, updatable = true, length = 15)
    public Integer getOid() {
        return oid;
    }

    public void setOid(Integer oid) {
        this.oid = oid;
    }

    @Column(name = "fSname", nullable = false, insertable = true, updatable = true, length = 50)
    public String getfSname() {
        return fSname;
    }

    public void setfSname(String fSname) {
        this.fSname = fSname;
    }

    @Column(name = "fSphone", nullable = false, insertable = true, updatable = true, length = 50)
    public String getfSphone() {
        return fSphone;
    }

    public void setfSphone(String fSphone) {
        this.fSphone = fSphone;
    }

    @Column(name = "fSaddress", nullable = false, insertable = true, updatable = true, length = 50)
    public String getfSaddress() {
        return fSaddress;
    }

    public void setfSaddress(String fSaddress) {
        this.fSaddress = fSaddress;
    }

    @Column(name = "city", nullable = false, insertable = true, updatable = true, length = 50)
    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    @Column(name = "cityid", nullable = false, insertable = true, updatable = true, length = 11)
    public Integer getCityid() {
        return cityid;
    }

    public void setCity(Integer cityid) {
        this.cityid = cityid;
    }

}
