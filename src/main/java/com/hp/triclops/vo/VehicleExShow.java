package com.hp.triclops.vo;

import com.hp.triclops.entity.VehicleEx;

import java.util.Date;

/**
 * Created by Teemol on 2016/1/22.
 */
public class VehicleExShow {

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
    private Integer vflag;
    private Integer isUpdate;
    private String realNameAuthentication; //实名认证状态 0已实名  1不通过   2审核中

    private Date regTime;

    private Integer hwisUpdate; //固件是否升级
    private String softVersion; //版本号
    private String hardVersion; //固件版本号
    private String version;//当前已完成升级版本号
    private String srcVersion;//原固件版本号

    private String activeState;//激活状态

    public VehicleExShow() {}

    public VehicleExShow(VehicleEx vehicleEx) {

        this.id = vehicleEx.getId();
        this.vin = vehicleEx.getVin();
        this.tboxsn = vehicleEx.getTboxsn();
        this.vendor = vehicleEx.getVendor();
        this.model = vehicleEx.getModel();
        this.displacement = vehicleEx.getDisplacement();
        this.product_date = vehicleEx.getProduct_date();
        this.vcolor = vehicleEx.getVcolor();
        this.buystore = vehicleEx.getBuystore();
        this.buydate = vehicleEx.getBuydate();
        this.vpurl = vehicleEx.getVpurl();
        this.vtype = vehicleEx.getVtype();
        this.license_plate = vehicleEx.getLicense_plate();
        this.t_flag = vehicleEx.getT_flag();
        this.security_pwd = vehicleEx.getSecurity_pwd();
        this.security_salt = vehicleEx.getSecurity_salt();
        this.isUpdate = vehicleEx.getIsUpdate();
        this.realNameAuthentication = vehicleEx.getRealNameAuthentication();
        this.isUpdate = vehicleEx.getIsUpdate();
        this.regTime = vehicleEx.getRegTime();
        this.hwisUpdate = vehicleEx.getHwisUpdate();
        this.softVersion = vehicleEx.getSoftVersion();
        this.hardVersion = vehicleEx.getHardVersion();
        this.version = vehicleEx.getVersion();
        this.activeState = vehicleEx.getActiveState();
        this.srcVersion = vehicleEx.getSrcVersion();
    }

    /**
     * 车辆信息模糊化
     */
    public void blur()
    {
        if(vin!=null && vin.length()>6)
        {
            this.vin = vin.substring(0,vin.length()-4) + "****";
        }

        if(tboxsn!=null && tboxsn.length()>6)
        {
            this.tboxsn = tboxsn.substring(0,tboxsn.length()-4) + "****";
        }

        if(license_plate!=null && license_plate.length()>3)
        {
            this.license_plate = license_plate.substring(0,license_plate.length()-3) + "***";
        }

        this.buystore = null;
        this.buydate = null;
        this.security_pwd = "****";
        this.security_salt = "****";
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getVin() {
        return vin;
    }

    public void setVin(String vin) {
        this.vin = vin;
    }

    public String getTboxsn() {
        return tboxsn;
    }

    public void setTboxsn(String tboxsn) {
        this.tboxsn = tboxsn;
    }

    public String getVendor() {
        return vendor;
    }

    public void setVendor(String vendor) {
        this.vendor = vendor;
    }

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public String getDisplacement() {
        return displacement;
    }

    public void setDisplacement(String displacement) {
        this.displacement = displacement;
    }

    public Date getProduct_date() {
        return product_date;
    }

    public void setProduct_date(Date product_date) {
        this.product_date = product_date;
    }

    public String getVcolor() {
        return vcolor;
    }

    public void setVcolor(String vcolor) {
        this.vcolor = vcolor;
    }

    public String getBuystore() {
        return buystore;
    }

    public void setBuystore(String buystore) {
        this.buystore = buystore;
    }

    public Date getBuydate() {
        return buydate;
    }

    public void setBuydate(Date buydate) {
        this.buydate = buydate;
    }

    public String getVpurl() {
        return vpurl;
    }

    public void setVpurl(String vpurl) {
        this.vpurl = vpurl;
    }

    public Integer getVtype() {
        return vtype;
    }

    public void setVtype(Integer vtype) {
        this.vtype = vtype;
    }

    public String getLicense_plate() {
        return license_plate;
    }

    public void setLicense_plate(String license_plate) {
        this.license_plate = license_plate;
    }

    public Integer getT_flag() {
        return t_flag;
    }

    public void setT_flag(Integer t_flag) {
        this.t_flag = t_flag;
    }

    public String getSecurity_pwd() {
        return security_pwd;
    }

    public void setSecurity_pwd(String security_pwd) {
        this.security_pwd = security_pwd;
    }

    public String getSecurity_salt() {
        return security_salt;
    }

    public void setSecurity_salt(String security_salt) {
        this.security_salt = security_salt;
    }

    public Integer getVflag() {
        return vflag;
    }

    public void setVflag(Integer vflag) {
        this.vflag = vflag;
    }

    public Integer getIsUpdate() {
        return isUpdate;
    }

    public void setIsUpdate(Integer isUpdate) {
        this.isUpdate = isUpdate;
    }

    public String getRealNameAuthentication() {
        return realNameAuthentication;
    }

    public void setRealNameAuthentication(String realNameAuthentication) {
        this.realNameAuthentication = realNameAuthentication;
    }

    public Date getRegTime() {
        return regTime;
    }

    public void setRegTime(Date regTime) {
        this.regTime = regTime;
    }

    public Integer getHwisUpdate() {
        return hwisUpdate;
    }

    public void setHwisUpdate(Integer hwisUpdate) {
        this.hwisUpdate = hwisUpdate;
    }

    public String getSoftVersion() {
        return softVersion;
    }

    public void setSoftVersion(String softVersion) {
        this.softVersion = softVersion;
    }

    public String getHardVersion() {
        return hardVersion;
    }

    public void setHardVersion(String hardVersion) {
        this.hardVersion = hardVersion;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public String getActiveState() {
        return activeState;
    }

    public void setActiveState(String activeState) {
        this.activeState = activeState;
    }

    public String getSrcVersion() {
        return srcVersion;
    }

    public void setSrcVersion(String srcVersion) {
        this.srcVersion = srcVersion;
    }
}
