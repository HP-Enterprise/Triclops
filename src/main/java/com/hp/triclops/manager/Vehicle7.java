package com.hp.triclops.manager;

import com.hp.triclops.repository.TBoxRepository;

/**
 * Created by liz on 2015/10/26.
 */
public class Vehicle7 extends Vehicle6S {


    private TBoxRepository tBoxRepository;

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
            this.getVehicle().addTboxVehicle(tbox.getTbox());
            this.vehicleRepository.save(this.getVehicle());
        }
    }


    /**
     * 判断是否已经绑定
     * @param tbox TBoxMgr
     * @return Boolean true-已绑定 false-未绑定
     */
    public Boolean isBinding(TBoxMgr tbox){
        return this.getVehicle().getTboxSet().contains(tbox.getTbox());
    }
}
