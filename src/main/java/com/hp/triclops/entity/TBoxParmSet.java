package com.hp.triclops.entity;

import javax.persistence.*;
import java.util.Date;

/**
 * Created by luj on 2015/10/14.
 */
@Entity
@Table(name = "t_tbox_parm_set")
public class TBoxParmSet {
    private Long id;
    private String vin;
    private Date sendingTime;
    private Long eventId;
    private int frequencySaveLocalMedia;
    private Short frequencySaveLocalMediaResult;
    private int frequencyForReport;
    private Short frequencyForReportResult;
    private int frequencyForWarningReport;
    private Short frequencyForWarningReportResult;
    private Short frequencyHeartbeat;
    private Short frequencyHeartbeatResult;
    private int timeOutForTerminalSearch;
    private Short timeOutForTerminalSearchResult;
    private int timeOutForServerSearch;
    private Short timeOutForServerSearchResult;
    private Short uploadType;
    private Short uploadTypeResult;
    private String enterpriseBroadcastAddress1;
    private Short enterpriseBroadcastAddress1Result;
    private int enterpriseBroadcastPort1;
    private Short enterpriseBroadcastPort1Result;
    private String enterpriseBroadcastAddress2;
    private Short enterpriseBroadcastAddress2Result;
    private int enterpriseBroadcastPort2;
    private Short enterpriseBroadcastPort2Result;
    private Short enterpriseDomainNameSize;
    private Short enterpriseDomainNameSizeResult;
    private String enterpriseDomainName;
    private Short enterpriseDomainNameResult;
    private Short status;//0:TBox离线尚未发出 1:已向TBox发出 2:TBOX已响应
    private String version;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false, insertable = true, updatable = true)
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

    public Date getSendingTime() {
        return sendingTime;
    }

    public void setSendingTime(Date sendingTime) {
        this.sendingTime = sendingTime;
    }


    public Long getEventId() {
        return eventId;
    }

    public void setEventId(Long eventId) {
        this.eventId = eventId;
    }

    public int getFrequencySaveLocalMedia() {
        return frequencySaveLocalMedia;
    }

    public void setFrequencySaveLocalMedia(int frequencySaveLocalMedia) {
        this.frequencySaveLocalMedia = frequencySaveLocalMedia;
    }

    public Short getFrequencySaveLocalMediaResult() {
        return frequencySaveLocalMediaResult;
    }

    public void setFrequencySaveLocalMediaResult(Short frequencySaveLocalMediaResult) {
        this.frequencySaveLocalMediaResult = frequencySaveLocalMediaResult;
    }

    public int getFrequencyForReport() {
        return frequencyForReport;
    }

    public void setFrequencyForReport(int frequencyForReport) {
        this.frequencyForReport = frequencyForReport;
    }

    public Short getFrequencyForReportResult() {
        return frequencyForReportResult;
    }

    public void setFrequencyForReportResult(Short frequencyForReportResult) {
        this.frequencyForReportResult = frequencyForReportResult;
    }

    public int getFrequencyForWarningReport() {
        return frequencyForWarningReport;
    }

    public void setFrequencyForWarningReport(int frequencyForWarningReport) {
        this.frequencyForWarningReport = frequencyForWarningReport;
    }

    public Short getFrequencyForWarningReportResult() {
        return frequencyForWarningReportResult;
    }

    public void setFrequencyForWarningReportResult(Short frequencyForWarningReportResult) {
        this.frequencyForWarningReportResult = frequencyForWarningReportResult;
    }

    public Short getFrequencyHeartbeat() {
        return frequencyHeartbeat;
    }

    public void setFrequencyHeartbeat(Short frequencyHeartbeat) {
        this.frequencyHeartbeat = frequencyHeartbeat;
    }

    public Short getFrequencyHeartbeatResult() {
        return frequencyHeartbeatResult;
    }

    public void setFrequencyHeartbeatResult(Short frequencyHeartbeatResult) {
        this.frequencyHeartbeatResult = frequencyHeartbeatResult;
    }

    public int getTimeOutForTerminalSearch() {
        return timeOutForTerminalSearch;
    }

    public void setTimeOutForTerminalSearch(int timeOutForTerminalSearch) {
        this.timeOutForTerminalSearch = timeOutForTerminalSearch;
    }

    public Short getTimeOutForTerminalSearchResult() {
        return timeOutForTerminalSearchResult;
    }

    public void setTimeOutForTerminalSearchResult(Short timeOutForTerminalSearchResult) {
        this.timeOutForTerminalSearchResult = timeOutForTerminalSearchResult;
    }

    public int getTimeOutForServerSearch() {
        return timeOutForServerSearch;
    }

    public void setTimeOutForServerSearch(int timeOutForServerSearch) {
        this.timeOutForServerSearch = timeOutForServerSearch;
    }

    public Short getTimeOutForServerSearchResult() {
        return timeOutForServerSearchResult;
    }

    public void setTimeOutForServerSearchResult(Short timeOutForServerSearchResult) {
        this.timeOutForServerSearchResult = timeOutForServerSearchResult;
    }

    public Short getUploadType() {
        return uploadType;
    }

    public void setUploadType(Short uploadType) {
        this.uploadType = uploadType;
    }

    public Short getUploadTypeResult() {
        return uploadTypeResult;
    }

    public void setUploadTypeResult(Short uploadTypeResult) {
        this.uploadTypeResult = uploadTypeResult;
    }

    public String getEnterpriseBroadcastAddress1() {
        return enterpriseBroadcastAddress1;
    }

    public void setEnterpriseBroadcastAddress1(String enterpriseBroadcastAddress1) {
        this.enterpriseBroadcastAddress1 = enterpriseBroadcastAddress1;
    }

    public Short getEnterpriseBroadcastAddress1Result() {
        return enterpriseBroadcastAddress1Result;
    }

    public void setEnterpriseBroadcastAddress1Result(Short enterpriseBroadcastAddress1Result) {
        this.enterpriseBroadcastAddress1Result = enterpriseBroadcastAddress1Result;
    }

    public int getEnterpriseBroadcastPort1() {
        return enterpriseBroadcastPort1;
    }

    public void setEnterpriseBroadcastPort1(int enterpriseBroadcastPort1) {
        this.enterpriseBroadcastPort1 = enterpriseBroadcastPort1;
    }

    public Short getEnterpriseBroadcastPort1Result() {
        return enterpriseBroadcastPort1Result;
    }

    public void setEnterpriseBroadcastPort1Result(Short enterpriseBroadcastPort1Result) {
        this.enterpriseBroadcastPort1Result = enterpriseBroadcastPort1Result;
    }

    public String getEnterpriseBroadcastAddress2() {
        return enterpriseBroadcastAddress2;
    }

    public void setEnterpriseBroadcastAddress2(String enterpriseBroadcastAddress2) {
        this.enterpriseBroadcastAddress2 = enterpriseBroadcastAddress2;
    }

    public Short getEnterpriseBroadcastAddress2Result() {
        return enterpriseBroadcastAddress2Result;
    }

    public void setEnterpriseBroadcastAddress2Result(Short enterpriseBroadcastAddress2Result) {
        this.enterpriseBroadcastAddress2Result = enterpriseBroadcastAddress2Result;
    }

    public int getEnterpriseBroadcastPort2() {
        return enterpriseBroadcastPort2;
    }

    public void setEnterpriseBroadcastPort2(int enterpriseBroadcastPort2) {
        this.enterpriseBroadcastPort2 = enterpriseBroadcastPort2;
    }

    public Short getEnterpriseBroadcastPort2Result() {
        return enterpriseBroadcastPort2Result;
    }

    public void setEnterpriseBroadcastPort2Result(Short enterpriseBroadcastPort2Result) {
        this.enterpriseBroadcastPort2Result = enterpriseBroadcastPort2Result;
    }

    public Short getEnterpriseDomainNameSize() {
        return enterpriseDomainNameSize;
    }

    public void setEnterpriseDomainNameSize(Short enterpriseDomainNameSize) {
        this.enterpriseDomainNameSize = enterpriseDomainNameSize;
    }

    public Short getEnterpriseDomainNameSizeResult() {
        return enterpriseDomainNameSizeResult;
    }

    public void setEnterpriseDomainNameSizeResult(Short enterpriseDomainNameSizeResult) {
        this.enterpriseDomainNameSizeResult = enterpriseDomainNameSizeResult;
    }

    public String getEnterpriseDomainName() {
        return enterpriseDomainName;
    }

    public void setEnterpriseDomainName(String enterpriseDomainName) {
        this.enterpriseDomainName = enterpriseDomainName;
    }

    public Short getEnterpriseDomainNameResult() {
        return enterpriseDomainNameResult;
    }

    public void setEnterpriseDomainNameResult(Short enterpriseDomainNameResult) {
        this.enterpriseDomainNameResult = enterpriseDomainNameResult;
    }

    public Short getStatus() {
        return status;
    }

    public void setStatus(Short status) {
        this.status = status;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }



}
