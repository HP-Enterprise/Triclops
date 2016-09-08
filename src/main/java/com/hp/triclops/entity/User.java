package com.hp.triclops.entity;

import javax.persistence.*;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

/**
 * 用户类
 */
@Entity
@Table(name = "t_user")
public class User implements Serializable {
    private int id;
    private String name;
    private Integer gender;
    private String nick;
    private String phone;
    private int isVerified;
    private String contacts;
    private String contactsPhone;
    private Set<UserVehicleRelatived> userSet;
    private Set<UserVehicleRelatived> parentUserSet;
    private Set<Organization> organizationSet;
    private Set<Vehicle> vehicleSet;
    private String icon;
    private String lastDeviceId;

    public User() {
        this.userSet = new HashSet<UserVehicleRelatived>();
        this.parentUserSet = new HashSet<UserVehicleRelatived>();
        this.organizationSet = new HashSet<Organization>();
        this.vehicleSet=new HashSet<Vehicle>();
    }

    public User(String name, Integer gender, String nick, String phone,int isVerified,String contacts,String contactsPhone) {
        this.name = name;
        this.gender = gender;
        this.nick = nick;
        this.phone = phone;
        this.isVerified = isVerified;
        this.contacts = contacts;
        this.contactsPhone = contactsPhone;
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "Id", nullable = false, insertable = true, updatable = true)
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
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
    @Column(name = "gender", nullable = true, insertable = true, updatable = true)
    public Integer getGender() {
        return gender;
    }

    public void setGender(Integer gender) {
        this.gender = gender;
    }

    @Basic
    @Column(name = "nick", nullable = false, insertable = true, updatable = true, length = 50)
    public String getNick() {
        return nick;
    }

    public void setNick(String nick) {
        this.nick = nick;
    }

    @Basic
    @Column(name = "phone", nullable = true, insertable = true, updatable = true, length = 11)
    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    @Basic
    @Column(name = "is_verified", nullable = true, insertable = true, updatable = true, length = 5)
    public int getIsVerified() {
        return isVerified;
    }

    public void setIsVerified(int isVerified) {
        this.isVerified = isVerified;
    }

    @Basic
    @Column(name = "contacts", nullable = true, insertable = true, updatable = true, length = 50)
    public String getContacts() {
        return contacts;
    }

    public void setContacts(String contacts) {
        this.contacts = contacts;
    }

    @Basic
    @Column(name = "contacts_phone", nullable = true, insertable = true, updatable = true, length = 11)
    public String getContactsPhone() {
        return contactsPhone;
    }

    public void setContactsPhone(String contactsPhone) {
        this.contactsPhone = contactsPhone;
    }

    @OneToMany(mappedBy = "uid", cascade = CascadeType.REMOVE, fetch = FetchType.LAZY)
    public Set<UserVehicleRelatived> getUserSet() {
        return userSet;
    }

    public void setUserSet(Set<UserVehicleRelatived> userSet) {
        this.userSet = userSet;
    }

    @OneToMany(mappedBy = "parentuser", cascade = CascadeType.REMOVE, fetch = FetchType.LAZY)
    public Set<UserVehicleRelatived> getParentUserSet() {
        return parentUserSet;
    }

    public void setParentUserSet(Set<UserVehicleRelatived> parentUserSet) {
        this.parentUserSet = parentUserSet;
    }
    @ManyToMany(cascade = CascadeType.PERSIST, fetch = FetchType.LAZY)
    @JoinTable(name = "t_organization_user",
            joinColumns ={@JoinColumn(name = "uid", referencedColumnName = "id") },
            inverseJoinColumns = { @JoinColumn(name = "oid", referencedColumnName = "id")
            })
    public Set<Organization> getOrganizationSet() {
        return organizationSet;
    }

    public void setOrganizationSet(Set<Organization> organizationSet) {
        this.organizationSet = organizationSet;
    }

    @ManyToMany(cascade = CascadeType.PERSIST, fetch = FetchType.LAZY)
    @JoinTable(name = "t_user_vehicle_relatived",
            joinColumns ={@JoinColumn(name = "userid", referencedColumnName = "id") },
            inverseJoinColumns = { @JoinColumn(name = "vid", referencedColumnName = "id")
            })
    public Set<Vehicle> getVehicleSet() {
        return vehicleSet;
    }

    public void setVehicleSet(Set<Vehicle> vehicleSet) {
        this.vehicleSet = vehicleSet;
    }

    @Basic
    @Column(name = "icon", nullable = true, insertable = true, updatable = true, length = 50)
    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    @Basic
    @Column(name = "last_device_id", nullable = true, insertable = true, updatable = true, length = 50)
    public String getLastDeviceId() {
        return lastDeviceId;
    }

    public void setLastDeviceId(String lastDeviceId) {
        this.lastDeviceId = lastDeviceId;
    }
}
