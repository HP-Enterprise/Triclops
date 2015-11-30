package com.hp.triclops.repository;

import com.hp.triclops.entity.User;
import com.hp.triclops.entity.UserVehicleRelatived;
import com.hp.triclops.entity.Vehicle;
import com.hp.triclops.vo.UserVehicleRelativedShow;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;


/**
 * Created by Teemol on 2015/9/5.
 */

@Component
public class UserVehicleRelativedRepositoryDAO<T>  {

    @Autowired
    UserRepository userRepository;
    @Autowired
    UserVehicleRelativedRepository userVehicleRelativedRepository;
    @Autowired
    VehicleRepository vehicleRepository;

    /**
     * 查询车主车辆关系集
     * @param uid 用户id
     * @param vid 车辆vid
     * @param iflag '车主标识 1 车主 0 朋友'
     * @return 车主车辆关系集
     */
    public UserVehicleRelativedShow getRelative(int uid, int vid,Integer iflag)
    {

        Vehicle vehilce = vehicleRepository.findById(vid);
        User user = userRepository.findById(uid);
        UserVehicleRelatived userVehicleRelatived = userVehicleRelativedRepository.findOneReative(user,vehilce,iflag,user);
        if(userVehicleRelatived == null)
            return null;
        UserVehicleRelativedShow us = new UserVehicleRelativedShow();
        us.setId(userVehicleRelatived.getId());
        us.setUid(userVehicleRelatived.getUid().getId());
        us.setVid(userVehicleRelatived.getVid().getId());
        us.setIflag(userVehicleRelatived.getIflag());
        us.setVflag(userVehicleRelatived.getVflag());
        us.setParentuser(userVehicleRelatived.getParentuser().getId());
        return us;
    }

}


