package com.hp.triclops.entity;

import javax.persistence.*;

/**
 * Created by sunjun on 2017/11/30.
 */
@Entity
@Table(name = "t_ftp_setting")
public class FtpSetting {

    private int id;
    private String softUrl;
    private String hardUrl;
    private String ftpIp;
    private Integer ftpPort;
    private String dialUserName;
    private String dialPin;

    public FtpSetting(){}

    public FtpSetting(int id, String softUrl, String hardUrl, String ftpIp, Integer ftpPort, String dialUserName, String dialPin) {
        this.id = id;
        this.softUrl = softUrl;
        this.hardUrl = hardUrl;
        this.ftpIp = ftpIp;
        this.ftpPort = ftpPort;
        this.dialUserName = dialUserName;
        this.dialPin = dialPin;
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

    @Column(name = "softurl", nullable = false, insertable = true, updatable = true)
    public String getSoftUrl() {
        return softUrl;
    }

    public void setSoftUrl(String softUrl) {
        this.softUrl = softUrl;
    }

    @Column(name = "hardurl", nullable = false, insertable = true, updatable = true)
    public String getHardUrl() {
        return hardUrl;
    }

    public void setHardUrl(String hardUrl) {
        this.hardUrl = hardUrl;
    }

    @Column(name = "ftpip", nullable = false, insertable = true, updatable = true)
    public String getFtpIp() {
        return ftpIp;
    }

    public void setFtpIp(String ftpIp) {
        this.ftpIp = ftpIp;
    }

    @Column(name = "ftpport", nullable = false, insertable = true, updatable = true)
    public Integer getFtpPort() {
        return ftpPort;
    }

    public void setFtpPort(Integer ftpPort) {
        this.ftpPort = ftpPort;
    }

    @Column(name = "dialusername", nullable = false, insertable = true, updatable = true)
    public String getDialUserName() {
        return dialUserName;
    }

    public void setDialUserName(String dialUserName) {
        this.dialUserName = dialUserName;
    }

    @Column(name = "dialpin", nullable = false, insertable = true, updatable = true)
    public String getDialPin() {
        return dialPin;
    }

    public void setDialPin(String dialPin) {
        this.dialPin = dialPin;
    }
}
