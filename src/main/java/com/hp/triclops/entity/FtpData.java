package com.hp.triclops.entity;

import javax.persistence.*;
import java.util.Date;

/**
 * Created by sunjun on 2017/12/01.
 */
@Entity
@Table(name = "t_data_ftp")
public class FtpData {

    private Long id;
    private String vin;
    private Integer model;
    private String version;
    private Integer isUpdate;
    private Integer updateStep;
    private Integer downloadResult;
    private Integer errorCode;
    private Date createTime;

    public FtpData() {
        
    }

    public FtpData(Long id, String vin, Integer model, String version, Integer isUpdate, Integer updateStep, Integer downloadResult, Integer errorCode, Date createTime) {
        this.id = id;
        this.vin = vin;
        this.model = model;
        this.version = version;
        this.isUpdate = isUpdate;
        this.updateStep = updateStep;
        this.downloadResult = downloadResult;
        this.errorCode = errorCode;
        this.createTime = createTime;
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false, insertable = true, updatable = true)
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    @Column(name = "vin", nullable = false, insertable = true, updatable = true)
    public String getVin() {
        return vin;
    }

    public void setVin(String vin) {
        this.vin = vin;
    }

    @Column(name = "model", nullable = false, insertable = true, updatable = true)
    public Integer getModel() {
        return model;
    }

    public void setModel(Integer model) {
        this.model = model;
    }

    @Column(name = "version", nullable = false, insertable = true, updatable = true)
    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    @Column(name = "isupdate", nullable = false, insertable = true, updatable = true)
    public Integer getIsUpdate() {
        return isUpdate;
    }

    public void setIsUpdate(Integer isUpdate) {
        this.isUpdate = isUpdate;
    }

    @Column(name = "updatestep", nullable = false, insertable = true, updatable = true)
    public Integer getUpdateStep() {
        return updateStep;
    }

    public void setUpdateStep(Integer updateStep) {
        this.updateStep = updateStep;
    }

    @Column(name = "downloadresult", nullable = false, insertable = true, updatable = true)
    public Integer getDownloadResult() {
        return downloadResult;
    }

    public void setDownloadResult(Integer downloadResult) {
        this.downloadResult = downloadResult;
    }

    @Column(name = "errorcode", nullable = false, insertable = true, updatable = true)
    public Integer getErrorCode() {
        return errorCode;
    }

    public void setErrorCode(Integer errorCode) {
        this.errorCode = errorCode;
    }

    @Column(name = "createtime", nullable = false, insertable = true, updatable = true)
    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }
}
