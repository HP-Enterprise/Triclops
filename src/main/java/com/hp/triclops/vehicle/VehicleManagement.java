package com.hp.triclops.vehicle;

import com.hp.triclops.entity.User;
import com.hp.triclops.entity.UserVehicleRelatived;
import com.hp.triclops.entity.Vehicle;
import com.hp.triclops.repository.UserRepository;
import com.hp.triclops.repository.UserVehicleRelativedRepository;
import com.hp.triclops.repository.VehicleRepository;
import com.hp.triclops.vo.UserVehicleRelativedShow;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

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
    @Autowired
    UserRepository userRepository;

    /**
     * 修改用户与车辆的绑定信息
     * @param userVehicleRelativedShow 用户与车辆绑定信息
     * @return true：修改成功   fasle:修改失败
     */
    public boolean updateUserVehicleRelatived(UserVehicleRelativedShow userVehicleRelativedShow){
        UserVehicleRelatived userVehicleRelatived = userVehicleRelativedRepository.findById(userVehicleRelativedShow.getId());
        if (userVehicleRelatived != null) {
            User user = userRepository.findById(userVehicleRelativedShow.getUid());
            Vehicle vehicle = vehicleRepository.findById(userVehicleRelativedShow.getVid());
            userVehicleRelativedRepository.update(user, vehicle, userVehicleRelativedShow.getVflag(), userVehicleRelativedShow.getIflag(), userVehicleRelativedShow.getId());
            return true;
        }
        return false;
    }

    /**
     * 查询用户的默认车辆
     * @param uid 用户ID
     * @return 车辆信息
     */
    public Vehicle getDefaultCar(int uid){
        User user = new User();
        user.setId(uid);
        Vehicle vehicle = null;
        List<UserVehicleRelatived> userVehicleRelativedList = userVehicleRelativedRepository.findByUid(user);
        for (UserVehicleRelatived uv : userVehicleRelativedList) {
            if (uv.getVflag() == 1) {
                vehicle = uv.getVid();
                break;
            }
        }
        return vehicle;
    }


}
