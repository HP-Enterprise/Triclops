package com.hp.triclops.user.vo;

/**
 * Created by  on 2015/11/4.
 */
public class UserShow {
    private int id;
    private String name;
    private Integer gender;
    private String nick;
    private String phone;
    private int isVerified;
    private String contacts;
    private String contactsPhone;

    public UserShow(int id, String name, Integer gender, String nick, String phone, int isVerified, String contacts, String contactsPhone) {
        this.id = id;
        this.name = name;
        this.gender = gender;
        this.nick = nick;
        this.phone = phone;
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
}
