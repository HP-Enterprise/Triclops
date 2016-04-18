package com.hp.triclops.entity;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

/**
 * 车辆实体类
 */

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
@Entity
@Table(name = "t_vehicle")
public class Vehicle implements Serializable {
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
    private Integer remoteCount;
    private Set<TBox> tboxSet;
    private Set<UserVehicleRelatived> vinSet;
    private Set<Organization> organizationSet;

    public Vehicle() {
        this.vinSet = new HashSet<UserVehicleRelatived>();
        this.organizationSet = new HashSet<Organization>();
        this.tboxSet = new HashSet<TBox>();
    }

    public Vehicle(int id, String vin, String tboxsn, String vendor, String model, String displacement, Date product_date, String vcolor, String buystore, Date buydate, String vpurl, Integer vtype, String license_plate, Integer t_flag, String security_pwd, String security_salt) {
        this.id = id;
        this.vin = vin;
        this.tboxsn = tboxsn;
        this.vendor = vendor;
        this.model = model;
        this.displacement = displacement;
        this.product_date = product_date;
        this.vcolor = vcolor;
        this.buystore = buystore;
        this.buydate = buydate;
        this.vpurl = vpurl;
        this.vtype = vtype;
        this.license_plate = license_plate;
        this.t_flag = t_flag;
        this.security_pwd = security_pwd;
        this.security_salt = security_salt;
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
    @Column(name = "displacement", nullable = false, insertable = true, updatable = true, length = 100)
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
    @Column(name = "license_plate", nullable = true, insertable = true, updatable = true, length = 100)
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
    @Column(name = "security_pwd", nullable = true, insertable = true, updatable = true, length = 32)
    public String getSecurity_pwd() {
        return security_pwd;
    }

    public void setSecurity_pwd(String security_pwd) {
        this.security_pwd = security_pwd;
    }

    @Basic
    @Column(name = "security_salt", nullable = true, insertable = true, updatable = true, length = 4)
    public String getSecurity_salt() {
        return security_salt;
    }

    public void setSecurity_salt(String security_salt) {
        this.security_salt = security_salt;
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

    @OneToMany(mappedBy = "vehicle", cascade ={CascadeType.REMOVE, CascadeType.PERSIST}, fetch = FetchType.LAZY)
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

    public Integer getRemoteCount() {
        return remoteCount;
    }

    public void setRemoteCount(Integer remoteCount) {
        this.remoteCount = remoteCount;
    }
}
