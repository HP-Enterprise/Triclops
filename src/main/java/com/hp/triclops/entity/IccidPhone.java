package com.hp.triclops.entity;

import javax.persistence.*;


@Entity
@Table(name = "t_iccid_phone")
public class IccidPhone {
    private int id;
    private String iccid;
    private String phone;

    public IccidPhone(){}

    public IccidPhone(String iccid, String phone){
        this.iccid = iccid;
        this.phone = phone;
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

    @Column(name = "iccid", nullable = false, insertable = true, updatable = true, length = 50)
    public String getIccid() {
        return iccid;
    }

    public void setIccid(String iccid) {
        this.iccid = iccid;
    }

    @Column(name = "phone", nullable = false, insertable = true, updatable = true, length = 50)
    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }
}
