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
    private String fsname;
    private String fsphone;
    private String fsaddress;
    private String city;
    private int cityid;
    public Shop4s(){}

    public Shop4s(Integer id,Integer oid,String fsname,String fsphone,String fsaddress,String city,Integer cityid){
        this.id = id;
        this.oid = oid;
        this.fsname = fsname;
        this.fsphone = fsphone;
        this.fsaddress = fsaddress;
        this.city = city;
        this.cityid = cityid;
    }
    public Shop4s(Integer id,String fsname,String fsphone,String fsaddress,String city,Integer cityid){
        this.id = id;
        this.fsname = fsname;
        this.fsphone = fsphone;
        this.fsaddress = fsaddress;
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

    @Basic
    @Column(name = "fsname", nullable = false, insertable = true, updatable = true, length = 50)
    public String getfsname() {
        return fsname;
    }

    public void setfsname(String fsname) {
        this.fsname = fsname;
    }

    @Basic
    @Column(name = "fsphone", nullable = false, insertable = true, updatable = true, length = 50)
    public String getfsphone() {
        return fsphone;
    }

    public void setfsphone(String fsphone) {
        this.fsphone = fsphone;
    }

    @Basic
    @Column(name = "fsaddress", nullable = false, insertable = true, updatable = true, length = 50)
    public String getfsaddress() {
        return fsaddress;
    }

    public void setfsaddress(String fsaddress) {
        this.fsaddress = fsaddress;
    }

    @Basic
    @Column(name = "city", nullable = false, insertable = true, updatable = true, length = 50)
    public String getcity() {
        return city;
    }

    public void setcity(String city) {
        this.city = city;
    }

    @Basic
    @Column(name = "cityid", nullable = false, insertable = true, updatable = true, length = 11)
    public Integer getcityid() {
        return cityid;
    }

    public void setcityid(Integer cityid) {
        this.cityid = cityid;
    }

}
