package com.hp.triclops.management;

import com.hp.triclops.entity.Vehicle;
import com.hp.triclops.repository.VehicleRepository;
import com.hp.triclops.vo.VehicleShow;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
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

    @Autowired
    OrganizationVehicleManagement organizationVehicleManagement;

    @Autowired
    UserVehicleManagement userVehicleManagement;

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

    /**
     * 查询用户有全选查看的车辆ID集合
     * @param uid 用户ID
     * @return 车辆ID集合
     */
    public List<Integer> selectVehicle(int uid)
    {
        List<Integer> orgVids = new ArrayList<>();

        List<Integer> oids = organizationUserManagement.findOidByUid(uid);
        if(oids.size()>0)
        {
            orgVids = organizationVehicleManagement.findVidByOids(oids);
        }

        List<Integer> ownerVids = userVehicleManagement.findVidByUid(uid);

        for(Integer vid:ownerVids) {
            if(!orgVids.contains(vid)){
                orgVids.add(vid);
            }
        }

        return orgVids;
    }


}
