package com.hp.triclops.user.vo;

/**
 * Created by Teemol on 2015/11/5.
 */

import com.hp.triclops.entity.User;

/**
 * <table summary="UserShow" class="typeSummary">
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
 *             <td>用户ID</td>
 *         </tr>
 *         <tr>
 *             <td>name</td>
 *             <td>String</td>
 *             <td>用户名称</td>
 *         </tr>
 *         <tr>
 *             <td>gender</td>
 *             <td>int</td>
 *             <td>性别</td>
 *         </tr>
 *         <tr>
 *             <td>nick</td>
 *             <td>String</td>
 *             <td>昵称,显示给他人看的名称</td>
 *         </tr>
 *         <tr>
 *             <td>phone</td>
 *             <td>String</td>
 *             <td>电话号码</td>
 *         </tr>
 *         <tr>
 *             <td>isVerified</td>
 *             <td>int</td>
 *             <td>电话号码是否已认证</td>
 *         </tr>
 *         <tr>
 *             <td>contacts</td>
 *             <td>String</td>
 *             <td>联系人姓名</td>
 *         </tr>
 *         <tr>
 *             <td>contactsPhone</td>
 *             <td>String</td>
 *             <td>联系人电话号码</td>
 *         </tr>
 *     </tbody>
 * </table>
 */
public class UserShow {

    private int id;
    private String name;
    private Integer gender;
    private String nick;
    private String phone;
    private int isVerified;
    private String contacts;
    private String contactsPhone;

    public UserShow() {}

    public UserShow(User user) {
        this.id = user.getId();
        this.name = user.getName();
        this.gender = user.getGender();
        this.nick = user.getNick();
        this.phone = user.getPhone();
        this.isVerified = user.getIsVerified();
        this.contacts = user.getContacts();
        this.contactsPhone = user.getContactsPhone();
    }

    public UserShow(int id, String name, Integer gender, String nick, String phone, int isVerified, String contacts, String contactsPhone) {
        this.id = id;
        this.name = name;
        this.gender = gender;
        this.nick = nick;
        this.phone = phone;
        this.isVerified = isVerified;
        this.contacts = contacts;
        this.contactsPhone = contactsPhone;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getGender() {
        return gender;
    }

    public void setGender(Integer gender) {
        this.gender = gender;
    }

    public String getNick() {
        return nick;
    }

    public void setNick(String nick) {
        this.nick = nick;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public int getIsVerified() {
        return isVerified;
    }

    public void setIsVerified(int isVerified) {
        this.isVerified = isVerified;
    }

    public String getContacts() {
        return contacts;
    }

    public void setContacts(String contacts) {
        this.contacts = contacts;
    }

    public String getContactsPhone() {
        return contactsPhone;
    }

    public void setContactsPhone(String contactsPhone) {
        this.contactsPhone = contactsPhone;
    }
}
