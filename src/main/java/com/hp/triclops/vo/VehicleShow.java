package com.hp.triclops.vo;

import com.hp.triclops.entity.Vehicle;

import java.util.Date;

/**
 * <table summary="VehicleShow" class="typeSummary">
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
 *             <td>用户ID</td>
 *         </tr>
 *         <tr>
 *             <td>vin</td>
 *             <td>String</td>
 *             <td>车辆VIN码</td>
 *         </tr>
 *         <tr>
 *             <td>vendor</td>
 *             <td>String</td>
 *             <td>厂家</td>
 *         </tr>
 *         <tr>
 *             <td>model</td>
 *             <td>String</td>
 *             <td>型号</td>
 *         </tr>
 *         <tr>
 *             <td>t_flag</td>
 *             <td>int</td>
 *             <td>车辆禁用标示0-未禁用 1-禁用</td>
 *         </tr>
 *         <tr>
 *             <td>displacement</td>
 *             <td>String</td>
 *             <td>排量</td>
 *         </tr>
 *         <tr>
 *             <td>license_plate</td>
 *             <td>String</td>
 *             <td>车牌号</td>
 *         </tr>
 *         <tr>
 *             <td>product_date</td>
 *             <td>Date</td>
 *             <td>生产日期</td>
 *         </tr>
 *         <tr>
 *             <td>tboxsn</td>
 *             <td>String</td>
 *             <td>tbox码</td>
 *         </tr>
 *         <tr>
 *             <td>oid</td>
 *             <td>int</td>
 *             <td>组织ID</td>
 *         </tr>
 *          <tr>
 *             <td>vflag</td>
 *             <td>int</td>
 *             <td>是否为默认车辆</td>
 *         </tr>
 *     </tbody>
 * </table>
 */
public class VehicleShow {
    private int id;
    private String vin;
    private String vendor;
    private String model;
    private Integer t_flag;
    private String displacement;
    private String license_plate;
    private Date product_date;
    private String tboxsn;
    private Integer oid;
    private Integer vflag;

    public VehicleShow() {
    }

    public VehicleShow(Vehicle vehicle) {
        this.id = vehicle.getId();
        this.vin = vehicle.getVin();
        this.vendor = vehicle.getVendor();
        this.model = vehicle.getModel();
        this.t_flag = vehicle.getT_flag();
        this.displacement = vehicle.getDisplacement();
        this.license_plate = vehicle.getLicense_plate();
        this.product_date = vehicle.getProduct_date();
        this.tboxsn = vehicle.getTboxsn();
    }

    public VehicleShow(Vehicle vehicle,Integer vflag) {
        this.id = vehicle.getId();
        this.vin = vehicle.getVin();
        this.vendor = vehicle.getVendor();
        this.model = vehicle.getModel();
        this.t_flag = vehicle.getT_flag();
        this.displacement = vehicle.getDisplacement();
        this.license_plate = vehicle.getLicense_plate();
        this.product_date = vehicle.getProduct_date();
        this.tboxsn = vehicle.getTboxsn();
        this.vflag = vflag;
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

    public Integer getT_flag() {
        return t_flag;
    }

    public void setT_flag(Integer t_flag) {
        this.t_flag = t_flag;
    }

    public String getDisplacement() {
        return displacement;
    }

    public void setDisplacement(String displacement) {
        this.displacement = displacement;
    }

    public String getLicense_plate() {
        return license_plate;
    }

    public void setLicense_plate(String license_plate) {
        this.license_plate = license_plate;
    }

    public Date getProduct_date() {
        return product_date;
    }

    public void setProduct_date(Date product_date) {
        this.product_date = product_date;
    }

    public String getTboxsn() {return tboxsn;}

    public void setTboxsn(String tboxsn) {this.tboxsn = tboxsn;}

    public Integer getOid() {
        return oid;
    }

    public void setOid(Integer oid) {
        this.oid = oid;
    }

    public Integer getVflag() {
        return vflag;
    }

    public void setVflag(Integer vflag) {
        this.vflag = vflag;
    }
}
