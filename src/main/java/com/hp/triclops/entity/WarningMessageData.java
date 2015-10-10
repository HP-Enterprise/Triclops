package com.hp.triclops.entity;

import javax.persistence.*;
import java.util.Date;

/**
 * Created by luj on 2015/10/3.
 */
@Entity
@Table(name = "t_data_warning_message")
public class WarningMessageData {
    private Long id;
    private String vin;
    private String imei;
    private int applicationId;
    private int messageId;
    private Date sendingTime;

    private Short isLocation;
    private String northSouth;
    private String eastWest;
    private String latitude;
    private String longitude;
    private float speed;
    private int heading;

    /*
    bcm1按bit
    BIT0：车辆电瓶过压
    0:故障发生 1:故障消除
    BIT1：车辆电瓶欠压
    0:故障发生 1:故障消除
    BIT2：多媒体异常
    0:故障发生 1:故障消除
    BIT3：冷冻液不足
    0:故障发生 1:故障消除
    BIT4：车灯系统故障
    0:故障发生 1:故障消除
    BIT5~BIT7：预留
     */
    private String batteryVoltageTooHigh;
    private String batteryVoltageTooLow;
    private String mediaAbnormal;
    private String frozenLiquidShortage;
    private String lampFailure;

    /*
    ems按bit
    BIT0：发动机异常
    0:故障发生 1:故障消除
    BIT1：水温过高
    0:故障发生 1:故障消除
    BIT2~BIT7：预留
     */
    private String engineAbnormal;
    private String waterTemperatureTooHigh;

    /*
    tcu按bit
    BIT0：危险传动系统故障
    0:故障发生 1:故障消除
    BIT1：警告传动系统故障
    0:故障发生 1:故障消除
    BIT2：传动系统过热
    0:故障发生 1:故障消除
    BIT3~BIT7：预留
     */
    private String dangerousDrivingSystemFault;
    private String warningDrivingSystemFault;
    private String drivingSystemOverheated;

    /*
    ic按bit
    BIT0：安全气囊异常
    0:故障发生 1:故障消除
    BIT1： ABS故障
    0:故障发生 1:故障消除
    BIT2：油压低
    0:故障发生 1:故障消除
    BIT3~BIT7：预留
     */
    private String airbagAbnormal;
    private String absFault;
    private String oilPressureLow;

    /*
    abs按bit
    BIT0： 刹车液位低。
    0:故障发生 1:故障消除
    BIT1~BIT7：预留
     */
    private String brakeFluidLevelLow;

