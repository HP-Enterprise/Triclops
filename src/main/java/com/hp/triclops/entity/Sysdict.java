package com.hp.triclops.entity;

import com.hp.triclops.entity.Organization;

import javax.persistence.*;
import java.util.Set;

/**
 * Created by Administrator on 2015/9/10.
 */
@Entity
@Table(name = "t_sysdict")
public class Sysdict {
    private int dictid;
    private int type;
    private String dictname;
    private String remark;
    private Set<Organization> organizationSet;

    public Sysdict() {
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "dictid", nullable = false, insertable = true, updatable = true)
    public int getDictid() {
        return dictid;
    }

    public void setDictid(int dictid) {
        this.dictid = dictid;
    }

    @Column(name = "type", nullable = false, insertable = true, updatable = true)
    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    @Column(name = "dictname", nullable = false, insertable = true, updatable = true,length = 500)
    public String getDictname() {
        return dictname;
    }

    public void setDictname(String dictname) {
        this.dictname = dictname;
    }

    @Column(name = "remark", nullable = false, insertable = true, updatable = true,length = 500)
    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    @OneToMany(mappedBy = "typeKey", cascade = CascadeType.REMOVE, fetch = FetchType.LAZY)
    public Set<Organization> getOrganizationSet() {
        return organizationSet;
    }

    public void setOrganizationSet(Set<Organization> organizationSet) {
        this.organizationSet = organizationSet;
    }
}
