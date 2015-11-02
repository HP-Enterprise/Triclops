package com.hp.triclops.vo;

import java.util.Date;

/**
 * Created by luj on 2015/11/2.
 */
public class RealTimeDataShow {

    private Long id;
    private String vin;
    private String imei;
    private int applicationId;
    private int messageId;
    private Date sendingTime;

    private Short fuelOil;
    private float avgOil;
    private Short oilLife;
    private int driveRange;
    private int leftFrontTirePressure;
    private int leftRearTirePressure;
    private int rightFrontTirePressure;
    private int rightRearTirePressure;
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
    private String engineDoorInformation;
    private String trunkDoorInformation;
    private String engineCondition;
    //  0:engine stop 1:engine start 2:idle speed 3:part load  4:trailling throttle  5:full load  6:Fuel Cut Off  7:undefined
    private int engineSpeed;
    private int rapidAcceleration;
    private int rapidDeceleration;
    private int speeding;
    private Short signalStrength;

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

    public Short getFuelOil() {
        return fuelOil;
    }

    public void setFuelOil(Short fuelOil) {
        this.fuelOil = fuelOil;
    }

    public float getAvgOil() {
        return avgOil;
    }

    public void setAvgOil(float avgOil) {
        this.avgOil = avgOil;
    }

    public Short getOilLife() {
        return oilLife;
    }

    public void setOilLife(Short oilLife) {
        this.oilLife = oilLife;
    }

    public int getDriveRange() {
        return driveRange;
    }

    public void setDriveRange(int driveRange) {
        this.driveRange = driveRange;
    }

    public int getLeftFrontTirePressure() {
        return leftFrontTirePressure;
    }

    public void setLeftFrontTirePressure(int leftFrontTirePressure) {
        this.leftFrontTirePressure = leftFrontTirePressure;
    }

    public int getLeftRearTirePressure() {
        return leftRearTirePressure;
    }

    public void setLeftRearTirePressure(int leftRearTirePressure) {
        this.leftRearTirePressure = leftRearTirePressure;
    }

    public int getRightFrontTirePressure() {
        return rightFrontTirePressure;
    }

    public void setRightFrontTirePressure(int rightFrontTirePressure) {
        this.rightFrontTirePressure = rightFrontTirePressure;
    }

    public int getRightRearTirePressure() {
        return rightRearTirePressure;
    }

    public void setRightRearTirePressure(int rightRearTirePressure) {
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

    public String getEngineDoorInformation() {
        return engineDoorInformation;
    }

    public void setEngineDoorInformation(String engineDoorInformation) {
        this.engineDoorInformation = engineDoorInformation;
    }

    public String getTrunkDoorInformation() {
        return trunkDoorInformation;
    }

    public void setTrunkDoorInformation(String trunkDoorInformation) {
        this.trunkDoorInformation = trunkDoorInformation;
    }

    public String getEngineCondition() {
        return engineCondition;
    }

    public void setEngineCondition(String engineCondition) {
        this.engineCondition = engineCondition;
    }

    public int getEngineSpeed() {
        return engineSpeed;
    }

    public void setEngineSpeed(int engineSpeed) {
        this.engineSpeed = engineSpeed;
    }

    public int getRapidAcceleration() {
        return rapidAcceleration;
    }

    public void setRapidAcceleration(int rapidAcceleration) {
        this.rapidAcceleration = rapidAcceleration;
    }

    public int getRapidDeceleration() {
        return rapidDeceleration;
    }

    public void setRapidDeceleration(int rapidDeceleration) {
        this.rapidDeceleration = rapidDeceleration;
    }

    public int getSpeeding() {
        return speeding;
    }

    public void setSpeeding(int speeding) {
        this.speeding = speeding;
    }

    public Short getSignalStrength() {
        return signalStrength;
    }

    public void setSignalStrength(Short signalStrength) {
        this.signalStrength = signalStrength;
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
