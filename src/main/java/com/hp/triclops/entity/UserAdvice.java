package com.hp.triclops.entity;

import javax.persistence.*;
import java.util.Date;

/**
 * Created by sunjun on 2017/7/26.
 */
@Entity
@Table(name = "t_user_advice")
public class UserAdvice {

    private int id;
    private String content;
    private int userId;
    private Date createTime;
    private int status;
    private String name;
    private String vin;

    public UserAdvice(){}

    public UserAdvice(int id, String content, int userId, Date createTime, int status,String name,String vin) {
        this.id = id;
        this.content = content;
        this.userId = userId;
        this.createTime = createTime;
        this.status = status;
        this.name=name;
        this.vin=vin;
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false, insertable = true, updatable = true)
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    @Column(name = "content", nullable = false, insertable = true, updatable = true, length = 1000)
    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    @Column(name = "user_id", nullable = false, insertable = true, updatable = true)
    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    @Column(name = "create_time", nullable = false, insertable = true, updatable = true)
    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    @Column(name = "status", nullable = false, insertable = true, updatable = true)
    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getVin() {
        return vin;
    }

    public void setVin(String vin) {
        this.vin = vin;
    }
}
