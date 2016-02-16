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
        this.license_plate = vehicleEx.getLicense_plate();
        this.t_flag = vehicleEx.getT_flag();
        this.security_pwd = vehicleEx.getSecurity_pwd();
        this.security_salt = vehicleEx.getSecurity_salt();
    }

    public void blur()
    {
        if(vin!=null && vin.length()>6)
        {
            this.vin = vin.substring(0,vin.length()-6) + "******";
        }
        else if(vin!=null)
        {
            this.vin = "******";
        }

        if(tboxsn!=null && tboxsn.length()>6)
        {
            this.tboxsn = tboxsn.substring(0,tboxsn.length()-6) + "******";
        }
        else if(tboxsn!=null)
        {
            this.license_plate = "******";
        }

        if(license_plate!=null && license_plate.length()>3)
        {
            this.license_plate = license_plate.substring(0,license_plate.length()-3) + "***";
        }
        else if(license_plate!=null)
        {
            this.license_plate = "******";
        }

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
}
