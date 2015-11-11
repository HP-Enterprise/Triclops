package com.hp.triclops.entity;

import javax.persistence.*;

/**
 * Created by Teemol on 2015/11/11.
 */

@Entity
@Table(name = "t_phone_book")
public class PhoneBook {

    private int id;
    private int uid;
    private String name;
    private String phone;
    private int isuser;

    public PhoneBook(int id, int uid, String name, String phone, int isuser) {
        this.id = id;
        this.uid = uid;
        this.name = name;
        this.phone = phone;
        this.isuser = isuser;
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
    @Column(name = "uid", nullable = false, insertable = true, updatable = true, length = 11)
    public int getUid() {
        return uid;
    }

    public void setUid(int uid) {
        this.uid = uid;
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
    @Column(name = "phone", nullable = true, insertable = true, updatable = true, length = 11)
    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    @Basic
    @Column(name = "isuser", nullable = false, insertable = true, updatable = true, length = 1)
    public int getIsuser() {
        return isuser;
    }

    public void setIsuser(int isuser) {
        this.isuser = isuser;
    }
}
