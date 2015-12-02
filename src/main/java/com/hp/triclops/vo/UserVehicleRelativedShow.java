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

    private Integer id;           // ID
    private Integer uid;          // 用户ID
    private Integer vid;          // 车辆ID
    private Integer vflag;        // 是否为默认车辆
    private Integer iflag;        // 是否为车主
    private Integer parentuser;   // 车主ID

    public UserVehicleRelativedShow() {}

    public UserVehicleRelativedShow(Integer id, Integer uid, Integer vid, Integer vflag, Integer iflag, Integer parentuser) {
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

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getUid() {
        return uid;
    }

    public void setUid(Integer uid) {
        this.uid = uid;
    }

    public Integer getVid() {
        return vid;
    }

    public void setVid(Integer vid) {
        this.vid = vid;
    }

    public Integer getVflag() {
        return vflag;
    }

    public void setVflag(Integer vflag) {
        this.vflag = vflag;
    }

    public Integer getIflag() {
        return iflag;
    }

    public void setIflag(Integer iflag) {
        this.iflag = iflag;
    }

    public Integer getParentuser() {
        return parentuser;
    }

    public void setParentuser(Integer parentuser) {
        this.parentuser = parentuser;
    }
}
