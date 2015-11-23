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
    private int tripId;


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
    public float getFuelOil() {
        return fuelOil;
    }

    public void setFuelOil(float fuelOil) {
        this.fuelOil = fuelOil;
    }
    @Basic
    @Column(name = "avg_oil_a", nullable = false, insertable = true, updatable = true)
    public float getAvgOilA() {
        return avgOilA;
    }

    public void setAvgOilA(float avgOilA) {
        this.avgOilA = avgOilA;
    }

    @Basic
    @Column(name = "avg_oil_b", nullable = false, insertable = true, updatable = true)
    public float getAvgOilB() {
        return avgOilB;
    }

    public void setAvgOilB(float avgOilB) {
        this.avgOilB = avgOilB;
    }

    @Basic
    @Column(name = "service_intervall", nullable = false, insertable = true, updatable = true)
    public int getServiceIntervall() {
        return serviceIntervall;
    }

    public void setServiceIntervall(int serviceIntervall) {
        this.serviceIntervall = serviceIntervall;
    }

    @Basic
    @Column(name = "left_front_tire_pressure", nullable = false, insertable = true, updatable = true)
    public float getLeftFrontTirePressure() {
        return leftFrontTirePressure;
    }

    public void setLeftFrontTirePressure(float leftFrontTirePressure) {
        this.leftFrontTirePressure = leftFrontTirePressure;
    }

    @Basic
    @Column(name = "left_rear_tire_pressure", nullable = false, insertable = true, updatable = true)
    public float getLeftRearTirePressure() {
        return leftRearTirePressure;
    }

    public void setLeftRearTirePressure(float leftRearTirePressure) {
        this.leftRearTirePressure = leftRearTirePressure;
    }

    @Basic
    @Column(name = "right_front_tire_pressure", nullable = false, insertable = true, updatable = true)
    public float getRightFrontTirePressure() {
        return rightFrontTirePressure;
    }

    public void setRightFrontTirePressure(float rightFrontTirePressure) {
        this.rightFrontTirePressure = rightFrontTirePressure;
    }

    @Basic
    @Column(name = "right_rear_tire_pressure", nullable = false, insertable = true, updatable = true)
    public float getRightRearTirePressure() {
        return rightRearTirePressure;
    }

    public void setRightRearTirePressure(float rightRearTirePressure) {
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
    @Column(name = "trip_id", nullable = false, insertable = true, updatable = true)
    public int getTripId() {
        return tripId;
    }

    public void setTripId(int tripId) {
        this.tripId = tripId;
    }
}