    /*
    pdc按bit
    BIT0： PDC系统故障。
    0:故障发生 1:故障消除
    BIT1~BIT7：预留
    */
    private String pdcSystemFault;
    /*
    bcm2按bit
    BIT0： 安全气囊触发。
    0: 触发 1:未触发
    BIT1~BIT7：预留
     */
    private String airbagTriggered;


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
    @Column(name = "latitude", nullable = false, insertable = true, updatable = true, length = 11)
    public String getLatitude() {
        return latitude;
    }

    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }

    @Basic
    @Column(name = "longitude", nullable = false, insertable = true, updatable = true, length = 11)
    public String getLongitude() {
        return longitude;
    }

    public void setLongitude(String longitude) {
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

    @Basic
    @Column(name = "battery_voltage_too_high", nullable = false, insertable = true, updatable = true, length = 1)
    public String getBatteryVoltageTooHigh() {
        return batteryVoltageTooHigh;
    }

    public void setBatteryVoltageTooHigh(String batteryVoltageTooHigh) {
        this.batteryVoltageTooHigh = batteryVoltageTooHigh;
    }

    @Basic
    @Column(name = "battery_voltage_too_low", nullable = false, insertable = true, updatable = true, length = 1)
    public String getBatteryVoltageTooLow() {
        return batteryVoltageTooLow;
    }

    public void setBatteryVoltageTooLow(String batteryVoltageTooLow) {
        this.batteryVoltageTooLow = batteryVoltageTooLow;
    }

    @Basic
    @Column(name = "media_abnormal", nullable = false, insertable = true, updatable = true, length = 1)
    public String getMediaAbnormal() {
        return mediaAbnormal;
    }

    public void setMediaAbnormal(String mediaAbnormal) {
        this.mediaAbnormal = mediaAbnormal;
    }

    @Basic
    @Column(name = "frozen_liquid_shortage", nullable = false, insertable = true, updatable = true, length = 1)
    public String getFrozenLiquidShortage() {
        return frozenLiquidShortage;
    }

    public void setFrozenLiquidShortage(String frozenLiquidShortage) {
        this.frozenLiquidShortage = frozenLiquidShortage;
    }

    @Basic
    @Column(name = "lamp_failure", nullable = false, insertable = true, updatable = true, length = 1)
    public String getLampFailure() {
        return lampFailure;
    }

    public void setLampFailure(String lampFailure) {
        this.lampFailure = lampFailure;
    }

    @Basic
    @Column(name = "engine_abnormal", nullable = false, insertable = true, updatable = true, length = 1)
    public String getEngineAbnormal() {
        return engineAbnormal;
    }

    public void setEngineAbnormal(String engineAbnormal) {
        this.engineAbnormal = engineAbnormal;
    }

    @Basic
    @Column(name = "water_temperature_too_high", nullable = false, insertable = true, updatable = true, length = 1)
    public String getWaterTemperatureTooHigh() {
        return waterTemperatureTooHigh;
    }

    public void setWaterTemperatureTooHigh(String waterTemperatureTooHigh) {
        this.waterTemperatureTooHigh = waterTemperatureTooHigh;
    }

    @Basic
    @Column(name = "dangerous_driving_system_fault", nullable = false, insertable = true, updatable = true, length = 1)
    public String getDangerousDrivingSystemFault() {
        return dangerousDrivingSystemFault;
    }

    public void setDangerousDrivingSystemFault(String dangerousDrivingSystemFault) {
        this.dangerousDrivingSystemFault = dangerousDrivingSystemFault;
    }

    @Basic
    @Column(name = "warning_driving_system_fault", nullable = false, insertable = true, updatable = true, length = 1)
    public String getWarningDrivingSystemFault() {
        return warningDrivingSystemFault;
    }

    public void setWarningDrivingSystemFault(String warningDrivingSystemFault) {
        this.warningDrivingSystemFault = warningDrivingSystemFault;
    }

    @Basic
    @Column(name = "driving_system_overheated", nullable = false, insertable = true, updatable = true, length = 1)
    public String getDrivingSystemOverheated() {
        return drivingSystemOverheated;
    }

    public void setDrivingSystemOverheated(String drivingSystemOverheated) {
        this.drivingSystemOverheated = drivingSystemOverheated;
    }

    @Basic
    @Column(name = "airbag_abnormal", nullable = false, insertable = true, updatable = true, length = 1)
    public String getAirbagAbnormal() {
        return airbagAbnormal;
    }

    public void setAirbagAbnormal(String airbagAbnormal) {
        this.airbagAbnormal = airbagAbnormal;
    }

    @Basic
    @Column(name = "abs_fault", nullable = false, insertable = true, updatable = true, length = 1)
    public String getAbsFault() {
        return absFault;
    }

    public void setAbsFault(String absFault) {
        this.absFault = absFault;
    }

    @Basic
    @Column(name = "oil_pressure_low", nullable = false, insertable = true, updatable = true, length = 1)
    public String getOilPressureLow() {
        return oilPressureLow;
    }

    public void setOilPressureLow(String oilPressureLow) {
        this.oilPressureLow = oilPressureLow;
    }

    @Basic
    @Column(name = "brake_fluid_level_low", nullable = false, insertable = true, updatable = true, length = 1)
    public String getBrakeFluidLevelLow() {
        return brakeFluidLevelLow;
    }

    public void setBrakeFluidLevelLow(String brakeFluidLevelLow) {
        this.brakeFluidLevelLow = brakeFluidLevelLow;
    }

    @Basic
    @Column(name = "pdc_system_fault", nullable = false, insertable = true, updatable = true, length = 1)
    public String getPdcSystemFault() {
        return pdcSystemFault;
    }

    public void setPdcSystemFault(String pdcSystemFault) {
        this.pdcSystemFault = pdcSystemFault;
    }

    @Basic
    @Column(name = "airbag_triggered", nullable = false, insertable = true, updatable = true, length = 1)
    public String getAirbagTriggered() {
        return airbagTriggered;
    }

    public void setAirbagTriggered(String airbagTriggered) {
        this.airbagTriggered = airbagTriggered;
    }
}
