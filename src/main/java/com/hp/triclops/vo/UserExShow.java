package com.hp.triclops.vo;

import com.hp.triclops.entity.UserEx;

/**
 * Created by Incar on 2016/2/23.
 */
public class UserExShow {

    private int id;
    private String name;
    private Integer gender;
    private String nick;
    private String phone;
    private int isVerified;
    private String contacts;
    private String contactsPhone;
    private String icon;

    public UserExShow(UserEx userEx) {
        this.id = userEx.getId();
        this.name = userEx.getName();
        this.gender = userEx.getGender();
        this.nick = userEx.getNick();
        this.phone = userEx.getPhone();
        this.isVerified = userEx.getIsVerified();
        this.contacts = userEx.getContacts();
        this.contactsPhone = userEx.getContactsPhone();
        this.icon = userEx.getIcon();
    }

    /**
     * 用户信息过滤
     */
    public void blur()
    {
        if(phone!=null && phone.length()>4)
        {
            this.phone = phone.substring(0,phone.length()-4) + "****";
        }
        if(contacts!=null && contacts.length()>1)
        {
            this.contacts = contacts.substring(0,1) + "**";
        }
        if(contactsPhone!=null && contactsPhone.length()>4)
        {
            this.contactsPhone = contactsPhone.substring(0,contactsPhone.length()-4) + "****";
        }
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

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }
}
