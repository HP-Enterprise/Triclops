package com.hp.triclops.vo;

import java.util.Date;

/**
 * Created by luj on 2015/11/2.
 */

/**
 * <table summary="RealTimeDataShow" class="typeSummary">
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
 *          <tr>
 *             <td>imei</td>
 *             <td>String</td>
 *             <td>T-box imei</td>
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
 *             <td>fuelOil</td>
 *             <td>Short</td>
 *             <td>燃油量</td>
 *         </tr>
 *         <tr>
 *             <td>avgOil</td>
 *             <td>float</td>
 *             <td>平均油耗</td>
 *         </tr>
 *         <tr>
 *             <td>oilLife</td>
 *             <td>Short</td>
 *             <td>机油寿命</td>
 *         </tr>
 *         <tr>
 *             <td>driveRange</td>
 *             <td>int</td>
 *             <td>行驶里程</td>
 *         </tr>
 *         <tr>
 *             <td>leftFrontTirePressure</td>
 *             <td>int</td>
 *             <td>左前轮胎压</td>
 *         </tr>
 *         <tr>
 *             <td>leftRearTirePressure</td>
 *             <td>int</td>
 *             <td>左后轮胎压</td>
 *         </tr>
 *         <tr>
 *             <td>rightFrontTirePressure</td>
 *             <td>int</td>
 *             <td>右前轮胎压</td>
 *         </tr>
 *         <tr>
 *             <td>rightRearTirePressure</td>
 *             <td>int</td>
 *             <td>右后轮胎压</td>
 *         </tr>
 *         <tr>
 *             <td>leftFrontWindowInformation</td>
 *             <td>String</td>
 *             <td>左前窗信息</td>
 *         </tr>
 *         <tr>
 *             <td>leftRearWindowInformation</td>
 *             <td>String</td>
 *             <td>左后窗信息</td>
 *         </tr>
 *         <tr>
 *             <td>rightFrontWindowInformation</td>
 *             <td>String</td>
 *             <td>右前窗信息</td>
 *         </tr>
 *         <tr>
 *             <td>rightRearWindowInformation</td>
 *             <td>String</td>
 *             <td>右后窗信息</td>
 *         </tr>
 *         <tr>
 *             <td>vehicleTemperature</td>
 *             <td>Short</td>
 *             <td>车内温度</td>
 *         </tr>
 *         <tr>
 *             <td>vehicleOuterTemperature</td>
 *             <td>Short</td>
 *             <td>车外温度</td>
 *         </tr>
 *         <tr>
 *             <td>leftFrontDoorInformation</td>
 *             <td>String</td>
 *             <td>左前门信息</td>
 *         </tr>
 *         <tr>
 *             <td>leftRearDoorInformation</td>
 *             <td>String</td>
 *             <td>左后门信息</td>
 *         </tr>
 *         <tr>
 *             <td>rightFrontDoorInformation</td>
 *             <td>String</td>
 *             <td>右前门信息</td>
 *         </tr>
 *         <tr>
 *             <td>rightRearDoorInformation</td>
 *             <td>String</td>
 *             <td>右后门信息</td>
 *         </tr>
 *         <tr>
 *             <td>engineDoorInformation</td>
 *             <td>String</td>
 *             <td>发动机门状态</td>
 *         </tr>
 *         <tr>
 *             <td>trunkDoorInformation</td>
 *             <td>String</td>
 *             <td>行李箱门状态</td>
 *         </tr>
 *         <tr>
 *             <td>engineCondition</td>
 *             <td>String</td>
 *             <td>发动机状态</td>
 *         </tr>
 *         <tr>
 *             <td>engineSpeed</td>
 *             <td>int</td>
 *             <td>发动机转速</td>
 *         </tr>
 *         <tr>
 *             <td>rapidAcceleration</td>
 *             <td>int</td>
 *             <td>急加速</td>
 *         </tr>
 *         <tr>
 *             <td>rapidDeceleration</td>
 *             <td>int</td>
 *             <td>急减速</td>
 *         </tr>
 *         <tr>
 *             <td>speeding</td>
 *             <td>int</td>
 *             <td>超速</td>
 *         </tr>
 *         <tr>
 *             <td>signalStrength</td>
 *             <td>Short</td>
 *             <td>信号强度</td>
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
public class RealTimeDataShow {

    private Long id;
    private String vin;
    private String imei;
    private int applicationId;
    private int messageId;
    private Date sendingTime;

    private float fuelOil;
    private float avgOilA;
    private float avgOilB;
    private int serviceIntervall;
    private float leftFrontTirePressure;
    private float leftRearTirePressure;
    private float rightFrontTirePressure;
    private float rightRearTirePressure;
    private String leftFrontWindowInformation;
    private String leftRearWindowInformation;
    private String rightFrontWindowInformation;
    private String rightRearWindowInformation;
    private Short vehicleTemperature;
    private Short vehicleOuterTemperature;
    private String leftFrontDoorInformation;
    private String leftRearDoorInformation;
    private String rightFrontDoorInformation;
    private String rightRearDoorInformation;

    private Short isLocation;
    private String northSouth;
    private String eastWest;
    private double latitude;
    private double longitude;
    private float speed;
    private int heading;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getVin() {
        return vin;
    }

    public void setVin(String vin) {
        this.vin = vin;
    }

    public String getImei() {
        return imei;
    }

    public void setImei(String imei) {
        this.imei = imei;
    }

    public int getApplicationId() {
        return applicationId;
    }

    public void setApplicationId(int applicationId) {
        this.applicationId = applicationId;
    }

    public int getMessageId() {
        return messageId;
    }

    public void setMessageId(int messageId) {
        this.messageId = messageId;
    }

    public Date getSendingTime() {
        return sendingTime;
    }

    public void setSendingTime(Date sendingTime) {
        this.sendingTime = sendingTime;
    }

    public float getFuelOil() {
        return fuelOil;
    }

    public void setFuelOil(float fuelOil) {
        this.fuelOil = fuelOil;
    }

    public float getAvgOilA() {
        return avgOilA;
    }

    public void setAvgOilA(float avgOilA) {
        this.avgOilA = avgOilA;
    }

    public float getAvgOilB() {
        return avgOilB;
    }

    public void setAvgOilB(float avgOilB) {
        this.avgOilB = avgOilB;
    }

    public int getServiceIntervall() {
        return serviceIntervall;
    }

    public void setServiceIntervall(int serviceIntervall) {
        this.serviceIntervall = serviceIntervall;
    }

    public float getLeftFrontTirePressure() {
        return leftFrontTirePressure;
    }

    public void setLeftFrontTirePressure(float leftFrontTirePressure) {
        this.leftFrontTirePressure = leftFrontTirePressure;
    }

    public float getLeftRearTirePressure() {
        return leftRearTirePressure;
    }

    public void setLeftRearTirePressure(float leftRearTirePressure) {
        this.leftRearTirePressure = leftRearTirePressure;
    }

    public float getRightFrontTirePressure() {
        return rightFrontTirePressure;
    }

    public void setRightFrontTirePressure(float rightFrontTirePressure) {
        this.rightFrontTirePressure = rightFrontTirePressure;
    }

    public float getRightRearTirePressure() {
        return rightRearTirePressure;
    }

    public void setRightRearTirePressure(float rightRearTirePressure) {
        this.rightRearTirePressure = rightRearTirePressure;
    }

    public String getLeftFrontWindowInformation() {
        return leftFrontWindowInformation;
    }

    public void setLeftFrontWindowInformation(String leftFrontWindowInformation) {
        this.leftFrontWindowInformation = leftFrontWindowInformation;
    }

    public String getLeftRearWindowInformation() {
        return leftRearWindowInformation;
    }

    public void setLeftRearWindowInformation(String leftRearWindowInformation) {
        this.leftRearWindowInformation = leftRearWindowInformation;
    }

    public String getRightFrontWindowInformation() {
        return rightFrontWindowInformation;
    }

    public void setRightFrontWindowInformation(String rightFrontWindowInformation) {
        this.rightFrontWindowInformation = rightFrontWindowInformation;
    }

    public String getRightRearWindowInformation() {
        return rightRearWindowInformation;
    }

    public void setRightRearWindowInformation(String rightRearWindowInformation) {
        this.rightRearWindowInformation = rightRearWindowInformation;
    }

    public Short getVehicleTemperature() {
        return vehicleTemperature;
    }

    public void setVehicleTemperature(Short vehicleTemperature) {
        this.vehicleTemperature = vehicleTemperature;
    }

    public Short getVehicleOuterTemperature() {
        return vehicleOuterTemperature;
    }

    public void setVehicleOuterTemperature(Short vehicleOuterTemperature) {
        this.vehicleOuterTemperature = vehicleOuterTemperature;
    }

    public String getLeftFrontDoorInformation() {
        return leftFrontDoorInformation;
    }

    public void setLeftFrontDoorInformation(String leftFrontDoorInformation) {
        this.leftFrontDoorInformation = leftFrontDoorInformation;
    }

    public String getLeftRearDoorInformation() {
        return leftRearDoorInformation;
    }

    public void setLeftRearDoorInformation(String leftRearDoorInformation) {
        this.leftRearDoorInformation = leftRearDoorInformation;
    }

    public String getRightFrontDoorInformation() {
        return rightFrontDoorInformation;
    }

    public void setRightFrontDoorInformation(String rightFrontDoorInformation) {
        this.rightFrontDoorInformation = rightFrontDoorInformation;
    }

    public String getRightRearDoorInformation() {
        return rightRearDoorInformation;
    }

    public void setRightRearDoorInformation(String rightRearDoorInformation) {
        this.rightRearDoorInformation = rightRearDoorInformation;
    }

    public Short getIsLocation() {
        return isLocation;
    }

    public void setIsLocation(Short isLocation) {
        this.isLocation = isLocation;
    }

    public String getNorthSouth() {
        return northSouth;
    }

    public void setNorthSouth(String northSouth) {
        this.northSouth = northSouth;
    }

    public String getEastWest() {
        return eastWest;
    }

    public void setEastWest(String eastWest) {
        this.eastWest = eastWest;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public float getSpeed() {
        return speed;
    }

    public void setSpeed(float speed) {
        this.speed = speed;
    }

    public int getHeading() {
        return heading;
    }

    public void setHeading(int heading) {
        this.heading = heading;
    }
}
