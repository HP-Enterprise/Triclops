package com.hp.triclops.entity;

import javax.persistence.*;
import java.sql.Date;


@Entity
@Table(name = "t_user", schema = "", catalog = "testssh")
public class User {
    private long id;
    private String name;
    private Integer age;
    private Integer sex;
    private Date birthday;

    protected User() {}

    public User(String name, Integer age, Integer sex, Date birthday) {
        this.name = name;
        this.age = age;
        this.sex = sex;
        this.birthday = birthday;
    }

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id", nullable = false, insertable = true, updatable = true)
    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    @Basic
    @Column(name = "name", nullable = true, insertable = true, updatable = true, length = 45)
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Basic
    @Column(name = "age", nullable = true, insertable = true, updatable = true)
    public Integer getAge() {
        return age;
    }

    public void setAge(Integer age) {
        this.age = age;
    }

    @Basic
    @Column(name = "sex", nullable = true, insertable = true, updatable = true)
    public Integer getSex() {
        return sex;
    }

    public void setSex(Integer sex) {
        this.sex = sex;
    }

    @Basic
    @Column(name = "birthday", nullable = true, insertable = true, updatable = true)
    public Date getBirthday() {
        return birthday;
    }

    public void setBirthday(Date birthday) {
        this.birthday = birthday;
    }


    @Override
    public String toString() {
        return String.format(
                "User[id=%d, name='%s', birthday='%s']",
                id, name, birthday);
    }
}
