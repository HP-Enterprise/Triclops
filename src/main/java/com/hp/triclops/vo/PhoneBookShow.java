package com.hp.triclops.vo;

import com.hp.triclops.entity.PhoneBook;

/**
 * Created by Teemol on 2015/11/11.
 */
public class PhoneBookShow{

    private int id;
    private int uid;
    private String name;
    private String phone;
    private int isuser;
    public PhoneBookShow(){

    }

    public PhoneBookShow(int id,int uid,String name,String phone,int isuser){
        this.id = id;
        this.uid = uid;
        this.name = name;
        this.phone = phone;
        this.isuser = isuser;
    }

    public PhoneBookShow(PhoneBook phoneBook){
        this.id = phoneBook.getId();
        this.uid = phoneBook.getUid();
        this.name = phoneBook.getName();
        this.phone = phoneBook.getPhone();
        this.isuser = phoneBook.getIsuser();
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getUid() {
        return uid;
    }

    public void setUid(int uid) {
        this.uid = uid;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public int getIsuser() {
        return isuser;
    }

    public void setIsuser(int isuser) {
        this.isuser = isuser;
    }
}
