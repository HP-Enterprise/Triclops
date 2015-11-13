package com.hp.triclops.entity;

import javax.persistence.*;

/**
 * 消息处理模型
 */
@Entity
@Table(name="t_message")
public class Message {
    private int id;
    private int sourceId;
    private int resourceFrom;
    private int targetType;
    private int targetId;
    private int resourceTo;
    private int funType;
    private int pType;
    private int contentType;
    private String textContent;
    private String fileName;

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

    public int getTargetType() {
        return targetType;
    }

    public void setTargetType(int targetType) {
        this.targetType = targetType;
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

    public int getFunType() {
        return funType;
    }

    public void setFunType(int funType) {
        this.funType = funType;
    }

    public int getpType() {
        return pType;
    }

    public void setpType(int pType) {
        this.pType = pType;
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

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }
}