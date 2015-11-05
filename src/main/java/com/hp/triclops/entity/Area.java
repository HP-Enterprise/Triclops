package com.hp.triclops.entity;

import javax.persistence.*;

/**
 * Created by Liujingguo91year on 2015/11/3.
 */
@Entity
@Table(name = "t_administrative_division")
public class Area {
    private int id;
    private String province;
    private String location;
 
    public Area(){}

    public Area(String province,String location){
        this.province = province;
        this.location = location;
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


    @Column(name = "province", nullable = false, insertable = true, updatable = true, length = 50)
    public String getProvince() {
        return province;
    }

    public void setProvince(String province) {
        this.province = province;
    }

    @Column(name = "location", nullable = false, insertable = true, updatable = true, length = 50)
    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

}
