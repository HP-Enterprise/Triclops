package com.hp.triclops.entity;

import javax.persistence.*;
import java.util.Date;

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
    private Date sendingTime;

    private Short fuelOil;
    private String avgOil;
    private String oilLife;
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
    private String singleBatteryVoltage;
    private Short maximumVoltagePowerBatteryPack;
    private String maximumBatteryVoltage;
    private String batteryMonomerMinimumVoltage;
    private String engineCondition;
    //  0:engine stop 1:engine start 2:idle speed 3:part load  4:trailling throttle  5:full load  6:Fuel Cut Off  7:undefined
    private String engineSpeed;
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
    public Date getSendingTime() {
        return sendingTime;
    }

    public void setSendingTime(Date sendingTime) {
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
    @Column(name = "avg_oil", nullable = false, insertable = true, updatable = true, length = 10)
    public String getAvgOil() {
        return avgOil;
    }

    public void setAvgOil(String avgOil) {
        this.avgOil = avgOil;
    }

    @Basic
    @Column(name = "oil_life", nullable = false, insertable = true, updatable = true, length = 10)
    public String getOilLife() {
        return oilLife;
    }

    public void setOilLife(String oilLife) {
        this.oilLife = oilLife;
    }

    @Basic
    @Column(name = "drive_range", nullable = false, insertable = true, updatable = true)
    public int getDriveRange() {
        return driveRange;
    }

    public void setDriveRange(int driveRange) {
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
    @Column(name = "left_front_window_information", nullable = false, insertable = true, updatable = true, length = 1)
    public String getLeftFrontWindowInformation() {
        return leftFrontWindowInformation;
    }

    public void setLeftFrontWindowInformation(String leftFrontWindowInformation) {
        this.leftFrontWindowInformation = leftFrontWindowInformation;
    }

    @Basic
    @Column(name = "left_rear_window_information", nullable = false, insertable = true, updatable = true, length = 1)
    public String getLeftRearWindowInformation() {
        return leftRearWindowInformation;
    }

    public void setLeftRearWindowInformation(String leftRearWindowInformation) {
        this.leftRearWindowInformation = leftRearWindowInformation;
    }

    @Basic
    @Column(name = "right_front_window_information", nullable = false, insertable = true, updatable = true, length = 1)
    public String getRightFrontWindowInformation() {
        return rightFrontWindowInformation;
    }

    public void setRightFrontWindowInformation(String rightFrontWindowInformation) {
        this.rightFrontWindowInformation = rightFrontWindowInformation;
    }

    @Basic
    @Column(name = "right_rear_window_information", nullable = false, insertable = true, updatable = true, length = 1)
    public String getRightRearWindowInformation() {
        return rightRearWindowInformation;
    }

    public void setRightRearWindowInformation(String rightRearWindowInformation) {
        this.rightRearWindowInformation = rightRearWindowInformation;
    }

    @Basic
    @Column(name = "left_front_door_information", nullable = false, insertable = true, updatable = true, length = 1)
    public String getLeftFrontDoorInformation() {
        return leftFrontDoorInformation;
    }

    public void setLeftFrontDoorInformation(String leftFrontDoorInformation) {
        this.leftFrontDoorInformation = leftFrontDoorInformation;
    }

    @Basic
    @Column(name = "left_rear_door_information", nullable = false, insertable = true, updatable = true, length = 1)
    public String getLeftRearDoorInformation() {
        return leftRearDoorInformation;
    }

    public void setLeftRearDoorInformation(String leftRearDoorInformation) {
        this.leftRearDoorInformation = leftRearDoorInformation;
    }

    @Basic
    @Column(name = "right_front_door_information", nullable = false, insertable = true, updatable = true, length = 1)
    public String getRightFrontDoorInformation() {
        return rightFrontDoorInformation;
    }

    public void setRightFrontDoorInformation(String rightFrontDoorInformation) {
        this.rightFrontDoorInformation = rightFrontDoorInformation;
    }

    @Basic
    @Column(name = "right_rear_door_information", nullable = false, insertable = true, updatable = true, length = 1)
    public String getRightRearDoorInformation() {
        return rightRearDoorInformation;
    }

    public void setRightRearDoorInformation(String rightRearDoorInformation) {
        this.rightRearDoorInformation = rightRearDoorInformation;
    }

    @Basic
    @Column(name = "engine_door_information", nullable = false, insertable = true, updatable = true, length = 1)
    public String getEngineDoorInformation() {
        return engineDoorInformation;
    }

    public void setEngineDoorInformation(String engineDoorInformation) {
        this.engineDoorInformation = engineDoorInformation;
    }

    @Basic
    @Column(name = "trunk_door_information", nullable = false, insertable = true, updatable = true, length = 1)
    public String getTrunkDoorInformation() {
        return trunkDoorInformation;
    }

    public void setTrunkDoorInformation(String trunkDoorInformation) {
        this.trunkDoorInformation = trunkDoorInformation;
    }


    @Basic
    @Column(name = "single_battery_voltage",nullable = false, insertable = true, updatable = true, length = 10)
    public String getSingleBatteryVoltage() {
        return singleBatteryVoltage;
    }

    public void setSingleBatteryVoltage(String singleBatteryVoltage) {
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
    @Column(name = "maximum_battery_voltage",nullable = false, insertable = true, updatable = true, length = 10)
    public String getMaximumBatteryVoltage() {
        return maximumBatteryVoltage;
    }

    public void setMaximumBatteryVoltage(String maximumBatteryVoltage) {
        this.maximumBatteryVoltage = maximumBatteryVoltage;
    }

    @Basic
    @Column(name = "battery_monomer_minimum_voltage", nullable = false, insertable = true, updatable = true, length = 10)
    public String getBatteryMonomerMinimumVoltage() {
        return batteryMonomerMinimumVoltage;
    }

    public void setBatteryMonomerMinimumVoltage(String batteryMonomerMinimumVoltage) {
        this.batteryMonomerMinimumVoltage = batteryMonomerMinimumVoltage;
    }

    @Basic
    @Column(name = "engine_condition", nullable = false, insertable = true, updatable = true, length = 1)
    public String getEngineCondition() {
        return engineCondition;
    }

    public void setEngineCondition(String engineCondition) {
        this.engineCondition = engineCondition;
    }

    @Basic
    @Column(name = "engine_speed", nullable = false, insertable = true, updatable = true, length = 10)
    public String getEngineSpeed() {
        return engineSpeed;
    }

    public void setEngineSpeed(String engineSpeed) {
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
