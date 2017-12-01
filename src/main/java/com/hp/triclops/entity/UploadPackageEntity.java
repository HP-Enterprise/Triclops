package com.hp.triclops.entity;

import javax.persistence.*;
import java.util.Date;

/**
 * 系统跟新包信息表
 */

/**
 * <table summary="UploadPackageEntity" class="typeSummary">
 *     <thead>
 *         <tr>
 *             <th>字段</th>
 *             <th>数据类型</th>
 *             <th>说明</th>
 *         </tr>
 *     </thead>
 *     <tbody>
 *         <tr>
 *             <td>id</td>
 *             <td>int</td>
 *             <td>上传包的id</td>
 *         </tr>
 *         <tr>
 *             <td>model</td>
 *             <td>int</td>
 *             <td>终端类型的id</td>
 *         </tr>
 *         <tr>
 *             <td>version</td>
 *             <td>String</td>
 *             <td>升级包的版本号</td>
 *         </tr>
 *         <tr>
 *             <td>fileName</td>
 *             <td>String</td>
 *             <td>文件名</td>
 *         </tr>
 *         <tr>
 *             <td>uploadName</td>
 *             <td>String</td>
 *             <td>上传到sftp的文件名</td>
 *         </tr>
 *         <tr>
 *             <td>androidOS</td>
 *             <td>String</td>
 *             <td>android系统版本</td>
 *         </tr>
 *         <tr>
 *             <td>uploadTime</td>
 *             <td>Date</td>
 *             <td>上传时间</td>
 *         </tr>
 *         <tr>
 *             <td>creater</td>
 *             <td>String</td>
 *             <td>上传的人</td>
 *         </tr>
 *         <tr>
 *             <td>available</td>
 *             <td>int</td>
 *             <td>是否可用 1:可用 2：禁用</td>
 *         </tr>
 *     </tbody>
 * </table>
 */
@Entity
@Table(name="t_upload_package")
public class UploadPackageEntity {
    private int id;
    private int model;    //终端类型
    private String version; //升级包的版本号
    private String fileName; //文件名
    private String uploadName; //上传到sftp文件名
    private String androidOS;  //android系统版本
    private Date uploadTime;  //上传时间
    private String creater;  //上传的人
    private int available;  //是否可用
    private String description; //描述

    public UploadPackageEntity() {
    }

    public UploadPackageEntity(int id, int model, String version, String fileName, String uploadName, String androidOS, Date uploadTime, String creater, int available, String description) {
        this.id = id;
        this.model = model;
        this.version = version;
        this.fileName = fileName;
        this.uploadName = uploadName;
        this.androidOS = androidOS;
        this.uploadTime = uploadTime;
        this.creater = creater;
        this.available = available;
        this.description = description;
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

    @Basic
    @Column(name = "model", nullable = true, insertable = true, updatable = true)
    public int getModel() {
        return model;
    }

    public void setModel(int model) {
        this.model = model;
    }

    @Basic
    @Column(name = "version", nullable = true, insertable = true, updatable = true, length = 50)
    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    @Basic
    @Column(name = "file_name", nullable = true, insertable = true, updatable = true, length = 50)
    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    @Basic
    @Column(name = "upload_name", nullable = true, insertable = true, updatable = true, length = 32)
    public String getUploadName() {
        return uploadName;
    }

    public void setUploadName(String uploadName) {
        this.uploadName = uploadName;
    }

    @Basic
    @Column(name = "androidOS", nullable = true, insertable = true, updatable = true, length = 50)
    public String getAndroidOS() {
        return androidOS;
    }

    public void setAndroidOS(String androidOS) {
        this.androidOS = androidOS;
    }

    @Basic
    @Column(name = "upload_time", nullable = false, insertable = true, updatable = true)
    public Date getUploadTime() {
        return uploadTime;
    }

    public void setUploadTime(Date uploadTime) {
        this.uploadTime = uploadTime;
    }

    @Basic
    @Column(name = "creater", nullable = true, insertable = true, updatable = true, length = 50)
    public String getCreater() {
        return creater;
    }

    public void setCreater(String creater) {
        this.creater = creater;
    }

    @Basic
    @Column(name = "available", nullable = true, insertable = true, updatable = true)
    public int getAvailable() {
        return available;
    }

    public void setAvailable(int available) {
        this.available = available;
    }

    @Basic
    @Column(name = "description", nullable = true, insertable = true, updatable = true,length = 1000)
    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
