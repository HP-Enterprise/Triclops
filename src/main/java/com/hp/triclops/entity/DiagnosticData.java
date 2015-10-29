package com.hp.triclops.entity;

import javax.persistence.*;
import java.util.Date;

/**
 * Created by luj on 2015/10/28.
 */
@Entity
@Table(name = "t_data_diagnostic")
public class DiagnosticData {
    private Long id;
    private String vin;
    private Long eventId;
    private Short hasAck;
    private Short diaCmdDataSize;
    private Short diaNumber;
    private Short diaId;
    private Date sendDate;
    private Date receiveDate;
    private String message1;
    private String message2;
    private String message3;
    private String message4;
    private String message5;
    private String message6;
    private String message7;
    private String message8;
    private String message9;
    private String message10;
    private String message11;
    private String message12;
    private String message13;
    private String message14;
    private String message15;
    private String message16;
    private String message17;


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

    public Long getEventId() {
        return eventId;
    }

    public void setEventId(Long eventId) {
        this.eventId = eventId;
    }

    public Short getHasAck() {
        return hasAck;
    }

    public void setHasAck(Short hasAck) {
        this.hasAck = hasAck;
    }

    public Short getDiaCmdDataSize() {
        return diaCmdDataSize;
    }

    public void setDiaCmdDataSize(Short diaCmdDataSize) {
        this.diaCmdDataSize = diaCmdDataSize;
    }

    public Short getDiaNumber() {
        return diaNumber;
    }

    public void setDiaNumber(Short diaNumber) {
        this.diaNumber = diaNumber;
    }

    public Short getDiaId() {
        return diaId;
    }

    public void setDiaId(Short diaId) {
        this.diaId = diaId;
    }

    public Date getSendDate() {
        return sendDate;
    }

    public void setSendDate(Date sendDate) {
        this.sendDate = sendDate;
    }

    public Date getReceiveDate() {
        return receiveDate;
    }

    public void setReceiveDate(Date receiveDate) {
        this.receiveDate = receiveDate;
    }

    public String getMessage1() {
        return message1;
    }

    public void setMessage1(String message1) {
        this.message1 = message1;
    }

    public String getMessage2() {
        return message2;
    }

    public void setMessage2(String message2) {
        this.message2 = message2;
    }

    public String getMessage3() {
        return message3;
    }

    public void setMessage3(String message3) {
        this.message3 = message3;
    }

    public String getMessage4() {
        return message4;
    }

    public void setMessage4(String message4) {
        this.message4 = message4;
    }

    public String getMessage5() {
        return message5;
    }

    public void setMessage5(String message5) {
        this.message5 = message5;
    }

    public String getMessage6() {
        return message6;
    }

    public void setMessage6(String message6) {
        this.message6 = message6;
    }

    public String getMessage7() {
        return message7;
    }

    public void setMessage7(String message7) {
        this.message7 = message7;
    }

    public String getMessage8() {
        return message8;
    }

    public void setMessage8(String message8) {
        this.message8 = message8;
    }

    public String getMessage9() {
        return message9;
    }

    public void setMessage9(String message9) {
        this.message9 = message9;
    }

    public String getMessage10() {
        return message10;
    }

    public void setMessage10(String message10) {
        this.message10 = message10;
    }

    public String getMessage11() {
        return message11;
    }

    public void setMessage11(String message11) {
        this.message11 = message11;
    }

    public String getMessage12() {
        return message12;
    }

    public void setMessage12(String message12) {
        this.message12 = message12;
    }

    public String getMessage13() {
        return message13;
    }

    public void setMessage13(String message13) {
        this.message13 = message13;
    }

    public String getMessage14() {
        return message14;
    }

    public void setMessage14(String message14) {
        this.message14 = message14;
    }

    public String getMessage15() {
        return message15;
    }

    public void setMessage15(String message15) {
        this.message15 = message15;
    }

    public String getMessage16() {
        return message16;
    }

    public void setMessage16(String message16) {
        this.message16 = message16;
    }

    public String getMessage17() {
        return message17;
    }

    public void setMessage17(String message17) {
        this.message17 = message17;
    }
}
