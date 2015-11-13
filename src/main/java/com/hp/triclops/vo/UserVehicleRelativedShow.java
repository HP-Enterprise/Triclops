package com.hp.triclops.vo;

import com.hp.triclops.entity.UserVehicleRelatived;

/**
 * Created by Teemol on 2015/11/11.
 */

/**
 * <table summary="UserVehicleRelativedShow" class="typeSummary">
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
 *             <td>int</td>
 *             <td>车辆绑定ID</td>
 *         </tr>
 *         <tr>
 *             <td>userid</td>
 *             <td>int</td>
 *             <td>用户ID</td>
 *         </tr>
 *         <tr>
 *             <td>vid</td>
 *             <td>int</td>
 *             <td>车辆ID</td>
 *         </tr>
 *         <tr>
 *             <td>vflag</td>
 *             <td>int</td>
 *             <td>是否为默认车辆</td>
 *         </tr>
 *         <tr>
 *             <td>iflag</td>
 *             <td>int</td>
 *             <td>是否为车主</td>
 *         </tr>
 *         <tr>
 *             <td>parentuser</td>
 *             <td>int</td>
 *             <td>共享驾驶人ID</td>
 *         </tr>
 *     </tbody>
 * </table>
 */
public class UserVehicleRelativedShow {

    private int id;           // ID
    private int uid;          // 用户ID
    private int vid;          // 车辆ID
    private int vflag;        // 是否为默认车辆
    private int iflag;        // 是否为车主
    private int parentuser;   // 共享驾驶人ID

    public UserVehicleRelativedShow() {}

    public UserVehicleRelativedShow(int id, int uid, int vid, int vflag, int iflag, int parentuser) {
        this.id = id;
        this.uid = uid;
        this.vid = vid;
        this.vflag = vflag;
        this.iflag = iflag;
        this.parentuser = parentuser;
    }

    public UserVehicleRelativedShow(UserVehicleRelatived userVehicleRelatived){

        this.id = userVehicleRelatived.getId();
        this.uid = userVehicleRelatived.getUid().getId();
        this.vid = userVehicleRelatived.getVid().getId();
        this.vflag = userVehicleRelatived.getVflag();
        this.iflag = userVehicleRelatived.getIflag();
        this.parentuser = userVehicleRelatived.getParentuser().getId();

    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getUid() {
        return uid;
    }

    public void setUid(int uid) {
        this.uid = uid;
    }

    public int getVid() {
        return vid;
    }

    public void setVid(int vid) {
        this.vid = vid;
    }

    public int getVflag() {
        return vflag;
    }

    public void setVflag(int vflag) {
        this.vflag = vflag;
    }

    public int getIflag() {
        return iflag;
    }

    public void setIflag(int iflag) {
        this.iflag = iflag;
    }

    public int getParentuser() {
        return parentuser;
    }

    public void setParentuser(int parentuser) {
        this.parentuser = parentuser;
    }
}
