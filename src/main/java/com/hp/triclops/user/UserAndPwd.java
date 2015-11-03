package com.hp.triclops.user;

/**
 * 注册信息类
 * Created by Teemol on 2015/11/3.
 */
public class UserAndPwd {

    private int id;
    private String name;
    private String nick;
    private Integer gender;
    private String phone;
    private String password;
    private int isVerified;
    private String contacts;
    private String contactsPhone;


    public UserAndPwd(){}

    public UserAndPwd(int id, String name, String nick, Integer gender, String phone, String password, int isVerified, String contacts, String contactsPhone) {
        this.id = id;
        this.name = name;
        this.nick = nick;
        this.gender = gender;
        this.phone = phone;
        this.password = password;
        this.isVerified = isVerified;
        this.contacts = contacts;
        this.contactsPhone = contactsPhone;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getGender() {
        return gender;
    }

    public void setGender(Integer gender) {
        this.gender = gender;
    }

    public String getNick() {
        return nick;
    }

    public void setNick(String nick) {
        this.nick = nick;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public int getIsVerified() {
        return isVerified;
    }

    public void setIsVerified(int isVerified) {
        this.isVerified = isVerified;
    }

    public String getContacts() {
        return contacts;
    }

    public void setContacts(String contacts) {
        this.contacts = contacts;
    }

    public String getContactsPhone() {
        return contactsPhone;
    }

    public void setContactsPhone(String contactsPhone) {
        this.contactsPhone = contactsPhone;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    @Override
    public String toString() {
        return "UserPwdAndMore{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", nick='" + nick + '\'' +
                ", gender=" + gender +
                ", phone='" + phone + '\'' +
                ", password='" + password + '\'' +
                ", isVerified=" + isVerified +
                ", contacts='" + contacts + '\'' +
                ", contactsPhone='" + contactsPhone + '\'' +
                '}';
    }
}
