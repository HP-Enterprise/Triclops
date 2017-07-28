package com.hp.triclops.entity;

import javax.persistence.*;

/**
 * Created by sunjun on 2017/7/26.
 */
@Entity
@Table(name = "t_setting")
public class Setting {

    private int id;
    private String type;
    private String code;
    private String name;
    private String value;

    public Setting(){}

    public Setting(int id, String type, String code, String name, String value) {
        this.id = id;
        this.type = type;
        this.code = code;
        this.name = name;
        this.value = value;
    }

    @Column(name = "value", nullable = false, insertable = true, updatable = true)
    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
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

    @Column(name = "type", nullable = false, insertable = true, updatable = true)
    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    @Column(name = "code", nullable = false, insertable = true, updatable = true)
    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    @Column(name = "name", nullable = false, insertable = true, updatable = true)
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
