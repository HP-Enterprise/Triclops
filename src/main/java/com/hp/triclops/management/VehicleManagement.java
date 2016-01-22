package com.hp.triclops.management;

import com.hp.triclops.entity.Vehicle;
import com.hp.triclops.repository.VehicleExRepository;
import com.hp.triclops.repository.VehicleRepository;
import com.hp.triclops.vo.OrganisationVehicleRelativeExShow;
import com.hp.triclops.vo.OrganizationUserRelativeShow;
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
    VehicleExRepository vehicleExRepository;

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

    public List<Integer> selectVehicle(int uid)
    {
        List<Integer> vids = new ArrayList<>();

        List<Integer> oids = organizationUserManagement.findOidByUid(uid);
        if(oids.size()>0)
        {
            vids = organizationVehicleManagement.findVidByOids(oids);
        }
//
//
//
//
//        //List<VehicleEx> list = vehicleExRepository.findByIdIn(null,vids);
//
//        //return list.stream().map(VehicleExShow::new).collect(Collectors.toList());
//
//        List<Integer> oids = new ArrayList<>();
//        oids.add(1);
//        oids.add(2);
//        List<Integer> list = organizationVehicleManagement.findVidByOids(oids);
//
//        return list;
        return null;
    }
}
