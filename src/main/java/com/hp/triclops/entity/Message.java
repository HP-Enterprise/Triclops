package com.hp.triclops.entity;

import javax.persistence.*;
import java.util.Date;

/**
 * 消息处理模型
 */
@Entity
@Table(name="t_message")
public class Message {

    private int id;
    private int sourceId;
    private int resourceFrom;
    private int targetId;
    private int resourceTo;
    private int contentType;
    private String textContent;
    private byte[] fileContent;
    private Date sendTime;
    private String filetype;
    private String messagefrom;
    private String messagetarget;
    private int targettype;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getSourceId() {
        return sourceId;
    }

    public void setSourceId(int sourceId) {
        this.sourceId = sourceId;
    }

    public int getResourceFrom() {
        return resourceFrom;
    }

    public void setResourceFrom(int resourceFrom) {
        this.resourceFrom = resourceFrom;
    }

    public int getTargetId() {
        return targetId;
    }

    public void setTargetId(int targetId) {
        this.targetId = targetId;
    }

    public int getResourceTo() {
        return resourceTo;
    }

    public void setResourceTo(int resourceTo) {
        this.resourceTo = resourceTo;
    }

    public int getContentType() {
        return contentType;
    }

    public void setContentType(int contentType) {
        this.contentType = contentType;
    }

    public String getTextContent() {
        return textContent;
    }

    public void setTextContent(String textContent) {
        this.textContent = textContent;
    }

    @Lob
    public byte[] getFileContent() {
        return fileContent;
    }

    public void setFileContent(byte[] fileContent) {
        this.fileContent = fileContent;
    }

    public Date getSendTime() {
        return sendTime;
    }

    public void setSendTime(Date sendTime) {
        this.sendTime = sendTime;
    }

    public String getFiletype() {
        return filetype;
    }

    public void setFiletype(String filetype) {
        this.filetype = filetype;
    }

    public String getMessagefrom() {
        return messagefrom;
    }

    public void setMessagefrom(String messagefrom) {
        this.messagefrom = messagefrom;
    }

    public String getMessagetarget() {
        return messagetarget;
    }

    public void setMessagetarget(String messagetarget) {
        this.messagetarget = messagetarget;
    }

    public int getTargettype() {
        return targettype;
    }

    public void setTargettype(int targettype) {
        this.targettype = targettype;
    }

//    @Override
//    public String toString() {
//        return "Message{" +
//                "id=" + id +
//                ", sourceId=" + sourceId +
//                ", resourceFrom=" + resourceFrom +
//                ", targetId=" + targetId +
//                ", resourceTo=" + resourceTo +
//                ", contentType=" + contentType +
//                ", textContent='" + textContent + '\'' +
//                ", fileContent=" + Arrays.toString(fileContent) +
//                ", sendTime=" + sendTime +
//                ", filetype='" + filetype + '\'' +
//                ", messagefrom='" + messagefrom + '\'' +
//                ", messagetarget='" + messagetarget + '\'' +
//                ", targettype=" + targettype +
//                '}';
//    }
}
