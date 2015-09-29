package com.hp.triclops.entity;

import javax.persistence.*;

/**
 * Created by luj on 2015/9/29.
 */

@Entity
@Table(name = "t_data_regular_report")
public class RegularReportData {
    //额定数据
    private Long id;
    private String vin;
    private String serialNumber;
    private String imei;
    private int applicationId;
    private int messageId;
    private int sendingTime;
    //参见文档额定数据表
    private int frequencyForRealTimeReport;
    private int frequencyForWarningReport;
    private int frequencyHeartbeat;
    private int timeOutForTerminalSearch;
    private int timeOutForServerSearch;
    private Short vehicleType;
    private int vehicleModels;
    private Short maxSpeed;
    private String hardwareVersion;
    private String softwareVersion;
    private int frequencySaveLocalMedia;
    private String enterpriseBroadcastAddress;
    private int enterpriseBroadcastPort;

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
    @Column(name = "serial_number", nullable = false, insertable = true, updatable = true, length = 50)
    public String getSerialNumber() {
        return serialNumber;
    }

    public void setSerialNumber(String serialNumber) {
        this.serialNumber = serialNumber;
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
    public int getSendingTime() {
        return sendingTime;
    }

    public void setSendingTime(int sendingTime) {
        this.sendingTime = sendingTime;
    }

    @Basic
    @Column(name = "frequency_for_realtime_report", nullable = false, insertable = true, updatable = true)
    public int getFrequencyForRealTimeReport() {
        return frequencyForRealTimeReport;
    }

    public void setFrequencyForRealTimeReport(int frequencyForRealTimeReport) {
        this.frequencyForRealTimeReport = frequencyForRealTimeReport;
    }

    @Basic
    @Column(name = "frequency_for_warning_report", nullable = false, insertable = true, updatable = true)
    public int getFrequencyForWarningReport() {
        return frequencyForWarningReport;
    }

    public void setFrequencyForWarningReport(int frequencyForWarningReport) {
        this.frequencyForWarningReport = frequencyForWarningReport;
    }

    @Basic
    @Column(name = "frequency_heartbeat", nullable = false, insertable = true, updatable = true)
    public int getFrequencyHeartbeat() {
        return frequencyHeartbeat;
    }

    public void setFrequencyHeartbeat(int frequencyHeartbeat) {
        this.frequencyHeartbeat = frequencyHeartbeat;
    }

    @Basic
    @Column(name = "timeout_for_terminal_search", nullable = false, insertable = true, updatable = true)
    public int getTimeOutForTerminalSearch() {
        return timeOutForTerminalSearch;
    }

    public void setTimeOutForTerminalSearch(int timeOutForTerminalSearch) {
        this.timeOutForTerminalSearch = timeOutForTerminalSearch;
    }

    @Basic
    @Column(name = "timeout_for_server_search", nullable = false, insertable = true, updatable = true)
    public int getTimeOutForServerSearch() {
        return timeOutForServerSearch;
    }

    public void setTimeOutForServerSearch(int timeOutForServerSearch) {
        this.timeOutForServerSearch = timeOutForServerSearch;
    }

    @Basic
    @Column(name = "vehicle_type", nullable = false, insertable = true, updatable = true)
    public Short getVehicleType() {
        return vehicleType;
    }

    public void setVehicleType(Short vehicleType) {
        this.vehicleType = vehicleType;
    }

    @Basic
    @Column(name = "vehicle_models", nullable = false, insertable = true, updatable = true)
    public int getVehicleModels() {
        return vehicleModels;
    }

    public void setVehicleModels(int vehicleModels) {
        this.vehicleModels = vehicleModels;
    }

    @Basic
    @Column(name = "max_speed", nullable = false, insertable = true, updatable = true)
    public Short getMaxSpeed() {
        return maxSpeed;
    }

    public void setMaxSpeed(Short maxSpeed) {
        this.maxSpeed = maxSpeed;
    }

    @Basic
    @Column(name = "hardware_version", nullable = false, insertable = true, updatable = true, length = 50)
    public String getHardwareVersion() {
        return hardwareVersion;
    }

    public void setHardwareVersion(String hardwareVersion) {
        this.hardwareVersion = hardwareVersion;
    }

    @Basic
    @Column(name = "software_version", nullable = false, insertable = true, updatable = true, length = 50)
    public String getSoftwareVersion() {
        return softwareVersion;
    }

    public void setSoftwareVersion(String softwareVersion) {
        this.softwareVersion = softwareVersion;
    }

    @Basic
    @Column(name = "frequency_save_local_media", nullable = false, insertable = true, updatable = true)
    public int getFrequencySaveLocalMedia() {
        return frequencySaveLocalMedia;
    }

    public void setFrequencySaveLocalMedia(int frequencySaveLocalMedia) {
        this.frequencySaveLocalMedia = frequencySaveLocalMedia;
    }

    @Basic
    @Column(name = "enterprise_broadcast_address", nullable = false, insertable = true, updatable = true, length = 50)
    public String getEnterpriseBroadcastAddress() {
        return enterpriseBroadcastAddress;
    }

    public void setEnterpriseBroadcastAddress(String enterpriseBroadcastAddress) {
        this.enterpriseBroadcastAddress = enterpriseBroadcastAddress;
    }

    @Basic
    @Column(name = "enterprise_broadcast_port", nullable = false, insertable = true, updatable = true, length = 50)
    public int getEnterpriseBroadcastPort() {
        return enterpriseBroadcastPort;
    }

    public void setEnterpriseBroadcastPort(int enterpriseBroadcastPort) {
        this.enterpriseBroadcastPort = enterpriseBroadcastPort;
    }




}
