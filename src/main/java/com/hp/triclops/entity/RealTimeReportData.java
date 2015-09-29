package com.hp.triclops.entity;

import javax.persistence.Entity;
import javax.persistence.Table;

/**
 * Created by luj on 2015/9/28.
 */

@Entity
@Table(name = "t_data_realtimereport")
public class RealTimeReportData {

    private Long id;
    private String vin;
    private String serialNumber;
    private String imei;
    private int applicationId;
    private int messageId;
    private int sendingTime;

    private Short isLocation;
    private Long latitude;
    private Long longitude;
    private int speed;
    private int heading;
    private Short fuelOil;
    private int avgOil;
    private Short oilLife;
    private int driveRange;
    private int leftFrontTirePressure;
    private int leftRearTirePressure;
    private int rightFrontTirePressure;
    private int rightRearTirePressure;
    private Short windowInformation;
    private Short vehicleTemperature;
    private Short vehicleOuterTemperature;
    private Short doorInformation;
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


}
