package com.hp.triclops.vo;


import com.hp.triclops.entity.UserEx;

/**
 * Created by Teemol on 2016/1/29.
 */
public class UserExPartShow {

    private int id;
    private String name;
    private Integer gender;
    private String nick;
    private String icon;

    public UserExPartShow(UserEx userEx) {
        this.id = userEx.getId();
        this.name = userEx.getName();
        this.gender = userEx.getGender();
        this.nick = userEx.getNick();
        this.icon = userEx.getIcon();
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

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }
}
