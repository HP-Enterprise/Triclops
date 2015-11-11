package com.hp.triclops.vo;

import com.hp.triclops.entity.PhoneBook;

/**
 * Created by Teemol on 2015/11/11.
 */
public class PhoneBookShow extends PhoneBook {

    private int id;
    private int uid;
    private String name;
    private String phone;
    private int isuser;

    public PhoneBookShow(int id, int uid, String name, String phone, int isuser) {
        super(id, uid, name, phone, isuser);
    }
}
