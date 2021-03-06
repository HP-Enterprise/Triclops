package com.hp.triclops.entity;

import com.hp.triclops.vo.UserExShow;

import javax.persistence.*;

/**
 * Created by Teemol on 2016/1/26.
 */

@Entity
@Table(name = "t_user")
public class UserEx {

    private int id;
    private String name;
    private Integer gender;
    private String nick;
    private String phone;
    private int isVerified;
    private String contacts;
    private String contactsPhone;
    private String icon;
    private int isWebLogin;

    public UserEx() {}

    public UserEx(UserExShow userExShow) {
        this.id = userExShow.getId();
        this.name = userExShow.getName();
        this.gender = userExShow.getGender();
        this.nick = userExShow.getNick();
        this.phone = userExShow.getPhone();
        this.isVerified = userExShow.getIsVerified();
        this.contacts = userExShow.getContacts();
        this.contactsPhone = userExShow.getContactsPhone();
        this.icon = userExShow.getIcon();
        this.isWebLogin = userExShow.getIsWebLogin();
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
    @Column(name = "name", nullable = false, insertable = true, updatable = true)
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
    @Column(name = "nick", nullable = false, insertable = true, updatable = true)
    public String getNick() {
        return nick;
    }

    public void setNick(String nick) {
        this.nick = nick;
    }

    @Basic
    @Column(name = "phone", nullable = true, insertable = true, updatable = true)
    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    @Basic
    @Column(name = "is_verified", nullable = true, insertable = true, updatable = true)
    public int getIsVerified() {
        return isVerified;
    }

    public void setIsVerified(int isVerified) {
        this.isVerified = isVerified;
    }

    @Basic
    @Column(name = "contacts", nullable = true, insertable = true, updatable = true)
    public String getContacts() {
        return contacts;
    }

    public void setContacts(String contacts) {
        this.contacts = contacts;
    }

    @Basic
    @Column(name = "contacts_phone", nullable = true, insertable = true, updatable = true)
    public String getContactsPhone() {
        return contactsPhone;
    }

    public void setContactsPhone(String contactsPhone) {
        this.contactsPhone = contactsPhone;
    }

    @Basic
    @Column(name = "icon", nullable = true, insertable = true, updatable = true)
    public String getIcon() {
        return icon;
    }

    @Basic
    @Column(name = "is_web_login", nullable = true, insertable = true, updatable = true)
    public int getIsWebLogin() {
        return isWebLogin;
    }

    public void setIsWebLogin(int isWebLogin) {
        this.isWebLogin = isWebLogin;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }
}
