package com.hp.triclops.entity;

import javax.persistence.*;
import java.io.File;
import java.io.Serializable;

/**
 * Created by luj on 2015/9/2.
 */
@Entity
@Table(name = "t_slot")
public class SlotInfo  implements Serializable {
    private Long id;
    private int uid;
    private String slotkey;
    private byte[] slot;



    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false, insertable = true, updatable = true)
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }



    @Basic
    @Column(name = "uid", nullable = false, insertable = true, updatable = true)
    public int getUid() {        return uid;    }

    public void setUid(int uid) {        this.uid = uid;    }

    @Basic
    @Column(name = "slotkey", nullable = false, insertable = true, updatable = true, length = 50)
    public String getSlotkey() {
        return slotkey;
    }

    public void setSlotkey(String slotkey) {
        this.slotkey = slotkey;
    }

    @Basic
    @Lob
    @Column(name = "slot", nullable = false, insertable = true, updatable = true)
    public byte[] getSlot() {
        return slot;
    }

    public void setSlot(byte[] slot) {
        this.slot = slot;
    }
}
