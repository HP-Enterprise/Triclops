package com.hp.triclops.vo;

import com.hp.triclops.entity.OrganisationVehicleRelativeEx;

/**
 * Created by Teemol on 2016/1/22.
 */
public class OrganisationVehicleRelativeExShow {

    private int id;
    private int oid;
    private int vid;

    public OrganisationVehicleRelativeExShow(OrganisationVehicleRelativeEx organisationVehicleRelativeEx) {
        this.id = organisationVehicleRelativeEx.getId();
        this.oid = organisationVehicleRelativeEx.getOid();
        this.vid = organisationVehicleRelativeEx.getVid();
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

    public int getVid() {
        return vid;
    }

    public void setVid(int vid) {
        this.vid = vid;
    }
}
