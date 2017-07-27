package com.hp.triclops.entity;

import javax.persistence.*;
import java.util.Date;

/**
 * Created by luj on 2015/9/28.
 */
/**
 * <table summary="GpsData" class="typeSummary">
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
 *             <td>Long</td>
 *             <td>ID</td>
 *         </tr>
 *         <tr>
 *             <td>vin</td>
 *             <td>String</td>
 *             <td>车辆VIN码</td>
 *         </tr>
 *         <tr>
 *             <td>imei</td>
 *             <td>String</td>
 *             <td>T-Box imei</td>
 *         </tr>
 *         <tr>
 *             <td>applicationId</td>
 *             <td>int</td>
 *             <td>业务标识</td>
 *         </tr>
 *         <tr>
 *             <td>messageId</td>
 *             <td>int</td>
 *             <td>消息标识</td>
 *         </tr>
 *         <tr>
 *             <td>sendingTime</td>
 *             <td>Date</td>
 *             <td>发送时间</td>
 *         </tr>
 *         <tr>
 *             <td>isLocation</td>
 *             <td>Short</td>
 *             <td>是否定位</td>
 *         </tr>
 *         <tr>
 *             <td>northSouth</td>
 *             <td>String</td>
 *             <td>南纬北纬</td>
 *         </tr>
 *         <tr>
 *             <td>eastWest</td>
 *             <td>String</td>
 *             <td>东经西经</td>
 *         </tr>
 *         <tr>
 *             <td>latitude</td>
 *             <td>double</td>
 *             <td>纬度</td>
 *         </tr>
 *          <tr>
 *             <td>longitude</td>
 *             <td>double</td>
 *             <td>经度</td>
 *         </tr>
 *          <tr>
 *             <td>speed</td>
 *             <td>float</td>
 *             <td>速度</td>
 *         </tr>
 *          <tr>
 *             <td>heading</td>
 *             <td>int</td>
 *             <td>方向角</td>
 *         </tr>
 *     </tbody>
 * </table>
 */
@Entity
@Table(name = "t_data_gps")
public class GpsData {

    private Long id;
    private String vin;
    private String imei;
    private int applicationId;
    private int messageId;
    private Date sendingTime;
    private Short isLocation;
    private String northSouth;
    private String eastWest;
    private double latitude;
    private double longitude;
    private float speed;
    private int heading;
    private Vehicle vehicle;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false, insertable = true, updatable = true)
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    @Basic
    @Column(name = "vin", nullable = false, insertable = true, updatable = true, length = 50)
    public String getVin() {
        return vin;
    }

    public void setVin(String vin) {
        this.vin = vin;
    }

    @Basic
    @Column(name = "imei", nullable = false, insertable = true, updatable = true, length = 50)
    public String getImei() {
        return imei;
    }

    public void setImei(String imei) {
        this.imei = imei;
    }

    @Basic
    @Column(name = "application_id", nullable = false, insertable = true, updatable = true)
    public int getApplicationId() {
        return applicationId;
    }

    public void setApplicationId(int applicationId) {
        this.applicationId = applicationId;
    }

    @Basic
    @Column(name = "message_id", nullable = false, insertable = true, updatable = true)
    public int getMessageId() {
        return messageId;
    }

    public void setMessageId(int messageId) {
        this.messageId = messageId;
    }

    @Basic
    @Column(name = "sending_time", nullable = false, insertable = true, updatable = true)
    public Date getSendingTime() {
        return sendingTime;
    }

    public void setSendingTime(Date sendingTime) {
        this.sendingTime = sendingTime;
    }

    @Basic
    @Column(name = "is_location", nullable = false, insertable = true, updatable = true)
    public Short getIsLocation() {
        return isLocation;
    }

    public void setIsLocation(Short isLocation) {
        this.isLocation = isLocation;
    }

    @Basic
    @Column(name = "north_south", nullable = false, insertable = true, updatable = true, length = 1)
    public String getNorthSouth() {
        return northSouth;
    }

    public void setNorthSouth(String northSouth) {
        this.northSouth = northSouth;
    }
    @Basic
    @Column(name = "east_west", nullable = false, insertable = true, updatable = true, length = 1)
    public String getEastWest() {
        return eastWest;
    }

    public void setEastWest(String eastWest) {
        this.eastWest = eastWest;
    }


    @Basic
    @Column(name = "latitude", nullable = false, insertable = true, updatable = true)
    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    @Basic
    @Column(name = "longitude", nullable = false, insertable = true, updatable = true)
    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    @Basic
    @Column(name = "speed", nullable = false, insertable = true, updatable = true)
    public float getSpeed() {
        return speed;
    }

    public void setSpeed(float speed) {
        this.speed = speed;
    }

    @Basic
    @Column(name = "heading", nullable = false, insertable = true, updatable = true)
    public int getHeading() {
        return heading;
    }

    public void setHeading(int heading) {
        this.heading = heading;
    }

    @OneToOne
    @JoinColumn(name="vin", referencedColumnName="vin", insertable = false, updatable = false)
    public Vehicle getVehicle() {
        return vehicle;
    }

    public void setVehicle(Vehicle vehicle) {
        this.vehicle = vehicle;
    }

}
