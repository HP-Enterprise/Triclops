package com.hp.triclops.entity;

/**
 * Created by jackl on 2016/11/14.
 */
public class LctHead {

    private int version;
    private long id;
    private String from;
    private int code;
    private int type;
    private String msg;

    public LctHead(){

    }

    public LctHead(int version, long id, String from, int code, int type, String msg) {
        this.version = version;
        this.id = id;
        this.from = from;
        this.code = code;
        this.type = type;
        this.msg = msg;
    }

    public int getVersion() {
        return version;
    }

    public void setVersion(int version) {
        this.version = version;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getFrom() {
        return from;
    }

    public void setFrom(String from) {
        this.from = from;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }
}
