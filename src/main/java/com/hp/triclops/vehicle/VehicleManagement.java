package com.hp.triclops.vehicle;

import com.hp.triclops.entity.Vehicle;
import com.hp.triclops.repository.VehicleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Created by Teemol on 2015/11/11.
 */
@Component
public class VehicleManagement {

    @Autowired
    VehicleRepository vehicleRepository;

    /**
     * 根据id查询车辆信息
     * @param vid 车辆ID
     * @return 车辆信息
     */
    public Vehicle findById(int vid)
    {
        return vehicleRepository.findById(vid);
    }
}
