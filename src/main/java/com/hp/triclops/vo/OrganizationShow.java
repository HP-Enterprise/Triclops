package com.hp.triclops.vo;

import com.hp.triclops.entity.Organization;
import com.hp.triclops.entity.OrganizationEx;

/**
 * <table summary="OrganizationShow" class="typeSummary">
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
 *             <td>组织ID</td>
 *         </tr>
 *         <tr>
 *             <td>orgName</td>
 *             <td>String</td>
 *             <td>组织名称</td>
 *         </tr>
 *         <tr>
 *             <td>breCode</td>
 *             <td>String</td>
 *             <td>组织简码</td>
 *         </tr>
 *         <tr>
 *             <td>typeKey</td>
 *             <td>int</td>
 *             <td>组织类型</td>
 *         </tr>
 *         <tr>
 *             <td>descript</td>
 *             <td>String</td>
 *             <td>组织描述</td>
 *         </tr>
 *         <tr>
 *             <td>areaid</td>
 *             <td>int</td>
 *             <td>组织所属地区id</td>
 *         </tr>
 *         <tr>
 *             <td>available</td>
 *             <td>int</td>
 *             <td>组织是否可用 0-失效 1-可用</td>
 *         </tr>
 *     </tbody>
 * </table>
 */
public class OrganizationShow{

    private int id;
    private String orgName;
    private String breCode;
    private int typeKey;
    private String descript;
    private int available;
    private Integer areaid;


    public OrganizationShow() {
    }

    public OrganizationShow(Organization orgOne) {
        this.id = orgOne.getId();
        this.orgName = orgOne.getOrgName();
        this.breCode = orgOne.getBreCode();
        this.typeKey = orgOne.getTypeKey().getDictid();
        this.descript = orgOne.getDescript();
        this.available = orgOne.getAvailable();
        this.areaid = orgOne.getAreaid();
    }

    public OrganizationShow(OrganizationEx orgOne) {
        this.id = orgOne.getId();
        this.orgName = orgOne.getOrgName();
        this.breCode = orgOne.getBreCode();
        this.typeKey = orgOne.getTypeKey();
        this.descript = orgOne.getDescript();
        this.available = orgOne.getAvailable();
        this.areaid = orgOne.getAreaid();
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getOrgName() {
        return orgName;
    }

    public void setOrgName(String orgName) {
        this.orgName = orgName;
    }

    public String getBreCode() {
        return breCode;
    }

    public void setBreCode(String breCode) {
        this.breCode = breCode;
    }

    public int getTypeKey() {
        return typeKey;
    }

    public void setTypeKey(int typeKey) {
        this.typeKey = typeKey;
    }

    public String getDescript() {
        return descript;
    }

    public void setDescript(String descript) {
        this.descript = descript;
    }

    public int getAvailable() {
        return available;
    }

    public void setAvailable(int available) {
        this.available = available;
    }

    public Integer getAreaid() {
        return areaid;
    }

    public void setAreaid(Integer areaid) {
        this.areaid = areaid;
    }
}
