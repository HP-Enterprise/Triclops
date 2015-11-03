package com.hp.triclops.user;

/**
 * 登录信息类
 * Created by Teemol on 2015/11/3.
 */

import java.io.Serializable;

/**
 * <table summary="LoginInfo" class="typeSummary">
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
 *             <td>nick</td>
 *             <td>String</td>
 *             <td>用户昵称</td>
 *         </tr>
 *         <tr>
 *             <td>loginDate</td>
 *             <td>String</td>
 *             <td>登录时间</td>
 *         </tr>
 *     </tbody>
 * </table>
 */

public class PwdLoginInfo implements Serializable {

    int id;              // 用户ID
    String name;         // 用户名
    String nick;         // 昵称
    String loginDate;    // 登录时间

    public PwdLoginInfo() {}

    public PwdLoginInfo(int id, String name, String nick, String loginDate) {
        this.id = id;
        this.name = name;
        this.nick = nick;
        this.loginDate = loginDate;
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

    public String getNick() {
        return nick;
    }

    public void setNick(String nick) {
        this.nick = nick;
    }

    public String getLoginDate() {
        return loginDate;
    }

    public void setLoginDate(String loginDate) {
        this.loginDate = loginDate;
    }

    @Override
    public String toString() {
        return "LoginInfo{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", nick='" + nick + '\'' +
                ", loginDate=" + loginDate +
                '}';
    }
}
