package com.hp.triclops.entity;

import javax.persistence.*;

/**
 * Created by Teemol on 2015/11/11.
 */

/**
 * 用户通讯录类
 */

/**
 * <table summary="PhoneBookShow" class="typeSummary">
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
 *             <td>ID</td>
 *         </tr>
 *         <tr>
 *             <td>uid</td>
 *             <td>int</td>
 *             <td>用户ID</td>
 *         </tr>
 *         <tr>
 *             <td>name</td>
 *             <td>String</td>
 *             <td>联系人姓名</td>
 *         </tr>
 *         <tr>
 *             <td>phone</td>
 *             <td>String</td>
 *             <td>电话号码</td>
 *         </tr>
 *         <tr>
 *             <td>userid</td>
 *             <td>int</td>
 *             <td>联系人对应用户ID</td>
 *         </tr>
 *         <tr>
 *             <td>isuser</td>
 *             <td>int</td>
 *             <td>是否为系统用户</td>
 *         </tr>
 *     </tbody>
 * </table>
 */

@Entity
@Table(name = "t_phone_book")
public class PhoneBook {

    private int id;
    private int uid;
    private String name;
    private String phone;
    private int userid;
    private int isuser;
    
    public PhoneBook(){}

    public PhoneBook(int id, int uid, String name, String phone, int userid, int isuser) {
        this.id = id;
        this.uid = uid;
        this.name = name;
        this.phone = phone;
        this.userid = userid;
        this.isuser = isuser;
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false, insertable = true, updatable = true)
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    @Basic
    @Column(name = "uid", nullable = false, insertable = true, updatable = true, length = 11)
    public int getUid() {
        return uid;
    }

    public void setUid(int uid) {
        this.uid = uid;
    }

    @Basic
    @Column(name = "name", nullable = false, insertable = true, updatable = true, length = 50)
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Basic
    @Column(name = "phone", nullable = true, insertable = true, updatable = true)
    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    @Basic
    @Column(name = "userid", nullable = true, insertable = true, updatable = true)
    public int getUserid() {
        return userid;
    }

    public void setUserid(int userid) {
        this.userid = userid;
    }

    @Basic
    @Column(name = "isuser", nullable = false, insertable = true, updatable = true, length = 1)
    public int getIsuser() {
        return isuser;
    }

    public void setIsuser(int isuser) {
        this.isuser = isuser;
    }
}
