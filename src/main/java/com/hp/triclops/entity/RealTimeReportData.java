package com.hp.triclops.entity;

import javax.persistence.*;

/**
 * Created by luj on 2015/9/28.
 */

@Entity
@Table(name = "t_data_realtime_report")
public class RealTimeReportData {

    private Long id;
    private String vin;
    private String imei;
    private int applicationId;
    private int messageId;
    private Long sendingTime;

    private Short fuelOil;
    private int avgOil;
    private Short oilLife;
    private String driveRange;
    private int leftFrontTirePressure;
    private int leftRearTirePressure;
    private int rightFrontTirePressure;
    private int rightRearTirePressure;
    private String windowInformation;
    private Short vehicleTemperature;
    private Short vehicleOuterTemperature;
    private String doorInformation;
    private int singleBatteryVoltage;
    private Short maximumVoltagePowerBatteryPack;
    private int maximumBatteryVoltage;
    private int batteryMonomerMinimumVoltage;
    private Short engineCondition;
    private int engineSpeed;
    private int rapidAcceleration;
    private int rapidDeceleration;
    private int speeding;
    private Short signalStrength;

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
    public Long getSendingTime() {
        return sendingTime;
    }

    public void setSendingTime(Long sendingTime) {
        this.sendingTime = sendingTime;
    }


    @Basic
    @Column(name = "fuel_oil", nullable = false, insertable = true, updatable = true)
    public Short getFuelOil() {
        return fuelOil;
    }

    public void setFuelOil(Short fuelOil) {
        this.fuelOil = fuelOil;
    }

    @Basic
    @Column(name = "avg_oil", nullable = false, insertable = true, updatable = true)
    public int getAvgOil() {
        return avgOil;
    }

    public void setAvgOil(int avgOil) {
        this.avgOil = avgOil;
    }

    @Basic
    @Column(name = "oil_life", nullable = false, insertable = true, updatable = true)
    public Short getOilLife() {
        return oilLife;
    }

    public void setOilLife(Short oilLife) {
        this.oilLife = oilLife;
    }

    @Basic
    @Column(name = "drive_range", nullable = false, insertable = true, updatable = true, length = 100)
    public String getDriveRange() {
        return driveRange;
    }

    public void setDriveRange(String driveRange) {
        this.driveRange = driveRange;
    }

    @Basic
    @Column(name = "left_front_tire_pressure", nullable = false, insertable = true, updatable = true)
    public int getLeftFrontTirePressure() {
        return leftFrontTirePressure;
    }

    public void setLeftFrontTirePressure(int leftFrontTirePressure) {
        this.leftFrontTirePressure = leftFrontTirePressure;
    }

    @Basic
    @Column(name = "left_rear_tire_pressure", nullable = false, insertable = true, updatable = true)
    public int getLeftRearTirePressure() {
        return leftRearTirePressure;
    }

    public void setLeftRearTirePressure(int leftRearTirePressure) {
        this.leftRearTirePressure = leftRearTirePressure;
    }

    @Basic
    @Column(name = "right_front_tire_pressure", nullable = false, insertable = true, updatable = true)
    public int getRightFrontTirePressure() {
        return rightFrontTirePressure;
    }

    public void setRightFrontTirePressure(int rightFrontTirePressure) {
        this.rightFrontTirePressure = rightFrontTirePressure;
    }

    @Basic
    @Column(name = "right_rear_tire_pressure", nullable = false, insertable = true, updatable = true)
    public int getRightRearTirePressure() {
        return rightRearTirePressure;
    }

    public void setRightRearTirePressure(int rightRearTirePressure) {
        this.rightRearTirePressure = rightRearTirePressure;
    }

    @Basic
    @Column(name = "window_information", nullable = false, insertable = true, updatable = true, length = 8)
    public String getWindowInformation() {
        return windowInformation;
    }

    public void setWindowInformation(String windowInformation) {
        this.windowInformation = windowInformation;
    }

    @Basic
    @Column(name = "vehicle_temperature", nullable = false, insertable = true, updatable = true)
    public Short getVehicleTemperature() {
        return vehicleTemperature;
    }

