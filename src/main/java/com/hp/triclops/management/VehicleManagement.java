package com.hp.triclops.management;

import com.hp.triclops.entity.Vehicle;
import com.hp.triclops.repository.VehicleRepository;
import com.hp.triclops.vo.OrganizationUserRelativeShow;
import com.hp.triclops.vo.VehicleShow;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Created by Teemol on 2016/1/22.
 */

@Component
public class VehicleManagement {

    @Autowired
    VehicleRepository vehicleRepository;

    @Autowired
    OrganizationUserManagement organizationUserManagement;

    /**
     * 根据id查询车辆信息
     * @param vid 车辆ID
     * @return 车辆信息
     */
    public VehicleShow findById(int vid)
    {
        Vehicle vehicle = vehicleRepository.findById(vid);
        return new VehicleShow(vehicle);
    }

    public List<OrganizationUserRelativeShow> selectVehicle(int uid)
    {
        List<OrganizationUserRelativeShow> list = organizationUserManagement.getByUid(uid);
        return list;
    }
}
