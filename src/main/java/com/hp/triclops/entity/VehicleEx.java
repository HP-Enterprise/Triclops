package com.hp.triclops.entity;

import com.hp.triclops.vo.VehicleExShow;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

/**
 * Created by Teemol on 2016/1/22.
 */

@Entity
@Table(name = "t_vehicle")
public class VehicleEx implements Serializable {

    private int id;
    private String vin;
    private String tboxsn;
    private String vendor;
    private String model;
    private String displacement;
    private Date product_date;
    private String vcolor;
    private String buystore;
    private Date buydate;
    private String vpurl;
    private Integer vtype;
    private String license_plate;
    private Integer t_flag;
    private String security_pwd;
    private String security_salt;
    private Integer isUpdate;
    private String realNameAuthentication; // 实名认证状态 0未实名 1已实名 2审核中 3认证失败

    private Date regTime; //注册时间

    private Integer hwisUpdate; //固件是否升级
    private String softVersion; //版本号
    private String hardVersion; //固件版本号
    private String version;//当前已完成升级版本号
    private String srcVersion;//原固件版本号

    private String activeState;//激活状态

    public VehicleEx() {
    }

    public VehicleEx(VehicleExShow vehicleExShow) {
        this.id = vehicleExShow.getId();
        this.vin = vehicleExShow.getVin();
        this.tboxsn = vehicleExShow.getTboxsn();
        this.vendor = vehicleExShow.getVendor();
        this.model = vehicleExShow.getModel();
        this.displacement = vehicleExShow.getDisplacement();
        this.product_date = vehicleExShow.getProduct_date();
        this.vcolor = vehicleExShow.getVcolor();
        this.buystore = vehicleExShow.getBuystore();
        this.buydate = vehicleExShow.getBuydate();
        this.vpurl = vehicleExShow.getVpurl();
        this.vtype = vehicleExShow.getVtype();
        this.license_plate = vehicleExShow.getLicense_plate();
        this.t_flag = vehicleExShow.getT_flag();
        this.security_pwd = vehicleExShow.getSecurity_pwd();
        this.security_salt = vehicleExShow.getSecurity_salt();
        this.realNameAuthentication = vehicleExShow.getRealNameAuthentication();
        this.isUpdate = vehicleExShow.getIsUpdate();
        this.regTime = vehicleExShow.getRegTime();
        this.hwisUpdate = vehicleExShow.getHwisUpdate();
        this.softVersion = vehicleExShow.getSoftVersion();
        this.hardVersion = vehicleExShow.getHardVersion();
        this.version = vehicleExShow.getVersion();
        this.activeState = vehicleExShow.getActiveState();
        this.srcVersion = vehicleExShow.getSrcVersion();
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
    @Column(name = "vin", nullable = false, insertable = true, updatable = true)
    public String getVin() {
        return vin;
    }

    public void setVin(String vin) {
        this.vin = vin;
    }

    @Basic
    @Column(name = "tboxsn", nullable = true, insertable = true, updatable = true)
    public String getTboxsn() {
        return tboxsn;
    }

    public void setTboxsn(String tboxsn) {
        this.tboxsn = tboxsn;
    }

    @Basic
    @Column(name = "vendor", nullable = false, insertable = true, updatable = true)
    public String getVendor() {
        return vendor;
    }

    public void setVendor(String vendor) {
        this.vendor = vendor;
    }

    @Basic
    @Column(name = "model", nullable = false, insertable = true, updatable = true)
    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    @Basic
    @Column(name = "displacement", nullable = false, insertable = true, updatable = true)
    public String getDisplacement() {
        return displacement;
    }

    public void setDisplacement(String displacement) {
        this.displacement = displacement;
    }

    @Basic
    @Column(name = "product_date", nullable = true, insertable = true, updatable = true)
    public Date getProduct_date() {
        return product_date;
    }

    public void setProduct_date(Date product_date) {
        this.product_date = product_date;
    }

    @Basic
    @Column(name = "reg_time", nullable = true, insertable = true, updatable = true)
    public Date getRegTime() {
        return regTime;
    }

    public void setRegTime(Date regTime) {
        this.regTime = regTime;
    }

    @Basic
    @Column(name = "vcolor", nullable = true, insertable = true, updatable = true)
    public String getVcolor() {
        return vcolor;
    }

    public void setVcolor(String vcolor) {
        this.vcolor = vcolor;
    }

    @Basic
    @Column(name = "buystore", nullable = true, insertable = true, updatable = true)
    public String getBuystore() {
        return buystore;
    }

    public void setBuystore(String buystore) {
        this.buystore = buystore;
    }

    @Basic
    @Column(name = "buydate", nullable = true, insertable = true, updatable = true)
    public Date getBuydate() {
        return buydate;
    }

    public void setBuydate(Date buydate) {
        this.buydate = buydate;
    }

    @Basic
    @Column(name = "vpurl", nullable = true, insertable = true, updatable = true)
    public String getVpurl() {
        return vpurl;
    }

    public void setVpurl(String vpurl) {
        this.vpurl = vpurl;
    }

    @Basic
    @Column(name = "vtype", nullable = true, insertable = true, updatable = true)
    public Integer getVtype() {
        return vtype;
    }

    public void setVtype(Integer vtype) {
        this.vtype = vtype;
    }

    @Basic
    @Column(name = "license_plate", nullable = true, insertable = true, updatable = true)
    public String getLicense_plate() {
        return license_plate;
    }

    public void setLicense_plate(String license_plate) {
        this.license_plate = license_plate;
    }

    @Basic
    @Column(name = "t_flag", nullable = true, insertable = true, updatable = true)
    public Integer getT_flag() {
        return t_flag;
    }

    public void setT_flag(Integer t_flag) {
        this.t_flag = t_flag;
    }

    @Basic
    @Column(name = "security_pwd", nullable = true, insertable = true, updatable = true)
    public String getSecurity_pwd() {
        return security_pwd;
    }

    public void setSecurity_pwd(String security_pwd) {
        this.security_pwd = security_pwd;
    }

    @Basic
    @Column(name = "security_salt", nullable = true, insertable = true, updatable = true)
    public String getSecurity_salt() {
        return security_salt;
    }

    public void setSecurity_salt(String security_salt) {
        this.security_salt = security_salt;
    }

    @Basic
    @Column(name = "isupdate", nullable = true, insertable = true, updatable = true)
    public Integer getIsUpdate() {
        return isUpdate;
    }

    public void setIsUpdate(Integer isUpdate) {
        this.isUpdate = isUpdate;
    }

    @Basic
    @Column(name = "real_name_authentication", nullable = true, insertable = true, updatable = true)
    public String getRealNameAuthentication() {
        return realNameAuthentication;
    }

    public void setRealNameAuthentication(String realNameAuthentication) {
        this.realNameAuthentication = realNameAuthentication;
    }

    @Basic
    @Column(name = "hwisupdate", nullable = true, insertable = true, updatable = true)
    public Integer getHwisUpdate() {
        return hwisUpdate;
    }

    public void setHwisUpdate(Integer hwisUpdate) {
        this.hwisUpdate = hwisUpdate;
    }

    @Basic
    @Column(name = "hard_version", nullable = true, insertable = true, updatable = true)
    public String getHardVersion() {
        return hardVersion;
    }

    public void setHardVersion(String hardVersion) {
        this.hardVersion = hardVersion;
    }

    @Basic
    @Column(name = "soft_version", nullable = true, insertable = true, updatable = true)
    public String getSoftVersion() {
        return softVersion;
    }

    public void setSoftVersion(String softVersion) {
        this.softVersion = softVersion;
    }

    @Basic
    @Column(name = "version", nullable = true, insertable = true, updatable = true)
    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    @Basic
    @Column(name = "active_state", nullable = true, insertable = true, updatable = true)
    public String getActiveState() {
        return activeState;
    }

    public void setActiveState(String activeState) {
        this.activeState = activeState;
    }

    @Basic
    @Column(name = "src_version", nullable = true, insertable = true, updatable = true)
    public String getSrcVersion() {
        return srcVersion;
    }

    public void setSrcVersion(String srcVersion) {
        this.srcVersion = srcVersion;
    }
}
