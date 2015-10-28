package com.hp.triclops.entity;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

/**
 * 用户类
 */
@Entity
@Table(name = "t_vehicle")
public class Vehicle implements Serializable {
    private int id;
    private String vin;
    private String vendor;
    private String model;
    private Integer t_flag;
    private String displacement;
    private String license_plate;
    private Date product_date;
    private String tboxsn;
    private Set<TBox> tboxSet;

    private Set<UserVehicleRelatived> vinSet;

    private Set<Organization> organizationSet;

    public Vehicle() {
        this.vinSet = new HashSet<UserVehicleRelatived>();
        this.organizationSet = new HashSet<Organization>();
        this.tboxSet = new HashSet<TBox>();
    }



    public Vehicle(int id,String vin,String vendor,String model,Integer t_flag,String displacement,String license_plate,Date product_date,String tboxsn){
        this.id = id;
        this.vin = vin;
        
        this.vendor = vendor;
        this.model = model;
        this.t_flag = t_flag;
        this.displacement = displacement;
        this.license_plate = license_plate;
        this.product_date = product_date;
        this.tboxsn = tboxsn;
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

    @Basic
    @Column(name = "tboxsn", nullable = true, insertable = true, updatable = true)
    public String getTboxsn() {
        return tboxsn;
    }

    public void setTboxsn(String tboxsn) {
        this.tboxsn = tboxsn;
    }

    @OneToMany(mappedBy = "vid", cascade = CascadeType.REMOVE, fetch = FetchType.LAZY)
    public Set<UserVehicleRelatived> getVinSet() {
        return vinSet;
    }

    public void setVinSet(Set<UserVehicleRelatived> vinSet) {
        this.vinSet = vinSet;
    }

    @ManyToMany(cascade = CascadeType.PERSIST, fetch = FetchType.LAZY)
    @JoinTable(name = "t_organization_vehicle",
            joinColumns ={@JoinColumn(name = "vid", referencedColumnName = "id") },
            inverseJoinColumns = { @JoinColumn(name = "oid", referencedColumnName = "id")
            })
    public Set<Organization> getOrganizationSet() {
        return organizationSet;
    }

    public void setOrganizationSet(Set<Organization> organizationSet) {
        this.organizationSet = organizationSet;
    }

    @OneToMany(mappedBy = "vehicle", cascade ={CascadeType.REMOVE, CascadeType.PERSIST})
    public Set<TBox> getTboxSet() {
        return tboxSet;
    }

    public void setTboxSet(Set<TBox> tboxSet) {
        this.tboxSet = tboxSet;
    }

    public void addTboxVehicle(TBox tbox) {
        tbox.setVehicle(this);
        this.tboxSet.add(tbox);
    }


}