    public void setVehicleTemperature(Short vehicleTemperature) {
        this.vehicleTemperature = vehicleTemperature;
    }

    @Basic
    @Column(name = "vehicle_outer_temperature", nullable = false, insertable = true, updatable = true)
    public Short getVehicleOuterTemperature() {
        return vehicleOuterTemperature;
    }

    public void setVehicleOuterTemperature(Short vehicleOuterTemperature) {
        this.vehicleOuterTemperature = vehicleOuterTemperature;
    }

    @Basic
    @Column(name = "door_information", nullable = false, insertable = true, updatable = true, length = 8)
    public String getDoorInformation() {
        return doorInformation;
    }

    public void setDoorInformation(String doorInformation) {
        this.doorInformation = doorInformation;
    }

    @Basic
    @Column(name = "single_battery_voltage", nullable = false, insertable = true, updatable = true)
    public int getSingleBatteryVoltage() {
        return singleBatteryVoltage;
    }

    public void setSingleBatteryVoltage(int singleBatteryVoltage) {
        this.singleBatteryVoltage = singleBatteryVoltage;
    }

    @Basic
    @Column(name = "maximum_voltage_power_pattery_pack", nullable = false, insertable = true, updatable = true)
    public Short getMaximumVoltagePowerBatteryPack() {
        return maximumVoltagePowerBatteryPack;
    }

    public void setMaximumVoltagePowerBatteryPack(Short maximumVoltagePowerBatteryPack) {
        this.maximumVoltagePowerBatteryPack = maximumVoltagePowerBatteryPack;
    }

    @Basic
    @Column(name = "maximum_battery_voltage", nullable = false, insertable = true, updatable = true)
    public int getMaximumBatteryVoltage() {
        return maximumBatteryVoltage;
    }

    public void setMaximumBatteryVoltage(int maximumBatteryVoltage) {
        this.maximumBatteryVoltage = maximumBatteryVoltage;
    }

    @Basic
    @Column(name = "battery_monomer_minimum_voltage", nullable = false, insertable = true, updatable = true)
    public int getBatteryMonomerMinimumVoltage() {
        return batteryMonomerMinimumVoltage;
    }

    public void setBatteryMonomerMinimumVoltage(int batteryMonomerMinimumVoltage) {
        this.batteryMonomerMinimumVoltage = batteryMonomerMinimumVoltage;
    }

    @Basic
    @Column(name = "engine_condition", nullable = false, insertable = true, updatable = true)
    public Short getEngineCondition() {
        return engineCondition;
    }

    public void setEngineCondition(Short engineCondition) {
        this.engineCondition = engineCondition;
    }

    @Basic
    @Column(name = "engine_speed", nullable = false, insertable = true, updatable = true)
    public int getEngineSpeed() {
        return engineSpeed;
    }

    public void setEngineSpeed(int engineSpeed) {
        this.engineSpeed = engineSpeed;
    }

    @Basic
    @Column(name = "rapid_acceleration", nullable = false, insertable = true, updatable = true)
    public int getRapidAcceleration() {
        return rapidAcceleration;
    }

    public void setRapidAcceleration(int rapidAcceleration) {
        this.rapidAcceleration = rapidAcceleration;
    }

    @Basic
    @Column(name = "rapid_deceleration", nullable = false, insertable = true, updatable = true)
    public int getRapidDeceleration() {
        return rapidDeceleration;
    }

    public void setRapidDeceleration(int rapidDeceleration) {
        this.rapidDeceleration = rapidDeceleration;
    }

    @Basic
    @Column(name = "speeding", nullable = false, insertable = true, updatable = true)
    public int getSpeeding() {
        return speeding;
    }

    public void setSpeeding(int speeding) {
        this.speeding = speeding;
    }

    @Basic
    @Column(name = "signal_strength", nullable = false, insertable = true, updatable = true)
    public Short getSignalStrength() {
        return signalStrength;
    }

    public void setSignalStrength(Short signalStrength) {
        this.signalStrength = signalStrength;
    }



}
