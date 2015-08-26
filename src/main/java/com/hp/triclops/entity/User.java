package com.hp.triclops.entity;

import javax.persistence.*;
import java.io.Serializable;

/**
 * 用户类
 */
@Entity
@Table(name = "t_user")
public class User implements Serializable {
    private int id;
    private String name;
    private Integer gender;
    private String nick;
    private String phone;
    private String isVerified;

    public User() {}

    public User(String name, Integer gender, String nick, String phone,String isVerified) {
        this.name = name;
        this.gender = gender;
        this.nick = nick;
        this.phone = phone;
        this.isVerified = isVerified;
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "Id", nullable = false, insertable = true, updatable = true)
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
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
    @Column(name = "gender", nullable = true, insertable = true, updatable = true)
    public Integer getGender() {
        return gender;
    }

    public void setGender(Integer gender) {
        this.gender = gender;
    }

    @Basic
    @Column(name = "nick", nullable = false, insertable = true, updatable = true, length = 50)
    public String getNick() {
        return nick;
    }

    public void setNick(String nick) {
        this.nick = nick;
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
    @Column(name = "is_verified", nullable = true, insertable = true, updatable = true, length = 5)
    public String getIsVerified() {
        return isVerified;
    }

    public void setIsVerified(String isVerified) {
        this.isVerified = isVerified;
    }

}
