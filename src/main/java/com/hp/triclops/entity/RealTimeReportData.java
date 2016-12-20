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
    private int drivingTime;
    private Short oilLife;
    private float fuelOil;
    private float avgOilA;
    private float avgOilB;
    private int drivingRange;
    private int mileageRange;
    private int serviceIntervall;
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
    private String engineCoverState;
    private String trunkLidState;
    private String skylightState;
    private String parkingState;
    private int tripId;
    private float voltage;
    private int averageSpeedA;
    private int averageSpeedB;
    private float tripA;
    private float tripB;

    private String seatbeltFl;
    private String seatbeltFr;
    private String seatbeltRl;
    private String seatbeltRm;
    private String seatbeltRr;

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
    public float getVehicleTemperature() {
        return vehicleTemperature;
    }

    public void setVehicleTemperature(float vehicleTemperature) {
        this.vehicleTemperature = vehicleTemperature;
    }

    @Basic
    @Column(name = "vehicle_outer_temperature", nullable = false, insertable = true, updatable = true)
    public float getVehicleOuterTemperature() {
        return vehicleOuterTemperature;
    }

    public void setVehicleOuterTemperature(float vehicleOuterTemperature) {
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

    @Basic
    @Column(name = "driving_time", nullable = true, insertable = true, updatable = true)
    public int getDrivingTime() {
        return drivingTime;
    }

    public void setDrivingTime(int drivingTime) {
        this.drivingTime = drivingTime;
    }

    @Basic
    @Column(name = "oil_life", nullable = true, insertable = true, updatable = true)
    public Short getOilLife() {
        return oilLife;
    }

    public void setOilLife(Short oilLife) {
        this.oilLife = oilLife;
    }

    @Basic
    @Column(name = "driving_range", nullable = true, insertable = true, updatable = true)
    public int getDrivingRange() {
        return drivingRange;
    }

    public void setDrivingRange(int drivingRange) {
        this.drivingRange = drivingRange;
    }

    @Basic
    @Column(name = "mileageRange", nullable = true, insertable = true, updatable = true)
    public int getMileageRange() {
        return mileageRange;
    }

    public void setMileageRange(int mileageRange) {
        this.mileageRange = mileageRange;
    }

    @Basic
    @Column(name = "engine_cover_state", nullable = true, insertable = true, updatable = true, length = 1)
    public String getEngineCoverState() {
        return engineCoverState;
    }

    public void setEngineCoverState(String engineCoverState) {
        this.engineCoverState = engineCoverState;
    }

    @Basic
    @Column(name = "trunk_lid_state", nullable = true, insertable = true, updatable = true, length = 1)
    public String getTrunkLidState() {
        return trunkLidState;
    }

    public void setTrunkLidState(String trunkLidState) {
        this.trunkLidState = trunkLidState;
    }

    @Basic
    @Column(name = "skylight_state", nullable = true, insertable = true, updatable = true, length = 1)
    public String getSkylightState() {
        return skylightState;
    }

    public void setSkylightState(String skylightState) {
        this.skylightState = skylightState;
    }

    @Basic
    @Column(name = "parking_state", nullable = true, insertable = true, updatable = true, length = 1)
    public String getParkingState() {
        return parkingState;
    }

    public void setParkingState(String parkingState) {
        this.parkingState = parkingState;
    }

    public float getVoltage() {
        return voltage;
    }

    public void setVoltage(float voltage) {
        this.voltage = voltage;
    }

    @Basic
    @Column(name = "average_speed_a", nullable = false, insertable = true, updatable = true)
    public int getAverageSpeedA() {
        return averageSpeedA;
    }

    public void setAverageSpeedA(int averageSpeedA) {
        this.averageSpeedA = averageSpeedA;
    }
    @Basic
    @Column(name = "average_speed_b", nullable = false, insertable = true, updatable = true)
    public int getAverageSpeedB() {
        return averageSpeedB;
    }

    public void setAverageSpeedB(int averageSpeedB) {
        this.averageSpeedB = averageSpeedB;
    }

    @Basic
    @Column(name = "trip_a", nullable = false, insertable = true, updatable = true)
    public float getTripA() {
        return tripA;
    }

    public void setTripA(float tripA) {
        this.tripA = tripA;
    }

    @Basic
    @Column(name = "trip_b", nullable = false, insertable = true, updatable = true)
    public float getTripB() {
        return tripB;
    }

    public void setTripB(float tripB) {
        this.tripB = tripB;
    }

    @Basic
    @Column(name = "seatbelt_fl", nullable = false, insertable = true, updatable = true, length = 1)
    public String getSeatbeltFl() {
        return seatbeltFl;
    }

    public void setSeatbeltFl(String seatbeltFl) {
        this.seatbeltFl = seatbeltFl;
    }

    @Basic
    @Column(name = "seatbelt_fr", nullable = false, insertable = true, updatable = true, length = 1)
    public String getSeatbeltFr() {
        return seatbeltFr;
    }

    public void setSeatbeltFr(String seatbeltFr) {
        this.seatbeltFr = seatbeltFr;
    }

    @Basic
    @Column(name = "seatbelt_rl", nullable = false, insertable = true, updatable = true, length = 1)
    public String getSeatbeltRl() {
        return seatbeltRl;
    }

    public void setSeatbeltRl(String seatbeltRl) {
        this.seatbeltRl = seatbeltRl;
    }

    @Basic
    @Column(name = "seatbelt_rm", nullable = false, insertable = true, updatable = true, length = 1)
    public String getSeatbeltRm() {
        return seatbeltRm;
    }

    public void setSeatbeltRm(String seatbeltRm) {
        this.seatbeltRm = seatbeltRm;
    }

    @Basic
    @Column(name = "seatbelt_rr", nullable = false, insertable = true, updatable = true, length = 1)
    public String getSeatbeltRr() {
        return seatbeltRr;
    }

    public void setSeatbeltRr(String seatbeltRr) {
        this.seatbeltRr = seatbeltRr;
    }
}
