package com.hp.triclops.vo;

import com.hp.triclops.entity.PhoneBook;

/**
 * Created by Teemol on 2015/11/11.
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
 *             <td>username</td>
 *             <td>String</td>
 *             <td>联系人用户名</td>
 *         </tr>
 *         <tr>
 *             <td>isuser</td>
 *             <td>int</td>
 *             <td>是否为系统用户</td>
 *         </tr>
 *     </tbody>
 * </table>
 */
public class PhoneBookShow{

    private int id;
    private int uid;
    private String name;
    private String phone;
    private String username;
    private int isuser;
    public PhoneBookShow(){

    }

    public PhoneBookShow(int id, int uid, String name, String phone, String username, int isuser) {
        this.id = id;
        this.uid = uid;
        this.name = name;
        this.phone = phone;
        this.username = username;
        this.isuser = isuser;
    }

    public PhoneBookShow(PhoneBook phoneBook){
        this.id = phoneBook.getId();
        this.uid = phoneBook.getUid();
        this.name = phoneBook.getName();
        this.phone = phoneBook.getPhone();
        this.username = phoneBook.getUsername();
        this.isuser = phoneBook.getIsuser();
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

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public int getIsuser() {
        return isuser;
    }

    public void setIsuser(int isuser) {
        this.isuser = isuser;
    }
}
