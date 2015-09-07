package com.hp.triclops.entity;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

/**
 * 用户类
 */
@Entity
@Table(name = "t_vehicle")
public class Vehicle implements Serializable {
    private String vin;
    private String vendor;
    private String model;
    private Integer t_flag;
    private String displacement;
    private String license_plate;
    private Date product_date;

    public Vehicle() {}

    public Vehicle(String vin,String vendor,String model,Integer t_flag,String displacement,String license_plate,Date product_date){
        this.vin = vin;
        this.vendor = vendor;
        this.model = model;
        this.t_flag = t_flag;
        this.displacement = displacement;
        this.license_plate = license_plate;
        this.product_date = product_date;
    }

    @Id
    @Column(name = "vin", nullable = false, insertable = true, updatable = true)
    public String getVin() {
        return vin;
    }

    public void setVin(String vin) {
        this.vin = vin;
    }

    @Basic
    @Column(name = "vendor", nullable = false, insertable = true, updatable = true, length = 50)
    public String getVendor() {
        return vendor;
    }

    public void setVendor(String vendor) {
        this.vendor = vendor;
    }

    @Basic
    @Column(name = "model", nullable = false, insertable = true, updatable = true, length = 20)
    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
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
    @Column(name = "displacement", nullable = false, insertable = true, updatable = true, length = 100)
    public String getDisplacement() {
        return displacement;
    }

    public void setDisplacement(String displacement) {
        this.displacement = displacement;
    }

    @Basic
    @Column(name = "license_plate", nullable = true, insertable = true, updatable = true, length = 100)
    public String getLicense_plate() {
        return license_plate;
    }

    public void setLicense_plate(String license_plate) {
        this.license_plate = license_plate;
    }

    @Basic
    @Column(name = "product_date", nullable = true, insertable = true, updatable = true)
    public Date getProduct_date() {
        return product_date;
    }

    public void setProduct_date(Date product_date) {
        this.product_date = product_date;
    }

}
