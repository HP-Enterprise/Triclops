package com.hp.triclops.entity;

/**
 * Created by jackl on 2016/11/14.
 */
public class MsgBean {

    private Body body;
    private Head head;

    public MsgBean(){

    }

    public MsgBean(Head head,Body body) {
        this.body = body;
        this.head = head;
    }

    public Body getBody() {
        return body;
    }

    public void setBody(Body body) {
        this.body = body;
    }

    public Head getHead() {
        return head;
    }

    public void setHead(Head head) {
        this.head = head;
    }
}
