package com.hp.triclops.vo;

import com.hp.triclops.entity.OrganizationUserRelative;

/**
 * Created by Teemol on 2016/1/22.
 */
public class OrganizationUserRelativeShow {

    private int id;
    private int oid;
    private int uid;

    public OrganizationUserRelativeShow(OrganizationUserRelative organizationUserRelative) {
        this.id = organizationUserRelative.getId();
        this.oid = organizationUserRelative.getOid();
        this.uid = organizationUserRelative.getUid();
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getOid() {
        return oid;
    }

    public void setOid(int oid) {
        this.oid = oid;
    }

    public int getUid() {
        return uid;
    }

    public void setUid(int uid) {
        this.uid = uid;
    }
}
