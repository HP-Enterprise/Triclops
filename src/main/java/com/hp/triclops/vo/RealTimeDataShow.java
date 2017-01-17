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
 *          <tr>
 *             <td>oilLife</td>
 *             <td>int</td>
 *             <td>机油寿命</td>
 *         </tr>
 *          <tr>
 *             <td>parkingState</td>
 *             <td>int</td>
 *             <td>驻车状态</td>
 *         </tr>
 *          <tr>
 *             <td>heading</td>
 *             <td>int</td>
 *             <td>方向角</td>
 *         </tr>
 *          <tr>
 *             <td>mileageRange</td>
 *             <td>int</td>
 *             <td>续航里程</td>
 *         </tr>
 *          <tr>
 *             <td>drivingRange</td>
 *             <td>int</td>
 *             <td>行驶里程</td>
 *         </tr>
 *          <tr>
 *             <td>drivingTime</td>
 *             <td>int</td>
 *             <td>行驶时间</td>
 *         </tr>
 *          <tr>
 *             <td>lfok</td>
 *             <td>int</td>
 *             <td>左前轮正常标识</td>
 *         </tr>
 *          <tr>
 *             <td>lrok</td>
 *             <td>int</td>
 *             <td>左后轮正常标识</td>
 *         </tr>
 *          <tr>
 *             <td>rfok</td>
 *             <td>int</td>
 *             <td>右前轮正常标识</td>
 *         </tr>
 *          <tr>
 *             <td>rrok</td>
 *             <td>int</td>
 *             <td>右后轮正常标识</td>
 *         </tr>
 *          <tr>
 *             <td>fmax</td>
 *             <td>int</td>
 *             <td>前轮最大值</td>
 *         </tr>
 *          <tr>
 *             <td>fmin</td>
 *             <td>int</td>
 *             <td>前轮最小值</td>
 *         </tr>
 *          <tr>
 *             <td>rmax</td>
 *             <td>int</td>
 *             <td>后轮最大值</td>
 *         </tr>
 *          <tr>
 *             <td>rmin</td>
 *             <td>int</td>
 *             <td>后轮最小值</td>
 *         </tr>
 *          <tr>
 *             <td>skylightState</td>
 *             <td>int</td>
 *             <td>天窗信息</td>
 *         </tr>
 *          <tr>
 *             <td>engineDoorInformation</td>
 *             <td>int</td>
 *             <td>发动机门状态</td>
 *         </tr>
 *          <tr>
 *             <td>trunkDoorInformation</td>
 *             <td>int</td>
 *             <td>行李箱门状态</td>
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

    //private float fuelOil;
    private int fuelOil;//剩余燃油量改为整数
    private float avgOilA;
    private float avgOilB;
    private float leftFrontTirePressure;
    private float leftRearTirePressure;
    private float rightFrontTirePressure;
    private float rightRearTirePressure;
    private String leftFrontWindowInformation;
    private String leftRearWindowInformation;
    private String rightFrontWindowInformation;
    private String rightRearWindowInformation;
    private float vehicleTemperature;
    private float vehicleOuterTemperature;
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

    private int oilLife;
    private int parkingState;
    private int mileageRange;
    private int drivingRange;
    private int drivingTime;
    private int lfok;
    private int lrok;
    private int rfok;
    private int rrok;
    private int fmax;
    private int fmin;
    private int rmax;
    private int rmin;
    private int skylightState;
    private int engineDoorInformation;
    private int trunkDoorInformation;
    private float batteryVoltage;
    private int averageSpeedA;
    private int averageSpeedB;
    private String mtGearPostion;

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

    public int getFuelOil() {
        return fuelOil;
    }

    public void setFuelOil(int fuelOil) {
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

    public float getVehicleTemperature() {
        return vehicleTemperature;
    }

    public void setVehicleTemperature(float vehicleTemperature) {
        this.vehicleTemperature = vehicleTemperature;
    }

    public float getVehicleOuterTemperature() {
        return vehicleOuterTemperature;
    }

    public void setVehicleOuterTemperature(float vehicleOuterTemperature) {
        this.vehicleOuterTemperature = vehicleOuterTemperature;
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

    public int getOilLife() {
        return oilLife;
    }

    public void setOilLife(int oilLife) {
        this.oilLife = oilLife;
    }

    public int getParkingState() {
        return parkingState;
    }

    public void setParkingState(int parkingState) {
        this.parkingState = parkingState;
    }

    public int getMileageRange() {
        return mileageRange;
    }

    public void setMileageRange(int mileageRange) {
        this.mileageRange = mileageRange;
    }

    public int getDrivingRange() {
        return drivingRange;
    }

    public void setDrivingRange(int drivingRange) {
        this.drivingRange = drivingRange;
    }

    public int getDrivingTime() {
        return drivingTime;
    }

    public void setDrivingTime(int drivingTime) {
        this.drivingTime = drivingTime;
    }

    public int getLfok() {
        return lfok;
    }

    public void setLfok(int lfok) {
        this.lfok = lfok;
    }

    public int getLrok() {
        return lrok;
    }

    public void setLrok(int lrok) {
        this.lrok = lrok;
    }

    public int getRfok() {
        return rfok;
    }

    public void setRfok(int rfok) {
        this.rfok = rfok;
    }

    public int getRrok() {
        return rrok;
    }

    public void setRrok(int rrok) {
        this.rrok = rrok;
    }

    public int getFmax() {
        return fmax;
    }

    public void setFmax(int fmax) {
        this.fmax = fmax;
    }

    public int getFmin() {
        return fmin;
    }

    public void setFmin(int fmin) {
        this.fmin = fmin;
    }

    public int getRmax() {
        return rmax;
    }

    public void setRmax(int rmax) {
        this.rmax = rmax;
    }

    public int getRmin() {
        return rmin;
    }

    public void setRmin(int rmin) {
        this.rmin = rmin;
    }

    public int getSkylightState() {
        return skylightState;
    }

    public void setSkylightState(int skylightState) {
        this.skylightState = skylightState;
    }

    public int getEngineDoorInformation() {
        return engineDoorInformation;
    }

    public void setEngineDoorInformation(int engineDoorInformation) {
        this.engineDoorInformation = engineDoorInformation;
    }

    public int getTrunkDoorInformation() {
        return trunkDoorInformation;
    }

    public void setTrunkDoorInformation(int trunkDoorInformation) {
        this.trunkDoorInformation = trunkDoorInformation;
    }

    public float getBatteryVoltage() {
        return batteryVoltage;
    }

    public void setBatteryVoltage(float batteryVoltage) {
        this.batteryVoltage = batteryVoltage;
    }

    public int getAverageSpeedA() {
        return averageSpeedA;
    }

    public void setAverageSpeedA(int averageSpeedA) {
        this.averageSpeedA = averageSpeedA;
    }

    public int getAverageSpeedB() {
        return averageSpeedB;
    }

    public void setAverageSpeedB(int averageSpeedB) {
        this.averageSpeedB = averageSpeedB;
    }

    public String getMtGearPostion() {
        return mtGearPostion;
    }

    public void setMtGearPostion(String mtGearPostion) {
        this.mtGearPostion = mtGearPostion;
    }
}
