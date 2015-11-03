package com.hp.triclops.user;

/**
 * 登录凭据类
 * Created by Teemol on 2015/11/3.
 */
public class PwdEvidence {

    private String name;         //用户名
    private String pwd;          //密码

    public PwdEvidence(){}

    public PwdEvidence(String name, String pwd) {
        this.name = name;
        this.pwd = pwd;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPwd() {
        return pwd;
    }

    public void setPwd(String pwd) {
        this.pwd = pwd;
    }
}
