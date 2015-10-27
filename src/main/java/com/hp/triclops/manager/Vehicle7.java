package com.hp.triclops.manager;

import com.hp.triclops.entity.Vehicle;
import com.hp.triclops.entity.VehicleTBox;

import java.util.Set;

/**
 * Created by liz on 2015/10/26.
 */
public class Vehicle7 extends Vehicle6S {

    public Vehicle7() {
    }

    public Vehicle7(int vid) {
        super(vid);
    }

    /**
     * 绑定TBox
     * @param tbox TBoxMgr
     */
    public void bindTbox(TBoxMgr tbox){
        if(!this.isBinding(tbox)){
            VehicleTBox vt = new VehicleTBox(tbox.getTbox(), this.getVehicle());
            Vehicle v = this.getVehicle();
            Set<VehicleTBox> tboxSet = v.getTboxSet();
            tboxSet.add(vt);
            this.vehicleRepository.save(v);
        }
    }

    /**
     * 解绑TBox
     * @param tbox TBox
     */
    public void unbindTbox(TBoxMgr tbox){
        if(this.isBinding(tbox)){
            VehicleTBox vt = new VehicleTBox(tbox.getTbox(), this.getVehicle());
            Vehicle v = this.getVehicle();
            Set<VehicleTBox> tboxSet = v.getTboxSet();
            tboxSet.remove(vt);
            this.vehicleRepository.save(v);
        }
    }

    /**
     * 判断是否已经绑定
     * @param tbox TBoxMgr
     * @return
     */
    public Boolean isBinding(TBoxMgr tbox){
        VehicleTBox vt = new VehicleTBox(tbox.getTbox(), this.getVehicle());
        if(this.getVehicle().getTboxSet().contains(vt)){
            return true;
        }
        return false;
    }
}
