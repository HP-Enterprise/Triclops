package com.hp.triclops.entity;

import javax.persistence.*;

/**
 * 车辆和TBox关系表
 * Created by liz on 2015/10/27.
 */
@Entity
@Table(name = "t_vehicle_tbox")
public class VehicleTBox {
    private int Id;
    private TBox tid;
    private Vehicle vid;

    public VehicleTBox() {
    }

    public VehicleTBox( TBox tid, Vehicle vid) {
        this.tid = tid;
        this.vid = vid;
    }

    @javax.persistence.Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "Id", nullable = false, insertable = true, updatable = true)
    public int getId() {
        return Id;
    }

    public void setId(int id) {
        Id = id;
    }

    @OneToOne(cascade = CascadeType.PERSIST, optional = false)
    @JoinColumn(name = "tid",referencedColumnName="id")
    public TBox getTid() {
        return tid;
    }

    public void setTid(TBox tid) {
        this.tid = tid;
    }

    @ManyToOne(cascade = CascadeType.PERSIST, optional = false)
    @JoinColumn(name = "vid",referencedColumnName="id")
    public Vehicle getVid() {
        return vid;
    }

    public void setVid(Vehicle vid) {
        this.vid = vid;
    }
}
