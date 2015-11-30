package com.hp.triclops.entity;

import org.springframework.cache.annotation.CacheEvict;

import javax.persistence.*;
import java.util.Date;

/**
 * Created by luj on 2015/10/28.
 */

/**
 * <table summary="DiagnosticData" class="typeSummary">
 *     <thead>
 *         <tr>
 *             <th>字段</th>
 *             <th>数据类型</th>
 *             <th>说明</th>
 *         </tr>
 *     </thead>
 *     <tbody>
 *         <tr>
 *             <td>id</td>
 *             <td>Long</td>
 *             <td>ID</td>
 *         </tr>
 *         <tr>
 *             <td>vin</td>
 *             <td>String</td>
 *             <td>车辆VIN码</td>
 *         </tr>
 *          <tr>
 *             <td>eventId</td>
 *             <td>Long</td>
 *             <td>事件标识</td>
 *         </tr>
 *         <tr>
 *             <td>hasAck</td>
 *             <td>Short</td>
 *             <td>是否有响应</td>
 *         </tr>
 *         <tr>
 *             <td>diaCmdDataSize</td>
 *             <td>Short</td>
 *             <td>响应参数大小</td>
 *         </tr>
 *         <tr>
 *             <td>diaNumber</td>
 *             <td>Short</td>
 *             <td>响应参数个数</td>
 *         </tr>
 *         <tr>
 *             <td>diaId</td>
 *             <td>Short</td>
 *             <td>命令ID</td>
 *         </tr>
 *         <tr>
 *             <td>sendDate</td>
 *             <td>Date</td>
 *             <td>命令发送时间</td>
 *         </tr>
 *         <tr>
 *             <td>receiveDate</td>
 *             <td>Date</td>
 *             <td>响应接收时间</td>
 *         </tr>
 *         <tr>
 *             <td>message1</td>
 *             <td>Short</td>
 *             <td>消息1</td>
 *         </tr>
 *         <tr>
 *             <td>message2</td>
 *             <td>Short</td>
 *             <td>消息2</td>
 *         </tr>
 *         <tr>
 *             <td>message3</td>
 *             <td>Short</td>
 *             <td>消息3</td>
 *         </tr>
 *         <tr>
 *             <td>message4</td>
 *             <td>Short</td>
 *             <td>消息4</td>
 *         </tr>
 *         <tr>
 *             <td>message5</td>
 *             <td>Short</td>
 *             <td>消息5</td>
 *         </tr>
 *         <tr>
 *             <td>message6</td>
 *             <td>Short</td>
 *             <td>消息6</td>
 *         </tr>
 *         <tr>
 *             <td>message7</td>
 *             <td>Short</td>
 *             <td>消息7</td>
 *         </tr>
 *         <tr>
 *             <td>message8</td>
 *             <td>Short</td>
 *             <td>消息8</td>
 *         </tr>
 *         <tr>
 *             <td>message9</td>
 *             <td>Short</td>
 *             <td>消息9</td>
 *         </tr>
 *         <tr>
 *             <td>message10</td>
 *             <td>Short</td>
 *             <td>消息10</td>
 *         </tr>
 *         <tr>
 *             <td>message11</td>
 *             <td>Short</td>
 *             <td>消息11</td>
 *         </tr>
 *         <tr>
 *             <td>message12</td>
 *             <td>Short</td>
 *             <td>消息12</td>
 *         </tr>
 *         <tr>
 *             <td>message13</td>
 *             <td>Short</td>
 *             <td>消息13</td>
 *         </tr>
 *         <tr>
 *             <td>message14</td>
 *             <td>Short</td>
 *             <td>消息14</td>
 *         </tr>
 *         <tr>
 *             <td>message15</td>
 *             <td>Short</td>
 *             <td>消息15</td>
 *         </tr>
 *         <tr>
 *             <td>message16</td>
 *             <td>Short</td>
 *             <td>消息16</td>
 *         </tr>
 *     </tbody>
 * </table>
 */
@Entity
@Table(name = "t_data_diagnostic")
public class DiagnosticData {
    private Long id;
    private String vin;
    private Long eventId;
    private Short hasAck;
    private Date sendDate;
    private Date receiveDate;
    private Short message1;
    private Short message2;
    private Short message3;
    private Short message4;
    private Short message5;
    private Short message6;
    private Short message7;
    private Short message8;
    private Short message9;
    private Short message10;
    private Short message11;
    private Short message12;
    private Short message13;
    private Short message14;
    private Short message15;
    private Short message16;


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

    public Short getMessage1() {
        return message1;
    }

    public void setMessage1(Short message1) {
        this.message1 = message1;
    }

    public Short getMessage2() {
        return message2;
    }

    public void setMessage2(Short message2) {
        this.message2 = message2;
    }

    public Short getMessage3() {
        return message3;
    }

    public void setMessage3(Short message3) {
        this.message3 = message3;
    }

    public Short getMessage4() {
        return message4;
    }

    public void setMessage4(Short message4) {
        this.message4 = message4;
    }

    public Short getMessage5() {
        return message5;
    }

    public void setMessage5(Short message5) {
        this.message5 = message5;
    }

    public Short getMessage6() {
        return message6;
    }

    public void setMessage6(Short message6) {
        this.message6 = message6;
    }

    public Short getMessage7() {
        return message7;
    }

    public void setMessage7(Short message7) {
        this.message7 = message7;
    }

    public Short getMessage8() {
        return message8;
    }

    public void setMessage8(Short message8) {
        this.message8 = message8;
    }

    public Short getMessage9() {
        return message9;
    }

    public void setMessage9(Short message9) {
        this.message9 = message9;
    }

    public Short getMessage10() {
        return message10;
    }

    public void setMessage10(Short message10) {
        this.message10 = message10;
    }

    public Short getMessage11() {
        return message11;
    }

    public void setMessage11(Short message11) {
        this.message11 = message11;
    }

    public Short getMessage12() {
        return message12;
    }

    public void setMessage12(Short message12) {
        this.message12 = message12;
    }

    public Short getMessage13() {
        return message13;
    }

    public void setMessage13(Short message13) {
        this.message13 = message13;
    }

    public Short getMessage14() {
        return message14;
    }

    public void setMessage14(Short message14) {
        this.message14 = message14;
    }

    public Short getMessage15() {
        return message15;
    }

    public void setMessage15(Short message15) {
        this.message15 = message15;
    }

    public Short getMessage16() {
        return message16;
    }

    public void setMessage16(Short message16) {
        this.message16 = message16;
    }
}
