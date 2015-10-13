package com.hp.triclops.entity;

import com.hp.triclops.repository.VehicleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Created by liz on 2015/10/13.
 */
@Component
public class Vehicle6S{

    @Autowired
    private VehicleRepository vehicleRepository;

    private Vehicle vehicle;

    public Vehicle6S() {
    }

    /**
     * 构造有参函数
     * @param vid
     */

    public Vehicle6S(int vid){
         this.setVehicle(this.findVehicleById(vid));
    }

    /**
     * 根据车辆ID查找车辆信息
     * @param vid
     * @return
     */
    public Vehicle findVehicleById(int vid){
        return this.vehicleRepository.findById(vid);
    }


    public Vehicle getVehicle() {
        return vehicle;
    }

    public void setVehicle(Vehicle vehicle) {
        this.vehicle = vehicle;
    }

}
