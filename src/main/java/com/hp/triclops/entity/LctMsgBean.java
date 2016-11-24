package com.hp.triclops.entity;

/**
 * Created by jackl on 2016/11/14.
 */
public class LctMsgBean {

    private LctBody body;
    private LctHead head;

    public LctMsgBean(){

    }

    public LctMsgBean(LctHead head, LctBody body) {
        this.body = body;
        this.head = head;
    }

    public LctBody getBody() {
        return body;
    }

    public void setBody(LctBody body) {
        this.body = body;
    }

    public LctHead getHead() {
        return head;
    }

    public void setHead(LctHead head) {
        this.head = head;
    }
}
