package com.hp.triclops.entity;

import javax.persistence.*;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

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
 *             <td>String</td>
 *             <td>消息1</td>
 *         </tr>
 *         <tr>
 *             <td>message2</td>
 *             <td>String</td>
 *             <td>消息2</td>
 *         </tr>
 *         <tr>
 *             <td>message3</td>
 *             <td>String</td>
 *             <td>消息3</td>
 *         </tr>
 *         <tr>
 *             <td>message4</td>
 *             <td>String</td>
 *             <td>消息4</td>
 *         </tr>
 *         <tr>
 *             <td>message5</td>
 *             <td>String</td>
 *             <td>消息5</td>
 *         </tr>
 *         <tr>
 *             <td>message6</td>
 *             <td>String</td>
 *             <td>消息6</td>
 *         </tr>
 *         <tr>
 *             <td>message7</td>
 *             <td>String</td>
 *             <td>消息7</td>
 *         </tr>
 *         <tr>
 *             <td>message8</td>
 *             <td>String</td>
 *             <td>消息8</td>
 *         </tr>
 *         <tr>
 *             <td>message9</td>
 *             <td>String</td>
 *             <td>消息9</td>
 *         </tr>
 *         <tr>
 *             <td>message10</td>
 *             <td>String</td>
 *             <td>消息10</td>
 *         </tr>
 *         <tr>
 *             <td>message11</td>
 *             <td>String</td>
 *             <td>消息11</td>
 *         </tr>
 *         <tr>
 *             <td>message12</td>
 *             <td>String</td>
 *             <td>消息12</td>
 *         </tr>
 *         <tr>
 *             <td>message13</td>
 *             <td>String</td>
 *             <td>消息13</td>
 *         </tr>
 *         <tr>
 *             <td>message14</td>
 *             <td>String</td>
 *             <td>消息14</td>
 *         </tr>
 *         <tr>
 *             <td>message15</td>
 *             <td>String</td>
 *             <td>消息15</td>
 *         </tr>
 *         <tr>
 *             <td>message16</td>
 *             <td>String</td>
 *             <td>消息16</td>
 *         </tr>
 *     </tbody>
 * </table>
 */
public class DiagnosticDataShow {
    private Long id;
    private String vin;
    private Long eventId;
    private Short hasAck;
    private Date sendDate;
    private Date receiveDate;
    private HashMap<String,String> list;



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

    public HashMap<String, String> getList() {
        return list;
    }

    public void setList(HashMap<String, String> list) {
        this.list = list;
    }
}
