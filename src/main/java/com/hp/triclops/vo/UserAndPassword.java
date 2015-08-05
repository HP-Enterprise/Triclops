package com.hp.triclops.vo;


/**
 * Created by wh on 2015/8/5.
 */
public class UserAndPassword {
    private int id;
    private String name;
    private String nick;
    private Integer gender;
    private String phone;
    private String password;


    public UserAndPassword() {
    }

    public UserAndPassword(int id, String name, String nick, int gender, String phone, String password) {
        this.id = id;
        this.name = name;
        this.nick = nick;
        this.gender = gender;
        this.phone = phone;
        this.password = password;
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

    public int getGender() {
        return gender;
    }

    public void setGender(int gender) {
        this.gender = gender;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        UserAndPassword that = (UserAndPassword) o;

        if (id != that.id) return false;
        if (gender != that.gender) return false;
        if (name != null ? !name.equals(that.name) : that.name != null) return false;
        if (nick != null ? !nick.equals(that.nick) : that.nick != null) return false;
        if (phone != null ? !phone.equals(that.phone) : that.phone != null) return false;
        return !(password != null ? !password.equals(that.password) : that.password != null);

    }

    @Override
    public int hashCode() {
        int result = id;
        result = 31 * result + (name != null ? name.hashCode() : 0);
        result = 31 * result + (nick != null ? nick.hashCode() : 0);
        result = 31 * result + gender;
        result = 31 * result + (phone != null ? phone.hashCode() : 0);
        result = 31 * result + (password != null ? password.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "UserAndPassword{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", nick='" + nick + '\'' +
                ", gender=" + gender +
                ", phone='" + phone + '\'' +
                ", password='" + password + '\'' +
                '}';
    }
}
