package com.hp.triclops.entity;

import javax.persistence.*;

/**
 * Created by jackl on 2017/1/12.
 */
@Entity
@Table(name = "t_drive_behavior_report")
public class DriveBehaviorReport {
    private Long id;
    private Short type;//1天 2周 3月
    private Short month;//月份1~12
    private String year;
    private Short week;//第多少周
    private String dateString;//日期字符串，2017-01-05

    private String brand;//品牌
    private String model;//车型
    private String vin;
    private Long runTimes;//运行时间 单位秒
    private Integer outTimes;//出行次数
    private Integer suddenUp;//急加速次数
    private Integer suddenDec;//急减速次数
    private Integer suddenTurn;//急转弯次数
    private Integer tireDriveTimes;//疲劳驾驶次数
    private Long speedingDrive;//超速时间 单位秒
    private Short maxSpeed;//最高超速速度 公里/小时
    private Float avgOil;//平均油耗
    private Integer mailRange;//累计行驶里程
    private Integer driveTime1;//1速段行驶时间　秒
    private Integer driveTime2;//1-45速段行驶时间 秒
    private Integer driveTime3;//40-90速段行驶时间 秒
    private Integer driveTime4;//90以上速段行驶时间 秒
    private Integer trafficGridlockTimes;//早晚高峰出行次数
    private Integer daybreakOutTimes;//凌晨出行次数
    private Integer score;//评分
    private String suggestion;//专家建议

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false, insertable = true, updatable = true)
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Short getType() {
        return type;
    }

    public void setType(Short type) {
        this.type = type;
    }

    public Short getMonth() {
        return month;
    }

    public void setMonth(Short month) {
        this.month = month;
    }

    public String getYear() {
        return year;
    }

    public void setYear(String year) {
        this.year = year;
    }

    public Short getWeek() {
        return week;
    }

    public void setWeek(Short week) {
        this.week = week;
    }

    public String getDateString() {
        return dateString;
    }

    public void setDateString(String dateString) {
        this.dateString = dateString;
    }

    public String getBrand() {
        return brand;
    }

    public void setBrand(String brand) {
        this.brand = brand;
    }

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public String getVin() {
        return vin;
    }

    public void setVin(String vin) {
        this.vin = vin;
    }

    public Long getRunTimes() {
        return runTimes;
    }

    public void setRunTimes(Long runTimes) {
        this.runTimes = runTimes;
    }

    public Integer getOutTimes() {
        return outTimes;
    }

    public void setOutTimes(Integer outTimes) {
        this.outTimes = outTimes;
    }

    public Integer getSuddenUp() {
        return suddenUp;
    }

    public void setSuddenUp(Integer suddenUp) {
        this.suddenUp = suddenUp;
    }

    public Integer getSuddenDec() {
        return suddenDec;
    }

    public void setSuddenDec(Integer suddenDec) {
        this.suddenDec = suddenDec;
    }

    public Integer getSuddenTurn() {
        return suddenTurn;
    }

    public void setSuddenTurn(Integer suddenTurn) {
        this.suddenTurn = suddenTurn;
    }

    public Integer getTireDriveTimes() {
        return tireDriveTimes;
    }

    public void setTireDriveTimes(Integer tireDriveTimes) {
        this.tireDriveTimes = tireDriveTimes;
    }

    public Long getSpeedingDrive() {
        return speedingDrive;
    }

    public void setSpeedingDrive(Long speedingDrive) {
        this.speedingDrive = speedingDrive;
    }

    public Short getMaxSpeed() {
        return maxSpeed;
    }

    public void setMaxSpeed(Short maxSpeed) {
        this.maxSpeed = maxSpeed;
    }

    public Float getAvgOil() {
        return avgOil;
    }

    public void setAvgOil(Float avgOil) {
        this.avgOil = avgOil;
    }

    public Integer getMailRange() {
        return mailRange;
    }

    public void setMailRange(Integer mailRange) {
        this.mailRange = mailRange;
    }

    public Integer getDriveTime1() {
        return driveTime1;
    }

    public void setDriveTime1(Integer driveTime1) {
        this.driveTime1 = driveTime1;
    }

    public Integer getDriveTime2() {
        return driveTime2;
    }

    public void setDriveTime2(Integer driveTime2) {
        this.driveTime2 = driveTime2;
    }

    public Integer getDriveTime3() {
        return driveTime3;
    }

    public void setDriveTime3(Integer driveTime3) {
        this.driveTime3 = driveTime3;
    }

    public Integer getDriveTime4() {
        return driveTime4;
    }

    public void setDriveTime4(Integer driveTime4) {
        this.driveTime4 = driveTime4;
    }

    public Integer getTrafficGridlockTimes() {
        return trafficGridlockTimes;
    }

    public void setTrafficGridlockTimes(Integer trafficGridlockTimes) {
        this.trafficGridlockTimes = trafficGridlockTimes;
    }

    public Integer getDaybreakOutTimes() {
        return daybreakOutTimes;
    }

    public void setDaybreakOutTimes(Integer daybreakOutTimes) {
        this.daybreakOutTimes = daybreakOutTimes;
    }

    public Integer getScore() {
        return score;
    }

    public void setScore(Integer score) {
        this.score = score;
    }

    public String getSuggestion() {
        return suggestion;
    }

    public void setSuggestion(String suggestion) {
        this.suggestion = suggestion;
    }
}
