package com.hp.triclops.entity;

import javax.persistence.*;

/**
 * Created by luj on 2015/10/20.
 */
@Entity
@Table(name = "t_warning_message_conversion")
public class WarningMessageConversion {
    private int id;
    private String messageId;
    private String messageZh;
    private String messageEn;
    private String groupId;
    private String groupMessage;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false, insertable = true, updatable = true)
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    @Basic
    @Column(name = "message_id", nullable = false, insertable = true, updatable = true, length = 5)
    public String getMessageId() {
        return messageId;
    }

    public void setMessageId(String messageId) {
        this.messageId = messageId;
    }

    @Basic
    @Column(name = "message_zh", nullable = false, insertable = true, updatable = true, length = 150)
    public String getMessageZh() {
        return messageZh;
    }

    public void setMessageZh(String messageZh) {
        this.messageZh = messageZh;
    }

    @Basic
    @Column(name = "message_en", nullable = false, insertable = true, updatable = true, length = 150)
    public String getMessageEn() {
        return messageEn;
    }

    public void setMessageEn(String messageEn) {
        this.messageEn = messageEn;
    }

    @Basic
    @Column(name = "group_id", nullable = false, insertable = true, updatable = true, length = 5)
    public String getGroupId() {
        return groupId;
    }

    public void setGroupId(String groupId) {
        this.groupId = groupId;
    }
    @Basic
    @Column(name = "group_message", nullable = false, insertable = true, updatable = true, length = 150)
    public String getGroupMessage() {
        return groupMessage;
    }

    public void setGroupMessage(String groupMessage) {
        this.groupMessage = groupMessage;
    }
}
