package com.hp.triclops.vo;

import com.hp.triclops.entity.Vehicle;

import java.util.Date;

/**
 * <table summary="Vehicle" class="typeSummary">
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
 *             <td>int</td>
 *             <td>车辆VIN码</td>
 *         </tr>
 *         <tr>
 *             <td>tboxsn</td>
 *             <td>String</td>
 *             <td>tbox码</td>
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
 *             <td>displacement </td>
 *             <td>String</td>
 *             <td>排量</td>
 *         </tr>
 *         <tr>
 *             <td>product_date</td>
 *             <td>datetime</td>
 *             <td>生产日期</td>
 *         </tr>
 *          <tr>
 *             <td>vcolor</td>
 *             <td>String</td>
 *             <td>颜色</td>
 *         </tr>
 *          <tr>
 *             <td>buystore</td>
 *             <td>String</td>
 *             <td>购买4S店</td>
 *         </tr>
 *          <tr>
 *             <td>buydate</td>
 *             <td>datetime</td>
 *             <td>购买时间</td>
 *         </tr>
 *          <tr>
 *             <td>vpurl</td>
 *             <td>String</td>
 *             <td>车辆图片URL</td>
 *         </tr>
 *          <tr>
 *             <td>vtype</td>
 *             <td>int</td>
 *             <td>车辆类型(1:乘用 2:电动)</td>
 *         </tr>
 *          <tr>
 *             <td>license_plate</td>
 *             <td>String</td>
 *             <td>车牌号</td>
 *         </tr>
 *          <tr>
 *             <td>t_flag</td>
 *             <td>int</td>
 *             <td>0 车辆未禁用 1 已禁用</td>
 *         </tr>
 *          <tr>
 *             <td>security_pwd</td>
 *             <td>String</td>
 *             <td>安防密码</td>
 *         </tr>
 *          <tr>
 *             <td>security_salt</td>
 *             <td>String</td>
 *             <td>salt</td>
 *         </tr>
 *     </tbody>
 * </table>
 */
public class VehicleShow {
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
    private Integer oid;
    private Integer vflag;

    public VehicleShow() {
    }

    public VehicleShow(Vehicle vehicle) {
        this.id = vehicle.getId();
        this.vin = vehicle.getVin();
        this.tboxsn = vehicle.getTboxsn();
        this.vendor = vehicle.getVendor();
        this.model = vehicle.getModel();
        this.displacement = vehicle.getDisplacement();
        this.product_date = vehicle.getProduct_date();
        this.vcolor = vehicle.getVcolor();
        this.buystore = vehicle.getBuystore();
        this.buydate = vehicle.getBuydate();
        this.vpurl = vehicle.getVpurl();
        this.vtype = vehicle.getVtype();
        this.license_plate = vehicle.getLicense_plate();
        this.t_flag = vehicle.getT_flag();
        this.security_pwd = vehicle.getSecurity_pwd();
        this.security_salt = vehicle.getSecurity_salt();
    }

    public VehicleShow(Vehicle vehicle,Integer vflag) {
        this.id = vehicle.getId();
        this.vin = vehicle.getVin();
        this.tboxsn = vehicle.getTboxsn();
        this.vendor = vehicle.getVendor();
        this.model = vehicle.getModel();
        this.displacement = vehicle.getDisplacement();
        this.product_date = vehicle.getProduct_date();
        this.vcolor = vehicle.getVcolor();
        this.buystore = vehicle.getBuystore();
        this.buydate = vehicle.getBuydate();
        this.vpurl = vehicle.getVpurl();
        this.vtype = vehicle.getVtype();
        this.license_plate = vehicle.getLicense_plate();
        this.t_flag = vehicle.getT_flag();
        this.security_pwd = vehicle.getSecurity_pwd();
        this.security_salt = vehicle.getSecurity_salt();
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
