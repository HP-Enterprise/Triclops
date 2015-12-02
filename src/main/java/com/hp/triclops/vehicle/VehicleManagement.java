package com.hp.triclops.vehicle;

import com.hp.triclops.entity.User;
import com.hp.triclops.entity.UserVehicleRelatived;
import com.hp.triclops.entity.Vehicle;
import com.hp.triclops.repository.UserRepository;
import com.hp.triclops.repository.UserVehicleRelativedRepository;
import com.hp.triclops.repository.VehicleRepository;
import com.hp.triclops.vo.UserVehicleRelativedShow;
import com.hp.triclops.vo.VehicleShow;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Teemol on 2015/11/11.
 */
@Component
public class VehicleManagement {

    @Autowired
    UserVehicleRelativedRepository userVehicleRelativedRepository;
    @Autowired
    VehicleRepository vehicleRepository;

    /**
     * 根据id查询车辆信息
     * @param vid 车辆ID
     * @return 车辆信息
     */
    public Vehicle findById(int vid){
        return vehicleRepository.findById(vid);
    }


    /**
     * 查询用户的默认车辆
     * @param uid 用户ID
     * @return 车辆信息
     */
    public VehicleShow getDefaultCar(int uid){
        User user = new User();
        user.setId(uid);
        VehicleShow vehicleShow = null;
        List<UserVehicleRelatived> userVehicleRelativedList = userVehicleRelativedRepository.findByUid(user);
        for (UserVehicleRelatived uv : userVehicleRelativedList) {
            if (uv.getVflag() == 1) {
                vehicleShow = new VehicleShow(uv.getVid(),uv.getVflag());
                break;
            }
        }
        return vehicleShow;
    }
    
}
