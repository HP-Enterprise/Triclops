package com.hp.triclops.manager;

import com.hp.triclops.entity.Vehicle;
import com.hp.triclops.repository.VehicleRepository;
import org.springframework.context.ApplicationContext;

/**
 * Created by liz on 2015/10/13.
 */

public class Vehicle6S{


    private VehicleRepository vehicleRepository;

    private Vehicle vehicle;


    private int vid;

    private ApplicationContext appContext;

    public Vehicle6S() {
    }

    /**
     * 构造有参函数
     * @param vid 车辆ID
     */

    public Vehicle6S(int vid){
         this.setVid(vid);
    }

    /**
     * 根据车辆ID查找车辆信息
     * @param vid 车辆ID
     * @return 执行完后的Vehicle
     */
    public Vehicle findVehicleById(int vid){
        return this.vehicleRepository.findById(vid);
    }


    /**
     * 手动注入Repository
     * @param appContext ApplicationContext
     */
    public void setAppCtxAndInit(ApplicationContext appContext){
        this.appContext = appContext;
        this.vehicleRepository = this.appContext.getBean(VehicleRepository.class);
        this.setVehicle(this.findVehicleById(this.getVid()));
    }



    public Vehicle getVehicle() {
        return vehicle;
    }

    public void setVehicle(Vehicle vehicle) {
        this.vehicle = vehicle;
    }

    public int getVid() {
        return vid;
    }

    public void setVid(int vid) {
        this.vid = vid;
    }


}
